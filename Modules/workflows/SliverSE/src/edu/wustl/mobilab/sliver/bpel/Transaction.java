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

import org.xmlpull.v1.*;

import edu.wustl.mobilab.sliver.util.*;

/**
 * Represents BPEL activities which send or receive data.
 * 
 * @author Greg Hackmann
 */
abstract class Transaction extends Activity
{
	/**
	 * The partner link that the transaction will occur over.
	 */
	protected PartnerLink partnerLink;
	/**
	 * The name of the transaction's port type.
	 */
	protected String portTypeName;
	/**
	 * The namespace of the transaction's port type.
	 */
	protected String portTypeNamespace;
	/**
	 * The name of the transaction's operation.
	 */
	protected String operation;
	/**
	 * The transaction's correlation information.
	 */
	protected Correlation [] correlations;

	/**
	 * Creates a new Transaction.
	 * 
	 * @param parser the parser to read the tag's attributes and children
	 * from
	 * @param scopeData a description of the current scope
	 * @param tagName the type of the transaction tag
	 */
	protected Transaction(XmlPullParser parser, ScopeData scopeData,
		String tagName)
	{
		this(parser, scopeData, tagName, BPELServer.namespace);
	}

	/**
	 * Creates a new Transaction.
	 * 
	 * @param parser the parser to read the tag's attributes and children
	 * from
	 * @param scopeData a description of the current scope
	 * @param tagName the type of the transaction tag
	 * @param tagNamespace the namespace of the transaction tag
	 */
	protected Transaction(XmlPullParser parser, ScopeData scopeData,
		String tagName, String tagNamespace)
	{
		super(parser, scopeData, tagName, tagNamespace);
	}
	
	protected void parseStartTag() throws XmlPullParserException,
		IOException, MalformedDocumentException
	{
		super.parseStartTag();
		parsePartnerLink();
		
		String portType = getAttribute("portType");
		operation = getAttribute("operation");
		// Get the portType and operation attributes
		
		if(portType == null)
			throw new MalformedBPELException(parser, "<" + type
				+ "> must specify portType");
		if(operation == null)
			throw new MalformedBPELException(parser, "<" + type
				+ "> must specify operation");
		// Verify that both are actually there
		
		String [] portTypeSplit = Namespace.expandNamespace(portType, parser);
		portTypeNamespace = portTypeSplit[0];
		portTypeName = portTypeSplit[1];
		// Split the port type into name and namespace
				
	}
	
	/**
	 * Parses the <tt>partnerLink</tt> attribute.
	 *
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	protected void parsePartnerLink() throws MalformedDocumentException
	{
		String partnerLinkName = getAttribute("partnerLink");
		if(partnerLinkName == null)
			throw new MalformedBPELException(parser, "<" + type
				+ "> must specify partnerLink");
		// Get the partnerLink attribute
		
		partnerLink = scopeData.getPartnerLink(partnerLinkName);
		if(partnerLink == null)
			throw new MalformedBPELException(parser, "partnerLink "
				+ partnerLinkName + " not found in scope");
		// Make sure the partner link actually exists
	}

	/**
	 * Parses the &lt;correlations&gt; child tags.
	 * 
	 * @param defaultPattern the default value of the &lt;correlation&gt;s'
	 * "pattern" attribute (one of {@link Correlation#IN},
	 * {@link Correlation#OUT}, {@link Correlation#OUT_IN}, or
	 * {@link Correlation#UNSPECIFIED})
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	protected void parseCorrelations(int defaultPattern)
		throws XmlPullParserException, IOException, MalformedDocumentException
	{
		if(!parser.getName().equals("correlations")) return;
		// If we're not sitting on a <correlations> tag (it's optional), then do
		// nothing

		parser.require(XmlPullParser.START_TAG, BPELServer.namespace,
						"correlations");
		parser.nextTag();

		Vector correlationVector = new Vector();
		while(parser.getName().equals("correlation"))
			correlationVector.addElement(
				new Correlation(parser, defaultPattern));
		// Read in all the child <correlation> tags

		if(correlationVector.isEmpty())
			throw new MalformedBPELException(parser,
				"<correlations> must specify at least one <correlation>");
		// Ensure there's at least one

		correlations = new Correlation[correlationVector.size()];
		correlationVector.copyInto(correlations);

		parser.require(XmlPullParser.END_TAG, BPELServer.namespace,
			"correlations");
		parser.nextTag();
	}
	
	/**
	 * Parses the input or output variable specifications.
	 * 
	 * @param attribute the name of the attribute (e.g.,
	 * <code>inputVariable</code>)
	 * @return the corresponding {@link VariableSpecification}, or
	 * <code>null</code> if the attribute does not exist
	 * 
	 * @throws MalformedBPELException the attribute referred to a non-existent
	 * variable
	 */
	protected VariableSpecification getVariableSpecification(String attribute)
		throws MalformedBPELException
	{
		String variableName = getAttribute(attribute);
		if(variableName == null)
			return null;
		// If the attribute's not there, give up
		
		VariableSpecification variableSpec =
			scopeData.getVariable(variableName);
		if(variableSpec == null)
			throw new MalformedBPELException(parser, "No variable named "
				+ variableName + " in scope");
		// Look the variable up in the scope, and complain if it's not there
		
		return variableSpec;
	}
	
	/**
	 * Gets the {@link PartnerLink} that will send or receive the message.
	 * 
	 * @return the {@link PartnerLink} that will send or receive the message
	 */
	PartnerLink getPartnerLink()
	{
		return partnerLink;
	}

	/**
	 * Gets the WSDL operation of the message that will be sent or received.
	 * 
	 * @return the WSDL operation of the message that will be sent or received
	 */
	String getOperation()
	{
		return operation;
	}
}
