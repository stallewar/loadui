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
package com.eviware.loadui.ui.fx.api.analysis;

import java.util.concurrent.Callable;

import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.util.Callback;

import com.eviware.loadui.api.statistics.store.Execution;

public interface ExecutionsInfo
{

	/**
	 * The ExecutionsInfo Data will be passed on to the client through the
	 * {@link ExecutionsInfo#runWhenReady(Callable)} callback.
	 * 
	 * @author renato
	 * 
	 */
	public interface Data
	{

		/**
		 * @return all recent executions (ie. executions which have not been
		 *         archived and not including the current run)
		 */
		ObservableList<Execution> getRecentExecutions();

		/**
		 * @return executions archived by the user
		 */
		ObservableList<Execution> getArchivedExecutions();

		/**
		 * @return current execution property. The actual value is updated as
		 *         required.
		 */
		ReadOnlyProperty<Execution> getCurrentExecution();

		/**
		 * Adds the given node to the executions menu.
		 * 
		 * @param node
		 */
		void addToMenu( Node node );

	}

	/**
	 * The given callback will be called when all properties have been set. If
	 * all properties are already set when registering this will run immediately.
	 * This callback will be run only once, whenever the executions data is ready.
	 * After a call to this.reset(), this callback will be removed. To allow the
	 * callback to be re-run after a call to this.reset() (and when the executions
	 * data is ready again), use the method alwaysRunWhenReady(..)
	 * 
	 * @param callback
	 */
	void runWhenReady( Callback<Data, Void> callback );
	
	/**
	 * The given callback will be called when all properties have been set. If
	 * all properties are already set when registering this will run immediately.
	 * The given callback will never be forgotten. After a call to this.reset(),
	 * it may be run again when all the executions data is ready again.
	 * @param callback
	 */
	void alwaysRunWhenReady( Callback<Data, Void> callback );
	
	/**
	 * Forget about the executions data, releasing any resources as necessary.
	 */
	void reset();

}
