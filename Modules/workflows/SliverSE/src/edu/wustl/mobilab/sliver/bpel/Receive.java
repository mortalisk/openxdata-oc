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

import org.ksoap2.*;
import org.kxml2.kdom.*;
import org.xmlpull.v1.*;

import edu.wustl.mobilab.sliver.soap.*;
import edu.wustl.mobilab.sliver.util.*;

/**
 * Represents and executes BPEL &lt;receive&gt; tags.
 * 
 * @author Greg Hackmann
 */
class Receive extends Transaction
{
	/**
	 * The variable that the message will be stored in.
	 */
	final VariableSpecification variableSpec;
	/**
	 * Whether or not this &lt;receive&gt; can start a new process.
	 */
	protected boolean createInstance = false;
	
	/**
	 * Creates a new Receive.
	 * 
	 * @param parser the parser to read the receive's attributes from
	 * @param scopeData a description of the current scope
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	Receive(XmlPullParser parser, ScopeData scopeData) throws IOException,
		XmlPullParserException, MalformedDocumentException
	{
		this(parser, scopeData, "receive");
	}

	/**
	 * Creates a new Receive with a specified tag name.
	 * 
	 * @param parser the parser to read the receive's attributes from
	 * @param scopeData a description of the current scope
	 * @param tagName the name of the tag (usually "receive")
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 * 
	 * @see OnMessage#OnMessage(XmlPullParser, ScopeData)
	 */
	protected Receive(XmlPullParser parser, ScopeData scopeData,
		String tagName) throws IOException, XmlPullParserException,
		MalformedDocumentException
	{
		super(parser, scopeData, tagName);
		parseStartTag();

		variableSpec = getVariableSpecification("variable");
		createInstance = "yes".equals(getAttribute("createInstance"));
		// Store the variableSpec and createInstance attributes

		parser.nextTag();
		parseBody();
		
		parseEndTag();
		parser.nextTag();
	}
	
	/**
	 * Parses the Receive's body.
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	protected void parseBody() throws IOException, XmlPullParserException,
		MalformedDocumentException
	{
		parseStandardElements();
		parseCorrelations(Correlation.IN);	
		// Parse the standard parts, then the correlations
	}

	Transaction [] getStartActivities()
	{
		if(createInstance)
			return new Transaction [] { this };
		return new Transaction[0];
		// Return this if we're allowed to create an instance, otherwise
		// return nothing
	}
	
	protected ActivityInstance newInstance(ProcessInstance processInstance)
	{
		return new ReceiveInstance(this, processInstance);
	}
}

/**
 * Represents an executable instance of BPEL &lt;receive&gt; tags.
 * 
 * @author Greg Hackmann
 */
class ReceiveInstance extends ActivityInstance
{
	/**
	 * The {@link Receive} that will be executed.
	 */
	private final Receive receive;
	/**
	 * The {@link Channel} that is bound to the incoming partner link.
	 */
	private Channel channel = null;

	/**
	 * Creates a new ReceiveInstance.
	 * 
	 * @param receive the {@link Receive} that will be executed
	 * @param processInstance the process instance that this activity instance is
	 * being executed in
	 */
	ReceiveInstance(Receive receive, ProcessInstance processInstance)
	{
		super(receive, processInstance);
		this.receive = receive;
	}
	
	protected Signal executeImpl()
	{
		try
		{
			synchronized(processInstance)
			{
				processInstance.enablePartnerLink(receive.partnerLink);
				channel = processInstance.getIncomingLinkBinding(this,
					receive.partnerLink, true);
				processInstance.disablePartnerLink(receive.partnerLink);
			}
			// Enable the partner link, get the binding, and then disable it
			// again
				
			if(state == State.CANCELING)
				return Signal.CANCELED;
			
			Element returnValue = channel.receiveElement();
			BPELServer.log.info(toString() + ": received remote value");
			if(receive.variableSpec != null)
				processInstance.setVariable(receive.variableSpec, returnValue);
			// Receive the message from the partner link and update the
			// variable (if applicable)
			
			return Signal.COMPLETED;			
		}
		catch(IOException e)
		{
			if(state == State.CANCELING)
				return Signal.CANCELED;
			// Ignore the exception if it was caused by cancelling the activity
			
			BPELServer.log.severe(toString() + ": exception " + e +
				" thrown while receiving value");
			e.printStackTrace();
			
			return new FaultedSignal(BPELServer.sliverNamespace, "ioException",
				e);
		}
		catch(MustUnderstandException e)
		{
			BPELServer.log.severe(toString() + ": exception " + e +
				" thrown while receiving value");
			e.printStackTrace();
			
			return new FaultedSignal(SoapEnvelope.ENV, "mustUnderstand", e);
		}
		catch(XmlPullParserException e)
		{
			BPELServer.log.severe(toString() + ": exception " + e +
				" thrown while receiving value");
			e.printStackTrace();
			
			return new FaultedSignal(BPELServer.sliverNamespace,
				"xmlPullParserException", e);
		}
		// Handle all the possible exceptions by throwing faults.  IOExceptions
		// might be caused by cancelling the activity, so check for that too.
	}
	
	protected void cancelImpl()
	{
		activityThread.interrupt();
		// Interrupt the activity
		try
		{
			if(channel != null)
				channel.close();
		}
		catch(IOException e)
		{
			//
		}
		// Close the channel to stop blocking I/O operations
	}
}
