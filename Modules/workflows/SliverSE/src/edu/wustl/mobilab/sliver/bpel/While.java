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
 * Represents and executes BPEL &lt;while&gt; tags.
 * 
 * @author Greg Hackmann
 */
class While extends Activity
{
	/**
	 * The {@link Activity} that the loop should repeatedly execute. 
	 */
	final Activity activity;
	/**
	 * The {@link Expression} that determines when the loop should terminate.
	 */
	final Expression condition;

	/**
	 * Creates a new While.
	 * 
	 * @param parser the parser to read the while's attributes and children
	 * from
	 * @param scopeData a description of the current scope
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	While(XmlPullParser parser, ScopeData scopeData) throws IOException,
		XmlPullParserException, MalformedDocumentException
	{
		super(parser, scopeData, "while");
		parseStartTag();

		String conditionString = parser.getAttributeValue(null, "condition");
		if(conditionString == null)
			throw new MalformedBPELException(parser,
				"<while> must specify condition");

		condition = ExpressionParser.parse(conditionString, parser, scopeData);
		// Parse the condition

		parser.nextTag();
		parseStandardElements();

		activity = Activity.parse(parser, scopeData.createNestedScope(false));
		// Parse the child activity

		parseEndTag();
		parser.nextTag();
	}
	
	protected ActivityInstance newInstance(ProcessInstance processInstance)
	{
		return new WhileInstance(this, processInstance);
	}
}

/**
 * Represents an executable instance of BPEL &lt;while&gt; tags.
 * 
 * @author Greg Hackmann
 */
class WhileInstance extends ActivityInstance
{
	/**
	 * The {@link While} that will be executed.
	 */
	private final While wile;
	/**
	 * The current instance of the loop's body.
	 */
	private ActivityInstance childInstance = null;

	/**
	 * Creates a new WhileInstance.
	 * 
	 * @param wile the {@link While} that will be executed
	 * @param processInstance the process instance that this activity instance is
	 * being executed in
	 */
	public WhileInstance(While wile, ProcessInstance processInstance)
	{
		super(wile, processInstance);
		this.wile = wile;
	}

	protected Signal executeImpl()
	{
		BPELServer.log.info(toString() + ": evaluating condition "
			+ wile.condition);
		try
		{
			// As long as the condition is true
			while(wile.condition.evaluateBoolean(wile.scopeData,
				processInstance))
			{
				synchronized(this)
				{
					if(state == State.CANCELING)
						return Signal.CANCELED;
					
					BPELServer.log.info(toString()
						+ ": condition was true; executing child "
						+ wile.activity);
	
					childInstance = wile.activity.newInstance(processInstance);
				}				
				Signal signal = childInstance.execute();
				childInstance = null;
				// Execute the child
				
				// TODO: what if signal is Signal.EXITED?
				if(signal != Signal.COMPLETED)
				{
					BPELServer.log.severe(toString()
						+ ": child activity did not complete");
					return signal;
				}
				// Complain if the child didn't complete successfully
			}
		}
		catch(ExpressionEvaluationException e)
		{
			BPELServer.log.severe(toString()
				+ ": exception thrown while evaluating condition: "
				+ e.toString());
			e.printStackTrace();

			return new FaultedSignal(BPELServer.sliverNamespace,
				"expressionEvaluationException", e);
		}
		// Complain if we can't evaluate the condition

		BPELServer.log.info(toString() + ": condition was false");
		return Signal.COMPLETED;
	}
	
	protected synchronized void cancelImpl()
	{
		if(childInstance != null)
			childInstance.cancel();
		// If the body of the loop is executing, cancel it
	}
}
