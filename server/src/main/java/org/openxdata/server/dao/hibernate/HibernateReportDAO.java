package org.openxdata.server.dao.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.openxdata.server.admin.model.Report;
import org.openxdata.server.dao.ReportDAO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provides a hibernate implementation
 * of the <code>ReportDAO</code> data access <code> interface.</code>
 * 
 * @author Angel
 *
 */
@Repository("reportDAO")
@Transactional(propagation=Propagation.MANDATORY)
public class HibernateReportDAO extends BaseDAOImpl<Report> implements ReportDAO {
	
	@Override
	public void deleteReport(Report report) {
		remove(report);
	}
	
	@Override
	public Report getReport(Integer reportId) {
		Query query = getSessionFactory().getCurrentSession().createQuery(
		"from Report where reportId = :reportId");
		query.setParameter("reportId", reportId);
		
		return (Report) query.uniqueResult();
	}
	
	@Override
	public List<Report> getReports() {
		return findAll();
	}
	
	@Override
	public void saveReport(Report report) {
		save(report);
	} 

}
