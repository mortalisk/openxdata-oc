package org.openxdata.server.dao;

import java.util.List;

import org.openxdata.server.admin.model.ReportGroup;

/**
 * @author Angel
 *
 */
public interface ReportGroupDAO extends BaseDAO<ReportGroup> {

	/** Gets a list of report definitions from the database.
	 * 
	 * @return the report definition list.
	 */
	List<ReportGroup> getReportGroups();
	
	ReportGroup getReportGroup(String groupName);
	
	/**
	 * Saves a report group to the database.
	 * 
	 * @param reportGroup the report group to save.
	 */
	void saveReportGroup(ReportGroup reportGroup);
	
	/**
	 * Deletes a report group from the database.
	 * 
	 * @param reportGroup the report group to delete.
	 */
	void deleteReportGroup(ReportGroup reportGroup);
}
