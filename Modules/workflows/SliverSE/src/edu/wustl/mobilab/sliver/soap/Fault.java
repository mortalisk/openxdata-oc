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

import org.ksoap2.serialization.*;

/**
 * A representation of SOAP faults.
 * This class is used internally and should never need to be referenced by the
 * user.
 * 
 * @author Greg Hackmann
 */
public class Fault extends SoapObject
{
	/**
	 * Creates an empty Fault.
	 * This constructor exists only for kSOAP's purposes and should
	 * never be called by the user.
	 */
	public Fault()
	{
		this("", "");
	}

	/**
	 * Creates a new Fault.
	 * 
	 * @param faultCode this fault's SOAP faultcode
	 * @param faultString this fault's SOAP faultstring
	 */
	public Fault(String faultCode, String faultString)
	{
		super("http://schemas.xmlsoap.org/soap/envelope/", "Fault");
		
		addProperty("faultcode",
			new SoapPrimitive("http://www.w3.org/2001/XMLSchema", "QName",
				faultCode));
		// Add the fault code as a QName
		addProperty("faultstring", faultString);
		// Add the fault message
	}

	/**
	 * Adds a {@link SerializableException} as the fault's detail object.
	 * 
	 * @param detail the Exception to be used at the detail object
	 */
	public void addDetail(SerializableException detail)
	{
		SoapObject detailObject =
			new SoapObject("http://schemas.xmlsoap.org/soap/envelope/",
				"detail");
	
		PropertyInfo detailInfo = new PropertyInfo();
		detailInfo.name = detail.getName();
		detailInfo.namespace = detail.getNamespace();
		detailObject.addProperty(detailInfo, detail);

		addProperty("detail", detailObject);
	}
	
	/**
	 * Adds a simple message as the fault's detail object.
	 * 
	 * @param detail the detail message
	 */
	public void addDetail(String detail)
	{
		SoapObject detailObject =
			new SoapObject("http://schemas.xmlsoap.org/soap/envelope/",
				"detail");
		detailObject.addProperty("message", detail);
		addProperty("detail", detailObject);
		// Add the detail message as a SOAP <detail>
	}

	/**
	 * Adds a SOAP faultactor attribute to the fault.
	 * 
	 * @param faultactor this fault's SOAP faultactor
	 */
	public void addFaultActor(String faultactor)
	{
		addProperty("faultactor", faultactor);
	}
}
