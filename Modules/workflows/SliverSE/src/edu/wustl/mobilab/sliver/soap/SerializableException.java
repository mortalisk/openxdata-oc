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

package edu.wustl.mobilab.sliver.soap;

import java.util.*;

import org.ksoap2.serialization.*;

/**
 * A base Exception that can be serialized by SOAP.
 * These Exceptions can be sent as detail objects in SOAP faults.
 * (Any other Exceptions will only be sent as a simple string representing
 * the Exception's message.)
 * 
 * <p>See {@link edu.wustl.mobilab.sliver.soap} for more information about
 * how to use this class.</p>
 * 
 * @author Greg Hackmann
 */
// Note: anytime someone argues that Sun did the right thing by not including
// multiple inheritence in Java, show them this class.  Most of the code
// is a work-around for the fact that I can't simultaneously extend Exception
// and SoapObject.
public abstract class SerializableException extends Exception implements
	KvmSerializable
{
	/**
	 * The SOAP-encoded exception.
	 */
	private final transient SoapObject soapObject;
	
	/**
	 * Creates a new SerializableException.
	 * 
	 * @param namespace the exception's SOAP namespace
	 * @param name the exception's SOAP name
	 */
	public SerializableException(String namespace, String name)
	{
		this(namespace, name, null);
	}
	
	/**
	 * Creates a new SerializableException.
	 * 
	 * @param namespace the exception's SOAP namespace
	 * @param name the exception's SOAP name
	 * @param message the exception's message
	 */
	public SerializableException(String namespace, String name, String message)
	{
		super(message);
		soapObject = new SoapObject(namespace, name);
	}
    
	/**
	 * Gets the exception's SOAP name.
	 * 
	 * @return the exception's SOAP name
	 */
    public String getName()
	{
    	return soapObject.getName();
	}
    
	/**
	 * Gets the exception's SOAP namespace.
	 * 
	 * @return the exception's SOAP namespace
	 */
    public String getNamespace()
	{
    	return soapObject.getNamespace();
	}

	/**
	 * Adds a property to the exception's SOAP object.
	 * 
	 * @param name the name of the property
	 * @param value the property's value
	 */
    protected void addProperty(String name, Object value)
	{
    	soapObject.addProperty(name, value);
	}
    
    // And here's the delegate calls that we would have gotten for free
    // if we had multiple inheritence . . .
    
    /**
     * Gets a property's value.
     * 
     * @param name the property's name
     * @return the corresponding property's value
     */
    public Object getProperty(String name)
    {
    	return soapObject.getProperty(name);
    }

	public Object getProperty(int index)
	{
		return soapObject.getProperty(index);
	}

	public int getPropertyCount()
	{
		return soapObject.getPropertyCount();
	}

	public void getPropertyInfo(int index, Hashtable properties,
		PropertyInfo info)
	{
		soapObject.getPropertyInfo(index, properties, info);
	}

	public void setProperty(int index, Object value)
	{
		soapObject.setProperty(index, value);
	}

	public boolean equals(Object other)
	{
		if(!(other instanceof SerializableException))
			return false;
		
		SerializableException o = (SerializableException)other;
		return soapObject.equals(o.soapObject);
	}

    public String toString()
    {
    	return soapObject.toString();
    }
}
