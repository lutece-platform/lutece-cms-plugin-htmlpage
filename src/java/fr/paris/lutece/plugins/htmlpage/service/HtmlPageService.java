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

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.htmlpage.business.HtmlPage;
import fr.paris.lutece.plugins.htmlpage.business.HtmlPageHome;
import fr.paris.lutece.plugins.htmlpage.utils.HtmlPageUtil;

/**
 * 
 * HtmlPageService
 */
public class HtmlPageService implements IHtmlPageService
{
    private static HtmlPageService _singleton;

    /**
     * Initializes the Housing service
     *
     */
    public void init( )
    {
        HtmlPage.init( );
    }

    /**
     * Returns the instance of the singleton
     *
     * @return The instance of the singleton
     */
    public static HtmlPageService getInstance( )
    {
        if ( _singleton == null )
        {
            _singleton = new HtmlPageService( );
        }
        return _singleton;
    }

    @Override
    public HtmlPage getHtmlPageCache( int nId )
    {
        HtmlPage htmlPage = PublicHtmlPageCacheService.getService( ).getHtmlPageCache( String.valueOf( nId ) );
        
        if ( htmlPage == null || !HtmlPageUtil.isActivedPageHtml( htmlPage ))
        {
            htmlPage = getEnableHtmlPage( nId );
            
            if ( htmlPage != null && !HtmlPageUtil.isRoleExist( htmlPage.getRole( ) ))
            {
                PublicHtmlPageCacheService.getService( ).addHtmlPageCache( htmlPage );
            }           
        }
        return htmlPage;
    }

    @Override
    public List<HtmlPage> getHtmlPageListCache( )
    {
        List<HtmlPage> htmlPageList = PublicHtmlPageCacheService.getService( ).getHtmlPageListCache(  );
        
        if ( htmlPageList == null )
        {
            htmlPageList = getEnabledHtmlPageList( );
            
            if ( htmlPageList != null )
            {
                PublicHtmlPageCacheService.getService( ).addHtmlPageCache( htmlPageList );
            }           
        }
        return htmlPageList;
    }

    @Override
    public HtmlPage getEnableHtmlPage( int nId )
    {
        HtmlPage htmlPage = HtmlPageHome.findByPrimaryKey( nId, HtmlPagePlugin.getPlugin( ) );
        
        return HtmlPageUtil.isActivedPageHtml( htmlPage ) ? htmlPage : null;
    }

    @Override
    public List<HtmlPage> getEnabledHtmlPageList( )
    {
        List<HtmlPage> enablehtmlPageList = new ArrayList<>( );
        List<HtmlPage> htmlPageList = ( List<HtmlPage> ) HtmlPageHome.findAll( HtmlPagePlugin.getPlugin( ) );
       
        for ( HtmlPage htmlPage : htmlPageList )
        {
            if ( HtmlPageUtil.isActivedPageHtml( htmlPage ) )
            {
                enablehtmlPageList.add( htmlPage );
            }
        }
        
        return enablehtmlPageList;
    }

}
