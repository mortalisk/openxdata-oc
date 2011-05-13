package org.openxdata.server.admin.server;

import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDataHeader;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.admin.model.mapping.UserStudyMap;
import org.openxdata.server.rpc.OxdPersistentRemoteService;
import org.springframework.web.context.WebApplicationContext;

/**
 * Default Implementation for the StudyManagerService <code>Interface.</code>
 */
public class StudyManagerServiceImpl extends OxdPersistentRemoteService implements
		org.openxdata.server.admin.client.service.StudyManagerService {

	private static final long serialVersionUID = 579255315976404465L;
	private org.openxdata.server.service.StudyManagerService studyManager;

	public StudyManagerServiceImpl() {
	}

	@Override
	public void init() throws ServletException {
		super.init();
		WebApplicationContext ctx = getApplicationContext();
		studyManager = (org.openxdata.server.service.StudyManagerService) ctx
				.getBean("studyManagerService");
	}

	@Override
	public List<StudyDef> getStudies() {
		return studyManager.getStudies();
	}

	@Override
	public void saveStudy(StudyDef studyDef) {
		studyManager.saveStudy(studyDef);
	}

	@Override
	public void deleteStudy(StudyDef studyDef) {
		studyManager.deleteStudy(studyDef);
	}

	@Override
	public void saveForm(FormDef formDef) {
		studyManager.saveForm(formDef);
	}

	@Override
	public void deleteForm(FormDef formDef) {
		studyManager.deleteForm(formDef);
	}

	@Override
	public void deleteFormData(Integer formDataId) {
		studyManager.deleteFormData(formDataId);
	}

	@Override
	public List<FormDataHeader> getFormData(Integer formDefId, Integer userId, Date fromDate,
			Date toDate) {
		return studyManager.getFormData(formDefId, userId, fromDate, toDate);
	}

	@Override
	public FormData getFormData(Integer formDataId) {
		return studyManager.getFormData(formDataId);
	}

	@Override
	public Boolean hasEditableData(Editable item) {
		return studyManager.hasEditableData(item);
	}
	
	@Override
	public void deleteUserMappedForm(UserFormMap map) {
		studyManager.deleteUserMappedForm(map);
	}

	@Override
	public void deleteUserMappedStudy(UserStudyMap map) {
		studyManager.deleteUserMappedStudy(map);
	}

	@Override
	public List<UserFormMap> getUserMappedForms() {
		return studyManager.getUserMappedForms();
	}

	@Override
	public List<UserStudyMap> getUserMappedStudies() {
		return studyManager.getUserMappedStudies();
	}

	@Override
	public void saveUserMappedForm(UserFormMap map) {
		studyManager.saveUserMappedForm(map);
	}

	@Override
	public void saveUserMappedStudy(UserStudyMap map) {
		studyManager.saveUserMappedStudy(map);
	}
}
