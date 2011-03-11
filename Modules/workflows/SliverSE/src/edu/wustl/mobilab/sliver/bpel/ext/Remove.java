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
import edu.wustl.mobilab.sliver.util.*;

/**
 * Represents and executes Sliver &lt;remove&gt; tags.
 * 
 * @author Greg Hackmann
 */
class Remove extends GroupMembershipChange
{
	/**
	 * Whether or not the operation will fail if the partner is not a
	 * member of the group.
	 */
	protected final boolean mustBeMember;
	
	/**
	 * Creates a new Remove.
	 * 
	 * @param parser the parser to read the remove's attributes and children
	 * from
	 * @param scopeData a description of the current scope
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	Remove(XmlPullParser parser, ScopeData scopeData) throws IOException,
		XmlPullParserException, MalformedDocumentException
	{
		super(parser, scopeData, "remove");
		parseStartTag();

		mustBeMember = "yes".equals(getAttribute("mustBeMember"));

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
				BPELServer.log.info(this + ": removing " + partnerLink + " from "
					+ partnerGroup);
				boolean succeeded = this.processInstance.removePartnerGroupMember(
					partnerGroup, partnerLink);
				// Perform the actual <remove> operation
				
				if(!succeeded)
				{
					String error = toString() + ": " + partnerLink
						+ " not a member of " + partnerGroup;

					if(mustBeMember)
					{
						BPELServer.log.severe(error);
						return new FaultedSignal(BPELServer.sliverNamespace,
							"mustBeMember", new Exception(error));
					}

					BPELServer.log.warning(error);
				}
				// If the partner is not in the group, throw a fault if
				// needed

				return Signal.COMPLETED;
			}

			protected void cancelImpl()
			{
				// <remove> is atomic
			}
		};
	}
}
