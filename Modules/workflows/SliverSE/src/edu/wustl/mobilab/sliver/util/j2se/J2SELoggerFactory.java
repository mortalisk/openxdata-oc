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

package edu.wustl.mobilab.sliver.util.j2se;

import java.util.logging.Level;
import java.util.logging.LogManager;

import edu.wustl.mobilab.sliver.util.*;

/**
 * A {@link LoggerFactory} that uses the {@link java.util.logging.Logger} class
 * built into Java 1.4 and above.
 * 
 * @author Greg Hackmann
 */
public class J2SELoggerFactory implements LoggerFactory
{
	/**
	 * The log manager.
	 */
	private static final LogManager manager = LogManager.getLogManager();
	
	public Logger getLogger(String name)
	{
		if(manager.getLogger(name) == null)
		{
			J2SELogger logger = new J2SELogger(name);
			manager.addLogger(logger);
		}
		// If the logger doesn't exist, make a new one
		
		return (J2SELogger)manager.getLogger(name);
	}

	/**
	 * A {@link java.util.logging.Logger}, with some extra code added to conform
	 * to the {@link Logger} interface.
	 * 
	 * @author Greg Hackmann
	 */
	private static class J2SELogger extends java.util.logging.Logger
		implements Logger
	{
		/**
		 * Creates a new J2SELogger.
		 * 
		 * @param name the logger's name
		 */
		protected J2SELogger(String name)
		{
			super(name, null);
			setLevel(Level.SEVERE);
		}

		public void setLevel(int level)
		{
			if(level == INFO)
				setLevel(Level.INFO);
			else if(level == WARNING)
				setLevel(Level.WARNING);
			else if(level == SEVERE)
				setLevel(Level.SEVERE);
		}
	}
}
