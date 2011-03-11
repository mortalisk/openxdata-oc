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
 * Represents and executes BPEL &lt;onMessage&gt; tags. (This class extends
 * {@link Receive} because they share a lot of code, not because they are
 * directly semantically related.)
 * 
 * @author Greg Hackmann
 */
class OnMessage extends Receive
{
	/**
	 * The activity that should be executed when the message is received.
	 */
	Activity activity;
	
	/**
	 * Creates a new OnMessage.
	 * 
	 * @param parser the parser to read the onMessage's attributes and children
	 * from
	 * @param scopeData a description of the current scope
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	OnMessage(XmlPullParser parser, ScopeData scopeData) throws IOException,
		XmlPullParserException, MalformedDocumentException
	{
		super(parser, scopeData, "onMessage");
	}
	
	protected void parseBody() throws IOException, XmlPullParserException,
		MalformedDocumentException
	{
		super.parseBody();
		activity = Activity.parse(parser, scopeData);
		// As part of the body, also parse the child tag
	}
	
	protected ActivityInstance newInstance(ProcessInstance processInstance)
	{
		return new OnMessageInstance(this, processInstance);
	}
}

/**
 * Represents an executable instance of BPEL &lt;onMessage&gt; tags.
 * 
 * @author Greg Hackmann
 */
class OnMessageInstance extends ReceiveInstance
{
	/**
	 * The activity that should be executed when the message is received.
	 */
	private final Activity activity;
	/**
	 * The event handler instance that is currently being executed.
	 */
	private ActivityInstance childInstance = null;

	/**
	 * Creates a new OnMessageInstance.
	 * 
	 * @param onMessage the {@link OnMessage} that will be executed
	 * @param processInstance the process instance that this activity instance is
	 * being executed in
	 */
	OnMessageInstance(OnMessage onMessage, ProcessInstance processInstance)
	{
		super(onMessage, processInstance);
		this.activity = onMessage.activity;
	}
	
	protected Signal executeImpl()
	{
		Signal signal = super.executeImpl();
		if(signal != Signal.COMPLETED)
			return signal;
		// Receive the message
		
		synchronized(this)
		{
			if(state == State.CANCELING)
				return Signal.CANCELED;
			
			childInstance = activity.newInstance(processInstance);
		}
		return childInstance.execute();
		// Create and execute the child instance if the activity wasn't
		// cancelled
	}
	
	protected synchronized void cancelImpl()
	{
		super.cancelImpl();
		if(childInstance != null)
			childInstance.cancel();
		// Cancel this activity, and the child if applicable
	}
}
