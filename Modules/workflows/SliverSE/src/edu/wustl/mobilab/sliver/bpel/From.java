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
import edu.wustl.mobilab.sliver.xpath.*;

/**
 * Represents and executes BPEL &lt;from&gt; tags.
 * 
 * @author Greg Hackmann
 */
abstract class From
{
	/**
	 * Parses a From tag.
	 * 
	 * @param parser the parser to read the from's attributes and children
	 * from
	 * @param scopeData a description of the current scope
	 * @return the parsed {@From} tag
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	static From parse(XmlPullParser parser, ScopeData scopeData)
		throws XmlPullParserException, IOException, MalformedDocumentException
	{
		parser.require(XmlPullParser.START_TAG, BPELServer.namespace, "from");
		From from;
		
		if(hasAttribute(parser, "variable"))
			from = new FromVariablePart(parser, scopeData);
		// First try variable sources
		else if(hasAttribute(parser, "expression"))
		{
			String expression = parser.getAttributeValue(null, "expression");
			from = new FromExpression(expression, parser, scopeData);
		}
		// Then try XPath expression sources
//		else if(hasAttribute(parser, "partnerLink"))
//		{
//			from = new FromPartnerLink(parser, scopeData);
//		}
		// Then try partner link sources
		else
		{
			String text = parser.nextText();
			if(text.trim().length() > 0)
				from = new FromLiteral(text);
			else
				throw new MalformedBPELException(parser,
					"<from> has invalid from-spec");
		}
		// Finally assume the text represents a literal source
		

		// TODO:
		// <from variable="ncname" property="qname"/>

		parser.require(XmlPullParser.END_TAG, BPELServer.namespace, "from");
		parser.nextTag();
		return from;		
	}
	
	/**
	 * Gets the kind of data that this tag produces (e.g.,
	 * <tt>{@link Element}.class</tt> or <tt>{@link Binding}.class</tt>).
	 * 
	 * @return the kind of data that this tag produces
	 * @see To#getSinkType()
	 */
	abstract Class getSourceType();

	/**
	 * Reads the data from the specified source.  This data will be of the type
	 * specified by {@link #getSourceType()}.
	 * 
	 * @param processInstance the current BPEL process instance
	 * @return the data that was read
	 * 
	 * @throws FaultedSignal an error occured while writing the data
	 */
	abstract Object read(ProcessInstance processInstance) throws FaultedSignal;
	
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
		return "<from>";
	}
}


/**
 * Describes data sources specified by literals.
 * 
 * @author Greg Hackmann
 */
class FromLiteral extends From
{
	/**
	 * The literal data source.
	 */
	private final String literal;

	/**
	 * Creates a new FromLiteral.
	 * 
	 * @param literal the literal data source
	 */
	FromLiteral(String literal)
	{
		this.literal = literal;
	}
	
	Object read(ProcessInstance processInstance) throws FaultedSignal
	{
		Element element = new Element();
		element.addChild(Node.TEXT, literal);
		// Create a new Element with the literal as its text
		
		return element;
	}

	Class getSourceType()
	{
		return Element.class;
	}	
}

/**
 * Describes data sources specified by an XPath expression.
 * 
 * @author Greg Hackmann
 */
class FromExpression extends From
{
	/**
	 * The expression that specifies the data source.
	 */
	private final Expression expression;
	/**
	 * The process's current scope.
	 */
	private final ScopeData scopeData;
	
	/**
	 * Creates a new FromExpression.
	 * 
	 * @param expression a String-encoded XPath expression
	 * @param parser the parser that the expression came from
	 * @param scopeData the process's current scope
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedExpressionException the XPath parser read malformed data
	 */
	FromExpression(String expression, XmlPullParser parser, ScopeData scopeData)
		throws MalformedExpressionException, IOException, XmlPullParserException
	{
		this.expression = ExpressionParser.parse(expression, parser, scopeData);
		this.scopeData = scopeData;
		// Parse the expression and save the scope for later
		
		parser.nextTag();
	}
	
	Object read(ProcessInstance processInstance) throws FaultedSignal
	{
		try
		{
			Object result = expression.evaluate(scopeData, processInstance);
			// Evaluate the XPath expression

			Element element = new Element();
			element.addChild(Node.TEXT, result.toString());
			return element;
			// Save the result in an Element
			// FIXME: I can't imagine that this will work for complex data
			// types.
		}
		catch(ExpressionEvaluationException e)
		{
			throw new FaultedSignal(BPELServer.sliverNamespace,
				"expressionEvaluationException", e);
		}
	}

	Class getSourceType()
	{
		return Element.class;
	}
}



/**
 * Describes data sources specified by BPEL variable parts.
 * 
 * @author Greg Hackmann
 */
class FromVariablePart extends From
{
	/**
	 * The BPEL variable that the data will be read from.
	 */
	private final VariableSpecification variable;
	/**
	 * The part of the variable that will be read.
	 */
	private final String partName;
	/**
	 * The process's current scope.
	 */
	private final ScopeData scopeData;
	
	/**
	 * Creates a new FromVariablePart.
	 * 
	 * @param parser the parser that the expression comes from
	 * @param scopeData the process's current scope
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedBPELException the BPEL parser read malformed data
	 */
	FromVariablePart(XmlPullParser parser, ScopeData scopeData)
		throws MalformedBPELException, IOException, XmlPullParserException
	{
		this.scopeData = scopeData;
		
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
				"<from> must specify variable");
		variable = scopeData.getVariable(variableName);
		// Read the variable name and get the corresponding spec		
		
		parser.nextTag();
	}
	
	Object read(ProcessInstance processInstance) throws FaultedSignal
	{
		return processInstance.getVariablePart(scopeData, variable, partName,
			null);
	}

	Class getSourceType()
	{
		return Element.class;
	}
}

//class FromPartnerLink extends From
//{
//	private final PartnerLink partnerLink;
//	private final boolean myRole;
//
//	FromPartnerLink(XmlPullParser parser, ScopeData scopeData)
//		throws MalformedBPELException, IOException, XmlPullParserException
//	{
//		String partnerLinkName = parser.getAttributeValue(null, "partnerLink");
//		String endpointReference = parser.getAttributeValue(null, "endpointReference");
//		
//		if(!("myRole".equals(endpointReference) || "partnerRole".equals(endpointReference)))
//			throw new MalformedBPELException(parser, "<from> with partnerLink source must specify endpointReference of \"myRole\" or \"partnerRole\"");
//
//		partnerLink = scopeData.getPartnerLink(partnerLinkName);
//		if(partnerLink == null)
//			throw new MalformedBPELException(parser, "partnerLink "
//				+ partnerLinkName + " not found in scope");
//		
//		myRole = "myRole".equals(endpointReference);
//
//		parser.nextTag();
//	}
//
//	Class getSourceType()
//	{
//		return Channel.class;
//	}
//
//	Object read(ProcessInstance processInstance) throws FaultedSignal
//	{
//		return null;
//		// TODO
//	}	
//}
