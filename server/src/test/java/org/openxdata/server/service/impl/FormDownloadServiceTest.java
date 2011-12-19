package org.openxdata.server.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.openxdata.proto.exception.ProtocolAccessDeniedException;
import org.openxdata.proto.exception.ProtocolInvalidSessionReferenceException;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.Role;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.service.FormDownloadService;
import org.openxdata.server.service.FormService;
import org.openxdata.server.service.StudyManagerService;
import org.openxdata.server.service.UserService;
import org.openxdata.test.BaseContextSensitiveTest;
import org.openxdata.test.XFormsFixture;
import org.springframework.beans.factory.annotation.Autowired;

public class FormDownloadServiceTest extends BaseContextSensitiveTest {
	
	@Autowired
	protected FormDownloadService formDownloadService;
	
	@Autowired
	protected StudyManagerService studyManagerService;
	
	@Autowired
	protected FormService formService;
	
	@Autowired
	protected UserService userService;
	
	@Test
	@Ignore("throws too many exceptions")
	public void testSubmitForms_noSerializer() throws Exception {
		
		// create the stream
    	final PipedOutputStream pout = new PipedOutputStream();
    	DataInputStream in = new DataInputStream(new PipedInputStream(pout));
    	Thread thread = new Thread(
		    new Runnable(){
		      @Override
			public void run(){
		      	DataOutput output = new DataOutputStream(pout);
		        try {
		        	output.writeByte(1);
					output.writeUTF(XFormsFixture.getSampleFormModelData());
				} catch (IOException e) {
					e.printStackTrace();
				}
		      }
		    }
		  );
    	thread.start();
    	DataOutputStream out = new DataOutputStream(new ByteArrayOutputStream());
    	
    	// run test
		formDownloadService.submitForms(in, out, null);
		
		// do checks afterwards
		FormData formData = formService.getFormData(new Integer(12));
		Assert.assertNotNull("after submit form data exists", formData);
	}
	
	@Test
	public void testGetStudyList_forUser() throws Exception {
		User user = userService.findUserByUsername("user");
		List<Object[]> studies = formDownloadService.getStudyList(user);
		Assert.assertNotNull("There are studies for ordinary user", studies);
		Assert.assertEquals("There are two studies", 2, studies.size());
		assertStudy(studies.get(0), new Integer(2), "Another Sample Study");
		assertStudy(studies.get(1), new Integer(1), "Sample Study");
	}
	
	@Test
	public void testGetStudyList_forAdmin() throws Exception {
		User user = userService.findUserByUsername("admin");
		List<Object[]> studies = formDownloadService.getStudyList(user);
		Assert.assertNotNull("There are studies for admin user", studies);
		Assert.assertEquals("There are four studies", 4, studies.size());
		assertStudy(studies.get(0), new Integer(2), "Another Sample Study");
		assertStudy(studies.get(1), new Integer(4), "More Sample Study");
		assertStudy(studies.get(2), new Integer(1), "Sample Study");
		assertStudy(studies.get(3), new Integer(3), "Yet Another Sample Study");
	}
	
	@Test
	public void testGetFormList_forUserStudy1() throws Exception {
		User user = userService.findUserByUsername("user");
		
		// user has study permissions for study1, so can see all forms even though they have only 1 form permission
		List<String> forms = formDownloadService.getFormsDefaultVersionXml(user, 1, null);
		Assert.assertNotNull("There are forms for user user", forms);
		Assert.assertEquals("There are two forms under study 1", 2, forms.size());
		Assert.assertTrue("xform is correct", forms.get(0).contains("<xf:instance id=\"patientreg\">"));
	}
	
	@Test
	public void testGetFormList_forUserStudy2() throws Exception {
		User user = userService.findUserByUsername("user");
		// user has only one form permission for study2
		List<String> forms = formDownloadService.getFormsDefaultVersionXml(user, 2, null);
		Assert.assertNotNull("There are forms for user user", forms);
		Assert.assertEquals("There is 1 forms under study 2", 1, forms.size());
	}
	
	@Test
	public void testGetFormList_forAdmin() throws Exception {
		User user = userService.findUserByUsername("admin");
		List<String> forms = formDownloadService.getFormsDefaultVersionXml(user, 1, null);
		Assert.assertNotNull("There are forms for admin user", forms);
		Assert.assertEquals("There are two forms under study 1", 2, forms.size());
		Assert.assertTrue("xform is correct", forms.get(0).contains("<xf:instance id=\"patientreg\">"));
		Assert.assertTrue("xform is correct", forms.get(1).contains("<xf:instance id=\"patientreg\">"));
	}
	
	private void assertStudy(Object[] study, Integer expectedId, String expectedName) {
		Assert.assertEquals("Study has Id "+expectedId, expectedId, study[0]);
		Assert.assertEquals("Study is called "+expectedName, expectedName, study[1]);
	}
	
	@Test
	public void testGetFormData_forAdmin() throws Exception {
		User admin = userService.findUserByUsername("admin");
		User user = userService.findUserByUsername("user");
		FormData formData = new FormData(1, "<data></data>", "admin data", new Date(), user);
		formDownloadService.saveFormData(formData);
		Assert.assertFalse("FormData has an ID", 0 == formData.getId());
		FormData downloadedFormData = formDownloadService.getFormData(admin, 1, formData.getId());
		Assert.assertNotNull("admin can download data submitted by another user", downloadedFormData);
	}
	
	@Test
	public void testGetFormData() throws Exception {
		User admin = userService.findUserByUsername("admin");
		User user = userService.findUserByUsername("user");
		Role testRole = new Role("Test");
		testRole.addPermission(new Permission(Permission.PERM_EDIT_FORM_DATA));
		user.addRole(testRole);
		FormData formData = new FormData(1, "<data></data>", "admin data", new Date(), admin);
		formDownloadService.saveFormData(formData);
		Assert.assertFalse("FormData has an ID", 0 == formData.getId());
		FormData downloadedFormData = formDownloadService.getFormData(user, 1, formData.getId());
		Assert.assertNotNull("user can download data submitted by someone else", downloadedFormData);
	}
	
	@Test
	public void testGetOwnFormData() throws Exception {
		User admin = userService.findUserByUsername("admin");
		User user = userService.findUserByUsername("user");
		Role testRole = new Role("Test");
		testRole.addPermission(new Permission(Permission.PERM_EDIT_MY_FORM_DATA));
		user.addRole(testRole);
		FormData formData = new FormData(1, "<data></data>", "admin data", new Date(), admin);
		formDownloadService.saveFormData(formData);
		Assert.assertFalse("FormData has an ID", 0 == formData.getId());
		try {
			formDownloadService.getFormData(user, 1, formData.getId());
			Assert.fail("User cannot get data that hasn't been submitted by them");
		} catch (ProtocolAccessDeniedException e) {
			// expected
		}
		FormData formData2 = new FormData(1, "<data></data>", "admin data", new Date(), user);
		formDownloadService.saveFormData(formData2);
		FormData downloadedFormData = formDownloadService.getFormData(user, 1, formData2.getId());
		Assert.assertNotNull("user can download data submitted by themselves", downloadedFormData);
	}
	
	@Test
	public void testGetFormData_IncorrectFormVersion() throws Exception {
		User admin = userService.findUserByUsername("admin");
		FormData formData = new FormData(1, "<data></data>", "admin data", new Date(), admin);
		formDownloadService.saveFormData(formData);
		Assert.assertFalse("FormData has an ID", 0 == formData.getId());
		try {
			formDownloadService.getFormData(admin, 2, formData.getId());
			Assert.fail("Incorrect form version specified");
		} catch (ProtocolInvalidSessionReferenceException e) {
			// expected
		}
	}
	
	@Test(expected=ProtocolInvalidSessionReferenceException.class)
	public void testGetFormData_voidedFormData() throws Exception {
		User admin = userService.findUserByUsername("admin");
		FormData formData = new FormData(1, "<data></data>", "admin data", new Date(), admin);
		formData.setVoided(true);
		formDownloadService.saveFormData(formData);
		Assert.assertFalse("FormData has an ID", 0 == formData.getId());
		formDownloadService.getFormData(admin, 2, formData.getId());
		// expect ProtocolInvalidSessionReferenceException
	}
	
	@Test
	public void testUpdateFormData() throws Exception {
		User user = userService.findUserByUsername("user");
		FormData formData = new FormData(1, "<data></data>", "admin data", new Date(), user);
		formDownloadService.saveFormData(formData);
		Assert.assertFalse("FormData has an ID", 0 == formData.getId());
		formDownloadService.updateFormData(formData.getId(), "<data>UPDATED</data>", user, new Date());
		FormData updatedFormData = formService.getFormData(new Integer(formData.getId()));
		Assert.assertEquals("Form data has been updated", "<data>UPDATED</data>", updatedFormData.getData());
	}
	
}
