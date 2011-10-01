package org.openxdata.server.service;

import java.util.List;

import org.openxdata.server.admin.model.OpenclinicaStudy;

public interface OpenclinicaService {

	/**
	 * Check if a study has data collected for its forms
	 * @param studyKey the studyKey for the study
	 * @return boolean
	 */
	Boolean hasStudyData(String studyKey);
	
	List<OpenclinicaStudy> getOpenClinicaStudies();

	String importOpenClinicaStudy(String identifier);

	void exportOpenClinicaStudyData(String studyKey);
}
