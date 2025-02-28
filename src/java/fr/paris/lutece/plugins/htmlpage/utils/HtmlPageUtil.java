/*
 * Copyright (c) 2002-2022, City of Paris
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
package fr.paris.lutece.plugins.htmlpage.utils;

import fr.paris.lutece.plugins.htmlpage.business.HtmlPage;
import fr.paris.lutece.plugins.htmlpage.service.EnumStatus;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.date.DateUtil;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;

import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author lenaini
 */
public class HtmlPageUtil
{
    // Constants
    private static final String EMPTY_STRING = "";

    /**
     * Checks if the page is visible for the current user
     * 
     * @param request
     *            The HTTP request
     * @return true if the page could be shown to the user
     */
    public static boolean isVisible( HttpServletRequest request, String strRole )
    {
        if ( !isRoleExist( strRole ) )
        {
            return true;
        }

        if ( SecurityService.isAuthenticationEnable( ) )
        {
            return SecurityService.getInstance( ).isUserInRole( request, strRole );
        }

        return true;
    }
    
    /**
     * Checks if the htmlpage could be shown
     * @param htmlPage
     * @return true if the htmlpage could be shown
     */
    public static boolean isActivedPageHtml( HtmlPage htmlPage )
    {
        Timestamp date = Timestamp.from( Instant.now( ) );

        if ( htmlPage.getStatus( ) == EnumStatus.conditioned.getId( ) )
        {
            if ( htmlPage.getDateStart( ) != null && htmlPage.getDateEnd( ) != null )
            {
                return htmlPage.getDateStart( ).before( date ) && htmlPage.getDateEnd( ).after( date );
            }

            if ( htmlPage.getDateStart( ) != null && htmlPage.getDateEnd( ) == null )
            {
                return htmlPage.getDateStart( ).before( date );
            }
        }

        return htmlPage.getStatus( ) == EnumStatus.enabled.getId( );
    }
    
    /**
     * Checks if htmlpage have role
     * @param strRole
     * @return true if htmlpage have role
     */
    public static boolean isRoleExist( String strRole )
    {
        return strRole != null && !strRole.trim( ).equals( EMPTY_STRING ) && !strRole.equals( HtmlPage.ROLE_NONE );
    }
    
    /**
     * Convert to sql date
     * @param strDate
     * @return return sql date
     */
    public static Timestamp convertToTimestamp( String strDate )
    {
        SimpleDateFormat formatter = new SimpleDateFormat( DateUtil.ISO_PATTERN_DATE );
        try
        {
            return new Timestamp( formatter.parse ( strDate ).getTime( ) );
            
        } catch ( ParseException e )
        {
            AppLogService.error( "Erreur lors de la tentative de parse de la date {}", strDate, e.getMessage( ) );
        }
        return null;
    }
}
