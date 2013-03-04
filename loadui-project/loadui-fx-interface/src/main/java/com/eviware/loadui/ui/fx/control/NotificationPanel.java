package com.eviware.loadui.ui.fx.control;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eviware.loadui.api.testevents.MessageLevel;
import com.eviware.loadui.api.testevents.TestEvent.Entry;
import com.eviware.loadui.api.testevents.TestEventManager.TestEventObserver;
import com.eviware.loadui.ui.fx.util.Animations;
import com.eviware.loadui.ui.fx.util.Animations.State;
import com.eviware.loadui.ui.fx.util.FXMLUtils;
import com.eviware.loadui.util.testevents.MessageTestEvent;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class NotificationPanel extends VBox implements TestEventObserver, EventHandler<MouseEvent>
{

	private static final Logger log = LoggerFactory.getLogger( NotificationPanel.class );
	private static final DateFormat dateFormat = new SimpleDateFormat( "EEE MMM dd HH:mm:ss", Locale.ENGLISH );
	private static final List<NotificationPanel> allPanels = new ArrayList<>();

	private final DetachedTabsHolder tabsHolder;
	private final Timer delayer = new Timer( "NotificationsPanel-Timer", true );
	private final Animations anime = new Animations( this, false );
	private TimerTask fadeAwayTask;

	@FXML
	private Label dateText;

	@FXML
	private Label msgCount;

	@FXML
	private Label msgText;

	private Pane mainView;

	private final Rectangle wholeWindowRec = new Rectangle( 5000, 5000, Color.TRANSPARENT );

	private final Runnable resizePanel = new Runnable()
	{

		@Override
		public void run()
		{
			double totalHeight = 0;
			for( Node child : getChildren() )
				totalHeight += ( ( Pane )child ).getHeight();
			setMaxHeight( totalHeight );
		}

	};

	private EventHandler<MouseEvent> showLogHandler = new EventHandler<MouseEvent>()
	{
		@Override
		public void handle( MouseEvent _ )
		{
			// no default action
		}
	};

	public NotificationPanel()
	{
		this( DetachedTabsHolder.get() );
	}

	public NotificationPanel( DetachedTabsHolder tabsHolder )
	{
		this.tabsHolder = tabsHolder;
		FXMLUtils.load( this );
		allPanels.add( this );
	}

	@FXML
	private void initialize()
	{
		log.debug( "Initializing" );
		setVisible( false );
		wholeWindowRec.addEventHandler( MouseEvent.MOUSE_MOVED, this );

		setOnMouseEntered( mouseEnteredHandler() );
		setOnMouseExited( mouseExitedHandler() );

	}

	@FXML
	private void showSystemLog( MouseEvent event )
	{
		showLogHandler.handle( event );
	}

	@FXML
	private void hideNotifications( MouseEvent event )
	{
		for( NotificationPanel panel : allPanels )
			panel.anime.slideUp();
	}

	public void setMainWindowView( Pane mainView )
	{
		this.mainView = mainView;
	}

	public void setOnShowLog( EventHandler<MouseEvent> handler )
	{
		showLogHandler = handler;
	}

	public Label getMsgText()
	{
		return msgText;
	}

	@Override
	public void onTestEvent( Entry entry )
	{
		if( entry.getTestEvent() instanceof MessageTestEvent )
		{
			final MessageTestEvent te = ( MessageTestEvent )entry.getTestEvent();
			if( te.getLevel() != MessageLevel.NOTIFICATION )
			{
				Platform.runLater( new Runnable()
				{
					@Override
					public void run()
					{
						for( NotificationPanel panel : allPanels )
							panel.receiveNewMessage( te.getMessage() );
					}

				} );
			}
		}

	}

	// called on all panels
	private void receiveNewMessage( final String message )
	{
		if( isVisible() )
		{
			msgCount.setText( Integer.toString( getCurrentMsgCount() + 1 ) );
		}
		else
		{
			msgCount.setText( "" );
			detectMouseMovement( false );
			final String dateStr = dateFormat.format( new Date() );
			msgText.setText( message );
			dateText.setText( dateStr );
			display();
		}
	}

	private Integer getCurrentMsgCount()
	{
		String text = msgCount.getText().isEmpty() ? "0" : msgCount.getText();
		return Integer.valueOf( text );
	}

	// called on all panels
	private void display()
	{
		if( fadeAwayTask != null )
			fadeAwayTask.cancel();

		// must run this only after the JavaFX processes the above code so the panel will be in
		// the correct place before sliding down
		Platform.runLater( new Runnable()
		{
			@Override
			public void run()
			{
				setMaxHeight( -1 );
				anime.slideDown().then( resizePanel );
				detectMouseMovement( true );
			}
		} );

	}

	private void detectMouseMovement( boolean enable )
	{
		if( enable )
			mainView.getChildren().add( wholeWindowRec );
		else
			mainView.getChildren().remove( wholeWindowRec );
	}

	@Override
	public void handle( MouseEvent event )
	{
		for( NotificationPanel panel : allPanels )
			panel.thisHandle();
	}

	private void thisHandle()
	{
		detectMouseMovement( false );
		fadeAwayTask = new TimerTask()
		{
			@Override
			public void run()
			{
				anime.fadeAway();
			}
		};

		delayer.schedule( fadeAwayTask, 3000 );
	}

	public void listenOnDetachedTabs()
	{
		tabsHolder.addOnDetachCallback( new Callback<StackPane, Boolean>()
		{
			@Override
			public Boolean call( StackPane tabContents )
			{
				NotificationPanel clone = new NotificationPanel();
				clone.setOnShowLog( showLogHandler );
				clone.setMainWindowView( tabContents );
				tabContents.getChildren().add( clone );
				return true;
			}
		} );

		tabsHolder.addOnReattachCallback( new Callback<StackPane, Boolean>()
		{
			@Override
			public Boolean call( StackPane tabContents )
			{
				Node panel = Iterables.find( tabContents.getChildren(), new Predicate<Node>()
				{
					@Override
					public boolean apply( @Nullable Node input )
					{
						return input != null && input.getClass().isAssignableFrom( NotificationPanel.class );
					}
				} );
				tabContents.getChildren().remove( panel );
				allPanels.remove( panel );
				return true;
			}
		} );

	}

	private EventHandler<MouseEvent> mouseExitedHandler()
	{
		return new EventHandler<MouseEvent>()
		{
			@Override
			public void handle( MouseEvent event )
			{
				log.debug( "Mouse Existed" );
				if( anime.getCurrentState() == State.VISIBLE )
				{
					for( NotificationPanel panel : allPanels )
						panel.anime.fadeAway();
				}
			}
		};
	}

	private EventHandler<MouseEvent> mouseEnteredHandler()
	{
		return new EventHandler<MouseEvent>()
		{

			@Override
			public void handle( MouseEvent event )
			{
				log.debug( "Mouse ENTERED" );
				State state = anime.getCurrentState();
				if( state == State.VISIBLE || state == State.FADING_AWAY )
				{
					for( NotificationPanel panel : allPanels )
						panel.thisOnMouseEntered();
				}
			}

		};
	}

	private void thisOnMouseEntered()
	{
		anime.stopAnyRunningAnimation();
		if( fadeAwayTask != null )
			fadeAwayTask.cancel();
	}

}
