package org.openxdata.server.admin.model;

import java.util.ArrayList;
import java.util.List;


/**
 * A grouping a category for reports or other child groups.
 * This class enables us to group reports in away that enabled users
 * organise their reports in an intuitive mannner. eg Analysis Reports,
 * Financial Reports, and more.
 */
public class ReportGroup extends AbstractEditable{

	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = -3107013083813048539L;

	/** The database identifier for a report group. */
	private int reportGroupId = 0;
	
	/** The group which is the parent for this group. */
	private ReportGroup parentReportGroup;
	
	/** The name of the group. */
	private String name;
	
	/** The description for the group. */
	private String description;
	
	/** A list of report groups that this group may contain as their parent. */
	private List<ReportGroup> groups = new ArrayList<ReportGroup>();
	
	/** A list of reports that this group contains. */
	private List<Report> reports = new ArrayList<Report>();
	
	
	/**
	 * Creates a new report group object.
	 */
	public ReportGroup(){
		
	}
	
	/** 
	 * Creates a new report group object with a given name.
	 * 
	 * @param name the name of the report group.
	 */
	public ReportGroup(String name){
		this.name = name;
	}
	
	public int getReportGroupId() {
		return reportGroupId;
	}

	@Override
	public int getId() {
		return reportGroupId;
	}
	
	public void setReportGroupId(int reportGroupId) {
		this.reportGroupId = reportGroupId;
	}
	
	public ReportGroup getParentReportGroup() {
		return parentReportGroup;
	}
	
	public void setParentReportGroup(ReportGroup parentReportGroup) {
		this.parentReportGroup = parentReportGroup;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<ReportGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<ReportGroup> groups) {
		this.groups = groups;
	}

	public void addReportGroup(ReportGroup reportGroup){
		groups.add(reportGroup);
	}
	
	public void addReport(Report report){
		reports.add(report);
	}
	
	public List<Report> getReports() {
		return reports;
	}

	public void setReports(List<Report> reports) {
		this.reports = reports;
	}
	
	public void removeReport(Report report){
		reports.remove(report);
	}

	@Override
	public boolean isNew(){
		if(reportGroupId == 0)
			return true;
		
		if(reports != null){
			for(Report report : reports){
				if(report.isNew())
					return true;
			}
		}
		
		if(groups != null){
			for(ReportGroup group : groups){
				if(group.isNew())
					return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void setDirty(boolean dirty){
		super.setDirty(dirty);
		
		if(parentReportGroup != null)
			parentReportGroup.setDirty(dirty);
	}
	
	@Override
	public String toString(){
		return name;
	}
	
	
	/**
	 * Gets a report with a given name from a report groups list.
	 * 
	 * @param name the report name.
	 * @param reportGroups the report groups list.
	 * @return the report object if found, else null.
	 */
	public static Report getReport(String name, List<ReportGroup> reportGroups){
		if(reportGroups == null)
			return null;

		for(ReportGroup reportGroup : reportGroups){
			Report report  = getReport(name,reportGroup);
			if(report != null)
				return report;
		}

		return null;
	}

	
	/**
	 * Gets a report with a given name from a report group.
	 * 
	 * @param name the report name.
	 * @param reportGroup the report group.
	 * @return the report if found, else null.
	 */
	public static Report getReport(String name, ReportGroup reportGroup){
		return getReport2(name,reportGroup.getReports());
	}
	
	
	/**
	 * Gets a report with a given name from a reports list.
	 * 
	 * @param name the report name.
	 * @param reports the reports list.
	 * @return the report if found, else null.
	 */
	private static Report getReport2(String name, List<Report> reports){
		if(reports == null)
			return null;

		for(Report report : reports){
			if(report.getName().equals(name))
				return report;
		}

		return null;
	}
}
