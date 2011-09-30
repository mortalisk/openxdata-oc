package org.openxdata.server.admin.client.service;

import java.util.List;
import java.util.Map;

import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.ExportedFormData;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.ExportedDataNotFoundException;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Defines the client side contract for the User Service.
 */
public interface FormService extends RemoteService {
	
	/**
	 * Returns a given Form given the ID.
	 * 
	 * @param formId Id of the Form to retrieve.
	 * @return FormDef
	 */
	FormDef getForm(int formId) throws OpenXDataSecurityException;

    /**
     * Saves the data captured by the user for a particular form.
     * 
     * @param formData FormData
     * @return FormData that was saved (contains id reference)
     */
    FormData saveFormData(FormData formData) throws OpenXDataSecurityException;
    
    /**
     * Retrieves all the form definitions in the system.
     * 
     * @return List of FormDef
     */
    List<FormDef> getForms() throws OpenXDataSecurityException ;
    
    /**
     * Retrieves all the form definitions that are available for the currently logged in user.
     * 
     * @return List of FormDef
     */
    List<FormDef> getFormsForCurrentUser() throws OpenXDataSecurityException;
    
    /**
     * Retrieves all the names of the forms that are available for the currently logged in user
     * and are under the specified study
     * @param studyId identifier of the study
     * @return
     */
    Map<Integer, String> getFormNamesForCurrentUser(Integer studyId) throws OpenXDataSecurityException;
    
    /**
     * Calculates the number of responses captured for a specified formDefVersion.
     * 
     * @param formId int identifier for a form definition version
     * @return Integer (positive number, 0 for no responses)
     */
    Integer getFormResponseCount(int formDefVersionId) throws OpenXDataSecurityException;
    
    /**
     * Retrieves all the FormData for a specified formDefVersion.
     * 
     * @param formId int identifier of the form definition version
     * @return List of FormData
     */
    List<FormData> getFormData(int formDefVersionId) throws OpenXDataSecurityException;
    
    /**
     * Retrieves a page of the form data (directly from exported tables) for a specified form definition.
     * 
     * @param formBinding String xform binding (table name)
     * @param formFields String question binding (column names)
     * @param pagingLoadConfig config to specify paging related config
     * @return PagingLoadResult containing a page of exported form data
     * @throws ExportedDataNotFoundException when the exported table does not exist
     */
    PagingLoadResult<ExportedFormData> getFormDataList(String formBinding, String[] questionBindings,
    		PagingLoadConfig pagingLoadConfig) throws OpenXDataSecurityException, ExportedDataNotFoundException;

    /**
     * Checks if a study, form or form version has data collected for it.
     *
     * @param item the study, form, or form version.
     * @return true if it has, else false.
     */
    Boolean hasEditableData(Editable item) throws OpenXDataSecurityException;
    
	/**
	 * Get a page of Users mapped to a specific form 
	 * @param formId
	 * @param loadConfig
	 * @return
	 */
	PagingLoadResult<User> getMappedUsers(Integer formId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException;
	
	/**
	 * Get a page of Users NOT mapped to the specified form
	 * @param studyId
	 * @param loadConfig
	 * @return
	 */
	PagingLoadResult<User> getUnmappedUsers(Integer formId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException;
	
	/**
	 * Updates the users currently mapped to the specified form.
	 * @param formId Integer id of specified form
	 * @param usersToAdd List of users to add to the study mapping
	 * @param usersToDelete List of users to delete from the study mapping
	 * @throws OpenXDataSecurityException
	 */
	void saveMappedFormUsers(Integer formId, List<User> usersToAdd, List<User> usersToDelete) throws OpenXDataSecurityException;

	/**
	 * Deletes a row of data from the database.
	 * 
	 * @param formData the form data to be deleted.
	 */
	void deleteFormData(FormData formData);
}
