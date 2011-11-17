package org.openxdata.server.servlet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openxdata.proto.SubmissionContext;
import org.openxdata.proto.exception.ProtocolAccessDeniedException;
import org.openxdata.proto.exception.ProtocolInvalidSessionReferenceException;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.service.FormDownloadService;
import org.openxdata.server.service.StudyManagerService;
import org.openxdata.server.service.UserService;

public class DefaultSubmissionContext implements SubmissionContext {

	private String locale;
	private byte action;
	private DataInputStream input;
	private DataOutputStream output;
	private UserService userService;
	private FormDownloadService formService;
	private StudyManagerService studyManagerService;

	public DefaultSubmissionContext(DataInputStream input,
			DataOutputStream output, byte action, String locale,
			UserService userService, FormDownloadService formService,
			StudyManagerService studyManagerService) {
		this.input = input;
		this.output = output;
		this.locale = locale;
		this.action = action;
		this.userService = userService;
		this.formService = formService;
		this.studyManagerService = studyManagerService;
	}

	@Override
	public byte getAction() {
		return action;
	}

	@Override
	public String getLocale() {
		return locale;
	}

	@Override
	public DataInputStream getInputStream() {
		return input;
	}

	@Override
	public DataOutputStream getOutputStream() {
		return output;
	}

	@Override
	public List<Object[]> getStudies() {
		return formService.getStudyList(userService.getLoggedInUser());
	}

	@Override
	public List<String> getStudyForms(int studyId) {
		return formService.getFormsDefaultVersionXml(userService
				.getLoggedInUser(), studyId, locale);
	}

	@Override
	public String getStudyName(int id) {
		return studyManagerService.getStudyName(id);
	}

	@Override
	public List<Object[]> getUsers() {
		List<User> users = userService.getUsers();
		List<Object[]> result = new ArrayList<Object[]>();
		for (User user : users) {
			Integer userId = user.getId();
			String name = user.getName();
			String encodedPassword = user.getPassword();
			String salt = user.getSalt();
			Object[] completeUser = { userId, name, encodedPassword, salt };
			result.add(completeUser);
		}
		return result;
	}

	@Override
	public Map<Integer, String> getXForms() {
		return formService.getFormsVersionXmlMap();
	}

	@Override
	public void setUploadResult(List<String> formInstances) {
		if (formInstances != null)
			for (String instance : formInstances)
				formService.saveFormData(instance, userService
						.getLoggedInUser(), new Date());
	}


	@Override
	public String setUploadResult(String formInstance) {
		FormData formData = formService.saveFormData(formInstance, userService.getLoggedInUser(), new Date());
		return String.valueOf(formData.getId());
	}

	@Override
	public String setUploadResult(Integer formInstanceId, String formInstance) {
		if (formInstanceId != null) {
			FormData formData = formService.updateFormData(formInstanceId, formInstance, userService.getLoggedInUser(), new Date());
			return String.valueOf(formData.getId());
		} else {
			return setUploadResult(formInstance);
		}
		
	}
	
	@Override
	public String getStudyKey(int studyId) {
		return studyManagerService.getStudyKey(studyId);
	}

	@Override
	public String getFormInstance(int formDefVersionId, int formDataId) throws ProtocolInvalidSessionReferenceException, ProtocolAccessDeniedException {
		FormData formData = formService.getFormData(userService.getLoggedInUser(), formDefVersionId, formDataId);
		return formData.getData();
	}

	@Override
	public String getXForm(int formDefVersionId) {
		FormDefVersion formVersion = formService.getFormVersion(formDefVersionId);
		return formVersion.getXform();
	}
}
