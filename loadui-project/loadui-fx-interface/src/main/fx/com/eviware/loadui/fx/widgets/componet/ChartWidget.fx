/* 
 * Copyright 2011 SmartBear Software
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
package com.eviware.loadui.fx.widgets.componet;

import javafx.ext.swing.SwingComponent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.CustomNode;
import javafx.scene.Node;

import com.eviware.loadui.fx.widgets.componet.ChartMouseListener;
import com.eviware.loadui.api.chart.CustomAbstractRange;
import com.eviware.loadui.api.chart.CustomTimeRange;
import com.eviware.loadui.api.chart.CustomNumericRange;
import com.eviware.loadui.api.chart.ChartModel;
import com.eviware.loadui.api.chart.Point;
import com.eviware.loadui.api.chart.ChartListener;
import com.eviware.loadui.api.chart.ChartSerie;

import com.jidesoft.chart.Chart;
import com.jidesoft.chart.annotation.AutoPositionedLabel;
import com.jidesoft.chart.axis.Axis;
import com.jidesoft.chart.axis.TimeAxis;
import com.jidesoft.chart.axis.NumericAxis;
import com.jidesoft.chart.model.ChartPoint;
import com.jidesoft.chart.model.DefaultChartModel;
import com.jidesoft.chart.style.ChartStyle;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.range.NumericRange;
import com.jidesoft.range.Range;
import com.jidesoft.range.TimeRange;
import com.jidesoft.range.AbstractRange;
import com.jidesoft.chart.Legend;

import com.jidesoft.chart.PointShape;
import com.jidesoft.chart.event.PointSelection;
import com.jidesoft.chart.model.*;
import com.jidesoft.plaf.LookAndFeelFactory;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.BasicStroke;
import java.awt.GradientPaint;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.BorderFactory;
import javax.swing.JButton;

import java.util.HashMap;
import java.util.Iterator;

import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.ext.swing.SwingComponent;

import com.eviware.loadui.fx.FxUtils.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.Group;
import javafx.util.Math;

import javafx.geometry.VPos;
import javafx.geometry.HPos;
import javafx.scene.layout.LayoutInfo;
import com.eviware.loadui.fx.FxUtils;
import com.jidesoft.chart.axis.AxisPlacement;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.lang.Duration;

import java.lang.System;
import org.slf4j.LoggerFactory;

def log = LoggerFactory.getLogger( "com.eviware.loadui.fx.widget.componet.ChartWidget" );

/**
 * @author predrag
 */
public class ChartWidget extends VBox, ChartListener {
    
    public-init var chartModel: ChartModel on replace oldModel {
    	oldModel.removeChartListener(this);
    	chartModel.addChartListener(this);
    }
    
    var chart: Chart;
    var xAxis: Axis;
    var yAxis: Axis;
    var y2Axis: Axis;
    var legend: Legend;
    var legendPanel: JPanel;
    var models: HashMap;
    var chartPanel: JPanel;
    
    var customXRange: CustomAbstractRange;
    var customYRange: CustomAbstractRange;
    var customY2Range: CustomAbstractRange;
    
    var seriesColors: Color[];
    
    var chartNode: Node;
    var legendNode: Node;
    
    var timeline: Timeline = Timeline {
	  repeatCount: Timeline.INDEFINITE
	  keyFrames: [
	     KeyFrame {
	         time: 1000ms
	         action: function() {
	         	var current = System.currentTimeMillis();
	         	var period: Long = (customXRange as CustomTimeRange).getPeriod();
					var rate: Long = (customXRange as CustomTimeRange).getRate();
			    	FxUtils.runInFxThread( function():Void {
			    		autoTimeAxis(xAxis, current - period + rate, period - rate);
    					adjustTimeAxisPoints(current - period, false);
						autoAxis();
			      })
	         }
	         canSkip: true
	     }
	  ]
    }
    
    init {
    	buildChartPanel();
    	buildLegendPanel();
    	chartNode = SwingComponent.wrap(chartPanel);
    	legendNode = SwingComponent.wrap(legendPanel);
    	def backgroundImg: ImageView = ImageView {
            layoutX: 0
            layoutY: 0
            image: Image {
                url: "{__ROOT__}images/png/chart-background.png"
            }
        }
    	var clearBtn: Button = Button {
			text: "Clear"
			action: function() {
				chartModel.clear(); 
			}
		}
    	content = [
	    	Group{
				content: [
					VBox{
		            	layoutX: 0
		                layoutY: 0
		                spacing: 2
		            	content: [
							backgroundImg,
				            HBox {
				    			spacing: 0
				    			hpos: HPos.RIGHT
				    			content: [
						    		clearBtn
								]
							}
						]
					}
		            VBox{
		            	layoutX: 0
		                layoutY: 0
		                spacing: 0
		                nodeHPos: HPos.CENTER
		            	content: bind [
				    		chartNode,
				    		legendNode
						]
					}
				]
			}
		];
    	spacing = 0;
    }
    
    postinit{
    	FxUtils.runInFxThread( function():Void {
			autoAxis();
		});
		
		if(chartModel.isTestRunning()){
			timeline.playFromStart();
		}
    }
    
    function buildChartPanel(): Void {
		customXRange = chartModel.getXRange();
	   customYRange = chartModel.getYRange();
	   
    	chart = new Chart();
		
		insert Color.orange into seriesColors;
		insert Color.yellow into seriesColors;
		insert Color.green into seriesColors;
		insert Color.red into seriesColors;
		insert Color.cyan into seriesColors;
		insert Color.magenta into seriesColors;
		insert Color.pink into seriesColors;
		insert Color.blue into seriesColors;
		insert Color.gray into seriesColors;
		insert new Color(150,150,150) into seriesColors;
		insert new Color(156,0,12) into seriesColors;
		insert new Color(156,12,156) into seriesColors;
		insert new Color(100,0,0) into seriesColors;
		insert new Color(0,0,150) into seriesColors;
		insert new Color(16,100,12) into seriesColors;
		insert new Color(255,255,255) into seriesColors;
		insert new Color(200,255,175) into seriesColors;
		insert new Color(255,136,0) into seriesColors;
		insert new Color(255,198,252) into seriesColors;
		insert new Color(186,201,255) into seriesColors;
		insert new Color(0,187,255) into seriesColors;
		
    	models = new HashMap();
		
    	var xRange: AbstractRange = convertRange(customXRange);
    	var yRange: AbstractRange = convertRange(customYRange);
    	
    	xAxis = createAxis(xRange);
    	xAxis.setVisible(customXRange.isVisible());
    	xAxis.setAxisColor(new Color(255, 255, 255));
    	
    	yAxis = createAxis(yRange);
    	yAxis.setVisible(customYRange.isVisible());
    	yAxis.setAxisColor(new Color(255, 255, 255));
    	
    	xAxis.setLabel(new AutoPositionedLabel(customXRange.getTitle(), new Color(0,255,0)));
      yAxis.setLabel(new AutoPositionedLabel(customYRange.getTitle(), new Color(0,255,0)));
    	
		def xPadding: Integer = 15;
		def yPadding: Integer = 15;
		
    	//this is to make padding for a chart
    	chartPanel = new JPanel();
    	chartPanel.setPreferredSize(new Dimension(chartModel.getWidth(), chartModel.getHeight()));
    	chartPanel.setLayout(new FlowLayout(FlowLayout.CENTER, xPadding, yPadding));//new GridLayout(1, 1, 55, 55));
    	chartPanel.setOpaque(false);
        
      var cl: ChartMouseListener = ChartMouseListener {
        	models: models
        	chartModel: chartModel
        	chart: chart
      }
        
    	chart.setXAxis(xAxis);
        chart.setYAxis(yAxis);
        customY2Range = chartModel.getY2Range();
        if (customY2Range != null) {
        	var y2Range: AbstractRange = convertRange(customY2Range);
            y2Axis = createAxis(y2Range);
            y2Axis.setVisible(customY2Range.isVisible());
            y2Axis.setAxisColor(new Color(255, 255, 255));
            y2Axis.setLabel(new AutoPositionedLabel(customY2Range.getTitle(), new Color(0,255,0)));
            y2Axis.setPlacement(AxisPlacement.TRAILING);
       		chart.addYAxis(y2Axis);
        }
        
        for(cs in chartModel.getSeries()){
			def model: DefaultChartModel = new DefaultChartModel(cs.getName());
    		models.put(cs.getName(), model);
			if(cs.isEnabled()){
			    if (cs.isDefaultAxis()) {
	    			chart.addModel(model);
			    } else {
	    			chart.addModel(model, y2Axis);
	    			
			    }
	    		chart.setStyle(model, createStyleForSerie(cs.getIndex(), chartModel.getStyle()));
			}
		}
      chart.setTitle(new AutoPositionedLabel(chartModel.getTitle(), Color.yellow.brighter()));
    	chart.setChartBackground(new Color(0, 0, 0, 0));
    	chart.setPanelBackground(new Color(0, 0, 0, 0));
    	chart.setOpaque(false);
    	chart.setHorizontalGridLinesVisible(false);
    	chart.setVerticalGridLinesVisible(false);
    	chart.setLabelColor(new Color(0, 255, 0));
    	chart.setPreferredSize(new Dimension(chartModel.getWidth() - 2 * xPadding, chartModel.getHeight() - 2 * yPadding));
    	chart.addMouseMotionListener(cl);
    	chartPanel.add(chart);
    }
    
    function buildLegendPanel(): Void {
    	legend = new Legend(chart, chartModel.getLegendColumns());
    	legend.setBorder(BorderFactory.createEmptyBorder());
    	legend.setOpaque(false);
    	legendPanel = new JPanel();
      legendPanel.setOpaque(false);
   	legendPanel.add(legend);
    }
    
    function createAxis(range: AbstractRange): Axis {
    	var axis: Axis;
    	if(range instanceof TimeRange){
    		axis = new TimeAxis(range as TimeRange);
    	}
    	else if(range instanceof NumericRange){
    		axis = new NumericAxis(range as NumericRange);
    	}
    	else{
    		axis = new Axis(range);
    	}
    	axis;
    }
    
    function convertRange(cutomRange: CustomAbstractRange): AbstractRange {
    	var range: AbstractRange;
    	if(cutomRange instanceof CustomNumericRange){
    		var low: Double = (cutomRange as CustomNumericRange).getLow();
    		var high: Double = (cutomRange as CustomNumericRange).getHigh();
    		var extraSpace: Double = (cutomRange as CustomNumericRange).getExtraSpace();
    		range = new NumericRange(low, high + extraSpace * (high - low) / 100);
    	}
    	else if(cutomRange instanceof CustomTimeRange){
    		var low: Long = (cutomRange as CustomTimeRange).getLow();
    		var high: Long = (cutomRange as CustomTimeRange).getHigh();
    		range = new TimeRange(low, high);
    	}
    	range;
    }
    
    function createStyleForSerie(index: Integer, style: Integer): ChartStyle {
    	var chartStyle: ChartStyle;
    	if(style == ChartModel.STYLE_BAR){
    		chartStyle = createBarStyle(seriesColors[index]);
    	}
    	else{
    		chartStyle = createLineStyle(seriesColors[index]);
    	}
    	chartStyle;
    }
    
    function createBarStyle(color: Color): ChartStyle {
        def style: ChartStyle = new ChartStyle(color, false, false);
        style.setBarsVisible(true);
        style.setBarColor(Color.green);
        style.setBarWidth(2);
        style.setLineStroke(new BasicStroke(3));
        style;
    }

    function createLineStyle(color: Color): ChartStyle {
        def style: ChartStyle = new ChartStyle(color);
        style.setLineWidth(2);
        style;
    }

    override function pointAddedToModel(cs: ChartSerie, p: Point): Void {
    	if(not cs.isEnabled()){
    		return;
    	}
    	
    	var scaleX: Double = 1.0;
    	if(customXRange instanceof CustomNumericRange){
    		scaleX = (customXRange as CustomNumericRange).getScale();
    	}
    	
    	var scaleY: Double = 1.0;
    	if(customYRange instanceof CustomNumericRange){
    		scaleY = (customYRange as CustomNumericRange).getScale();
    	}
    	
    	def x: Double = p.getX() * scaleX;
    	def y: Double = p.getY() * scaleY;
    	
    	FxUtils.runInFxThread( function():Void {
			(models.get(cs.getName()) as DefaultChartModel).addPoint(new ChartPoint(x, y));
      })
    }
    
    override function serieCleared(cs: ChartSerie): Void {
   		FxUtils.runInFxThread( function():Void { (models.get(cs.getName()) as DefaultChartModel).clearPoints() } );
    }
    
    override function chartCleared(): Void {

    }
    
    override function testStateChanged(running: Boolean): Void {
    	if(running){
    		timeline.playFromStart();
    	}
    	else{
    		timeline.stop();
    	}
    }
    
    override function serieEnabled(chartSerie: ChartSerie): Void {
    	FxUtils.runInFxThread( function():Void {
	    	var model: DefaultChartModel;
	    	
	    	try{
		    	if(chartSerie.isEnabled()){
		    		models.remove(chartSerie.getName());
		    		model = new DefaultChartModel(chartSerie.getName());
		    		models.put(chartSerie.getName(), model);
		    		
		    		if (chartSerie.isDefaultAxis())
		    			chart.addModel(model)
		    		else
		    			chart.addModel(model, y2Axis);
		    		chart.setStyle(model, createStyleForSerie(chartModel.getSerieIndex(chartSerie.getName()), chartModel.getStyle()));
		    	}
		    	else {
		    		model = models.get(chartSerie.getName()) as DefaultChartModel;
		    		chart.removeModel(model);
		    	}
		    	chartNode = SwingComponent.wrap(chartPanel);
		   	legendNode = SwingComponent.wrap(legendPanel);
		   	autoAxis();
			}
			catch( e ) {
				log.error( "Error enabling serie", e );
			}
		} );
    }
    
    function autoAxis(): Void {
    	if(yAxis.getRange() instanceof NumericRange){
    		var nr: CustomNumericRange = customYRange as CustomNumericRange;
    		var b: Bounds = findYBounds(true, nr.getLow(), nr.getHigh());
    		autoNumericAxis(yAxis, b.max, b.min, nr.getExtraSpace());
    	} 
		if(y2Axis.getRange() instanceof NumericRange){
    		var nr: CustomNumericRange = customY2Range as CustomNumericRange;
    		var b: Bounds = findYBounds(false, nr.getLow(), nr.getHigh());
    		autoNumericAxis(y2Axis, b.max, b.min, nr.getExtraSpace());
    	} 
    }
    
    function autoTimeAxis(axis: Axis, min: Double, period: Long): Void {
        def maxX: Long = (min + period) as Long;
        def axisRange: TimeRange = axis.getRange() as TimeRange;
        axisRange.setMin(min as Long);
        axisRange.setMax(maxX);
    }
    
    function autoNumericAxis(axis: Axis, max: Double, min: Double, extraSpace: Double): Void {
        def range: Double = max - min;
        def axisRange: NumericRange = axis.getRange() as NumericRange;
        axisRange.setMin(min as Long);
        axisRange.setMax(max + extraSpace * range / 100);
    }
    
    function findYBounds(default: Boolean, defaultMin: Double, defaultMax: Double): Bounds {
    	var max: Double = -Double.MAX_VALUE;
    	var min: Double = Double.MAX_VALUE;
		var model: DefaultChartModel;
		var pos: Double;
		var keys: Iterator = models.keySet().iterator();
		while(keys.hasNext()){
			var cs: ChartSerie = chartModel.getSerie(keys.next() as String);
			if(cs != null and cs.isEnabled() and cs.isDefaultAxis() == default){
				model = models.get(cs.getName()) as DefaultChartModel;
				if(model.getPointCount() > 0){
					pos = model.getYRange().maximum();
					if(pos > max){
						max = pos;
					}
					pos = model.getYRange().minimum();
					if(pos < min){
						min = pos;
					}
				}
			}
		}
		var bounds: Bounds;
		if(max == -Double.MAX_VALUE or min == Double.MAX_VALUE){
			bounds = Bounds {	max: Double.MAX_VALUE, min: -Double.MAX_VALUE }  
		}
		else{
			bounds = Bounds { max: Math.max(max, defaultMax), min: Math.min(min, defaultMin) }    
		}
		bounds;	
    }
    
    function adjustTimeAxisPoints(min: Long, y: Boolean){
    	var keys: Iterator = models.keySet().iterator();
    	var model: DefaultChartModel;
		while(keys.hasNext()){
			model = models.get(keys.next()) as DefaultChartModel; 
			if(y){
				while(model.getPointCount() > 0){
					if(min > model.getPoint(0).getY().position()){
						model.removePoint(0);	
					}
					else{
						break;
					}
				}
			}
			else{
				while(model.getPointCount() > 0){
					if(min > model.getPoint(0).getX().position()){
						model.removePoint(0);	
					}
					else{
						break;
					}
				}
			}
		}
    }
    
}


class Bounds {
	
	public var max: Double;
	
	public var min: Double;
	
}
















