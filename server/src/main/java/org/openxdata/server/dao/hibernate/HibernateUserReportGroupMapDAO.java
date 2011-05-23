package org.openxdata.server.dao.hibernate;

import java.util.List;

import org.openxdata.server.admin.model.mapping.UserReportGroupMap;
import org.openxdata.server.dao.UserReportGroupMapDAO;
import org.springframework.stereotype.Repository;

/**
 * @author Angel
 *
 */
@Repository("userReportGroupMapDAO")
public class HibernateUserReportGroupMapDAO extends BaseDAOImpl<UserReportGroupMap> implements UserReportGroupMapDAO {

	@Override
	public List<UserReportGroupMap> getUserMappedReportGroups() {
		return findAll();
	}

	@Override
	public void deleteUserMappedReportGroup(UserReportGroupMap map) {
		remove(map);		
	}

	@Override
	public void saveUserMappedReportGroup(UserReportGroupMap map) {
		save(map);		
	}
}
