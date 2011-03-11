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

import org.kxml2.kdom.*;

import edu.wustl.mobilab.sliver.soap.*;
import edu.wustl.mobilab.sliver.util.Queue;

/**
 * Represents a BPEL process instance.
 * 
 * @author Greg Hackmann
 */
public class ProcessInstance extends Thread
{
	/**
	 * The {@link Process} to be executed.
	 */
	private final Process process;

	/**
	 * The values of variables.
	 */
	private Hashtable variables = new Hashtable();
	/**
	 * The status of inter-activity links.
	 */
	private Hashtable concreteLinks = new Hashtable();
	/**
	 * The concrete bindings for each partner link.
	 */
	private Hashtable partnerLinkBindings = new Hashtable();
	/**
	 * The members of each partner group.
	 */
	private Hashtable partnerGroupMembers = new Hashtable();
	/**
	 * Incoming connections that are waiting to be bound to a partner link.
	 */
	private Hashtable newConnections = new Hashtable();
	/**
	 * A list of all enabled partner links.
	 */
	private Vector enabledPartnerLinks = new Vector();
	/**
	 * A list of all threads with executing activities.
	 */
	private Vector activeThreads = new Vector();
	/**
	 * A list of all open {@link Channel}s.
	 */
	private Vector activeConnections = new Vector();
	
	/**
	 * The {@link BPELServer} that is hosting the process.
	 */
	private final BPELServer server;
	
	/**
	 * A {@link Channel} to the client which created the process instance.
	 */
	private final Channel channel;
	
	/**
	 * A map from active {@link OnMessage} activities to their message queues.
	 */
	private final Hashtable activeOnMessages = new Hashtable();
	
	
	/**
	 * Creates a new ProcessInstance.
	 * 
	 * @param process the {@link Process} to be executed
	 * @param startActivity the {@link Activity} that will receive the incoming
	 * connection
	 * @param channel a {@link Channel} to the client which created the process
	 * instance
	 * @param server the {@link BPELServer} that is hosting the process
	 */
	ProcessInstance(Process process, Transaction startActivity,
		Channel channel, BPELServer server)
	{
		this.process = process;
		this.channel = channel;
		this.server = server;
		bindPartnerLink(startActivity.partnerLink, channel);
	}
	
	/**
	 * Gets the value of a variable.
	 * 
	 * @param variableSpec the variable's specification
	 * @return an {@link Element} representing the variable's value, or
	 * <tt>null</tt> if it is unset
	 */
	public Element getVariable(VariableSpecification variableSpec)
	{
		return getVariable(variableSpec.getName());
	}
	/**
	 * Gets the value of a variable.
	 * 
	 * @param name the variable's name
	 * @return an {@link Element} representing the variable's value, or
	 * <tt>null</tt> if it is unset
	 */	
	public Element getVariable(String name)
	{
		return (Element)variables.get(name);
	}

	/**
	 * Sets the value of a variable.
	 * 
	 * @param variableSpec the variable's specification
	 * @param value an {@link Element} representing the variable's new value
	 */
	public void setVariable(VariableSpecification variableSpec, Element value)
	{
		setVariable(variableSpec.getName(), value);
	}
	
	/**
	 * Sets the value of a variable.
	 * 
	 * @param name the variable's name
	 * @param value an {@link Element} representing the variable's new value
	 */
	public void setVariable(String name, Element value)
	{
		variables.put(name, value);
	}
	
	/**
	 * Gets the instantiation of an inter-activity link.
	 * 
	 * @param linkSpec the link's specification
	 * @return the corresponding inter-activity link
	 */
	public ConcreteLink getConcreteLink(LinkSpecification linkSpec)
	{
		return getConcreteLink(linkSpec.getName());
	}

	/**
	 * Gets the instantiation of an inter-activity link.
	 * 
	 * @param name the link's name
	 * @return the corresponding inter-activity link
	 */
	public ConcreteLink getConcreteLink(String name)
	{
		return (ConcreteLink)concreteLinks.get(name);		
	}
	
	/**
	 * Sets the instantiation of an inter-activity link.
	 * 
	 * @param linkSpec the link's specification
	 * @param link the instantiated inter-activity link
	 */
	void setConcreteLink(LinkSpecification linkSpec, ConcreteLink link)
	{
		concreteLinks.put(linkSpec.getName(), link);
	}
	
	/**
	 * Waits for any one of a set of {@link OnMessage}s to receive a message.
	 * 
	 * @param onMessages the {@link OnMessage}s to listen for
	 * @return the {@link OnMessage} which received its message first
	 * 
	 * @throws InterruptedException the thread was interrupted while waiting
	 * for a message to arrive
	 */
	public synchronized OnMessage waitForMessage(OnMessage [] onMessages)
		throws InterruptedException
	{
		for(int i = 0; i < onMessages.length; i++)
			activeOnMessages.put(onMessages[i], new Queue());
		
		while(true)
		{
			// For each onMessage
			for(int i = 0; i < onMessages.length; i++)
			{
				PartnerLink link = onMessages[i].partnerLink;
				// Get the onMessage's partner link
				if(newConnections.containsKey(link.getName()))
				{
					Channel binding = (Channel)newConnections.remove(link.getName());
					bindPartnerLink(link, binding);

					for(int j = 0; j < onMessages.length; j++)
						activeOnMessages.remove(onMessages[j]);
					
					return onMessages[i];
				}
				Queue eventQueue = (Queue)activeOnMessages.get(onMessages[i]);
				if(!eventQueue.isEmpty())
				{					
					eventQueue.pop();

					for(int j = 0; j < onMessages.length; j++)
						activeOnMessages.remove(onMessages[j]);
					
					return onMessages[i];
				}
				// If there's a connection waiting on the link, then bind it,
				// and we're done
			}

			wait();
			// Sleep until a new partner link is bound
		}		
	}

	/**
	 * Handles new incoming messages.
	 * 
	 * @param request the incoming message
	 * @param partnerLinkName the name of the partner link which received the
	 * message
	 */
	synchronized void handleEvent(Element request, String partnerLinkName)
	{
		String operationName = request.getName();
		
		Enumeration enumeration = activeOnMessages.keys();
		// For each active <onMessage>
		while(enumeration.hasMoreElements())
		{
			OnMessage next = (OnMessage)enumeration.nextElement();
			// If the partner link and operation match
			if(next.getPartnerLink().getName().equals(partnerLinkName)
				&& next.getOperation().equals(operationName))
			{
				Queue eventQueue = (Queue)activeOnMessages.get(next);
				eventQueue.push(request);
				// Push the new message on the <onMessage>'s message queue
				
				notifyAll();
				// Wake up any threads that are waiting for new messages
				return;
			}
		}
	}
	
	/**
	 * Accepts a new connection, but does not immediately bind it to a partner
	 * link.
	 * 
	 * @param partnerLinkName the name of the partner link that should
	 * eventually be bound to
	 * @param connection the new connection to accept
	 */
	synchronized void newConnection(String partnerLinkName, Channel connection)
	{
		while(partnerLinkBindings.containsKey(partnerLinkName))
		{
			try
			{
				wait();
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
				// TODO: Can anything bad happen if we just ignore interruptions?
			}
		}
		// Wait for the partner link to become available
		// TODO: if it's not immediately available, should we wait or throw a fault?
		
		newConnections.put(partnerLinkName, connection);
		notifyAll();
	}
	
	/**
	 * Binds a partner link.
	 * 
	 * @param partnerLink the partner link to bind
	 * @param binding the {@link Channel} to bind the link to
	 */
	public synchronized void bindPartnerLink(PartnerLink partnerLink, Channel binding)
	{
		partnerLinkBindings.put(partnerLink.getName(), binding);
	}

	/**
	 * Unbinds a partner link.
	 * 
	 * @param partnerLink the partner link to unbind
	 */
	public synchronized void unbindPartnerLink(PartnerLink partnerLink)
	{
		partnerLinkBindings.remove(partnerLink.getName());
		notifyAll();
	}

	/**
	 * Gets an incoming partner link's binding.  If the partner link is not yet
	 * bound, then this method optionally blocks until it is bound.
	 * 
	 * @param activity the activity instance that is using this partner link
	 * @param partnerLink the partner link to get the binding for
	 * @param blockUntilAvailable whether or not to block until the partner
	 * link is bound
	 * 
	 * @return the partner link's binding, or <tt>null</tt> if the partner
	 * link is unbound and <tt>blockUntilAvailable</tt> is <tt>false</tt>
	 */
	public synchronized Channel getIncomingLinkBinding(ActivityInstance activity,
		PartnerLink partnerLink, boolean blockUntilAvailable)
	{
		// If the partner link is already bound, then just return the binding
		if(partnerLinkBindings.containsKey(partnerLink.getName()))
			return (Channel)partnerLinkBindings.get(partnerLink.getName());
		
		if(!blockUntilAvailable)
			return null;
		
		while(!newConnections.containsKey(partnerLink.getName()))
		{
			try
			{
				wait();
			}
			catch(InterruptedException e)
			{
				if(activity.state == edu.wustl.mobilab.sliver.bpel.State.CANCELING)
					return null;
			}
		}
		// Otherwise, wait until the partner link has a connection

		Channel binding = (Channel)newConnections.remove(partnerLink.getName());
		bindPartnerLink(partnerLink, binding);
		// Bind the new connection to the partner link
		
		return binding;
	}

	/**
	 * Gets an outgoing partner link's binding.
	 * 
	 * @param partnerLink the partner link to get the binding for
	 * @return the partner link's binding
	 */
	public Binding getOutgoingLinkBinding(PartnerLink partnerLink)
	{
		return server.getOutgoingBinding(partnerLink.getName());
	}
	
	/**
	 * Enables a partner link.
	 * 
	 * @param partnerLink the partner link to disable
	 */
	public synchronized void enablePartnerLink(PartnerLink partnerLink)
	{
		enabledPartnerLinks.addElement(partnerLink.getName());
	}
	
	/**
	 * Disables a partner link.
	 * 
	 * @param partnerLink the partner link to disable
	 */
	public synchronized void disablePartnerLink(PartnerLink partnerLink)
	{
		enabledPartnerLinks.removeElement(partnerLink.getName());
	}

	/**
	 * Gets whether or not a partner link is enabled.
	 * 
	 * @param partnerLinkName the name of the partner link to check
	 * @return whether or not the partner link is enabled
	 */	
	public synchronized boolean isPartnerLinkEnabled(String partnerLinkName)
	{
		return enabledPartnerLinks.contains(partnerLinkName) &&
			!newConnections.containsKey(partnerLinkName);
		// Make sure that the partner link is enabled, and there's not already
		// someone waiting to be bound to it
	}

	/**
	 * Gets part of a variable's value, in accordance with the
	 * <tt>bpws:getVariableData()</tt> XPath function.
	 * 
	 * @param scopeData the process's current scope
	 * @param variable the variable to look up
	 * @param partName the part of the variable to read (or <tt>null</tt> to
	 * read the entire variable) 
	 * @param locationPath the XPath location to read (or <tt>null</tt> for
	 * no location)
	 * @return an {@link Element} representation of the corresponding variable
	 * part
	 * 
	 * @throws FaultedSignal an error occured while trying to read the
	 * variable part
	 */
	public Element getVariablePart(ScopeData scopeData,
		VariableSpecification variable, String partName, String locationPath)
		throws FaultedSignal
	{
		Element source = getVariable(variable);
		// Get the variable
		if(source == null)
		{
			String error = "Variable " + variable.getName() + " not found";
			BPELServer.log.severe(error);
			throw new FaultedSignal(BPELServer.namespace,
				"uninitializedVariable", new IllegalArgumentException(error));
			// If the variable has not value, throw an uninitializedVariable
			// fault
		}
	
		// If the part name is specified
		if(partName != null)
		{
			source = source.getElement(null, partName);
			// Get the corresponding part
			if(source == null)
			{
				String error = "Variable part " + variable.getName() + "/"
					+ partName + " not found";
				BPELServer.log.severe(error);
				throw new FaultedSignal(BPELServer.namespace,
					"uninitializedVariable",
					new IllegalArgumentException(error));
			}
			// If the part is empty, then throw an uninitializedVariable
			// fault
		}
		
		// TODO: don't ignore locationPath
	
		return new Element(source);
		// Return a copy of the data
	}
	
	/**
	 * Adds a new {@link Cancellable} activity instance to the list of active
	 * threads.
	 * 
	 * @param instance the {@link Cancellable} to add to the list
	 */
	synchronized void addActiveThread(Cancellable instance)
	{
		activeThreads.addElement(instance);
	}
	
	/**
	 * Removes a now-inactive {@link Cancellable} activity instance from the
	 * list of active threads.
	 * 
	 * @param instance the {@link Cancellable} to remove from the list
	 */
	synchronized void removeActiveThread(Cancellable instance)
	{
		activeThreads.removeElement(instance);
	}
	
	/**
	 * Adds a {@link Channel} to the list of active connections.
	 * 
	 * @param connection the {@link Channel} to add to the list
	 */
	public synchronized void addActiveConnection(Channel connection)
	{
		activeConnections.addElement(connection);
	}
	
	/**
	 * Removes a {@link Channel} from the list of active connections.
	 * 
	 * @param connection the {@link Channel} to remove from the list
	 */
	public synchronized void removeActiveConnection(Channel connection)
	{
		activeConnections.removeElement(connection);
	}

	/**
	 * Cancels the process instance, including all executing activities.
	 */
	synchronized void cancel()
	{
		BPELServer.log.warning("Cancelling all active threads");
		
		Enumeration e = activeThreads.elements();
		while(e.hasMoreElements())
		{
			Cancellable next = (Cancellable)e.nextElement();
			next.cancel();
		}
		// Iterate through the active threads, and cancel them all
	}
	
	/**
	 * Throws a process fault.
	 * This fault is sent back to client, and the process is immediately
	 * cancelled.
	 * 
	 * @param fault the fault to throw
	 */
	public synchronized void throwFault(FaultedSignal fault)
	{
		Enumeration enumeration = activeConnections.elements();
		// For each active connection
		while(enumeration.hasMoreElements())
		{
			try
			{
				Channel next = (Channel)enumeration.nextElement();

				synchronized(next)
				{
					BPELServer.log.severe("Sending uncaught "
						+ fault.getFault() + " to " + next);
					next.sendObject(fault.getFault());
					next.close();
				}
			}
			catch(IOException e)
			{
				BPELServer.log.severe("Exception thrown while sending fault: "
					+ e.getMessage());
				e.printStackTrace();
			}
			// Send the fault
		}

		activeConnections.removeAllElements();
		cancel();
	}
	
	/**
	 * Lists the members in the specified partner group.
	 * 
	 * @param partnerGroup the partner group to list
	 * @return a {@link Vector} of {@link Channel}s representing the partner
	 * group's membership
	 */
	public synchronized Vector getPartnerGroupMembership(
		PartnerGroup partnerGroup)
	{
		String name = partnerGroup.getName();
		if(!partnerGroupMembers.containsKey(name))
			partnerGroupMembers.put(name, new Vector());
		// Lazily create the group if necessary
		
		return (Vector)partnerGroupMembers.get(name);
	}

	/**
	 * Adds a partner link's current binding to a partner group.
	 * 
	 * @param partnerGroup the partner group to add to
	 * @param partnerLink the partner link whose binding should be added
	 * 
	 * @return <tt>true</tt> if the binding was added, or <tt>false</tt>
	 * if the binding was already a group member
	 */
	public synchronized boolean addPartnerGroupMember(
		PartnerGroup partnerGroup, PartnerLink partnerLink)
	{
		Channel member = (Channel)partnerLinkBindings.get(partnerLink.getName());
		Vector members = getPartnerGroupMembership(partnerGroup);
		if(members.contains(member))
			return false;
		
		members.addElement(member);
		return true;
	}
	
	/**
	 * Removes a partner link's current binding from a partner group.
	 * 
	 * @param partnerGroup the partner group to remove from
	 * @param partnerLink the partner link whose binding should be removed
	 * 
	 * @return <tt>true</tt> if the binding was removed, or <tt>false</tt>
	 * if the binding was not a group member
	 */
	public synchronized boolean removePartnerGroupMember(
		PartnerGroup partnerGroup, PartnerLink partnerLink)
	{
		Channel member = (Channel)partnerLinkBindings.get(partnerLink.getName());
		Vector members = getPartnerGroupMembership(partnerGroup);
		if(!members.contains(member))
			return false;
		
		members.removeElement(member);
		return true;
	}

	public void run()
	{
		process.activeInstances.addElement(this);
		// Add this instance to the process's active list

		ActivityInstance activity = process.activity.newInstance(this);
		// Create a new instance of the "root" activity

		BPELServer.log.info("Executing start activity " + activity);
		addActiveConnection(channel);
		addActiveThread(activity);
		// List the incoming connection and new activity thread as active
		EventThread eventThread = process.eventHandlers.enable(this);
		// Enable the global event handler
		Signal signal = null;
		try
		{
			signal = activity.execute();				
			// Try to execute the process
		}
//		catch(RuntimeException e)
//		{
//			throw e;
//			// Re-throw any really nasty exceptions
//		}
		catch(RuntimeException e)
		{
			String error = "Process did not complete normally; threw "
				+ e;
			BPELServer.log.severe(error);
			e.printStackTrace();
			FaultedSignal fault = new FaultedSignal(BPELServer.sliverNamespace,
				"processDidNotComplete", e);
			BPELServer.sendFault(fault, channel);
			
			throw e;
		}
		finally
		{
			// After the activity executes (or something goes terribly wrong)
			
			removeActiveThread(activity);
			// The activity is no longer active
			process.activeInstances.removeElement(this);
			// This process as a whole is no longer active
			eventThread.disable();
			// The event handler is disabled
			
			// TODO: should we disable, or cancel? (cancelling cancels the
			// child activity as well)
		}
		
		// If the activity threw a fault
		if(signal instanceof FaultedSignal)
		{
			cancel();
			// Stop the rest of the activities executing as part of this
			// process instance
			
			ActivityInstance catchInstance = process.faultHandlers.getHandler(
				(FaultedSignal)signal, this);
			if(catchInstance != null)
			{
				addActiveThread(catchInstance);
				signal = catchInstance.execute();
				removeActiveThread(catchInstance);
			}
			// If the fault has a handler, execute it
		}

		if(signal != Signal.COMPLETED)
		{
			String error = activity.toString()
				+ " failed to execute; sent signal " + signal;
			BPELServer.log.severe(error);
		}
		// Complain if the activity didn't complete
		
		if(signal == Signal.EXITED || signal == Signal.CANCELED)
		{
			String error = "Process did not complete normally; generated signal "
				+ signal;
			FaultedSignal fault = new FaultedSignal(BPELServer.sliverNamespace,
				"processDidNotComplete", new RuntimeException(error));
			BPELServer.sendFault(fault, channel);
		}
		// Complain if the process was cancelled or exited before completion
		else if(signal instanceof FaultedSignal)
		{
			FaultedSignal fault = (FaultedSignal)signal;
			BPELServer.log.severe("Sending uncaught " + fault.getFault());
			BPELServer.sendFault(fault, channel);
		}
		// Send back any BPEL faults that occur while executing the process
		
		FaultedSignal missingReply = new FaultedSignal(BPELServer.namespace,
			"missingReply", new Exception("Process terminated without <reply>"));
		throwFault(missingReply);
		// Throw a fault to close the remaining connections
	}
}
