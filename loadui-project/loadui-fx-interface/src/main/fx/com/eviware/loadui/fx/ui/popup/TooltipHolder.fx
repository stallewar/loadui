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
/*
*TooltipHolder.fx
*
*Created on mar 11, 2010, 10:49:48 fm
*/

package com.eviware.loadui.fx.ui.popup;

import com.eviware.loadui.fx.AppState;
import com.eviware.loadui.fx.ui.node.BaseMixin;
import com.eviware.loadui.fx.ui.node.BaseNode;
import com.eviware.loadui.fx.ui.node.BaseNode.*;

import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;


/**
 * Mixin class for giving BaseNodes a tooltip which is displayed when the user hovers the mouse cursor over the node.
 *
 * @author dain.nilsson
 */
public mixin class TooltipHolder extends BaseMixin {
	/**
	 * The text to display for this nodes tooltip. If it is null or an empty String, no tooltip will be displayed.
	 */
	public var tooltip:String;
	
	var tooltipEnabled = true;
	
	var myScene:Scene;
	
	var tooltipLabel:Label;
	function getTooltipLabel():Label {
		if( tooltipLabel == null ) tooltipLabel = Label {
			tooltip: Tooltip { text: bind tooltip, font: Font.font("Amble", 10) }
			managed: false
		};
		
		tooltipLabel
	}
	
	/*def label:Label = Label {
		tooltip: Tooltip { text: bind tooltip, font: Font.font("Amble", 10) }
		managed: false
	};*/
	
	public function enableTooltip( enabled:Boolean ) {
		if ( not enabled ) {
			getTooltipLabel().tooltip.hide();
			delete getTooltipLabel() from AppState.byScene( myScene ).overlay.content;
		}
		tooltipEnabled = enabled;
	}
	
	init {
	    initalizeTooltipHandlers();
	}
	
	protected function initalizeTooltipHandlers(): Void {
		(this as BaseNode).addMouseHandler( MOUSE_ENTERED, function( e:MouseEvent ):Void {
			if( tooltip != null and tooltipEnabled ) {
				if( myScene == null )
					myScene = (this as BaseNode).scene;
				def bounds = (this as BaseNode).localToScene((this as BaseNode).boundsInLocal);
				def label = getTooltipLabel();
				label.layoutX = bounds.minX;
				label.layoutY = bounds.minY;
				label.width = bounds.width;
				label.height = bounds.height;
				insert label into AppState.byScene( myScene ).overlay.content;
				label.tooltip.activate();
			}
		} );
		(this as BaseNode).addMouseHandler( MOUSE_EXITED, function( e:MouseEvent ):Void {
			def label = getTooltipLabel();
			if( label.tooltip.activated ) {
				label.tooltip.deactivate();
				delete label from AppState.byScene( myScene ).overlay.content;
			}
		} );
	}
}
