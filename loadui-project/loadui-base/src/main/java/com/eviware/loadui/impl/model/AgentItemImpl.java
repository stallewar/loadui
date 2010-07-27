/*
 * Copyright 2010 eviware software ab
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the Licence for the specific language governing permissions and limitations
 * under the Licence.
 */
package com.eviware.loadui.impl.model;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;

import com.eviware.loadui.api.messaging.BroadcastMessageEndpoint;
import com.eviware.loadui.api.messaging.ConnectionListener;
import com.eviware.loadui.api.messaging.MessageEndpoint;
import com.eviware.loadui.api.messaging.MessageEndpointProvider;
import com.eviware.loadui.api.messaging.MessageListener;
import com.eviware.loadui.api.model.AgentItem;
import com.eviware.loadui.api.model.WorkspaceItem;
import com.eviware.loadui.config.AgentItemConfig;
import com.eviware.loadui.util.BeanInjector;
import com.eviware.loadui.util.messaging.MessageEndpointSupport;

public class AgentItemImpl extends ModelItemImpl<AgentItemConfig> implements AgentItem
{
	private final WorkspaceItem workspace;
	private final MessageEndpointProvider provider;
	private final BroadcastMessageEndpoint broadcastEndpoint;
	private MessageEndpointSupport endpointSupport;
	private boolean connected = false;

	public AgentItemImpl( WorkspaceItem workspace, AgentItemConfig config )
	{
		super( config );
		this.workspace = workspace;
		broadcastEndpoint = BeanInjector.getBean( BroadcastMessageEndpoint.class );
		provider = BeanInjector.getBean( MessageEndpointProvider.class );
		createProperty( MAX_THREADS_PROPERTY, Long.class, 1000 );
		setupClient();

		if( isEnabled() )
		{
			try
			{
				open();
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
	}

	private void setupClient()
	{
		if( endpointSupport != null )
			endpointSupport.close();

		endpointSupport = new MessageEndpointSupport( this, provider.createEndpoint( getConfig().getUrl() + "cometd" ) );
		addConnectionListener( new ConnectionListener()
		{
			@Override
			public void handleConnectionChange( MessageEndpoint endpoint, boolean connected )
			{
				if( connected )
					broadcastEndpoint.registerEndpoint( AgentItemImpl.this );
				else
					broadcastEndpoint.deregisterEndpoint( AgentItemImpl.this );
				AgentItemImpl.this.connected = connected;
				String hostName;
				try
				{
					hostName = InetAddress.getLocalHost().getHostName();
				}
				catch( UnknownHostException e )
				{
					hostName = "Unknown Host";
				}
				sendMessage( AgentItem.AGENT_CHANNEL, Collections.singletonMap( AgentItem.CONNECTED, hostName ) );
				log
						.debug( "Agent connected, setting max threads: {}", getProperty( MAX_THREADS_PROPERTY )
								.getStringValue() );
				sendMessage( AgentItem.AGENT_CHANNEL, Collections.singletonMap( AgentItem.SET_MAX_THREADS, getProperty(
						MAX_THREADS_PROPERTY ).getStringValue() ) );

				fireBaseEvent( READY );
			}
		} );
	}

	@Override
	public String getUrl()
	{
		return getConfig().getUrl();
	}

	@Override
	public WorkspaceItem getWorkspace()
	{
		return workspace;
	}

	@Override
	public boolean isEnabled()
	{
		return getConfig().getEnabled();
	}

	@Override
	public boolean isReady()
	{
		return connected;
	}

	@Override
	public void setEnabled( boolean enabled )
	{
		if( enabled != isEnabled() )
		{
			getConfig().setEnabled( enabled );
			fireBaseEvent( ENABLED );
			try
			{
				if( enabled )
					open();
				else
					close();
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setUrl( String url )
	{
		if( !getConfig().equals( url ) )
		{
			getConfig().setUrl( url );
			setupClient();
		}
	}

	@Override
	public void addMessageListener( String channel, MessageListener listener )
	{
		endpointSupport.addMessageListener( channel, listener );
	}

	@Override
	public void removeMessageListener( MessageListener listener )
	{
		endpointSupport.removeMessageListener( listener );
	}

	@Override
	public void sendMessage( String channel, Object data )
	{
		// if( !isReady() )
		// throw new RuntimeException(
		// "Message can't be sent unless Agent is ready." );
		if( isReady() )
			endpointSupport.sendMessage( channel, data );
	}

	@Override
	public void addConnectionListener( ConnectionListener listener )
	{
		endpointSupport.addConnectionListener( listener );
	}

	@Override
	public void open()
	{
		endpointSupport.open();
	}

	@Override
	public void close()
	{
		endpointSupport.close();
	}

	@Override
	public void removeConnectionListener( ConnectionListener listener )
	{
		endpointSupport.removeConnectionListener( listener );
	}
}
