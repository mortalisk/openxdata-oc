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

package edu.wustl.mobilab.sliver.xpath.minimal;

import java.util.*;

import edu.wustl.mobilab.sliver.bpel.*;
import edu.wustl.mobilab.sliver.xpath.*;

/**
 * Represents simple XPath expressions.
 * 
 * @author Greg Hackmann
 */
abstract class MinimalExpression extends Expression
{
	/**
	 * Splits a string at a designated seperator.
	 * 
	 * @param expression the string to split
	 * @param seperator the seperator used to divide the string
	 * @return the split pieces of the string
	 */
	protected String [] split(String expression, String seperator)
	{
		Vector parts = new Vector();
		String remaining = expression;
		int seperatorAt;
		
		// While the seperator's still in the remaining part of the string
		while((seperatorAt = remaining.indexOf(seperator)) >= 0)
		{
			parts.addElement(remaining.substring(0, seperatorAt).trim());
			// Split off everything before the seperator
			remaining = remaining.substring(seperatorAt + seperator.length());
			// Update the remaining part to everything after the seperator
		}
		parts.addElement(remaining.trim());
		// Also save whatever's left after we've consumed all the seperators
		
		String [] partsArray = new String[parts.size()];
		parts.copyInto(partsArray);
		// Copy the pieces into an array and return it
		
		return partsArray;
	}
	
	/**
	 * Parses XPath expressions.
	 * 
	 * @param expression the XPath expression to parse
	 * @param scopeData the current scope
	 * @return a {@link Expression} representing the inputted expression
	 * 
	 * @throws MalformedExpressionException the expression could not be parsed

	 */
	public static Expression parse(String expression, ScopeData scopeData)
		throws MalformedExpressionException
	{
		Expression parsed = RelationalExpr.parseRelational(expression, scopeData);
		if(parsed == null)
			parsed = GetVariableDataExpr.parseGetVariableData(expression, scopeData);
		if(parsed == null)
			parsed = new ImmediateExpr(expression);
		// Try to parse with the following "order of operations":
		// RelationalExpr, bpws:getVariableData(), immediates
		
		return parsed;
	}
	
	/**
	 * Strips single and double quotation marks from the ends of strings.
	 * 
	 * @param in the string to strip the quotes from
	 * @return the inputted string, minus the surrounding quotation marks
	 * @throws MalformedExpressionException the inputted string did not have
	 * quotation marks around it
	 */
	protected String stripQuotations(String in) throws
		MalformedExpressionException
	{
		if((in.charAt(0) == '\'' && in.charAt(in.length() - 1) == '\'') ||
			(in.charAt(0) == '\"' && in.charAt(in.length() - 1) == '\"'))
			return in.substring(1, in.length() - 1);
		// Make sure the string is surrounded by the same kind of quote
		
		throw new MalformedExpressionException("String expected in expression, but read " + in);
	}
	
	/**
	 * Compares two values. This method has the same semantics as
	 * {@link Comparable#compareTo(Object)}; i.e., 0 means that they are
	 * equal, -1 means that the first is smaller, and 1 means that the first is
	 * larger.
	 * 
	 * @param o1 the first object to compare
	 * @param o2 the second object to compare
	 * @return See {@link Comparable#compareTo(Object)}
	 */
	protected static int compare(Object o1, Object o2)
	{
		// TODO: whine when types are incompatible
		
		if(o1 instanceof String)
			return ((String)o1).compareTo((String)o2);
		
		if(o1 instanceof Boolean)
		{
			boolean b1 = ((Boolean)o1).booleanValue();
			boolean b2 = ((Boolean)o2).booleanValue();
			
			if(b1 == b2)
				return 0;
			if(b1 && !b2)
				return 1;
			return -1;
		}
		
		double diff = ((Double)o2).doubleValue() - 
				((Double)o1).doubleValue();
			
		if(diff > 0)
			return -1;
		else if(diff < 0)
			return 1;
		return 0;
	}
}
