package org.openxdata.server.dao;

import java.util.List;
import java.util.Map;

import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.mapping.UserStudyMap;

/**
 *
 */
public interface UserStudyMapDAO extends BaseDAO<UserStudyMap> {

	/**
	 * Gets a list of StudyUserMap definitions from the database
	 * 
	 * @return list of mapped objects
	 */
	List<UserStudyMap> getUserMappedStudies();
	
	/**
	 * Gets a list of the UserStudyMap for a specified Study
	 * @param studyId
	 * @return
	 */
	List<UserStudyMap> getUserMappedStudies(Integer studyId);
	
	/**
	 * Gets a specific user study map
	 * @param userId
	 * @param studyId
	 * @return
	 */
	UserStudyMap getUserStudyMap(Integer userId, Integer studyId);
	
	/**
	 * Deletes a StudyUserMap definition from the database
	 * 
	 * @param map map to delete
	 */
	void deleteUserMappedStudy(UserStudyMap map);
	
	/**
	 * Saves a StudyUserMap definition to the database
	 * 
	 * @param map map to save
	 */
	void saveUserMappedStudy(UserStudyMap map);
	
    /**
     * Gets a list of study names mapped to study ids
     *
     * @param user User with permissions
     * @return the study list
     */
    Map<Integer, String> getStudyNamesForUser(User user);

	/**
	 * Delete all StudyUserMap entries for a study
	 * 
	 * @param studyId
	 */
	void deleteUserMappedStudies(int studyId);
	
}
