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
 * Represents and executes BPEL &lt;onAlarm&gt; tags.
 * 
 * @author Greg Hackmann
 */
class OnAlarm extends Wait
{
	/**
	 * The activity that should be executed when the alarm fires.
	 */
	private Activity activity;

	/**
	 * Creates a new OnAlarm.
	 * 
	 * @param parser the parser to read the onAlarm's attributes and children
	 * from
	 * @param scopeData a description of the current scope
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	OnAlarm(XmlPullParser parser, ScopeData scopeData) throws IOException,
		XmlPullParserException, MalformedDocumentException
	{
		super(parser, scopeData, "onAlarm");
	}
	
	protected void parseStandardElements() throws IOException,
		XmlPullParserException, MalformedDocumentException
	{
		activity = Activity.parse(parser, scopeData);
		// Parse only the child activity
	}

	/**
	 * Gets the {@link WaitSpecification} that specifies how long to sleep.
	 * 
	 * @return the {@link WaitSpecification} that specifies how long to sleep
	 */
	WaitSpecification getWaitSpecification()
	{
		return waitSpec;
	}
	
	protected ActivityInstance newInstance(ProcessInstance processInstance)
	{
		return activity.newInstance(processInstance);
	}
}

/**
 * A thread that monitors the alarm specified by an {@link OnAlarm}.
 * 
 * @author Greg Hackmann
 */
class OnAlarmThread extends Thread
{
	/**
	 * Whether or not the alarm has fired.
	 */
	private boolean fired = false;
	/**
	 * The {@link WaitSpecification} that will actually sleep for the specified
	 * time.
	 */
	private final WaitSpecification waitSpec;
	/**
	 * The {@link EventThread} that should be pre-empted if an alarm fires.
	 */
	private final EventThread eventThread;
	/**
	 * The current process instance.
	 */
	private final ProcessInstance processInstance;
	
	/**
	 * Creates a new OnAlarmThread.
	 * 
	 * @param onAlarm the {@link OnAlarm} that this thread is executing
	 * @param eventThread the {@link EventThread} that should be pre-empted
	 * if an alarm fires
	 * @param processInstance the current process instance
	 */
	OnAlarmThread(OnAlarm onAlarm, EventThread eventThread,
		ProcessInstance processInstance)
	{
		super("OnAlarmThread");
		this.waitSpec = onAlarm.getWaitSpecification();
		this.eventThread = eventThread;
		this.processInstance = processInstance;
	}
	
	public void run()
	{
		try
		{
			waitSpec.sleep(processInstance);
			// Try to sleep until the alarm should fire
			if(eventThread != null)
				eventThread.cancel();
			fired = true;
			// If we're not interrupted, then pre-empt the pick thread (if
			// any) and note that the alarm fired
		}
		catch(InterruptedException e)
		{
			BPELServer.log.info("<onAlarm> was interrupted before it fired");
		}
		catch(ExpressionEvaluationException e)
		{
			BPELServer.log.severe("<onAlarm> could not be evaluated: " + e);
			FaultedSignal fault = new FaultedSignal(BPELServer.sliverNamespace,
				"expressionEvaluationException", e);
			processInstance.throwFault(fault);
		}
	}
	
	/**
	 * Gets whether or not the alarm has fired.
	 * 
	 * @return whether or not the alarm has fired
	 */
	boolean isFired()
	{
		return fired;
	}
}
