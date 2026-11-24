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
package fr.paris.lutece.plugins.htmlpage.service.search;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.htmlpage.business.HtmlPage;
import fr.paris.lutece.plugins.htmlpage.service.EnumStatus;
import fr.paris.lutece.plugins.htmlpage.service.HtmlPageService;
import fr.paris.lutece.portal.service.search.SearchResult;
import fr.paris.lutece.portal.service.security.LuteceAuthentication;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The plugin search lists only the pages the visitor may see: role held, page published
 */
public class HtmlPageSearchEngineVisibilityTest extends LuteceTestCase
{
    @Inject
    private HtmlPageService _htmlPageService;
    private final List<HtmlPage> _listCreated = new ArrayList<>( );
    private Object _savedAuthentication;
    private Object _savedEnable;

    @AfterEach
    protected void restore( ) throws Exception
    {
        for ( HtmlPage htmlPage : _listCreated )
        {
            _htmlPageService.remove( htmlPage, HtmlPageService.getPlugin( ) );
        }
        if ( _savedEnable != null )
        {
            setSecurityField( "_bEnable", _savedEnable );
            setSecurityField( "_authenticationService", _savedAuthentication );
        }
    }

    @Test
    public void testRestrictedPageIsNotListedToAnAnonymousVisitor( ) throws Exception
    {
        enableAuthenticationWithAnonymousVisitor( );
        HtmlPage pageRestricted = create( "htmlpage search restricted", EnumStatus.enabled, "htmlpage_test_role" );
        HtmlPage pagePublic = create( "htmlpage search public", EnumStatus.enabled, "none" );

        List<SearchResult> listVisible = filter( List.of( result( pageRestricted ), result( pagePublic ) ), new MockHttpServletRequest( ) );

        assertEquals( 1, listVisible.size( ) );
        assertEquals( pagePublic.getDescription( ), listVisible.get( 0 ).getTitle( ) );
    }

    @Test
    public void testDisabledPageIsNotListed( ) throws Exception
    {
        HtmlPage pageDisabled = create( "htmlpage search disabled", EnumStatus.disabled, "none" );

        assertTrue( filter( List.of( result( pageDisabled ) ), new MockHttpServletRequest( ) ).isEmpty( ) );
    }

    @SuppressWarnings( "unchecked" )
    private static List<SearchResult> filter( List<SearchResult> listResults, HttpServletRequest request ) throws Exception
    {
        Method method = HtmlPageSearchEngine.class.getDeclaredMethod( "filterVisibleResults", List.class, HttpServletRequest.class );
        method.setAccessible( true );

        return (List<SearchResult>) method.invoke( null, listResults, request );
    }

    private static SearchResult result( HtmlPage htmlPage )
    {
        SearchResult result = new SearchResult( );
        result.setTitle( htmlPage.getDescription( ) );
        result.setUrl( "http://localhost/lutece/jsp/site/Portal.jsp?page=htmlpage&htmlpage_id=" + htmlPage.getId( ) );

        return result;
    }

    private HtmlPage create( String strDescription, EnumStatus status, String strRole )
    {
        HtmlPage htmlPage = new HtmlPage( );
        htmlPage.setDescription( strDescription );
        htmlPage.setHtmlContent( "<p>" + strDescription + "</p>" );
        htmlPage.setStatus( status.getId( ) );
        htmlPage.setWorkgroup( "all" );
        htmlPage.setRole( strRole );
        _htmlPageService.create( htmlPage, HtmlPageService.getPlugin( ) );
        _listCreated.add( htmlPage );

        return htmlPage;
    }

    /**
     * Authentication on, with a portal authentication where nobody is signed in
     */
    private void enableAuthenticationWithAnonymousVisitor( ) throws Exception
    {
        _savedEnable = getSecurityField( "_bEnable" );
        _savedAuthentication = getSecurityField( "_authenticationService" );
        Object authentication = Proxy.newProxyInstance( getClass( ).getClassLoader( ), new Class<?> [ ] {
                LuteceAuthentication.class
        }, ( proxy, method, args ) -> method.getReturnType( ) == boolean.class ? Boolean.FALSE : null );
        setSecurityField( "_authenticationService", authentication );
        setSecurityField( "_bEnable", Boolean.TRUE );
    }

    private static Object getSecurityField( String strName ) throws Exception
    {
        Field field = SecurityService.class.getDeclaredField( strName );
        field.setAccessible( true );

        return field.get( null );
    }

    private static void setSecurityField( String strName, Object value ) throws Exception
    {
        Field field = SecurityService.class.getDeclaredField( strName );
        field.setAccessible( true );
        field.set( null, value );
    }
}
