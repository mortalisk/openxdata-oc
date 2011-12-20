package org.openxdata.server.admin.client.service;

import java.util.List;
import java.util.Map;

import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.ExportedFormData;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDataHeader;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefHeader;
import org.openxdata.server.admin.model.FormDefVersion;
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
	 * Returns a FormDefVersion given the ID
	 * @param formDefVersionId
	 * @return
	 * @throws OpenXDataSecurityException
	 */
	FormDefVersion getFormVersion(Integer formDefVersionId) throws OpenXDataSecurityException;
	
	/**
	 * Returns FormData given the ID
	 * @param formDataId
	 * @return
	 * @throws OpenXDataSecurityException
	 */
	FormData getFormData(Integer formDataId) throws OpenXDataSecurityException;

    /**
     * Saves the data captured by the user for a particular form.
     * 
     * @param formData FormData
     * @return FormData that was saved (contains id reference)
     */
    FormData saveFormData(FormData formData) throws OpenXDataSecurityException;
    
    /**
     * Deletes the data captured by the user for a particular form.
     * 
     * @param formData FormData
     */
    void deleteFormData(FormData formData) throws OpenXDataSecurityException;
    
    /**
     * Deletes all the submitted data specified by the ids
     * NOTE: this method assumes the form data has not been exported (hence
     * does not try to delete from exported tables)
     * @param formDataIds
     * @throws OpenXDataSecurityException
     */
    void deleteFormData(List<Integer> formDataIds) throws OpenXDataSecurityException;
    
    /**
     * Reprocesses (runs RDMS Export) for all the specified form data
     * @param formDataIds
     * @throws OpenXDataSecurityException
     */
    void exportFormData(List<Integer> formDataIds) throws OpenXDataSecurityException;
    
    /**
     * Retrieves a page of form definitions that are available for the specified user.
     * @param user User
     * @param loadConfig PagingLoadConfiguration specifying page number and size etc
     * @return PagingLoadResult of Form Definitions
     */
    PagingLoadResult<FormDef> getForms(User user, PagingLoadConfig loadConfig) throws OpenXDataSecurityException;
    
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
	 * Calculates the number of responses which have not been exported for a specified formDefVersion
	 * @param formDefVersionId int identifier for the form definition version
	 * @return Integer (positive number and 0 for no unprocessed data)
	 * @throws OpenXDataSecurityException
	 */
	Integer getUnprocessedDataCount(int formDefVersionId) throws OpenXDataSecurityException;
	
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
	PagingLoadResult<FormDefHeader> getMappedFormNames(Integer userId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException;

	/**
	 * Get a page of Forms NOT mapped to the specified user
	 * @param userId
	 * @param loadConfig
	 * @return
	 */
	PagingLoadResult<FormDefHeader> getUnmappedFormNames(Integer userId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException;
	
	/**
	 * Updates the forms currently mapped to the specified user.
	 * @param userId Integer id of specified user
	 * @param formsToAdd List of forms to add to the user's access
	 * @param formsToDelete List of forms to delete from the user's access
	 * @throws OpenXDataSecurityException
	 */
	void saveMappedUserFormNames(Integer userId, List<FormDefHeader> formsToAdd, List<FormDefHeader> formsToDelete) throws OpenXDataSecurityException;

	PagingLoadResult<FormDefVersion> getFormVersions(User user,
			PagingLoadConfig loadConfig) throws OpenXDataSecurityException;
	
	/**
	 * Retrieves a page of unexported Form Data
	 * @param loadConfig
	 * @return
	 * @throws OpenXDataSecurityException
	 */
	PagingLoadResult<FormDataHeader> getUnexportedFormData(PagingLoadConfig loadConfig) throws OpenXDataSecurityException;
}
