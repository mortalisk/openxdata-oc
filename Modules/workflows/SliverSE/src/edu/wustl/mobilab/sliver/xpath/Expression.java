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

import edu.wustl.mobilab.sliver.bpel.*;

/**
 * Represents a parsed XPath expression.
 * 
 * @author Greg Hackmann
 */
public abstract class Expression
{
	/**
	 * The URI of the XPath language specification.
	 */
	public static final String uri =
		"http://www.w3.org/TR/1999/REC-xpath-19991116";

	/**
	 * Evaluates the XPath expression.
	 * 
	 * @param scopeData the current scope
	 * @param processInstance the current process instance
	 * @return the result of evaluating the expression
	 * 
	 * @throws ExpressionEvaluationException an error occured while evaluating
	 * the expression
	 */
	public abstract Object evaluate(ScopeData scopeData,
		ProcessInstance processInstance) throws ExpressionEvaluationException;

	/**
	 * Evaluates the XPath expression; the result is a boolean value. This
	 * method will throw an exception if the expression does not actually
	 * produce a boolean value.
	 * 
	 * @param scopeData the current scope
	 * @param processInstance the current process instance
	 * @return the boolean result of evaluating the expression
	 * 
	 * @throws ExpressionEvaluationException an error occured while evaluating
	 * the expression
	 */
	public boolean evaluateBoolean(ScopeData scopeData,
		ProcessInstance processInstance) throws ExpressionEvaluationException
	{
		try
		{
			return ((Boolean)evaluate(scopeData, processInstance)).booleanValue();
		}
		catch(ClassCastException e)
		{
			throw new ExpressionEvaluationException("\"" + toString()
				+ "\" did not evaluate to a boolean", e);
		}
	}

	/**
	 * Evaluates the XPath expression; the result is a number. This method will
	 * throw an exception if the expression does not actually produce a number.
	 * 
	 * @param scopeData the current scope
	 * @param processInstance the current process instance
	 * @return the double result of evaluating the expression
	 * 
	 * @throws ExpressionEvaluationException an error occured while evaluating
	 * the expression
	 */
	public double evaluateNumber(ScopeData scopeData, ProcessInstance processInstance)
		throws ExpressionEvaluationException
	{
		try
		{
			return ((Double)evaluate(scopeData, processInstance)).doubleValue();
		}
		catch(ClassCastException e)
		{
			throw new ExpressionEvaluationException("\"" + toString()
				+ "\" did not evaluate to a number", e);
		}
	}

	/**
	 * Evaluates the XPath expression; the result is a literal. This method will
	 * throw an exception if the expression does not actually produce a literal.
	 * 
	 * @param scopeData the current scope
	 * @param processInstance the current process instance
	 * @return the String result of evaluating the expression
	 * 
	 * @throws ExpressionEvaluationException an error occured while evaluating
	 * the expression
	 */
	public String evaluateLiteral(ScopeData scopeData, ProcessInstance processInstance)
		throws ExpressionEvaluationException
	{
		try
		{
			return (String)evaluate(scopeData, processInstance);
		}
		catch(ClassCastException e)
		{
			throw new ExpressionEvaluationException("\"" + toString()
				+ "\" did not evaluate to a string", e);
		}
	}
}
