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

import edu.wustl.mobilab.sliver.soap.*;
import edu.wustl.mobilab.sliver.util.*;

/**
 * Represents and executes BPEL &lt;reply&gt; tags.
 * 
 * @author Greg Hackmann
 */
public class Reply extends Transaction
{
	/**
	 * The variable source of the message to send.
	 */
	protected VariableSpecification variableSpec;
	/**
	 * The BPEL fault source of the message to send.
	 */
	protected String faultName;
	/**
	 * Whether or not the {@link Channel} should be closed after the reply is
	 * sent.
	 */
	protected boolean closeConnection = true;
	
	/**
	 * Creates a new Reply.
	 * 
	 * @param parser the parser to read the reply's attributes from
	 * @param scopeData a description of the current scope
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	Reply(XmlPullParser parser, ScopeData scopeData)
		throws IOException, XmlPullParserException, MalformedDocumentException
	{
		this(parser, scopeData, BPELServer.namespace);
	}

	/**
	 * Creates a new Reply.
	 * 
	 * @param parser the parser to read the reply's attributes from
	 * @param scopeData a description of the current scope
	 * @param tagNamespace the namespace of the &lt;reply&gt; tag
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	protected Reply(XmlPullParser parser, ScopeData scopeData,
		String tagNamespace)
		throws IOException, XmlPullParserException, MalformedDocumentException
	{
		super(parser, scopeData, "reply", tagNamespace);
		parseStartTag();
		parseVariableSpec();

		parser.nextTag();
		parseStandardElements();

		// TODO: parse correlations
		
		parseEndTag();
		parser.nextTag();
	}
	
	/**
	 * Parses the <tt>variable</tt> and <tt>variableSpec</tt> attributes.
	 * 
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	protected void parseVariableSpec() throws MalformedDocumentException
	{
		variableSpec = getVariableSpecification("variable");
		faultName = getAttribute("faultName");
		// Save the variable and faultName attributes
		
		if(variableSpec == null && faultName == null)
			throw new MalformedBPELException(parser, "<" + type
				+ "> must specify variableSpec or faultName");
		// Complain if neither is specified
	}
	
	protected ActivityInstance newInstance(ProcessInstance processInstance)
	{
		return new ReplyInstance(processInstance);
	}
	
	/**
	 * Represents an executable instance of BPEL &lt;reply&gt; tags.
	 * 
	 * @author Greg Hackmann
	 */
	protected class ReplyInstance extends ActivityInstance
	{
		/**
		 * The {@link Channel} that is bound to the outgoing partner link.
		 */
		protected Channel activeChannel = null;

		/**
		 * Creates a new ReplyInstance.
		 * 
		 * @param processInstance the process instance that this activity
		 * instance is being executed in
		 */
		public ReplyInstance(ProcessInstance processInstance)
		{
			super(Reply.this, processInstance);
		}

		protected Signal executeImpl()
		{
			// TODO: throw invalidReply when incoming link isn't already bound

			BPELServer.log.info(this + ": waiting for " + partnerLink
				+ " to open");
			Channel binding = processInstance.getIncomingLinkBinding(this,
				partnerLink, false);

			if(binding == null)
			{
				String error = "Partner link " + partnerLink.getName()
					+ " unbound";
				BPELServer.log.severe(error);
				return new FaultedSignal(BPELServer.namespace, "invalidReply",
					new IOException(error));
			}
			Signal signal = sendReply(binding);
			// Send the reply to the partner link's binding

			return signal;
		}

		/**
		 * Sends the payload to a single {@link Channel}.
		 * 
		 * @param destination the {@link Channel} to send the payload over
		 * @return the {@link Signal} generated by this send operation
		 */
		protected Signal sendReply(Channel destination)
		{
			try
			{
				activeChannel = destination;
				// Get the partnerLink's Channel as soon as it's opened by a
				// <receive>

				if(state == State.CANCELING)
					return Signal.CANCELED;

				synchronized(activeChannel)
				{
					if(variableSpec != null)
					{
						activeChannel.sendElement(processInstance.getVariable(variableSpec));
						BPELServer.log.info(this + ": sent variable "
							+ variableSpec + " over " + destination);
					}
					// TODO: handle faultName properly

					if(closeConnection)
					{
						activeChannel.close();
						BPELServer.log.info(this + ": " + destination
							+ " closed");
					}
				}
				// Send the result, and close the channel if needed

				return Signal.COMPLETED;
			}
			catch(IOException e)
			{
				BPELServer.log.severe(this + ": IOException thrown: "
					+ e.getMessage());
				e.printStackTrace();

				if(state == State.CANCELING)
					return Signal.CANCELED;
				// Ignore the exception if it's caused by cancelling the
				// activity

				return new FaultedSignal(BPELServer.sliverNamespace,
					"ioException", e);
			}
			finally
			{
				if(closeConnection)
					closeConnection();
				// Mark the channel as inactive if needed
			}
		}

		/**
		 * Closes the active {@link Channel}.
		 */
		protected void closeConnection()
		{
			processInstance.removeActiveConnection(activeChannel);
			processInstance.unbindPartnerLink(partnerLink);
		}

		protected void cancelImpl()
		{
			activityThread.interrupt();
			// Interrupt the activity
			try
			{
				if(activeChannel != null)
					activeChannel.close();
			}
			catch(IOException e)
			{
				//
			}
			// Close the channel to stop blocking I/O operations
		}
	}
}