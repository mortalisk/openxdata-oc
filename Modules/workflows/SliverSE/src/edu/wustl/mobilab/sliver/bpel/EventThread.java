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

/**
 * Waits for BPEL &lt;onAlarm&gt; and &lt;onMessage&gt; events to fire.
 * 
 * @author Greg Hackmann
 */
abstract class EventThread extends Thread implements Cancellable
{
	/**
	 * The {@link OnAlarm}s that this thread should monitor.
	 */
	private final OnAlarm [] onAlarms;
	/**
	 * The {@link OnMessage}s that this thread should monitor.
	 */
	private final OnMessage [] onMessages;
	
	/**
	 * The current BPEL process instance.
	 */
	protected final ProcessInstance processInstance;
	/**
	 * Whether or not the corresponding event handler is disabled.
	 */
	protected boolean disabled = false;
	
	/**
	 * The {@link OnMessage} which received a message first.
	 */
	private OnAlarmThread [] onAlarmThreads = new OnAlarmThread[0];
	
	/**
	 * Creates a new {@link EventThread}.
	 * 
	 * @param name the thread's name
	 * @param onAlarms the {@link OnAlarm}s that this thread should monitor
	 * @param onMessages the {@link OnMessage}s that this thread should monitor
	 * @param processInstance the process instance that enabled the event
	 * handler
	 */
	protected EventThread(String name, OnAlarm [] onAlarms,
		OnMessage [] onMessages, ProcessInstance processInstance)
	{
		super(name);
		this.onAlarms = onAlarms;
		this.onMessages = onMessages;
		this.processInstance = processInstance;
	}
	
	/**
	 * Blocks until one event fires.
	 * 
	 * @return the {@link OnAlarm} or {@link OnMessage} that fired, or 
	 * <tt>null</tt> if the thread was interrupted before an activity fired
	 * (this should never happen)
	 */
	protected Activity waitForEvent()
	{
		try
		{
			return processInstance.waitForMessage(onMessages);
			// Wait for one of the <onMessage>s to fire
		}
		catch(InterruptedException e)
		{
			for(int i = 0; i < onAlarmThreads.length; i++)
				if(onAlarmThreads[i].isFired())
					return onAlarms[i];
			// If we were interrupted, check to see if any of the <onAlarm>s
			// did it
		}
		
		return null;
		// If not, then something bad happened; return null
	}
	
	/**
	 * Enables the event handler.
	 * All corresponding partner links are enabled, and timers are started to
	 * monitor alarms.
	 */
	protected synchronized void enable()
	{
		processInstance.addActiveThread(this);

		synchronized(processInstance)
		{
			for(int i = 0; i < onMessages.length; i++)
				processInstance.enablePartnerLink(onMessages[i].partnerLink);
		}
		// Enable all the <onMessage>s' partner links
		
		onAlarmThreads = new OnAlarmThread[onAlarms.length];
		for(int i = 0; i < onAlarms.length; i++)
		{
			onAlarmThreads[i] = new OnAlarmThread(onAlarms[i], this,
				processInstance);
			onAlarmThreads[i].start();
		}
		// Create threads for all the <onAlarm>s
	}

	/**
	 * Disables the event handler.
	 * All corresponding partner links are disabled, and any unfired timers are 
	 * stopped.
 	 */
	synchronized void disable()
	{
		processInstance.removeActiveThread(this);
		
		synchronized(processInstance)
		{	
			for(int i = 0; i < onMessages.length; i++)
				processInstance.disablePartnerLink(onMessages[i].partnerLink);
		}
		// Disable all the <onMessage>s' partner links
		
		disabled = true;
		interrupt();
		// Stop this thread
		
		for(int i = 0; i < onAlarmThreads.length; i++)
			onAlarmThreads[i].interrupt();
		// Stop all the remaining alarms
	}
	
	public String toString()
	{
		return getName();
	}
}


/**
 * Monitors the &lt;onAlarm&gt; and &lt;onMessage&gt; events specified by
 * a {@link EventHandlers}.
 * The corresponding handlers are executed as the event fires; events may
 * fire any number of times until the thread is disabled.
 * 
 * @author Greg Hackmann
 */
class EventHandlersThread extends EventThread
{
	/**
	 * The event handler that is currently being executed.
	 */
	private ActivityInstance childInstance = null;
	
	/**
	 * Creates a new EventHandlersThread.
	 * 
	 * @param parent the {@link EventHandlers} whose events this thread will
	 * monitor
	 * @param processInstance the process instance that created this thread
	 */
	EventHandlersThread(EventHandlers parent, ProcessInstance processInstance)
	{
		super(parent + " EventHandlersThread", parent.onAlarms,
			parent.onMessages, processInstance);
	}
	
	public void run()
	{
		enable();
		
		// Until the thread is stopped
		while(!disabled)
		{
			Activity fired = waitForEvent();
			// Wait for any event to fire
			
			synchronized(this)
			{
				if(disabled)
					return;

				if(fired == null)
				{
					String error = toString()
						+ ": interrupted before a child fired";
					BPELServer.log.severe(error);
					FaultedSignal fault = new FaultedSignal(
						BPELServer.sliverNamespace, "interruptedException",
						new InterruptedException(error));
					processInstance.throwFault(fault);
					
					return;
				}
				// Complain and stop the handler if the event thread
				// was unexpectedly interrupted
				
				BPELServer.log.info(toString() + ": child " + fired
					+ " fired; executing child activity");
				childInstance = fired.newInstance(processInstance);
				// Create an instance of the event's handler
			}
			
			Signal signal = childInstance.execute();
			// Execute the event handler
			if(signal instanceof FaultedSignal)
			{
				FaultedSignal fault = (FaultedSignal)signal;
				processInstance.throwFault(fault);
			}
			// If the event threw a fault, then inform the process
			// (all other signals are ignored, since event handlers
			// have no sink)
		}
	}
	
	public synchronized void cancel()
	{
		disable();

		if(childInstance != null)
			childInstance.cancel();
	}
}

/**
 * Monitors the &lt;onAlarm&gt; and &lt;onMessage&gt; events specified by
 * a {@link Pick} until exactly one fires.
 * 
 * @author Greg Hackmann
 */
class PickThread extends EventThread
{
	/**
	 * The activity that fired.
	 */
	private Activity fired;
	
	/**
	 * Creates a new PickThread.
	 * 
	 * @param parent the {@link Pick} whose events this thread will monitor
	 * @param processInstance the process instance that created this thread
	 */
	PickThread(Pick parent, ProcessInstance processInstance)
	{
		super(parent + " PickThread", parent.onAlarms, parent.onMessages,
			processInstance);
	}
	
	public void run()
	{
		enable();
		fired = waitForEvent();
		// Enable the handler, then wait for exactly one event to fire
	}
	
	/**
	 * Blocks until one event fires.
	 * 
	 * @return the {@link OnAlarm} or {@link OnMessage} that fired
	 * 
	 * @throws InterruptedException the thread was interrupted before an
	 * activity fired (this should never happen)
	 */	
	Activity getFiredEvent() throws InterruptedException
	{
		join();
		if(fired == null)
			throw new InterruptedException();		
		return fired;
		// Block until the thread completes, then return whatever event fired
	}
	
	public synchronized void cancel()
	{
		disable();
	}
}