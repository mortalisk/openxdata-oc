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
 * Allows Sliver to log messages.
 * 
 * @author Greg Hackmann
 */
public class Logging
{
	/**
	 * The {@link LoggerFactory} implementation being used to generate
	 * {@link Logger}s.
	 */
	private static LoggerFactory loggerFactory;
	static
	{
		try
		{
			Class.forName("java.util.logging.Logger");
			loggerFactory = (LoggerFactory)Class.forName(
				"edu.wustl.mobilab.sliver.util.j2se.J2SELoggerFactory").
				newInstance();
			// Try to use the J2SE logger
		}
		catch(RuntimeException e) { throw e; }
		catch(Exception e)
		{
			loggerFactory = new NullLoggerFactory();
			// If the J2SE logger doesn't exist here, then just send messages to
			// oblivion
		}	
	}

	/**
	 * Overrides the default {@link LoggerFactory} selection.  Generally, this
	 * will not need to be called, since the appropriate {@link LoggerFactory}
	 * should be chosen automatically.
	 * 
	 * @param loggerFactory the new {@link LoggerFactory}
	 */
	public static void setLoggerFactory(LoggerFactory loggerFactory)
	{
		Logging.loggerFactory = loggerFactory;
	}
	
	/**
	 * Gets the {@link Logger} with the provided name.  If one does not exist
	 * already, then a new one is created.
	 * 
	 * @param name the logger's name
	 * @return the corresponding logger
	 */
	public static Logger getLogger(String name)
	{
		Logger logger = loggerFactory.getLogger(name);
		return logger;
	}	
}
