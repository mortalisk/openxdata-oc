package org.openxdata.server.admin.client.service;

import java.util.List;

import org.openxdata.server.admin.model.OpenclinicaStudy;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("openclinica")
public interface OpenclinicaService extends RemoteService {
	
	List<OpenclinicaStudy> getOpenClinicaStudies();
	
	String importOpenClinicaStudy(String identifier);
	
	Boolean hasStudyData(String studyKey);
	
	void exportOpenclinicaStudyData(String studyKey);

}
