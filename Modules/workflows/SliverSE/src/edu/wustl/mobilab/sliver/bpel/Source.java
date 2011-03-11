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
import edu.wustl.mobilab.sliver.xpath.*;

/**
 * Represents BPEL &lt;source&gt; tags.
 * 
 * @author Greg Hackmann
 */
class Source extends LinkEndpoint
{
	/**
	 * The link's transition condition, represented as an XPath expression.
	 */
	private Expression transitionCondition = null;
	/**
	 * The process's current scope.
	 */
	private final ScopeData scopeData;

	/**
	 * Creates a new Source.
	 * 
	 * @param parser the parser to read the source's attributes from
	 * @param scopeData a description of the current scope
	 * @param activity the {@link Activity} that acts as the link's source
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */	
	Source(XmlPullParser parser, ScopeData scopeData, Activity activity)
		throws IOException, XmlPullParserException, MalformedDocumentException
	{
		super(parser, "source", activity);
		this.scopeData = scopeData;
	
		parseStartTag();
		String transitionConditionString = parser.getAttributeValue(null,
			"transitionCondition");
		if(transitionConditionString != null)
			transitionCondition = ExpressionParser.parse(
				transitionConditionString, parser, scopeData);
		// Read and parse the transition condition (if any)
		
		parser.nextTag();		
		parseEndTag();
		parser.nextTag();
	}
	
	/**
	 * Fires the links.
	 * 
	 * @param processInstance the current BPEL process instance
	 * @param completed whether or not the source activity ran to completion
	 * 
	 * @throws ExpressionEvaluationException there was an error evaluating
	 * the link's status
	 */
	void fire(ProcessInstance processInstance, boolean completed)
		throws ExpressionEvaluationException
	{
		boolean status = false;
		try
		{
			status = completed;
			if(status && transitionCondition != null)
				status &= transitionCondition.evaluateBoolean(scopeData,
					processInstance);
			// The link's status is true if the activity succeeded and
			// (a) the link has no transition condition, or (b) the link's
			// transition condition is true
		}
		catch(ExpressionEvaluationException e)
		{
			BPELServer.log.severe("Exception thrown while evaluating transitionCondition: "
				+ e.toString());
			e.printStackTrace();
			
			status = false;	
			throw e;
		}
		finally
		{
			BPELServer.log.info("Link " + linkSpec + " firing with status "
				+ status);
			processInstance.getConcreteLink(linkSpec).setStatus(status);
			// Fire the link after evaluating its status
		}
	}
}
