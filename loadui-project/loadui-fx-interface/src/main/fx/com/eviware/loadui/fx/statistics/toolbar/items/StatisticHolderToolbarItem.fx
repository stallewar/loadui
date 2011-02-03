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
package com.eviware.loadui.fx.statistics.toolbar.items;

import com.eviware.loadui.fx.statistics.toolbar.StatisticsToolbarItem;
import com.eviware.loadui.fx.FxUtils.*;

import com.eviware.loadui.api.events.BaseEvent;
import com.eviware.loadui.api.events.WeakEventHandler;
import com.eviware.loadui.api.statistics.StatisticHolder;
import com.eviware.loadui.fx.FxUtils;

public class StatisticHolderToolbarItem extends StatisticsToolbarItem {
	def labelListener = new LabelListener();
	
   public var statisticHolder:StatisticHolder on replace oldValue {
   	if( oldValue != null )
   		oldValue.removeEventListener( BaseEvent.class, labelListener );
   	statisticHolder.addEventListener( BaseEvent.class, labelListener );
      label = statisticHolder.getLabel();
      tooltip = "Adds {label} to a chart";
      icon = FxUtils.getImageFor( statisticHolder );
   }
}

class LabelListener extends WeakEventHandler {
	override function handleEvent(e):Void {
		def event:BaseEvent = e as BaseEvent;
		if( event.getKey().equals( StatisticHolder.LABEL ) )
		{
			FxUtils.runInFxThread( function():Void {
				label = statisticHolder.getLabel();
				tooltip = "Adds {label} to a chart";
			} );
		}
	} 
}