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

import java.util.*;

import edu.wustl.mobilab.sliver.xpath.*;

/**
 * Represents instances of BPEL activities.
 * 
 * @author Greg Hackmann
 */
public abstract class ActivityInstance implements Cancellable
{
	/**
	 * The {@link Activity} that this object is an instance of. 
	 */
	private final Activity activity;

	/**
	 * The process instance that created this activity instance.
	 */
	protected final ProcessInstance processInstance;
	/**
	 * The {@link Thread} that the activity instance is being executed in.
	 */
	protected Thread activityThread;
	
	/**
	 * A lock around the instance's {@link #state}.
	 */
	private final Object stateLock = new Object();
	/**
	 * The activity's current {@link State}.
	 */
	protected State state = State.INACTIVE;
	
	/**
	 * Creates a new {@link ActivityInstance}.
	 * 
	 * @param activity the {@link Activity} that this object is an instance of
	 * @param processInstance the process instance is creating this activity
	 * instance
	 */
	protected ActivityInstance(Activity activity, ProcessInstance processInstance)
	{
		this.activity = activity;
		this.processInstance = processInstance;
	}
	
	/**
	 * Fires this instance's outgoing inter-activity links.
	 * 
	 * @param completed whether or not the activity completed successfully
	 * @throws ExpressionEvaluationException there was an error evaluating the
	 * link's transitionCondition
	 */
	protected void fireLinks(boolean completed)
		throws ExpressionEvaluationException
	{
		int i = 0;
		try
		{
			for(i = 0; i < activity.sources.length; i++)
				activity.sources[i].fire(processInstance, completed);
			// Try to fire all the links
		}
		catch(ExpressionEvaluationException e)
		{
			for(int j = i + 1; j < activity.sources.length; j++)
				activity.sources[j].fire(processInstance, false);

			throw e;
			// If any one fails, then fire the remaining links as false, and
			// re-throw the exception we caught.
			// (Note that this set of links won't throw an exception while
			// firing, since the transitionCondition is never evaluated
			// when completed == false.)
		}
	}
	
	/**
	 * Waits on incoming links, executes the instance, and fires outgoing links.
	 * Note that most of the work is done by each class's implementation of
	 * {@link #executeImpl()}; this method just does the bookkeeping regarding
	 * the links and the instance's state.
	 * 
	 * @return the {@link Signal} that this instance generated
	 */
	Signal execute()
	{
		// Note that the instance's state is locked before and after it
		// executes, to prevent race conditions when cancellation occurs
		// in another thread.
		
		synchronized(stateLock)
		{
			state = State.ACTIVE;
			activityThread = Thread.currentThread();
			
			Hashtable linkStatus = new Hashtable();
			// For each of the target links
			for(int i = 0; i < activity.targets.length; i++)
			{
				LinkSpecification linkSpec = activity.targets[i].getLinkSpecification();
				ConcreteLink link = processInstance.getConcreteLink(linkSpec);
				// Get the link's instantiation
	
				BPELServer.log.info(toString() + ": waiting for link " +
					linkSpec + " to fire");
				link.waitForStatus();
				BPELServer.log.info(toString() + ": link " + linkSpec +
					" fired");
				linkStatus.put(linkSpec.getName(),
					new Boolean(link.getStatus()));
				// Wait for the link to fire and store its status
			}
			
			boolean shouldExecute;
			
			// If the joinCondition is unspecified
			if(activity.joinCondition == null)
			{
				Enumeration e = linkStatus.elements();
				shouldExecute = activity.targets.length == 0;
				while(e.hasMoreElements())
				{
					Boolean condition = (Boolean)e.nextElement();
					shouldExecute |= condition.booleanValue();
				}
				// Take the boolean OR of all the links
			}
			else
			{
				try
				{
					shouldExecute = activity.joinCondition.evaluateBoolean(
						activity.scopeData, processInstance);				
				}
				catch(ExpressionEvaluationException e)
				{
					BPELServer.log.severe(toString()
						+ ": could not evaluate joinCondition "
						+ activity.joinCondition);
					e.printStackTrace();
					
					return new FaultedSignal(BPELServer.sliverNamespace,
						"expressionEvaluationException", e);
				}
				// Otherwise, evaluate the joinCondition directly
			}
			
			// If the joinCondition failed
			if(!shouldExecute)
			{
				String error = "joinCondition failed at " + this;
				
				Signal signal;
				// If we're not supposed to suppress the failure
				if(!activity.suppressJoinFailure)
				{
					BPELServer.log.severe(toString() + ": " + error +
						"; fault thrown");
	
					state = State.ENDED;
					signal = new FaultedSignal(BPELServer.namespace,
						"joinFailure", new Exception(error));
					// Create a fault for the failure
				}
				else
				{
					BPELServer.log.info(toString() + ": " + error +
						"; fault suppressed");
					
					state = State.ENDED;
					signal = Signal.EXITED;
					// If we're supposed to suppress it, log it as such, then
					// quietly exit the instance
				}
				
				try
				{
					fireLinks(false);
				}
				catch(ExpressionEvaluationException e)
				{
					// This can never happen, since the expression is never
					// actually evaluated when the activity fails
				}
				return signal;
				// Fire the links, then return the appropriate signal
			}
		}
		
		Signal signal = executeImpl();
		// Do the actual execution

		synchronized(stateLock)
		{
			if(signal instanceof FaultedSignal)
				state = State.FAULTED;
			else if(signal == Signal.COMPLETED)
				state = State.COMPLETED;
			else if(signal == Signal.CANCELED || signal == Signal.EXITED)
				state = State.ENDED;
			// Update the state based on the signal that was sent back
	
			try
			{
				fireLinks(state == State.COMPLETED);
			}
			catch(ExpressionEvaluationException e)
			{
				String error = toString()
					+ ": could not evaluate link transition condition: " + e;
				BPELServer.log.severe(error);
				e.printStackTrace();
	
				return new FaultedSignal(BPELServer.sliverNamespace,
					"expressionEvaluationException", e);
			}
			// Fire the links based on the instance's success
		
			return signal;
		}
	}
	
	/**
	 * Executes the activity instance.
	 * 
	 * @return the {@link Signal} that this instance generated
	 */
	protected abstract Signal executeImpl();

	/**
	 * Changes the activity instance's state and cancels it. Again, most of the
	 * work is done by each class's implementation of {@link #cancelImpl()};
	 */
	public void cancel()
	{
		synchronized(stateLock)
		{
			if(state == State.ACTIVE)
			{
				state = State.CANCELING;
				cancelImpl();
			}
			// If we're currently executing, update the state and perform
			// the actual cancellation
		}
	}

	/**
	 * Cancels the activity instance.
	 */
	protected abstract void cancelImpl();
	
	public String toString()
	{
		return activity.toString();
	}
}