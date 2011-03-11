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
 * Represents simple XPath RelationalExpr expressions.
 * RelationalExpr expressions are simple boolean comparison operators
 * (i.e., &lt, &lte, &gt, &gte, =, and !=).
 * 
 * @author Greg Hackmann
 */
abstract class RelationalExpr extends MinimalExpression
{
	/**
	 * The expression on the left side of the operator.
	 */
	private final Expression leftExpr;
	/**
	 * The expression on the right side of the operator.
	 */
	private final Expression rightExpr;
	/**
	 * The String representation of the comparison operator (e.g., "&lt;").
	 */
	private final String operator;
	
	/**
	 * Creates a new RelationalExpr.
	 * 
	 * @param expression the RelationalExpr expression to parse
	 * @param scopeData the current scope
	 * @param operator a String representation of the comparison operator (e.g., "&lt;")
	 *
	 * @throws MalformedExpressionException the expression could not be parsed
	 */
	protected RelationalExpr(String expression, ScopeData scopeData, String operator)
		throws MalformedExpressionException
	{
		this.operator = operator;
		String [] split = split(expression, operator);
		// Split the string at the operator
		
		leftExpr = MinimalExpression.parse(split[0], scopeData);
		rightExpr = MinimalExpression.parse(split[1], scopeData);
		// Then parse both sides seperately
	}
	
	/**
	 * Parses RelationalExpr strings.
	 * 
	 * @param expression the RelationalExpr expression to parse
	 * @param scopeData the current scope
	 * @return a RelationalExpr representing the inputted expression, or
	 * <tt>null</tt> if the expression was not a RelationalExpr
	 * 
	 * @throws MalformedExpressionException the expression could not be parsed
	 */
	static Expression parseRelational(String expression, ScopeData scopeData)
		throws MalformedExpressionException
	{
		if(expression.indexOf("<=") >= 0)
			return new LessThanOrEquals(expression, scopeData);
		else if(expression.indexOf(">=") >= 0)
			return new GreaterThanOrEquals(expression, scopeData);
		else if(expression.indexOf("<") >= 0)
			return new LessThan(expression, scopeData);
		else if(expression.indexOf(">") >= 0)
			return new GreaterThan(expression, scopeData);
		else if(expression.indexOf("!=") >= 0)
			return new NotEquals(expression, scopeData);
		else if(expression.indexOf("=") >= 0)
			return new Equals(expression, scopeData);
		// Construct the appropriate expression based on the operators that we find
		
		return null;
		// If no operators are found, then this isn't a RelationalExpr
	}
	
	public Object evaluate(ScopeData scopeData, ProcessInstance processInstance)
		throws ExpressionEvaluationException
	{
		Object leftValue = leftExpr.evaluate(scopeData, processInstance);
		Object rightValue = rightExpr.evaluate(scopeData, processInstance);
		// Evaluate both halves of the expression
		
		return new Boolean(evaluate(leftValue, rightValue));
		// Then compare the halves
	}
	
	/**
	 * Evaluates the relational expression.
	 * 
	 * @param left the left half of the comparison
	 * @param right the right half of the comparison
	 * @return whether or not the relation holds
	 */
	protected abstract boolean evaluate(Object left, Object right);
	
	public String toString()
	{
		return leftExpr + " " + operator + " " + rightExpr;
	}
}

/**
 * Represents XPath &lt; comparisons.
 * 
 * @author Greg Hackmann
 */
class LessThan extends RelationalExpr
{	
	/**
	 * Creates a new LessThan.
	 * 
	 * @param expression the &lt; comparison to parse
	 * @param scopeData the current scope
	 *
	 * @throws MalformedExpressionException the expression could not be parsed
	 */
	LessThan(String expression, ScopeData scopeData)
		throws MalformedExpressionException
	{
		super(expression, scopeData, "<");
	}

	protected boolean evaluate(Object left, Object right)
	{
		return compare(left, right) < 0;
	}
}

/**
 * Represents XPath &lt;= comparisons.
 * 
 * @author Greg Hackmann
 */
class LessThanOrEquals extends RelationalExpr
{	
	/**
	 * Creates a new LessThanOrEquals.
	 * 
	 * @param expression the &lt;= comparison to parse
	 * @param scopeData the current scope
	 *
	 * @throws MalformedExpressionException the expression could not be parsed
	 */
	LessThanOrEquals(String expression, ScopeData scopeData)
		throws MalformedExpressionException
	{
		super(expression, scopeData, "<=");
	}

	protected boolean evaluate(Object left, Object right)
	{
		return compare(left, right) <= 0;
	}
}

/**
 * Represents XPath &gt; comparisons.
 * 
 * @author Greg Hackmann
 */
class GreaterThan extends RelationalExpr
{	
	/**
	 * Creates a new GreaterThan.
	 * 
	 * @param expression the &gt; comparison to parse
	 * @param scopeData the current scope
	 *
	 * @throws MalformedExpressionException the expression could not be parsed
	 */
	GreaterThan(String expression, ScopeData scopeData)
		throws MalformedExpressionException
	{
		super(expression, scopeData, ">");
	}

	protected boolean evaluate(Object left, Object right)
	{
		return compare(left, right) > 0;
	}
}

/**
 * Represents XPath &gt;= comparisons.
 * 
 * @author Greg Hackmann
 */
class GreaterThanOrEquals extends RelationalExpr
{	
	/**
	 * Creates a new GreaterThanOrEquals.
	 * 
	 * @param expression the &gt;= comparison to parse
	 * @param scopeData the current scope
	 *
	 * @throws MalformedExpressionException the expression could not be parsed
	 */
	GreaterThanOrEquals(String expression, ScopeData scopeData)
		throws MalformedExpressionException
	{
		super(expression, scopeData, ">=");
	}

	protected boolean evaluate(Object left, Object right)
	{
		return compare(left, right) >= 0;
	}
}

/**
 * Represents XPath != comparisons.
 * 
 * @author Greg Hackmann
 */
class NotEquals extends RelationalExpr
{	
	/**
	 * Creates a new NotEquals.
	 * 
	 * @param expression the != comparison to parse
	 * @param scopeData the current scope
	 *
	 * @throws MalformedExpressionException the expression could not be parsed
	 */
	NotEquals(String expression, ScopeData scopeData)
		throws MalformedExpressionException
	{
		super(expression, scopeData, "!=");
	}

	protected boolean evaluate(Object left, Object right)
	{
		return compare(left, right) != 0;
	}
}

/**
 * Represents XPath == comparisons.
 * 
 * @author Greg Hackmann
 */
class Equals extends RelationalExpr
{	
	/**
	 * Creates a new Equals.
	 * 
	 * @param expression the = comparison to parse
	 * @param scopeData the current scope
	 *
	 * @throws MalformedExpressionException the expression could not be parsed
	 */
	Equals(String expression, ScopeData scopeData)
		throws MalformedExpressionException
	{
		super(expression, scopeData, "=");
	}

	protected boolean evaluate(Object left, Object right)
	{
		return compare(left, right) == 0;
	}
}
