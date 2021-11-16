/*
 * Copyright (c) 2002-2021, City of Paris
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
import fr.paris.lutece.portal.business.role.RoleHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author lenaini
 */
public class HtmlPageJspBean extends PluginAdminPageJspBean
{
    // Right
    public static final String RIGHT_MANAGE_HTMLPAGE = "HTMLPAGE_MANAGEMENT";

    // properties for page titles
    private static final String PROPERTY_PAGE_TITLE_HTMLPAGE_LIST = "htmlpage.manage_htmlpage.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY = "htmlpage.modify_htmlpage.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE = "htmlpage.create_htmlpage.pageTitle";

    // Properties
    private static final String PROPERTY_DEFAULT_HTMLPAGE_LIST_PER_PAGE = "htmlpage.htmlPageList.itemsPerPage";

    // Messages
    private static final String MESSAGE_CONFIRM_REMOVE_HTMLPAGE = "htmlpage.message.confirmRemoveHtmlPage";

    // Markers
    private static final String MARK_LIST_HTMLPAGE_LIST = "htmlpage_list";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_WORKGROUPS_LIST = "workgroups_list";
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_HTML_CONTENT = "html_content";
    private static final String MARK_HTMLPAGE = "htmlpage";
    private static final String MARK_ROLES_LIST = "roles_list";
    private static final String MARK_PAGINATOR = "paginator";

    // parameters
    private static final String PARAMETER_HTMLPAGE_ID = "id_htmlpage";
    private static final String PARAMETER_HTMLPAGE_DESCRIPTION = "description";
    private static final String PARAMETER_HTMLPAGE_STATUS = "status";
    private static final String PARAMETER_HTMLPAGE_WORKGROUP = "workgroup";
    private static final String PARAMETER_HTMLPAGE_HTML_CONTENT = "html_content";
    private static final String PARAMETER_ID_HTMLPAGE_LIST = "htmlpage_list_id";
    private static final String PARAMETER_HTMLPAGE_ROLE = "role";
    private static final String PARAMETER_PAGE_INDEX = "page_index";

    // templates
    private static final String TEMPLATE_MANAGE_HTMLPAGE = "/admin/plugins/htmlpage/manage_htmlpage.html";
    private static final String TEMPLATE_CREATE_HTMLPAGE = "/admin/plugins/htmlpage/create_htmlpage.html";
    private static final String TEMPLATE_MODIFY_HTMLPAGE = "/admin/plugins/htmlpage/modify_htmlpage.html";

    // Jsp Definition
    private static final String JSP_DO_REMOVE_HTMLPAGE = "jsp/admin/plugins/htmlpage/DoRemoveHtmlPage.jsp";
    private static final String JSP_REDIRECT_TO_MANAGE_HTMLPAGE = "ManageHtmlPage.jsp";

    // Variables
    private int _nDefaultItemsPerPage;
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;

    /**
     * returns the template of the HtmlPageLists management
     * 
     * @param request
     *            The HttpRequest
     * @return template of lists management
     */
    public String getManageHtmlPage( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_HTMLPAGE_LIST );

        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_HTMLPAGE_LIST_PER_PAGE, 50 );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage, _nDefaultItemsPerPage );

        Collection<HtmlPage> listHtmlPageList = HtmlPageHome.findAll( getPlugin( ) );
        listHtmlPageList = AdminWorkgroupService.getAuthorizedCollection( listHtmlPageList, getUser( ) );

        Paginator paginator = new Paginator( (List<HtmlPage>) listHtmlPageList, _nItemsPerPage, getHomeUrl( request ), PARAMETER_PAGE_INDEX,
                _strCurrentPageIndex );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_LIST_HTMLPAGE_LIST, paginator.getPageItems( ) );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_HTMLPAGE, getLocale( ), model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Returns the form to create a htmlpage
     *
     * @param request
     *            The Http request
     * @return the html code of the htmlpage form
     */
    public String getCreateHtmlPage( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_CREATE );

        Map<String, Object> model = new HashMap<String, Object>( );
        ReferenceList workgroupsList = AdminWorkgroupService.getUserWorkgroups( getUser( ), getLocale( ) );
        model.put( MARK_WORKGROUPS_LIST, workgroupsList );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, getLocale( ) );
        model.put( MARK_HTML_CONTENT, "" );
        model.put( MARK_ROLES_LIST, RoleHome.getRolesList( ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_HTMLPAGE, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Process the data capture form of a new htmlpage
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    public String doCreateHtmlPage( HttpServletRequest request )
    {
        String strDescription = request.getParameter( PARAMETER_HTMLPAGE_DESCRIPTION );
        String strHtmlContent = request.getParameter( PARAMETER_HTMLPAGE_HTML_CONTENT );
        String strStatus = request.getParameter( PARAMETER_HTMLPAGE_STATUS );
        String strWorkgroup = request.getParameter( PARAMETER_HTMLPAGE_WORKGROUP );
        String strRole = request.getParameter( PARAMETER_HTMLPAGE_ROLE );

        // Mandatory fields
        if ( ( strDescription == null ) || strDescription.trim( ).equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        HtmlPage htmlpage = new HtmlPage( );

        htmlpage.setDescription( strDescription );
        htmlpage.setHtmlContent( strHtmlContent );
        htmlpage.setStatus( Integer.parseInt( strStatus ) );
        htmlpage.setWorkgroup( strWorkgroup );
        htmlpage.setRole( strRole );

        HtmlPageHome.create( htmlpage, getPlugin( ) );

        // if the operation occurred well, redirects towards the list of the HtmlPages
        return JSP_REDIRECT_TO_MANAGE_HTMLPAGE;
    }

    /**
     * Process the data capture form of a new htmlpage from copy of other
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    public String doDuplicateHtmlPage( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_HTMLPAGE_ID ) );

        HtmlPage htmlpage = HtmlPageHome.findByPrimaryKey( nId, getPlugin( ) );

        HtmlPage duplicateHtmlPage = new HtmlPage( );
        duplicateHtmlPage.setDescription( htmlpage.getDescription( ) );
        duplicateHtmlPage.setHtmlContent( htmlpage.getHtmlContent( ) );
        duplicateHtmlPage.setStatus( htmlpage.getStatus( ) );
        duplicateHtmlPage.setWorkgroup( htmlpage.getWorkgroup( ) );
        duplicateHtmlPage.setRole( htmlpage.getRole( ) );

        HtmlPageHome.create( duplicateHtmlPage, getPlugin( ) );

        // if the operation occurred well, redirects towards the list of the HtmlPages
        return JSP_REDIRECT_TO_MANAGE_HTMLPAGE;
    }

    /**
     * Returns the form to update info about a htmlPage
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    public String getModifyHtmlPage( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MODIFY );

        int nId = Integer.parseInt( request.getParameter( PARAMETER_HTMLPAGE_ID ) );
        HtmlPage htmlPage = HtmlPageHome.findByPrimaryKey( nId, getPlugin( ) );

        Map<String, Object> model = new HashMap<String, Object>( );
        ReferenceList workgroupsList = AdminWorkgroupService.getUserWorkgroups( getUser( ), getLocale( ) );
        model.put( MARK_WORKGROUPS_LIST, workgroupsList );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, getLocale( ) );
        model.put( MARK_HTMLPAGE, htmlPage );
        model.put( MARK_ROLES_LIST, RoleHome.getRolesList( ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_HTMLPAGE, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Process the change form of a htmlPage
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     */
    public String doModifyHtmlPage( HttpServletRequest request )
    {
        // Mandatory fields
        if ( request.getParameter( PARAMETER_HTMLPAGE_DESCRIPTION ).equals( "" ) || request.getParameter( PARAMETER_HTMLPAGE_HTML_CONTENT ).equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nId = Integer.parseInt( request.getParameter( PARAMETER_HTMLPAGE_ID ) );

        HtmlPage htmlPage = HtmlPageHome.findByPrimaryKey( nId, getPlugin( ) );
        htmlPage.setDescription( request.getParameter( PARAMETER_HTMLPAGE_DESCRIPTION ) );
        htmlPage.setHtmlContent( request.getParameter( PARAMETER_HTMLPAGE_HTML_CONTENT ) );
        htmlPage.setStatus( Integer.parseInt( request.getParameter( PARAMETER_HTMLPAGE_STATUS ) ) );
        htmlPage.setWorkgroup( request.getParameter( PARAMETER_HTMLPAGE_WORKGROUP ) );
        htmlPage.setRole( request.getParameter( PARAMETER_HTMLPAGE_ROLE ) );

        HtmlPageHome.update( htmlPage, getPlugin( ) );

        // if the operation occurred well, redirects towards the list of the HtmlPages
        return JSP_REDIRECT_TO_MANAGE_HTMLPAGE;
    }

    /**
     * Manages the removal form of a htmlPage whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    public String getConfirmRemoveHtmlPage( HttpServletRequest request )
    {
        int nIdHtmlPage = Integer.parseInt( request.getParameter( PARAMETER_HTMLPAGE_ID ) );

        UrlItem url = new UrlItem( JSP_DO_REMOVE_HTMLPAGE );
        url.addParameter( PARAMETER_HTMLPAGE_ID, nIdHtmlPage );
        url.addParameter( PARAMETER_ID_HTMLPAGE_LIST, request.getParameter( PARAMETER_ID_HTMLPAGE_LIST ) );

        Object [ ] args = {
                request.getParameter( PARAMETER_HTMLPAGE_DESCRIPTION )
        };

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_HTMLPAGE, args, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Treats the removal form of a htmlPage
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage htmlPages
     */
    public String doRemoveHtmlPage( HttpServletRequest request )
    {
        int nIdHtmlPage = Integer.parseInt( request.getParameter( PARAMETER_HTMLPAGE_ID ) );

        HtmlPage htmlPage = HtmlPageHome.findByPrimaryKey( nIdHtmlPage, getPlugin( ) );
        HtmlPageHome.remove( htmlPage, getPlugin( ) );

        // if the operation occurred well, redirects towards the list of the HtmlPages
        return JSP_REDIRECT_TO_MANAGE_HTMLPAGE;
    }
}
