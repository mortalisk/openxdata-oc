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

package edu.wustl.mobilab.sliver.bpel;

import java.io.*;

import org.xmlpull.v1.*;

import edu.wustl.mobilab.sliver.util.*;

/**
 * Represents and executes BPEL &lt;catch&gt; tags.
 * 
 * @author Greg Hackmann
 */
class Catch
{
	/**
	 * The name of the type of fault handled by this Catch.
	 */
	private String faultName;
	/**
	 * The namespace of the type of fault handled by this Catch.
	 */
	private String faultNamespace;
	/**
	 * The name of the fault variable handled by this Catch.
	 */
	private String faultVariable;
	/**
	 * The activity that is executed when a fault is thrown.
	 */
	private Activity activity;

	/**
	 * Creates a new Catch.
	 * 
	 * @param parser the parser to read the catch's attributes and children
	 * from
	 * @param scopeData a description of the current scope
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	Catch(XmlPullParser parser, ScopeData scopeData) throws IOException,
		XmlPullParserException, MalformedDocumentException
	{
		parser.require(XmlPullParser.START_TAG, BPELServer.namespace, "catch");

		String faultNameQualified = parser.getAttributeValue(null, "faultName");
		// Get the fully-qualified fault type

		String [] portTypeSplit = Namespace.expandNamespace(faultNameQualified,
			parser);
		faultNamespace = portTypeSplit[0];
		faultName = portTypeSplit[1];
		// Split the type into name and namespace

		faultVariable = parser.getAttributeValue(null, "faultVariable");
		parser.nextTag();
		// Get the fault variable name

		activity = Activity.parse(parser, scopeData);
		// Parse the child fault handler
		
		parser.require(XmlPullParser.END_TAG, BPELServer.namespace, "catch");
		parser.nextTag();
	}
	
	/**
	 * Determines if this Catch matches the specified type of fault.
	 * 
	 * @param namespace the type's namespace
	 * @param name the type's name
	 * @return whether or not this Catch matches the specified name and
	 * namespace
	 */
	boolean matchesType(String namespace, String name)
	{
		return namespace.equals(faultNamespace) && name.equals(faultName);
		// Make sure both name and namespace match
 	}

	/**
	 * Creates a new instance of the &lt;catch&gt;'s fault handling activity.
	 * 
	 * @param processInstance information about the current process instance
	 * @return a {@link ActivityInstance} corresponding to the activity that
	 * should be executed to handle the fault
	 */
	ActivityInstance newInstance(ProcessInstance processInstance)
	{
		BPELServer.log.warning("Executing <catch> handler for type "
			+ faultNamespace + ":" + faultName);		

		return activity.newInstance(processInstance);
	}
}
