package org.openxdata.server.dao.hibernate;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openxdata.server.admin.model.StudyDef;
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
		assertNotNull(study);
		assertEquals("Sample Study", study.getName());
	}
	
	@Test
	public void testGetStudyKey() throws Exception {
		String studyKey = studyManagerService.getStudyKey(1);
		assertEquals("study key correct", "sample", studyKey);
		
		studyKey = studyManagerService.getStudyKey(111);
		assertEquals("study key unknown", "", studyKey);
	}
	
	@Test
	public void testGetStudyName() throws Exception {
		String studyName = studyManagerService.getStudyName(1);
		assertEquals("study name correct", "Sample Study", studyName);
		
		studyName = studyManagerService.getStudyName(111);
		assertEquals("study name unknown", "UNKNOWN STUDY", studyName);
	}
	
	@Test public void testGetStudyNameWithStudyKey(){
		StudyDef study = studyManagerService.getStudy("sample");
		assertNotNull(study);
		assertEquals("Sample Study", study.getName());
	}
}
