package org.openxdata.server.export.http;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.TaskDef;
import org.openxdata.server.admin.model.TaskParam;
import org.openxdata.server.export.ExportConstants;
import org.openxdata.server.service.DataExportService;
import org.openxdata.test.BaseHttpTest;

@RunWith(MockitoJUnitRunner.class)
public class HttpPostExportTaskTest extends BaseHttpTest {

	private final class HttpRequestHandlerImplementation implements
			HttpRequestHandler {

		private final String expectedData;
		private String authToken;

		public HttpRequestHandlerImplementation(String expectedData) {
			this.expectedData = expectedData;
		}

		public void setAuthToken(String authToken) {
			this.authToken = authToken;
		}

		@Override
		public void handle(HttpRequest request, HttpResponse response,
				HttpContext context) throws HttpException, IOException {

			String creds = (String) context.getAttribute("creds");
			if (authToken != null
					&& (creds == null || !creds.equals(authToken))) {
				response.setStatusCode(HttpStatus.SC_UNAUTHORIZED);
				return;
			}

			BasicHttpEntity entity = (BasicHttpEntity) ((HttpEntityEnclosingRequest) request)
					.getEntity();
			Assert.assertEquals(expectedData, EntityUtils.toString(entity));
			response.setStatusCode(HttpStatus.SC_OK);
		}
	}
	
	@Mock
	DataExportService dataExportService;
	
	protected void registerHandler(String expectedData, boolean withAuth) {
		HttpRequestHandlerImplementation handler = new HttpRequestHandlerImplementation(
				expectedData);
		if (withAuth) {
			handler.setAuthToken("username:password");
		}
		getLocalServer().register("*", handler);
	}
	
	@Test
	public void testExportWithBasicAuth(){
		String expectedData = "test data";
		registerHandler(expectedData, true);
		TaskDef taskDef = getTaskDef(true);

		FormData data = new FormData();
		data.setData(expectedData);
		
		getExportTask(taskDef, data);
 		
		verify(dataExportService).setFormDataExported(data, ExportConstants.EXPORT_BIT_HTTP_POST);
	}
	
	@Test
	public void testExportWithNoAuth(){
		String expectedData = "test data";
		registerHandler(expectedData, false);
		TaskDef taskDef = getTaskDef(false);

		FormData data = new FormData();
		data.setData(expectedData);
		
		getExportTask(taskDef, data);
		
		verify(dataExportService).setFormDataExported(data, ExportConstants.EXPORT_BIT_HTTP_POST);
	}
	
	@Test
	public void testExportWithNoAuth_fail(){
		registerHandler("", true);
		TaskDef taskDef = getTaskDef(false);

		FormData data = new FormData();
		data.setData("");
		
		getExportTask(taskDef, data);
		
		verify(dataExportService, never()).setFormDataExported(data, ExportConstants.EXPORT_BIT_HTTP_POST);
	}
	
	private void getExportTask(TaskDef taskDef, FormData data) {
 		HttpPostExportTask task = new HttpPostExportTask(taskDef);
 		task.setDataExportService(dataExportService);
 		task.exportFormData(data, null);
 	}
	
	private TaskDef getTaskDef(boolean withAuth) {
		TaskDef taskDef = new TaskDef();
		addParam(taskDef, HttpPostExportTask.PARAM_POST_URL, getServerUrl());
		if (withAuth) {
			addParam(taskDef, HttpPostExportTask.PARAM_AUTHENTICATION_PROVIDER,
					"basic");
			addParam(taskDef, BasicDigestAuthenticationProvider.PARAM_USERNAME,
					"username");
			addParam(taskDef, BasicDigestAuthenticationProvider.PARAM_PASSWORD,
					"password");
		}
		return taskDef;
	}

	private void addParam(TaskDef taskDef, String name, String value) {
		TaskParam param = new TaskParam();
		param.setName(name);
		param.setValue(value);
		taskDef.addParam(param);
	}
}