package org.openxdata.server.admin.model;

/**
 * Holds data about a report.
 */
public class Report extends AbstractEditable{

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 6089598959091826602L;

	/** The database unique identifier of the report. */
	private int reportId = 0;
	
	/** The report name. */
	private String name;
	
	/** The report description. */
	private String description;
	
	/** The report definition file xml contents. */
	private String definition;
	
	/** The report sql statement. */
	private String querySql;
	
	/** The report query definition. */
	private String queryDefinition;
	
	/** The report parameter values */
	private String paramValues = "Report Listing";
	
	/** The form version id that is used to define this report. */
	private Integer formDefVersionId;
	
	/** The group to which this report belongs. */
	private ReportGroup reportGroup;
	
	
	/**
	 * Creates a new report object. 
	 */
	public Report(){
		
	}
	
	/**
	 * Creates a new report object with a given name.
	 * 
	 * @param name the name of the report.
	 */
	public Report(String name){
		this.name = name;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getQuerySql() {
		return querySql;
	}

	public void setQuerySql(String querySql) {
		this.querySql = querySql;
	}

	public String getQueryDefinition() {
		return queryDefinition;
	}

	public void setQueryDefinition(String queryDefinition) {
		this.queryDefinition = queryDefinition;
	}

	public Integer getFormDefVersionId() {
		return formDefVersionId;
	}

	public void setFormDefVersionId(Integer formDefVersionId) {
		this.formDefVersionId = formDefVersionId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getReportId() {
		return reportId;
	}

	@Override
	public int getId() {
		return reportId;
	}
	
	public void setReportId(int reportId) {
		this.reportId = reportId;
	}
	
	public String getParamValues() {
		return paramValues;
	}

	public void setParamValues(String paramValues) {
		this.paramValues = paramValues;
	}

	public ReportGroup getReportGroup() {
		return reportGroup;
	}

	public void setReportGroup(ReportGroup reportGroup) {
		this.reportGroup = reportGroup;
	}

	@Override
	public boolean isNew(){
		return reportId == 0;
	}
	
	@Override
	public void setDirty(boolean dirty){
		super.setDirty(dirty);
		
		if(reportGroup != null)
			reportGroup.setDirty(dirty);
	}
	
	@Override
	public String toString(){
		return name;
	}
}
