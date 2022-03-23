/*
 * Copyright (c) 2002-2021, City of Paris
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

import fr.paris.lutece.plugins.htmlpage.business.HtmlPage;
import fr.paris.lutece.plugins.htmlpage.business.HtmlPageHome;
import fr.paris.lutece.plugins.htmlpage.service.HtmlPagePlugin;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.search.SearchIndexer;
import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.url.UrlItem;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Indexer service for htmlPage Xpages
 */
public class HtmlPageIndexer implements SearchIndexer
{
    public static final String SHORT_NAME = "hpg";
    public static final String PROPERTY_INDEXER_NAME = "htmlpage.indexer.name";
    private static final String ENABLE_VALUE_TRUE = "1";

    // private static final String PROPERTY_PAGE_PATH_LABEL = "htmlpage.pagePathLabel";
    private static final String PROPERTY_INDEXER_DESCRIPTION = "htmlpage.indexer.description";
    private static final String PROPERTY_INDEXER_VERSION = "htmlpage.indexer.version";
    private static final String PROPERTY_INDEXER_ENABLE = "htmlpage.indexer.enable";
    public static final String PROPERTY_INDEX_TYPE_PAGE = "htmlpage";
    private static final String PARAMETER_HTMLPAGE_ID = "htmlpage_id";
    private static final String JSP_SEARCH_HTMLPAGE = "jsp/site/Portal.jsp?page=htmlpage&query=";

    /**
     * Returns the indexer service description
     * 
     * @return The indexer service description
     */
    public String getDescription( )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_DESCRIPTION );
    }

    /**
     * Index all enabled HtmlPage
     * 
     * @throws IOException
     *             exception
     * @throws InterruptedException
     *             exception
     * @throws SiteMessageException
     *             exception
     */
    public void indexDocuments( ) throws IOException, InterruptedException, SiteMessageException
    {
        String strPortalUrl = AppPathService.getPortalUrl( );
        Plugin plugin = PluginService.getPlugin( HtmlPagePlugin.PLUGIN_NAME );

        Collection<HtmlPage> listHtmlPages = HtmlPageHome.findEnabledHtmlPageList( plugin );

        for ( HtmlPage htmlpage : listHtmlPages )
        {
            UrlItem url = new UrlItem( strPortalUrl );
            url.addParameter( XPageAppService.PARAM_XPAGE_APP, HtmlPagePlugin.PLUGIN_NAME );
            url.addParameter( PARAMETER_HTMLPAGE_ID, htmlpage.getId( ) );

            org.apache.lucene.document.Document docHtmlPage = getDocument( htmlpage, url.getUrl( ), plugin );
            IndexationService.write( docHtmlPage );
        }
    }

    /**
     * Return a list of lucene document for incremental indexing
     * 
     * @param strId
     *            the uid of the document
     * @return listDocuments the document list
     */
    public List<Document> getDocuments( String strId ) throws IOException, InterruptedException, SiteMessageException
    {
        ArrayList<org.apache.lucene.document.Document> listDocuments = new ArrayList<>( );
        String strPortalUrl = AppPathService.getPortalUrl( );
        Plugin plugin = PluginService.getPlugin( HtmlPagePlugin.PLUGIN_NAME );

        HtmlPage htmlpage = HtmlPageHome.findEnabledHtmlPage( Integer.parseInt( strId ), plugin );
        if ( htmlpage != null )
        {
            UrlItem url = new UrlItem( strPortalUrl );
            url.addParameter( XPageAppService.PARAM_XPAGE_APP, HtmlPagePlugin.PLUGIN_NAME );
            url.addParameter( PARAMETER_HTMLPAGE_ID, htmlpage.getId( ) );

            org.apache.lucene.document.Document docHtmlPage = null;
            try
            {
                docHtmlPage = getDocument( htmlpage, url.getUrl( ), plugin );
            }
            catch( Exception e )
            {
                String strMessage = "HtmlPage ID : " + htmlpage.getId( );
                IndexationService.error( this, e, strMessage );
            }
            if ( docHtmlPage != null )
            {
                listDocuments.add( docHtmlPage );
            }
        }
        return listDocuments;
    }

    /**
     * {@inheritDoc}
     */
    public String getName( )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_NAME );
    }

    /**
     * {@inheritDoc}
     */
    public String getVersion( )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_VERSION );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnable( )
    {
        boolean bReturn = false;
        String strEnable = AppPropertiesService.getProperty( PROPERTY_INDEXER_ENABLE );

        if ( ( strEnable != null ) && ( strEnable.equalsIgnoreCase( Boolean.TRUE.toString( ) ) || strEnable.equals( ENABLE_VALUE_TRUE ) )
                && PluginService.isPluginEnable( HtmlPagePlugin.PLUGIN_NAME ) )
        {
            bReturn = true;
        }

        return bReturn;
    }

    /**
     * Builds a document which will be used by Lucene during the indexing of the pages of the site with the following fields : summary, uid, url, contents,
     * title and description.
     * 
     * @return the built Document
     * @param strUrl
     *            The base URL for documents
     * @param htmlpage
     *            the page to index
     * @param plugin
     *            The {@link Plugin}
     * @throws IOException
     *             The IO Exception
     * @throws InterruptedException
     *             The InterruptedException
     * @throws SiteMessageException
     *             occurs when a site message need to be displayed
     */
    private Document getDocument( HtmlPage htmlpage, String strUrl, Plugin plugin ) throws IOException
    {
    	  FieldType ft = new FieldType( StringField.TYPE_STORED );
          ft.setOmitNorms( false );

          FieldType ftNotStored = new FieldType( StringField.TYPE_NOT_STORED );
          ftNotStored.setOmitNorms( false );
          ftNotStored.setTokenized( false );

        // make a new, empty document
        org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document( );

        // Add the url as a field named "url". Use an UnIndexed field, so
        // that the url is just stored with the question/answer, but is not searchable.
        doc.add( new Field( SearchItem.FIELD_URL, strUrl, ft ) );

        // Add the uid as a field, so that index can be incrementally maintained.
        // This field is not stored with question/answer, it is indexed, but it is not
        // tokenized prior to indexing.
        doc.add( new Field( SearchItem.FIELD_UID, htmlpage.getId( ) + "_" + SHORT_NAME, ftNotStored) );

        String strContentToIndex = getContentToIndex( htmlpage );
        ContentHandler handler = new BodyContentHandler( );
        Metadata metadata = new Metadata( );
        try
        {
            new HtmlParser( ).parse( new ByteArrayInputStream( strContentToIndex.getBytes( ) ), handler, metadata, new ParseContext( ) );
        }
        catch( SAXException | TikaException e )
        {
            throw new AppException( "Error during page parsing." );
        }

        // the content of the article is recovered in the parser because this one
        // had replaced the encoded caracters (as &eacute;) by the corresponding special caracter (as ?)
        StringBuilder sb = new StringBuilder( handler.toString( ) );

        doc.add( new Field( SearchItem.FIELD_CONTENTS, sb.toString( ), TextField.TYPE_NOT_STORED ) );

        // Add the subject name as a separate Text field, so that it can be searched
        // separately.
        doc.add( new Field( SearchItem.FIELD_TITLE, htmlpage.getDescription( ), ft ) );

        doc.add( new Field( SearchItem.FIELD_TYPE, HtmlPagePlugin.PLUGIN_NAME, ft ) );

        // return the document
        return doc;
    }

    /**
     * Set the Content to index
     * 
     * @param htmlpage
     *            The htmlpage to index
     * @return The content to index
     */
    private static String getContentToIndex( HtmlPage htmlpage )
    {
        StringBuffer sbContentToIndex = new StringBuffer( );

        // index the title
        sbContentToIndex.append( htmlpage.getDescription( ) );

        sbContentToIndex.append( " " );

        sbContentToIndex.append( htmlpage.getHtmlContent( ) );

        return sbContentToIndex.toString( );
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getListType( )
    {
        List<String> listType = new ArrayList<>( );
        listType.add( HtmlPagePlugin.PLUGIN_NAME );

        return listType;
    }

    /**
     * {@inheritDoc}
     */
    public String getSpecificSearchAppUrl( )
    {
        return JSP_SEARCH_HTMLPAGE;
    }
}
