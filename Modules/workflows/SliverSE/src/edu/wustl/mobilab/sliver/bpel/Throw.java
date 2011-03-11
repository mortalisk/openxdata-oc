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
 * Represents and executes BPEL &lt;throw&gt; tags.
 * 
 * @author Greg Hackmann
 */
class Throw extends Activity
{
	/**
	 * The namespace of the fault's message type.
	 */
	final String faultNamespace;
	/**
	 * The fault's message type.
	 */
	final String faultName;

	/**
	 * Creates a new Throw.
	 * 
	 * @param parser the parser to read the throw's attributes from
	 * @param scopeData a description of the current scope
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	Throw(XmlPullParser parser, ScopeData scopeData) throws IOException,
		XmlPullParserException, MalformedDocumentException
	{
		super(parser, scopeData.createNestedScope(), "throw");
		parseStartTag();
		
		String faultString = getAttribute("faultName");
		if(faultString == null)
			throw new MalformedBPELException(parser, "<throw> must specify faultName");
		
		String [] split = Namespace.expandNamespace(faultString, parser);
		faultNamespace = split[0];
		faultName = split[1];
		// Read the faultName and split it into name and namespace
		
		// TODO: parse faultVariable
		
		parseStandardElements();
		parser.nextTag();
		
		parseEndTag();
		parser.nextTag();
	}
	
	protected ActivityInstance newInstance(ProcessInstance processInstance)
	{
		return new ActivityInstance(this, processInstance)
		{
			protected Signal executeImpl()
			{
				String error = toString() + " threw fault of type "
					+ faultNamespace + ":" + faultName;
				BPELServer.log.warning(error);

				return new FaultedSignal(faultNamespace, faultName,
					new RuntimeException(error));
			}

			protected void cancelImpl()
			{
				// Do nothing, since throwing a fault is effectively atomic
			}
		};
	}
		
}
