package org.openxdata.server.service;

import java.util.List;
import java.util.Map;

import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.StudyDefHeader;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;
import org.openxdata.server.dao.StudyDAO;


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
	 * Checks if a study, form or form version has data collected for it.
	 * 
	 * @param item
	 *            the study, form, or form version.
	 * @return true if it has, else false.
	 */
	Boolean hasEditableData(Editable item); 

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
	 * Get a list of studies with the specified name
	 * 
	 * @param studyName
	 * @return a list of StudyDef objects
	 */
	List<StudyDef> getStudyByName(String studyName);

	/**
	 * Get a page of Users mapped to a specific study 
	 * @param studyId
	 * @param loadConfig
	 * @return
	 */
	PagingLoadResult<User> getMappedUsers(Integer studyId, PagingLoadConfig loadConfig);
	
	/**
	 * Get a page of Users NOT mapped to the specified study
	 * @param studyId
	 * @param loadConfig
	 * @return
	 */
	PagingLoadResult<User> getUnmappedUsers(Integer studyId, PagingLoadConfig loadConfig);
	
	/**
	 * Updates the users currently mapped to the specified study.
	 * @param studyId Integer id of specified study
	 * @param usersToAdd List of users to add to the study mapping
	 * @param usersToDelete List of users to delete from the study mapping
	 * @throws OpenXDataSecurityException
	 */
	void saveMappedStudyUsers(Integer studyId, List<User> usersToAdd, List<User> usersToDelete);
	
	/**
	 * Get a page of Studies mapped to a specific User 
	 * @param userId Integer id of specified user
	 * @param loadConfig PagingLoadConfig specifying page size and number
	 * @return
	 */
	PagingLoadResult<StudyDef> getMappedStudies(Integer userId, PagingLoadConfig loadConfig);

	/**
	 * Get a page of Studies mapped to a specific User 
	 * @param userId Integer id of specified user
	 * @param loadConfig PagingLoadConfig specifying page size and number
	 * @return
	 */
	PagingLoadResult<StudyDefHeader> getMappedStudyNames(Integer userId, PagingLoadConfig loadConfig);
	
	/**
	 * Get a page of Studies NOT mapped to the specified User
	 * @param userId Integer id of specified user
	 * @param loadConfig PagingLoadConfig specifying page size and number
	 * @return
	 */
	PagingLoadResult<StudyDefHeader> getUnmappedStudyNames(Integer userId, PagingLoadConfig loadConfig);
	
	StudyDef getStudy(String studyKey);

	void setStudyDAO(StudyDAO studyDAO);
	
	void saveMappedUserStudyNames(Integer userId, List<StudyDefHeader> studiesToAdd, List<StudyDefHeader> studiesToDelete);
}
