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
 * Represents BPEL &lt;partners&gt; tags.
 * 
 * @author Greg Hackmann
 */
class Partners extends Hashtable
{
	/**
	 * Creates a new Partners.
	 * 
	 * @param parser the parser to read the partners' attributes from
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	Partners(XmlPullParser parser) throws XmlPullParserException,
		IOException, MalformedDocumentException
	{
		if(!parser.getName().equals("partners"))
			return;
		// If there's no <partners> block, then don't parse anything
		
		parser.require(XmlPullParser.START_TAG, BPELServer.namespace, "partners");
		parser.nextTag();
		
		Vector partners = new Vector();
		while(parser.getName().equals("partner"))
			partners.addElement(new Partner(parser));
		// Parse all the child <partner>s
		
		if(partners.isEmpty())
			throw new MalformedBPELException(parser,
				"<partners> must specify at least one <partner>");
		// Make sure there's at least one partner
	
		Enumeration e = partners.elements();
		// For each partner
		while(e.hasMoreElements())
		{
			Partner next = (Partner)e.nextElement();
			String [] partnerLinkNames = next.getPartnerLinkNames();
			
			// For each partner link in the partner specification
			for(int i = 0; i < partnerLinkNames.length; i++)
			{
				String partnerLinkName = partnerLinkNames[i];
				if(containsKey(partnerLinkName))
					throw new MalformedBPELException(parser, "Partner "
						+ next.getName() + " overlaps with partner "
						+ getPartner(partnerLinkName) + " over partnerLink "
						+ partnerLinkName);
				// Make sure we haven't already allocated the partner link
				// to a partner

				put(partnerLinkName, next.getName());
				// If the link's unallocated, then allocate it
			}
		}
	
		parser.require(XmlPullParser.END_TAG, BPELServer.namespace,
			"partners");
		parser.nextTag();
	}
	
	/**
	 * Gets the name of the partner that "owns" a partner link.
	 * 
	 * @param partnerLink the partner link's name
	 * @return the partner that the partner link is allocated to, or
	 * <tt>null</tt> if the link is not part of any partner specification
	 */
	String getPartner(String partnerLink)
	{
		return (String)get(partnerLink);
	}
}
