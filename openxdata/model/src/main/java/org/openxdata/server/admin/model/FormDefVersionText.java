package org.openxdata.server.admin.model;

/**
 * This class represents locale text of a form version.
 */
public class FormDefVersionText extends AbstractEditable{

	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = -8844252387415837817L;

	/** The database identifier of the form version text. */
	private int formDefVersionTextId = 0;
	
	/** The database identifier of the form version whose text we represent. */
	private int formDefVersionId;
	
	/** The key of the locale. */
	private String localeKey;
	
	/** The locale text for the xform as per the localeKey field. */
	private String xformText;
	
	/** The locale text for the layout xml as per the localeKey field. */
	private String layoutText;
	
	
	/**
	 * Constructs a new form definition version text object.
	 */
	public FormDefVersionText(){
		
	}
	
	public FormDefVersionText(String locale, int formDefVersionId, String xformText, String layoutText){
		this.localeKey = locale;
		this.formDefVersionId = formDefVersionId;
		this.xformText = xformText;
		this.layoutText = layoutText;
	}
	
	public int getFormDefVersionTextId() {
		return formDefVersionTextId;
	}

	@Override
	public int getId() {
		return formDefVersionTextId;
	}
	
	public void setFormDefVersionTextId(int formDefVersionTextId) {
		this.formDefVersionTextId = formDefVersionTextId;
	}
	
	public int getFormDefVersionId() {
		return formDefVersionId;
	}
	
	public void setFormDefVersionId(int formDefVersionId) {
		this.formDefVersionId = formDefVersionId;
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
	
	@Override
	public boolean isNew(){
		return formDefVersionTextId == 0;
	}
}
