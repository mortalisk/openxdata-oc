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

import org.kxml2.kdom.*;

import edu.wustl.mobilab.sliver.bpel.*;
import edu.wustl.mobilab.sliver.util.*;
import edu.wustl.mobilab.sliver.xpath.*;

/**
 * Represents simple XPath calls to <tt>bpws:getVariableData()</tt>.
 * 
 * @author Greg Hackmann
 */
class GetVariableDataExpr extends MinimalExpression
{
	/**
	 * The variable to read from.
	 */
	private final VariableSpecification variable;
	/**
	 * The part of the variable to read.
	 */
	private String partName = null;
	/**
	 * The path to read from.
	 */
	private String locationPath = null;
	
	/**
	 * Creates a new GetVariableDataExpr.
	 * 
	 * @param expression the getVariableData() call to parse
	 * @param scopeData the current scope
	 *
	 * @throws MalformedExpressionException the expression could not be parsed
	 */
	private GetVariableDataExpr(String expression, ScopeData scopeData)
		throws MalformedExpressionException
	{
		String parameterString = expression.substring("bpws:getVariableData(".length(), expression.length() - 1);
		// Strip off the parentheses and everything outside
		String [] parameters = split(parameterString, ",");
		// Split the parameters
		if(parameters.length == 0)
			throw new MalformedExpressionException("getVariableData() must specify a variableName");

		String variableName = stripQuotations(parameters[0]);
		if(parameters.length > 1)
			partName = stripQuotations(parameters[1]);
		if(parameters.length > 2)
			locationPath = stripQuotations(parameters[2]);
		if(parameters.length > 3)
			throw new MalformedExpressionException("getVariableData() has too many parameters");
		// Save the parameters
		// TODO: these should recursively parse as expressions
		
		variable = scopeData.getVariable(variableName);
		if(variable == null)
			throw new MalformedExpressionException(
				"getVariableData() specified unknown variable " + variableName);
		// Get the corresponding variable
	}
	
	/**
	 * Parses getVariableData() calls.
	 * 
	 * @param expression the getVariableData() call to parse
	 * @param scopeData the current scope
	 * @return a GetVariableDataExpr representing the inputted expression, or
	 * <tt>null</tt> if the expression was not a getVariableData() call
	 *
	 * @throws MalformedExpressionException the expression could not be parsed
	 */
	static Expression parseGetVariableData(String expression, ScopeData scopeData)
		throws MalformedExpressionException
	{
		if(expression.startsWith("bpws:getVariableData("))
			return new GetVariableDataExpr(expression, scopeData);
		
		return null;
	}

	public Object evaluate(ScopeData scopeData, ProcessInstance processInstance)
		throws ExpressionEvaluationException
	{
		try
		{
			Element part = processInstance.getVariablePart(scopeData, variable,
														partName, locationPath);
			return DOM.getText(part);
			// Look up the part being referred to, then convert the element to
			// an object
		}
		catch(Throwable t)
		{
			throw new ExpressionEvaluationException(
				"Could not evaluate bpws:getVariableData()", t);
		}
	}
	
	public String toString()
	{
		String returnMe = "bpws:getVariableData(" + variable;
		if(partName != null)
			returnMe += ", " + partName;
		if(locationPath != null)
			returnMe += ", " + locationPath;
		returnMe += ")";

		return returnMe;
	}
}
