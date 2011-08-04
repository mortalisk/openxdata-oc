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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.openxdata.server.Task;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.TaskDef;
import org.openxdata.server.export.AbstractExportTask;
import org.openxdata.server.export.DataExportUtil;
import org.openxdata.server.export.ExportConstants;
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
 * <td>postFormat
 * <td>The format to post the data in. Options are 'file' or 'text'.
 * <td align="center">
 * <td>file
 * <tr>
 * <td>dataParameterName
 * <td>This task parameter is used as the name for the post parameter that will contain the form data.
 * <td align="center">
 * <td>upload
 * <tr bgcolor="#eeeeff">
 * <td>authenticationProvider
 * <td>The name of the authentication provider to use. Options are:
 * 		<ul>
 * 		<li><i>form</i> - see {@link FormAuthenticationProvider} for details
 * 		<li><i>basic</i>, <i>digest</i> - see {@link BasicDigestAuthenticationProvider} for details
 * 		</ul> 
 * <td align="center">
 * <td>basic
 * <tr>
 * <td>customPostParameter
 * <td>Any custom post parameters to be included in the post may be defined as task parameters with this name. 
 * The task parameter value must be in the format 'name=value'. Each post parameter must be a separate task parameter.
 * <br>
 * e.g. 
 * 		<table border=1>
 * 		<tr>
 * 		<td>customPostParameter
 * 		<td>source=openxdata
 * 		<tr>
 * 		<td>customPostParameter
 * 		<td>incomingRecords=new_and_updates
 * 		</table>
 * 		<td align="center">
 * 		<td>
 * 		</table>
 * 
 * @author simon@cell-life.org
 */
@Component("httpPostExportTask")
public class HttpPostExportTask extends AbstractExportTask implements Task {

	private static final Logger log = Logger.getLogger(HttpPostExportTask.class);

	public static final String PARAM_POST_URL = "postUrl";
	public static final String PARAM_POST_FORMAT = "postFormat";
	public static final String PARAM_DATA_PARAM_NAME = "dataParameterName";
	public static final String PARAM_CUSTOM_POST_PARAMS = "customPostParameter";
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
			authenticate(client);
			Integer status = postData(formData.getData(), client);
			
			if (status == HttpStatus.SC_OK){
				dataExportService.setFormDataExported(formData, ExportConstants.EXPORT_BIT_HTTP_POST);
			}
		} catch (Exception e) {
			log.error("Failed to export data via HTTP post", e);
		} finally {
			try { client.getConnectionManager().shutdown(); } catch (Exception ignore) {}
		}
	}

	private void authenticate(DefaultHttpClient client) throws Exception {
		String authProviderClass = DataExportUtil.getParameter(getTaskDef(), PARAM_AUTHENTICATION_PROVIDER, "basic");
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

		MultipartEntity requestEntity = new MultipartEntity();
		addDataPart(data, requestEntity);
		addCustomParameterParts(requestEntity);

		
		if (log.isTraceEnabled()){
			log.trace("========== Export post request start ==========");
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			requestEntity.writeTo(stream);
			log.trace("\n" + new String(stream.toByteArray()) + "\n");
			log.trace("========== Export post request end  ==========");
		}
		
		method.setEntity(requestEntity);

		HttpResponse response = client.execute(method);
		
		if (log.isTraceEnabled()){
			log.trace(EntityUtils.toString(response.getEntity()));
		}
		
		return response.getStatusLine().getStatusCode();
	}

	private void addDataPart(String data, MultipartEntity requestEntity) throws IOException, FileNotFoundException {
		String postFormat = DataExportUtil.getParameter(getTaskDef(), PARAM_POST_FORMAT, "file");
		String paramName = DataExportUtil.getParameter(getTaskDef(), PARAM_DATA_PARAM_NAME, "upload");
		ContentBody dataPart = null;
		if (postFormat.equalsIgnoreCase("file")){
			File file = writeFormDataToFile(data);
			dataPart = new FileBody(file, "text/xml");
		} else {
			dataPart = new StringBody(data);
		}
		requestEntity.addPart(paramName, dataPart);
	}

	private void addCustomParameterParts(MultipartEntity requestEntity) {
		List<String> customParameters = DataExportUtil.getMultiParamValues(getTaskDef(), PARAM_CUSTOM_POST_PARAMS);
		for (int i = 0; i < customParameters.size(); i++) {
			String param = customParameters.get(i);
			String[] splitParam = param.split("=", 2);
			if (splitParam.length == 2) {
				try {
					requestEntity.addPart(splitParam[0], new StringBody(splitParam[1]));
				} catch (UnsupportedEncodingException e) {
					log.error("Unable to add custom export parameter: " + splitParam[0], e);
				}
			}
		}
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