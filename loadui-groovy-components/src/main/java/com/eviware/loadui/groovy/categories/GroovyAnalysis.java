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
package com.eviware.loadui.groovy.categories;

import org.codehaus.groovy.runtime.InvokerHelper;

import com.eviware.loadui.api.component.ComponentContext;
import com.eviware.loadui.api.terminal.TerminalMessage;
import com.eviware.loadui.impl.component.categories.AnalysisBase;

public class GroovyAnalysis extends AnalysisBase
{
	private final Object delegate;

	public GroovyAnalysis( final ComponentContext context, Object delegate )
	{
		super( context );
		this.delegate = delegate;
	}

	@Override
	public void analyze( TerminalMessage message )
	{
		InvokerHelper.invokeMethod( delegate, "analyze", new Object[] { message } );
	}
}
