package org.openxdata.server.export;

import org.junit.Assert;
import org.junit.Test;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.service.DataExportService;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Tests exporting of studies to xml.
 * 
 * @author daniel
 *
 */
public class StudyExportTest extends BaseContextSensitiveTest {
	
	@Autowired
	protected DataExportService dataExportService;
	
	@Test(expected=IllegalArgumentException.class)
	public void exportShouldThrowExeceptionWhenStudyIsNull() {
		StudyExport.export(null);
	}

	@Test
	public void export_shouldReturnValidExportXmlForStudyWithNoForms() throws Exception {

		String xml = StudyExport.export(new StudyDef(1,"Study"));
		
		Assert.assertNotNull(xml);
		Assert.assertTrue(xml.trim().length() > 50);
	}

	@Test
	public void export_shouldReturnValidExportXml() throws Exception {

		StudyDef studyDef = dataExportService.getStudyDef(1);
		String xml = StudyExport.export(studyDef);
		
		Assert.assertNotNull(xml);
		Assert.assertTrue(xml.trim().length() > 1000);
	}
}
