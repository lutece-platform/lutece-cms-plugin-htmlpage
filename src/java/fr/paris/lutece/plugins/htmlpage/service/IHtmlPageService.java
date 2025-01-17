package fr.paris.lutece.plugins.htmlpage.service;

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
     * Get htmlpage in the cache by id
     * @param nId
     * @return htmlPage
     */
    public HtmlPage getHtmlPageCache ( int nId );
    
    /**
     * Get enable htmlpage by id
     * @param nId
     * @return htmlPage
     */
    public HtmlPage getEnableHtmlPage ( int nId );
    
    /**
     * Gets all htmlpage in the cache
     * @return list of html
     */
    public List<HtmlPage> getHtmlPageListCache ( );
    
    /**
     * Gets enable htmlPage list
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
}
