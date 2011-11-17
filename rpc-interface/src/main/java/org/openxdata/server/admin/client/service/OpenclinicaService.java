package org.openxdata.server.admin.client.service;

import java.util.List;

import org.openxdata.server.admin.model.OpenclinicaStudy;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.exception.UnexpectedException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("openclinica")
public interface OpenclinicaService extends RemoteService {
	
	List<OpenclinicaStudy> getOpenClinicaStudies() throws UnexpectedException;
	
	StudyDef importOpenClinicaStudy(String identifier) throws UnexpectedException;
	
	Boolean hasStudyData(String studyKey);
	
	void exportOpenclinicaStudyData(String studyKey);

}
