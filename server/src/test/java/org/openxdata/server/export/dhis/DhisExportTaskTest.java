package org.openxdata.server.export.dhis;

import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.TaskDef;
import org.openxdata.server.admin.model.TaskParam;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.export.ExportConstants;
import org.openxdata.server.export.http.HttpPostExportTask;
import org.openxdata.server.service.DataExportService;
import org.openxdata.test.BaseHttpTest;

@RunWith(MockitoJUnitRunner.class)
public class DhisExportTaskTest extends BaseHttpTest {

	private final class HttpRequestHandlerImplementation implements
			HttpRequestHandler {
		
		private final String expectedData;

		public HttpRequestHandlerImplementation(String expectedData) {
			this.expectedData = expectedData;
		}
		
		@Override
		public void handle(HttpRequest request, HttpResponse response, HttpContext context)
				throws HttpException, IOException {
			
			BasicHttpEntity entity = (BasicHttpEntity) ((HttpEntityEnclosingRequest) request).getEntity();
			String actualData = EntityUtils.toString(entity);
			Assert.assertEquals(expectedData.trim(), actualData.trim());
			response.setStatusCode(HttpStatus.SC_OK);
		}
	}

	@Mock
	DataExportService dataExportService;
	
	protected void registerHandler(String expectedData) {
		HttpRequestHandlerImplementation handler = new HttpRequestHandlerImplementation(expectedData);
		getLocalServer().register("*", handler);
	}
	
	@Test
	public void testExportWithBasicAuth() throws FileNotFoundException, IOException{
		String expectedData = IOUtils.toString(this.getClass().getResourceAsStream("expectedPostData.xml"));
		registerHandler(expectedData);
		TaskDef taskDef = getTaskDef();

		String formData = IOUtils.toString(this.getClass().getResourceAsStream("testFormData.xml"));
		FormData data = new FormData();
		data.setData(formData);
		User user = new User();
		user.setPhoneNo("2");
		data.setCreator(user);
		
		getExportTask(taskDef, data);
		
		Mockito.verify(dataExportService).setFormDataExported(data, ExportConstants.EXPORT_BIT_HTTP_POST);
	}
	
	private void getExportTask(TaskDef taskDef, FormData data) {
		DhisExportTask task = new DhisExportTask(taskDef);
		task.setDataExportService(dataExportService);
		task.exportFormData(data, null);
	}

	private TaskDef getTaskDef() {
		TaskDef taskDef = new TaskDef();
		addParam(taskDef, HttpPostExportTask.PARAM_POST_URL, getServerUrl());
		return taskDef;
	}

	private void addParam(TaskDef taskDef, String name, String value) {
		TaskParam param = new TaskParam();
		param.setName(name);
		param.setValue(value);
		taskDef.addParam(param);
	}
}