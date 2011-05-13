/*
 *  Licensed to the OpenXdata Foundation (OXDF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The OXDF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, 
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 *  Copyright 2010 http://www.openxdata.org.
 */
package org.openxdata.server.service;

import java.util.List;


/**
 * This service is used for 
 * managing <code>Utilities</code> and any other unclassified operations.
 * 
 * @author Angel
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
