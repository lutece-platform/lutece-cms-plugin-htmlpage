package fr.paris.lutece.plugins.htmlpage.service;

import java.util.List;

import fr.paris.lutece.plugins.htmlpage.business.HtmlPage;

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
}
