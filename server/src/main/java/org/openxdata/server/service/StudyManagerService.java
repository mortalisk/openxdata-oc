package org.openxdata.server.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDataHeader;
import org.openxdata.server.admin.model.FormDataVersion;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.admin.model.mapping.UserStudyMap;


/**
 * This service is used for managing studies and their forms together with versions.
 * 
 * @author daniel
 *
 */
public interface StudyManagerService {
	
	/**
	 * Gets a list of studies.
	 * 
	 * @return the study list
	 */
	List<StudyDef> getStudies();
	
	/**
	 * Gets a list of study names mapped to study ids - for the logged in user
	 * 
	 * @return the study list
	 */
	Map<Integer, String> getStudyNamesForCurrentUser();
	
	/**
	 * Returns a StudyDef given an id
	 * @param id
	 * @return
	 */
	StudyDef getStudy(Integer id);
	
	/**
	 * Saves a study to the database.
	 * 
	 * @param studyDef the study to save.
	 */
	void saveStudy(StudyDef studyDef);
	
	/**
	 * Deletes a study from the database.
	 * 
	 * @param studyDef the study to delete.
	 */
	void deleteStudy(StudyDef studyDef);
	
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
	 * Deletes a row of data from the database.
	 * 
	 * @param formDataId the identifier for the row of data to delete.
	 */
	void deleteFormData(Integer formDataId);
	
	/**
	 * Gets form data as identified by the id.
	 * 
	 * @param formDataId the form data identifier.
	 * @return the form data.
	 */
	FormData getFormData(Integer formDataId);

	/**
	 * Checks if a study, form or form version has data collected for it.
	 * 
	 * @param item
	 *            the study, form, or form version.
	 * @return true if it has, else false.
	 */
	Boolean hasEditableData(Editable item); 
	
	/**
	 * Gets a list of headers for form data submitted to the database.
	 * 
	 * @param formDefId
	 * @param userId the user who submitted the data. If you want all users, pass null.
	 * @param fromDate the submission date from which to start the search. To include all dates, pass null.
	 * @param toDate the submission date up to which to do the search. To include all dates, pass null.
	 * @return the form data header list.
	 */
	List<FormDataHeader> getFormData(Integer formDefId, Integer userId, Date fromDate, Date toDate);
	
    /**
     * Retrieves the history of the specified FormData object
     * 
     * @param formDataId Integer FormData identifier
     * @return List of FormDataVersion
     */
    List<FormDataVersion> getFormDataVersion(Integer formDataId);

	/**
	 * Gets a list of all StudyUserMap objects from the database
	 * 
	 * @return list of mapped objects
	 */
	List<UserStudyMap> getUserMappedStudies();
	
	/**
	 * Gets a list of UserStudyMap objects for a specified Study
	 * @param studyId
	 * @return
	 */
	List<UserStudyMap> getUserMappedStudies(Integer studyId);
	
	/**
	 * Saves a StudyUserMap object
	 * @param map map to save
	 */
	void saveUserMappedStudy(UserStudyMap map);
	
	/**
	 * Deletes a StudyUserMap object
	 * @param map map to save
	 */
	void deleteUserMappedStudy(UserStudyMap map);
	
	/**
	 * Gets all the forms that are mapped to the specified user
	 * @param user User
	 * @return List of FormDef
	 */
	List<FormDef> getFormsForUser(User user);
	
	/**
	 * Gets all the forms that are mapped to the specified user in a specified study
	 * @param user User
	 * @param studyDefId Integer
	 * @return List of FormDef
	 */
	List<FormDef> getFormsForUser(User user, Integer studyDefId);

	/**
	 * Deletes a given <code>UserFormMap.</code>
	 * @param map map to delete.
	 */
	void deleteUserMappedForm(UserFormMap map);

	/**
	 * Fetches a List of <code>UserFormMap</code> definitions.
	 * @return List of <code>UserFormMaps.</code>
	 */
	List<UserFormMap> getUserMappedForms();

	/**
	 * Saves a given <code>UserFormMap.</code>
	 * @param map map to save.
	 */
	void saveUserMappedForm(UserFormMap map);

	/**
	 * Gets the key of a study with a given id.
	 * 
	 * @param studyId
	 *            the identifier of the study.
	 * @return the key of the study or an empty string if not found.
	 */
	String getStudyKey(int studyId);
	
	/**
	 * Gets the name of a study with a given id.
	 * 
	 * @param studyId
	 *            the identifier of the study.
	 * @return the name of the study or "UNKNOWN STUDY" if not found
	 */
	String getStudyName(int studyId);

    /**
     * Sets the Users who have permissions to access a given form. Note that existing permissions will be overridden.
     * 
     * @param form Form to restrict access for.
     * @param users Definite list of users who will have access to the form.
     * @throws OpenXDataSecurityException If User does not have permission to map objects.
     */
	void setUserMappingForForm(FormDef form, List<User> users);

    /**
     * Sets the Users who have permissions to access a given Study. Note that existing permissions will be overridden.
     * 
     * @param study Study to restrict access for.
     * @param users Definite list of users who will have access to the form.
     * @throws OpenXDataSecurityException If User does not have permission to map objects.
     */
	void setUserMappingForStudy(StudyDef study, List<User> users);
	
	/**
	 * Get a list of studies with the specified name
	 * 
	 * @param studyName
	 * @return a list of StudyDef objects
	 */
	List<StudyDef> getStudyByName(String studyName);
	
	/**
	 * Get a list of forms with the specified name
	 * 
	 * @param formName
	 * @return a list of FormDef objects
	 */
	List<FormDef> getFormByName(String formName);
}
