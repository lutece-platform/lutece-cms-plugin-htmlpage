/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
package fr.paris.lutece.plugins.htmlpage.business;

import java.util.Collection;

import fr.paris.lutece.plugins.htmlpage.service.search.HtmlPageIndexer;
import fr.paris.lutece.plugins.htmlpage.utils.HtmlPageIndexerUtils;
import fr.paris.lutece.portal.business.indexeraction.IndexerAction;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;


/**
 * This class provides instances management methods (create, find, ...) for Htmlpage objects
 * @author lenaini
 */
public class HtmlPageHome
{
    // Static variable pointed at the DAO instance
    private static IHtmlPageDAO _dao = (IHtmlPageDAO) SpringContextService.getPluginBean( "htmlpage", "htmlPageDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private HtmlPageHome(  )
    {
    }

    /**
      * Creation of an instance of htmlpage
      *
      * @param htmlpage The instance of the htmlpage which contains the informations to store
      * @param plugin The Plugin object
      * @return The  instance of htmlpage which has been created with its primary key.
      */
    public static HtmlPage create( HtmlPage htmlpage, Plugin plugin )
    {
        _dao.insert( htmlpage, plugin );

        if ( htmlpage.isEnabled(  ) )
        {
        	String strIdHtmlPage = Integer.toString( htmlpage.getId(  ) );
            IndexationService.addIndexerAction( strIdHtmlPage,
                AppPropertiesService.getProperty( HtmlPageIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_CREATE );
            
            HtmlPageIndexerUtils.addIndexerAction( strIdHtmlPage, IndexerAction.TASK_CREATE );
        }

        return htmlpage;
    }

    /**
     * Update of the htmlpage which is specified in parameter
     *
     * @param htmlpage The instance of the htmlpage which contains the data to store
     * @param plugin The Plugin object
     * @return The instance of the  htmlpage which has been updated
     */
    public static HtmlPage update( HtmlPage htmlpage, Plugin plugin )
    {
    	String strIdHtmlPage = Integer.toString( htmlpage.getId(  ) );
        if ( htmlpage.isEnabled(  ) )
        {
            IndexationService.addIndexerAction( strIdHtmlPage,
                AppPropertiesService.getProperty( HtmlPageIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_MODIFY );
            
            HtmlPageIndexerUtils.addIndexerAction( strIdHtmlPage, IndexerAction.TASK_MODIFY );
        }
        else
        {
            HtmlPage oldPage = findEnabledHtmlPage( htmlpage.getId(  ), plugin );

            if ( oldPage != null )
            {
                IndexationService.addIndexerAction( strIdHtmlPage + "_" +
                    HtmlPageIndexer.SHORT_NAME,
                    AppPropertiesService.getProperty( HtmlPageIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_DELETE );
                
                HtmlPageIndexerUtils.addIndexerAction( strIdHtmlPage, IndexerAction.TASK_DELETE );
            }
        }

        _dao.store( htmlpage, plugin );

        return htmlpage;
    }

    /**
     * Remove the Htmlpage whose identifier is specified in parameter
     * @param htmlpage The Htmlpage object to remove
     * @param plugin The Plugin object
     */
    public static void remove( HtmlPage htmlpage, Plugin plugin )
    {
        _dao.delete( htmlpage, plugin );

        if ( htmlpage.isEnabled(  ) )
        {
        	String strIdHtmlPage = Integer.toString( htmlpage.getId(  ) );
            IndexationService.addIndexerAction( strIdHtmlPage + "_" +
                HtmlPageIndexer.SHORT_NAME, AppPropertiesService.getProperty( HtmlPageIndexer.PROPERTY_INDEXER_NAME ),
                IndexerAction.TASK_DELETE );
            
            HtmlPageIndexerUtils.addIndexerAction( strIdHtmlPage, IndexerAction.TASK_DELETE );
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a htmlpage whose identifier is specified in parameter
     * @param nKey The Primary key of the htmlpage
     * @param plugin The Plugin object
     * @return An instance of htmlpage
     */
    public static HtmlPage findByPrimaryKey( int nKey, Plugin plugin )
    {
        return _dao.load( nKey, plugin );
    }

    /**
     * Returns a collection of htmlpages objects
     * @param plugin The Plugin object
     * @return A collection of htmlpages
     */
    public static Collection<HtmlPage> findAll( Plugin plugin )
    {
        return _dao.selectAll( plugin );
    }

    /**
     * Returns  htmlpage object with valid status
     * @param nKey the page id
     * @param plugin The Plugin object
     * @return A htmlpage
     */
    public static HtmlPage findEnabledHtmlPage( int nKey, Plugin plugin )
    {
        return _dao.selectEnabledHtmlPage( nKey, plugin );
    }

    /**
     * Returns a collection of htmlpages objects with valid status
     * @param plugin The Plugin object
     * @return A collection of htmlpages
     */
    public static Collection<HtmlPage> findEnabledHtmlPageList( Plugin plugin )
    {
        return _dao.selectEnabledHtmlPageList( plugin );
    }
}
