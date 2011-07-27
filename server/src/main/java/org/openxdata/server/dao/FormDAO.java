package org.openxdata.server.dao;

import java.util.List;
import java.util.Map;

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
	 * Returns a list of forms that belong to the specified study
	 * @param studyId
	 * @return
	 */
	List<FormDef> getForms(Integer studyId);
	
	/**
	 * Gets the form names for the forms in a particular study.
	 * @param studyId Integer identifier of the parent study
	 * @return Map of form names with key of form id
	 */
	Map<Integer, String> getFormNames(Integer studyId);
	
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
