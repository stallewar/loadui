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
package com.eviware.loadui.util.test;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.eviware.loadui.util.BeanInjector;

/**
 * Creating an instance of BeanInjectorMocker causes calls to
 * BeanInjector.getBean() to return mocks, or custom instances of beans.
 * 
 * @author dain.nilsson
 */
public class BeanInjectorMocker
{
	private final Map<Class<?>, Object> mapping;

	/**
	 * Creates a default BeanInjectorMocker, which will return a mock for each
	 * call to getBean(), unless a custom instance has been provided in the
	 * mapping. For multiple calls to getBean using the same class, the same mock
	 * will be returned.
	 */
	public BeanInjectorMocker()
	{
		this( new HashMap<Class<?>, Object>() );
	}

	/**
	 * Like the default constructor, but provides a mapping for custom instances.
	 * 
	 * @see BeanInjectorMocker#BeanInjectorMocker()
	 * @param mapping
	 */
	public BeanInjectorMocker( Map<Class<?>, Object> mapping )
	{
		this.mapping = mapping;

		init();
	}

	/**
	 * Adds a custom instance for a bean to return for the given class.
	 * 
	 * @param cls
	 * @param bean
	 * @return
	 */
	public <T> BeanInjectorMocker put( Class<T> cls, T bean )
	{
		mapping.put( cls, bean );

		return this;
	}

	private void init()
	{
		final BundleContext contextMock = mock( BundleContext.class );
		BeanInjector.setBundleContext( contextMock );
		BeanInjector.INSTANCE.clearCache();

		when( contextMock.getServiceReference( anyString() ) ).thenAnswer( new Answer<ServiceReference>()
		{
			public ServiceReference answer( InvocationOnMock invocation ) throws Throwable
			{
				ServiceReference referenceMock = mock( ServiceReference.class );
				Object value = getBean( ( String )invocation.getArguments()[0] );
				when( contextMock.getService( referenceMock ) ).thenReturn( value );

				return referenceMock;
			}
		} );
	}

	private Object getBean( String className )
	{
		Class<?> cls = null;
		for( Class<?> c : mapping.keySet() )
		{
			if( className.equals( c.getName() ) )
			{
				cls = c;
				break;
			}
		}

		if( cls == null )
		{
			try
			{
				cls = Class.forName( className );
			}
			catch( ClassNotFoundException e )
			{
				return null;
			}
			mapping.put( cls, mock( cls ) );
		}

		return mapping.get( cls );
	}
}