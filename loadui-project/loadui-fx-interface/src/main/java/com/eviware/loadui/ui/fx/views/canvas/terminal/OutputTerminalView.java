package com.eviware.loadui.ui.fx.views.canvas.terminal;

import javafx.event.EventHandler;
import javafx.fxml.FXML;

import com.eviware.loadui.api.terminal.InputTerminal;
import com.eviware.loadui.api.terminal.OutputTerminal;
import com.eviware.loadui.ui.fx.api.input.DraggableEvent;

public class OutputTerminalView extends TerminalView
{
	public OutputTerminalView( OutputTerminal terminal )
	{
		super( terminal );
	}

	@FXML
	@Override
	protected void initialize()
	{
		super.initialize();

		addEventHandler( DraggableEvent.ANY, new EventHandler<DraggableEvent>()
		{
			@Override
			public void handle( DraggableEvent event )
			{
				if( event.getEventType() == DraggableEvent.DRAGGABLE_ENTERED && event.getData() instanceof InputTerminal )
				{
					event.accept();
					event.consume();
				}
				else if( event.getEventType() == DraggableEvent.DRAGGABLE_DROPPED )
				{
					InputTerminal inputTerminal = ( InputTerminal )event.getData();
					getTerminal().connectTo( inputTerminal );
					event.consume();
				}
			}
		} );
	}

	@Override
	public OutputTerminal getTerminal()
	{
		return ( OutputTerminal )super.getTerminal();
	}
}