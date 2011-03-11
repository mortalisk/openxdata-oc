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
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.openxdata.server.Task;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.TaskDef;
import org.openxdata.server.export.ExportConstants;
import org.openxdata.server.service.DataExportService;
import org.openxdata.server.service.SchedulerService;
import org.openxdata.server.sms.OpenXDataAbstractJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class represents the task that handles the export of collected data from OpenXdata to DHIS2
 * 
 * In order for this to work the DHIS2 installation must be set up to transform incoming XML requests
 * to the proper DXF format. If DHIS2 is set up correctly you should be able to extract the xml
 * for a formData from the OpenXdata database and manually import it into DHIS2 via the web interface.
 * 
 * @author simon@cell-life.org
 */
@Component("dhisExportTask")
public class DhisExportTask extends OpenXDataAbstractJob implements Task {

	@Autowired
	private DataExportService dataExportService;

	private Logger log = Logger.getLogger(this.getClass());

	public static final String PARAM_URL = "url";
	public static final String PARAM_USERNAME = "username";
	public static final String PARAM_PASSWORD = "password";

	private static final String DHIS_LOGIN_ACTION = "/dhis-web-commons-security/login.action";
	private static final String DHIS_IMPORT_ACTION = "/dhis-web-importexport/import.action";

	private TaskDef taskDef = null;
	
	private String url = "";
	private String username = "";
	private String password = "";

	/** flag to indicate if the task is executing or not */
	private boolean running = false;

	/** thread pool to manage the direct invocation of the task (without quartz) */
	ThreadPoolExecutor tpe = new ThreadPoolExecutor(5, 10, 3600,
			TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

	public DhisExportTask() {
	}

	public DhisExportTask(TaskDef taskDef) {
		this.taskDef = taskDef;
		init(taskDef);
	}

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {

		SchedulerService schedulerService = (SchedulerService) getBean(
				"schedulerService", context);
		dataExportService = (DataExportService) getBean("dataExportService",
				context);

		if (taskDef == null) {
			taskDef = (TaskDef) context.getJobDetail().getJobDataMap().get(
					"taskdef");
			schedulerService.registerTaskRunningInstance(this);
			init(taskDef);
		}

		List<FormData> dataList = dataExportService.getFormDataToExport(ExportConstants.EXPORT_BIT_DHIS);
		log.info("Running Data Export Service to export " + dataList.size()
				+ " form data items");
		running = true;

		for (int index = 0; index < dataList.size(); index++) {
			FormData formData = dataList.get(index);
			exportFormData(formData, false);
		}
	}

	/**
	 * Exports the specified FormData
	 * 
	 * @param formData
	 *            FormData to exports
	 * @param threaded
	 *            boolean true if the export must be run in a thread (only for
	 *            outside of quartz execution)
	 */
	public void exportFormData(FormData formData, boolean threaded) {
		if (formData == null)
			return;

		if (threaded) {
			tpe.execute(new ExportFormDataThread(formData));
		} else {
			exportFormData(formData);
		}
	}

	/**
	 * Runnable class to handle executing the Task outside of Quartz
	 * 
	 */
	class ExportFormDataThread implements Runnable {
		FormData formData;

		public ExportFormDataThread(FormData formData) {
			this.formData = formData;
		}

		@Override
		public void run() {
			exportFormData(formData);
		}
	}

	protected void exportFormData(FormData formData) {
		try {
			File file = writeFormDataToFile(formData);
			HttpClient client = new HttpClient();
			authenticate(client);
			Integer status = postData(client, file);
			
			if (status == HttpStatus.SC_OK){
				dataExportService.setFormDataExported(formData, ExportConstants.EXPORT_BIT_DHIS);
			}
		} catch (Exception e) {
			log.error("Failed to export data to DHIS at URL: " + url, e);
		}
	}

	/**
	 * Posts the data to DHIS
	 * 
	 * @param client the HttpClient instance to use which must contain the session cookie
	 * @param file the file to upload
	 * @return {@link HttpStatus}
	 * @throws IOException
	 * @throws HttpException
	 */
	Integer postData(HttpClient client, File file) throws IOException, HttpException {
		PostMethod method = new PostMethod(url + DHIS_IMPORT_ACTION);

		Part[] parts = { new StringPart("type", "IMPORT"),
				new StringPart("incomingRecords", "NEW_AND_UPDATES"),
				new StringPart("dataValues", "true"),
				new StringPart("skipCheckMatching", "false"),
				new FilePart("upload", file.getName(), file, "text/xml", null) };
		MultipartRequestEntity requestEntity = new MultipartRequestEntity(
				parts, method.getParams());
		
		if (log.isTraceEnabled()){
			System.out.println("========== DHIS post request start ==========");
			requestEntity.writeRequest(System.out);
			System.out.println("==========  DHIS post request end  ==========");
		}
		
		method.setRequestEntity(requestEntity);

		try {
			int status = client.executeMethod(method);
			
			if (log.isTraceEnabled()){
				log.trace(method.getResponseBodyAsString());
			}
			
			return status;
		} finally {
			method.releaseConnection();
		}
	}

	/**
	 * Writes the formData to a temporary file
	 * 
	 * @param formData
	 * @return the temporary file
	 */
	File writeFormDataToFile(FormData formData) {
		try {
			File file = File.createTempFile("openxdata", "formdata-" + formData.getId());
			IOUtils.write(formData.getData(), new FileOutputStream(file));
			return file;
		} catch (IOException e) {
			log.error("Unable to write form data to temporary file", e);
		}
		return null;
	}

	/**
	 * Authenticate via the login page in order to get the session cookie
	 * 
	 * @param client the client to use for authentication
	 * @throws Exception if unable to authenticate
	 */
	void authenticate(HttpClient client) throws Exception {
		PostMethod method = new PostMethod(url + DHIS_LOGIN_ACTION);
		method.addParameter("j_username", username);
		method.addParameter("j_password", password);
		client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

		method.setDoAuthentication(true);
		try {
			client.executeMethod(method);
		} finally {
			method.releaseConnection();
		}
	}

	public void init(TaskDef taskDef) {
		if (taskDef != null) {
			this.url = taskDef.getParamValue("url");
			this.username = taskDef.getParamValue("username");;
			this.password = taskDef.getParamValue("password");;
		}
	}

	@Override
	public void stop() {
		log.info("Stopping Data Export Service");
		taskDef = null;
		running = false;
	}

	@Override
	public TaskDef getTaskDef() {
		return taskDef;
	}

	@Override
	public boolean isRunning() {
		return running;
	}
	
	public void setDataExportService(DataExportService dataExportService) {
		this.dataExportService = dataExportService;
	}
}
