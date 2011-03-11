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
 * Parses and encapsulates XSD duration strings.
 * 
 * @author Greg Hackmann
 */
public class Duration
{
	/**
	 * The number of years in the duration.
	 */
	private int years;
	/**
	 * The number of months in the duration.
	 */
	private int months;
	/**
	 * The number of days in the duration.
	 */
	private int days;
	/**
	 * The number of hours in the duration.
	 */
	private int hours;
	/**
	 * The number of minutes in the duration.
	 */
	private int minutes;
	/**
	 * The number of seconds in the duration.
	 */
	private int seconds;
	
	/**
	 * Creates a new Duration.
	 * 
	 * @param encoded the XSD duration string to parse
	 * @throws IllegalArgumentException the duration could not be parsed
	 */
	public Duration(String encoded) throws IllegalArgumentException
	{
		// Duration format:
		// PnYnMnDTnHnMnS
		
		if(encoded.charAt(0) != 'P')
			throw new IllegalArgumentException("Invalid duration specification");
		// Durations must start with "P"
		
		String remaining;
		try
		{
			remaining = encoded.substring(1);
			remaining = parseYears(remaining);
			remaining = parseMonths(remaining);
			remaining = parseDays(remaining);
		}
		catch(Throwable e)
		{
			throw new IllegalArgumentException("Invalid duration specification");
		}
		// Try to parse the years, months, and days
		
		if(remaining.length() == 0)
			return;
		// Check to see if there's anything left
		
		if(remaining.charAt(0) != 'T')
			throw new IllegalArgumentException("Invalid time specification");
		// If so, it must be a time specification beginning with "T"
		
		try
		{
			remaining = remaining.substring(1);
			remaining = parseHours(remaining);
			remaining = parseMinutes(remaining);
			remaining = parseSeconds(remaining);
		}
		catch (Throwable e)
		{
			throw new IllegalArgumentException("Invalid time specification");
		}
		// Try to parse the hours, minutes, and seconds
		
		if(remaining.length() > 0)
			throw new IllegalArgumentException("Invalid time specification");
		// There should be nothing left
	}
	
	/**
	 * Parses the years part of the duration string.
	 * 
	 * @param encoded the unparsed part of the string
	 * @return the remainder of the string after the years part
	 */
	private String parseYears(String encoded)
	{
		int end = encoded.indexOf('Y');
		if(end == -1)
			return encoded;
		// If there's no "Y", then there's no years, so just skip it
		
		years = Integer.parseInt(encoded.substring(0, end));
		// Parse up until the "Y" divider
		return encoded.substring(end + 1);
		// Then return everything after the "Y"
	}
	
	/**
	 * Gets the number of years in the duration.
	 * 
	 * @return the number of years in the duration
	 */
	public int getYears()
	{
		return years;
	}

	/**
	 * Parses the months part of the duration string.
	 * 
	 * @param encoded the unparsed part of the string
	 * @return the remainder of the string after the months part
	 */
	private String parseMonths(String encoded)
	{
		if(encoded.charAt(0) == 'T')
			return encoded;
		
		int end = encoded.indexOf('M');
		if(end == -1)
			return encoded;
		
		months = Integer.parseInt(encoded.substring(0, end));
		return encoded.substring(end + 1);
	}
	
	/**
	 * Gets the number of months in the duration.
	 * 
	 * @return the number of months in the duration
	 */
	public int getMonths()
	{
		return months;
	}

	/**
	 * Parses the days part of the duration string.
	 * 
	 * @param encoded the unparsed part of the string
	 * @return the remainder of the string after the days part
	 */
	private String parseDays(String encoded)
	{
		int end = encoded.indexOf('D');
		if(end == -1)
			return encoded;
		
		days = Integer.parseInt(encoded.substring(0, end));
		return encoded.substring(end + 1);
	}
	
	/**
	 * Gets the number of days in the duration.
	 * 
	 * @return the number of days in the duration
	 */
	public int getDays()
	{
		return days;
	}
	
	/**
	 * Parses the hours part of the duration string.
	 * 
	 * @param encoded the unparsed part of the string
	 * @return the remainder of the string after the hours part
	 */
	private String parseHours(String encoded)
	{
		int end = encoded.indexOf('H');
		if(end == -1)
			return encoded;
		
		hours = Integer.parseInt(encoded.substring(0, end));
		return encoded.substring(end + 1);
	}
	
	/**
	 * Gets the number of hours in the duration.
	 * 
	 * @return the number of hours in the duration
	 */
	public int getHours()
	{
		return hours;
	}

	/**
	 * Parses the minutes part of the duration string.
	 * 
	 * @param encoded the unparsed part of the string
	 * @return the remainder of the string after the minutes part
	 */
	private String parseMinutes(String encoded)
	{
		int end = encoded.indexOf('M');
		if(end == -1)
			return encoded;
		
		minutes = Integer.parseInt(encoded.substring(0, end));
		return encoded.substring(end + 1);
	}
	
	/**
	 * Gets the number of minutes in the duration.
	 * 
	 * @return the number of minutes in the duration
	 */
	public int getMinutes()
	{
		return minutes;
	}

	/**
	 * Parses the seconds part of the duration string.
	 * 
	 * @param encoded the unparsed part of the string
	 * @return the remainder of the string after the seconds part
	 */
	private String parseSeconds(String encoded)
	{
		int end = encoded.indexOf('S');
		if(end == -1)
			return encoded;
		
		seconds = Integer.parseInt(encoded.substring(0, end));
		return encoded.substring(end + 1);
	}
	
	/**
	 * Gets the number of seconds in the duration.
	 * 
	 * @return the number of seconds in the duration
	 */
	public int getSeconds()
	{
		return seconds;
	}

	public String toString()
	{
		String returnMe = "";
		if(years != 0)
			returnMe = years + " years ";
		if(months != 0)
			returnMe += months + " months ";
		if(days != 0)
			returnMe += days + " days ";
		if(hours != 0)
			returnMe += hours + " hours ";
		if(minutes != 0)
			returnMe += minutes + " minutes ";
		if(seconds != 0)
			returnMe += seconds + " seconds";
		
		return returnMe;
	}
}
