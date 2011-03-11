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
import edu.wustl.mobilab.sliver.xpath.*;

/**
 * Represents and executes BPEL &lt;switch&gt; tags.
 * 
 * @author Greg Hackmann
 */
class Switch extends Activity
{
	/**
	 * The {@link Case}s that this Switch considers.
	 */
	final Vector cases = new Vector();
	/**
	 * The Activity that should be executed if none of the cases are true.
	 */
	Activity otherwise = null;

	/**
	 * Creates a new Switch.
	 * 
	 * @param parser the parser to read the switch's attributes and children
	 * from
	 * @param scopeData a description of the current scope
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */	
	Switch(XmlPullParser parser, ScopeData scopeData) throws IOException,
		XmlPullParserException, MalformedDocumentException
	{
		super(parser, scopeData, "switch");
		parseStartTag();

		parser.nextTag();
		parseStandardElements();

		while(parser.getName().equals("case"))
			cases.addElement(new Case(parser, scopeData));
		// Collect all the <case> child tags

		if(cases.isEmpty())
			throw new MalformedBPELException(parser,
				"<switch> must specify at least one <case>");
		// Make sure there's at least one <case>

		if(parser.getName().equals("otherwise"))
		{
			parser.require(XmlPullParser.START_TAG, BPELServer.namespace,
							"otherwise");
			parser.nextTag();

			otherwise = Activity.parse(parser, scopeData);

			parser.require(XmlPullParser.END_TAG, BPELServer.namespace,
							"otherwise");
			parser.nextTag();
		}
		// If there's an <otherwise> activity, then parse it

		parseEndTag();
		parser.nextTag();
	}

	protected ActivityInstance newInstance(ProcessInstance processInstance)
	{
		return new SwitchInstance(this, processInstance);
	}
}

/**
 * Represents an executable instance of BPEL &lt;switch&gt; tags.
 * 
 * @author Greg Hackmann
 */
class SwitchInstance extends ActivityInstance
{
	/**
	 * The {@link Switch} that will be executed.
	 */
	private final Switch switchh;
	/**
	 * The {@link Case} that is currently executing.
	 */
	private ActivityInstance selectedCase = null;

	/**
	 * Creates a new SwitchInstance.
	 * 
	 * @param switchh the {@link Switch} that will be executed
	 * @param processInstance the process instance that this activity instance is
	 * being executed in
	 */
	SwitchInstance(Switch switchh, ProcessInstance processInstance)
	{
		super(switchh, processInstance);
		this.switchh = switchh;
	}

	protected Signal executeImpl()
	{
		// This whole section is synchronized so the activity can't be
		// cancelled while it's being set up
		synchronized(this)
		{
			Enumeration e = switchh.cases.elements();
			// For each <case>
			while(e.hasMoreElements())
			{
				Case next = (Case)e.nextElement();
				BPELServer.log.info(toString() + ": evaluating " + next);
				try
				{
					if(next.matches(processInstance))
					{
						BPELServer.log.info(toString() +
							": case matches; executing");
						selectedCase = next.newInstance(processInstance);
					}
					// If the case matches, select it
				}
				catch(ExpressionEvaluationException ex)
				{
					BPELServer.log.severe(toString() +
						": exception thrown while evaluating case: "
						+ ex.toString());
					ex.printStackTrace();
					
					return new FaultedSignal(BPELServer.namespace,
						"expressionEvaluationException", ex);
				}
			}
			if(selectedCase == null && switchh.otherwise != null)
			{
				BPELServer.log.info(toString() + ": executing otherwise case");
				selectedCase = switchh.otherwise.newInstance(processInstance);
			}
			// Try the <otherwise> case if none of the above were selected
			
			if(selectedCase == null)
			{
				BPELServer.log.info(toString() + ": executing empty otherwise case");
				return Signal.COMPLETED;
			}
			// As a last resort, do nothing
		}
		
		return selectedCase.execute();
		// If a <case> was selected, then execute it
	}
	
	protected synchronized void cancelImpl()
	{
		if(selectedCase != null)
			selectedCase.cancel();
		// If a <case> is executing, then cancel it
	}	
}
