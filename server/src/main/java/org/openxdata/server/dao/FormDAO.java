package org.openxdata.server.dao;

import java.util.List;
import java.util.Map;

import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;

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
	 * Retrieves the Form with the specified identifier
	 * @param id
	 * @return
	 */
	FormDef getForm(Integer id);
	
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
	
	/**
	 * Get a page of Users mapped to a specific form 
	 * @param formId
	 * @param loadConfig
	 * @return
	 */
	PagingLoadResult<User> getMappedUsers(Integer formId, PagingLoadConfig loadConfig);
	
	/**
	 * Get a page of Users NOT mapped to the specified form
	 * @param studyId
	 * @param loadConfig
	 * @return
	 */
	PagingLoadResult<User> getUnmappedUsers(Integer formId, PagingLoadConfig loadConfig);
	
	/**
	 * Get a page of Forms mapped to a specific user 
	 * @param userId
	 * @param loadConfig
	 * @return
	 */
	PagingLoadResult<FormDef> getMappedForms(Integer userId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException;
	
	/**
	 * Get a page of Forms NOT mapped to the specified user
	 * @param userId
	 * @param loadConfig
	 * @return
	 */
	PagingLoadResult<FormDef> getUnmappedForms(Integer userId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException;
}
