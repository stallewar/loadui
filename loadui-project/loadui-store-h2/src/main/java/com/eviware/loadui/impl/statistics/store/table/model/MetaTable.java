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
package com.eviware.loadui.impl.statistics.store.table.model;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.eviware.loadui.impl.statistics.store.table.ConnectionProvider;
import com.eviware.loadui.impl.statistics.store.table.MetadataProvider;
import com.eviware.loadui.impl.statistics.store.table.TableBase;
import com.eviware.loadui.impl.statistics.store.table.TableDescriptor;
import com.eviware.loadui.impl.statistics.store.table.TableProvider;

public class MetaTable extends TableBase
{
	public static final String SELECT_ARG_TRACKNAME_EQ = "track_eq";

	public static final String METATABLE_NAME = "meta_table";

	public static final String STATIC_FIELD_TRACK_NAME = "__TRACK_ID";

	public MetaTable( String dbName, ConnectionProvider connectionProvider, MetadataProvider metadataProvider,
			TableProvider tableProvider )
	{
		super( dbName, METATABLE_NAME, null, connectionProvider, metadataProvider, tableProvider );
	}

	@Override
	public synchronized void insert( Map<String, ? extends Object> data ) throws SQLException
	{
		Map<String, Object> queryData = new HashMap<String, Object>();
		queryData.put( SELECT_ARG_TRACKNAME_EQ, data.get( STATIC_FIELD_TRACK_NAME ) );
		if( select( queryData ).size() == 0 )
		{
			super.insert( data );
			// TODO commit for now, transaction management needs to be implemented
			commit();
		}
	}

	@Override
	protected void initializeDescriptor( TableDescriptor descriptor )
	{
		descriptor.addStaticField( STATIC_FIELD_TRACK_NAME, String.class );
		descriptor.addToPkSequence( STATIC_FIELD_TRACK_NAME );
		descriptor.addSelectCriteria( SELECT_ARG_TRACKNAME_EQ, STATIC_FIELD_TRACK_NAME, "=?" );
	}
}