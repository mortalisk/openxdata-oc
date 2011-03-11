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

import org.ksoap2.*;
import org.ksoap2.serialization.*;
import org.kxml2.kdom.*;
import org.xmlpull.v1.*;

import edu.wustl.mobilab.sliver.util.*;

/**
 * Defines an abstract communication channel for SOAP calls.
 * Channels must be able to send and receive SOAP objects.
 * 
 * @author Greg Hackmann
 */
public abstract class Channel
{
	/**
	 * Whether or not the channel is closed.
	 */
	private boolean closed = false;
	
	
	/**
	 * Checks if the communication channel is closed.
	 * 
	 * @return whether or not the communication channel is closed
	 */
	public boolean isClosed()
	{
		return closed;
	}
	
	/**
	 * Closes the communication channel.
	 * The actual closing operation is deferred to {@link #closeImpl()}.
	 * 
	 * @throws IOException an I/O error occured while closing the channel
	 */
	public void close() throws IOException
	{
		closed = true;
		closeImpl();
	}
	
	/**
	 * Closes the communication channel using the underlying communication
	 * library.
	 * 
	 * @throws IOException an I/O error occured while closing the channel
	 */
	protected abstract void closeImpl() throws IOException;
	
	/**
	 * Sends an object using SOAP encoding.
	 * 
	 * @param object the object to send
	 * @throws IOException an I/O error occured while sending the object
	 */
	public abstract void sendObject(Object object) throws IOException;
	
	/**
	 * Sends a {@link Element} using SOAP encoding.
	 * 
	 * @param element the {@link Element} to send
	 * @throws IOException an I/O error occured while sending the element
	 */
	public abstract void sendElement(Element element) throws IOException;
	
	/**
	 * Receives a SOAP-encoded object from the remote endpoint.
	 * 
	 * @return the deserialized version of the received object
	 * @throws IOException an I/O error occured while receiving the object
	 * @throws XmlPullParserException the remote host sent a malformed
	 * XML response
	 * @throws MustUnderstandException the remote host sent a
	 * header element with a <code>mustUnderstand</code> attribute of "1"
	 */
	public abstract Object receiveObject() throws IOException,
		XmlPullParserException, MustUnderstandException;

	/**
	 * Receives a DOM-parsed {@link Element} from the remote endpoint.
	 * 
	 * @return the DOM version of the received object
	 * @throws IOException an I/O error occured while receiving the object
	 * @throws XmlPullParserException the remote host sent a malformed
	 * XML response	
	 * @throws MustUnderstandException the remote host sent a
	 * header element with a <code>mustUnderstand</code> attribute of "1"
	 */
	public abstract Element receiveElement() throws IOException,
		XmlPullParserException, MustUnderstandException;

	/**
	 * Helps to serialize an object.
	 * 
	 * @param serializer the {@link XmlSerializer} that will serialize
	 * the object
	 * @param object the object to be serialized
	 * @param envelope the {@link SoapEnvelope} that the serialized object
	 * will be wrapped in
	 * 
	 * @throws IOException an I/O error occured while serializing the object
	 */
	protected static void serialize(XmlSerializer serializer, Object object,
		SoapEnvelope envelope) throws IOException
	{
		envelope.bodyOut = object;
		envelope.write(serializer);

		serializer.flush();
	}
	
	/**
	 * Ensures that the SOAP header does not contain any entries that we cannot
	 * handle.
	 * 
	 * @param header the SOAP header entries
	 * @throws MustUnderstandException one of the SOAP header entries had its
	 * <tt>mustUnderstand</tt> attribute set to "1"
	 */
	private static void validateHeader(Element [] header)
		throws MustUnderstandException
	{	
		if(header != null)
			for(int i = 0; i < header.length; i++)
				if("1".equals(header[i].getAttributeValue(null,
					"mustUnderstand")))
					throw new MustUnderstandException(header[i]);
		// Ensure that none of the header entries have a mustUnderstand
		// attribute of "1"
		
		// TODO: what about actor attribute?
	}
	
	/**
	 * Helps to deserialize an object.
	 * 
	 * @param parser the {@link XmlPullParser} that will provide the
	 * object
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
	protected static Object deserialize(XmlPullParser parser,
		SoapEnvelope envelope) throws XmlPullParserException, IOException,
		MustUnderstandException, SoapFault
	{
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
		envelope.parse(parser);		
		validateHeader(envelope.headerIn);
		// Parse the envelope and validate the header
		
		if(envelope.bodyIn instanceof SoapFault)
			throw (SoapFault)envelope.bodyIn;
		// If we received a SoapFault, throw it

		Object returnMe = envelope.bodyIn;
		// If the object is unserialized, we need to do some processing before
		// returning it
		if(returnMe instanceof Node)
		{
			Node rootNode = (Node)returnMe;

			Element response = null;
			int numChildren = rootNode.getChildCount();
			// For all children in the body
			for(int i = 0; i < numChildren; i++)
			{
				if(rootNode.getType(i) == Node.ELEMENT)
				{
					Element element = rootNode.getElement(i);
					if(!"multiRef".equals(element.getName()))
					{
						response = element;
						break;
						// If the child is an element and not a multiRef, then
						// stop looking
					}
				}
			}
			// Find a non-reference child in the body; there should be exactly
			// one
			
			if(response == null)
				throw new
					XmlPullParserException("No response found in SOAP body");
			// Complain if no response element was found

			expandHrefs(rootNode, response, new Hashtable());
			sanitizeNamespaces(response, response, envelope, parser);
			// If we're handling a DOM element, then sanitize its namespaces
		}
		
		return returnMe;
	}
	
	/**
	 * Recursively searches an {@link Element} for the target of an 
	 * <tt>href</tt>.
	 * 
	 * @param current the current node being searched
	 * @param href the href to look for
	 * @return the {@link Element} corresponding to the href, or <tt>null</tt>
	 * if it could not be found
	 */
	private static Element resolveHref(Node current, String href)
	{
		if(current instanceof Element)
		{
			Element element = (Element)current;
			if("multiRef".equals(element.getName()))
				if(href.equals(element.getAttributeValue(null, "id")))
					return element;
			// If we're currently looking at a <multiRef> tag with a matching
			// ID, then return it
		}
		
		int numChildren = current.getChildCount();
		// For each child
		for(int i = 0; i < numChildren; i++)
		{
			Object child = current.getChild(i);
			if(child instanceof Node)
			{
				Element value =	resolveHref((Node)child, href);
				if(value != null)
					return value;
			}
			// If it's an Element, search it recursively for the href
		}
		
		return null;
		// If we've exhausted all the children, then give up
	}
	
	/**
	 * Recursively "expands" tags which refer to other tags.  For example,
	 * 
	 * <blockquote><pre>&lt;tag1 href="id0" /&gt;
	 *...
	 *&lt;multiRef="id0" xsi:type="ns1:..."&gt;
	 *    &lt;tag2 /&gt;
	 *&lt;/multiRef&gt;
	 *</pre></blockquote>
	 * 
	 * becomes
	 * 
	 * <blockquote><pre>&lt;tag1 xsi:type="ns1:..."&gt;
	 *    &lt;tag2 /&gt;
	 *&lt;/tag1&gt;
	 *...
	 *&lt;multiRef="id0" xsi:type="ns1:..."&gt;
	 *    &lt;tag2 /&gt;
	 *&lt;/multiRef&gt;</pre></blockquote>
	 * 
	 * Note that leaving the <tt>multiRef</tt> tag in the document is fine
	 * for our purposes, since {@link #deserialize(XmlPullParser, SoapEnvelope)}
	 * will strip it out before returning the body of the SOAP request.
	 * 
	 * @param root the root Element of the document
	 * @param current the current Element that is being processed (usually
	 * the same as <code>root</code>)
	 * @param resolved a table containing all the hrefs that have already been
	 * looked up
	 * 
	 * @throws XmlPullParserException the document referred to an href which
	 * does not exist
	 */
	private static void expandHrefs(Node root, Element current,
		Hashtable resolved) throws XmlPullParserException
	{
		String href = current.getAttributeValue(null, "href");
		// If the tag has an href attributed
		if(href != null)
		{
			Element value = (Element)resolved.get(href);
			// Try to fetch the target from the lookup table
			if(value == null)
			{
				value = resolveHref(root, href);
				resolved.put(href, value);
			}
			// If it's not there, then search the root for it
			if(value == null)
				throw new XmlPullParserException("href " + href +
					" could not be resolved");
			// If it's still not there, then complain
			
			current.setAttribute(null, "href", null);
			// Strip out the href attribute
			
			int numAttributes = value.getAttributeCount();
			for(int i = 0; i < numAttributes; i++)
			{
				String name = value.getAttributeName(i);
				String namespace = value.getAttributeNamespace(i);
				if(!"".equals(namespace) || !"id".equals(name))
					current.setAttribute(namespace,
					                     name,
					                     value.getAttributeValue(i));
			}
			// Copy all the attributes (besides the ID) into the tag 
			
			int numChildren = value.getChildCount();
			for(int i = 0; i < numChildren; i++)
				current.addChild(value.getType(i),
				                 value.getChild(i));
			// Copy all the children into the tag 
		}
		
		int numChildren = current.getChildCount();
		for(int i = 0; i < numChildren; i++)
		{
			Object child = current.getChild(i);
			if(child instanceof Element)
				expandHrefs(root, (Element)child, resolved);
		}
		// Repeat this process for all the children
	}
	
	/**
	 * "Sanitizes" an Element's namespace declarations by copying them directly
	 * into the child tags that use them.  For example,
	 * 
	 * <blockquote><pre>&lt;tag1 xmlns:xsi="..." xmlns:ns1="..."&gt;
	 *    &lt;tag2 xsi:type="ns1:..." /&gt;
	 *&lt;/tag1&gt;</pre></blockquote>
	 * 
	 * becomes
	 * 
	 * <blockquote><pre>&lt;tag1 xmlns:xsi="..." xmlns:ns1="..."&gt;
	 *    &lt;tag2 xsi:type="ns1:..." xmlns:ns1="..." /&gt;
	 *&lt;/tag1&gt;</pre></blockquote>
	 * 
	 * This way, the <code>&lt;tag2&gt;</code> tag is still coherent, even if it
	 * is extracted verbatim from the surrounding
	 * <code>&lt;tag1&gt;...&lt;/tag1&gt;</code> block.
	 * 
	 * @param root the root Element of the document
	 * @param current the current Element that is being processed (usually
	 * the same as <code>root</code>)
	 * @param envelope the {@link SoapEnvelope} that contains the namespace
	 * declarations referred to by the Element
	 * @param parser the {@link XmlPullParser} that was used to parse the
	 * Element
	 */
	private static void sanitizeNamespaces(Element root, Element current,
		SoapEnvelope envelope, XmlPullParser parser)
	{
		String type = current.getAttributeValue(envelope.xsi, "type");
		// If there's a "type" attribute
		if(type != null)
		{
			String prefix = Namespace.splitNamespace(type)[0];
			String namespace = current.getNamespaceUri(prefix);
			// First ask the SOAP body if it knows how to resolve the prefix
			if(namespace == null)
				namespace = parser.getNamespace(prefix);
			// Otherwise, check the envelope
			
			// If we understand the namespace
			if(!"".equals(prefix))
				current.setPrefix(prefix, namespace);
				// Copy the namespace declaration into the current tag
		}
		
		int numChildren = current.getChildCount();
		// For each child
		for(int i = 0; i < numChildren; i++)
		{
			Object child = current.getChild(i);
			if(child instanceof Element)
				sanitizeNamespaces(root, (Element)child, envelope, parser);
			// If it's an Element, sanitize its namespaces too
		}
	}
	

	/**
	 * Calls a remote SOAP service.
	 * 
	 * @param call the method call encoded as a {@link SoapObject}
	 * @return the remote service's response
	 * 
	 * @throws IOException an I/O error occured while sending the call or 
	 * receiving the response
	 * @throws XmlPullParserException the remote host sent a malformed
	 * XML response
	 * @throws MustUnderstandException the remote host sent a
	 * header element with a <code>mustUnderstand</code> attribute of "1"
	 * @throws SoapFault the remote host responded with a SOAP fault
	 */
	public Object call(SoapObject call) throws IOException,
		XmlPullParserException, SoapFault, MustUnderstandException
	{
		sendObject(call);
		return receiveObject();
	}
	
	/**
	 * Calls a remote SOAP service.
	 * 
	 * @param element the method call encoded as a DOM {@link Element}
	 * @return the remote service's response
	 * 
	 * @throws IOException an I/O error occured while sending the call or 
	 * receiving the response
	 * @throws XmlPullParserException the remote host sent a malformed
	 * XML response
	 * @throws MustUnderstandException the remote host sent a
	 * header element with a <code>mustUnderstand</code> attribute of "1"
	 * @throws SoapFault the remote host responded with a SOAP fault
	 */
	public Element call(Element element) throws IOException,
		XmlPullParserException, SoapFault, MustUnderstandException
	{
		sendElement(element);
		return receiveElement();
	}
}
