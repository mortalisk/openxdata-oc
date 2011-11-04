package org.openxdata.server.service;

import java.util.List;

import org.openxdata.server.admin.model.OpenclinicaStudy;
import org.openxdata.server.admin.model.exception.UnexpectedException;

public interface OpenclinicaService {

	Boolean hasStudyData(String studyKey);
	
	List<OpenclinicaStudy> getOpenClinicaStudies() throws UnexpectedException;
	
	List<String> getStudySubjects(String studyOID) throws UnexpectedException;

	String importOpenClinicaStudy(String identifier) throws UnexpectedException;

	void exportOpenClinicaStudyData(String studyKey);

}
