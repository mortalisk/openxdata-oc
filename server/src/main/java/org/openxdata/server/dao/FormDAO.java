package org.openxdata.server.dao;

import java.util.List;

import org.openxdata.server.admin.model.FormDef;

/**
 *
 */
public interface FormDAO extends BaseDAO<FormDef> {
	
	/**
	 * Gets a list of form definitions from the database.
	 * 
	 * @return the form list.
	 */
	List<FormDef> getForms();
	
	/**
	 * Saves a form definition to the database.
	 * 
	 * @param formDef the form definition.
	 */
	void saveForm(FormDef formDef);
	
	/**
	 * Deletes a form definition from the database.
	 * 
	 * @param formDef the form definition to delete.
	 */
	void deleteForm(FormDef formDef);
}
