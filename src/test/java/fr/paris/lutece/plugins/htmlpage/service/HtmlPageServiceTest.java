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
package fr.paris.lutece.plugins.htmlpage.service;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.htmlpage.business.HtmlPage;
import fr.paris.lutece.plugins.htmlpage.service.search.HtmlPageIndexer;
import fr.paris.lutece.portal.business.indexeraction.IndexerAction;
import fr.paris.lutece.portal.business.indexeraction.IndexerActionHome;
import fr.paris.lutece.portal.service.cache.Lutece107Cache;
import fr.paris.lutece.portal.service.cache.LuteceCache;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;

/**
 * Cache of the html pages, shared by the instances of the site
 */
public class HtmlPageServiceTest extends LuteceTestCase
{
    @Inject
    private HtmlPageService _htmlPageService;
    @Inject
    @LuteceCache( cacheName = "PublicHtmlPageCacheService", keyType = String.class, valueType = HtmlPage.class, enable = true )
    private Lutece107Cache<String, HtmlPage> _cache;
    private final List<HtmlPage> _listCreated = new ArrayList<>( );

    @AfterEach
    protected void tearDown( ) throws Exception
    {
        for ( HtmlPage htmlPage : _listCreated )
        {
            _htmlPageService.remove( htmlPage, HtmlPageService.getPlugin( ) );
        }
        super.tearDown( );
    }

    /**
     * The list holds the enabled pages sorted by description, from the cache and after the cache was emptied (reset of all the
     * caches, or another instance)
     */
    @Test
    public void testListFromCacheAndAfterReset( )
    {
        HtmlPage pageB = create( "htmlpage test b", EnumStatus.enabled );
        HtmlPage pageA = create( "htmlpage test a", EnumStatus.enabled );
        HtmlPage pageDisabled = create( "htmlpage test c", EnumStatus.disabled );

        assertEquals( List.of( pageA.getId( ), pageB.getId( ) ), testPageIds( ) );

        _cache.resetCache( );
        assertEquals( List.of( pageA.getId( ), pageB.getId( ) ), testPageIds( ) );
        assertNull( _htmlPageService.getHtmlPageCache( pageDisabled.getId( ) ) );
    }

    /**
     * A removed page leaves the list and the cache
     */
    @Test
    public void testRemovedPageLeavesTheCache( )
    {
        HtmlPage pageKept = create( "htmlpage test kept", EnumStatus.enabled );
        HtmlPage pageRemoved = create( "htmlpage test removed", EnumStatus.enabled );

        _htmlPageService.remove( pageRemoved, HtmlPageService.getPlugin( ) );
        _listCreated.remove( pageRemoved );

        assertEquals( List.of( pageKept.getId( ) ), testPageIds( ) );
        assertNull( _cache.get( "[htmlpage:" + pageRemoved.getId( ) + "]" ) );
        assertNull( _htmlPageService.getHtmlPageCache( pageRemoved.getId( ) ) );
    }

    /**
     * A published page that is disabled is removed from the search index: a delete action is queued for its document
     */
    @Test
    public void testDisabledPageLeavesTheIndex( )
    {
        HtmlPage htmlPage = create( "htmlpage test disabled later", EnumStatus.enabled );
        htmlPage.setStatus( EnumStatus.disabled.getId( ) );

        _htmlPageService.update( htmlPage, HtmlPageService.getPlugin( ) );

        assertTrue( IndexerActionHome.getList( ).stream( ).anyMatch( action -> action.getIdTask( ) == IndexerAction.TASK_DELETE
                && ( htmlPage.getId( ) + "_" + HtmlPageIndexer.SHORT_NAME ).equals( action.getIdDocument( ) ) ) );
    }

    /**
     * A conditioned page is indexed when its publication period starts, and removed from the index when it ends
     */
    @Test
    public void testPublicationPeriodChangesAreIndexed( ) throws Exception
    {
        long lNow = System.currentTimeMillis( );
        HtmlPage pageStarting = createConditioned( "htmlpage test starting", lNow - 60_000, lNow + 3_600_000 );
        HtmlPage pageEnding = createConditioned( "htmlpage test ending", lNow - 3_600_000, lNow - 60_000 );
        HtmlPage pageLater = createConditioned( "htmlpage test later", lNow + 3_600_000, lNow + 7_200_000 );

        Method method = HtmlPageService.class.getMethod( "indexPublicationChanges", Timestamp.class, Timestamp.class );
        method.invoke( _htmlPageService, new Timestamp( lNow - 120_000 ), new Timestamp( lNow ) );

        List<IndexerAction> listActions = IndexerActionHome.getList( );
        assertTrue( listActions.stream( ).anyMatch( action -> action.getIdTask( ) == IndexerAction.TASK_CREATE
                && Integer.toString( pageStarting.getId( ) ).equals( action.getIdDocument( ) ) ) );
        assertTrue( listActions.stream( ).anyMatch( action -> action.getIdTask( ) == IndexerAction.TASK_DELETE
                && ( pageEnding.getId( ) + "_" + HtmlPageIndexer.SHORT_NAME ).equals( action.getIdDocument( ) ) ) );
        assertFalse( listActions.stream( ).anyMatch( action -> action.getIdDocument( ).startsWith( Integer.toString( pageLater.getId( ) ) ) ) );
    }

    private HtmlPage createConditioned( String strDescription, long lStart, long lEnd )
    {
        HtmlPage htmlPage = new HtmlPage( );
        htmlPage.setDescription( strDescription );
        htmlPage.setHtmlContent( "<p>" + strDescription + "</p>" );
        htmlPage.setStatus( EnumStatus.conditioned.getId( ) );
        htmlPage.setDateStart( new Timestamp( lStart ) );
        htmlPage.setDateEnd( new Timestamp( lEnd ) );
        htmlPage.setWorkgroup( "all" );
        htmlPage.setRole( "none" );
        _htmlPageService.create( htmlPage, HtmlPageService.getPlugin( ) );
        _listCreated.add( htmlPage );

        return htmlPage;
    }

    private HtmlPage create( String strDescription, EnumStatus status )
    {
        HtmlPage htmlPage = new HtmlPage( );
        htmlPage.setDescription( strDescription );
        htmlPage.setHtmlContent( "<p>" + strDescription + "</p>" );
        htmlPage.setStatus( status.getId( ) );
        htmlPage.setWorkgroup( "all" );
        htmlPage.setRole( "none" );
        _htmlPageService.create( htmlPage, HtmlPageService.getPlugin( ) );
        // The generated key is set on the created page (used by the cache and the indexer)
        assertTrue( htmlPage.getId( ) > 0 );
        _listCreated.add( htmlPage );

        return htmlPage;
    }

    /**
     * @return the ids of the pages of this test in the list served to the portal
     */
    private List<Integer> testPageIds( )
    {
        return _htmlPageService.getHtmlPageListCache( ).stream( ).filter( p -> p.getDescription( ).startsWith( "htmlpage test" ) ).map( HtmlPage::getId )
                .toList( );
    }
}
