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
package com.eviware.loadui.api.charting.line;

import java.awt.Color;

import com.eviware.loadui.api.statistics.model.chart.line.Segment;

public interface SegmentModel
{
	public static final String COLOR = "color";
	public static final String STROKE = "stroke";
	public static final String WIDTH = "width";

	public Segment getSegment();

	public Color getColor();

	public void setColor( Color color );

	public StrokeStyle getStrokeStyle();

	public int getStrokeWidth();

	public interface MutableStrokeWidth extends SegmentModel
	{
		public void setStrokeWidth( int strokeWidth );
	}

	public interface MutableStrokeStyle extends SegmentModel
	{
		public void setStrokeStyle( StrokeStyle strokeStyle );
	}
}
