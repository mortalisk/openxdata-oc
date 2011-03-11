package org.openxdata.server.admin.model.mapping;

import org.openxdata.server.admin.model.AbstractEditable;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;

/**
 * Maps <code>Studies</code> to <code>Users</code>.
 */
public class UserStudyMap extends AbstractEditable {

	/**
	 * Generated serialization version ID
	 */
	private int	userId;
	private int	studyId;
	private int	userStudyMapId;
	private static final long	serialVersionUID	= 2870582564160870766L;

	/**
	 * Creates an instance of this <code>class</code>.
	 */
	public UserStudyMap() {
	}

	public int getUserStudyMapId() {
		return this.userStudyMapId;
	}

	@Override
	public int getId() {
		return this.userStudyMapId;
	}
	
	public void setUserStudyMapId(int userStudyId) {
		this.userStudyMapId = userStudyId;
	}

	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getStudyId() {
		return this.studyId;
	}

	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	@Override
	public boolean isNew() {
		return this.userStudyMapId == 0;
	}

	/**
	 * Adds the specified <code>User</code> to the Map.
	 * @param user <code>User</code> to remove.
	 */
	public void addUser(User user) {
		setUserId(user.getUserId());
	}

	/**
	 * Removes the specified <code>User</code> from the Map.
	 * @param user <code>User</code> to remove.
	 */
	public void removeUser(User user) {
		setUserId(user.getUserId());
	}

	/**
	 * Adds the specified <code>StudyDef</code> to the Map.
	 * @param study <code>StudyDef</code> to remove.
	 */
	public void addStudy(StudyDef study) {
		setStudyId(study.getStudyId());
	}

	/**
	 * Removes the specified <code>StudyDef</code> from the Map.
	 * @param study <code>StudyDef</code> to remove.
	 */
	public void removeStudy(StudyDef study) {
		setStudyId(study.getStudyId());
	}
}
