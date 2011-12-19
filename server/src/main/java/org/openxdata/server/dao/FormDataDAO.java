package org.openxdata.server.dao;

import java.util.List;

import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDataHeader;
import org.openxdata.server.admin.model.FormDataVersion;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;

/**
 *
 */
public interface FormDataDAO extends BaseDAO<FormData> {

	/**
	 * Gets form data as identified by the id.
	 * 
	 * @param formDataId the form data identifier.
	 * @return the form data.
	 */
	FormData getFormData(Integer formDataId);
	
	/**
	 * Saves form data.
	 * 
	 * @param formData the form data to save.
	 */
	void saveFormData(FormData formData);
	
    /**
     * Creates a FormDataVersion given the FormData being backed up
     * @param formData FormData to be versioned
     */
    void saveFormDataVersion(FormData formData);
    
    /**
     * Creates a FormDataVersion given the FormData being backed up.
     * 
     * If isDelete then the it is assumed that the FormData will be deleted
     * and therefore no link to the new FormData is created
     *  
     * @param formData FormData to be versioned
     * @param isDelete boolean indicating if the FormData is going to be deleted
     */
    void saveFormDataVersion(FormData formData, boolean isDelete);
    
	/**
     * Saves form data version (backup).
     * 
     * @param formDataVersion the form data version to save.
     */
    void saveFormDataVersion(FormDataVersion formDataVersion);
    
    /**
     * Retrieves the history of the specified FormData object
     * 
     * @param formDataId Integer FormData identifier
     * @return List of FormDataVersion
     */
	List<FormDataVersion> getFormDataVersion(Integer formDataId);
	
	/**
	 * Retrieves the number of unprocessed (unexported) form data 
	 * entries for the specified form
	 * @param formDefVersionId
	 * @return
	 */
	Integer getUnprocessedDataCount(Integer formDefVersionId);

	List<FormData> getFormDataList(FormDef form);
	
	/**
	 * Gets the number of responses for the specified form definition version
	 * @param formDefId Integer form definition identifier
	 * @return Integer count of form_data
	 */
	Integer getFormDataCount(Integer formDefId);
    
	/**
	 * Gets a paged list of all unexported/unviewable form data
	 * @param loadConfig
	 * @return
	 */
	PagingLoadResult<FormDataHeader> getUnexportedFormData(PagingLoadConfig loadConfig);

}