package org.openxdata.server.dao;

import java.util.List;
import java.util.Map;

import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefHeader;
import org.openxdata.server.admin.model.FormDefVersion;
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
	 * @param loadConfig PagingLoadConfig
	 * @return the form list.
	 */
	PagingLoadResult<FormDef> getForms(PagingLoadConfig loadConfig);
	
	/**
	 * Gets a list of form definitions from the database.
	 * @param User user
	 * @param loadConfig PagingLoadConfig
	 * @return the form list.
	 */
	PagingLoadResult<FormDef> getForms(User user, PagingLoadConfig loadConfig);
	
	/**
	 * Retrieves the Form with the specified identifier
	 * @param id
	 * @return
	 */
	FormDef getForm(Integer id);
	
	/**
	 * Retrieves the Form Version with the specified identifier
	 * @param formVersionId
	 * @return
	 */
	FormDefVersion getFormVersion(Integer formVersionId);
	
	/**
	 * Retrieves the Form by the specified name
	 * @param name
	 * @return
	 */
	FormDef getForm(String  name);
	
	/**
	 * Retrieves the Forms for the specified Study for which the user has permission
	 * @param user User
	 * @param studyDefId Integer identifier
	 * @return List of Forms
	 */
	List<FormDef> getStudyForms(User user, Integer studyDefId);
	
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
	 * Get a page of Forms mapped to a specific user 
	 * @param userId
	 * @param loadConfig
	 * @return
	 */
	PagingLoadResult<FormDef> getMappedForms(Integer userId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException;

	/**
	 * Get a page of Forms mapped to a specific user 
	 * @param userId
	 * @param loadConfig
	 * @return
	 */
	PagingLoadResult<FormDefHeader> getMappedFormNames(Integer userId, PagingLoadConfig loadConfig);

	/**
	 * Get a page of Forms NOT mapped to the specified user
	 * @param userId
	 * @param loadConfig
	 * @return
	 */
	PagingLoadResult<FormDefHeader> getUnmappedFormNames(Integer userId, PagingLoadConfig loadConfig);
}
