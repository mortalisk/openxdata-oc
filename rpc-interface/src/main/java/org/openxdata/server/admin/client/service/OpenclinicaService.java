package org.openxdata.server.admin.client.service;

import java.util.Set;

import org.openxdata.server.admin.model.OpenclinicaStudy;
import org.openxdata.server.admin.model.exception.UnexpectedException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("openclinica")
public interface OpenclinicaService extends RemoteService {
	
	Set<OpenclinicaStudy> getOpenClinicaStudies() throws UnexpectedException;
	
	String importOpenClinicaStudy(String identifier) throws UnexpectedException;
	
	Boolean hasStudyData(String studyKey);
	
	void exportOpenclinicaStudyData(String studyKey);

}
