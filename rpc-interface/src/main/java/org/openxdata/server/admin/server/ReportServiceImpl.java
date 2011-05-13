package org.openxdata.server.admin.server;

import java.util.List;

import javax.servlet.ServletException;

import org.openxdata.server.admin.client.service.ReportService;
import org.openxdata.server.admin.model.Report;
import org.openxdata.server.admin.model.ReportGroup;
import org.openxdata.server.admin.model.mapping.UserReportGroupMap;
import org.openxdata.server.admin.model.mapping.UserReportMap;
import org.openxdata.server.rpc.OxdPersistentRemoteService;
import org.springframework.web.context.WebApplicationContext;

/**
 * Default Implementation for the <code>ReportService Interface.</code>
 */
public class ReportServiceImpl extends OxdPersistentRemoteService implements ReportService {

	private org.openxdata.server.service.ReportService reportService;
	private static final long serialVersionUID = -4224505027873253611L;
	
	public ReportServiceImpl() {}
	
	@Override
	public void init() throws ServletException {
		super.init();
		WebApplicationContext ctx = getApplicationContext();
		reportService = (org.openxdata.server.service.ReportService)ctx.getBean("reportService");
	}

	@Override
	public void deleteReport(Report report) {
		reportService.deleteReport(report);

	}

	@Override
	public void deleteReportGroup(ReportGroup reportGroup) {
		reportService.deleteReportGroup(reportGroup);
	}

	@Override
	public String getReportData(Report report, String format) {
		throw new UnsupportedOperationException("Removed");
	}

	@Override
	public List<ReportGroup> getReports() {
		return reportService.getReportGroups();
	}

	@Override
	public void saveReport(Report report) {
		reportService.saveReport(report);

	}

	@Override
	public void saveReportGroup(ReportGroup reportGroup) {
		reportService.saveReportGroup(reportGroup);
	}

	@Override
	public void deleteUserMappedReport(UserReportMap userReportMap) {
		reportService.deleteUserMappedReport(userReportMap);
	}

	@Override
	public void deleteUserMappedReportGroup(UserReportGroupMap userReportGroupMap) {
		reportService.deleteUserMappedReportGroup(userReportGroupMap);
	}

	@Override
	public List<UserReportGroupMap> getUserMappedReportGroups() {
		return reportService.getUserMappedReportGroups();
	}
	
	@Override
	public List<UserReportMap> getUserMappedReports() {
		return reportService.getUserMappedReports();
	}

	@Override
	public void saveUserMappedReport(UserReportMap userReportMap) {
		reportService.saveUserMappedReport(userReportMap);
	}

	@Override
	public void saveUserMappedReportGroup(UserReportGroupMap userReportMap) {
		reportService.saveUserMappedReportGroup(userReportMap);
	}

	public byte[] getReportDataBytes(String baseUrl, Report report, String format) {
		throw new UnsupportedOperationException("Removed");
	}
	
	public byte[] getReportDataBytes(String baseUrl, Integer reportId, String format) {
		throw new UnsupportedOperationException("Removed");
	}

}
