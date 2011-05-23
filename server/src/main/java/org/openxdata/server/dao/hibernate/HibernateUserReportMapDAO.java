package org.openxdata.server.dao.hibernate;

import java.util.List;

import org.openxdata.server.admin.model.mapping.UserReportMap;
import org.openxdata.server.dao.UserReportMapDAO;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository("userReportMapDAO")
public class HibernateUserReportMapDAO extends BaseDAOImpl<UserReportMap> implements UserReportMapDAO {

	/* (non-Javadoc)
	 * @see org.openxdata.server.dao.UserReportMapDAO#deleteUserMappedReport(org.openxdata.server.admin.model.mapping.UserReportMap)
	 */
	@Override
	public void deleteUserMappedReport(UserReportMap map) {
		remove(map);

	}

	/* (non-Javadoc)
	 * @see org.openxdata.server.dao.UserReportMapDAO#getUserMappedReports()
	 */
	@Override
	public List<UserReportMap> getUserMappedReports() {
		return findAll();
	}

	/* (non-Javadoc)
	 * @see org.openxdata.server.dao.UserReportMapDAO#saveUserMappedReport(org.openxdata.server.admin.model.mapping.UserReportMap)
	 */
	@Override
	public void saveUserMappedReport(UserReportMap map) {
		save(map);

	}
	
}
