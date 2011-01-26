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
package com.eviware.loadui.impl.statistics;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eviware.loadui.api.addressable.AddressableRegistry;
import com.eviware.loadui.api.messaging.BroadcastMessageEndpoint;
import com.eviware.loadui.api.messaging.MessageEndpoint;
import com.eviware.loadui.api.messaging.MessageListener;
import com.eviware.loadui.api.model.AgentItem;
import com.eviware.loadui.api.statistics.Statistic;
import com.eviware.loadui.api.statistics.store.ExecutionManager;
import com.eviware.loadui.util.statistics.store.EntryImpl;

/**
 * Receives Track data from agents and saves it to the current Execution.
 * 
 * @author dain.nilsson
 */
public class TrackStreamReceiver
{
	private final static Logger log = LoggerFactory.getLogger( TrackStreamReceiver.class );

	private final ExecutionManager manager;
	private final MessageEndpoint endpoint;
	private final AddressableRegistry addressableRegistry;

	public TrackStreamReceiver( ExecutionManager executionManager, BroadcastMessageEndpoint endpoint,
			AddressableRegistry addressableRegistry )
	{
		this.manager = executionManager;
		this.endpoint = endpoint;
		this.addressableRegistry = addressableRegistry;

		this.endpoint.addMessageListener( "/" + Statistic.class.getName(), new MessageListener()
		{
			@Override
			@SuppressWarnings( "unchecked" )
			public void handleMessage( String channel, MessageEndpoint endpoint, Object data )
			{
				if( endpoint instanceof AgentItem )
				{
					Map<String, Object> map = ( Map<String, Object> )data;
					AgentItem agent = ( AgentItem )endpoint;
					int level = ( ( Number )map.remove( "_LEVEL" ) ).intValue();
					int timestamp = ( ( Number )map.remove( "_TIMESTAMP" ) ).intValue();
					String trackId = ( String )map.remove( "_TRACK_ID" );

					manager.writeEntry( trackId, new EntryImpl( timestamp, ( Map<String, Number> )data ), agent.getLabel(),
							level );

					log.debug( "To aggregate track: {}, use: {}", trackId,
							TrackStreamReceiver.this.addressableRegistry.lookup( trackId ) );
				}
			}
		} );
	}
}