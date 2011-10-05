package org.openxdata.server.dao;

import java.util.List;

import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;

/**
 *
 * @author Jonny Heggheim
 */
public interface StudyDAO extends BaseDAO<StudyDef>{

    /**
     * Gets a list of studies.
     *
     * @return the study list
     */
    List<StudyDef> getStudies();
    
    /**
     * Gets a study given an id
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
	 * Gets the key of a study with a given id.
	 * 
	 * @param studyId
	 *            the identifier of the study.
	 * @return the key of the study or null if not found
	 */
	String getStudyKey(Integer studyId);

	/**
	 * Gets the name of a study with a given id.
	 * 
	 * @param studyId
	 *            the identifier of the study.
	 * @return the name of the study or null if not found
	 */
	String getStudyName(int studyId);
	
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

	StudyDef getStudy(String studyKey);
	
	/**
	 * Get a page of Studies mapped to a specific User 
	 * @param userId Integer id of specified user
	 * @param loadConfig PagingLoadConfig specifying page size and number
	 * @return
	 */
	PagingLoadResult<StudyDef> getMappedStudies(Integer userId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException;
	
	/**
	 * Get a page of Studies NOT mapped to the specified User
	 * @param userId Integer id of specified user
	 * @param loadConfig PagingLoadConfig specifying page size and number
	 * @return
	 */
	PagingLoadResult<StudyDef> getUnmappedStudies(Integer userId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException;
}
