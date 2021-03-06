package org.openxdata.server.export;

import org.junit.Assert;
import org.junit.Test;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.FormDefVersionText;
import org.openxdata.server.service.DataExportService;
import org.openxdata.server.util.XmlUtil;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Tests exporting of form version text to xml.
 * 
 * @author daniel
 *
 */
public class VersionTextExportTest extends BaseContextSensitiveTest {

	@Autowired
	protected DataExportService dataExportService;

	@Test
	public void export_shouldReturnNullForNullVersionTextAndParentNode() throws Exception {
		Assert.assertNull(VersionTextExport.export(null,null));
	}

	@Test
	public void export_shouldReturnNullForNullParentNode() throws Exception {
		Assert.assertNull(VersionTextExport.export(new FormDefVersionText(),null));
	}

	@Test
	public void export_shouldReturnNullForNullVersionText() throws Exception {
		Assert.assertNull(VersionTextExport.export(null,getVersionTextParent()));
	}

	@Test
	public void export_shouldReturnValidExportXmlForVersionTextWithNoXformsText() throws Exception {

		String xml = VersionTextExport.export(new FormDefVersionText("en",null,""),getVersionTextParent());
		
		Assert.assertNotNull(xml);
		Assert.assertTrue(xml.trim().length() > 50);
	}

	@Test
	public void export_shouldReturnValidExportXmlForVersionTextWithNoText() throws Exception {

		String xml = VersionTextExport.export(new FormDefVersionText("en","",null),getVersionTextParent());
		
		Assert.assertNotNull(xml);
		Assert.assertTrue(xml.trim().length() > 50);
	}

	@Test
	public void export_shouldReturnValidExportXml() throws Exception {

		FormDefVersion version = dataExportService.getFormDefVersion(1);
		assert(version.getVersionText().size() == 1);
		String xml = VersionTextExport.export((FormDefVersionText)version.getVersionText().get(0),getVersionTextParent());
		
		Assert.assertNotNull(xml);
		Assert.assertTrue(xml.trim().length() > 1000);
	}
	
	private Element getVersionTextParent() {
		Document doc = XmlUtil.createNewXmlDocument();
		doc.appendChild(doc.createElement("version"));
		return doc.getDocumentElement();
	}
}
