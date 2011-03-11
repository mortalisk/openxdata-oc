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

package edu.wustl.mobilab.sliver.bpel.ext;

import java.io.*;

import org.xmlpull.v1.*;

import edu.wustl.mobilab.sliver.bpel.*;
import edu.wustl.mobilab.sliver.soap.*;
import edu.wustl.mobilab.sliver.util.*;

/**
 * Represents and executes Sliver &lt;close&gt; tags.
 * 
 * @author Greg Hackmann
 */
class Close extends SliverActivity
{
	/**
	 * The {@link PartnerLink} to close.
	 */
	final PartnerLink partnerLink;

	/**
	 * Creates a new Close.
	 * 
	 * @param parser the parser to read the close's attributes and children
	 * from
	 * @param scopeData a description of the current scope
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	Close(XmlPullParser parser, ScopeData scopeData) throws IOException,
		XmlPullParserException, MalformedDocumentException
	{
		super(parser, scopeData, "close");
		parseStartTag();

		String partnerLinkName = getAttribute("partnerLink");
		if(partnerLinkName == null)
			throw new MalformedBPELException(parser,
				"<close> must specify partnerLink");

		partnerLink = scopeData.getPartnerLink(partnerLinkName);
		if(partnerLink == null)
			throw new MalformedBPELException(parser, "partnerLink "
				+ partnerLinkName + " not found in scope");

		parser.nextTag();
		parseStandardElements();

		parseEndTag();
		parser.nextTag();
	}

	protected ActivityInstance newInstance(ProcessInstance processInstance)
	{
		return new ActivityInstance(this, processInstance)
		{
			protected Signal executeImpl()
			{
				Channel binding = this.processInstance.getIncomingLinkBinding(
					this, partnerLink, false);
				try
				{
					binding.close();
				}
				catch(IOException e)
				{
					BPELServer.log.severe(toString()
						+ ": I/O exception thrown: " + e);
					e.printStackTrace();
					return new FaultedSignal(BPELServer.namespace,
						"ioException", e);
				}
				// Get the actual binding and try to close it

				return Signal.COMPLETED;
			}

			protected void cancelImpl()
			{
				// <close> is atomic
			}
		};
	}
}
