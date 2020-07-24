/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.htmlpage.web;

import fr.paris.lutece.plugins.htmlpage.business.HtmlPage;
import fr.paris.lutece.plugins.htmlpage.business.HtmlPageHome;
import fr.paris.lutece.plugins.htmlpage.service.HtmlPageUtil;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.search.SearchEngine;
import fr.paris.lutece.portal.service.search.SearchResult;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.l10n.LocaleService;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;


/**
 * This class manages HtmlPage page.
 */
public class HtmlPageApp implements XPageApplication
{
    //////////////////////////////////////////////////////////////////////////////////////////////
    // Constants

    /** Serial id */
    private static final long serialVersionUID = 4891750061103627788L;

    // Parameters
    private static final String PARAMETER_PAGE = "page";
    private static final String PARAMETER_HTMLPAGE_ID = "htmlpage_id";
    private static final String PARAMETER_QUERY = "query";

    // Properties
    private static final String PROPERTY_PAGE_TITLE = "htmlpage.pageTitle";
    private static final String PROPERTY_PAGE_PATH = "htmlpage.pagePathLabel";

    // Messages
    private static final String PROPERTY_MESSAGE_ERROR_HTMLPAGE = "htmlpage.message.pageInvalid";
    private static final String PROPERTY_MESSAGE_NOT_AUTHORIZED = "htmlpage.message.notAuthorized";

    // Markers
    private static final String MARK_HTMLPAGE_LIST = "htmlpages_list";
    private static final String MARK_HTMLPAGE = "htmlpage";
    private static final String MARK_PAGE = "page";
    private static final String MARK_RESULT = "result";
    private static final String MARK_QUERY = "query";

    // Templates
    private static final String TEMPLATE_XPAGE_HTMLPAGE = "skin/plugins/htmlpage/page_htmlpage.html";
    private static final String TEMPLATE_XPAGE_HTMLPAGE_LISTS = "skin/plugins/htmlpage/htmlpages_list.html";
    // Bean
    private static final String BEAN_SEARCH_ENGINE = "htmlpage.htmlpageSearchEngine";

    // private fields
    private Plugin _plugin;

    /** Creates a new instance of HtmlPageApp */
    public HtmlPageApp( )
    {
    }

    /**
     * Returns the content of the page HtmlPage. It is composed by a form which
     * to capture the data to send a message to
     * a contact of the portal.
     * @return the Content of the page Contact
     * @param request The http request
     * @param nMode The current mode
     * @param plugin The plugin object
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException
     *             Message displayed if an exception occures
     */
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin ) throws SiteMessageException
    {
        XPage page = new XPage( );

        String strPluginName = request.getParameter( PARAMETER_PAGE );
        String strQuery = request.getParameter( PARAMETER_QUERY );
        _plugin = PluginService.getPlugin( strPluginName );

        page.setTitle( AppPropertiesService.getProperty( PROPERTY_PAGE_TITLE ) );
        page.setPathLabel( AppPropertiesService.getProperty( PROPERTY_PAGE_PATH ) );

        String strHtmlPageId = request.getParameter( PARAMETER_HTMLPAGE_ID );

        if ( StringUtils.isNotBlank( strQuery ) )
        {
            page.setContent( getSearch( strQuery, strPluginName, request ) );
        }
        else
        {
            if ( strHtmlPageId == null )
            {
                page.setContent( getHtmlPagesLists( request ) );
            }

            if ( strHtmlPageId != null )
            {
                page.setContent( getHtmlPage( request, strHtmlPageId ) );
            }
        }

        return page;
    }

    private String getSearch( String strQuery, String strPluginName, HttpServletRequest request )
    {
        SearchEngine engine = SpringContextService.getBean( BEAN_SEARCH_ENGINE );
        List<SearchResult> listResults = engine.getSearchResults( strQuery, request );

        HashMap<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_RESULT, listResults );
        model.put( MARK_QUERY, strQuery );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_HTMLPAGE_LISTS, getLocale( request ),
                model );
        return template.getHtml( );
    }

    private String getHtmlPagesLists( HttpServletRequest request ) throws SiteMessageException
    {
        HashMap<String, Object> model = new HashMap<String, Object>( );

        Collection<HtmlPage> htmlPageList = HtmlPageHome.findEnabledHtmlPageList( _plugin );
        Collection<HtmlPage> visibleHtmlPageList = new ArrayList<HtmlPage>( ); // filter the list of lists by role

        for ( HtmlPage htmlpage : htmlPageList )
        {
            if ( HtmlPageUtil.isVisible( request, htmlpage.getRole( ) ) )
            {
                visibleHtmlPageList.add( htmlpage );
            }
        }

        model.put( MARK_HTMLPAGE_LIST, visibleHtmlPageList );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_HTMLPAGE_LISTS, getLocale( request ),
                model );

        return template.getHtml( );
    }

    /**
     * Returns the htmlpage page
     * @param request The Html request
     * @param plugin The plugin
     * @return The Html template
     */
    private String getHtmlPage( HttpServletRequest request, String strHtmlPageId ) throws SiteMessageException
    {
        HashMap<String, Object> model = new HashMap<String, Object>( );

        int nHtmlPageId = Integer.parseInt( strHtmlPageId );
        HtmlPage htmlpage = HtmlPageHome.findByPrimaryKey( nHtmlPageId, _plugin );

        if ( htmlpage != null )
        {
            int nStatus = htmlpage.getStatus( );

            if ( ( nStatus == 0 ) && ( HtmlPageUtil.isVisible( request, htmlpage.getRole( ) ) ) )
            {
                model.put( MARK_HTMLPAGE, htmlpage );
                model.put( MARK_PAGE, _plugin.getName( ) );
            }
            else
            {
                SiteMessageService.setMessage( request, PROPERTY_MESSAGE_NOT_AUTHORIZED, SiteMessage.TYPE_ERROR );
            }
        }
        else
        {
            SiteMessageService.setMessage( request, PROPERTY_MESSAGE_ERROR_HTMLPAGE, SiteMessage.TYPE_ERROR );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_HTMLPAGE, getLocale( request ), model );

        return template.getHtml( );
    }
    
    /**
     * Default getLocale() implementation. Could be overriden
     * 
     * @param request
     *            The HTTP request
     * @return The Locale
     */
    public Locale getLocale( HttpServletRequest request )
    {
        return LocaleService.getContextUserLocale( request );
    }
}
