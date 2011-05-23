package org.openxdata.server.export;

import org.junit.Assert;
import org.junit.Test;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.service.DataExportService;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Tests exporting of form version to xml.
 * 
 * @author daniel
 *
 */
public class VersionExportTest extends BaseContextSensitiveTest {

	@Autowired
	protected DataExportService dataExportService;
	
	@Test(expected=IllegalArgumentException.class)
	public void exportShouldThrowExceptionWhenVersionIsNull() {
		VersionExport.export(null);
	}

	@Test
	public void export_shouldReturnValidExportXmlForVersionWithNoText() throws Exception {

		String xml = VersionExport.export(new FormDefVersion(1,"v1",null));
		
		Assert.assertNotNull(xml);
		Assert.assertTrue(xml.trim().length() > 50);
	}

	@Test
	public void export_shouldReturnValidExportXmlForVersionWithNoTextAndLayout() throws Exception {

		FormDefVersion version = new FormDefVersion(1,"v1",null);
		version.setXform("");
		String xml = VersionExport.export(version);
		
		Assert.assertNotNull(xml);
		Assert.assertTrue(xml.trim().length() > 50);
	}

	@Test
	public void export_shouldReturnValidExportXml() throws Exception {

		FormDefVersion version = dataExportService.getFormDefVersion(1);
		String xml = VersionExport.export(version);

		Assert.assertNotNull(xml);
		Assert.assertTrue(xml.trim().length() > 1000);
	}
}
