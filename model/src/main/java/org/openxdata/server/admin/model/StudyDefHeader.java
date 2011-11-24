package org.openxdata.server.admin.model;

import net.sf.gilead.pojo.gwt.LightEntity;

/**
 * Lightweight StudyDef object used when just the name
 * needs to be displayed to the user - instead of retrieving
 * the whole StudyDef object (e.g. in lists)
 */
public class StudyDefHeader extends LightEntity {
	private static final long serialVersionUID = 2596766993739570623L;

	private int id = 0;
	
	/** descriptive name of the form */
	private String name;
	
	public StudyDefHeader() {
		// default constructor for GWT serialization
	}

	public StudyDefHeader(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}