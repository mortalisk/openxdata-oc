package org.openxdata.server.dao;

import java.util.List;

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
	
}
