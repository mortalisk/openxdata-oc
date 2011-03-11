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

/**
 * Represents and executes BPEL &lt;flow&gt; tags.
 * 
 * @author Greg Hackmann
 */
class Flow extends Activity
{
	/**
	 * The new links declared within the &lt;flow&gt;'s scope.
	 */
	final Links links;
	/**
	 * A list of all the child activities.
	 */
	final Vector children = new Vector();
	
	/**
	 * Creates a new Flow.
	 * 
	 * @param parser the parser to read the flow's attributes and children
	 * from
	 * @param scopeData a description of the current scope
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	Flow(XmlPullParser parser, ScopeData scopeData) throws IOException,
		XmlPullParserException, MalformedDocumentException
	{
		super(parser, scopeData, "flow");
		parseStartTag();
		
		parser.nextTag();
		parseStandardElements();
		
		links = new Links(parser);
		// Parse the new links
		
		ScopeData newScopeData;
		if(links.isEmpty())
			newScopeData = scopeData;
		// If there aren't any, then reuse the old scope
		else
		{
			newScopeData = scopeData.createNestedScope();
			newScopeData.setLinks(links);
		}
		// Otherwise, copy the old scope, and add the new links to the copy
		
		while(parser.getEventType() != XmlPullParser.END_TAG)
			children.addElement(Activity.parse(parser, newScopeData));
		// Parse the children activities
		
		if(children.isEmpty())
			throw new MalformedBPELException(parser,
				"<flow> must specify at least one activity");
		// Complain if there are no children
		
		parseEndTag();
		parser.nextTag();
	}
	
	protected ActivityInstance newInstance(ProcessInstance processInstance)
	{
		return new FlowInstance(this, processInstance);
	}
	
	Transaction [] getStartActivities()
	{
		Vector startActivities = new Vector();
		
		Enumeration e = children.elements();
		// For each child activity
		while(e.hasMoreElements())
		{
			Activity next = (Activity)e.nextElement();
			Transaction [] nextStart = next.getStartActivities();
			for(int i = 0; i < nextStart.length; i++)
				startActivities.addElement(nextStart[i]);
			// Add its start activities
		}
		
		Transaction [] startActivitiesArray = new Transaction[startActivities.size()];
		startActivities.copyInto(startActivitiesArray);
		return startActivitiesArray;
		// Return the start activities in array form
	}
}

/**
 * Represents an executable instance of BPEL &lt;flow&gt; tags.
 * 
 * @author Greg Hackmann
 */
class FlowInstance extends ActivityInstance
{
	/**
	 * The {@link Flow} that will be executed.
	 */
	private final Flow flow;
	
	/**
	 * The {@link FlowThread}s that execute the child activities.
	 */
	private final FlowThread [] childThreads;
	/**
	 * The number of active child threads.
	 */
	private int numActiveChildren;
	/**
	 * The aggregation of all the signals received from the child activities.
	 */
	private Signal aggregateSignal = Signal.COMPLETED;
	
	/**
	 * Creates a new FlowInstance.
	 * 
	 * @param flow the {@link Flow} that will be executed
	 * @param processInstance the process instance that this activity instance is
	 * being executed in
	 */
	FlowInstance(Flow flow, ProcessInstance processInstance)
	{
		super(flow, processInstance);
		this.flow = flow;
		
		childThreads = new FlowThread[flow.children.size()];
		// For each child
		for(int i = 0; i < childThreads.length; i++)
		{
			Activity activity = (Activity)flow.children.elementAt(i);
			String threadName = this + " child " + activity + " (" + i + ")";
			
			ActivityInstance childInstance = activity.newInstance(processInstance);
			childThreads[i] = new FlowThread(childInstance, this, threadName);
			// Create a new thread containing that child
		}
	}

	Signal execute()
	{
		flow.links.createConcreteLinks(processInstance);
		return super.execute();
		// Set up the new links before executing the process
	}
	
	protected synchronized Signal executeImpl()
	{
		// This method is synchronized so that cancellation can only occur
		// while there are active threads.
		
		if(state == State.CANCELING)
			return Signal.CANCELED;
		
		numActiveChildren = childThreads.length;
		for(int i = 0; i < childThreads.length; i++)
			childThreads[i].start();
		// Start all the threads

		while(numActiveChildren > 0)
		{
			try
			{
				wait();
			}
			catch(InterruptedException e)
			{
				//
			}
		}
		// Wait until no children are active
		
		return aggregateSignal;
		// Return the combined signal
	}
	
	/**
	 * Collects a signal from one of the child {@link FlowThread}
	 * 
	 * @param signal the signal being sent from the child
	 */
	synchronized void signal(Signal signal)
	{
		// If one of the children didn't complete, and we haven't already
		// noted a failure
		if(signal instanceof FaultedSignal)
		{
			aggregateSignal = signal;
			// Update the aggregate signal to note the failure
			
			for(int i = 0; i < childThreads.length; i++)
				childThreads[i].cancel();
			// Cancel the rest of the children
		}
		
		numActiveChildren--;
		notifyAll();
		// Mark one less child as active
	}
	
	protected synchronized void cancelImpl()
	{
		for(int i = 0; i < childThreads.length; i++)
			childThreads[i].cancel();
		// Pass the cancellation signal along to all the child threads
	}
}

/**
 * Executes one of a {@link Flow}'s child activities.
 * 
 * @author Greg Hackmann
 */
class FlowThread extends Thread
{
	/**
	 * The activity that should be executed
	 */
	private final ActivityInstance child;
	/**
	 * The {@link FlowInstance} that spawned this thread.
	 */
	private final FlowInstance parent;
	
	/**
	 * Creates a new FlowThread.
	 * 
	 * @param child the activity that should be executed
	 * @param parent the {@link FlowInstance} that spawned this thread
	 * @param name the thread's name
	 */
	FlowThread(ActivityInstance child, FlowInstance parent, String name)
	{
		super(name);
		this.child = child;
		this.parent = parent;
	}
	
	public void run()
	{
		parent.signal(child.execute());
		// Execute the child, and send its signal to the parent instance
	}
	
	/**
	 * Cancels the child activity.
	 */
	void cancel()
	{
		child.cancel();
	}
}
