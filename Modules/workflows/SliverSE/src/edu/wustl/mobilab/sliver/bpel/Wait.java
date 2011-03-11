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
 * Represents and executes BPEL &lt;wait&gt; tags.
 * 
 * @author Greg Hackmann
 */
class Wait extends Activity
{
	/**
	 * The {@link WaitSpecification} that defines how long to sleep.
	 */
	protected final WaitSpecification waitSpec;
	
	/**
	 * Creates a new Wait.
	 * 
	 * @param parser the parser to read the wait's attributes from
	 * @param scopeData a description of the current scope
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	Wait(XmlPullParser parser, ScopeData scopeData) throws IOException,
		XmlPullParserException, MalformedDocumentException
	{
		this(parser, scopeData, "wait");
	}

	/**
	 * Creates a new Wait.
	 * 
	 * @param parser the parser to read the wait's attributes from
	 * @param scopeData a description of the current scope
	 * @param tagName the tag's name (normally <tt>"wait"</tt>)
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	protected Wait(XmlPullParser parser, ScopeData scopeData, String tagName)
		throws IOException, XmlPullParserException, MalformedDocumentException
	{
		super(parser, scopeData, tagName);
		
		String forString = getAttribute("for");
		String untilString = getAttribute("until");
		// Get the "for" and "until" attributes
		
		if(forString == null && untilString == null)
			throw new MalformedBPELException(parser,
				"<" + tagName + "> must specify for or until");
		if(forString != null && untilString != null)
			throw new MalformedBPELException(parser,
				"<" + tagName + "> cannot specify both for and until");
		// Make sure we have exactly one of "for" and "until"
		
		// If the tag specifies "for"
		if(forString != null)
		{
			waitSpec = new WaitFor(forString, parser, scopeData);
			// Parse the "for" attribute
		}
		// Otherwise, it specified an "until"
		else
		{
			waitSpec = new WaitUntil(untilString, parser, scopeData);
			// Parse the "until" attribute
		}
		
		parser.nextTag();
		parseStandardElements();

		parseEndTag();
		parser.nextTag();
	}
	
	protected ActivityInstance newInstance(ProcessInstance processInstance)
	{
		return new WaitInstance(this, processInstance);
	}
}

/**
 * Represents an executable instance of BPEL &lt;wait&gt; tags.
 * 
 * @author Greg Hackmann
 */
class WaitInstance extends ActivityInstance
{
	/**
	 * The {@link WaitSpecification} that defines how long to sleep.
	 */
	private WaitSpecification waitSpec;

	/**
	 * Creates a new WaitInstance.
	 * 
	 * @param wait the {@link Wait} that will be executed
	 * @param processInstance the process instance that this activity instance is
	 * being executed in
	 */
	public WaitInstance(Wait wait, ProcessInstance processInstance)
	{
		super(wait, processInstance);
		this.waitSpec = wait.waitSpec;
	}

	protected Signal executeImpl()
	{
		try
		{			
			waitSpec.sleep(processInstance);
		}
		catch(InterruptedException e)
		{
			if(state == State.CANCELING)
				return Signal.CANCELED;
								
			return new FaultedSignal(BPELServer.sliverNamespace,
				"interruptedException", e);
		}
		catch(ExpressionEvaluationException e)
		{
			return new FaultedSignal(BPELServer.sliverNamespace,
				"expressionEvaluationException", e);
		}
		// Try to sleep, and throw a fault if we were interrupted in the
		// middle
		
		return Signal.COMPLETED;
	}
		
	protected synchronized void cancelImpl()
	{
		activityThread.interrupt();
	}
}

/**
 * Specifies how long the {@link Wait} activity should sleep.
 * Used by {@link Wait#newInstance(ProcessInstance)}.
 * 
 * @see WaitFor
 * @see WaitUntil
 * @author Greg Hackmann
 */
abstract class WaitSpecification
{
	
	/**
	 * Gets the length of time that the thread should sleep.
	 * 
	 * @param processInstance the currently-executing process instance 
	 * @return the length of time that the thread should sleep,
	 * in milliseconds
	 * 
	 * @throws ExpressionEvaluationException the expression that determines
	 * the interval could not be evaluated
	 */
	protected abstract long getInterval(ProcessInstance processInstance) throws
		ExpressionEvaluationException;
	
	/**
	 * Sleeps for the specified amount of time.
	 * 
	 * @param processInstance the currently-executing process instance
	 * 
	 * @throws InterruptedException the thread was interrupted while sleeping
	 * @throws ExpressionEvaluationException the expression that determines
	 * the interval could not be evaluated
	 */
	synchronized void sleep(ProcessInstance processInstance)
		throws ExpressionEvaluationException, InterruptedException
	{
		long interval = getInterval(processInstance);
		// Find out how long to sleep
		
		if(interval > 0)
			wait(interval);
		// If we haven't already passed the deadline, then sleep that long
	}
}

/**
 * Sleeps for a specified interval.
 * 
 * @author Greg Hackmann
 */
class WaitFor extends WaitSpecification
{
	/**
	 * The number of milliseconds in 1 second.
	 */
	private static final long S_TO_MS = 1000;
	/**
	 * The number of milliseconds in 1 minute.
	 */
	private static final long M_TO_MS = 60 * S_TO_MS;
	/**
	 * The number of milliseconds in 1 hour.
	 */
	private static final long H_TO_MS = 60 * M_TO_MS;
	/**
	 * The number of milliseconds in 1 day.
	 */
	private static final long D_TO_MS = 24 * H_TO_MS;
	
	/**
	 * The scope that contains the corresponding {@link Wait} activity.
	 */
	private final ScopeData scopeData;
	/**
	 * An XPath expression representing for how long the thread should sleep.
	 */
	private final Expression forExpression;
	
	/**
	 * Creates a new WaitFor.
	 * 
	 * @param forString an XPath expression, which evaluates to an
	 * XSD-encoded duration that specifies how long the thread should sleep
	 * @param parser the {@link XmlPullParser} that was used to parse the BPEL
	 * document
	 * @param scopeData the scope that contains the corresponding {@link Wait}
	 * activity
	 * 
	 * @throws MalformedExpressionException the forString was invalid
	 */
	WaitFor(String forString, XmlPullParser parser, ScopeData scopeData)
		throws MalformedExpressionException
	{
		this.scopeData = scopeData;
		forExpression = ExpressionParser.parse(forString, parser, scopeData);
	}
	
	protected long getInterval(ProcessInstance processInstance) throws
		ExpressionEvaluationException
	{
		String evaluated = forExpression.evaluateLiteral(scopeData, processInstance);
		Duration forDate = new Duration(evaluated);
		// Evaluate the expression, then parse the result
		
		// Note that we have to do some weird calculations since the lengths
		// of months and years isn't constant.  Fortunately, we can make
		// Java do most of those calculations for us.
		
		Calendar calendar = Calendar.getInstance();
		long now = calendar.getTime().getTime();
		// Get the current time so we can gauge the length of months and years
	
		int years = calendar.get(Calendar.YEAR) + forDate.getYears();
		int months = calendar.get(Calendar.MONTH) + forDate.getMonths();
		// Get the end time's months and years
		years += months / 12;
		months = months % 12;
		// Convert months past December into more years
		calendar.set(Calendar.YEAR, years);
		calendar.set(Calendar.MONTH, months);
		long later = calendar.getTime().getTime();
		// Find out how many milliseconds difference there is between the 
		// months and years
		
		long interval = forDate.getSeconds() * S_TO_MS +
			forDate.getMinutes() * M_TO_MS +
			forDate.getHours() * H_TO_MS +
			forDate.getDays() * D_TO_MS +
			later - now;
		// Convert the date into milliseconds
		
		return interval;
	}
}

/**
 * Sleeps until a specified date/time.
 * 
 * @author Greg Hackmann
 */
class WaitUntil extends WaitSpecification
{
	/**
	 * The scope that contains the corresponding {@link Wait} activity.
	 */
	private final ScopeData scopeData;
	/**
	 * An XPath expression representing until when the thread should sleep.
	 */
	private final Expression untilExpression;

	/**
	 * Creates a new WaitUntil.
	 * 
	 * @param untilString an XPath expression, which evaluates to an
	 * XSD-encoded dateTime that specifies until when the thread should sleep
	 * @param parser the {@link XmlPullParser} that was used to parse the BPEL
	 * document
	 * @param scopeData the scope that contains the corresponding {@link Wait}
	 * activity
	 * 
	 * @throws MalformedExpressionException the untilString was invalid
	 */
	WaitUntil(String untilString, XmlPullParser parser, ScopeData scopeData)
		throws MalformedExpressionException
	{
		this.scopeData = scopeData;
		untilExpression = ExpressionParser.parse(untilString, parser, scopeData);
	}

	protected long getInterval(ProcessInstance processInstance)
		throws ExpressionEvaluationException
	{
		String evaluated = untilExpression.evaluateLiteral(scopeData,
			processInstance);
		// Evaluate the expression

		Date forDate = DateParser.parseDateTime(evaluated).getTime();
		long until = forDate.getTime();
		// Parse the result of the evaluation

		long interval = until - Calendar.getInstance().getTime().getTime();
		// Find how long is left until the right time arrives

		return interval;
	}
}