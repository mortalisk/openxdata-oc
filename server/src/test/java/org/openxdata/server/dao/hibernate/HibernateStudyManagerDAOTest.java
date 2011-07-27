package org.openxdata.server.dao.hibernate;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.service.StudyManagerService;
import org.openxdata.server.service.UserService;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test class for the HibernateStudyManagerDAO
 * 
 *
 */
public class HibernateStudyManagerDAOTest extends BaseContextSensitiveTest {
	
	@Autowired
	protected UserService userService;
	
	@Autowired
	private StudyManagerService studyManagerService;
	
	@Test
	public void testGetFormsForUser_admin() throws Exception {
		User user = userService.findUserByUsername("admin");
		List<FormDef> forms = studyManagerService.getFormsForUser(user);
		Assert.assertNotNull(forms);
		Assert.assertEquals("There are 5 forms", 5, forms.size());
	}
	
	@Test
	public void testGetStudy() throws Exception {
		StudyDef study = studyManagerService.getStudy(1);
		Assert.assertNotNull(study);
		Assert.assertEquals("Sample Study", study.getName());
	}
	
	@Test
	public void testGetForm() throws Exception {
		
	}
	
	@Test
	public void testGetFormsForUser() throws Exception {
		User user = userService.findUserByUsername("user");
		List<FormDef> forms = studyManagerService.getFormsForUser(user);
		Assert.assertNotNull(forms);
		Assert.assertEquals("user has 3 forms", 3, forms.size());
		Assert.assertEquals("Sample Form", forms.get(0).getName());
		Assert.assertEquals("Another Sample Form", forms.get(1).getName());
		Assert.assertEquals("Sample Form2", forms.get(2).getName());
		
		User user3 = userService.findUserByUsername("user3");
		forms = studyManagerService.getFormsForUser(user3);
		Assert.assertNotNull(forms);
		Assert.assertEquals("user3 has 4 forms", 4, forms.size());
	}

	@Test
	public void testGetStudyFormsForUser_admin() throws Exception {
		User user = userService.findUserByUsername("user");
		List<FormDef> forms = studyManagerService.getFormsForUser(user, 1);
		Assert.assertNotNull(forms);
		Assert.assertEquals("There are 2 forms in study 1", 2, forms.size());
	}
	
	@Test
	public void testGetStudyFormsForUser() throws Exception {
		User user = userService.findUserByUsername("user");
		List<FormDef> forms = studyManagerService.getFormsForUser(user, 2);
		Assert.assertNotNull(forms);
		Assert.assertEquals("There is 1 form for user in study 2", 1, forms.size());
	}
	
	@Test
	public void testGetStudyKey() throws Exception {
		String studyKey = studyManagerService.getStudyKey(1);
		Assert.assertEquals("study key correct", "sample", studyKey);
		
		studyKey = studyManagerService.getStudyKey(111);
		Assert.assertEquals("study key unknown", "", studyKey);
	}
	
	@Test
	public void testGetStudyName() throws Exception {
		String studyName = studyManagerService.getStudyName(1);
		Assert.assertEquals("study name correct", "Sample Study", studyName);
		
		studyName = studyManagerService.getStudyName(111);
		Assert.assertEquals("study name unknown", "UNKNOWN STUDY", studyName);
	}
}
