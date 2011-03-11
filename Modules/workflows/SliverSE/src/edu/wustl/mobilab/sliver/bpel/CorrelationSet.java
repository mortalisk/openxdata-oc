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
 * Represents BPEL &lt;correlationSet&gt; tags.
 * 
 * @author Greg Hackmann
 */
class CorrelationSet
{
	/**
	 * The correlation set's name.
	 */
	private final String name;
	/**
	 * The correlation set's properties.
	 */
	private final String [] properties;
	
	/**
	 * Creates a new CorrelationSet.
	 * 
	 * @param parser the parser to read the correlationSet's attributes from
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	CorrelationSet(XmlPullParser parser) throws IOException,
		XmlPullParserException, MalformedDocumentException
	{
		parser.require(XmlPullParser.START_TAG, BPELServer.namespace,
			"correlationSet");

		name = parser.getAttributeValue(null, "name");
		if(name == null)
			throw new MalformedBPELException(parser,
				"<correlationSet> must specify name");
		// Read the name

		String propsList = parser.getAttributeValue(null, "properties").trim();
		if(name == null)
			throw new MalformedBPELException(parser,
				"<correlationSet> must specify properties");
		// Read the properties

		Vector props = new Vector();

		int seperator;
		while((seperator = propsList.indexOf(' ')) > -1)
		{
			props.addElement(propsList.substring(0, seperator));
			propsList = propsList.substring(seperator + 1).trim();
		}
		if(propsList.length() > 0) props.addElement(propsList);
		// Split the properties at the spaces

		if(props.isEmpty())
			throw new MalformedBPELException(parser,
				"<correlationSet>'s properties must not be empty");
		// Make sure there's at least one property

		properties = new String[props.size()];
		props.copyInto(properties);

		parser.nextTag();
		parser.require(XmlPullParser.END_TAG, BPELServer.namespace,
			"correlationSet");
		parser.nextTag();
	}

	/**
	 * Gets the correlation set's name.
	 * 
	 * @return the correlation set's name
	 */
	String getName()
	{
		return name;
	}

	/**
	 * Gets the correlation set's properties.
	 * 
	 * @return the correlation set's properties
	 */
	String [] getProperties()
	{
		return properties;
	}
}
