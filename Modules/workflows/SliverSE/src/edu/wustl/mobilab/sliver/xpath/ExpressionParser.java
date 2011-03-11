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

package edu.wustl.mobilab.sliver.xpath;

import org.xmlpull.v1.*;

import edu.wustl.mobilab.sliver.bpel.*;
import edu.wustl.mobilab.sliver.xpath.minimal.*;

/**
 * Parses XPath expressions into {@link Expression} objects.
 * 
 * By default, invoking {@link #parse(String, XmlPullParser, ScopeData)} will
 * use {@link MinimalExpressionParser} to parse XPath expressions.  Use
 * {@link #setExpressionParser(ExpressionParser)} to select an alternate parser.
 * 
 * @see edu.wustl.mobilab.sliver.xpath.minimal.MinimalExpressionParser
 * <!--@see edu.wustl.mobilab.sliver.xpath.full.FullExpressionParser-->
 * 
 * @author Greg Hackmann
 */
public abstract class ExpressionParser
{
	/**
	 * The default parser that should be used to generate {@link Expression}s.
	 */
	private static ExpressionParser parser = new MinimalExpressionParser();

	/**
	 * Sets the default ExpressionParser.
	 * 
	 * @param parsr the new default ExpressionParser.
	 */
	public static void setExpressionParser(ExpressionParser parsr)
	{
		parser = parsr;
	}

	/**
	 * Parses XPath expressions.
	 * 
	 * @param expression the XPath expression encoded as a String
	 * @param xpp the {@link XmlPullParser} that was used to read the XML
	 * document that the expression came from
	 * @param scopeData the current scope
	 * @return a parsed version of the expression
	 * 
	 * @throws MalformedExpressionException the expression could not be parsed
	 * properly
	 */
	public static Expression parse(String expression, XmlPullParser xpp,
		ScopeData scopeData) throws MalformedExpressionException
	{
		return parser.parseImpl(expression, xpp, scopeData);
	}
	
	/**
	 * Performs the actual XPath parsing. This method must be implemented by
	 * each concrete ExpressionParser.
	 * 
	 * @param expression the XPath expression encoded as a String
	 * @param xpp the {@link XmlPullParser} that was used to read the XML
	 * document that the expression came from
	 * @param scopeData the current scope
	 * @return a parsed version of the expression
	 * 
	 * @throws MalformedExpressionException the expression could not be parsed
	 * properly
	 */
	protected abstract Expression parseImpl(String expression,
		XmlPullParser xpp, ScopeData scopeData)
		throws MalformedExpressionException;	
}