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

import java.util.*;

/**
 * Parses XSD date and dateTime strings.
 * 
 * @author Greg Hackmann
 */
public abstract class DateParser
{
	/**
	 * Parses XSD date strings.
	 * 
	 * @param in the date string to parse
	 * @return the decoded date
	 * @throws IllegalArgumentException the date could not be parsed
	 */
	public static Calendar parseDate(String in) throws
		IllegalArgumentException
	{
		// Date formats:
		//
		// 0123456789012345
		// yyyy-MM-dd        (local time zone)
		// yyyy-MM-ddZ       (UTC)
		// yyyy-MM-dd+tt:tt  (other time zone)
		
		if(in.charAt(4) != '-' || in.charAt(7) != '-')
			throw new IllegalArgumentException("Invalid date specification");
		// Make sure the hyphens are in the right place
		
		int year, month, day;
		try
		{
			String yearString = in.substring(0, 4);
			String monthString = in.substring(5, 7);
			String dayString = in.substring(8, 10);

			year = Integer.parseInt(yearString);
			month = Integer.parseInt(monthString);
			day = Integer.parseInt(dayString);
		}
		catch(Throwable e)
		{
			throw new IllegalArgumentException("Invalid date specification");
		}
		// Split off and parse the year, month, and day 
		
		TimeZone timeZone = getTimeZone(in, 10);
		Calendar calendar = Calendar.getInstance(timeZone);
		// Set the time zone to whatever's past the 10th character

		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		// Set the calendar's date
		
		// Note: we have to subtract 1 from month because Java counts months
		// (and months only!) starting at 0.

		return calendar;
	}

	/**
	 * Parses XSD dateTime strings.
	 * 
	 * @param in the dateTime string to parse
	 * @return the decoded date/time
	 * @throws IllegalArgumentException the date could not be parsed
	 */
	public static Calendar parseDateTime(String in) throws
		IllegalArgumentException
	{
		// Date/time formats:
		//
		// 01234567890123456789
		// yyyy-MM-dd
		// yyyy-MM-ddThh:mm:ss
		// yyyy-MM-ddThh:mm:ssZ
		// yyyy-MM-ddThh:mm:ss+tt:tt  (other time zone)
		
		// TODO: support fractional seconds

		String datePart = in.substring(0, 10);
		if(in.indexOf('T') != 10)
			return parseDate(datePart);
		// If there's no 'T', then just parse it as a date
		
		if(in.charAt(13) != ':' || in.charAt(16) != ':')
			throw new IllegalArgumentException("Invalid time specification");
		// Make sure the colons are in the expected places
		
		int hour, minutes, seconds;
		try
		{
			String hourString = in.substring(11, 13);
			String minutesString = in.substring(14, 16);
			String secondsString = in.substring(17, 19);

			hour = Integer.parseInt(hourString);
			minutes = Integer.parseInt(minutesString);
			seconds = Integer.parseInt(secondsString);
		}
		catch(Throwable e)
		{
			throw new IllegalArgumentException("Invalid time specification");
		}
		// Split off and parse the hour, minutes, and seconds
		
		TimeZone timeZone = getTimeZone(in, 19);
		Calendar calendar = parseDate(datePart);
		calendar.setTimeZone(timeZone);
		// Set the time zone to whatever's past the 19th character, and
		// start by parsing the date part of the string
		
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minutes);
		calendar.set(Calendar.SECOND, seconds);
		// Then set the time

		return calendar;
	}

	/**
	 * Parses XSD timezone specifications.
	 * 
	 * @param in the dateTime string to parse
	 * @param divider the position in the string immediately before the
	 * timezone
	 * @return the decoded timezone
	 * @throws IllegalArgumentException the timezone could not be parsed
	 */
	private static TimeZone getTimeZone(String in, int divider)
		throws IllegalArgumentException
	{
		if(in.length() == divider)
			return TimeZone.getDefault();
		// If there's nothing left over, use the local timezone
		
		String suffix = in.substring(divider);
		if(suffix.length() == 1 && suffix.charAt(0) == 'Z')
			return TimeZone.getTimeZone("UTC");
		// 'Z' = UTC
		if(suffix.length() == 6)
			throw new IllegalArgumentException("Non-UTC time zones not currently supported");
		// +xx:xx = UTC+xx:xx

		throw new IllegalArgumentException("Invalid time zone specification");
	}
}
