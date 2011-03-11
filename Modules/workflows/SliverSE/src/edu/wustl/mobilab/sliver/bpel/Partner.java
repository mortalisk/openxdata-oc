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
 * Represents BPEL &lt;partner&gt; tags.
 * 
 * @author Greg Hackmann
 */
class Partner
{
	/**
	 * The partner's name.
	 */
	private final String name;
	/**
	 * The names of the partner links that this partner includes.
	 */
	private final String [] partnerLinkNames;

	/**
	 * Creates a new Partner.
	 * 
	 * @param parser the parser to read the partner's attributes from
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	Partner(XmlPullParser parser) throws MalformedDocumentException,
		IOException, XmlPullParserException
	{
		parser.require(XmlPullParser.START_TAG, BPELServer.namespace, "partner");

		name = parser.getAttributeValue(null, "name");
		if(name == null)
			throw new MalformedBPELException(parser,
				"<partner> must specify name");
		// Make sure the partner has a name
		
		parser.nextTag();
		Vector partnerLinks = new Vector();
		// For each <partnerLink>
		while(parser.getName().equals("partnerLink"))
		{
			parser.require(XmlPullParser.START_TAG, BPELServer.namespace, "partnerLink");
			
			String partnerLinkName = parser.getAttributeValue(null, "name");
			if(partnerLinkName == null)
				throw new MalformedBPELException(parser,
					"<partnerLink> must specify name");
			// Make sure the partnerLink has a name
			
			partnerLinks.addElement(partnerLinkName);
			// Add the link to the list
			
			parser.nextTag();
			parser.require(XmlPullParser.START_TAG, BPELServer.namespace, "partnerLink");
			parser.nextTag();
		}
		
		if(partnerLinks.isEmpty())
			throw new MalformedBPELException(parser,
				"<partner> must specify at least one <partnerLink>");
		// Make sure there's at least one partnerLink
		
		partnerLinkNames = new String[partnerLinks.size()];
		partnerLinks.copyInto(partnerLinkNames);			
		
		parser.require(XmlPullParser.END_TAG, BPELServer.namespace, "partner");
		parser.nextTag();
	}
	
	/**
	 * Gets the partner's name.
	 * 
	 * @return the partner's name
	 */
	String getName()
	{
		return name;
	}
	
	/**
	 * Gets the names of the partner links that this partner includes.
	 * 
	 * @return the names of the partner links that this partner includes
	 */
	String [] getPartnerLinkNames()
	{
		return partnerLinkNames;
	}
}
