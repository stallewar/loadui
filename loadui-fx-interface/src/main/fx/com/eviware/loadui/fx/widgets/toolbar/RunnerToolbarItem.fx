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
/*
*RunnerItem.fx
*
*Created on mar 11, 2010, 15:37:53 em
*/

package com.eviware.loadui.fx.widgets.toolbar;

import com.eviware.loadui.fx.ui.toolbar.ToolbarItem;
import com.eviware.loadui.fx.FxUtils.*;

import javafx.scene.image.Image;

def iconImage = Image { url: "{__ROOT__}images/png/runner-icon.png" };

public class RunnerToolbarItem extends ToolbarItem {
	override var icon = iconImage;
	
	override var tooltip = "Creates a new Agent in the Workspace";
	
	override var label = "New Agent";
	
	override var category = "agents";
}
