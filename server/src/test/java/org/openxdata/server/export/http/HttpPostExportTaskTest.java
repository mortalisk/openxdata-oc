package org.openxdata.server.export.http;

import static org.mockito.Mockito.*;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.localserver.LocalTestServer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
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

	private static final String TEST_DATA = "<some>test data</some>";
	@Mock
	DataExportService dataExportService;
	
	@Override
	protected void registerHandlers(LocalTestServer localServer) {
		localServer.register("*", new HttpRequestHandler() {
			@Override
			public void handle(HttpRequest request, HttpResponse response, HttpContext context)
					throws HttpException, IOException {
				BasicHttpEntity entity = (BasicHttpEntity) ((HttpEntityEnclosingRequest) request).getEntity();
				String postData = EntityUtils.toString(entity);
				if (!postData.contains(TEST_DATA)){
					response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
				}
			}
		});
	}
	
	@Test
	public void testHttpExportTask(){
		TaskDef taskDef = new TaskDef();
		addParam(taskDef, HttpPostExportTask.PARAM_POST_URL, getServerUrl());
		addParam(taskDef, HttpPostExportTask.PARAM_POST_FORMAT, "text");
		addParam(taskDef, HttpPostExportTask.PARAM_DATA_PARAM_NAME, "data");
		addParam(taskDef, HttpPostExportTask.PARAM_AUTHENTICATION_PROVIDER, "basic");
		addParam(taskDef, BasicDigestAuthenticationProvider.PARAM_USERNAME, "username");
		addParam(taskDef, BasicDigestAuthenticationProvider.PARAM_PASSWORD, "password");
		
		HttpPostExportTask task = new HttpPostExportTask(taskDef);
		task.setDataExportService(dataExportService);
		FormData data = new FormData();
		data.setData(TEST_DATA);
		task.exportFormData(data, null);
		
		verify(dataExportService).setFormDataExported(eq(data), eq(ExportConstants.EXPORT_BIT_HTTP_POST));
	}

	private void addParam(TaskDef taskDef, String name, String value) {
		TaskParam param = new TaskParam();
		param.setName(name);
		param.setValue(value);
		taskDef.addParam(param);
	}
}