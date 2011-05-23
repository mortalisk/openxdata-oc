package org.openxdata.server.admin.client.internationalization;

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
