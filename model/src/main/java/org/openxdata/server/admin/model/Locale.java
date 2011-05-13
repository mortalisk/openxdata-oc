package org.openxdata.server.admin.model;

/**
 * This class represents a locale or language.
 */
public class Locale extends AbstractEditable{
	
	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = 2100312849246884803L;

	/** The data base identifier for the locale. */
	private int localeId = 0;
	
	/** The locale key. eg en */
	private String key;
	
	/** The locale name. eg English. */
	private String name;
	
	
	/** Constructs a new locale object. */
	public Locale(){
		
	}
	
	public int getLocaleId() {
		return localeId;
	}
	
	@Override
	public int getId() {
		return localeId;
	}
	
	public void setLocaleId(int localeId) {
		this.localeId = localeId;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean isNew(){
		return localeId == 0;
	}
}
