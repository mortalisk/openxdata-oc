package org.openxdata.server.export;

import org.junit.Assert;
import org.junit.Test;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.service.DataExportService;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Tests exporting of forms to xml.
 * 
 * @author daniel
 *
 */
public class FormExportTest extends BaseContextSensitiveTest {
	
	@Autowired
	protected DataExportService dataExportService;

	@Test(expected=IllegalArgumentException.class)
	public void exportShouldThrowExceptionWhenFormIsNull() {
		FormExport.export(null);
	}

	@Test
	public void export_shouldReturnValidExportXmlForFormWithNoVersions() throws Exception {

		String xml = FormExport.export(new FormDef(1,"Form",(StudyDef)null));
		
		Assert.assertNotNull(xml);
		Assert.assertTrue(xml.trim().length() > 50);
	}

	@Test
	public void export_shouldReturnValidExportXml() throws Exception {

		FormDef formDef = dataExportService.getFormDef(1);
		String xml = FormExport.export(formDef);
		
		Assert.assertNotNull(xml);
		Assert.assertTrue(xml.trim().length() > 1000);
	}
}
