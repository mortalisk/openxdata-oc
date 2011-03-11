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

import org.ksoap2.*;
import org.kxml2.io.*;
import org.kxml2.kdom.*;
import org.xmlpull.v1.*;

/**
 * A helper class that simplifies implementing {@link Channel}s for
 * transports that support {@link InputStream} and {@link OutputStream}.
 * 
 * @author Greg Hackmann
 */
public abstract class StreamChannel extends Channel
{
	/**
	 * Gets the {@link InputStream} to the remote host.
	 * 
	 * @return the {@link InputStream} to the remote host
	 * @throws IOException an I/O error occured while opening the stream
	 */
	protected abstract InputStream getInputStream() throws IOException;
	/**
	 * Writes a buffer to the output stream.
	 * 
	 * @param buffer the buffer to write
	 * @throws IOException an I/O error occured while writing to the stream
	 */
	protected abstract void write(byte [] buffer) throws IOException;
	
	// At this point, you're probably wondering the API doesn't have
	// getOutputStream() instead of write().  Well, here's why:
	//
	// In principle, you should just be able to feed a raw input or output
	// stream to the XML parser/serializer, and everyone will be happy.
	// Everyone, that is, except for the J9 VM -- which will let you read from
	// Socket input streams a handful of times and then just block forever for
	// no good reason.
	//
	// In principle, you should be able to work around this by just using
	// DataInputStream's available() method to get the size of the incoming
	// message, and then use readFully() to dump it to a byte array.  That works
	// fine on J9, but Sun has apparently declared that available() should
	// return 0 from *its* Socket input stream.
	//
	// The least horrible solution I've found is for SocketChannel to use
	// DataInputStream's readUTF() method to read the XML, then feed that 
	// to a ByteArrayInputStream.  Unfortunately, that requires that you write
	// the data using DataOutputStream's writeUTF() method, and there's no
	// clean way to make that happen just for SocketChannel unless you delegate
	// the entire writing procedure to the channel.  Hence the asymmetric API
	// above.
	//
	// In short, I acknowledge that this is a hack.  Any complaints should be
	// directed to Sun and IBM.
	
	public Element receiveElement() throws IOException,
		XmlPullParserException, MustUnderstandException
	{
		InputStream in = getInputStream();
		XmlPullParser parser = new KXmlParser();

		parser.setInput(in, null);
		SoapEnvelope envelope = Serialization.newDOMEnvelope();
		// Create a new parser from the input stream and a DOM envelope

		Node requestRoot = (Node)deserialize(parser, envelope);
		return requestRoot.getElement(0);
		// Deseralize the request and return the root element
	}

	public Object receiveObject() throws IOException,
		XmlPullParserException, MustUnderstandException
	{
		InputStream in = getInputStream();

		XmlPullParser parser = new KXmlParser();
		parser.setInput(in, null);
		// Create a new parser from the input stream
		
		Object request = deserialize(parser, Serialization.newEnvelope());
		return request;
		// Deseralize the request using a SOAP envelope
	}

	public synchronized void sendElement(Element element) throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		XmlSerializer serializer = new KXmlSerializer();
		serializer.setOutput(out, null);
		serialize(serializer, element, Serialization.newDOMEnvelope());
		out.flush();
		// Serialize using a DOM envelope to a byte array
		
		write(out.toByteArray());
		// Write the byte array to the stream
	}

	public synchronized void sendObject(Object object) throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		XmlSerializer serializer = new KXmlSerializer();
		serializer.setOutput(out, null);
		serialize(serializer, object, Serialization.newEnvelope());
		out.flush();
		// Serialize using a SOAP envelope to a byte array

		write(out.toByteArray());
		// Write the byte array to the stream
	}
}
