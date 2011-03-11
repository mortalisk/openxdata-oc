/*
 *  Licensed to the OpenXdata Foundation (OXDF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The OXDF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, 
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 *  Copyright 2010 http://www.openxdata.org.
 */
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
