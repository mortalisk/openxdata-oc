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
	private int userFormMapId;
	
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
	 * @param userFormId the userFormId to set
	 */
	public void setUserFormMapId(int userFormId) {
		this.userFormMapId = userFormId;
	}

	/**
	 * @return the userFormId
	 */
	public int getUserFormMapId() {
		return userFormMapId;
	}

	@Override
	public int getId() {
		return userFormMapId;
	}
	
	@Override
	public boolean isNew(){
		return this.userFormMapId == 0;
	}
	
	/**
	 * Adds the specified <code>User</code> to the Map.
	 * @param user <code>User</code> to remove.
	 */
	public void addUser(User user){
		setUserId(user.getUserId());
	}
	
	/**
	 * Removes the specified <code>User</code> from the Map.
	 * @param user <code>User</code> to remove.
	 */
	public void removeUser(User user){
		setUserId(user.getUserId());
	}
	
	/**
	 * Adds the specified <code>FormDef</code> to the Map.
	 * @param form <code>FormDef</code> to remove.
	 */
	public void addForm(FormDef form){
		setFormId(form.getFormId());
	}
	
	/**
	 * Removes the specified <code>FormDef</code> from the Map.
	 * @param form <code>FormDef</code> to remove.
	 */
	public void removeForm(FormDef form){
		setFormId(form.getFormId());
	}

}
