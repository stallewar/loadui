/* 
 * Copyright 2011 eviware software ab
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
package com.eviware.loadui.fx.ui.pagelist;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.LayoutInfo;
import javafx.scene.control.Separator;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.util.Math;

import com.sun.javafx.scene.layout.Region;

import com.eviware.loadui.fx.ui.pagination.Pagination;

public class PageList extends VBox, Pagination {
	public var label:String = "PageList";
	
	override var styleClass = "page-list";
	
	override var padding = Insets { left: 20, top: 30, right: 20, bottom: 5 };
	
	var itemWidth: Number;
	
	def displayed = bind displayedItems on replace {
		labelBox.content = for( x in displayedItems ) {
			Label {
				styleClass: "item-label"
				text: "{x}"
				textWrap: true
				vpos: VPos.TOP
				layoutInfo: LayoutInfo {
					width: bind x.layoutBounds.width
					height: 45
				}
			}
		}
		itemBox.content = displayedItems;
		doLayout();
	}
	
	var itemBox = HBox { spacing: 20 };
	var labelBox = HBox {
		spacing: 20
		padding: Insets { left: 60, right: 58 }
	}
	
	init {
		content = [
			Region { styleClass: "page-list", width: bind width, height: bind height, managed: false },
			HBox {
				content: [
					Label { text: bind label.toUpperCase() },
					Label { layoutInfo: LayoutInfo { hfill: true, hgrow: Priority.ALWAYS } },
					Label { text: bind "Page {page+1} of {numPages}", styleClass: "page-label" }
				]
			},
			HBox {
				nodeVPos: VPos.BOTTOM
				spacing: 20
				padding: Insets { top: 10, right: 10, left: 10 }
				content: [
					Button {
						styleClass: "left-button"
						layoutInfo: LayoutInfo { vpos: VPos.CENTER }
						disable: bind page <= 0
						action: function():Void {
							if( page > 0 ) page--;
						}
					},
					itemBox,
					Button {
						styleClass: "right-button"
						layoutInfo: LayoutInfo { vpos: VPos.CENTER }
						disable: bind page >= (numPages-1)
						action: function():Void {
							if( page < (numPages-1) ) page++;
						}
					}
				]
			},
			Separator {},
			labelBox
		];
	}
	
	override function doLayout():Void {
		super.doLayout();
		
		for( x in displayedItems ) {
			itemWidth = Math.max( itemWidth, x.layoutBounds.width );
		}
		
		itemsPerPage = (width - 136)/(itemWidth + itemBox.spacing) as Integer;
	}
}
