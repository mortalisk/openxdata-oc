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

import org.kxml2.kdom.*;
import org.xmlpull.v1.*;

import edu.wustl.mobilab.sliver.soap.*;
import edu.wustl.mobilab.sliver.util.*;

/**
 * A server that handles SOAP requests.
 * 
 * <p>Example usage:</p>
 * 
 * <code>
 * String namespace = "http://some/namespace";
 * <br />SocketTransport transport = new SocketTransport(9000);
 * <br />BPELServer server = new BPELServer(transport);
 * <br />server.addProcess(namespace, new FileInputStream("SomeWorkflow.bpel")); 
 * <br />server.bindIncomingLink("ClientLink", namespace, "ExecuteWorkflow");
 * <br />server.bindOutgoingLink("ExternalSOAPServiceLink", new SocketAddress("some.host", 9000));
 * <br />server.start();
 * </code>
 * 
 * @see SOAPServer
 * 
 * @author Greg Hackmann
 */
public class BPELServer extends SOAPServer
{
	/**
	 * The BPEL namespace.
	 */
	public static final String namespace =
		"http://schemas.xmlsoap.org/ws/2003/03/business-process/";
	
	/**
	 * The namespace for Sliver-specific faults.
	 */
	public static final String sliverNamespace = 
		"http://mobilab.cse.wustl.edu/projects/sliver/";
	
	/**
	 * The {@link Logger} that will log BPEL server events.
	 */
	public static final Logger log =
		Logging.getLogger("edu.wustl.mobilab.sliver.bpel");

	/**
	 * All known BPEL processes.
	 */
	private final Hashtable processes = new Hashtable();
	/**
	 * The bindings for incoming partner links.
	 */
	private final Hashtable incomingBindings = new Hashtable();
	/**
	 * The bindings for outgoing partner links.
	 */
	private final Hashtable outgoingBindings = new Hashtable();

	/**
	 * Creates a new BPEL server.
	 * 
	 * @param transport the transport that SOAP requests will arrive from
	 */
	public BPELServer(Transport transport)
	{
		super(transport);
	}

	/**
	 * Binds an incoming partner link to an operation namespace/name.
	 * 
	 * @param partnerLinkName the partner link's name
	 * @param operationNamespace the namespace of the partner link's SOAP
	 * operation
	 * @param operation the partner link's SOAP operation
	 */
	public void bindIncomingLink(String partnerLinkName,
		String operationNamespace, String operation)
	{
		incomingBindings.put(operationNamespace + ":" + operation,
			partnerLinkName);
		
		// TODO: enforce <partner> constraints
	}
	
	/**
	 * Gets an incoming partner link binding.
	 * 
	 * @param operationNamespace the namespace of the partner link's SOAP
	 * operation
	 * @param operation the partner link's SOAP operation
	 * 
	 * @return the name of the corresponding partner link
	 */
	String getIncomingLink(String operationNamespace, String operation)
	{
		return (String)incomingBindings.get(operationNamespace + ":"
			+ operation);
	}

	/**
	 * Binds an outgoing partner link.
	 * 
	 * @param partnerLinkName the partner link's name
	 * @param binding the partner link's binding
	 */
	public void bindOutgoingLink(String partnerLinkName, Binding binding)
	{
		outgoingBindings.put(partnerLinkName, binding);
	}

	/**
	 * Gets an outgoing partner link binding.
	 * 
	 * @param partnerLinkName the partner link's name
	 * @return the corresponding binding
	 */
	Binding getOutgoingBinding(String partnerLinkName)
	{
		return (Binding)outgoingBindings.get(partnerLinkName);
	}
	
	/**
	 * Adds a process to the BPEL server.
	 * 
	 * @param operationNamespace the process's namespace
	 * @param in an {@link InputStream} containing the process's BPEL
	 * specification
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	public void addProcess(String operationNamespace, InputStream in)
		throws IOException, XmlPullParserException, MalformedDocumentException
	{
		Process process = Process.parse(in);
		addProcess(operationNamespace, process);
	}

	/**
	 * Adds an already-parsed process to the BPEL server.
	 * 
	 * @param operationNamespace the process's namespace
	 * @param process the process to add
	 */
	public void addProcess(String operationNamespace, Process process)
	{
		processes.put(operationNamespace, process);
		// Store our new process for later execution
		
		log.info("Added process to namespace " + operationNamespace);
	}
	
	protected Object receiveRequest(Channel channel) throws IOException,
		MustUnderstandException, XmlPullParserException
	{
		return channel.receiveElement();
	}
	
	protected void handleRequest(Object request, Channel channel)
		throws IOException
	{
		Element soapRequest = (Element)request;
		
		String requestNamespace = soapRequest.getNamespace();
		Process process = (Process)processes.get(requestNamespace);
		// Get the request's namespace and look up the process
		
		if(process == null)
		{
			Exception e = new IllegalArgumentException("Unknown namespace "
				+ requestNamespace);
			sendFault("Client", e.getMessage(), e.getMessage(), channel);
			return;
		}
		// Complain if the process doesn't exist
		
		try
		{
			process.execute(this, soapRequest, channel);
		}
		catch(Throwable t)
		{
			log.severe("Exception thrown while executing process: " + t);
			t.printStackTrace();
			sendFault(new FaultedSignal(sliverNamespace, "uncaughtException", t),
				channel);
		}
		// Try to execute the process
	}

	/**
	 * Sends a BPEL fault over a communication channel.
	 * 
	 * @param fault the BPEL {@link FaultedSignal} to send
	 * @param channel the {@link Channel} to send the fault over
	 */
	static void sendFault(FaultedSignal fault, Channel channel)
	{
		try
		{
			BPELServer.log.severe("Sending uncaught " + fault.getFault());
			channel.sendObject(fault.getFault());
			// Send the fault
	
			if(!channel.isClosed())
			{
				BPELServer.log.severe("Closing channel " + channel);
				channel.close();
			}
			// Close the channel if it's not yet closed			
		}
		catch(IOException e)
		{
			BPELServer.log.severe("Could not send fault: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public String toString()
	{
		return "BPELServer";
	}
}
