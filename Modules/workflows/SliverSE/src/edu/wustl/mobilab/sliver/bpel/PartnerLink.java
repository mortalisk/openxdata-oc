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
 * Represents BPEL &lt;partnerLink&gt; tags.
 * 
 * @author Greg Hackmann
 */
public class PartnerLink
{
	/**
	 * The partner link's name.
	 */
	private final String name;
	/**
	 * The partner link's type.
	 */
	private final String type;
	/**
	 * The namespace of the partner link's type.
	 */
	private final String namespace;
	/**
	 * The BPEL server's role in the partner link.
	 */
	private final String myRole;
	/**
	 * The partner's role in the partner link.
	 */
	private final String partnerRole;
	
	/**
	 * Creates a new PartnerLink.
	 * 
	 * @param parser the parser to read the partnerLink's attributes from
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */	
	PartnerLink(XmlPullParser parser) throws MalformedDocumentException,
		IOException, XmlPullParserException
	{
		parser.require(XmlPullParser.START_TAG, BPELServer.namespace, "partnerLink");

		name = parser.getAttributeValue(null, "name");
		String partnerLinkType = parser.getAttributeValue(null, "partnerLinkType");
		myRole = parser.getAttributeValue(null, "myRole");
		partnerRole = parser.getAttributeValue(null, "partnerRole");
		// Read the attributes
		
		if(name == null)
			throw new MalformedBPELException(parser,
				"<partnerLink> must specify name");
		if(partnerLinkType == null)
			throw new MalformedBPELException(parser,
				"<partnerLink> must specify partnerLinkType");
		if(myRole == null && partnerRole == null)
			throw new MalformedBPELException(parser,
				"<partnerLink> must specify at least one role");
		// Make sure we have a name, type, and role
		
		String [] expanded = Namespace.expandNamespace(partnerLinkType, parser);
		namespace = expanded[0];
		type = expanded[1];
		// Expand the link type into namespace/name
		
		parser.nextTag();
		parser.require(XmlPullParser.END_TAG, BPELServer.namespace, "partnerLink");
		parser.nextTag();
	}

	/**
	 * Gets the partner link's name.
	 * 
	 * @return the partner link's name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Gets the partner link's type.
	 * 
	 * @return the partner link's type
	 */
	public String getType()
	{
		return type;
	}
	
	/**
	 * Gets the namespace of the partner link's type.
	 * 
	 * @return the namespace of the partner link's type
	 */	
	public String getNamespace()
	{
		return namespace;
	}
	
	/**
	 * Gets the BPEL server's role.
	 * 
	 * @return the BPEL server's role
	 */
	public String getMyRole()
	{
		return myRole;
	}
	
	/**
	 * Gets the partner's role.
	 * 
	 * @return the partner's role
	 */
	public String getPartnerRole()
	{
		return partnerRole;
	}

	/**
	 * Gets the partner link's outgoing binding.
	 * 
	 * @param processInstance the current BPEL process instance
	 * @return the partner link's outgoing binding
	 */
	Binding getOutgoingBinding(ProcessInstance processInstance)
	{
		return processInstance.getOutgoingLinkBinding(this);
	}
	
	public String toString()
	{
		return "PartnerLink{name=\"" + name + "\"}";
	}
}
