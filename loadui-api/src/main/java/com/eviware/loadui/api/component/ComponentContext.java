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
package com.eviware.loadui.api.component;

import java.util.Collection;
import java.util.Map;

import com.eviware.loadui.api.counter.CounterHolder;
import com.eviware.loadui.api.layout.LayoutComponent;
import com.eviware.loadui.api.layout.SettingsLayoutContainer;
import com.eviware.loadui.api.model.CanvasItem;
import com.eviware.loadui.api.model.ComponentItem;
import com.eviware.loadui.api.model.PropertyHolder;
import com.eviware.loadui.api.terminal.DualTerminal;
import com.eviware.loadui.api.terminal.InputTerminal;
import com.eviware.loadui.api.terminal.MutableTerminalHolder;
import com.eviware.loadui.api.terminal.OutputTerminal;
import com.eviware.loadui.api.terminal.TerminalMessage;

/**
 * The ComponentContext is attached to a Component and given to its
 * ComponentBehavior. It allows the ComponentBehavior to access and modify parts
 * of the Component.
 * 
 * @author dain.nilsson
 */
public interface ComponentContext extends MutableTerminalHolder, PropertyHolder, CounterHolder
{
	/**
	 * Used when triggering actions to specify the scope of the action.
	 * 
	 * @author dain.nilsson
	 */
	public static enum Scope
	{
		COMPONENT, CANVAS, PROJECT, WORKSPACE
	}

	/**
	 * The label of the Terminal which is returned when calling
	 * getControllerTerminal();
	 */
	public static final String CONTROLLER_TERMINAL = "controllerTerminal";

	/**
	 * The label of the Terminal which is returned when calling
	 * getRemoteTerminal();
	 */
	public static final String REMOTE_TERMINAL = "remoteTerminal";

	/**
	 * CollectionEvent key for listening for changes to the runnerTerminals
	 * collection.
	 */
	public static final String RUNNER_TERMINALS = ComponentContext.class.getName() + "@runnerTerminals";

	/**
	 * The base channel for all Component communication.
	 */
	public static final String COMPONENT_CONTEXT_CHANNEL = "/" + ComponentContext.class.getName();

	/**
	 * Sets a String attribute on the underlying ComponentItem.
	 * 
	 * @param key
	 *           The name of the attribute to set.
	 * @param value
	 *           The value to set.
	 */
	public void setAttribute( String key, String value );

	/**
	 * Gets a String attribute previously stored for the underlying
	 * ComponentItem.
	 * 
	 * @param key
	 *           The name of the attribute to get.
	 * @param defaultValue
	 *           A default String to return if the attribute does not exist.
	 * @return The value of the attribute, or the default value if the attribute
	 *         does not exist.
	 */
	public String getAttribute( String key, String defaultValue );

	/**
	 * Gets a reference to the CanvasItem containing the Component.
	 * 
	 * @return
	 */
	public CanvasItem getCanvas();

	/**
	 * Gets a reference to the (possibly not initialized) ComponentItem
	 * associated to the ComponentContext.
	 * 
	 * @return
	 */
	public ComponentItem getComponent();

	/**
	 * @see #createInput(String label, String description)
	 */
	public InputTerminal createInput( String label );

	/**
	 * @see #createOutput(String label, String description)
	 */
	public OutputTerminal createOutput( String label );

	/**
	 * Changes the signature of the messages sent from an OutputTerminal on this
	 * Component.
	 * 
	 * @param terminal
	 *           The OutputTerminal to set the signature for.
	 * @param signature
	 *           The new signature to set.
	 */
	public void setSignature( OutputTerminal terminal, Map<String, Class<?>> signature );

	/**
	 * Sends a message out through an OutputTerminal.
	 * 
	 * @param terminal
	 *           The terminal to send the message through.
	 * @param message
	 *           The message to send.
	 */
	public void send( OutputTerminal terminal, TerminalMessage message );

	/**
	 * Gets a special DualTerminal which can be used to send messages to remote
	 * instances of the ComponentItem itself. If used by a Component on the
	 * Controller then the sent message is broadcast to all assigned Runners. If
	 * used by a Component on a Runner then the message is sent to the
	 * Controller.
	 * 
	 * @return
	 */
	public DualTerminal getRemoteTerminal();

	/**
	 * Gets a special OutputTerminal which and can be used to send messages to
	 * remote instances of the ComponentItem itself. Regardless of if a
	 * TerminalMessage is sent to this Terminal from a Component on a Runner or
	 * on the Controller, the Component instance on the Controller will receive
	 * the event.
	 * 
	 * @return The Controller OutputTerminal.
	 */
	public OutputTerminal getControllerTerminal();

	/**
	 * Gets a Collection of Terminals, one corresponding to each assigned Runner.
	 * These terminals can be used to target individual runners from the
	 * Controller. When invoked on a Runner, this will return an empty
	 * Collection.
	 * 
	 * @return
	 */
	public Collection<DualTerminal> getRunnerTerminals();

	/**
	 * Creates a new empty message which can be modified and sent using the send
	 * method.
	 * 
	 * @return A new blank message.
	 */
	public TerminalMessage newMessage();

	/**
	 * Sets the category for the Component.
	 * 
	 * @param category
	 *           The category to set.
	 */
	public void setCategory( String category );

	/**
	 * Gets the category for the Component.
	 * 
	 * @return The category for the Component.
	 */
	public String getCategory();

	/**
	 * Get the label for the Component.
	 */
	public String getLabel();

	/**
	 * Set the label for the Component.
	 */
	public void setLabel( String label );

	/**
	 * Get the ID for the Component.
	 */
	public String getId();

	/**
	 * Get the Layout for the Component.
	 */
	public LayoutComponent getLayout();

	/**
	 * Set the Layout for the Component.
	 */
	public void setLayout( LayoutComponent layout );

	/**
	 * If set to true, then calls to the "onTerminal..."-methods on the
	 * BehaviorProvider will be called directly in the event thread, without the
	 * overhead of allocating a separate Thread. These methods must then not
	 * block, returning as quickly as possible to prevent the application from
	 * becoming unresponsive. This should only be set to true if you know what
	 * you are doing. With great power comes great responsibility!
	 * 
	 * @param nonBlocking
	 *           True if and only if the BehaviorProvider is guaranteed to not
	 *           block when processing incoming messages.
	 */
	public void setNonBlocking( boolean nonBlocking );

	/**
	 * Sets the URL of the web page which is used to display help information
	 * about the component.
	 * 
	 * @param helpUrl
	 */
	public void setHelpUrl( String helpUrl );

	/**
	 * Adds settings layout container to component. This container is added as a
	 * tab in wrench dialog of a component and is defined in component's groovy
	 * script.
	 * 
	 * @param tab
	 */
	public void addSettingsTab( SettingsLayoutContainer tab );

	/**
	 * Triggers the given action upon the proper ModelItem as defined by the
	 * scope.
	 * 
	 * @param actionName
	 *           The name of the action to trigger.
	 * @param scope
	 *           The scope of the action.
	 */
	public void triggerAction( String actionName, Scope scope );

	/**
	 * Gets the current running state of the surrounding CanvasItem.
	 * 
	 * @return True if the surrounding canvas is running, false if it is stopped.
	 */
	public boolean isRunning();

	/**
	 * Checks to see if the ComponentItem is in an invalid state and won't
	 * function properly. This may be due to an invalid configuration or a
	 * missing resource, etc.
	 * 
	 * @return
	 */
	public boolean isInvalid();

	/**
	 * Set the invalid state of the ComponentItem.
	 * 
	 * @param state
	 */
	public void setInvalid( boolean state );

	/**
	 * True if running on the Controller, false if on a Runner.
	 * 
	 * @return
	 */
	public boolean isController();
}
