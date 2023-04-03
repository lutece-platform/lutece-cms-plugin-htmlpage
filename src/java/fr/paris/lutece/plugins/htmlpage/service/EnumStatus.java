/*
 * Copyright (c) 2002-2023, City of Paris
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

import fr.paris.lutece.util.ReferenceList;

/**
 * 
 * EnumStatus
 *
 */
public enum EnumStatus
{
    disabled( 0, "#i18n{htmlpage.create_htmlpage.statusLabelDisabled}" ),
    enabled( 1, "#i18n{htmlpage.create_htmlpage.statusLabelEnabled}" ),
    conditioned( 2, "#i18n{htmlpage.create_htmlpage.statusLabelConditioned}" );

    private int    _nId;
    private String _strLabel;

    /**
     * Private constructor
     * @param nId
     * @param strLabel
     */
    private EnumStatus( int nId, String strLabel )
    {
        _nId = nId;
        _strLabel = strLabel;
    }

    /**
     * @return the _nId
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * @return the _strLabel
     */
    public String getLabel( )
    {
        return _strLabel;
    }
    
    /**
     * ReferenceList of EnumStatus
     * @return referenceList of EnumStatus
     */
    public static ReferenceList getReferenceList ( )
    {
        ReferenceList refList = new ReferenceList( );
        for ( EnumStatus status : EnumStatus.values( ) )
        {
            refList.addItem( status.getId( ), status.getLabel( ) );
        }
        return refList;
    }
}
