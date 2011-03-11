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

package edu.wustl.mobilab.sliver.util;

/**
 * Logs messages and errors.
 * 
 * @author Greg Hackmann
 */
public interface Logger
{
	/**
	 * A flag for informational messages.
	 */
	public final static int INFO = 0;
	/**
	 * A flag for warning messages.
	 */
	public final static int WARNING = 1;
	/**
	 * A flag for error messages.
	 */
	public final static int SEVERE = 2;
	
	/**
	 * Logs an informational message.
	 * 
	 * @param message the message to log
	 */
	public void info(String message);
	/**
	 * Logs a warning message.
	 * 
	 * @param message the message to log
	 */
	public void warning(String message);
	/**
	 * Logs an error message.
	 * 
	 * @param message the message to log
	 */
	public void severe(String message);
	
	/**
	 * Sets the minimum "level" of messages that are actually logged.
	 * Any messages below this level are ignored; e.g.,
	 * <code>setLevel(WARNING)</code> will cause informational
	 * messages to be ignored.
	 * 
	 * @param level the minimum logging level (one of {@link #INFO},
	 * {@link #WARNING}, or {@link #SEVERE})
	 */
	public void setLevel(int level);
}
