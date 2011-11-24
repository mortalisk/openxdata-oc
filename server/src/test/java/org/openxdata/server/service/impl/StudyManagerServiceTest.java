package org.openxdata.server.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDataVersion;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefHeader;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.StudyDefHeader;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;
import org.openxdata.server.dao.UserFormMapDAO;
import org.openxdata.server.dao.UserStudyMapDAO;
import org.openxdata.server.service.FormService;
import org.openxdata.server.service.StudyManagerService;
import org.openxdata.server.service.UserService;
import org.openxdata.test.BaseContextSensitiveTest;
import org.openxdata.test.XFormsFixture;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests methods in the StudyManagerService which deal with StudyDef.
 * 
 * @author daniel
 * 
 */
public class StudyManagerServiceTest extends BaseContextSensitiveTest {
	
	@Autowired
	protected FormService formService;
	
	@Autowired
	protected StudyManagerService studyManagerService;
	
	@Autowired	
	protected UserService userService;
	
	@Autowired
	protected UserFormMapDAO userFormMapDAO;
	
	@Autowired
	protected UserStudyMapDAO userStudyMapDAO;

    @Test
	public void getStudies_shouldReturnAllStudies() throws Exception {
		List<StudyDef> studies = studyManagerService.getStudies();
		
		Assert.assertNotNull(studies);
		Assert.assertEquals("There are 4 studies", 4, studies.size());
		Assert.assertNotNull(getStudy("Sample Study", studies));
	}
		
	@Test
	public void saveStudy_shouldSaveStudy() throws Exception {
		final String studyName = "StudyName";
		
		List<StudyDef> studies = studyManagerService.getStudies();
		Assert.assertEquals("There are 4 studies", 4, studies.size());
		Assert.assertNull(getStudy(studyName,studies));
		
		StudyDef study = new StudyDef();
		study.setName(studyName);
		study.setCreator(userService.getUsers().get(0));
		study.setDateCreated(new Date());
		
		studyManagerService.saveStudy(study);
		
		studies = studyManagerService.getStudies();
		Assert.assertEquals("Added 1 study, now there are 5", 5, studies.size());
		Assert.assertNotNull(getStudy(studyName,studies));
	}
	
	@Test
	public void saveFormData_editExistingData() throws Exception {
		PagingLoadResult<FormDef> formsLoadResult = formService.getForms(new PagingLoadConfig(0,20));
		List<FormDef> forms = formsLoadResult.getData();
		int formDefVersionId = forms.get(0).getVersions().get(0).getId();
		int dataCount = formService.getFormResponseCount(formDefVersionId);
		
		// create some form data
		FormData formData = new FormData();
		formData.setData("testing");
		formData.setFormDefVersionId(formDefVersionId);
		formData.setCreator(userService.getUsers().get(0));
		formData.setDateCreated(new Date());
		formService.saveFormData(formData);
		
		// check if the form data was created
		Assert.assertNotNull("FormData Id is set", formData.getId());
		int dataCount2 = formService.getFormResponseCount(formDefVersionId);
		Assert.assertEquals("One extra FormData", dataCount+1, dataCount2);
		
		// try edit the form data and save
		formData.setData("testing updated");
		formData.setChangedBy(formData.getCreator());
		formData.setDateChanged(new Date());
		formService.saveFormData(formData);
		
		// check if the form data was updated
		int dataCount3 = formService.getFormResponseCount(formDefVersionId);
		Assert.assertEquals("No extra FormData", dataCount2, dataCount3);
		FormData savedFormData = formService.getFormData(new Integer(formData.getId()));
		Assert.assertEquals("Data text is updated", "testing updated", savedFormData.getData());
		
		// check if the version was correctly saved
		List<FormDataVersion> versions = formService.getFormDataVersion(formData.getId());
		Assert.assertEquals("Only 1 previous version", 1, versions.size());
		Assert.assertEquals("Previous version data text is correct", "testing", versions.get(0).getData());
	}

	@Test
	public void deleteStudy_shouldDeleteGivenStudy() throws Exception {
		
		final String studyName = "Study Name";
		
		List<StudyDef> studies = studyManagerService.getStudies();
		Assert.assertEquals("There are 4 studies", 4, studies.size());
		Assert.assertNull(getStudy(studyName,studies));
	
		StudyDef study = new StudyDef();
		study.setName(studyName);
		study.setCreator(userService.getUsers().get(0));
		study.setDateCreated(new Date());
		
		studyManagerService.saveStudy(study);
		studies = studyManagerService.getStudies();
		Assert.assertEquals("Added 1 study, now there are 5", 5, studies.size());
		
		study = getStudy(studyName,studies);
		Assert.assertNotNull(study);

		studyManagerService.deleteStudy(study);
		
		studies = studyManagerService.getStudies();
		Assert.assertEquals("Deleted the study so there are 4 studies again", 4, studies.size());
		Assert.assertNull(getStudy(studyName,studies));
	}
	
	@Test
	public void deleteStudy_shouldDeleteUserMappings() throws Exception {
		
		final String studyName = "Study Name";

		// create study
		StudyDef study = new StudyDef();
		study.setName(studyName);
		study.setCreator(userService.getUsers().get(0));
		study.setDateCreated(new Date());
		
		studyManagerService.saveStudy(study);
		List<StudyDef> studies = studyManagerService.getStudyByName(studyName);
		Assert.assertEquals("New study created", 1, studies.size());
		
		// create user
		User user = new User("study delete user");
		user.setCreator(userService.getLoggedInUser());
		userService.saveUser(user);
		
		// create user study mapping
		List<StudyDefHeader> studiesToAdd = new ArrayList<StudyDefHeader>();
		StudyDefHeader studyHeader = new StudyDefHeader(study.getId(),
				study.getName());
		studiesToAdd.add(studyHeader);
		studyManagerService.saveMappedUserStudyNames(user.getId(),
				studiesToAdd, null);

		PagingLoadResult<User> mappedUsers = studyManagerService.getMappedUsers(study.getId(), new PagingLoadConfig(0,100));
		Assert.assertEquals("Study has 1 mapped user", 1, mappedUsers.getData().size());
		
		// create form
		FormDef form = new FormDef();
        form.setName("deletStudy-testform");
        form.setCreator(user);
        form.setDateCreated(new Date());
        form.setStudy(study);
        study.addForm(form);
        // create form version
        FormDefVersion formV = new FormDefVersion();
        formV.setName("deletStudy-testformversion");
        formV.setCreator(user);
        formV.setDateCreated(new Date());
        formV.setXform(XFormsFixture.getSampleForm());
        formV.setIsDefault(true);
        formV.setFormDef(form);
        form.addVersion(formV);
        formService.saveForm(form);
        
		// create user form mapping
		List<FormDefHeader> formsToAdd = new ArrayList<FormDefHeader>();
		FormDefHeader formHeader = new FormDefHeader(form.getId(),
				form.getName());
		formsToAdd.add(formHeader);
		formService.saveMappedUserFormNames(user.getId(), formsToAdd, null);

		List<UserFormMap> userMappedForms = userFormMapDAO.getUserMappedForms(form.getId());
		Assert.assertEquals("Form has 1 mapped user", 1, userMappedForms.size());
		
		studyManagerService.deleteStudy(study);
		
		studies = studyManagerService.getStudyByName(studyName);
		Assert.assertEquals("Deleted the study", 0, studies.size());
		
		mappedUsers = studyManagerService.getMappedUsers(study.getId(), new PagingLoadConfig(0,100));
		Assert.assertEquals("Study has 1 mapped user", 0, mappedUsers.getData().size());
		
		userMappedForms = userFormMapDAO.getUserMappedForms(form.getId());
		Assert.assertEquals("UserFormMap was deleted", 0, userMappedForms.size());
	}
	
	@Test
	public void deleteForm_shouldDeleteUserMappings() throws Exception {
		
		final String studyName = "Study Name";

		// create study
		StudyDef study = new StudyDef();
		study.setName(studyName);
		study.setCreator(userService.getUsers().get(0));
		study.setDateCreated(new Date());
		
		studyManagerService.saveStudy(study);
		List<StudyDef> studies = studyManagerService.getStudyByName(studyName);
		Assert.assertEquals("New study created", 1, studies.size());
		
		// create user
		User user = new User("study delete user");
		user.setCreator(userService.getLoggedInUser());
		userService.saveUser(user);
		
		// create form
		FormDef form = new FormDef();
        form.setName("deletStudy-testform");
        form.setCreator(user);
        form.setDateCreated(new Date());
        form.setStudy(study);
        // create form version
        FormDefVersion formV = new FormDefVersion();
        formV.setName("deletStudy-testformversion");
        formV.setCreator(user);
        formV.setDateCreated(new Date());
        formV.setXform(XFormsFixture.getSampleForm());
        formV.setIsDefault(true);
        formV.setFormDef(form);
        form.addVersion(formV);
        formService.saveForm(form);
        
        FormDef savedForm = formService.getForm(form.getId());
        Assert.assertNotNull("Created the form", savedForm);
        
		// create user form mapping
		List<FormDefHeader> formsToAdd = new ArrayList<FormDefHeader>();
		FormDefHeader formHeader = new FormDefHeader(form.getId(),
				form.getName());
		formsToAdd.add(formHeader);
		formService.saveMappedUserFormNames(user.getId(), formsToAdd, null);

		List<UserFormMap> userMappedForms = userFormMapDAO.getUserMappedForms(form.getId());
		Assert.assertEquals("Form has 1 mapped user", 1, userMappedForms.size());
		
		formService.deleteForm(form);
		
		FormDef savedForm2 = formService.getForm(form.getId());
		Assert.assertNull("Deleted the form",savedForm2);
		
		userMappedForms = userFormMapDAO.getUserMappedForms(form.getId());
		Assert.assertEquals("UserFormMap was deleted", 0, userMappedForms.size());
	}
	
	private StudyDef getStudy(String name, List<StudyDef> studies){
		for(StudyDef study : studies){
			if(study.getName().equals(name))
				return study;
		}
		
		return null;
	}

	@Test
	public void testSetUserMappingForStudy(){
		StudyDef study = studyManagerService.getStudies().get(0);
		List<User> users = userService.getUsers();
		
		PagingLoadResult<User> mappedUsers = studyManagerService.getMappedUsers(study.getId(), new PagingLoadConfig(0,100));
		int initialCount = mappedUsers.getData().size();
		
		List<User> dummyPermissions = new ArrayList<User>();
		dummyPermissions.add(users.get(0));
		dummyPermissions.add(users.get(1));
		dummyPermissions.add(users.get(2));
		
		studyManagerService.saveMappedStudyUsers(study.getId(), dummyPermissions, null);
		mappedUsers = studyManagerService.getMappedUsers(study.getId(), new PagingLoadConfig(0,100));
		Assert.assertEquals("added user permissions", initialCount+3, mappedUsers.getData().size());
		
		studyManagerService.saveMappedStudyUsers(study.getId(), null, dummyPermissions);
		mappedUsers = studyManagerService.getMappedUsers(study.getId(), new PagingLoadConfig(0,100));
		Assert.assertEquals("deleted user permissions", initialCount, mappedUsers.getData().size());
	}

	@Test
	public void testSetUserMappingForForm(){
		
		FormDef form = studyManagerService.getStudies().get(0).getForms().get(0);
		List<User> users = userService.getUsers();
		
		int initialCount = userFormMapDAO.getUserMappedForms(form.getId()).size();
		
		List<User> dummyPermissions = new ArrayList<User>();
		dummyPermissions.add(users.get(0));
		dummyPermissions.add(users.get(1));
		dummyPermissions.add(users.get(2));
		
		formService.saveMappedFormUsers(form.getId(), dummyPermissions, null);
		Assert.assertEquals("added user permissions", initialCount+3, userFormMapDAO.getUserMappedForms(form.getId()).size());
		
		formService.saveMappedFormUsers(form.getId(), null, dummyPermissions);
		Assert.assertEquals("deleted user permissions", initialCount, userFormMapDAO.getUserMappedForms(form.getId()).size());
	}
	
	@Test
	public void testGetMappedStudies() throws Exception {
		PagingLoadConfig config = new PagingLoadConfig(0,10);
		PagingLoadResult<StudyDef> result = studyManagerService.getMappedStudies(4, config);
		List<StudyDef> data = result.getData();
		Assert.assertEquals(3, data.size());
		Assert.assertEquals("Sample Study", data.get(0).getName());
		Assert.assertEquals("Another Sample Study", data.get(1).getName());
		Assert.assertEquals("Yet Another Sample Study", data.get(2).getName());
	}
	
	@Test
	public void testGetUnMappedStudies() throws Exception {
		PagingLoadConfig config = new PagingLoadConfig(0,10);
		PagingLoadResult<StudyDefHeader> result = studyManagerService.getUnmappedStudyNames(4, config);
		List<StudyDefHeader> data = result.getData();
		Assert.assertEquals(1, data.size());
		Assert.assertEquals("More Sample Study", data.get(0).getName());
	}
	
	@Test
	public void testGetMappedForms() throws Exception {
		PagingLoadConfig config = new PagingLoadConfig(0,10);
		PagingLoadResult<FormDef> result = formService.getMappedForms(3, config);
		List<FormDef> data = result.getData();
		Assert.assertEquals(2, data.size());
		Assert.assertEquals("Sample Form", data.get(0).getName());
		Assert.assertEquals("Another Sample Form", data.get(1).getName());
	}
	
	@Test
	public void testGetUnMappedFormNames() throws Exception {
		PagingLoadConfig config = new PagingLoadConfig(0,10);
		PagingLoadResult<FormDefHeader> result = formService.getUnmappedFormNames(3, config);
		List<FormDefHeader> data = result.getData();
		Assert.assertEquals(3, data.size());
		Assert.assertEquals("Yet Another Sample Form", data.get(0).getName());
		Assert.assertEquals("Sample Form2", data.get(1).getName());
		Assert.assertEquals("Another Sample Form2", data.get(2).getName());
	}
	
	@Test
	public void testGetFormsForAdminUser() throws Exception {
		User adminUser = userService.findUserByUsername("admin");
		PagingLoadResult<FormDef> loadResult = formService.getForms(adminUser, new PagingLoadConfig(0,10));
		Assert.assertEquals(5, loadResult.getTotalLength());
		Assert.assertEquals(5, loadResult.getData().size());
	}
	
	@Test
	public void testGetFormsForUser() throws Exception {
		User user = userService.findUserByUsername("user");
		PagingLoadResult<FormDef> loadResult = formService.getForms(user, new PagingLoadConfig(0,10));
		Assert.assertEquals(3, loadResult.getTotalLength());
		Assert.assertEquals(3, loadResult.getData().size());
	}
	
	@Test
	public void testGetMappedFormNames() throws Exception {
		PagingLoadConfig config = new PagingLoadConfig(0,10);
		PagingLoadResult<FormDefHeader> result = formService.getMappedFormNames(3, config);
		List<FormDefHeader> data = result.getData();
		Assert.assertEquals(2, data.size());
		Assert.assertEquals("Sample Form", data.get(0).getName());
		Assert.assertEquals("Another Sample Form", data.get(1).getName());
	}
}