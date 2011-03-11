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

package edu.wustl.mobilab.sliver.soap.j2se;

import java.io.*;

import javax.servlet.http.*;

import org.ksoap2.*;
import org.kxml2.io.*;
import org.kxml2.kdom.*;
import org.xmlpull.v1.*;

import edu.wustl.mobilab.sliver.soap.*;

/**
 * A {@link Channel} for {@link HttpServletRequest}-based communication.
 * Unlike {@link HTTPChannel}, this Channel designed specifically for responding
 * to incoming requests.
 * 
 * @author Greg Hackmann
 */
class JettyChannel extends Channel
{
	/**
	 * The incoming HTTP request.
	 */
	private final HttpServletRequest request;
	/**
	 * The outgoing HTTP response.
	 */
	private final HttpServletResponse response;
	/**
	 * Whether or not we've already closed the HTTP session.
	 */
	private boolean closed = false;
	/**
	 * Whether or not a response has been sent.
	 */
	private boolean sentResponse = false;

	/**
	 * Creates a new JettyChannel.
	 * 
	 * @param request the incoming HTTP request
	 * @param response the outgoing HTTP response
	 */
	JettyChannel(HttpServletRequest request, HttpServletResponse response)
	{
		this.request = request;
		this.response = response;
	}	

	public synchronized void closeImpl() throws IOException
	{
		closed = true;

		response.setStatus(sentResponse ? HttpServletResponse.SC_OK
			: HttpServletResponse.SC_ACCEPTED);
		// Set the status based on whether or not we've actually
		// made a response
		
		response.setHeader("Connection", "close");
		response.getWriter().flush();
		response.getWriter().close();
		// Set the HTTP header's "Connection" header to "close" and
		// shut down the session

		notifyAll();
	}
	
	/**
	 * Blocks until the connection is closed.
	 */
	synchronized void waitToClose()
	{
		while(!closed)
		{
			try
			{
				wait();
			}
			catch(InterruptedException e) { /**/ }
		}
	}
	
	/**
	 * Serializes outgoing SOAP responses.
	 * 
	 * @param object the outgoing response
	 * @param envelope the {@link SoapEnvelope} that the response
	 * will be wrapped in
	 * 
	 * @throws IOException an I/O error occured while serializing the object
	 */
	private void send(Object object, SoapEnvelope envelope) throws IOException
	{
		if(isClosed())
			throw new IOException("HTTP session already closed");
		// Whine if we're writing to a closed session

		response.setContentType("text/xml; charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		// Set the appropriate Content-Type and response code

		XmlSerializer serializer = new KXmlSerializer();
		Writer writer = response.getWriter();
		serializer.setOutput(writer);

		serialize(serializer, object, envelope);
		writer.flush();
		// Write to the response and flush it

		sentResponse = true;
		close();
		// Shut down the session
	}
	
	public synchronized void sendObject(Object object) throws IOException
	{
		send(object, Serialization.newEnvelope());
	}
	
	public synchronized void sendElement(Element element) throws IOException
	{
		send(element, Serialization.newDOMEnvelope());
	}
	
	/**
	 * Deserializes incoming SOAP requests.
	 * 
	 * @param envelope the {@link SoapEnvelope} that the deserialized object
	 * will be received in
	 * @return the deserialized object
	 * 
	 * @throws IOException an I/O error occured while receiving the object
	 * @throws XmlPullParserException the remote host sent a malformed
	 * XML response
	 * @throws MustUnderstandException the remote host sent a
	 * header element with a <code>mustUnderstand</code> attribute of "1"
	 * @throws SoapFault the remote host responded with a SOAP fault
	 */
	private Object receive(SoapEnvelope envelope) throws IOException,
		XmlPullParserException, MustUnderstandException
	{
		if(isClosed())
			throw new IOException("HTTP session already closed");
		// Whine if we're reading from a closed session
		
		XmlPullParser parser = new KXmlParser();
		parser.setInput(request.getReader());
		return deserialize(parser, envelope);
		// Deserialize the incoming request
	}

	public synchronized Element receiveElement() throws IOException,
		XmlPullParserException, MustUnderstandException
	{
		Node rootNode = (Node)receive(Serialization.newDOMEnvelope());
		return rootNode.getElement(0);
	}

	public synchronized Object receiveObject() throws IOException,
		XmlPullParserException, MustUnderstandException
	{
		return receive(Serialization.newEnvelope());
	}
	
	public String toString()
	{
		return "JettyChannel(" + request.getRemoteAddr() + ":" + request.getRemotePort() + ")";
	}
}
