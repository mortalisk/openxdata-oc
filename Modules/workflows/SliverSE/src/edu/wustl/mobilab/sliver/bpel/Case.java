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
 * Represents and executes BPEL &lt;case&gt; tags.
 * 
 * @author Greg Hackmann
 */
class Case
{
	/**
	 * The condition that determines if this case should execute.
	 */
	private final Expression condition;
	/**
	 * The activity that should be executed if the condition is true.
	 */
	private final Activity activity;
	/**
	 * The process's current scope.
	 */
	private final ScopeData scopeData;

	/**
	 * Creates a new Case.
	 * 
	 * @param parser the parser to read the case's attributes and children
	 * from
	 * @param scopeData a description of the current scope
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	Case(XmlPullParser parser, ScopeData scopeData) throws IOException,
		XmlPullParserException, MalformedDocumentException
	{
		this.scopeData = scopeData;
		parser.require(XmlPullParser.START_TAG, BPELServer.namespace, "case");

		String conditionString = parser.getAttributeValue(null, "condition");
		if(conditionString == null)
			throw new MalformedBPELException(parser,
				"<case> must specify condition");
		// Read the condition string

		condition = ExpressionParser.parse(conditionString, parser, scopeData);
		parser.nextTag();
		// Parse it as an XPath expression

		activity = Activity.parse(parser, scopeData);
		// Parse the child activity

		parser.require(XmlPullParser.END_TAG, BPELServer.namespace, "case");
		parser.nextTag();
	}

	/**
	 * Evaluates whether or not the case should execute.
	 * 
	 * @param processInstance the current BPEL process instance
	 * @return whether or not the case should execute
	 * 
	 * @throws ExpressionEvaluationException an error occurred while evaluating
	 * the condition
	 */
	boolean matches(ProcessInstance processInstance)
		throws ExpressionEvaluationException
	{
		return condition.evaluateBoolean(scopeData, processInstance);
	}

	/**
	 * Creates a new instance of the &lt;case&gt;'s child activity.
	 * 
	 * @param processInstance information about the current process instance
	 * @return a {@link ActivityInstance} corresponding to the activity that
	 * should be executed in this case
	 */
	ActivityInstance newInstance(ProcessInstance processInstance)
	{
		return activity.newInstance(processInstance);
	}
}
