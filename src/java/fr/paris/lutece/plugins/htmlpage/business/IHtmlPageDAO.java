/*
 * Copyright (c) 2002-2009, Mairie de Paris
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

import java.util.Collection;


/**
 * htmlPageInterface
 * @author lenaini
 */
public interface IHtmlPageDAO
{
    /**
     * Delete a record from the table
     * @param htmlpage The HtmlPage object
     * @param plugin The plugin
     */
    void delete( HtmlPage htmlpage, Plugin plugin );

    /**
     * Insert a new record in the table.
     * @param htmlpage The htmlpage object
     * @param plugin The plugin
     */
    void insert( HtmlPage htmlpage, Plugin plugin );

    /**
     * Load the data of HtmlPage from the table
     * @param nHtmlPageId The identifier of HtmlPage
     * @param plugin The plugin
     * @return the instance of the HtmlPage
     */
    HtmlPage load( int nHtmlPageId, Plugin plugin );

    /**
     * Load the data of enabled HtmlPage from the table
     * @param nHtmlPageId The identifier of HtmlPage
     * @param plugin The plugin
     * @return the instance of the HtmlPage
     */
    HtmlPage selectEnabledHtmlPage( int nHtmlPageId, Plugin plugin );

    /**
     * Load the list of htmlpages
     * @param plugin The plugin
     * @return The Collection of the HtmlPages
     */
    Collection<HtmlPage> selectAll( Plugin plugin );

    /**
     * Load the list of htmlpages with valid status
     * @param plugin The plugin
     * @return The Collection of the HtmlPages
     */
    Collection<HtmlPage> selectEnabledHtmlPageList( Plugin plugin );

    /**
     * Update the record in the table
     * @param htmlpage The reference of htmlpage
     * @param plugin The plugin
     */
    void store( HtmlPage htmlpage, Plugin plugin );
}
