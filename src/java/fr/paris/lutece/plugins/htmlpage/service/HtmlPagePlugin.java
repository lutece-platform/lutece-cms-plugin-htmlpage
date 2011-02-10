/*
 * HtmlPageListPlugin.java
 *
 * Created on 2 juillet 2008, 10:52
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.htmlpage.service;

import fr.paris.lutece.portal.service.plugin.PluginDefaultImplementation;


/**
 * The HtmlPage Plugin
 * @author lenaini
 */
public class HtmlPagePlugin extends PluginDefaultImplementation
{
    public static final String PLUGIN_NAME = "htmlpage";

    /**
     * Initialize the plugin HtmlPage
     */
    public void init(  )
    {
        HtmlPageService.getInstance(  ).init(  );
    }
}
