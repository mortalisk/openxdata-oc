package org.openxdata.server.admin.client.service;

import java.util.List;

import org.openxdata.server.admin.model.Report;
import org.openxdata.server.admin.model.ReportGroup;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.mapping.UserReportGroupMap;
import org.openxdata.server.admin.model.mapping.UserReportMap;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Defines the client side contract for the Report Service.
 */
public interface ReportService extends RemoteService {
	
	/**
	 * Fetches all the <tt>Reports</tt> in the system.
	 * 
	 * @return <tt>List</tt> of <tt>Reports.</tt>
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	List<ReportGroup> getReports() throws OpenXDataSecurityException;
	
	/**
	 * Fetches the report data for the specified report
	 * @param report Report to run
	 * @param format String format (not used currently)
	 * @return String data in HTML format
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	String getReportData(Report report, String format) throws OpenXDataSecurityException;

	/**
	 * Saves a dirty or new <tt>Report.</tt>
	 * 
	 * @param report <tt>Report</tt> to save.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void saveReport(Report report) throws OpenXDataSecurityException;
	
	/**
	 * Saves a dirty or new <tt>Report Group.</tt>
	 * 
	 * @param reportGroup <tt>Report Group</tt> to save.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void saveReportGroup(ReportGroup reportGroup) throws OpenXDataSecurityException;
		
	/**
	 * Deletes a given <tt>Report.</tt>
	 * 
	 * @param report <tt>Report</tt> to delete.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void deleteReport(Report report) throws OpenXDataSecurityException;
	
	/**
	 * Deletes a given <tt>Report Group.</tt>
	 * 
	 * @param reportGroup <tt>Report Group</tt> to delete.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void deleteReportGroup(ReportGroup reportGroup) throws OpenXDataSecurityException;
	
	/**
	 * Fetches all the <tt>User Mapped Report Groups</tt> in the system.
	 * 
	 * @return <tt>List</tt> of <tt>User Mapped Report Groups.</tt>
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>e layer.</tt>
	 */
	List<UserReportGroupMap> getUserMappedReportGroups() throws OpenXDataSecurityException;
	
	/**
	 * Save a dirty or new <tt>User Mapped Report Group.</tt>
	 * 
	 * @param userReportGroupMap <tt>User Mapped Report Group</tt> to save.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void saveUserMappedReportGroup(UserReportGroupMap userReportGroupMap) throws OpenXDataSecurityException;
	
	/**
	 * Deletes a given <tt>User Mapped Report Group.</tt>
	 * 
	 * @param userReportGroupMap <tt>User Mapped Report Group</tt> to delete.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void deleteUserMappedReportGroup(UserReportGroupMap userReportGroupMap) throws OpenXDataSecurityException;
	
	/**
	 * Fetches all the <tt>User Mapped Reports</tt> in the system.
	 * 
	 * @return <tt>List</tt> of <tt>User Mapped Reports.</tt>
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	List<UserReportMap> getUserMappedReports() throws OpenXDataSecurityException;
	
	/**
	 * Save a dirty or new <tt>User Mapped Report.</tt>
	 * 
	 * @param userReportMap <tt>User Mapped Report</tt> to save.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void saveUserMappedReport(UserReportMap userReportMap) throws OpenXDataSecurityException;
	
	/**
	 * Deletes a given <tt>User Mapped Report.</tt>
	 * 
	 * @param userReportMap <tt>User Mapped Report</tt> to delete.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void deleteUserMappedReport(UserReportMap userReportMap) throws OpenXDataSecurityException;

}
