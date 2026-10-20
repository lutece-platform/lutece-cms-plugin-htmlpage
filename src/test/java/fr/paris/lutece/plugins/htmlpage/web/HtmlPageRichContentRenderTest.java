/*
 * Copyright (c) 2002-2026, City of Paris
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
package fr.paris.lutece.plugins.htmlpage.web;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.htmlpage.business.HtmlPage;
import fr.paris.lutece.plugins.htmlpage.service.EnumStatus;
import fr.paris.lutece.plugins.htmlpage.service.HtmlPagePlugin;
import fr.paris.lutece.plugins.htmlpage.service.HtmlPageService;
import fr.paris.lutece.portal.service.editor.RichTextContentMethod;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.HTMLOutputFormat;
import freemarker.core.TemplateHTMLOutputModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.inject.Inject;

/**
 * Renders the content of an html page with HTML auto-escaping (LUT-33153), in the two places it is written: the front
 * office page (page_htmlpage.html, through richText()) and the markers htmlpage_&lt;id&gt; that HtmlPageInclude puts in
 * the model of the portal page (RichTextContentService.getMarkup). The content is rich text entered by an
 * administrator: its formatting is kept, its scripts and event handlers are removed by the sanitizer of the core, hence
 * the CDI container of LuteceTestCase.
 */
public class HtmlPageRichContentRenderTest extends LuteceTestCase
{
    private static final String TEMPLATES_ROOT = "webapp/WEB-INF/templates";
    private static final String TEMPLATE_PAGE = "skin/plugins/htmlpage/page_htmlpage.html";
    /** Stand-ins of the core skin macros page_htmlpage.html calls. */
    private static final String MACROS = "htmlpage_test_macros.ftl";
    private static final String MACROS_SOURCE = "<#macro cTpl rest...><#nested></#macro><#macro cContainer rest...><#nested></#macro>"
            + "<#macro cRow rest...><#nested></#macro><#macro cCol rest...><#nested></#macro><#macro cSetPageTitle rest...></#macro>";

    private static final String TITLE = "<h2>Horaires</h2>";
    private static final String LIST = "<ul><li>Lundi</li><li>Mardi</li></ul>";
    private static final String LINK_IN = "<a href=\"https://example.org/plan\">Plan</a>";
    private static final String LINK_OUT = "<a href=\"https://example.org/plan\" rel=\"nofollow\">Plan</a>";
    private static final String IMAGE_IN = "<img src=\"images/plan.png\" alt=\"Plan\">";
    private static final String IMAGE_OUT = "<img src=\"images/plan.png\" alt=\"Plan\" />";
    private static final String TABLE = "<table><tbody><tr><td>9h</td><td>18h</td></tr></tbody></table>";
    private static final String STYLED = "<p style=\"text-align:center\"><strong>Accueil</strong> &amp; <em>information</em></p>";
    private static final String HOSTILE = "<script>alert(1)</script><p onclick=\"alert(2)\">Contact</p>";
    private static final String CONTENT = TITLE + LIST + "<p>" + LINK_IN + " " + IMAGE_IN + "</p>" + TABLE + STYLED + HOSTILE;

    @Inject
    private HtmlPageService _htmlPageService;
    private final List<HtmlPage> _listCreated = new ArrayList<>( );

    @AfterEach
    protected void tearDown( ) throws Exception
    {
        for ( HtmlPage htmlPage : _listCreated )
        {
            _htmlPageService.remove( htmlPage, HtmlPagePlugin.getPlugin( ) );
        }
        super.tearDown( );
    }

    /**
     * The configuration of AbstractFreeMarkerTemplateService for HTML templates, with the richText() method of the core.
     *
     * @return the configuration
     * @throws IOException
     *             if the templates directory cannot be read
     */
    private static Configuration configuration( ) throws IOException
    {
        StringTemplateLoader macros = new StringTemplateLoader( );
        macros.putTemplate( MACROS, MACROS_SOURCE );
        Configuration cfg = new Configuration( Configuration.VERSION_2_3_0 );
        cfg.setOutputFormat( HTMLOutputFormat.INSTANCE );
        cfg.setAutoEscapingPolicy( Configuration.ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY );
        cfg.setTemplateLoader( new MultiTemplateLoader( new TemplateLoader [ ] {
                new FileTemplateLoader( new File( TEMPLATES_ROOT ) ), macros
        } ) );
        cfg.addAutoInclude( MACROS );
        cfg.setSharedVariable( "richText", new RichTextContentMethod( ) );

        return cfg;
    }

    /**
     * Renders a template given as a string, as the portal page template is rendered.
     *
     * @param strTemplate
     *            the template source
     * @param model
     *            the model
     * @return the rendering
     * @throws IOException
     *             if the template cannot be parsed
     * @throws TemplateException
     *             if the rendering fails
     */
    private static String renderString( String strTemplate, Map<String, Object> model ) throws IOException, TemplateException
    {
        StringWriter writer = new StringWriter( );
        new Template( "page", strTemplate, configuration( ) ).process( model, writer );

        return writer.toString( );
    }

    /**
     * Checks that the formatting of the content is kept and that its scripts are removed.
     *
     * @param strHtml
     *            the rendering
     */
    private static void assertRichContentKeptAndScriptsRemoved( String strHtml )
    {
        for ( String strKept : new String [ ] {
                TITLE, LIST, LINK_OUT, IMAGE_OUT, TABLE, STYLED, "<p>Contact</p>"
        } )
        {
            assertTrue( strHtml.contains( strKept ), strKept + " expected in " + strHtml );
        }
        assertFalse( strHtml.contains( "<script>alert" ), strHtml );
        assertFalse( strHtml.contains( "alert(" ), strHtml );
        assertFalse( strHtml.contains( "onclick" ), strHtml );
        assertFalse( strHtml.contains( "&lt;" ), strHtml );
    }

    /**
     * The front office page writes the content with richText(): formatting kept, scripts removed.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testPageKeepsTheRichContentAndRemovesItsScripts( ) throws Exception
    {
        HtmlPage htmlPage = new HtmlPage( );
        htmlPage.setDescription( "Horaires <b>\"l'accueil\"</b>" );
        htmlPage.setHtmlContent( CONTENT );
        Map<String, Object> model = new HashMap<>( );
        model.put( "htmlpage", htmlPage );
        model.put( "page", "htmlpage" );

        StringWriter writer = new StringWriter( );
        configuration( ).getTemplate( TEMPLATE_PAGE ).process( model, writer );

        assertRichContentKeptAndScriptsRemoved( writer.toString( ) );
    }

    /**
     * HtmlPageInclude puts the content of each visible page in the model of the portal page as sanitized markup:
     * ${htmlpage_&lt;id&gt;!} writes it as HTML, without its scripts.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testIncludeMarkerKeepsTheRichContentAndRemovesItsScripts( ) throws Exception
    {
        HtmlPage htmlPage = new HtmlPage( );
        htmlPage.setDescription( "htmlpage test include" );
        htmlPage.setHtmlContent( CONTENT );
        htmlPage.setStatus( EnumStatus.enabled.getId( ) );
        htmlPage.setWorkgroup( "all" );
        htmlPage.setRole( HtmlPage.ROLE_NONE );
        _htmlPageService.create( htmlPage, HtmlPagePlugin.getPlugin( ) );
        _listCreated.add( htmlPage );

        Map<String, Object> model = new HashMap<>( );
        new HtmlPageInclude( ).fillTemplate( model, null, 0, new MockHttpServletRequest( ) );

        String strMarker = "htmlpage_" + htmlPage.getId( );
        assertTrue( model.get( strMarker ) instanceof TemplateHTMLOutputModel, String.valueOf( model.get( strMarker ) ) );
        assertRichContentKeptAndScriptsRemoved( renderString( "<div>${" + strMarker + "!}</div>", model ) );
    }

    /**
     * A content left as a String is escaped: the reason the page uses richText() and the include a markup.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testAStringContentIsEscaped( ) throws Exception
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "htmlpage_1", CONTENT );

        String strHtml = renderString( "${htmlpage_1!}", model );

        assertTrue( strHtml.startsWith( "&lt;h2&gt;Horaires&lt;/h2&gt;" ), strHtml );
    }
}
