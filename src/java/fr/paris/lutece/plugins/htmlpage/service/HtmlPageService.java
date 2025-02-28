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
import fr.paris.lutece.plugins.htmlpage.service.search.HtmlPageIndexer;
import fr.paris.lutece.plugins.htmlpage.utils.HtmlPageIndexerUtils;
import fr.paris.lutece.plugins.htmlpage.utils.HtmlPageUtil;
import fr.paris.lutece.portal.business.indexeraction.IndexerAction;
import fr.paris.lutece.portal.service.cache.Lutece107Cache;
import fr.paris.lutece.portal.service.cache.LuteceCache;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;

/**
 * 
 * HtmlPageService
 */
@ApplicationScoped
public class HtmlPageService implements IHtmlPageService
{   
	private static final String CACHE_SERVICE_NAME = "PublicHtmlPageCacheService";
	
    @Inject
    private HtmlPageIndexerUtils _htmlPageIndexerUtils;
    
    @Inject
	@LuteceCache( cacheName = CACHE_SERVICE_NAME, keyType = String.class, valueType = Object.class, enable = true )
	private Lutece107Cache<String, Object> _cachePublicHtmlPage;

    /**
     * Initializes the Housing service
     *
     */
    public void init( )
    {
        HtmlPage.init( );
    }
    
    @Override
    public HtmlPage getHtmlPageCache( int nId )
    {
    	HtmlPage htmlPage = ( HtmlPage ) _cachePublicHtmlPage.get( getCacheKey( String.valueOf( nId ) ) );
        
        if ( htmlPage == null || !HtmlPageUtil.isActivedPageHtml( htmlPage ) )
        {
            htmlPage = getEnableHtmlPage( nId );
            
            if ( htmlPage != null && !HtmlPageUtil.isRoleExist( htmlPage.getRole( ) ))
            {
            	String htmlPageCacheKey = getCacheKey( String.valueOf( htmlPage.getId( ) ) );
            	if( _cachePublicHtmlPage.get( htmlPageCacheKey ) == null )
            	{
            		_cachePublicHtmlPage.put( htmlPageCacheKey, htmlPage );
            	}
            }           
        }
        return htmlPage;
    }

    @Override
    public List<HtmlPage> getHtmlPageListCache( )
    {
    	String allPageKey = getCacheKey( "all" );
    	List<HtmlPage> htmlPageList = ( List<HtmlPage> ) _cachePublicHtmlPage.get( allPageKey );
        
        if ( htmlPageList == null )
        {
            htmlPageList = getEnabledHtmlPageList( );
            
            if ( htmlPageList != null )
            {
            	if( _cachePublicHtmlPage.get( allPageKey ) == null )
            	{
            		_cachePublicHtmlPage.put( allPageKey, htmlPageList );
            	}
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
    
    /**
     * Creation of an instance of htmlpage
     *
     * @param htmlpage
     *            The instance of the htmlpage which contains the informations to store
     * @param plugin
     *            The Plugin object
     * @return The instance of htmlpage which has been created with its primary key.
     */
    public HtmlPage create( HtmlPage htmlpage, Plugin plugin )
    {
    	HtmlPageHome.create( htmlpage, plugin );

        if ( htmlpage.isEnabled( ) )
        {
            String strIdHtmlPage = Integer.toString( htmlpage.getId( ) );
            IndexationService.addIndexerAction( strIdHtmlPage, AppPropertiesService.getProperty( HtmlPageIndexer.PROPERTY_INDEXER_NAME ),
                    IndexerAction.TASK_CREATE );

            _htmlPageIndexerUtils.addIndexerAction( strIdHtmlPage, IndexerAction.TASK_CREATE );
        }

        return htmlpage;
    }
    
    /**
     * Update of the htmlpage which is specified in parameter
     *
     * @param htmlpage
     *            The instance of the htmlpage which contains the data to store
     * @param plugin
     *            The Plugin object
     * @return The instance of the htmlpage which has been updated
     */
    public HtmlPage update( HtmlPage htmlpage, Plugin plugin )
    {   	
        String strIdHtmlPage = Integer.toString( htmlpage.getId( ) );
        if ( htmlpage.isEnabled( ) )
        {
            IndexationService.addIndexerAction( strIdHtmlPage, AppPropertiesService.getProperty( HtmlPageIndexer.PROPERTY_INDEXER_NAME ),
                    IndexerAction.TASK_MODIFY );

            _htmlPageIndexerUtils.addIndexerAction( strIdHtmlPage, IndexerAction.TASK_MODIFY );
        }
        else
        {
            HtmlPage oldPage = getEnableHtmlPage( Integer.parseInt( strIdHtmlPage ) );

            if ( oldPage != null )
            {
                IndexationService.addIndexerAction( strIdHtmlPage + "_" + HtmlPageIndexer.SHORT_NAME,
                        AppPropertiesService.getProperty( HtmlPageIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_DELETE );

                _htmlPageIndexerUtils.addIndexerAction( strIdHtmlPage, IndexerAction.TASK_DELETE );
            }
        }
        
        HtmlPageHome.update( htmlpage, plugin );

        return htmlpage;
    }

    /**
     * Remove the Htmlpage whose identifier is specified in parameter
     * 
     * @param htmlpage
     *            The Htmlpage object to remove
     * @param plugin
     *            The Plugin object
     */
    public void remove( HtmlPage htmlpage, Plugin plugin )
    {
    	HtmlPageHome.remove( htmlpage, plugin );

        if ( htmlpage.isEnabled( ) )
        {
            String strIdHtmlPage = Integer.toString( htmlpage.getId( ) );
            IndexationService.addIndexerAction( strIdHtmlPage + "_" + HtmlPageIndexer.SHORT_NAME,
                    AppPropertiesService.getProperty( HtmlPageIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_DELETE );

            _htmlPageIndexerUtils.addIndexerAction( strIdHtmlPage, IndexerAction.TASK_DELETE );
        }
    }
    
    /**
     * Create a cache key from the html page id
     * 
     * @param strId
     *           The html page id
     * @return cache key
     */
    private String getCacheKey( String strId )
    {
        StringBuilder sbKey = new StringBuilder( );
        sbKey.append( "[htmlpage:" ).append( strId ).append( "]" );
        return sbKey.toString( );
    }
    
    /**
     * This method observes the initialization of the {@link ApplicationScoped} context.
     * It ensures that this CDI beans are instantiated at the application startup.
     *
     * <p>This method is triggered automatically by CDI when the {@link ApplicationScoped} context is initialized,
     * which typically occurs during the startup of the application server.</p>
     *
     * @param context the {@link ServletContext} that is initialized. This parameter is observed
     *                and injected automatically by CDI when the {@link ApplicationScoped} context is initialized.
     */
    public void initializedService(@Observes @Initialized(ApplicationScoped.class) ServletContext context) {
        // This method is intentionally left empty to trigger CDI bean instantiation
    }
}
