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
import java.util.*;

import org.xmlpull.v1.*;

import edu.wustl.mobilab.sliver.bpel.*;
import edu.wustl.mobilab.sliver.soap.*;
import edu.wustl.mobilab.sliver.util.*;

/**
 * Represents and executes Sliver &lt;reply&gt; tags.
 * 
 * @author Greg Hackmann
 */
public class Reply extends edu.wustl.mobilab.sliver.bpel.Reply
{
	/**
	 * The partner group that the reply will be broadcast to.
	 */
	PartnerGroup partnerGroup;
	
	/**
	 * Creates a new Reply.
	 * 
	 * @param parser the parser to read the reply's attributes from
	 * @param scopeData a description of the current scope
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	Reply(XmlPullParser parser, ScopeData scopeData)
		throws IOException, XmlPullParserException, MalformedDocumentException
	{
		super(parser, scopeData, BPELServer.sliverNamespace);
	}

	protected void parsePartnerLink() throws MalformedDocumentException
	{
		String partnerLinkName = getAttribute("partnerLink");
		String partnerGroupName = getAttribute("partnerGroup");
		if(partnerLinkName == null && partnerGroupName == null)
			throw new MalformedBPELException(parser, "<" + type
				+ "> must specify partnerLink or partnerGroup");
		if(partnerLinkName != null && partnerGroupName != null)
			throw new MalformedBPELException(parser, "<" + type
				+ "> cannot specify both partnerLink and partnerGroup");
		// Get the partnerLink and partnerGroup attributes

		if(partnerLinkName != null)
		{
			partnerLink = scopeData.getPartnerLink(partnerLinkName);
			if(partnerLink == null)
				throw new MalformedBPELException(parser, "partnerLink "
					+ partnerLinkName + " not found in scope");
			// If a partner link was chosen, make sure that it actually exists
		}
		else
		{
			partnerGroup = scopeData.getPartnerGroup(partnerGroupName);
			if(partnerGroup == null)
				throw new MalformedBPELException(parser, "partnerGroup "
					+ partnerGroupName + " not found in scope");
			// Otherwise, make sure that the partner group exists
		}
	}

	protected void parseStartTag() throws XmlPullParserException, IOException,
		MalformedDocumentException
	{
		super.parseStartTag();
		closeConnection = !("no".equals(getAttribute("closeConnection")));
		// Also parse the closeConnection attribute
	}

	protected ActivityInstance newInstance(ProcessInstance processInstance)
	{
		if(partnerLink != null)
			return new ReplyInstance(processInstance);
		// Use a regular reply activity instance if we're not broadcasting
		// to a group
		
		return new GroupReplyInstance(processInstance);
		// Otherwise use the custom group reply activity
	}

	/**
	 * Represents an executable instance of Sliver &lt;reply&gt; tags which
	 * send their replies to partner groups.
	 * 
	 * @author Greg Hackmann
	 */
	private class GroupReplyInstance extends ReplyInstance
	{
		/**
		 * Creates a new GroupReplyInstance.
		 * 
		 * @param processInstance the process instance that this activity
		 * instance is being executed in
		 */
		GroupReplyInstance(ProcessInstance processInstance)
		{
			super(processInstance);
		}

		protected Signal executeImpl()
		{
			Vector membership = processInstance.getPartnerGroupMembership(partnerGroup);
			// Get the group's membership
			synchronized(membership)
			{
				Enumeration e = membership.elements();
				// For each group member
				while(e.hasMoreElements())
				{
					Channel next = (Channel)e.nextElement();
					Signal nextSignal = sendReply(next);
					if(nextSignal != Signal.COMPLETED)
						return nextSignal;
					// Send the reply to that member
				}
			}

			return Signal.COMPLETED;
		}

		protected void closeConnection()
		{
			processInstance.removeActiveConnection(activeChannel);
			// Close the connection, but don't unbind the channel
			// (since unbinding loses meaning when talking about a whole group)
		}
	}
}
