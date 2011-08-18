package org.openxdata.server.service;

import java.util.List;
import java.util.Map;

import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.ExportedFormDataList;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.exception.ExportedDataNotFoundException;
import org.openxdata.server.admin.model.mapping.UserFormMap;


public interface FormService {

	/**
	 * Returns a given Form given the ID.
	 * 
	 * @param formId Id of the Form to retrieve.
	 * @return FormDef
	 */
	FormDef getForm(int formId);
	
	/**
	 * Gets form data as identified by the id.
	 * 
	 * @param formDataId the form data identifier.
	 * @return the form data.
	 */
	FormData getFormData(Integer formDataId);
	
	/**
	 * Deletes a form definition from the database.
	 * 
	 * @param formDef the form definition to delete.
	 */
	void deleteForm(FormDef formDef);
	
	/**
	 * Deletes a row of data from the database.
	 * 
	 * @param formDataId the identifier for the row of data to delete.
	 */
	void deleteFormData(Integer formDataId);
	
	/**
	 * Saves a given Form.
	 * 
	 * @param formDef Form to save.
	 */
	void saveForm(FormDef formDef);
	
    /**
     * Saves the data captured by the user for a particular form
     * @param formData FormData
     * @return FormData that was saved (contains id reference)
     */
    FormData saveFormData(FormData formData);
    
    /**
     * Retrieves all the form definitions in the system
     * @return List of FormDef
     */
    List<FormDef> getForms();
    
    /**
     * Retrives all the form definitions that are available for the currently logged in user
     * @return List of FormDef
     */
    List<FormDef> getFormsForCurrentUser();
    
    /**
     * Retrieves all the names of the forms that are available for the currently logged in user
     * and are under the specified study
     * @param studyId identifier of the study
     * @return
     */
    Map<Integer, String> getFormNamesForCurrentUser(Integer studyId);

    /**
     * Retrives all usermapped forms from the database
     * @return List of userFormMaps
     */
    List<UserFormMap> getUserMappedForms();
    
    /**
     * Retrieves all the User Mapped Forms for a particular Form
     * @param formId Integer id of the specified form
     * @return List of UserFormMap
     */
    List<UserFormMap> getUserMappedForms(Integer formId);

      /**
     * saves a usermapped form
     * @param map usermappedform
     */
    void saveUserMappedForm(UserFormMap map);
     /**
     * deletes a usermapped form
     * @param map usermappedform
     */
    void deleteUserMappedForm(UserFormMap map);
    /**
     * Calculates the number of responses captured for a specified formDefVersion
     * @param formId int identifier for a form definition version
     * @return Integer (positive number, 0 for no responses)
     */
    Integer getFormResponseCount(int formDefVersionId);
    
    /**
     * Retrieves all the FormData for a specified formDefVersion
     * @param formId int identifier of the form definition version
     * @return List of FormData
     */
    List<FormData> getFormData(int formDefVersionId);

    /**
     * Checks if a study, form or form version has data collected for it.
     *
     * @param item the study, form, or form version.
     * @return true if it has, else false.
     */
    Boolean hasEditableData(Editable item);

    /**
     * Retrieves a page of the form data (directly from exported tables) for a specified form definition
     * @param formBinding String xform binding (table name)
     * @param formFields String question binding (column names)
     * @param offset int indicating at which position to start returning FormData objects
     * @param limit int indicating how many FormData objects to return
     * @param sortField String containing binding of field to sort
     * @param ascending boolean true if the sort should be ascending
     * @return ExportedFormDataList containing ExportedData
     * @throws ExportedDataNotFoundException when the exported table does not exist
     */
    ExportedFormDataList getFormDataList(String formBinding, String[] questionBindings, int offset, int limit, 
    		String sortField, boolean ascending) throws ExportedDataNotFoundException;
}
