package org.openxdata.server.admin.client.internationalization;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Dictionary;

public class Facade {

	private Facade(){}
	
	private static OpenXdataConstants constants;
	private static Dictionary localeConstants = Dictionary.getDictionary("PurcformsText");
	
	public static OpenXdataConstants getConstants(){
		if(constants == null)
			constants = GWT.create(OpenXdataConstants.class);
			
		return constants;
	}
	
	public static Dictionary getDictionary(){
		if(localeConstants == null)
			localeConstants = Dictionary.getDictionary("PurcformsText");
		
		return localeConstants;
	}
}
