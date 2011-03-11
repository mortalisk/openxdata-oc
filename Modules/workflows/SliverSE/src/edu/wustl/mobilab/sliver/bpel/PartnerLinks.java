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
 * Represents BPEL &lt;partnerLinks&gt; tags.
 * 
 * @author Greg Hackmann
 */
public class PartnerLinks
{
	/**
	 * The &lt;partnerLink&gt; children.
	 */
	private final Hashtable partnerLinks = new Hashtable();
	/**
	 * The &lt;partnerGroup&gt; children. 
	 */
	private final Hashtable partnerGroups = new Hashtable();
	
	/**
	 * Creates a new PartnerLinks.
	 * 
	 * @param parser the parser to read the faultHandlers' attributes and
	 * children from
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	PartnerLinks(XmlPullParser parser) throws XmlPullParserException,
		IOException, MalformedDocumentException
	{
		if(!parser.getName().equals("partnerLinks"))
			return;
		// If there's no <partnerLinks> block, then don't parse anything
		
		parser.require(XmlPullParser.START_TAG, BPELServer.namespace, "partnerLinks");
		parser.nextTag();
		
		Vector links = new Vector();
		while(parser.getName().equals("partnerLink"))
			links.addElement(new PartnerLink(parser));
		// Parse all the child <partnerLink>s
		
		if(links.isEmpty())
			throw new MalformedBPELException(parser,
				"<partnerLinks> must specify at least one <partnerLink>");
		// Make sure there's at least one partnerLink

		Vector groups = new Vector();
		while(parser.getName().equals("partnerGroup"))
			groups.addElement(new PartnerGroup(parser));
		// Parse all the child <partnerGroups>s
		
		Enumeration e = links.elements();
		while(e.hasMoreElements())
		{
			PartnerLink next = (PartnerLink)e.nextElement();
			partnerLinks.put(next.getName(), next);
		}

		e = groups.elements();
		while(e.hasMoreElements())
		{
			PartnerGroup next = (PartnerGroup)e.nextElement();
			partnerGroups.put(next.getName(), next);
		}

		parser.require(XmlPullParser.END_TAG, BPELServer.namespace,
			"partnerLinks");
		parser.nextTag();
	}

	/**
	 * Gets the partner group with the specified name.
	 * 
	 * @param name the partner group name
	 * @return the corresponding {@link PartnerGroup}
	 */
	PartnerGroup getPartnerGroup(String name)
	{
		return (PartnerGroup)partnerGroups.get(name);
	}

	/**
	 * Gets the partner link with the specified name.
	 * 
	 * @param name the partner link name
	 * @return the corresponding {@link PartnerLink}
	 */
	public PartnerLink getPartnerLink(String name)
	{
		return (PartnerLink)partnerLinks.get(name);
	}
	
	/**
	 * Enumerates through the all partner links declared within this
	 * <tt>partnerLinks</tt> section.
	 * 
	 * @return an {@link Enumeration} of all {@link PartnerLink}s
	 */
	public Enumeration getPartnerLinks()
	{
		return partnerLinks.elements();
	}
}
