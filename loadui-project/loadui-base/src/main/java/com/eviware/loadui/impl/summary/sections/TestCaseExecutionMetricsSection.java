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
package com.eviware.loadui.impl.summary.sections;

import java.util.Map;

import javax.swing.table.TableModel;

import com.eviware.loadui.api.component.categories.RunnerCategory;
import com.eviware.loadui.api.model.CanvasItem;
import com.eviware.loadui.api.model.ComponentItem;
import com.eviware.loadui.api.model.SceneItem;
import com.eviware.loadui.impl.summary.MutableSectionImpl;
import com.eviware.loadui.impl.summary.sections.tablemodels.TestCaseSamplerStatisticsTable;

public class TestCaseExecutionMetricsSection extends MutableSectionImpl
{
	SceneItem testcase;

	public TestCaseExecutionMetricsSection( SceneItem sceneItem )
	{
		super( "Execution Metrics" );
		testcase = sceneItem;
		addValue( "Assertion Failure Ratio", getFailedAssertions() );
		addValue( "Request Failure Ratio", getFailedRequests() );
		addTable( "Runners", getRunnersMetrics() );
	}

	private final String getFailedAssertions()
	{
		long failed = testcase.getCounter( CanvasItem.ASSERTION_FAILURE_COUNTER ).get();
		long total = testcase.getCounter( CanvasItem.ASSERTION_COUNTER ).get();
		int perc = ( int )( total > 0 ? failed * 100 / total : 0 );
		return perc + "%";
	}

	private final String getFailedRequests()
	{
		long failed = testcase.getCounter( CanvasItem.REQUEST_FAILURE_COUNTER ).get();
		long total = testcase.getCounter( CanvasItem.REQUEST_COUNTER ).get();
		int perc = ( int )( total > 0 ? failed * 100 / total : 0 );
		return perc + "%";
	}

	private final TableModel getRunnersMetrics()
	{
		TestCaseSamplerStatisticsTable table = new TestCaseSamplerStatisticsTable();
		for( ComponentItem component : testcase.getComponents() )
			// if( component.getType().equals("HttpSampler")) {
			if( component.getBehavior() instanceof RunnerCategory )
			{
				Map<String, String> stats = ( ( RunnerCategory )component.getBehavior() ).getStatistics();
				table.add( new TestCaseSamplerStatisticsTable.TestCaseSamplerStatisticsModel( component.getLabel(), stats ) );
			}
		return table;
	}
}
