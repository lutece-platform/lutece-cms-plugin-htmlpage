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

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.htmlpage.business.HtmlPage;
import fr.paris.lutece.plugins.htmlpage.service.EnumStatus;
import fr.paris.lutece.plugins.htmlpage.service.HtmlPageService;
import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;

/**
 * The indexed documents carry the role of the page, read by the role filter of the portal search
 */
public class HtmlPageIndexerRoleTest extends LuteceTestCase
{
    @Inject
    private HtmlPageService _htmlPageService;
    private final List<HtmlPage> _listCreated = new ArrayList<>( );

    @AfterEach
    protected void removeHtmlPages( )
    {
        for ( HtmlPage htmlPage : _listCreated )
        {
            _htmlPageService.remove( htmlPage, HtmlPageService.getPlugin( ) );
        }
    }

    @Test
    public void testDocumentCarriesTheRole( ) throws Exception
    {
        assertEquals( "none", roleOfTheDocument( create( "htmlpage indexer public", null ) ) );
        assertEquals( "none", roleOfTheDocument( create( "htmlpage indexer none", "none" ) ) );
        assertEquals( "htmlpage_test_role", roleOfTheDocument( create( "htmlpage indexer restricted", "htmlpage_test_role" ) ) );
    }

    private String roleOfTheDocument( HtmlPage htmlPage ) throws Exception
    {
        List<Document> listDocuments = new HtmlPageIndexer( ).getDocuments( Integer.toString( htmlPage.getId( ) ) );
        assertEquals( 1, listDocuments.size( ) );

        return listDocuments.get( 0 ).get( SearchItem.FIELD_ROLE );
    }

    private HtmlPage create( String strDescription, String strRole )
    {
        HtmlPage htmlPage = new HtmlPage( );
        htmlPage.setDescription( strDescription );
        htmlPage.setHtmlContent( "<p>" + strDescription + "</p>" );
        htmlPage.setStatus( EnumStatus.enabled.getId( ) );
        htmlPage.setWorkgroup( "all" );
        htmlPage.setRole( strRole );
        _htmlPageService.create( htmlPage, HtmlPageService.getPlugin( ) );
        _listCreated.add( htmlPage );

        return htmlPage;
    }
}
