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

/**
 * Represents and executes BPEL &lt;scope&gt; tags.
 * 
 * @author Greg Hackmann
 */
class Scope extends Activity
{
	/**
	 * A list of all the child activities.
	 */
	final Activity child;
	/**
	 * A list of all local variables.
	 */
	private final Variables variables;
	/**
	 * A list of all local correlation sets.
	 */
	private final CorrelationSets correlationSets;
	/**
	 * A list of all local fault handlers.
	 */
	final FaultHandlers faultHandlers;
	/**
	 * A list of all local event handlers.
	 */
	final EventHandlers eventHandlers;
	
	
	/**
	 * Creates a new Scope.
	 * 
	 * @param parser the parser to read the scope's attributes and children
	 * from
	 * @param parentScopeData a description of the parent scope
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	Scope(XmlPullParser parser, ScopeData parentScopeData) throws IOException,
		XmlPullParserException, MalformedDocumentException
	{
		super(parser, parentScopeData.createNestedScope(), "scope");
		parseStartTag();
		scopeData.setName(name);
		
		parser.nextTag();
		parseStandardElements();
		
		variables = new Variables(parser);
		scopeData.setVariables(variables);
		// Parse and update the local variables
		correlationSets = new CorrelationSets(parser);
		scopeData.setCorrelationSets(correlationSets);
		// Parse and update the local correlation sets
		faultHandlers = new FaultHandlers(parser, scopeData);
//		this.scopeData.setCatches(faultHandlers.getCatches());
//		this.scopeData.setCatchAll(faultHandlers.getCatchAll());
		// Parse and update the local fault handlers

		// TODO: parse compensation handler
		eventHandlers = new EventHandlers(parser, scopeData);
		// Parse and update the local event handlers
		
		child = Activity.parse(parser, scopeData);
		// Parse the child activity
		
		parseEndTag();
		parser.nextTag();
	}
	
	protected ActivityInstance newInstance(ProcessInstance processInstance)
	{
		return new ActivityInstance(this, processInstance)
		{
			private final ActivityInstance childInstance =
				child.newInstance(this.processInstance);
			private ActivityInstance catchInstance;
			
			protected Signal executeImpl()
			{
				EventThread eventThread = eventHandlers.enable(this.processInstance);
				Signal signal = childInstance.execute();
				eventThread.disable();
				// Enable the event handlers, execute the child, and disable
				// the event handlers
				
				synchronized(this)
				{
					if(!(signal instanceof FaultedSignal))
						return signal;
					// Return the signal if it wasn't a fault
				
					FaultedSignal fault = ((FaultedSignal)signal);
					catchInstance = faultHandlers.getHandler(fault,
						this.processInstance);
					// If the signal was a fault, try to catch it
					
					if(catchInstance == null)
						return signal;
					// If there's no handler, pass along the fault
				}
				
				return catchInstance.execute();
				// If there was a handler, then execute the child handler,
				// and return its signal
			}
			
			protected synchronized void cancelImpl()
			{
				childInstance.cancel();
				if(catchInstance != null)
					catchInstance.cancel();
				// Cancel the child activity, and also cancel the fault handler
				// if it's executing
			}
		};
	}
}