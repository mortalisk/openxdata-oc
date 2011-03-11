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

import org.kxml2.kdom.*;
import org.xmlpull.v1.*;

//import edu.wustl.mobilab.sliver.soap.*;
import edu.wustl.mobilab.sliver.util.*;

/**
 * Represents and executes BPEL &lt;to&gt; tags.
 * 
 * @author Greg Hackmann
 */
abstract class To
{
	/**
	 * Parses a From tag.
	 * 
	 * @param parser the parser to read the to's attributes and children
	 * from
	 * @param scopeData a description of the current scope
	 * @return the parsed {@To} tag
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */	
	static To parse(XmlPullParser parser, ScopeData scopeData)
		throws XmlPullParserException, IOException, MalformedDocumentException
	{
		parser.require(XmlPullParser.START_TAG, BPELServer.namespace, "to");
		To to;
		
		if(hasAttribute(parser, "variable"))
			to = new ToVariablePart(parser, scopeData);
		// First try variable sinks
//		else if(hasAttribute(parser, "partnerLink"))
//			to = new ToPartnerLink(parser, scopeData);
		else
			throw new MalformedBPELException(parser, "<to> has invalid to-spec");
		
		// TODO:
		// <to variable="ncname" property="qname"/>

		parser.require(XmlPullParser.END_TAG, BPELServer.namespace, "to");
		parser.nextTag();
		
		return to;
	}

	/**
	 * Gets the kind of data that this tag consumes (e.g.,
	 * <tt>{@link Element}.class</tt> or <tt>{@link Binding}.class</tt>).
	 * 
	 * @return the kind of data that this tag consumes
	 * @see From#getSourceType()
	 */
	abstract Class getSinkType();

	/**
	 * Writes data to the specified destination.  This data must be of the type
	 * specified by {@link #getSinkType()}.
	 * 
	 * @param value the data to write
	 * @param processInstance the current BPEL process instance
	 * 
	 * @throws FaultedSignal an error occured while writing the data
	 */
	abstract void write(Object value, ProcessInstance processInstance)
		throws FaultedSignal;

	/**
	 * Determines whether or not the tag has the specified attribute.
	 * 
	 * @param parser the parser that is reading the current tag
	 * @param name the name of the attribute to look for
	 * @return whether or not the tag has the specified attribute
	 */
	private static boolean hasAttribute(XmlPullParser parser, String name)
	{
		return parser.getAttributeValue(null, name) != null;
	}
	
	public String toString()
	{
		return "<to>";
	}
}

/**
 * Describes data sinks specified by BPEL variable parts.
 * 
 * @author Greg Hackmann
 */
class ToVariablePart extends To
{
	/**
	 * The BPEL variable that the data will be written to.
	 */
	private final VariableSpecification variable;
	/**
	 * The part of the variable that will be written.
	 */
	private final String partName;
	
	/**
	 * Creates a new ToVariablePart.
	 * 
	 * @param parser the parser that the expression comes from
	 * @param scopeData the process's current scope
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedBPELException the BPEL parser read malformed data
	 */
	ToVariablePart(XmlPullParser parser, ScopeData scopeData)
		throws MalformedBPELException, IOException, XmlPullParserException
	{
		String part = parser.getAttributeValue(null, "part");
		if(part == null)
			partName = null;
		else
		{
			String [] splitPart = Namespace.expandNamespace(part, parser);
			partName = splitPart[1];
		}
		// Read the part, and split off the name portion if the part isn't empty
		
		String variableName = parser.getAttributeValue(null, "variable");
		if(variableName == null)
			throw new MalformedBPELException(parser,
				"<to> must specify variable");
		variable = scopeData.getVariable(variableName);
		// Read the variable name and get the corresponding spec		

		parser.nextTag();
	}

	Class getSinkType()
	{
		return Element.class;
	}
	
	void write(Object value, ProcessInstance processInstance) throws
		FaultedSignal
	{
		Element element = (Element)value;
		
		// If no part was specified
		if(partName == null)
		{
			processInstance.setVariable(variable.getName(), element);
			BPELServer.log.info(toString() + ": setting variable " +
				variable.getName());
			// Write directly into the variable
		}
		// Otherwise, if a part was specified
		else
		{
			Element oldValue = processInstance.getVariable(variable.getName());
			if(oldValue == null)
			{
				oldValue = new Element();
				oldValue.setName(variable.getMessageType());
				oldValue.setNamespace(variable.getMessageTypeNamespace());
				BPELServer.log.info(toString() + ": creating variable " +
					variable.getName());
			}
			// Get the old value if it exists, or make a new one if it doesn't

			element.setName(partName);
			int oldChild = oldValue.indexOf(null, partName, 0);
			if(oldChild >= 0)
				oldValue.removeChild(oldChild);
			// Remove the old part, if it exists
			
			oldValue.addChild(Node.ELEMENT, element);
			processInstance.setVariable(variable.getName(), oldValue);
			BPELServer.log.info(toString() + ": updating variable " +
				variable.getName());
			// Add the new part and update the variable's value
		}
	}
}

//class ToPartnerLink extends To
//{
//
//	/**
//	 * Creates a new ToPartnerLink.
//	 * 
//	 * @param parser the parser that the expression comes from
//	 * @param scopeData the process's current scope
//	 * 
//	 * @throws IOException there was an I/O error while reading from the parser
//	 * @throws XmlPullParserException the XML parser read malformed data
//	 * @throws MalformedBPELException the BPEL parser read malformed data
//	 */
//	ToPartnerLink(XmlPullParser parser, ScopeData scopeData)
//		throws MalformedBPELException, IOException, XmlPullParserException
//	{
//	
//	}
//	
//	Class getSinkType()
//	{
//		return Channel.class;
//	}
//	
//	void write(Object value, ProcessInstance processInstance) throws
//		FaultedSignal
//	{
//		// TODO
//		
//	}
//}

