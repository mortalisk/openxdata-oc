package org.openxdata.server.dao;

import java.util.List;

import org.openxdata.server.admin.model.mapping.UserReportMap;

/**
 * @author Angel
 *
 */
public interface UserReportMapDAO extends BaseDAO<UserReportMap> {

	/**
	 * Gets a list of <code>UserReportMap</code> definitions from the database
	 * 
	 * @return list of mapped objects
	 */
	List<UserReportMap> getUserMappedReports();

	/**
	 * Saves a <code>UserReportMap</code> definition to the database
	 * 
	 * @param map map to save
	 */
	void saveUserMappedReport(UserReportMap map);

	/**
	 * Delete a <code>UserReportMap</code> definition from the database
	 * 
	 * @param map map to delete
	 */
	void deleteUserMappedReport(UserReportMap map);
	
}
