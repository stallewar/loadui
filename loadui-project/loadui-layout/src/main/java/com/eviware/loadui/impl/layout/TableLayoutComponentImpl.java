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
package com.eviware.loadui.impl.layout;

import java.util.Map;

import javax.swing.table.TableModel;

import com.eviware.loadui.api.layout.TableLayoutComponent;
import com.eviware.loadui.util.MapUtils;
import com.google.common.collect.ImmutableMap;

public class TableLayoutComponentImpl extends LayoutComponentImpl implements TableLayoutComponent
{
	public final static String TABLE_MODEL = "tableModel";
	public final static String LABEL = "label";

	public TableLayoutComponentImpl( Map<String, ?> args )
	{
		super( args );

		if( !( properties.get( TABLE_MODEL ) instanceof TableModel ) )
			throw new IllegalArgumentException( "Illegal arguments: " + args );
	}

	public TableLayoutComponentImpl( TableModel tableModel, String label, String constraints )
	{
		this( ImmutableMap.of( TABLE_MODEL, tableModel, LABEL, label, CONSTRAINTS, constraints ) );
	}

	@Override
	public TableModel getTableModel()
	{
		return ( TableModel )properties.get( TABLE_MODEL );
	}

	@Override
	public String getLabel()
	{
		return MapUtils.getOr( properties, LABEL, "" );
	}
}
