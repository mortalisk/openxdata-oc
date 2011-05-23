package org.openxdata.server.dao.hibernate;

import java.util.List;

import org.openxdata.server.admin.model.ReportGroup;
import org.openxdata.server.dao.ReportGroupDAO;
import org.springframework.stereotype.Repository;

/**
 * @author Angel
 *
 */
@Repository("reportGroupDAO")
public class HibernateReportGroupDAO extends BaseDAOImpl<ReportGroup> implements ReportGroupDAO {
	
	@Override
	public void deleteReportGroup(ReportGroup reportGroup) {
		remove(reportGroup);
	}

	@Override
	public ReportGroup getReportGroup(String groupName) {
        return searchUniqueByPropertyEqual("name", groupName);
	}

	@Override
	public List<ReportGroup> getReportGroups() {
		return findAll();
	}

	@Override
	public void saveReportGroup(ReportGroup reportGroup) {
		save(reportGroup);
	}
}
