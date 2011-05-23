package org.openxdata.server.dao;

import java.util.List;

import org.openxdata.server.admin.model.mapping.UserReportGroupMap;

/**
 * @author Angel
 *
 */
public interface UserReportGroupMapDAO extends BaseDAO<UserReportGroupMap> {
	
	/**
	 * Gets a list of <code>UserReportGroupMap</code> definitions from the database.
	 */
	List<UserReportGroupMap> getUserMappedReportGroups();

	void deleteUserMappedReportGroup(UserReportGroupMap map);

	void saveUserMappedReportGroup(UserReportGroupMap map);

}
