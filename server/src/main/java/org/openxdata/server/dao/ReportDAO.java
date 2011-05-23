package org.openxdata.server.dao;

import java.util.List;

import org.openxdata.server.admin.model.Report;

/**
 * Provides data access 
 * services to the <code>Report service</code>.
 * 
 *
 */
public interface ReportDAO extends BaseDAO<Report> {

	/** Gets a list of report definitions from the database.
	 * 
	 * @return the report definition list.
	 */
	List<Report> getReports();
	
	/**
	 * Gets a report definition object from the database.
	 * 
	 * @param reportId the report definition identifier.
	 * @return the report definition object.
	 */
	Report getReport(Integer reportId) ;
	
	/**
	 * Saves a report definition to the database.
	 * 
	 * @param report the report definition to save.
	 */
	void saveReport(Report report);
	
	/**
	 * Deletes a report definition from the database.
	 * 
	 * @param report the report definition to delete.
	 */
	void deleteReport(Report report);
	
}
