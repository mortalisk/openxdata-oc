package org.openxdata.server.admin.model;

/**
 * This class contains text of a form in a given locale.
 * For each form definition, we have as many of these objects as the locales
 * in which the form is translated.
 */
public class FormDefText extends AbstractEditable{
	
	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = -3343467905549659116L;

	/** The database identifier of the form locale text. */
	private int formTextId = 0;
	
	/** The database identifier of the form whose locale text we represent. */
	private int formId;
	
	/** The key for the locale. */
	private String localeKey;
	
	/** The name of the form in the locale as in the localeKey field. */
	private String name;
	
	/** The description of the form in the locale as in the localeKey field. */
	private String description;
	
	
	/** 
	 * Constructs a new form locale text object.
	 */
	public FormDefText(){
		
	}

	public int getFormTextId() {
		return formTextId;
	}

	@Override
	public int getId() {
		return formTextId;
	}
	
	public void setFormTextId(int formTextId) {
		this.formTextId = formTextId;
	}

	public int getFormId() {
		return formId;
	}

	public void setFormId(int formId) {
		this.formId = formId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocaleKey() {
		return localeKey;
	}

	public void setLocaleKey(String localeKey) {
		this.localeKey = localeKey;
	}
	
	@Override
	public boolean isNew(){
		return formTextId == 0;
	}
}
