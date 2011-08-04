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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.openxdata.server.Task;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.TaskDef;
import org.openxdata.server.export.AbstractExportTask;
import org.openxdata.server.export.DataExportUtil;
import org.openxdata.server.export.ExportConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Class represents the task that handles the export of collected data from OpenXdata to an external
 * system via HTTP Post. Supports authentication via basic, digest or web form. Data can be posted as
 * a string part or a file part in a multi-part post.
 * 
 * <table border=0 cellspacing=3 cellpadding=0>
 * <tr bgcolor="#ccccff">
 * <th>Parameter name
 * <th>Description
 * <th>Required
 * <th>Default value
 * <tr>
 * <td>postUrl
 * <td>The url to post the data to.
 * <td align="center">X
 * <td>
 * <tr bgcolor="#eeeeff">
 * <td>authenticationProvider
 * <td>The name of the authentication provider to use (leave empty for no 
 * authentication):
 * 		<ul>
 * 		<li><i>form</i> - see {@link FormAuthenticationProvider} for details
 * 		<li><i>basic</i>, <i>digest</i> - see {@link BasicDigestAuthenticationProvider} for details
 * 		</ul> 
 * <td align="center">
 * <td align="center">
 * </table>
 * 
 * @author simon@cell-life.org
 */
@Component("httpPostExportTask")
public class HttpPostExportTask extends AbstractExportTask implements Task {

	private static final Logger log = LoggerFactory.getLogger(HttpPostExportTask.class);

	public static final String PARAM_POST_URL = "postUrl";
	public static final String PARAM_AUTHENTICATION_PROVIDER = "authenticationProvider";

	private static Map<String,AuthenticationProvider> authProviders;

	public HttpPostExportTask() {
		super();
	}

	public HttpPostExportTask(TaskDef taskDef) {
		super(taskDef);
		init();
	}

	@Override
	public void exportFormData(FormData formData, FormDefVersion formDefVersion) {
		DefaultHttpClient client = new DefaultHttpClient();
		try {
			String postData = getData(formData, formDefVersion);
			authenticate(client);
			Integer status = postData(postData, client);
			
			if (status == HttpStatus.SC_OK){
				dataExportService.setFormDataExported(formData, ExportConstants.EXPORT_BIT_HTTP_POST);
			}else {
				log.error("Unable to export data vi HTTP. Response code = " + status);
			}
		} catch (Exception e) {
			log.error("Failed to export data via HTTP post", e);
		} finally {
			try { client.getConnectionManager().shutdown(); } catch (Exception ignore) {}
		}
	}

	/**
	 * Default implementation of getData returns only the form data.
	 * Override this method to customise the posted data.
	 * 
	 * @param formData
	 * @param formDefVersion
	 * @return String data
	 */
	protected String getData(FormData formData, FormDefVersion formDefVersion) {
		return formData.getData();
	}

	private void authenticate(DefaultHttpClient client) throws Exception {
		String authProviderClass = DataExportUtil.getParameter(getTaskDef(), PARAM_AUTHENTICATION_PROVIDER, "none");
		AuthenticationProvider authProvider = authProviders.get(authProviderClass);
		if (authProvider != null){
			authProvider.authenticate(getTaskDef(), client);
		}
	}

	/**
	 * Posts the data
	 * 
	 * @param formData the formData to post
	 * @param client the HttpClient instance to use which must contain the session cookie
	 *  
	 * @return {@link HttpStatus}
	 * @throws IOException
	 * @throws HttpException
	 */
	Integer postData(String data, DefaultHttpClient client) throws IOException, HttpException {
		String postUrl = DataExportUtil.getParameter(getTaskDef(), PARAM_POST_URL);
		HttpPost method = new HttpPost(postUrl);
		
		HttpEntity entity = new StringEntity(data, /*"text/xml",*/ "UTF-8");

		if (log.isTraceEnabled()){
			log.trace("========== Export post request start ==========");
			log.trace("\n" + data + "\n");
			log.trace("========== Export post request end  ==========");
		}
		
		method.setEntity(entity);
		
		HttpResponse response = client.execute(method);
		
		if (log.isTraceEnabled()){
			log.trace(EntityUtils.toString(response.getEntity()));
		}
		
		return response.getStatusLine().getStatusCode();
	}

	/**
	 * Writes the formData to a temporary file
	 * 
	 * @param formData
	 * @return the temporary file
	 * @throws IOException 
	 */
	File writeFormDataToFile(String data) throws IOException {
		File file = File.createTempFile("openxdata", "httppostdata");
		IOUtils.write(data, new FileOutputStream(file));
		return file;
	}

	@Override
	public void init() {
		if (authProviders == null){
			authProviders = new HashMap<String, AuthenticationProvider>();
			authProviders.put("form", new FormAuthenticationProvider());
			authProviders.put("basic", new BasicDigestAuthenticationProvider());
			authProviders.put("digest", new BasicDigestAuthenticationProvider());
		}
	}
}