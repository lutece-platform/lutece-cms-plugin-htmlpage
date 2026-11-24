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
package fr.paris.lutece.plugins.htmlpage.service;

import java.sql.Timestamp;

import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.util.AppLogService;
import jakarta.enterprise.inject.spi.CDI;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Daemon that keeps the search index in line with the publication period of the conditioned HTML pages: a page is indexed when its period
 * starts and removed from the index when it ends
 */
public class HtmlPagePublicationDaemon extends Daemon
{
    public static final String DAEMON_ID = "htmlPagePublicationDaemon";
    private static final String DATASTORE_KEY_LAST_RUN = "htmlpage.publicationDaemon.lastRun";

    /**
     * {@inheritDoc}
     */
    @Override
    public void run( )
    {
        long lNow = System.currentTimeMillis( );
        long lLastRun = NumberUtils.toLong( DatastoreService.getInstanceDataValue( DATASTORE_KEY_LAST_RUN, null ), lNow );

        int nActions = CDI.current( ).select( IHtmlPageService.class ).get( ).indexPublicationChanges( new Timestamp( lLastRun ), new Timestamp( lNow ) );
        DatastoreService.setInstanceDataValue( DATASTORE_KEY_LAST_RUN, Long.toString( lNow ) );

        String strLog = nActions + " indexer action(s) queued for the HTML pages whose publication period started or ended";
        AppLogService.debug( strLog );
        setLastRunLogs( strLog );
    }
}
