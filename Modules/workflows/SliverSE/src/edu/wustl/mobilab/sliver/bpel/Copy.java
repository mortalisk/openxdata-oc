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
 * Represents and executes BPEL &lt;copy&gt; tags.
 * 
 * @author Greg Hackmann
 */
class Copy
{	
	/**
	 * The source of the data to copy.
	 */
	private final From from;
	/**
	 * The destination of the data to copy.
	 */
	private final To to;

	/**
	 * Creates a new Copy.
	 * 
	 * @param parser the parser to read the copy's attributes and children
	 * from
	 * @param scopeData a description of the current scope
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */	
	Copy(XmlPullParser parser, ScopeData scopeData) throws IOException,
		XmlPullParserException, MalformedDocumentException
	{
		parser.require(XmlPullParser.START_TAG, BPELServer.namespace, "copy");
		parser.nextTag();
	
		from = From.parse(parser, scopeData);
		to = To.parse(parser, scopeData);
		// Parse the <from> and <to> children
		
		Class sourceType = from.getSourceType();
		Class sinkType = to.getSinkType();
		if(!sourceType.equals(sinkType))
			throw new MalformedBPELException(parser,
				"<from> and <to> specs are incompatible");
		
		parser.require(XmlPullParser.END_TAG, BPELServer.namespace, "copy");
		parser.nextTag();
	}
	
	/**
	 * Executes the copy.
	 * 
	 * @param processInstance the current BPEL process instance
	 * @return the {@link Signal} that the copy activity generated
	 */
	Signal execute(ProcessInstance processInstance)
	{
		try
		{
			Object value = from.read(processInstance);
			if(value == null)
			{
				String error = "<copy> tried to read uninitalized data";
				BPELServer.log.severe(error);
				return new FaultedSignal(BPELServer.namespace,
					"uninitializedVariable",
					new IllegalArgumentException(error));
			}
			// Read the data in and make sure it isn't empty
			to.write(value, processInstance);
			// Write the data out
		}
		catch(FaultedSignal e)
		{
			return e;
		}
		
		return Signal.COMPLETED;		
	}
}
