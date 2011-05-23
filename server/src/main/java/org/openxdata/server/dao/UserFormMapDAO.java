package org.openxdata.server.dao;

import java.util.List;

import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.mapping.UserFormMap;

/**
 * @author Angel
 *
 */
public interface UserFormMapDAO extends BaseDAO<UserFormMap> {
	
	/**
	 * Deletes a given <code>UserFormMap</code> from the database.
	 * @param map map to delete.
	 */
	void deleteUserMappedForm(UserFormMap map);

	/**
	 * Fetches a list of <code>UserFormMap</code> definitions from the database.
	 * @return List of <code>UserFormMap</code> definitions.
	 */
	List<UserFormMap> getUserMappedForms();

	/**
	 * Persists a given <code>UserFormMap</code> to the database.
	 * @param map map to persist.
	 */
	void saveUserMappedForm(UserFormMap map);
	
	/**
	 * Gets all the forms that are mapped to the specified user
	 * @param user User
	 * @return List of FormDef
	 */
	List<FormDef> getFormsForUser(User user);
	
	/**
	 * Gets all the forms that are mapped to the specified user in a specified study
	 * 
	 * @param user User
	 * @param studyDefId Integer
	 * @return List of FormDef
	 */
	List<FormDef> getFormsForUser(User user, Integer studyDefId);
}
