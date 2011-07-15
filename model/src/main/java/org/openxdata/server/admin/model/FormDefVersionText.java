package org.openxdata.server.admin.model;

/**
 * This class represents locale text of a form version.
 */
public class FormDefVersionText extends AbstractEditable{

	private static final long serialVersionUID = -8844252387415837817L;
	
	/** The key of the locale. */
	private String localeKey;
	
	/** The locale text for the xform as per the localeKey field. */
	private String xformText;
	
	/** The locale text for the layout xml as per the localeKey field. */
	private String layoutText;
	
	public FormDefVersionText() {
	}
	
	public FormDefVersionText(String locale, String xformText, String layoutText){
		this.localeKey = locale;
		this.xformText = xformText;
		this.layoutText = layoutText;
	}
		
	public String getLocaleKey() {
		return localeKey;
	}
	
	public void setLocaleKey(String localeKey) {
		this.localeKey = localeKey;
	}
	
	public String getXformText() {
		return xformText;
	}
	
	public void setXformText(String xformText) {
		this.xformText = xformText;
	}
	
	public String getLayoutText() {
		return layoutText;
	}
	
	public void setLayoutText(String layoutText) {
		this.layoutText = layoutText;
	}
}