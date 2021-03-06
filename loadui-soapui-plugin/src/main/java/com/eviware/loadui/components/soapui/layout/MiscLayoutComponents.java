/*
 * Copyright 2013 SmartBear Software
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the Licence for the specific language governing permissions and limitations
 * under the Licence.
 */
package com.eviware.loadui.components.soapui.layout;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eviware.loadui.impl.layout.ActionLayoutComponentImpl;
import com.eviware.loadui.util.StringUtils;
import com.eviware.loadui.util.soapui.CajoClient;
import com.eviware.soapui.support.UISupport;
import com.google.common.collect.ImmutableMap;

public class MiscLayoutComponents
{
	private static final Logger log = LoggerFactory.getLogger( MiscLayoutComponents.class );

	public static ActionLayoutComponentImpl buildOpenInSoapUiButton( final String projectFileName,
			final String testSuiteName, final String testCaseName )
	{
		return new ActionLayoutComponentImpl( ImmutableMap.<String, Object> builder() //
				.put( ActionLayoutComponentImpl.LABEL, "Open in soapUI" ) //
				.put( ActionLayoutComponentImpl.ASYNC, false ) //
				.put( ActionLayoutComponentImpl.ACTION, new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							if( StringUtils.isNullOrEmpty( CajoClient.getInstance().getPathToSoapUIBat() ) )
							{
								log.warn( "No path to soapUI has been set!" );
								boolean confirm = UISupport
										.confirm(
												"To open the TestCase in soapUI you must provide the path to the soapUI executable\nWould you like to do that now?.",
												"Enter path to soapUI" );
								if( !confirm )
									return;
								File soapUIPath = UISupport.getFileDialogs().open( null, "Select soapUI executable", null,
										null, null );
								if( soapUIPath == null )
								{
									log.error( "Path to soapUI not provided." );
									return;
								}
								CajoClient.getInstance().setPathToSoapUIBat( soapUIPath.getAbsolutePath() );
							}
							if( !CajoClient.getInstance().testConnection() )
							{
								CajoClient.getInstance().startSoapUI();
							}
							if( CajoClient.getInstance().testConnection() )
							{
								CajoClient.getInstance().invoke( "openTestCase",
										new String[] { projectFileName, testSuiteName, testCaseName } );
							}
						}
						catch( Exception e )
						{
							e.printStackTrace();
						}
					}
				} ).build() );
	}
}
