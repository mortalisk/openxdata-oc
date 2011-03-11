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

import org.ksoap2.*;
import org.kxml2.kdom.*;
import org.xmlpull.v1.*;

import edu.wustl.mobilab.sliver.soap.*;
import edu.wustl.mobilab.sliver.util.*;

/**
 * Represents and executes BPEL &lt;invoke&gt; tags.
 * 
 * @author Greg Hackmann
 */
class Invoke extends Transaction
{
	/**
	 * The variable that provides input to the invocation.
	 */
	final VariableSpecification inputVariable;
	/**
	 * The variable that stores the invocation's output.
	 */
	final VariableSpecification outputVariable;
	/**
	 * The invocation's compensation handler.
	 */
	private Activity compensationHandler = null;
	/**
	 * The invocation's fault handlers.
	 */
	final Catch [] catches;
	/**
	 * The invocation's default fault handler.
	 */	
	final CatchAll catchAll;
	
	/**
	 * Creates a new Invoke.
	 * 
	 * @param parser the parser to read the invoke's attributes from
	 * @param scopeData a description of the current scope
	 * 
	 * @throws IOException there was an I/O error while reading from the parser
	 * @throws XmlPullParserException the XML parser read malformed data
	 * @throws MalformedDocumentException the BPEL parser read malformed data
	 */
	Invoke(XmlPullParser parser, ScopeData scopeData) throws IOException,
		XmlPullParserException, MalformedDocumentException
	{
		super(parser, scopeData.createNestedScope(), "invoke");
		parseStartTag();
		this.scopeData.setName(name);
		
		inputVariable = getVariableSpecification("inputVariable");
		outputVariable = getVariableSpecification("outputVariable");
		// Read the input and output variables
		
		parser.nextTag();
		parseStandardElements();
		parseCorrelations(Correlation.UNSPECIFIED);
		// Parse the correlations (if any)
		
		Vector catchVector = new Vector();
		while("catch".equals(parser.getName()))
			catchVector.addElement(new Catch(parser, this.scopeData));
		catches = new Catch[catchVector.size()];
		catchVector.copyInto(catches);
		// Parse all the child <catch>s

		if("catchAll".equals(parser.getName()))
			catchAll = new CatchAll(parser, this.scopeData);
		else
			catchAll = null;
		// If there's a <catchAll>, parse it

		if("compensationHandler".equals(parser.getName()))
		{
			parser.require(XmlPullParser.START_TAG, BPELServer.namespace,
				"compensationHandler");
			parser.nextTag();

			compensationHandler = Activity.parse(parser, scopeData);

			parser.require(XmlPullParser.END_TAG, BPELServer.namespace,
				"compensationHandler");
			parser.nextTag();
		}
		// If there's a <compensationHandler>, parse it
		
		parseEndTag();
		parser.nextTag();
	}
	
	protected ActivityInstance newInstance(ProcessInstance processInstance)
	{
		return new InvokeInstance(this, processInstance);
	}
}

/**
 * Represents an executable instance of BPEL &lt;invoke&gt; tags.
 * 
 * @author Greg Hackmann
 */
class InvokeInstance extends ActivityInstance
{
	/**
	 * The {@link Invoke} that will be executed.
	 */
	private final Invoke invoke;
	/**
	 * The {@link Channel} that is bound to the incoming partner link.
	 */
	private Channel channel = null;

	/**
	 * The fault handler that is currently executing.
	 */
	private ActivityInstance catchInstance;


	/**
	 * Creates a new InvokeInstance.
	 * 
	 * @param invoke the {@link Invoke} that will be executed
	 * @param processInstance the process instance that this activity instance is
	 * being executed in
	 */
	InvokeInstance(Invoke invoke, ProcessInstance processInstance)
	{
		super(invoke, processInstance);
		this.invoke = invoke;
	}

	protected Signal executeImpl()
	{
		Binding binding;
		Element call;
		
		synchronized(this)
		{
			binding = invoke.partnerLink.getOutgoingBinding(processInstance);
			if(binding == null)
			{
				String error = "Partner link " + invoke.partnerLink +  " unbound";
				BPELServer.log.severe(error);
				
				IllegalArgumentException e = new IllegalArgumentException(error);
				return new FaultedSignal(BPELServer.sliverNamespace,
					"partnerLinkUnbound", e);
			}
			// Get the binding
			
			call = new Element();
			call.setName(invoke.operation);
			call.setNamespace(invoke.portTypeNamespace);
			// Create the call object
			
			if(invoke.inputVariable != null)
			{
				Element variable = processInstance.getVariable(invoke.inputVariable);
				int numChildren = variable.getChildCount();
	
				for(int i = 0; i < numChildren; i++)
					call.addChild(variable.getType(i), variable.getChild(i));
			}
			// If there's an input variable, copy its contents into the call
			
			try
			{
				channel = binding.openChannel();
				BPELServer.log.info(toString() + " channel to " + binding +
					" opened");
			}
			catch(IOException e)
			{
				BPELServer.log.severe(toString() + ": IOException " + e +
					" thrown while opening channel to " + binding);
				e.printStackTrace();
				
				return new FaultedSignal(BPELServer.sliverNamespace,
					"ioException", e);
			}
			// Open a channel to the remote service
		}
		
		synchronized(channel)
		{
			try
			{
				Element returnValue = channel.call(call);
				BPELServer.log.info(toString() + ": called remote service " +
					invoke.operation + "()");
	
				if(invoke.outputVariable != null)
					processInstance.setVariable(invoke.outputVariable, returnValue);
				
				// Call the remote service and copy the return value into the 
				// output variable, if any
			}
			// If the remote server threw a fault
			catch(SoapFault e)
			{
				BPELServer.log.warning(toString() + ": fault " + e +
					" caught during invoke");
				
				Element detail = (Element)e.detail.getChild(0);
				if(detail.getType(0) != Node.ELEMENT)
				{
					BPELServer.log.severe("Cannot understand fault");
					return new FaultedSignal(BPELServer.namespace,
						"invokeFault", e);
				}
				// If the child isn't an element, then we can't handle the fault
				
				synchronized(this)
				{
					if(state == State.CANCELING)
						return Signal.CANCELED;
					
					Element fault = (Element)detail.getChild(0);
					String faultName = fault.getName(), faultNamespace =
						fault.getNamespace();
					catchInstance = FaultHandlers.getHandler(faultNamespace,
						faultName, processInstance, invoke.catches,
						invoke.catchAll);
					
					if(catchInstance == null)
						return new FaultedSignal(e);
				}
				
				return catchInstance.execute();
			}
			catch(IOException e)
			{
				if(state == State.CANCELING)
					return Signal.CANCELED;
				// Ignore the exception if it was caused by cancelling the
				// activity

				BPELServer.log.severe(toString() + ": exception " + e
					+ " thrown while receiving value");
				e.printStackTrace();

				return new FaultedSignal(BPELServer.sliverNamespace,
					"ioException", e);
			}
			catch(MustUnderstandException e)
			{
				BPELServer.log.severe(toString() + ": exception " + e
					+ " thrown while receiving value");
				e.printStackTrace();

				return new FaultedSignal(SoapEnvelope.ENV, "mustUnderstand", e);
			}
			catch(XmlPullParserException e)
			{
				BPELServer.log.severe(toString() + ": exception " + e
					+ " thrown while receiving value");
				e.printStackTrace();

				return new FaultedSignal(BPELServer.sliverNamespace,
					"xmlPullParserException", e);
			}
			// Handle all the possible exceptions by throwing faults.
			// IOExceptions might be caused by cancelling the activity, so check
			// for that too.
			finally
			{
				try
				{
					if(!channel.isClosed())
					{
						channel.close();
						BPELServer.log.info(toString() + ": channel to " +
							binding + " closed");
					}
					// Close the channel at the end
				}
				catch(IOException e)
				{
					BPELServer.log.severe(toString() + ": IOException " + e +
						" thrown while closing channel to " + binding);
					e.printStackTrace();
					
					if(state == State.CANCELING)
						return Signal.CANCELED;
					return new FaultedSignal(BPELServer.sliverNamespace,
						"ioException", e);
				}
			}
		}
		
		return Signal.COMPLETED;
	}
	
	protected synchronized void cancelImpl()
	{
		activityThread.interrupt();
		// Interrupt the activity
		
		if(catchInstance != null)
			catchInstance.cancel();
		// Cancel any event handlers
		
		try
		{
			if(channel != null)
				channel.close();
		}
		catch(IOException e)
		{
			//
		}
		// Close the channel to stop blocking I/O operations
	}
}