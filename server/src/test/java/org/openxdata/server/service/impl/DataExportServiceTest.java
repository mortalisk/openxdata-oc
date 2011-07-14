package org.openxdata.server.service.impl;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openxdata.server.service.DataExportService;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class DataExportServiceTest extends BaseContextSensitiveTest {

	@Autowired
	private DataExportService dataExportService;
	
	@Test
	public void testGetFormDefVersion() {		
		assertNotNull(dataExportService.getFormDefVersion(1));
	}
	
	@Test
	public void testGetFormDef() {		
		assertNotNull(dataExportService.getFormDef(1));
	}
	
	@Test
	public void testGetStudyDef() {		
		assertNotNull(dataExportService.getStudyDef(1));
	}
}
