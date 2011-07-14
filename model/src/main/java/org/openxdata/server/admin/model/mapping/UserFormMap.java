package org.openxdata.server.admin.model.mapping;

import org.openxdata.server.admin.model.AbstractEditable;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.User;

/**
 * Maps <code>Forms</code> to <code>User</code>.
 */
public class UserFormMap extends AbstractEditable {

	private int userId;
	private int formId;
	
	/**
	 * Generated serialization ID
	 */
	private static final long serialVersionUID = 4366549281586602840L;
	
	public UserFormMap(){}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * @param formId the formId to set
	 */
	public void setFormId(int formId) {
		this.formId = formId;
	}

	/**
	 * @return the formId
	 */
	public int getFormId() {
		return formId;
	}
	
	/**
	 * Sets the specified <code>User</code> to the Map.
	 * @param user <code>User</code> to remove.
	 */
	public void setUser(User user){
		setUserId(user.getUserId());
	}
	
	/**
	 * Sets the specified <code>FormDef</code> to the Map.
	 * @param form <code>FormDef</code> to remove.
	 */
	public void setForm(FormDef form){
		setFormId(form.getFormId());
	}
}
