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
 * Represents BPEL &lt;variable&gt; tags.
 * 
 * @author Greg Hackmann
 */
public class VariableSpecification
{
	/**
	 * The variable's name.
	 */
	private String name;
	/**
	 * The variable's simple XSD type.
	 */
	private String type;
	/**
	 * The variable's message type.
	 */
	private String messageTypeName;
	/**
	 * The namespace of the variable's message type.
	 */
	private String messageTypeNamespace;
	/**
	 * The variable's XSD element type.
	 */
	private String element;
	
	/**
	 * Creates a new VariableSpecification.
	 * 
	 * @param parser the parser to read the variable's attributes from
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	VariableSpecification(XmlPullParser parser)
		throws MalformedDocumentException, IOException, XmlPullParserException
	{
		parser.require(XmlPullParser.START_TAG, BPELServer.namespace,
			"variable");

		name = parser.getAttributeValue(null, "name");
		String messageType = parser.getAttributeValue(null, "messageType");
		type = parser.getAttributeValue(null, "type");
		element = parser.getAttributeValue(null, "element");
		// Read the attributes

		int numTypes = 0;
		if(messageType != null)
			numTypes++;
		if(type != null)
			numTypes++;
		if(element != null)
			numTypes++;
		
		if(numTypes != 1)
			throw new MalformedBPELException(parser, "<variable> must "
				+ "specify exactly one of type, messageType, or element");

		if(messageType != null)
		{
			String [] messageTypeSplit = Namespace.expandNamespace(messageType,
				parser);
			messageTypeNamespace = messageTypeSplit[0];
			messageTypeName = messageTypeSplit[1];
		}
		// If a message type was specified, then split and expand it into name
		// and namespace
		
		if(name == null)
			throw new MalformedBPELException(parser,
				"<variable> must specify name");
		// Complain if the variable is anonymous

		parser.nextTag();
		parser.require(XmlPullParser.END_TAG, BPELServer.namespace, "variable");
		parser.nextTag();
	}


	/**
	 * Gets the variable's name.
	 * 
	 * @return the variable's name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Gets the variable's simple XSD type.
	 * 
	 * @return the variable's simple XSD type, or <tt>null</tt> if the variable
	 * has a message or element type
	 */
	String getType()
	{
		return type;
	}
	
	/**
	 * Gets the variable's message type.
	 * 
	 * @return the variable's message type, or <tt>null</tt> if the variable
	 * has a simple XSD or element type
	 */
	String getMessageType()
	{
		return messageTypeName;
	}

	/**
	 * Gets the namespace of the variable's message type.
	 * 
	 * @return the namespace of the variable's message type, or <tt>null</tt>
	 * if the variable has a simple XSD or element type
	 */
	String getMessageTypeNamespace()
	{
		return messageTypeNamespace;
	}

	/**
	 * Gets the variable's XSD element type.
	 * 
	 * @return the namespace of the variable's message type, or <tt>null</tt>
	 * if the variable has a simple XSD or message type
	 */
	String getElement()
	{
		return element;
	}
	
	public String toString()
	{
		return getName();
	}
}
