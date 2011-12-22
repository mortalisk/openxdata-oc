package org.openxdata.server.admin.model;

import net.sf.gilead.pojo.gwt.LightEntity;

/**
 * Lightweight User object used when just the name needs to be displayed to the
 * user - instead of retrieving the whole User object (e.g. in lists)
 */
public class UserHeader extends LightEntity {
	private static final long serialVersionUID = 6980413279121919952L;

	private int id = 0;

	/** descriptive name of the form */
	private String name;

	/** indicates that this user has access to a form via its study */
	private boolean studyAccess = false;

	public UserHeader() {
		// default constructor for GWT serialization
	}

	public UserHeader(int id, String name) {
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

	public boolean isStudyAccess() {
		return studyAccess;
	}

	public void setStudyAccess(boolean studyAccess) {
		this.studyAccess = studyAccess;
	}
}