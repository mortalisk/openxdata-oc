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

package edu.wustl.mobilab.sliver.util;

import org.xmlpull.v1.*;

/**
 * A helper class that processes fully-qualified names (<code>prefix:name</code>).
 * 
 * @author Greg Hackmann
 */
public abstract class Namespace
{	
	/**
	 * Splits a fully-qualified name into its prefix and unqualified name.
	 * 
	 * @param qualified the fully-qualified name
	 * @return an array of the form [ prefix, unqualified name ]
	 */
	public static String [] splitNamespace(String qualified)
	{
		int colonAt = qualified.indexOf(':');
		if(colonAt == -1)
			return new String [] { null, qualified };
		// If there's no ":", then we can't split the prefix off
		
		String prefix = qualified.substring(0, colonAt);
		String name = qualified.substring(colonAt + 1);
		// Split the name at the ":" mark
		
		return new String [] { prefix, name };
	}
	
	/**
	 * Splits a fully-qualified name into its full namespace and unqualified
	 * name.
	 * 
	 * @param qualified the fully-qualified name
	 * @param parser the {@link XmlPullParser} used to parse the document
	 * @return an array of the form [ expanded namespace, unqualified name ]
	 */
	public static String [] expandNamespace(String qualified,
		XmlPullParser parser)
	{
		String [] split = splitNamespace(qualified);
		// Split the name
		split[0] = split[0] == null ? parser.getNamespace() :
			parser.getNamespace(split[0]);
		// Expand the namespace; if the namespace is null, then there was no
		// prefix, so assume the current namespace
		
		return split;
	}
}
