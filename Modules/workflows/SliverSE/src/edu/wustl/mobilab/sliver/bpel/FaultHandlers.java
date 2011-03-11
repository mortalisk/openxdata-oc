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
import java.util.*;

import org.xmlpull.v1.*;

import edu.wustl.mobilab.sliver.util.*;

/**
 * Represents BPEL &lt;faultHandlers&gt; tags.
 * 
 * @author Greg Hackmann
 */
class FaultHandlers
{
	/**
	 * The catch blocks.
	 */
	private final Catch [] catches;
	/**
	 * The catchAll block.
	 */
	private CatchAll catchAll = null;

	/**
	 * Creates a new FaultHandlers.
	 * 
	 * @param parser the parser to read the faultHandlers' attributes and
	 * children from
	 * @param scopeData a description of the current scope
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	FaultHandlers(XmlPullParser parser, ScopeData scopeData)
		throws XmlPullParserException, IOException, MalformedDocumentException
	{
		if(!"faultHandlers".equals(parser.getName()))
		{
			catches = new Catch[0];
			return;
		}
		// If there's no <faultHandlers> block, then create an empty set
		// of catches and return without parsing anything

		parser.require(XmlPullParser.START_TAG, BPELServer.namespace,
			"faultHandlers");
		parser.nextTag();

		Vector catchesVector = new Vector();
		while("catch".equals(parser.getName()))
			catchesVector.addElement(new Catch(parser, scopeData));
		// Parse the <catch> child tags

		if("catchAll".equals(parser.getName()))
			catchAll = new CatchAll(parser, scopeData);
		// Parse the <catchAll> child tag if it's there

		if(catchesVector.isEmpty() && catchAll == null)
			throw new MalformedBPELException(parser,
				"<faultHandlers> must have at least one <catch> or <catchAll>");
		// Make sure there's at least one handler

		catches = new Catch[catchesVector.size()];
		catchesVector.copyInto(catches);

		parser.require(XmlPullParser.END_TAG, BPELServer.namespace,
			"faultHandlers");
		parser.nextTag();
	}
	
	/**
	 * Gets the fault handler for a specified fault.
	 * 
	 * @param fault the {@link FaultedSignal} that needs to be handled
	 * @param processInstance the process instance that threw the fault
	 * @return an instance of the corresponding fault handler, or <tt>null</tt>
	 * if this {@link FaultHandlers} cannot handle the fault
	 */
	ActivityInstance getHandler(FaultedSignal fault,
		ProcessInstance processInstance)
	{
		return getHandler(fault.getNamespace(), fault.getType(), processInstance,
			catches, catchAll);
	}

	/**
	 * Gets the fault handler for a specified fault.
	 * 
	 * @param namespace the fault's namespace
	 * @param type the fault's type
	 * @param processInstance the process instance that threw the fault
	 * @param catches the candidate {@link Catch}es to search for a handler 
	 * @param catchAll the {@link CatchAll} to fall back onto (optionally
	 * <tt>null</tt>)
	 * 
	 * @return an instance of the corresponding fault handler, or <tt>null</tt>
	 * if none was found in the provided handlers
	 */
	static ActivityInstance getHandler(String namespace, String type,
		ProcessInstance processInstance, Catch [] catches, CatchAll catchAll)
	{
		for(int i = 0; i < catches.length; i++)
			if(catches[i].matchesType(namespace, type))
				return catches[i].newInstance(processInstance);
		// Iterate through all the <catch>es
		
		if(catchAll != null)
			return catchAll.newInstance(processInstance);
		// If we have a <catchAll>, fall back on that
		
		return null;
		// Otherwise give up
	}
}
