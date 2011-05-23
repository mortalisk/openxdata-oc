package org.openxdata.server.service;

import java.util.List;


/**
 * This service is used for 
 * managing <code>Utilities</code> and any other unclassified operations.
 * 
 *
 */

public interface UtilityService {
		
	/**
	 * Installs a mobile application to a number of phones as identified by their phone numbers.
	 * 
	 * @param phonenos the phone number list. The phone numbers should be in international format.
	 * @param url the OTA installation url.
	 * @param modemComPort the com port at which the modem is attached.
	 * @param modemBaudRate the modem baud rate.
	 * @param promptText the prompt text which should appear on the phone screen at the beginning of the installation.
	 * @return true if installation was successful for all the phones, else false.
	 */
	Boolean installMobileApp(List<String> phonenos, String url, String modemComPort, int modemBaudRate, 
			String promptText);

}
