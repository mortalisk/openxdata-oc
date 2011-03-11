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
 * Represents the state of {@link ActivityInstance}s.
 * Sliver uses the set of states described in Appendix C of the
 * <a href = "ftp://www6.software.ibm.com/software/developer/library/ws-bpel.pdf">
 * WS-BPEL Specification 1.1</a>, with the addition of an <i>Inactive</i> state
 * (i.e., the state the instance is in between construction and execution).
 * 
 * @author Greg Hackmann
 */
class State
{
	/**
	 * The state's name.
	 */
	private final String name;
	/**
	 * Creates a new State.
	 * 
	 * @param name the state's name
	 */
	private State(String name) { this.name = name; }
	public String toString() { return name; }
	
	/**
	 * The <i>Inactive</i> state.
	 */
	static final State INACTIVE = new State("Inactive");
	/**
	 * The <i>Active</i> state.
	 */
	static final State ACTIVE = new State("Active");
	/**
	 * The <i>Canceling</i> state.
	 */
	static final State CANCELING = new State("Canceling");
	/**
	 * The <i>Completed</i> state.
	 */
	static final State COMPLETED = new State("Completed");
	/**
	 * The <i>Closing</i> state.
	 */
	static final State CLOSING = new State("Closing");
	/**
	 * The <i>Compensating</i> state.
	 */
	static final State COMPENSATING = new State("Compensating");
	/**
	 * The <i>Faulted</i> state.
	 */
	static final State FAULTED = new State("Faulted");
	/**
	 * The <i>Ended</i> state.
	 */
	static final State ENDED = new State("Ended");
}