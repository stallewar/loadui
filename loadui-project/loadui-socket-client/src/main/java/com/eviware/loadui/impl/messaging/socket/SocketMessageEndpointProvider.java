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
package com.eviware.loadui.impl.messaging.socket;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.ssl.KeyMaterial;
import org.apache.commons.ssl.SSLClient;
import org.apache.commons.ssl.TrustMaterial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eviware.loadui.LoadUI;
import com.eviware.loadui.api.messaging.MessageEndpoint;
import com.eviware.loadui.api.messaging.MessageEndpointProvider;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class SocketMessageEndpointProvider implements MessageEndpointProvider
{
	public static final Logger log = LoggerFactory.getLogger( SocketMessageEndpointProvider.class );

	private static final Pattern urlPattern = Pattern.compile( "https?://([^/:]+)(:(\\d+))?(/.*)?" );

	private final SSLClient client;

	public SocketMessageEndpointProvider() throws IOException, GeneralSecurityException
	{
		client = new SSLClient();

		client.addTrustMaterial( new TrustMaterial( System.getProperty( LoadUI.TRUST_STORE ), System.getProperty(
				LoadUI.TRUST_STORE_PASSWORD ).toCharArray() ) );

		client.setCheckHostname( false ); // default setting is "true" for SSLClient
		client.setCheckCRL( false ); // default setting is "true" for SSLClient

		client.setKeyMaterial( new KeyMaterial( System.getProperty( LoadUI.KEY_STORE ), System.getProperty(
				LoadUI.KEY_STORE_PASSWORD ).toCharArray() ) );
	}

	@Override
	public MessageEndpoint createEndpoint( String url )
	{
		Matcher urlMatcher = urlPattern.matcher( url );
		Preconditions.checkArgument( urlMatcher.find(), "Invalid url format: %s", url );

		String host = urlMatcher.group( 1 );
		int port = Integer.parseInt( Objects.firstNonNull( urlMatcher.group( 3 ), "8443" ) );

		return new ClientSocketMessageEndpoint( client, host, port );
	}
}
