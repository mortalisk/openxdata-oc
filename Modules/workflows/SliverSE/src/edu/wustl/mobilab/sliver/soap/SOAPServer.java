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

package edu.wustl.mobilab.sliver.soap;

import java.io.*;
import java.util.*;

import org.ksoap2.serialization.*;
import org.xmlpull.v1.*;

import edu.wustl.mobilab.sliver.util.*;

/**
 * A server that handles SOAP requests. The user must provide one
 * {@link Transport} per server, and one {@link SOAPInvocationHandler} per
 * namespace. On J2SE, typically
 * {@link edu.wustl.mobilab.sliver.soap.j2se.SocketTransport} or
 * {@link edu.wustl.mobilab.sliver.soap.j2se.JettyTransport} are used as the
 * transport (for raw sockets and HTTP, respectively), along with
 * {@link edu.wustl.mobilab.sliver.soap.j2se.MethodCallHandler} for the service
 * handlers. On J2ME, {@link edu.wustl.mobilab.sliver.soap.midp.SocketTransport}
 * is usually used for the transport, with
 * {@link edu.wustl.mobilab.sliver.soap.midp.MethodCallHandler} for the service
 * handlers.
 * 
 * <p>
 * Example usage:
 * </p>
 * 
 * <code>
 * Serialization.registerClass("http://some/namespace", SomeCustomMessage.class);
 * <br />SocketTransport transport = new SocketTransport(9000);
 * <br />SOAPServer server = new SOAPServer(transport);
 * <br />server.registerService("http://some/namespace", new MethodCallHandler(SomeService.class);
 * <br />server.start();
 * </code>
 * 
 * @see Serialization
 * @see Transport
 * @see SOAPInvocationHandler
 * 
 * @author Greg Hackmann
 */
public class SOAPServer implements TransportListener
{
	/**
	 * The {@link Logger} that will log SOAP server events.
	 */
	public static final Logger log = Logging.getLogger("edu.wustl.mobilab.sliver.soap");

	/**
	 * The underlying transports used by the server.
	 */
	protected final Vector transports = new Vector();

	/**
	 * A mapping from namespaces to their {@link SOAPInvocationHandler}s.
	 */
	private final Hashtable handlers = new Hashtable();

	/**
	 * Creates a new SOAP server.
	 * 
	 * @param transport the transport that SOAP requests will arrive from
	 */
	public SOAPServer(Transport transport)
	{
		addTransport(transport);
	}
	
	/**
	 * Adds a secondary transport to the SOAP server.
	 * 
	 * @param transport another transport that SOAP requests will arrive from
	 */
	public void addTransport(Transport transport)
	{
		transports.addElement(transport);
		transport.setTransportListener(this);
	}

	/**
	 * Registers a service with the server. The server will delegate incoming
	 * requests to the appropriate handler (typically a
	 * {@link edu.wustl.mobilab.sliver.soap.j2se.MethodCallHandler J2SE
	 * MethodCallHandler} or
	 * {@link edu.wustl.mobilab.sliver.soap.midp.MethodCallHandler J2ME
	 * MethodCallHandler}) based on their namespace. It is assumed that each
	 * service handler corresponds to exactly one unique namespace.
	 * 
	 * @see edu.wustl.mobilab.sliver.soap.j2se.MethodCallHandler
	 * @see edu.wustl.mobilab.sliver.soap.midp.MethodCallHandler
	 * 
	 * @param namespace the service's unique namespace
	 * @param handler the handler for this service
	 */
	public void registerService(String namespace, SOAPInvocationHandler handler)
	{
		handlers.put(namespace, handler);
		log.info("Service registered with namespace " + namespace);
	}

	/**
	 * Deregisters a service from the server.
	 * 
	 * @param namespace the service's unique namespace
	 */
	public void deregisterService(String namespace)
	{
		handlers.remove(namespace);
		log.info("Service deregistered from namespace " + namespace);
	}

	/**
	 * Sends a SOAP fault over a communication channel.
	 * 
	 * @param fault the SOAP fault to send
	 * @param channel the {@link Channel} to send the fault over
	 * 
	 * @throws IOException an I/O error occurred while sending the fault
	 */
	private void sendFault(Fault fault, Channel channel)
		throws IOException
	{		
		log.severe("Sending fault " + fault);
		channel.sendObject(fault);
		if(!channel.isClosed())
		{
			log.severe("Closing channel " + channel);
			channel.close();
		}
	}
	
	/**
	 * Sends a SOAP fault over a communication channel.
	 * 
	 * @param faultCode the SOAP fault code
	 * @param message the fault message
	 * @param detail the fault details
	 * @param channel the {@link Channel} to send the fault over
	 * 
	 * @throws IOException an I/O error occurred while sending the fault
	 */
	protected void sendFault(String faultCode, String message, String detail,
		Channel channel) throws IOException
	{
		Fault fault = new Fault(faultCode, message);
		fault.addDetail(detail);
		sendFault(fault, channel);
	}

	/**
	 * Sends a SOAP fault over a communication channel.
	 * 
	 * @param faultCode the SOAP fault code
	 * @param message the fault message
	 * @param detail the fault details
	 * @param channel the {@link Channel} to send the fault over
	 * 
	 * @throws IOException an I/O error occurred while sending the fault
	 */
	protected void sendFault(String faultCode, String message,
		SerializableException detail, Channel channel) throws IOException
	{
		Fault fault = new Fault(faultCode, message);
		fault.addDetail(detail);
		sendFault(fault, channel);
	}

	/**
	 * Starts the server.
	 * 
	 * @throws IOException the {@link Transport} threw an I/O exception while
	 * starting
	 */
	public void start() throws IOException
	{
		Enumeration e = transports.elements();
		while(e.hasMoreElements())
		{
			Transport next = (Transport)e.nextElement();
			next.open();
		}
		log.info(toString() + ": server started");
	}

	/**
	 * Stops the server.
	 * 
	 * @throws IOException the {@link Transport} threw an I/O exception while
	 * stopping
	 */
	public void stop() throws IOException
	{
		Enumeration e = transports.elements();
		while(e.hasMoreElements())
		{
			Transport next = (Transport)e.nextElement();
			next.close();
		}
		log.info(toString() + ": server stopped");
	}

	public void newConnection(Channel channel)
	{
		try
		{
			Object request;
			try
			{
				request = receiveRequest(channel);
				// Read in the next request
			}
			catch(XmlPullParserException e)
			{
				log.severe("Could not parse incoming SOAP request");
				sendFault("Client", e.getMessage(), "" + e.getDetail(), channel);
				return;
			}
			catch(MustUnderstandException e)
			{
				log.severe("Incoming SOAP request had invalid mustUnderstand attribute");
				sendFault("MustUnderstand", e.getMessage(), e.toString(),
					channel);
				return;
			}
			// Handle parser exceptions and mustUnderstand header entries

			handleRequest(request, channel);
			// Handle the request
		}
		catch(IOException e)
		{
			log.severe("IOException thrown while handling SOAP call: " + e);
			e.printStackTrace();

			try
			{
				if(channel != null && !channel.isClosed())
				{
					log.info("Closing channel " + channel);
					channel.close();
				}
			}
			catch(IOException ex)
			{
				e.printStackTrace();
			}
			// If we failed somewhere, close the channel
		}
	}

	/**
	 * Receives the next SOAP request from a channel.
	 * 
	 * @param channel the Channel to receive the request from
	 * @return the SOAP request that was received
	 * @throws IOException an I/O error occurred while receiving the request
	 * @throws XmlPullParserException the remote host sent a malformed XML
	 * response
	 * @throws MustUnderstandException the remote host sent a header element
	 * with a <code>mustUnderstand</code> attribute of "1"
	 */
	protected Object receiveRequest(Channel channel) throws IOException,
		MustUnderstandException, XmlPullParserException
	{
		return channel.receiveObject();
	}

	/**
	 * Handles SOAP requests.
	 * 
	 * @param request the request to handle
	 * @param channel the channel that the request arrived on
	 * 
	 * @throws IOException an I/O error occured while responding to the request
	 */
	protected void handleRequest(Object request, Channel channel)
		throws IOException
	{
		if(!(request instanceof SoapObject))
		{
			log.severe("Invalid SOAP request received");
			Exception e = new IllegalArgumentException(
				"Request must be a SOAP object");
			sendFault("Client", e.getMessage(), e.toString(), channel);
			return;
		}
		// Verify that what we received is in fact a SOAP request

		SoapObject soapRequest = (SoapObject)request;
		String namespace = soapRequest.getNamespace();
		log.info("Received SOAP request with namespace " + namespace);

		SOAPInvocationHandler handler = (SOAPInvocationHandler)handlers.get(namespace);
		if(handler == null)
		{
			log.severe("Received SOAP request with unknown namespace "
				+ namespace);
			Exception e = new IllegalArgumentException("Unknown namespace "
				+ namespace);
			sendFault("Client", e.getMessage(), e.getMessage(), channel);
			return;
		}
		// Look up the request's namespace, then find its handler

		Object result;
		try
		{
			result = handler.handleInvocation(soapRequest);
		}
		catch(IllegalArgumentException e)
		{
			log.severe("Received SOAP request which could not be understood");
			sendFault("Client", e.getClass().getName() + ": " + e.getMessage(),
				e.toString(), channel);
			return;
		}
		catch(SerializableException e)
		{
			log.severe("SOAP handler threw SerializableException: " + e);
			sendFault("Server", e.getClass().getName() + ": " + e.getMessage(),
				e, channel);
			return;
		}
		catch(UnserializableException e)
		{
			log.severe("SOAP handler threw UnserializableException: " + e);
			sendFault("Server", e.getCause().getClass().getName() + ": "
				+ e.getCause().getMessage(), e.getCause().toString(), channel);
			return;
		}
		// Tell the handler to deal with the request, and send back any faults
		// we encounter

		if(result == null)
			log.info("No result to send; treating as one-way operation");
		else
		{
			log.info("Sending result over channel " + channel);
			channel.sendObject(result);
		}
		log.info("Closing channel " + channel);
		channel.close();
		// Send back the result
	}

	public String toString()
	{
		return "SOAPServer";
	}
}
