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
package com.eviware.loadui.fx.statistics.chart.line;

import javafx.util.Math;
import javafx.scene.paint.Color;

import com.eviware.loadui.fx.FxUtils;

import com.eviware.loadui.api.statistics.Statistic;
import com.eviware.loadui.api.statistics.DataPoint;
import com.eviware.loadui.api.statistics.model.chart.LineChartView.LineSegment;

import com.jidesoft.chart.model.DefaultChartModel;
import com.jidesoft.chart.style.ChartStyle;

def SCALE = "scale";
def COLOR = "color";
def STROKE = "stroke";
def WIDTH = "width";

public class LineSegmentChartModel extends DefaultChartModel {
	public-read def chartStyle = new ChartStyle();
	
	public-read var latestTime:Number;
	
	public-init var segment:LineSegment on replace {
		statistic = segment.getStatistic();
		latestTime = statistic.getTimestamp();
		loadStyles();
		initialized = true;
	}
	
	var statistic:Statistic;
	def scaler = new ScaledPointScale();
	var initialized = false;
	
	public var level:Integer = 0 on replace {
		clearPoints();
		for( dataPoint in statistic.getPeriod( xRange[0], xRange[1], level ) ) {
			addPoint( scaler.createPoint( (dataPoint as DataPoint).getTimestamp(), (dataPoint as DataPoint).getValue() as Number ), false );
		}
		update();
	}
	
	public var xRange:Number[] = [ 0, 0 ] on replace oldXRange {
		clearPoints();
		for( dataPoint in statistic.getPeriod( xRange[0], xRange[1], level ) ) {
			addPoint( scaler.createPoint( (dataPoint as DataPoint).getTimestamp(), (dataPoint as DataPoint).getValue() as Number ), false );
		}
		update();
	}
	
	public var scale:Integer on replace {
		scaler.setScale( Math.pow( 10, scale ) );
		fireModelChanged();
		if( initialized ) {
			segment.setAttribute( SCALE, "{scale}" );
		}
	}
	
	public var color:Color on replace {
		chartStyle.setLineColor( FxUtils.getAwtColor( color ) );
		if( initialized ) {
			segment.setAttribute( COLOR, FxUtils.colorToWebString(color) );
		}
	}
	
	public var stroke:String on replace {
		if( initialized ) {
			segment.setAttribute( STROKE, stroke );
			updateStroke();
		}
	}
	
	public var width:Integer on replace {
		if( initialized ) {
			segment.setAttribute( WIDTH, "{width}" );
			updateStroke();
		}
	}
	
	public function refresh():Void {
		def dataPoint = statistic.getLatestPoint( level );
		if( dataPoint != null ) {
			def timestamp = dataPoint.getTimestamp();
			if( timestamp != latestTime and timestamp >= 0 ) {
				latestTime = timestamp;
				if( timestamp <= xRange[1] )
					addPoint( scaler.createPoint( timestamp, dataPoint.getValue() as Number ) );
			}
		}
	}
	
	function loadStyles() {
		try {
			scale = Integer.parseInt( segment.getAttribute( SCALE, "0" ) );
		} catch(e) {
			scale = 0;
		}
		
		def colorStr = segment.getAttribute( COLOR, "#ff0000" );
		chartStyle.setLineColor( FxUtils.getAwtColor( colorStr ) );
		color = Color.web( colorStr );
		
		try {
			width = Integer.parseInt( segment.getAttribute( WIDTH, "1" ) );
		} catch(e) {
			width = 1;
		}
		
		stroke = segment.getAttribute( STROKE, "solid" );
		
		updateStroke();
	}
	
	function updateStroke():Void {
		def newStroke = if( stroke == "dashed" ) {
			LineChartStyles.dashedStroke
		} else if( stroke == "dotted" ) {
			LineChartStyles.dottedStroke
		} else {
			LineChartStyles.solidStroke
		}
		
		chartStyle.setLineStroke( LineChartStyles.getStroke( width, newStroke ) );
		fireModelChanged();
	}
}