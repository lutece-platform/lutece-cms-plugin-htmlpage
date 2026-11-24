package fr.paris.lutece.plugins.htmlpage.service;

import java.sql.Timestamp;
import java.util.List;


import fr.paris.lutece.plugins.htmlpage.business.HtmlPage;
import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * 
 * IHtmlPageService
 *
 */
public interface IHtmlPageService
{
    /**
     * Get enabled html page by id from the cache if possible, from database otherwise
     * @param nId
     * @return htmlPage
     */
    public HtmlPage getHtmlPageCache ( int nId );
    
    /**
     * Get enabled html page by id from database
     * @param nId
     * @return htmlPage
     */
    public HtmlPage getEnableHtmlPage ( int nId );
    
    /**
     * Get all enabled html pages from cache if possible, from database otherwise
     * @return list of htmlpage
     */
    public List<HtmlPage> getHtmlPageListCache ( );
    
    /**
     * Get all enabled html pages from database
     * @return list of htmlpage
     */
    public List<HtmlPage> getEnabledHtmlPageList( );
    
    /**
     * Creation of an instance of htmlpage
     *
     * @param htmlpage
     *            The instance of the htmlpage which contains the informations to store
     * @param plugin
     *            The Plugin object
     * @return The instance of htmlpage which has been created with its primary key.
     */
    public HtmlPage create( HtmlPage htmlpage, Plugin plugin );
    
    /**
     * Update of the htmlpage which is specified in parameter
     *
     * @param htmlpage
     *            The instance of the htmlpage which contains the data to store
     * @param plugin
     *            The Plugin object
     * @return The instance of the htmlpage which has been updated
     */
    public HtmlPage update( HtmlPage htmlpage, Plugin plugin );
    
    /**
     * Remove the Htmlpage whose identifier is specified in parameter
     * 
     * @param htmlpage
     *            The Htmlpage object to remove
     * @param plugin
     *            The Plugin object
     */
    public void remove( HtmlPage htmlpage, Plugin plugin );

    /**
     * Queues the indexing of the conditioned pages whose publication period started, and the removal from the index of those whose period
     * ended, between two instants
     *
     * @param tsFrom
     *            the beginning of the interval, excluded
     * @param tsTo
     *            the end of the interval, included
     * @return the number of indexer actions queued
     */
    public int indexPublicationChanges( Timestamp tsFrom, Timestamp tsTo );
}
