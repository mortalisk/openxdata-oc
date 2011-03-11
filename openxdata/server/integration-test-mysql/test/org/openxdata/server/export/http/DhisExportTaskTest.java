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
package org.openxdata.server.export.http;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URL;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.TaskDef;
import org.openxdata.server.admin.model.TaskParam;
import org.openxdata.server.export.ExportConstants;
import org.openxdata.server.service.DataExportService;

/**
 * A class for testing DHIS integration. Requires an configured DHIS2 installation.
 * 
 * @author simon@cell-life.org
 */
public class DhisExportTaskTest {

	private static final String DATA = "<this_is>test form data</this_is>";
	private static final String TEST_FORMDATA_XML = "dhis_test_formdata.xml";

	@Test
	public void testAuthentication() throws Exception {
		DhisExportTask task = new DhisExportTask(mockTaskDef());
		HttpClient client = new HttpClient();
		task.authenticate(client);
		
		HttpState state = client.getState();
		boolean authenticated = false;
        for(Cookie c : state.getCookies()) {
            if ("JSESSIONID".equals(c.getName())){
            	authenticated = true;            	
            }
        }
		Assert.assertTrue(authenticated);
	}
	
	@Test
	public void testWriteToFile() throws Exception{
		DhisExportTask task = new DhisExportTask(mockTaskDef());
		
		File dataFile = task.writeFormDataToFile(mockFormData());
		String stringFromFile = FileUtils.readFileToString(dataFile);
		Assert.assertEquals(DATA, stringFromFile);
	}
	
	@Test
	public void testPost() throws Exception{
		DhisExportTask task = new DhisExportTask(mockTaskDef());
		HttpClient client = new HttpClient();
		task.authenticate(client);
		
		URL resource = ClassLoader.getSystemResource(TEST_FORMDATA_XML);
		File file = new File(resource.toURI());
		Integer status = task.postData(client, file);
		
		Assert.assertEquals(Integer.valueOf(HttpStatus.SC_OK), status);
	}
	
	@Test
	public void testExportFormData() throws Exception{
		testExportFormDataInternal(false);
	}
	
	@Test
	public void testExportFormDataThreaded() throws Exception{
		testExportFormDataInternal(true);
	}
	
	private void testExportFormDataInternal(boolean threaded) throws Exception{
		DataExportService service = mock(DataExportService.class);
		
		DhisExportTask task = new DhisExportTask(mockTaskDef());
		task.setDataExportService(service);
		
		HttpClient client = new HttpClient();
		task.authenticate(client);
		
		FormData mockFormData = mockFormData();
		task.exportFormData(mockFormData, threaded);
		if (threaded){
			// give the thread a chance to complete
			Thread.sleep(1000);
		}
		verify(service).setFormDataExported(mockFormData, ExportConstants.EXPORT_BIT_DHIS);
	}
	
	private FormData mockFormData() {
		FormData d = new FormData(0, DATA, null, null, null);
		return d;
	}

	private TaskDef mockTaskDef(){
		TaskDef taskDef = new TaskDef ("DHIS data export task");
		taskDef.addParam(new TaskParam(taskDef, DhisExportTask.PARAM_URL, "http://dev.cell-life.org/dhis2"));
		taskDef.addParam(new TaskParam(taskDef, DhisExportTask.PARAM_USERNAME, "admin"));
		taskDef.addParam(new TaskParam(taskDef, DhisExportTask.PARAM_PASSWORD, "district"));
		return taskDef;
	}
	
}
