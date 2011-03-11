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
 * Represents and executes BPEL &lt;pick&gt; tags.
 * 
 * @author Greg Hackmann
 */
class Pick extends Activity
{
	/**
	 * Whether or not this &lt;pick&gt; can start a new process.
	 */
	final boolean createInstance;
	/**
	 * The &lt;onMessage&gt; child activities.
	 */
	final OnMessage [] onMessages;
	/**
	 * The &lt;onAlarm&gt; child activities.
	 */
	final OnAlarm [] onAlarms;
	
	/**
	 * Creates a new Pick.
	 * 
	 * @param parser the parser to read the pick's attributes and children
	 * from
	 * @param scopeData a description of the current scope
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	Pick(XmlPullParser parser, ScopeData scopeData) throws IOException,
		XmlPullParserException, MalformedDocumentException
	{
		super(parser, scopeData, "pick");
		parseStartTag();
		
		createInstance = "yes".equals(getAttribute("createInstance"));
		// Store the createInstance attribute
		
		parser.nextTag();
		parseStandardElements();
		
		Vector onMessageVector = new Vector();
		while(parser.getName().equals("onMessage"))
			onMessageVector.addElement(new OnMessage(parser, scopeData));
		// Parse all the <onMessage> child tags
		
		if(onMessageVector.isEmpty())
			throw new MalformedBPELException(parser,
				"<pick> must specify at least one <onMessage>");
		// Complain if there aren't any <onMessage>s

		Vector onAlarmVector = new Vector();
		while(parser.getName().equals("onAlarm"))
			onAlarmVector.addElement(new OnAlarm(parser, scopeData));
		// Parse all the <onAlarm> child tags

		if(createInstance && onAlarmVector.size() > 1)
			throw new MalformedBPELException(parser,
				"<pick> with createInstance=\"yes\" cannot specify <onAlarm>s");
		// Complain if there's an incompatible combination of createInstance
		// and onAlarm children
		
		onAlarms = new OnAlarm[onAlarmVector.size()];
		onAlarmVector.copyInto(onAlarms);
		
		onMessages = new OnMessage[onMessageVector.size()];
		onMessageVector.copyInto(onMessages);

		parseEndTag();
		parser.nextTag();
	}	

	Transaction [] getStartActivities()
	{
		if(createInstance)
			return onMessages;
		return new Receive[0];
		// Return the children if we're allowed to create an instance, otherwise
		// return nothing
	}
	
	protected ActivityInstance newInstance(ProcessInstance processInstance)
	{
		return new PickInstance(this, processInstance);
	}
}

/**
 * Represents an executable instance of BPEL &lt;pick&gt; tags.
 * 
 * @author Greg Hackmann
 */
class PickInstance extends ActivityInstance
{
	/**
	 * The event handler instance that is currently being executed.
	 */
	private ActivityInstance childInstance = null;
	/**
	 * The {@link PickThread} that is currently monitoring the events.
	 */
	private final PickThread thread;

	/**
	 * Creates a new PickInstance.
	 * 
	 * @param pick the {@link Pick} that will be executed
	 * @param processInstance the process instance that this activity instance is
	 * being executed in
	 */
	PickInstance(Pick pick, ProcessInstance processInstance)
	{
		super(pick, processInstance);
		thread = new PickThread(pick, processInstance);
	}

	protected Signal executeImpl()
	{
		thread.start();
		// Start a new thread to wait for an event to fire
	
		Activity fired;
		try
		{
			fired = thread.getFiredEvent();
		}
		catch(InterruptedException e)
		{
			if(state == State.CANCELING)
				return Signal.CANCELED;

			String error = toString() + ": interrupted before a child fired";
			BPELServer.log.severe(error);
			return new FaultedSignal(BPELServer.sliverNamespace,
				"interruptedException", new InterruptedException(error));
		}
		// Wait for the event to fire

		BPELServer.log.info(toString() + ": child " + fired
			+ " fired; executing child activity");
		
		childInstance = fired.newInstance(processInstance);
		return childInstance.execute();
		// Execute the fired event's child
	}
	
	protected synchronized void cancelImpl()
	{
		thread.cancel();
		if(childInstance != null)
			childInstance.cancel();
		// Cancel the PickThread, and cancel the child activity if it's
		// executing
	}
}
