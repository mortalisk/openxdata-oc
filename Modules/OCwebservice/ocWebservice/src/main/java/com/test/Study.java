package com.test;

/** 
 *  .
 * @author gbro
 *
 */

public class Study {

	private String Description;
	
	private String DateCreated;
	
	private String PrincipalInvestigator;
	
	private String StudyName;
	
	private String StudyId;

	
	public Study() {
		
	}
		
	public String getStudyName() {
		return StudyName;
	}

	public void setStudyName(String StudyName) {
		this.StudyName = StudyName;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String Description) {
		this.Description = Description;
	}

	public String getStudyId() {
		return StudyId;
	}

	public void setStudyId(String StudyId) {
		this.StudyId = StudyId;
	}
	
	public String getDateCreated() {
		return DateCreated;
	}

	public void setDateCreated(String DateCreated) {
		this.DateCreated = DateCreated;
	}
	public String getPrincipalInvestigator() {
		return PrincipalInvestigator;
	}

	public void setPrincipalInvestigator(String PrincipalInvestigator) {
		this.PrincipalInvestigator = PrincipalInvestigator;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" { Study Details --");
		sb.append("Description:" + getDescription());
		sb.append(", ");
		sb.append("StudyName:" + getStudyName());
		sb.append(", ");
		sb.append("DateCreated:" + getDateCreated());
		sb.append(", ");
		sb.append("PrincipalInvestigator:" + getPrincipalInvestigator());
		sb.append(", ");
		sb.append("StudyId:" + getStudyId());
		sb.append(". } \n");
		
		return sb.toString();
	}
}
