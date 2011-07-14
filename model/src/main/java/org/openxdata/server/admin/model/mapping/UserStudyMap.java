package org.openxdata.server.admin.model.mapping;

import org.openxdata.server.admin.model.AbstractEditable;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;

/**
 * Maps <code>Studies</code> to <code>Users</code>.
 */
public class UserStudyMap extends AbstractEditable {

	private int	userId;
	private int	studyId;
	private static final long serialVersionUID = 2870582564160870766L;

	public UserStudyMap() {
	}
	
	public UserStudyMap(int userId, int studyId) {
		this.userId = userId;
		this.studyId = studyId;
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

	/**
	 * Adds the specified <code>User</code> to the Map.
	 * @param user <code>User</code> to remove.
	 */
	public void setUser(User user) {
		setUserId(user.getId());
	}

	/**
	 * Adds the specified <code>StudyDef</code> to the Map.
	 * @param study <code>StudyDef</code> to remove.
	 */
	public void setStudy(StudyDef study) {
		setStudyId(study.getStudyId());
	}
}
