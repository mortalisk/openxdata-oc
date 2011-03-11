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
package org.openxdata.server.admin.client.locale;

import com.google.gwt.i18n.client.Dictionary;


/**
 * Used for getting localized text messages.
 * This method of setting localized messages as a javascript object is the html host file
 * has been chosen because it does not require the form designer to be compiled into
 * various languages, which would be required if we had used the other method of
 * localization in GWT. The html host file holding the widget will always have text for
 * one locale. When the user switched to another locale, the page has to be reloaded such
 * that the server replaces this text with that of the new locale.
 * 
 * @author daniel
 *
 */
public class OpenXdataText {
	
	/**
	 * The dictionary having all localized text.
	 */
	private static Dictionary openXdataText = Dictionary.getDictionary("OpenXdataText");
	
	/**
	 * Gets the localized text for a given key.
	 * 
	 * @param key the key
	 * @return the localized text.
	 */
	public static String get(String key){
		return openXdataText.get(key);
	}
}
