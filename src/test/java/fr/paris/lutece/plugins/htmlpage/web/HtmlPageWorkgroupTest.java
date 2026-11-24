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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.htmlpage.business.HtmlPage;
import fr.paris.lutece.plugins.htmlpage.business.HtmlPageHome;
import fr.paris.lutece.plugins.htmlpage.service.EnumStatus;
import fr.paris.lutece.plugins.htmlpage.service.HtmlPageService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import jakarta.inject.Inject;

/**
 * An administrator who does not belong to the workgroup of a page can neither modify, duplicate nor remove it, nor give a page a
 * workgroup he does not belong to
 */
public class HtmlPageWorkgroupTest extends LuteceTestCase
{
    private static final String WORKGROUP_OTHER = "htmlpage_test_other_workgroup";
    private static final String TEMPLATE_MANAGE_HTMLPAGE = "/admin/plugins/htmlpage/manage_htmlpage.html";
    private static final String TEMPLATE_CREATE_HTMLPAGE = "/admin/plugins/htmlpage/create_htmlpage.html";
    private static final String TEMPLATE_MODIFY_HTMLPAGE = "/admin/plugins/htmlpage/modify_htmlpage.html";
    private static final String JSP_DO_REMOVE_HTMLPAGE = "jsp/admin/plugins/htmlpage/DoRemoveHtmlPage.jsp";

    @Inject
    private HtmlPageJspBean _bean;
    @Inject
    private HtmlPageService _htmlPageService;
    private HtmlPage _htmlPage;

    @BeforeEach
    protected void createHtmlPage( )
    {
        _htmlPage = new HtmlPage( );
        _htmlPage.setDescription( "htmlpage workgroup test" );
        _htmlPage.setHtmlContent( "<p>content</p>" );
        _htmlPage.setStatus( EnumStatus.disabled.getId( ) );
        _htmlPage.setWorkgroup( WORKGROUP_OTHER );
        _htmlPage.setRole( "none" );
        _htmlPageService.create( _htmlPage, HtmlPageService.getPlugin( ) );
    }

    @AfterEach
    protected void removeHtmlPages( )
    {
        for ( HtmlPage htmlPage : HtmlPageHome.findAll( HtmlPageService.getPlugin( ) ) )
        {
            if ( htmlPage.getDescription( ).startsWith( "htmlpage workgroup test" ) )
            {
                _htmlPageService.remove( htmlPage, HtmlPageService.getPlugin( ) );
            }
        }
    }

    @Test
    public void testModifyOfAPageOfAnotherWorkgroupIsRefused( ) throws AccessDeniedException
    {
        MockHttpServletRequest request = getUserRequest( );
        request.setParameter( "id_htmlpage", Integer.toString( _htmlPage.getId( ) ) );
        request.setParameter( "description", "htmlpage workgroup test modified" );
        request.setParameter( "html_content", "<p>modified</p>" );
        request.setParameter( "status", Integer.toString( EnumStatus.disabled.getId( ) ) );
        request.setParameter( "workgroup", "all" );
        request.setParameter( "role", "none" );
        request.setParameter( "token", _bean.getSecurityTokenService( ).getToken( request, TEMPLATE_MODIFY_HTMLPAGE ) );

        _bean.doModifyHtmlPage( request );

        HtmlPage htmlPage = HtmlPageHome.findByPrimaryKey( _htmlPage.getId( ), HtmlPageService.getPlugin( ) );
        assertEquals( WORKGROUP_OTHER, htmlPage.getWorkgroup( ) );
        assertEquals( "htmlpage workgroup test", htmlPage.getDescription( ) );
    }

    @Test
    public void testRemoveOfAPageOfAnotherWorkgroupIsRefused( ) throws AccessDeniedException
    {
        MockHttpServletRequest request = getUserRequest( );
        request.setParameter( "id_htmlpage", Integer.toString( _htmlPage.getId( ) ) );
        request.setParameter( "token", _bean.getSecurityTokenService( ).getToken( request, JSP_DO_REMOVE_HTMLPAGE ) );

        _bean.doRemoveHtmlPage( request );

        assertNotNull( HtmlPageHome.findByPrimaryKey( _htmlPage.getId( ), HtmlPageService.getPlugin( ) ) );
    }

    @Test
    public void testDuplicateOfAPageOfAnotherWorkgroupIsRefused( ) throws AccessDeniedException
    {
        MockHttpServletRequest request = getUserRequest( );
        request.setParameter( "id_htmlpage", Integer.toString( _htmlPage.getId( ) ) );
        request.setParameter( "token", _bean.getSecurityTokenService( ).getToken( request, TEMPLATE_MANAGE_HTMLPAGE ) );

        _bean.doDuplicateHtmlPage( request );

        assertEquals( 1, HtmlPageHome.findAll( HtmlPageService.getPlugin( ) ).stream( )
                .filter( htmlPage -> htmlPage.getDescription( ).startsWith( "htmlpage workgroup test" ) ).count( ) );
    }

    @Test
    public void testCreateInAWorkgroupOfAnotherAdministratorIsRefused( ) throws AccessDeniedException
    {
        MockHttpServletRequest request = getUserRequest( );
        request.setParameter( "description", "htmlpage workgroup test created" );
        request.setParameter( "html_content", "<p>created</p>" );
        request.setParameter( "status", Integer.toString( EnumStatus.disabled.getId( ) ) );
        request.setParameter( "workgroup", WORKGROUP_OTHER );
        request.setParameter( "role", "none" );
        request.setParameter( "token", _bean.getSecurityTokenService( ).getToken( request, TEMPLATE_CREATE_HTMLPAGE ) );

        _bean.doCreateHtmlPage( request );

        assertEquals( 0, HtmlPageHome.findAll( HtmlPageService.getPlugin( ) ).stream( )
                .filter( htmlPage -> "htmlpage workgroup test created".equals( htmlPage.getDescription( ) ) ).count( ) );
    }

    private MockHttpServletRequest getUserRequest( ) throws AccessDeniedException
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setMethod( "POST" );
        AdminUserUtils.registerAdminUserWithRight( request, new AdminUser( ), HtmlPageJspBean.RIGHT_MANAGE_HTMLPAGE );
        _bean.init( request, HtmlPageJspBean.RIGHT_MANAGE_HTMLPAGE );

        return request;
    }
}
