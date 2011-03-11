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
 * Represents BPEL &lt;links&gt; tags.
 * 
 * @author Greg Hackmann
 */
class Links extends Hashtable
{
	/**
	 * Creates a new Links.
	 * 
	 * @param parser the parser to read the links' attributes and children
	 * from
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	Links(XmlPullParser parser) throws IOException, XmlPullParserException,
		MalformedDocumentException
	{
		if(!"links".equals(parser.getName()))
			return;
		// If we're not sitting on a <links> tag (it's optional), then do
		// nothing

		parser.require(XmlPullParser.START_TAG, BPELServer.namespace, "links");
		parser.nextTag();

		while("link".equals(parser.getName()))
		{
			LinkSpecification linkSpec = new LinkSpecification(parser);
			put(linkSpec.getName(), linkSpec);
		}
		// Read in all the children <link>s

		if(isEmpty())
			throw new MalformedBPELException(parser,
				"<links> must specify at least one link");
		// Ensure that there's at least one child
		
		parser.require(XmlPullParser.END_TAG, BPELServer.namespace, "links");
		parser.nextTag();
	}
	
	/**
	 * Gets a {@link LinkSpecification} with a specified name.
	 * 
	 * @param name the name of the link to get
	 * @return the corresponding {@link LinkSpecification}
	 */
	LinkSpecification getLinkSpecification(String name)
	{
		return (LinkSpecification)get(name);
	}
	
	/**
	 * Instantiates all the links for a given process instance.
	 * 
	 * @param processInstance the current BPEL process instance
	 */
	void createConcreteLinks(ProcessInstance processInstance)
	{
		Enumeration e = elements();
		// For each link
		while(e.hasMoreElements())
		{
			LinkSpecification linkSpec = (LinkSpecification)e.nextElement();
			ConcreteLink link = new ConcreteLink();
			processInstance.setConcreteLink(linkSpec, link);
			// Get the link and instatiate it, then update the instance
		}
	}
}
