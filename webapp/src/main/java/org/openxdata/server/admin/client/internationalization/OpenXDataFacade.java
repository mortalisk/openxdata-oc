package org.openxdata.server.admin.client.internationalization;

import com.google.gwt.i18n.client.Dictionary;

/**
 * Abstracts the retrieval of constants to be used in the application.
 *
 * @author Angel
 *
 */
public class OpenXDataFacade {
	
	private OpenXDataFacade(){}	
	
	/**
	 * Inner <tt>class</tt> to guarantee the initialization of <tt>Dictionary variables</tt> using <tt>class initialization rules.</tt>
	 *
	 * @author Angel
	 *
	 */
	private static class DictionaryHolder{
		private static Dictionary INSTANCE = Dictionary.getDictionary("PurcformsText");
	}
	
	/**
	 * Return an instance of the <tt>Dictionary class.</tt>
	 * @return instance of the <tt>Dictionary class.</tt>
	 */
	public static Dictionary getDictionary(){
		return DictionaryHolder.INSTANCE;
	}
}
