package org.openxdata.server.admin.client.service;

import java.util.Set;

import org.openxdata.server.admin.model.OpenclinicaStudy;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("openclinica")
public interface OpenclinicaService extends RemoteService {
	
	Set<OpenclinicaStudy> getOpenClinicaStudies();
	
	String importOpenClinicaStudy(String identifier);
	
	Boolean hasStudyData(String studyKey);
	
	void exportOpenclinicaStudyData(String studyKey);

}
