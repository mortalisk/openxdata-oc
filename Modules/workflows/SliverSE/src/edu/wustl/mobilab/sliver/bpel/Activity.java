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
import edu.wustl.mobilab.sliver.xpath.*;

/**
 * Represents the common traits of BPEL activities.
 * 
 * @author Greg Hackmann
 */
public abstract class Activity
{
	/**
	 * The parser that the input is coming from.
	 */
	protected final XmlPullParser parser;
	/**
	 * The actual name of the tag (e.g., "assign", "copy", etc.)
	 */
	protected final String type;
	/**
	 * The tag's namespace (usually {@link BPELServer#namespace})
	 */
	protected final String namespace;
	
	/**
	 * The tag's name attribute.
	 */
	protected String name;	
	/**
	 * The tag's suppressJoinFailure attribute.
	 */
	protected boolean suppressJoinFailure = false;
	/**
	 * The tag's joinCondition attribute.
	 */
	protected Expression joinCondition = null;
	
	/**
	 * The scope enclosing the tag.
	 */
	protected final ScopeData scopeData;
	/**
	 * The tag's target links.
	 */
	protected Target [] targets;
	/**
	 * The tag's source links.
	 */
	protected Source [] sources;
	
	/**
	 * The extensions recognized by the activity parser, indexed by their
	 * namespace.
	 */
	private static final Hashtable extensions = new Hashtable();
	static
	{
		addExtension(new StandardActivityExtension());
	}
	
	/**
	 * Registers an extension with the activity parser.
	 * 
	 * @param extension the {@link ActivityExtension} that will parse the
	 * activities in the extension's namespace
	 */
	public static final void addExtension(ActivityExtension extension)
	{
		extensions.put(extension.getNamespace(), extension);
	}

	/**
	 * Creates a new Activity.
	 * 
	 * @param parser the parser to read the tag's attributes and children
	 * from
	 * @param scopeData a description of the current scope
	 * @param type the type of the activity tag
	 */
	protected Activity(XmlPullParser parser, ScopeData scopeData,
		String type)
	{
		this(parser, scopeData, type, BPELServer.namespace);
	}
	
	/**
	 * Creates a new Activity.
	 * 
	 * @param parser the parser to read the tag's attributes and children
	 * from
	 * @param scopeData a description of the current scope
	 * @param type the type of the activity tag
	 * @param namespace the namespace of the activity tag
	 */
	protected Activity(XmlPullParser parser, ScopeData scopeData,
		String type, String namespace)
	{
		this.type = type;
		this.parser = parser;
		this.scopeData = scopeData;
		this.namespace = namespace;
	}

	/**
	 * Parses the next activity tag.  
	 * 
	 * @param parser the parser to read the tag's attributes and children
	 * from
	 * @param scopeData a description of the current scope
	 * @return the next activity tag that the parser read
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	static Activity parse(XmlPullParser parser, ScopeData scopeData) throws
		IOException, XmlPullParserException, MalformedDocumentException
	{
		String type = parser.getName();
		String namespace = parser.getNamespace();
		// Get the name of the next tag
		
		ActivityExtension extension = (ActivityExtension)extensions.get(namespace);
		if(extension == null)
			throw new MalformedBPELException(parser,
				"No BPEL activities were found in the namespace " + namespace);
		
		Activity activity = extension.parseActivity(type, parser, scopeData);
		if(activity == null)
			throw new MalformedBPELException(parser,
				"<" + type + "> is not a BPEL activity");

		return activity;
	}
	
	/**
	 * Creates a new instance of this activity.
	 * 
	 * @param processInstance the process instance that "owns" this activity
	 * @return a {@link ActivityInstance} corresponding to this activity
	 */
	protected abstract ActivityInstance newInstance(
		ProcessInstance processInstance);
	
	/**
	 * Gets a list of any "start" activities (i.e., receive activities with
	 * createInstance="true") contained within this tag.  Generally, most
	 * tags will return an empty list; those that do not (e.g., {@link Receive})
	 * should override this method.
	 * 
	 * @return the list of start activities
	 */
	Transaction [] getStartActivities()
	{
		return new Transaction[0];
	}
	
	/**
	 * Gets the value of an attribute from the parser
	 * 
	 * @param attribute the name of the attribute
	 * @return the value of the attribute, or <tt>null</tt> if the tag has
	 * no such attribute
	 */
	protected final String getAttribute(String attribute)
	{
		return parser.getAttributeValue(null, attribute);
	}
	
	/**
	 * Parses the opening tag, including all standard attributes.
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	protected void parseStartTag() throws XmlPullParserException,
		IOException, MalformedDocumentException
	{
		parser.require(XmlPullParser.START_TAG, namespace, type);
		
		name = getAttribute("name");
		String joinConditionExpr = getAttribute("joinCondition");
		if(joinConditionExpr != null)
			joinCondition = ExpressionParser.parse(joinConditionExpr, parser,
				scopeData);

		suppressJoinFailure = scopeData.isSuppressJoinFailure() ||
			"true".equals(getAttribute("suppressJoinFailure"));
	}
	
	/**
	 * Parses standard child tags.
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	protected void parseStandardElements() throws IOException,
		XmlPullParserException, MalformedDocumentException
	{
		Vector targetVector = new Vector();
		while("target".equals(parser.getName()))
			targetVector.addElement(new Target(parser, this));
		
		targets = new Target[targetVector.size()];
		targetVector.copyInto(targets);

		Vector sourceVector = new Vector();
		while("source".equals(parser.getName()))
			sourceVector.addElement(new Source(parser, scopeData, this));

		sources = new Source[sourceVector.size()];
		sourceVector.copyInto(sources);
	}

	/**
	 * Parses the closing tag.
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 */
	protected void parseEndTag() throws XmlPullParserException, IOException
	{
		parser.require(XmlPullParser.END_TAG, namespace, type);
	}

	/**
	 * Gets the activity's name, if any.
	 * 
	 * @return the activity's name, or <tt>null</tt> if the activity is unnamed
	 */
	public String getName()
	{
		return name;
	}
	
	public String toString()
	{
		if(name == null)
			return "<" + type + ">";
		return name;
	}
}

/**
 * The {@link ActivityExtension} that handles standard BPEL activities
 * (i.e., those in the namespace {@link BPELServer#namespace}.
 * 
 * @author Greg Hackmann
 */
class StandardActivityExtension implements ActivityExtension
{
	public String getNamespace()
	{
		return BPELServer.namespace;
	}

	public Activity parseActivity(String type, XmlPullParser parser,
		ScopeData scopeData) throws IOException, XmlPullParserException,
		MalformedDocumentException
	{
		if("receive".equals(type))
			return new Receive(parser, scopeData);
		else if("reply".equals(type))
			return new Reply(parser, scopeData);
		else if("sequence".equals(type))
			return new Sequence(parser, scopeData);
		else if("flow".equals(type))
			return new Flow(parser, scopeData);
		else if("assign".equals(type))
			return new Assign(parser, scopeData);
		else if("invoke".equals(type))
			return new Invoke(parser, scopeData);
		else if("switch".equals(type))
			return new Switch(parser, scopeData);
		else if("while".equals(type))
			return new While(parser, scopeData);
		else if("empty".equals(type))
			return new Empty(parser, scopeData);
		else if("terminate".equals(type))
			return new Terminate(parser, scopeData);
		else if("scope".equals(type))
			return new Scope(parser, scopeData);
		else if("throw".equals(type))
			return new Throw(parser, scopeData);
		else if("wait".equals(type))
			return new Wait(parser, scopeData);
		else if("pick".equals(type))
			return new Pick(parser, scopeData);
		else
			return null;
		// Delegate the parsing to the appropriate class based on the tag name

		// TODO: compensate
	}
}

