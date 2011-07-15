package org.openxdata.server.admin.model;

/**
 * This class represents a locale or language.
 */
public class Locale extends AbstractEditable{
	
	private static final long serialVersionUID = 2100312849246884803L;
	
	/** The locale key. eg en */
	private String key;
	
	/** The locale name. eg English. */
	private String name;
	
	public Locale(){
		
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
}
