package org.openxdata.server.servlet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openxdata.proto.SubmissionContext;
import org.openxdata.server.admin.model.FormData;
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


	public String setUploadResult(String formInstance) {
		FormData formData = formService.saveFormData(formInstance, userService.getLoggedInUser(), new Date());
		return String.valueOf(formData.getId());
	}

	
	@Override
	public String getStudyKey(int studyId) {
		return studyManagerService.getStudyKey(studyId);
	}
}
