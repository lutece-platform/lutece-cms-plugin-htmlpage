/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;


/**
 * This class provides Data Access methods for HtmlPage objects
 * @author lenaini
 */
public class HtmlPageDAO implements IHtmlPageDAO
{
    // Constants
    private static final String SQL_QUERY_NEWPK = "SELECT max( id_htmlpage ) FROM htmlpage ";
    private static final String SQL_QUERY_SELECT = "SELECT id_htmlpage, description, html_content, status, workgroup_key, role FROM htmlpage WHERE id_htmlpage = ? ";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_htmlpage, description, html_content, status, workgroup_key, role FROM htmlpage ORDER BY description, id_htmlpage DESC";
    private static final String SQL_QUERY_SELECT_ENABLED = "SELECT id_htmlpage, description, html_content, status, workgroup_key, role FROM htmlpage WHERE id_htmlpage = ? AND status = 0 ";
    private static final String SQL_QUERY_SELECT_ENABLED_HTMLPAGE_LIST = "SELECT id_htmlpage, description, html_content, status, workgroup_key, role FROM htmlpage WHERE status = 0 ORDER BY description, id_htmlpage DESC";
    private static final String SQL_QUERY_INSERT = "INSERT INTO htmlpage ( id_htmlpage , description, html_content, status, workgroup_key, role )  VALUES ( ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM htmlpage WHERE id_htmlpage = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE htmlpage SET description = ? , html_content = ?, status = ?, workgroup_key = ?, role = ?  WHERE id_htmlpage = ?  ";

    ///////////////////////////////////////////////////////////////////////////////////////
    //Access methods to data

    /**
     * Generates a new primary key
     * @param plugin The plugin
     * @return The new primary key
     */
    private int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEWPK, plugin );
        daoUtil.executeQuery(  );

        int nKey;

        if ( !daoUtil.next(  ) )
        {
            // if the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;

        daoUtil.free(  );

        return nKey;
    }

    ////////////////////////////////////////////////////////////////////////
    // Methods using a dynamic pool

    /**
     * Insert a new record in the table.
     *
     * @param htmlpage The htmlpage object
     * @param plugin The plugin
     */
    public void insert( HtmlPage htmlpage, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        htmlpage.setId( newPrimaryKey( plugin ) );
        daoUtil.setInt( 1, htmlpage.getId(  ) );
        daoUtil.setString( 2, htmlpage.getDescription(  ) );
        daoUtil.setString( 3, htmlpage.getHtmlContent(  ) );
        daoUtil.setInt( 4, htmlpage.getStatus(  ) );
        daoUtil.setString( 5, htmlpage.getWorkgroup(  ) );
        daoUtil.setString( 6, htmlpage.getRole(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Load the data of HtmlPage from the table
     * @param nHtmlPageId The identifier of HtmlPage
     * @param plugin The plugin
     * @return the instance of the HtmlPage
     */
    public HtmlPage load( int nHtmlPageId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nHtmlPageId );
        daoUtil.executeQuery(  );

        HtmlPage htmlpage = null;

        if ( daoUtil.next(  ) )
        {
            htmlpage = new HtmlPage(  );
            htmlpage.setId( daoUtil.getInt( 1 ) );
            htmlpage.setDescription( daoUtil.getString( 2 ) );
            htmlpage.setHtmlContent( daoUtil.getString( 3 ) );
            htmlpage.setStatus( daoUtil.getInt( 4 ) );
            htmlpage.setWorkgroup( daoUtil.getString( 5 ) );
            htmlpage.setRole( daoUtil.getString( 6 ) );
        }

        daoUtil.free(  );

        return htmlpage;
    }

    /**
     * Delete a record from the table
     * @param htmlpage The HtmlPage object
     * @param plugin The plugin
     */
    public void delete( HtmlPage htmlpage, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, htmlpage.getId(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Update the record in the table
     * @param htmlpage The reference of htmlpage
     * @param plugin The plugin
     */
    public void store( HtmlPage htmlpage, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        int nHtmlPageId = htmlpage.getId(  );

        daoUtil.setString( 1, htmlpage.getDescription(  ) );
        daoUtil.setString( 2, htmlpage.getHtmlContent(  ) );
        daoUtil.setInt( 3, htmlpage.getStatus(  ) );
        daoUtil.setString( 4, htmlpage.getWorkgroup(  ) );
        daoUtil.setString( 5, htmlpage.getRole(  ) );
        daoUtil.setInt( 6, nHtmlPageId );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Load the list of htmlpages
     *
     * @param plugin The plugin
     * @return The Collection of the HtmlPages
     */
    public Collection<HtmlPage> selectAll( Plugin plugin )
    {
        Collection<HtmlPage> htmlpageList = new ArrayList<HtmlPage>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            HtmlPage htmlpage = new HtmlPage(  );
            htmlpage.setId( daoUtil.getInt( 1 ) );
            htmlpage.setDescription( daoUtil.getString( 2 ) );
            htmlpage.setHtmlContent( daoUtil.getString( 3 ) );
            htmlpage.setStatus( daoUtil.getInt( 4 ) );
            htmlpage.setWorkgroup( daoUtil.getString( 5 ) );
            htmlpage.setRole( daoUtil.getString( 6 ) );
            htmlpageList.add( htmlpage );
        }

        daoUtil.free(  );

        return htmlpageList;
    }

    /**
     * Load enabled htmlpage
     * @param nHtmlPageId The page id
     * @param plugin The plugin
     * @return The Collection of the HtmlPages
     */
    public HtmlPage selectEnabledHtmlPage( int nHtmlPageId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ENABLED, plugin );
        daoUtil.setInt( 1, nHtmlPageId );
        daoUtil.executeQuery(  );

        HtmlPage htmlpage = null;

        if ( daoUtil.next(  ) )
        {
            htmlpage = new HtmlPage(  );
            htmlpage.setId( daoUtil.getInt( 1 ) );
            htmlpage.setDescription( daoUtil.getString( 2 ) );
            htmlpage.setHtmlContent( daoUtil.getString( 3 ) );
            htmlpage.setStatus( daoUtil.getInt( 4 ) );
            htmlpage.setWorkgroup( daoUtil.getString( 5 ) );
            htmlpage.setRole( daoUtil.getString( 6 ) );
        }

        daoUtil.free(  );

        return htmlpage;
    }

    /**
     * Load the list of htmlpages
     *
     * @param plugin The plugin
     * @return The Collection of the HtmlPages
     */
    public Collection<HtmlPage> selectEnabledHtmlPageList( Plugin plugin )
    {
        Collection<HtmlPage> htmlpageList = new ArrayList<HtmlPage>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ENABLED_HTMLPAGE_LIST, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            HtmlPage htmlpage = new HtmlPage(  );
            htmlpage.setId( daoUtil.getInt( 1 ) );
            htmlpage.setDescription( daoUtil.getString( 2 ) );
            htmlpage.setHtmlContent( daoUtil.getString( 3 ) );
            htmlpage.setStatus( daoUtil.getInt( 4 ) );
            htmlpage.setWorkgroup( daoUtil.getString( 5 ) );
            htmlpage.setRole( daoUtil.getString( 6 ) );
            htmlpageList.add( htmlpage );
        }

        daoUtil.free(  );

        return htmlpageList;
    }
}
