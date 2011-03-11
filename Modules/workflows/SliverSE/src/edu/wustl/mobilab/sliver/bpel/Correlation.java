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
 * Represents BPEL &lt;correlation&gt; tags.
 * 
 * @author Greg Hackmann
 */
class Correlation
{
	/**
	 * The name of the correlation set.
	 */
	private final String set;
	/**
	 * Whether or not the correlation can initiate a new correlation set.
	 */
	private final boolean initiate;
	/**
	 * The correlation's pattern type (one of {@link #IN}, {@link #OUT},
	 * or {@link #OUT_IN})
 	 */
	private final int pattern;
	
	/**
	 * An unspecified pattern type.
	 */
	static final int UNSPECIFIED = -1;
	/**
	 * The "in" pattern type.
	 */
	static final int IN = 0;
	/**
	 * The "out" pattern type.
	 */
	static final int OUT = 1;
	/**
	 * The "out-in" pattern type.
	 */
	static final int OUT_IN = 2;

	/**
	 * Creates a new Correlation.
	 * 
	 * @param parser the parser to read the correlation's attributes from
	 * @param defaultPattern the default pattern type to assume (one of
	 * {@link #IN}, {@link #OUT}, {@link #OUT_IN}, or {@link #UNSPECIFIED}) 
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	Correlation(XmlPullParser parser, int defaultPattern) throws IOException,
		XmlPullParserException, MalformedDocumentException
	{
		parser.require(XmlPullParser.START_TAG, BPELServer.namespace,
			"correlation");

		set = parser.getAttributeValue(null, "set");
		if(set == null)
			throw new MalformedBPELException(parser,
				"<correlation> must specify set");
		// Read the set name

		initiate = "yes".equals(parser.getAttributeValue(null, "initiate"));
		// Read the initiate flag

		String patternAttrib = parser.getAttributeValue(null, "pattern");
		if("in".equals(patternAttrib))
			pattern = IN;
		else if("out".equals(patternAttrib))
			pattern = OUT;
		else if("out-in".equals(patternAttrib))
			pattern = OUT - IN;
		// First try to match one of the known pattern types
		else
		{
			if(defaultPattern == UNSPECIFIED)
				throw new MalformedBPELException(parser,
					"<correlation> must specify pattern");
			// If none on the types matched and no default is specified, then
			// complain

			pattern = defaultPattern;
			// Otherwise accept the default
		}

		parser.nextTag();
		parser.require(XmlPullParser.END_TAG, BPELServer.namespace,
			"correlation");
		parser.nextTag();
	}

	/**
	 * Gets the name of the correlation set.
	 * 
	 * @return the name of the correlation set
	 */
	String getSet()
	{
		return set;
	}
	
	/**
	 * Gets whether or not the correlation can initiate a new correlation set.
	 * 
	 * @return whether or not the correlation can initiate a new correlation set
	 */
	boolean isInitiate()
	{
		return initiate;
	}

	/**
	 * Gets the correlation's pattern type.
	 * 
	 * @return the correlation's pattern type (one of {@link #IN}, {@link #OUT},
	 * or {@link #OUT_IN})
	 */
	int getPattern()
	{
		return pattern;
	}
}
