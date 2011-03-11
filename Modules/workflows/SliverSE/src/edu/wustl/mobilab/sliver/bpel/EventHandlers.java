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
 * Represents BPEL &lt;eventHandlers&gt; tags.
 * 
 * @author Greg Hackmann
 */
class EventHandlers
{
	/**
	 * The &lt;onMessage&gt; child activities.
	 */
	final OnMessage [] onMessages;
	/**
	 * The &lt;onAlarm&gt; child activities.
	 */
	final OnAlarm [] onAlarms;	
	
	/**
	 * Creates a new EventHandlers.
	 * 
	 * @param parser the parser to read the eventHandlers' attributes and
	 * children from
	 * @param scopeData a description of the current scope
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	EventHandlers(XmlPullParser parser, ScopeData scopeData)
		throws IOException, XmlPullParserException, MalformedDocumentException
	{
		if(!"eventHandlers".equals(parser.getName()))
		{
			onMessages = new OnMessage[0];
			onAlarms = new OnAlarm[0];
			return;
		}
		// If there's no <faultHandlers> block, then create an empty set
		// of events and return without parsing anything

		parser.require(XmlPullParser.START_TAG, BPELServer.namespace,
			"eventHandlers");
		parser.nextTag();

		Vector onMessageVector = new Vector();
		while(parser.getName().equals("onMessage"))
			onMessageVector.addElement(new OnMessage(parser, scopeData));
		// Parse all the <onMessage> child tags

		Vector onAlarmVector = new Vector();
		while(parser.getName().equals("onAlarm"))
			onAlarmVector.addElement(new OnAlarm(parser, scopeData));
		// Parse all the <onAlarm> child tags

		if(onMessageVector.isEmpty() && onAlarmVector.isEmpty())
			throw new MalformedBPELException(parser,
				"<eventHandlers> must specify at least one <onMessage> or <onAlarm>");
		// Complain if there aren't any events to handle
		
		onAlarms = new OnAlarm[onAlarmVector.size()];
		onAlarmVector.copyInto(onAlarms);
		
		onMessages = new OnMessage[onMessageVector.size()];
		onMessageVector.copyInto(onMessages);

		parser.require(XmlPullParser.END_TAG, BPELServer.namespace,
			"eventHandlers");
		parser.nextTag();
	}
		
	/**
	 * Enables the fault handler.
	 * 
	 * @param processInstance the BPEL process instance that the fault handler
	 * should be enabled for
	 * 
	 * @return the {@link EventThread} that will handle incoming events
	 */
	synchronized EventThread enable(ProcessInstance processInstance)
	{
		EventThread thread = new EventHandlersThread(this, processInstance);
		thread.start();
		return thread;
	}
	
	public String toString()
	{
		return "<eventHandlers>";
	}
}