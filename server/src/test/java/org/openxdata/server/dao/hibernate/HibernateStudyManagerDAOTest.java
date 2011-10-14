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
	public void testGetStudy() throws Exception {
		StudyDef study = studyManagerService.getStudy(1);
		Assert.assertNotNull(study);
		Assert.assertEquals("Sample Study", study.getName());
	}
	
	@Test
	public void testGetForm() throws Exception {
		
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
