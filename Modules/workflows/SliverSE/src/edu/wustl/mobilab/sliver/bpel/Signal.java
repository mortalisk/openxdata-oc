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
 * Represents the signals sent among {@link ActivityInstance}s. Sliver uses the
 * set of signals described in Appendix C of the <a href =
 * "ftp://www6.software.ibm.com/software/developer/library/ws-bpel.pdf"> WS-BPEL
 * Specification 1.1</a>.
 * <p>
 * Sliver only explicitly represents the signals sent from child to parent
 * (i.e., the dashed lines shown in Figure 2 of the WS-BPEL specification). All
 * other signals are represented implicitly as function calls.
 * </p>
 * 
 * @author Greg Hackmann
 * @see FaultedSignal
 */
public interface Signal
{
	/**
	 * A trivial implementation of a {@link Signal}.
	 * This class is used internally and should not need to be refered to.
	 * 
	 * @author Greg Hackmann
	 */
	static class SignalImpl implements Signal
	{
		/**
		 * The signal's name.
		 */
		private final String name;
		/**
		 * Creates a new SignalImpl.
		 * 
		 * @param name the signal's name
		 */
		protected SignalImpl(String name) { this.name = name; }
		public String toString() { return name; }
	}

	/**
	 * The <i>Exited</i> signal.
	 */
	public static final Signal EXITED = new SignalImpl("Exited");
	/**
	 * The <i>Completed</i> signal.
	 */
	public static final Signal COMPLETED = new SignalImpl("Completed");
	/**
	 * The <i>Canceled</i> signal.
	 */
	public static final Signal CANCELED = new SignalImpl("Canceled");
	/**
	 * The <i>Closed</i> signal.
	 */
	public static final Signal CLOSED = new SignalImpl("Closed");
	/**
	 * The <i>Compensated</i> signal.
	 */
	public static final Signal COMPENSATED = new SignalImpl("Compensated");	
}