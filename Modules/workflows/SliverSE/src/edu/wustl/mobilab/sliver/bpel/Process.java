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

import org.kxml2.io.*;
import org.kxml2.kdom.*;
import org.xmlpull.v1.*;

import edu.wustl.mobilab.sliver.soap.*;
import edu.wustl.mobilab.sliver.util.*;
import edu.wustl.mobilab.sliver.util.Queue;
import edu.wustl.mobilab.sliver.xpath.*;

/**
 * Represents, and executes a BPEL process and manages all of its instances.
 * 
 * @author Greg Hackmann
 */
public class Process
{
	/**
	 * The process's name.
	 */
	private final String name;
	/**
	 * The process's target namespace.
	 */
	private final String targetNamespace;
	/**
	 * Whether or not join failures should be ignored.
	 */
	private final boolean suppressJoinFailure;

	/**
	 * The &lt;partnerLinks&gt; child tag.
	 */
	private final PartnerLinks partnerLinks;
	/**
	 * The &lt;partners&gt; child tag.
	 */
	private final Partners partners;
	/**
	 * The &lt;variables&gt; child tag.
	 */
	private final Variables variables;
	/**
	 * The &lt;correlationSets&gt; child tag.
	 */
	private final CorrelationSets correlationSets;
	/**
	 * The &lt;faultHandlers&gt; child tag.
	 */
	final FaultHandlers faultHandlers;
	/**
	 * The &lt;eventHandlers&gt; child tag.
	 */
	final EventHandlers eventHandlers;
	/**
	 * The process's "root" activity.
	 */
	final Activity activity;
	/**
	 * All of the activities that can create a new process.
	 */
	private final Transaction [] startActivities;
	
	/**
	 * A list of process instances that are currently running.
	 */
	final Vector activeInstances = new Vector();
	
	/**
	 * Parses a BPEL process.
	 * 
	 * @param parser the parser to read the BPEL process from
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	Process(XmlPullParser parser)
		throws XmlPullParserException, IOException, MalformedDocumentException
	{
		parser.require(XmlPullParser.START_TAG, BPELServer.namespace, "process");
		// Read the opening tag

		name = parser.getAttributeValue(null, "name");
		if(name == null)
			throw new MalformedBPELException(parser,
				"<process> must specify name");
		targetNamespace = parser.getAttributeValue(null, "targetNamespace");
		if(targetNamespace == null)
			throw new MalformedBPELException(parser,
				"<process> must specify targetNamespace");
		// Read the process's name and target namespace
		
		String queryLanguage = parser.getAttributeValue(null, "queryLanguage");
		if(queryLanguage != null && !queryLanguage.equals(Expression.uri))
			throw new MalformedBPELException(parser, "queryLanguage "
				+ queryLanguage + " not supported");
		// Read the queryLanguage and complain if it's not XPath
		
		String expressionLanguage = parser.getAttributeValue(null,
			"expressionLanguage");
		if(expressionLanguage != null
			&& !expressionLanguage.equals(Expression.uri))
			throw new MalformedBPELException(parser, "expressionLanguage "
				+ expressionLanguage + " not supported");
		// Read the expressionLanguage and complain if it's not XPath

		suppressJoinFailure = "yes".equals(parser.getAttributeValue(null,
			"suppressJoinFailure"));
		// Read whether or not to ignore join failures
		
		// TODO: enableInstanceCompensation
		// TODO: abstractProcess
				
		parser.nextTag();

		partnerLinks = new PartnerLinks(parser);
		// Read the <partnerLinks> child
		partners = new Partners(parser);
		// Read the <partners> child
		variables = new Variables(parser);
		// Read the <variables> child
		correlationSets = new CorrelationSets(parser);
		// Read the <correlationSets> child
		ScopeData scopeData = new ScopeData(name, partnerLinks, partners,
			variables, correlationSets, suppressJoinFailure);
		// Create a new scope with the scope data we've read so far
		faultHandlers = new FaultHandlers(parser, scopeData);
		// Read the <faultHandlers> child

		// TODO: parse compensation handler
		eventHandlers = new EventHandlers(parser, scopeData);
		// Read the <eventHandlers> child
		
		activity = Activity.parse(parser, scopeData);
		startActivities = activity.getStartActivities();
		// Read the root activity
		
		parser.require(XmlPullParser.END_TAG, BPELServer.namespace, "process");
		// Read the closing tag
	}
	
	/**
	 * Parses a BPEL process.
	 * 
	 * @param in an {@link InputStream} containing the process's BPEL
	 * specification
	 * @return a {@link Process} representing the parsed process
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	public static Process parse(InputStream in)
		throws XmlPullParserException, IOException, MalformedDocumentException
	{
		XmlPullParser parser = new KXmlParser();
		parser.setInput(in, null);
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
		// Create the XML parser
		
		parser.require(XmlPullParser.START_DOCUMENT, null, null);
		parser.nextTag();
		// Parse the start of the document

		Process process = new Process(parser);
		// Parse the process
		
		parser.next();
		parser.require(XmlPullParser.END_DOCUMENT, null, null);	
		// Parse the end of the document
		
		return process;
	}
	
	/**
	 * Gets the process's name.
	 * 
	 * @return the process's name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the process's target namespace.
	 * 
	 * @return the process's target namespace
	 */
	public String getTargetNamespace()
	{
		return targetNamespace;
	}
	
	/**
	 * Gets the process's partner links.
	 * 
	 * @return the process's partner links
	 */
	public PartnerLinks getPartnerLinks()
	{
		return partnerLinks;
	}
	
	/**
	 * Executes the BPEL process.
	 * 
	 * @param server the {@link BPELServer} that is hosting the process
	 * @param request a DOM representation of the incoming request message
	 * @param channel the {@link Channel} that the message arrived from
	 */
	void execute(BPELServer server, Element request, Channel channel)
	{
		String namespace = request.getNamespace(), operation = request.getName();
		// Get the incoming request's name and namespace
		
		String partnerLinkName = server.getIncomingLink(namespace, operation);
		// Find out which partner link the request corresponds to
		if(partnerLinkName == null)
		{
			String error = "No partner link defined for operation " + operation;
			BPELServer.log.severe(error);
			IllegalArgumentException e = new IllegalArgumentException(error);
			BPELServer.sendFault(new FaultedSignal(BPELServer.sliverNamespace,
				"noPartnerLink", e), channel);
			// Complain if there's no corresponding partner link
		}
		
		// TODO: first, check active instances for correlations

		// For each start activity
		for(int i = 0; i < startActivities.length; i++)
		{
			// If the start activity's incoming partner link matches
			if(startActivities[i].partnerLink.getName().equals(partnerLinkName))
			{
				BPELServer.log.info("Incoming request matches partner link for "
					+ startActivities[i]);
				
				BufferedChannel bufferedChannel =
					new BufferedChannel(channel, request, partnerLinkName);
				ProcessInstance instance = new ProcessInstance(this,
					startActivities[i], bufferedChannel,
					server);
				bufferedChannel.start(instance);
				instance.start();
				// Create a new process instance and bind the incoming request
				// to the appropriate partner link
				
				return;
			}
		}
		
		Enumeration e = activeInstances.elements();
		// For each active instance
		while(e.hasMoreElements())
		{
			ProcessInstance processInstance = (ProcessInstance)e.nextElement();
			// If the corresponding partner link is enabled 
			if(processInstance.isPartnerLinkEnabled(partnerLinkName))
			{
				BPELServer.log.info("Incoming request matches enabled partner link for existing instance");
				BufferedChannel bufferedChannel =
					new BufferedChannel(channel, request, partnerLinkName);
				bufferedChannel.start(processInstance);
				processInstance.addActiveConnection(bufferedChannel);
				processInstance.newConnection(partnerLinkName,
					bufferedChannel);
	
				return;
				// Bind the partner link to the incoming request
			}
		}
		
		String error = "No start activity or active instance found for partner link " +
			partnerLinkName;
		BPELServer.log.severe(error);
		IllegalArgumentException ex = new IllegalArgumentException(error);
		BPELServer.sendFault(new FaultedSignal(BPELServer.sliverNamespace,
			"noActiveInstance", ex), channel);
		// Complain if no matching start activity was found
	}
}

/**
 * A special {@link Channel} which buffers incoming request messages. Using
 * this, the BPEL server can inspect messages, without the {@link Receive}
 * activity being aware that the message has already been consumed.
 * 
 * @author Greg Hackmann
 */
class BufferedChannel extends Channel implements Runnable
{
	/**
	 * The incoming request queue.
	 */
	private final Queue requestQueue = new Queue();
	/**
	 * The {@link Channel} which represents the incoming connection.
	 */
	private final Channel channel;
	/**
	 * The name of the partner link which accepted this connection.
	 */
	private final String partnerLinkName;
	/**
	 * The {@link ProcessInstance} that "owns" this connection.
	 */
	private ProcessInstance processInstance;
	
	/**
	 * Creates a new BufferedChannel.
	 * 
	 * @param channel the {@link Channel} which represents the incoming
	 * connection
	 * @param initialRequest the first request which arrived from the connection
	 * @param partnerLinkName the name of the partner link which accepted the
	 * connection
	 */
	BufferedChannel(Channel channel, Element initialRequest, String partnerLinkName)
	{
		this.channel = channel;
		this.partnerLinkName = partnerLinkName;
		requestQueue.push(initialRequest);
	}
	
	/**
	 * Starts monitoring the connection for incoming messages.
	 * 
	 * @param instance the {@link ProcessInstance} that should be
	 * notified when new requests arrive
	 */
	void start(ProcessInstance instance)
	{
		this.processInstance = instance;
		new Thread(this, toString()).start();
	}
	
	public void closeImpl() throws IOException
	{
		channel.close();
		requestQueue.cancel();
	}

	public Object receiveObject() throws IOException, XmlPullParserException,
		MustUnderstandException
	{
		return null;
		// Note that this will never be called, since <receive> and <invoke>
		// only call receiveElement()
	}

	public Element receiveElement() throws IOException, XmlPullParserException,
		MustUnderstandException
	{
		Object next = requestQueue.pop();
		// Get the next item off the queue
		
		if(next instanceof IOException)
			throw (IOException)next;
		if(next instanceof XmlPullParserException)
			throw (XmlPullParserException)next;
		if(next instanceof MustUnderstandException)
			throw (MustUnderstandException)next;
		// If it's an exception, then re-throw it
		
		return (Element)next;
		// Otherwise, return the message we popped
	}

	public void sendObject(Object object) throws IOException
	{
		channel.sendObject(object);
	}

	public void sendElement(Element element) throws IOException
	{
		channel.sendElement(element);
	}
	
	public String toString()
	{
		return channel.toString();
	}
	
	public void run()
	{
		// As long as the channel is open
		while(!channel.isClosed())
		{
			try
			{
				Element received = channel.receiveElement();
				// Wait for a message to arrive
				processInstance.newConnection(partnerLinkName, this);
				// Notify the process that a new message was received
				requestQueue.push(received);
				// Save the message on the queue for later consumption
			}
			catch(Exception e)
			{
				requestQueue.push(e);
			}
		}
	}
}