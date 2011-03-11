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

import edu.wustl.mobilab.sliver.bpel.*;
import edu.wustl.mobilab.sliver.xpath.*;

/**
 * Represents simple XPath immediate expressions. Immediates include literals
 * (strings), numbers (doubles), and boolean values.
 * 
 * @author Greg Hackmann
 */
class ImmediateExpr extends MinimalExpression
{
	/**
	 * The value that this immediate represents.
	 */
	private final Object value;

	/**
	 * Creates a new ImmediateExpr.
	 * 
	 * @param expression the immediate expression to parse
	 *
	 * @throws MalformedExpressionException the expression could not be parsed
	 */
	ImmediateExpr(String expression) throws MalformedExpressionException
	{
		if(expression.charAt(0) == '\'' || expression.charAt(0) == '\"')
			value = stripQuotations(expression);
		// If the expression has quotes, it's a literal
		else if(expression.equals("true()") || expression.equals("false()"))
			value = new Boolean(expression.equals("true()"));
		// If it's "true" or "false", it's a boolean
		else try
		{
			value = Double.valueOf(expression);
		}
		catch(NumberFormatException e)
		{
			throw new MalformedExpressionException("Cannot parse expression "
			                                       + expression);
		}
		// Otherwise, it must be a number
	}

	public Object evaluate(ScopeData scopeData, ProcessInstance processInstance)
		throws ExpressionEvaluationException
	{
		return value;
	}

	public String toString()
	{
		return value.toString();
	}
}
