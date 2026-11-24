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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.htmlpage.business.HtmlPage;
import fr.paris.lutece.plugins.htmlpage.service.EnumStatus;
import fr.paris.lutece.plugins.htmlpage.service.HtmlPageService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import jakarta.inject.Inject;

/**
 * HtmlPageJspBean Test: the screen opened by the button of an error message
 */
public class HtmlPageJspBeanTest extends LuteceTestCase
{
    @Inject
    private HtmlPageJspBean _bean;
    @Inject
    private HtmlPageService _htmlPageService;
    private HtmlPage _htmlPage;

    @AfterEach
    protected void removeHtmlPage( )
    {
        if ( _htmlPage != null )
        {
            _htmlPageService.remove( _htmlPage, HtmlPageService.getPlugin( ) );
        }
    }

    @Test
    public void testDoCreateHtmlPageWithoutDescriptionReturnsToTheCreationScreen( ) throws AccessDeniedException
    {
        MockHttpServletRequest request = getUserRequest( );
        _bean.init( request, HtmlPageJspBean.RIGHT_MANAGE_HTMLPAGE );
        request.setParameter( "html_content", "<p>content</p>" );
        request.setParameter( "status", Integer.toString( EnumStatus.enabled.getId( ) ) );

        String strUrl = _bean.doCreateHtmlPage( request );

        assertTrue( strUrl.endsWith( AdminMessageService.getMessageRelativeUrl( ) ), strUrl );
        String strBackUrl = AdminMessageService.getMessage( request ).getBackUrl( );
        assertTrue( strBackUrl.endsWith( "jsp/admin/plugins/htmlpage/CreateHtmlPage.jsp" ), strBackUrl );
    }

    @Test
    public void testDoModifyHtmlPageWithEndDateBeforeStartDateReturnsToTheModificationScreen( ) throws AccessDeniedException
    {
        _htmlPage = new HtmlPage( );
        _htmlPage.setDescription( "htmlpage return url" );
        _htmlPage.setHtmlContent( "<p>content</p>" );
        _htmlPage.setStatus( EnumStatus.disabled.getId( ) );
        _htmlPage.setWorkgroup( "all" );
        _htmlPage.setRole( "none" );
        _htmlPageService.create( _htmlPage, HtmlPageService.getPlugin( ) );

        MockHttpServletRequest request = getUserRequest( );
        _bean.init( request, HtmlPageJspBean.RIGHT_MANAGE_HTMLPAGE );
        request.setParameter( "id_htmlpage", Integer.toString( _htmlPage.getId( ) ) );
        request.setParameter( "description", "htmlpage return url" );
        request.setParameter( "html_content", "<p>content</p>" );
        request.setParameter( "status", Integer.toString( EnumStatus.conditioned.getId( ) ) );
        request.setParameter( "date_start", "2026-12-31" );
        request.setParameter( "date_end", "2026-01-01" );

        String strUrl = _bean.doModifyHtmlPage( request );

        assertTrue( strUrl.endsWith( AdminMessageService.getMessageRelativeUrl( ) ), strUrl );
        String strBackUrl = AdminMessageService.getMessage( request ).getBackUrl( );
        assertTrue( strBackUrl.endsWith( "jsp/admin/plugins/htmlpage/ModifyHtmlPage.jsp?id_htmlpage=" + _htmlPage.getId( ) ), strBackUrl );
    }

    /**
     * A page of the list asked first in a new session (bookmark, session expired) shows the list
     */
    @Test
    public void testPageOfTheListInANewSession( ) throws Exception
    {
        MockHttpServletRequest request = getUserRequest( );
        _bean.init( request, HtmlPageJspBean.RIGHT_MANAGE_HTMLPAGE );
        java.lang.reflect.Field field = HtmlPageJspBean.class.getDeclaredField( "_listIdHtmlPages" );
        field.setAccessible( true );
        field.set( ( (org.jboss.weld.proxy.WeldClientProxy) _bean ).getMetadata( ).getContextualInstance( ), null );
        request.setParameter( "page_index", "1" );

        assertDoesNotThrow( ( ) -> _bean.getManageHtmlPage( request ) );
    }

    /**
     * Modify, duplicate and remove of a page removed meanwhile (other tab) answer the message of an unknown page
     */
    @Test
    public void testActionsOnARemovedPage( ) throws Exception
    {
        HtmlPage htmlPage = new HtmlPage( );
        htmlPage.setDescription( "htmlpage removed meanwhile" );
        htmlPage.setHtmlContent( "<p>content</p>" );
        htmlPage.setStatus( EnumStatus.disabled.getId( ) );
        htmlPage.setWorkgroup( "all" );
        htmlPage.setRole( "none" );
        _htmlPageService.create( htmlPage, HtmlPageService.getPlugin( ) );
        String strId = Integer.toString( htmlPage.getId( ) );
        _htmlPageService.remove( htmlPage, HtmlPageService.getPlugin( ) );

        MockHttpServletRequest request = getUserRequest( );
        _bean.init( request, HtmlPageJspBean.RIGHT_MANAGE_HTMLPAGE );
        request.setParameter( "id_htmlpage", strId );
        request.setParameter( "description", "htmlpage removed meanwhile" );
        request.setParameter( "html_content", "<p>content</p>" );
        request.setParameter( "status", Integer.toString( EnumStatus.disabled.getId( ) ) );
        request.setParameter( "workgroup", "all" );
        request.setParameter( "role", "none" );

        request.setParameter( "token", _bean.getSecurityTokenService( ).getToken( request, "/admin/plugins/htmlpage/modify_htmlpage.html" ) );
        assertTrue( _bean.doModifyHtmlPage( request ).endsWith( AdminMessageService.getMessageRelativeUrl( ) ) );
        request.setParameter( "token", _bean.getSecurityTokenService( ).getToken( request, "/admin/plugins/htmlpage/manage_htmlpage.html" ) );
        assertTrue( _bean.doDuplicateHtmlPage( request ).endsWith( AdminMessageService.getMessageRelativeUrl( ) ) );
        request.setParameter( "token", _bean.getSecurityTokenService( ).getToken( request, "jsp/admin/plugins/htmlpage/DoRemoveHtmlPage.jsp" ) );
        assertTrue( _bean.doRemoveHtmlPage( request ).endsWith( AdminMessageService.getMessageRelativeUrl( ) ) );
    }

    private MockHttpServletRequest getUserRequest( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setMethod( "POST" );
        AdminUserUtils.registerAdminUserWithRight( request, new AdminUser( ), HtmlPageJspBean.RIGHT_MANAGE_HTMLPAGE );

        return request;
    }
}
