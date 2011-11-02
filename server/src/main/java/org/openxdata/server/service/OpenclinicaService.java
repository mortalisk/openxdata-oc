package org.openxdata.server.service;

import java.util.Set;

import org.openxdata.oc.transport.OpenClinicaSoapClient;
import org.openxdata.server.admin.model.OpenclinicaStudy;
import org.openxdata.server.admin.model.exception.UnexpectedException;

public interface OpenclinicaService {

	/**
	 * Check if a study has data collected for its forms
	 * @param studyKey the studyKey for the study
	 * @return boolean
	 */
	Boolean hasStudyData(String studyKey);
	
	Set<OpenclinicaStudy> getOpenClinicaStudies() throws UnexpectedException;
	
	Set<String> getStudySubjects(String studyOID) throws UnexpectedException;

	String importOpenClinicaStudy(String identifier) throws UnexpectedException;

	void exportOpenClinicaStudyData(String studyKey);

	void setClient(OpenClinicaSoapClient client);
}
