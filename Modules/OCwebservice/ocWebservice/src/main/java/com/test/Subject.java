package com.test;

/** 
 *  .
 * @author gbro
 *
 */

public class Subject {

	private String Gender;
	
	private String DateCreated;
	
	private String Unique_identifier;
	
	private String SubjectId;
	
	private String StudyId;

	
	public Subject() {
		
	}
		
	public String getSubjectId() {
		return SubjectId;
	}

	public void setSubjectId(String SubjectId) {
		this.SubjectId = SubjectId;
	}

	public String getGender() {
		return Gender;
	}

	public void setGender(String Gender) {
		this.Gender = Gender;
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
	public String getUnique_identifier() {
		return Unique_identifier;
	}

	public void setUnique_identifier(String Unique_identifier) {
		this.Unique_identifier = Unique_identifier;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" { Subject Details --");
		sb.append("Gender:" + getGender());
		sb.append(", ");
		sb.append("SubjectId:" + getSubjectId());
		sb.append(", ");
		//sb.append("DateCreated:" + getDateCreated());
		//sb.append(", ");
		sb.append("Unique_identifier:" + getUnique_identifier());
		sb.append(", ");
		sb.append("StudyId:" + getStudyId());
		sb.append(". } \n");
		
		return sb.toString();
	}
}
