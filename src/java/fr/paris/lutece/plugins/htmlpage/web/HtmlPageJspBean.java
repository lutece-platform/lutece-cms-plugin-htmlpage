/*
 * Copyright (c) 2002-2022, City of Paris
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
import fr.paris.lutece.plugins.htmlpage.service.EnumStatus;
import fr.paris.lutece.plugins.htmlpage.service.IHtmlPageService;
import fr.paris.lutece.plugins.htmlpage.utils.HtmlPageUtil;
import fr.paris.lutece.portal.business.role.RoleHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.AbstractPaginator;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.http.SecurityUtil;
import fr.paris.lutece.util.url.UrlItem;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author lenaini
 */
@SessionScoped
@Named
public class HtmlPageJspBean extends AbstractManageHtmlPageJspBean <Integer, HtmlPage>
{
    // Right
    public static final String RIGHT_MANAGE_HTMLPAGE = "HTMLPAGE_MANAGEMENT";

    // properties for page titles
    private static final String PROPERTY_PAGE_TITLE_HTMLPAGE_LIST = "htmlpage.manage_htmlpage.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY = "htmlpage.modify_htmlpage.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE = "htmlpage.create_htmlpage.pageTitle";

    // Messages
    private static final String MESSAGE_CONFIRM_REMOVE_HTMLPAGE = "htmlpage.message.confirmRemoveHtmlPage";
    private static final String MESSAGE_INVALID_DATE_START = "htmlpage.message.invalidDateStart";
    private static final String MESSAGE_INVALID_DATE_END = "htmlpage.message.invalidDateEnd";
    private static final String MESSAGE_INVALID_DATE_END_BEFORE_DATE_START = "htmlpage.message.dateEndBeforeDateStart";
    
    // Markers
    private static final String MARK_LIST_HTMLPAGE_LIST = "htmlpage_list";
    private static final String MARK_WORKGROUPS_LIST = "workgroups_list";
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_PROD_URL = "prod_url";
    
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_HTML_CONTENT = "html_content";
    private static final String MARK_HTMLPAGE = "htmlpage";
    private static final String MARK_ROLES_LIST = "roles_list";
    private static final String MARK_STATUS = "status_list";

    // parameters
    private static final String PARAMETER_HTMLPAGE_ID = "id_htmlpage";
    private static final String PARAMETER_HTMLPAGE_DESCRIPTION = "description";
    private static final String PARAMETER_HTMLPAGE_STATUS = "status";
    private static final String PARAMETER_HTMLPAGE_WORKGROUP = "workgroup";
    private static final String PARAMETER_HTMLPAGE_HTML_CONTENT = "html_content";
    private static final String PARAMETER_ID_HTMLPAGE_LIST = "htmlpage_list_id";
    private static final String PARAMETER_HTMLPAGE_ROLE = "role";
    private static final String PARAMETER_HTMLPAGE_DATE_START = "date_start";
    private static final String PARAMETER_HTMLPAGE_DATE_END = "date_end";
    
    // templates
    private static final String TEMPLATE_MANAGE_HTMLPAGE = "/admin/plugins/htmlpage/manage_htmlpage.html";
    private static final String TEMPLATE_CREATE_HTMLPAGE = "/admin/plugins/htmlpage/create_htmlpage.html";
    private static final String TEMPLATE_MODIFY_HTMLPAGE = "/admin/plugins/htmlpage/modify_htmlpage.html";

    // Jsp Definition
    private static final String JSP_DO_REMOVE_HTMLPAGE = "jsp/admin/plugins/htmlpage/DoRemoveHtmlPage.jsp";
    private static final String JSP_MANAGE_HTMLPAGE = "jsp/admin/plugins/htmlpage/ManageHtmlPage.jsp";
    private static final String JSP_REDIRECT_TO_MANAGE_HTMLPAGE = "ManageHtmlPage.jsp";
    
    private List<Integer> _listIdHtmlPages;
    
    @Inject 
    private IHtmlPageService _htmlPageService;

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
        
        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX) == null || _listIdHtmlPages.isEmpty( ) )
        {
        	_listIdHtmlPages = HtmlPageHome.getIdHtmlPagesList( getPlugin( ) );
        }
        
        Map<String, Object> model = getPaginatedListModel( request, MARK_LIST_HTMLPAGE_LIST, _listIdHtmlPages, JSP_MANAGE_HTMLPAGE );

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
        model.put( MARK_PROD_URL, AppPathService.getProdUrl(request) );
        model.put( MARK_LOCALE, getLocale( ) );
        model.put( MARK_HTML_CONTENT, "" );
        model.put( MARK_ROLES_LIST, RoleHome.getRolesList( ) );
        model.put( MARK_STATUS, EnumStatus.getReferenceList( ) );

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
    	HtmlPage htmlPage = new HtmlPage( );
    	fillHtmlPageValues( request, htmlPage );
        
        // Mandatory fields
        if( existsMandatoryFieldEmpty( request, htmlPage ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }
               
        //Dates check         
        String error = checkHtmlPageDates( request, htmlPage );
        if( error != null )
        {
           return error;
        }       	
        
		_htmlPageService.create( htmlPage, getPlugin( ) );

        // if the operation occurred well, redirects towards the list of the HtmlPages
        return JSP_REDIRECT_TO_MANAGE_HTMLPAGE;
    }

    /**
     * fills Html page object with data coming from the Http request
     * 
     * @param request
     *         The http request
     * @param htmlpage
     *         Thee html page object
     */
    private void fillHtmlPageValues( HttpServletRequest request, HtmlPage htmlpage )
    {
    	htmlpage.setDescription( request.getParameter( PARAMETER_HTMLPAGE_DESCRIPTION ) );
        htmlpage.setHtmlContent( request.getParameter( PARAMETER_HTMLPAGE_HTML_CONTENT ) );
        htmlpage.setStatus( Integer.parseInt( request.getParameter( PARAMETER_HTMLPAGE_STATUS ) ) );
        htmlpage.setWorkgroup( request.getParameter( PARAMETER_HTMLPAGE_WORKGROUP ) );
        htmlpage.setRole( request.getParameter( PARAMETER_HTMLPAGE_ROLE ) );
		htmlpage.setDateStart( HtmlPageUtil.convertToTimestamp( request.getParameter( PARAMETER_HTMLPAGE_DATE_START ) ) );
		htmlpage.setDateEnd( HtmlPageUtil.convertToTimestamp( request.getParameter( PARAMETER_HTMLPAGE_DATE_END ) ) );
    }
    
    /**
     * Checks that mandatory fields are filled
     * 
     * @param request
     * @param htmlpage
     * @return
     */
    private boolean existsMandatoryFieldEmpty( HttpServletRequest request, HtmlPage htmlpage )
    {
    	return  StringUtils.isBlank( htmlpage.getDescription( ) )  || StringUtils.isBlank( htmlpage.getHtmlContent( ) ) || 
    	        ( htmlpage.getStatus( ) == EnumStatus.conditioned.getId( )  && ( StringUtils.isBlank( request.getParameter( PARAMETER_HTMLPAGE_DATE_START ) ) || 
    	        		                                                         StringUtils.isBlank( request.getParameter( PARAMETER_HTMLPAGE_DATE_END ) ) ) );
    }
    
    /**
     * Checks that start date and end date are valid and that start date is before end date for html page with conditioned state
     * 
     * @param request
     *            The Http Request
     * @param htmlpage
     *            The Html page
     * @return an error message if controls have failed, null otherwise
     */
    private String checkHtmlPageDates( HttpServletRequest request, HtmlPage htmlPage )
    {
    	if( htmlPage.getStatus( ) == EnumStatus.conditioned.getId( ) )
        {
    		if ( htmlPage.getDateStart( ) == null )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_INVALID_DATE_START, AdminMessage.TYPE_STOP );
            }
            
            if ( htmlPage.getDateEnd( ) == null  )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_INVALID_DATE_END, AdminMessage.TYPE_STOP );
            }
            
            if( htmlPage.getDateEnd( ).before( htmlPage.getDateStart( ) ) )
            {
            	return AdminMessageService.getMessageUrl( request, MESSAGE_INVALID_DATE_END_BEFORE_DATE_START, AdminMessage.TYPE_STOP );
            }
        }
    	
        return null;
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
        duplicateHtmlPage.setDateStart( htmlpage.getDateStart() ); 
        duplicateHtmlPage.setDateEnd( htmlpage.getDateEnd() );

        _htmlPageService.create( duplicateHtmlPage, getPlugin( ) );

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

        String strHtmlPageId = request.getParameter( PARAMETER_HTMLPAGE_ID );
        
        if ( !StringUtils.isNumeric( strHtmlPageId ) )
        {
            AppLogService.error( " {} is not a valid html page id.", ( ) -> SecurityUtil.logForgingProtect( strHtmlPageId ) );

            return getManageHtmlPage( request );
        }
        
        HtmlPage htmlPage = HtmlPageHome.findByPrimaryKey( Integer.parseInt( strHtmlPageId ), getPlugin( ) );
        if ( htmlPage == null )
        {
            AppLogService.error( "{} is not a valid html page id.", ( ) -> SecurityUtil.logForgingProtect( strHtmlPageId ) );

            return getManageHtmlPage( request );
        }

        Map<String, Object> model = new HashMap<String, Object>( );
        ReferenceList workgroupsList = AdminWorkgroupService.getUserWorkgroups( getUser( ), getLocale( ) );
        model.put( MARK_WORKGROUPS_LIST, workgroupsList );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_PROD_URL, AppPathService.getProdUrl(request) );
        model.put( MARK_LOCALE, getLocale( ) );
        model.put( MARK_HTMLPAGE, htmlPage );
        model.put( MARK_ROLES_LIST, RoleHome.getRolesList( ) );
        model.put( MARK_STATUS, EnumStatus.getReferenceList( ) );
        
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
    	int nId = Integer.parseInt( request.getParameter( PARAMETER_HTMLPAGE_ID ) );
    	
    	HtmlPage htmlPage = HtmlPageHome.findByPrimaryKey( nId, getPlugin( ) );
    	fillHtmlPageValues( request, htmlPage );

    	// Mandatory fields
        if( existsMandatoryFieldEmpty( request, htmlPage ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }
               
        //Dates check
        String error = checkHtmlPageDates( request, htmlPage );
        if( error != null )
        {
            return error;
        }

        _htmlPageService.update( htmlPage, getPlugin( ) );

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
        _htmlPageService.remove( htmlPage, getPlugin( ) );

        // if the operation occurred well, redirects towards the list of the HtmlPages
        return JSP_REDIRECT_TO_MANAGE_HTMLPAGE;
    }
    
    /**
     * {@inheritDoc }
     */
	@Override
	List<HtmlPage> getItemsFromIds( List<Integer> listIds ) 
	{
		List<HtmlPage> listHtmlPage = HtmlPageHome.getHtmlPagesListByIds( listIds, getPlugin( ) );
		
		// keep original order
        return listHtmlPage.stream( )
                           .sorted( Comparator.comparingInt( htmlPage -> listIds.indexOf( htmlPage.getId( ) ) ) )
                           .collect( Collectors.toList( ) );
	}
     
}
