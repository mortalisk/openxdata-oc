/* Sliver - a BPEL execution engine for lightweight and mobile devices.
 * Copyright (C) 2006, Washington University in Saint Louis
 * By Gregory Hackmann.
 *
 * Washington University states that Sliver is free software;
 * you can redistribute it and/or modify it under the terms of
 * the current version of the GNU Lesser General Public License
 * as published by the Free Software Foundation.
 *
 * Sliver is distributed in the hope that it will be useful, but
 * THERE ARE NO WARRANTIES, WHETHER ORAL OR WRITTEN, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO, IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR USE.
 *
 * YOU UNDERSTAND THAT SLIVER IS PROVIDED "AS IS" FOR WHICH NO
 * WARRANTIES AS TO CAPABILITIES OR ACCURACY ARE MADE. THERE ARE NO
 * WARRANTIES AND NO REPRESENTATION THAT SLIVER IS FREE OF
 * INFRINGEMENT OF THIRD PARTY PATENT, COPYRIGHT, OR OTHER
 * PROPRIETARY RIGHTS.  THERE ARE NO WARRANTIES THAT SOFTWARE IS
 * FREE FROM "BUGS", "VIRUSES", "TROJAN HORSES", "TRAP DOORS", "WORMS",
 * OR OTHER HARMFUL CODE.
 *
 * YOU ASSUME THE ENTIRE RISK AS TO THE PERFORMANCE OF SOFTWARE AND/OR
 * ASSOCIATED MATERIALS, AND TO THE PERFORMANCE AND VALIDITY OF
 * INFORMATION GENERATED USING SOFTWARE. By using Sliver you agree to
 * indemnify, defend, and hold harmless WU, its employees, officers and
 * agents from any and all claims, costs, or liabilities, including
 * attorneys fees and court costs at both the trial and appellate levels
 * for any loss, damage, or injury caused by your actions or actions of
 * your officers, servants, agents or third parties acting on behalf or
 * under authorization from you, as a result of using Sliver.
 *
 * See the GNU Lesser General Public License for more details, which can
 * be found here: http://www.gnu.org/copyleft/lesser.html
 */

package edu.wustl.mobilab.sliver.soap.j2se;

import java.lang.reflect.*;
import java.util.*;

import org.ksoap2.serialization.*;

import edu.wustl.mobilab.sliver.soap.*;

/**
 * Delegates SOAP requests to Java methods using reflection.
 * To use this handler, create a class which will handle all of the
 * requests have a particular namespace.  Then, create <i>static</i>
 * methods in this class named after each request, using the same
 * parameters and return values specified by the WSDL file.
 * 
 * <p>Because of the way SOAP handles variable names, all return values
 * must implement the {@link KvmSerializable} interface.  I suggest extending
 * the {@link SoapObject} helper class to do this; see the code snippet below
 * for more information.</p>
 * 
 * <p>Methods must have a non-<tt>void</tt>, non-<tt>null</tt> return value
 * unless they are one-way operations.  If a method has no return value, or if
 * it returns <tt>null</tt>, then the SOAP server will interpret this as meaning
 * that the operation is one-way.</p>
 * 
 * <p>If one of your requests uses a non-standard message type
 * (i.e., something that's not defined by XSD or SOAP), then be sure
 * to use the {@link Serialization#registerClass(String, Class)} to
 * register this custom type with Sliver.</p>
 * 
 * <p>Example usage:</p>
 * 
 * <pre>public class AddResponse extends SoapObject
 *{
 *    public AddResponse()
 *    {
 *        this(0.0);
 *    }
 *
 *    public AddResponse(double result)
 *    {
 *        super("http://example.com/SampleService", "AddResponse");
 *        addProperty("result", new Double(result));
 *    }
 *}
 *
 *public class SampleService
 *{
 *    public static AddResponse add(double in1, double in2)
 *    {
 *        return new AddResponse(in1 + in2);
 *    }
 *}
 *
 *...
 *
 *Serialization.registerClass("http://example.com/SampleService", AddResponse.class);
 *server.registerService("http://example.com/SampleService", new MethodCallHandler(SampleService.class));</pre>
 *
 * <p>Note that all public static methods are exposed to the outside world
 * &mdash; even those that aren't in a WSDL file.  Be sure that your class
 * doesn't have any such methods that shouldn't be exposed (like a 
 * <code>main()</code> method) before hooking it into the
 * {@link SOAPServer}.</p>
 * 
 * @author Greg Hackmann
 */
public class MethodCallHandler implements SOAPInvocationHandler
{
	/**
	 * A mapping from primitive classes to their respective wrapper classes
	 * (e.g., <code>byte.class</code> =>
	 * <code>{@link java.lang.Byte}.class</code>)
	 */
	private static final Map primitives = new HashMap();
	static
	{
		primitives.put(byte.class, Byte.class);
		primitives.put(short.class, Short.class);
		primitives.put(int.class, Integer.class);
		primitives.put(long.class, Long.class);
		primitives.put(float.class, Float.class);
		primitives.put(double.class, Double.class);
		primitives.put(char.class, Character.class);
		// Add all the primitives to the mapping
	}
	
	/**
	 * The class that will ultimately handle the incoming requests.
	 */
	private final Class serviceClass;
	
	/**
	 * Creates a new MethodCallHandler.
	 * 
	 * @param serviceClass a reference to the class that will handle 
	 * incoming requests
	 */
	public MethodCallHandler(Class serviceClass)
	{
		this.serviceClass = serviceClass;
	}
	
	public Object handleInvocation(SoapObject request) throws
		IllegalArgumentException, UnserializableException, SerializableException
	{
		SOAPServer.log.info("Looking for method " + request.getName());
		Method method = findMethod(request);
		if(method == null)
		{
			SOAPServer.log.severe("Could not find method " + request.getName());
			throw new IllegalArgumentException("Unknown method " +
				request.getName() + " in namespace " + request.getNamespace());
		}
		// Try to find the appropriate method
		
		Object [] params = new Object[request.getPropertyCount()];
		for(int i = 0; i < params.length; i++)
			params[i] = request.getProperty(i);
		// Copy the request's parameters into an array
		
		try
		{
			SOAPServer.log.info("Invoking method " + request.getName());
			return method.invoke(null, params);
			// Invoke the method
		}
		// If the method threw an Exception
		catch(InvocationTargetException e)
		{
			Throwable cause = e.getTargetException();
			SOAPServer.log.severe("Exception thrown while executing method "
				+ request.getName() + ": " + cause);
			// Get the actual Exception that was thrown
			
			if(cause instanceof SerializableException)
				throw (SerializableException)cause;
		
			throw new UnserializableException(e.getMessage(), cause);
			// Re-throw it in the appropriate form
		}
		// If we don't have permission to access the method
		catch(IllegalAccessException e)
		{
			SOAPServer.log.severe("Exception thrown while executing method "
				+ request.getName() + ": " + e);
			throw new UnserializableException(e.getMessage(), e);
			// Re-throw it in the appropriate form
		}
	}
	
	/**
	 * Finds the method corresponding to an incoming SOAP request.
	 * 
	 * @param request the incoming request
	 * @return the corresponding method, or <code>null</code> if none exists
	 */
	private Method findMethod(SoapObject request)
	{
		String name = request.getName();
		int numParams = request.getPropertyCount();

		Method [] methods = serviceClass.getMethods();

		// For each method
nextMethod:
		for(int i = 0; i < methods.length; i++)
		{
			Method m = methods[i];
			
			if((m.getModifiers() & Modifier.STATIC) == 0)
				continue;
			if(!m.getName().equals(name))
				continue;
			// Make sure it's static and has the right name
		
			Class [] paramTypes = m.getParameterTypes();
			if(paramTypes.length != numParams)
				continue;
			// Make sure it has the right # of parameters
			
			// For each parameter
			for(int j = 0; j < paramTypes.length; j++)
			{
				Class paramType = paramTypes[j];
				Object param = request.getProperty(j);
				
				if(paramType.isPrimitive())
				{
					Class nonPrimitiveType = (Class)primitives.get(paramType);
					if(!nonPrimitiveType.isInstance(param))
						continue nextMethod;
				}
				else if(!paramType.isInstance(param))
					continue nextMethod;
				// Ensure that the method will accept the incoming
				// parameter, converting any primitive types to the appropriate
				// wrapper type first
			}
			
			return m;
		}
		
		return null;
	}
}
