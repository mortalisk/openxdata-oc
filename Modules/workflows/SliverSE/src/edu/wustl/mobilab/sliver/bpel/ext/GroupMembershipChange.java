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
 * Represents and executes activities which change {@link PartnerGroup}
 * memberships.
 * 
 * @author Greg Hackmann
 */
abstract class GroupMembershipChange extends SliverActivity
{
	/**
	 * The {@link PartnerGroup} whose membership should be changed.
	 */
	protected PartnerGroup partnerGroup;
	/**
	 * The {@link PartnerLink} whose binding will be used to change the
	 * membership.
	 */
	protected PartnerLink partnerLink;

	/**
	 * Creates a new GroupMembershipChange.
	 * 
	 * @param parser the parser to read the tag's attributes and children
	 * from
	 * @param scopeData a description of the current scope
	 * @param type the type of the activity tag
	 */
	GroupMembershipChange(XmlPullParser parser, ScopeData scopeData, String type)
	{
		super(parser, scopeData, type);
	}

	protected void parseStartTag() throws XmlPullParserException, IOException,
		MalformedDocumentException
	{
		super.parseStartTag();

		String partnerLinkName = getAttribute("partnerLink");
		if(partnerLinkName == null)
			throw new MalformedBPELException(parser, "<" + type
				+ "> must specify partnerLink");
		// Read the partnerLink attribute

		partnerLink = scopeData.getPartnerLink(partnerLinkName);
		if(partnerLink == null)
			throw new MalformedBPELException(parser, "partnerLink "
				+ partnerLinkName + " not found in scope");
		// Verify that it refers to a real partner link

		String partnerGroupName = getAttribute("partnerGroup");
		if(partnerGroupName == null)
			throw new MalformedBPELException(parser, "<" + type
				+ "> must specify partnerGroup");
		// Read the partnerGroup attribute

		partnerGroup = scopeData.getPartnerGroup(partnerGroupName);
		if(partnerGroup == null)
			throw new MalformedBPELException(parser, "partnerGroup "
				+ partnerGroupName + " not found in scope");
		// Verify that it refers to a real partner group
	}
}
