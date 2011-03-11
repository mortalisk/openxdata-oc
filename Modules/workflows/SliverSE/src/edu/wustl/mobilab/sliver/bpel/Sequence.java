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
 * Represents and executes BPEL &lt;sequence&gt; tags.
 * 
 * @author Greg Hackmann
 */
class Sequence extends Activity
{
	/**
	 * A list of all the child activities.
	 */
	final Vector children = new Vector();
	
	/**
	 * Creates a new Sequence.
	 * 
	 * @param parser the parser to read the sequence's attributes and children
	 * from
	 * @param scopeData a description of the current scope
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	Sequence(XmlPullParser parser, ScopeData scopeData) throws IOException,
		XmlPullParserException, MalformedDocumentException
	{
		super(parser, scopeData, "sequence");
		parseStartTag();
		
		parser.nextTag();
		parseStandardElements();
		
		while(parser.getEventType() != XmlPullParser.END_TAG)
			children.addElement(Activity.parse(parser, scopeData));
		// Keep adding children until we reach the </sequence> tag
		
		if(children.isEmpty())
			throw new MalformedBPELException(parser,
				"<sequence> must specify at least one activity");
		// Complain if there's not at least one child
		
		parseEndTag();
		parser.nextTag();
	}

	Transaction [] getStartActivities()
	{
		Activity first = (Activity)children.firstElement();
		return first.getStartActivities();
		// Only the first activity in the sequence can have start activities
	}

	protected ActivityInstance newInstance(ProcessInstance processInstance)
	{
		return new SequenceInstance(this, processInstance);
	}
}

/**
 * Represents an executable instance of BPEL &lt;sequence&gt; tags.
 * 
 * @author Greg Hackmann
 */
class SequenceInstance extends ActivityInstance
{	
	/**
	 * An {@link Enumeration} of all unexecuted child activities.
	 */
	private final Enumeration activities;
	/**
	 * The instance of the currently-executing child activity.
	 */
	private ActivityInstance childInstance = null;

	/**
	 * Creates a new SequenceInstance.
	 * 
	 * @param sequence the {@link Sequence} that will be executed
	 * @param processInstance the process instance that this activity instance is
	 * being executed in
	 */
	SequenceInstance(Sequence sequence, ProcessInstance processInstance)
	{
		super(sequence, processInstance);
		this.activities = sequence.children.elements();
	}
	
	protected Signal executeImpl()
	{
		// The evaluation of activities.hasMoreElements() is moved into the
		// while loop so that we can lock access to the activities list
		// without keeping it locked for the entire lifespan of the loop.
		// (The alternatives are to wrap the entire while loop in a synchronized
		// block, which is not the behavior we want; or to put the evaluation
		// step in the while(...) declaration, which creates a race condition
		// if the activity is cancelled between evaluating the expression and
		// actually executing the loop.)
		
		// Until we exhaust children
		while(true)
		{
			synchronized(this)
			{
				if(!activities.hasMoreElements())
					return Signal.COMPLETED;
				// If the children have been exhausted, we're done
				
				Activity next = (Activity)activities.nextElement();
				BPELServer.log.info(toString() + ": executing child " + next);
				childInstance = next.newInstance(processInstance);
			}
			
			Signal signal = childInstance.execute();
			// Try to execute the next task
			
			synchronized(this)
			{
				// If the task fails
				if(signal instanceof FaultedSignal)
				{
					BPELServer.log.severe(toString() + ": child "
						+ childInstance + " threw a fault");
	
					cancelRemainingActivities();
					return signal;
				}
				
				// If we need to do dead path elimination
				if(signal == Signal.EXITED)
				{
					BPELServer.log.severe(toString() + ": child "
						+ childInstance + " exited without completing");
	
					cancelRemainingActivities();
					return signal;
					// Finally, pass along the failure
				}
			}
		}
	}
	
	/**
	 * Cancels the remaining activities in the sequence.
	 * All outgoing links are fired as <tt>false</tt> so that dead-path
	 * elimination can occur.
	 */
	private void cancelRemainingActivities()
	{
		// For all remaining child activities
		while(activities.hasMoreElements())
		{
			Activity activity = (Activity)activities.nextElement();
			try
			{
				activity.newInstance(processInstance).fireLinks(false);
				// Fire the activity's links as false
			}
			catch(ExpressionEvaluationException e)
			{
				// This can't possibly be thrown, since no expressions are
				// evaluated when the activity doesn't complete
			}
		}
	}
	
	protected synchronized void cancelImpl()
	{
		if(childInstance != null)
			childInstance.cancel();

		cancelRemainingActivities();
	}
}
