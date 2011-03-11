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
package org.openxdata.server;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.service.AuthenticationService;
import org.openxdata.server.service.FormDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZOutputStream;

/**
 * Serves xform services to non HTTP connections. Examples of such connections
 * can be SMS, Bluetooth, Data cable, etc. In other wards the forms server is not
 * tied to any particular connection type. The connection type specific listeners 
 * (eg FormsBluetoothServer, FormSmsServer, etc)
 * will handle the connection specific issues and optionally delegate to this class.
 * 
 * @author Daniel
 * 
 */
@Component("formsServer")
public class FormsServer {

	/** Value representing a not yet set status. */
	public static final byte STATUS_NULL = -1;

	/** Value representing success of an action. */
	public static final byte STATUS_SUCCESS = 1;

	/** Value representing failure of an action. */
	public static final byte STATUS_FAILURE = 0;

	/** Action to get a lsit of studies. */
	public static final byte ACTION_DOWNLOAD_STUDY_LIST = 2;

	/** Action to get a list of form definitions. */
	public static final byte ACTION_DOWNLOAD_FORMS = 3;

	/** Action to save a list of form data. */
	public static final byte ACTION_UPLOAD_DATA = 5;

	/** Action to download a list of patients from the server. */
	public static final byte ACTION_DOWNLOAD_USERS = 7;

	/** Action to download a list of users and forms from the server. */
	public static final byte ACTION_DOWNLOAD_USERS_AND_FORMS = 11;

	/** Status to download a list of users and studies from the server. */
	public static final byte ACTION_DOWNLOAD_USERS_AND_ALL_FORMS = 12;
	
	/** Action to download a list of languages from the server. */
	public static final byte ACTION_DOWNLOAD_LANGUAGES = 15;
	
	/** Status to download menu text in the selected language. */
	public static final byte ACTION_DOWNLOAD_MENU_TEXT = 16;
	
	/** Action to get a list of form definitions. */
	public static final byte ACTION_DOWNLOAD_STUDY_FORMS = 17;

	@Autowired
	private FormDownloadService formDownloadService;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	private Logger log = Logger.getLogger(this.getClass());

	/**
	 * Creates a new instance of the form server.
	 */
	public FormsServer(){}

	/**
	 * Creates a new instance of the form server and passes it a reference to the form download service.
	 * 
	 * @param formDownloadService the form download service.
	 */
	public FormsServer(FormDownloadService formDownloadService) {
		this.formDownloadService = formDownloadService;
	}

	/**
	 * Called when a new connection has been received. Failures are not handled
	 * in this class as different servers (BT,SMS, etc) may want to handle them
	 * differently.
	 * 
	 * @param dis - the stream to read from.
	 * @param dos - the stream to write to.
	 */
	public void processConnection(InputStream disParam, OutputStream dosParam){

		ZOutputStream gzip = new ZOutputStream(dosParam, JZlib.Z_BEST_COMPRESSION);
		DataOutputStream dos = new DataOutputStream(gzip);

		byte responseStatus = ResponseStatus.STATUS_ERROR;
		User user = null;

		try {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataInputStream dis = new DataInputStream(disParam);
	
				String name = dis.readUTF();
				String password = dis.readUTF();
				String serializer = dis.readUTF();
				String locale = dis.readUTF();
				
				byte action = dis.readByte();
				
				user = authenticationService.authenticate(name,password);
				if (user == null) {
					responseStatus = ResponseStatus.STATUS_ACCESS_DENIED;
				} else {
					DataOutputStream dosTemp = new DataOutputStream(baos);
	
					if (action == ACTION_DOWNLOAD_FORMS)
						formDownloadService.downloadForms(dosTemp,serializer,locale);
					else if (action == ACTION_UPLOAD_DATA)
						submitXforms(dis, dosTemp,serializer);
					else if (action == ACTION_DOWNLOAD_USERS)
						formDownloadService.downloadUsers(dosTemp,serializer);
					else if (action == ACTION_DOWNLOAD_USERS_AND_FORMS)
						downloadUsersAndForms(dis.readInt(),dosTemp,serializer,locale);
					else if (action == ACTION_DOWNLOAD_STUDY_LIST)
						formDownloadService.downloadStudies(dosTemp,serializer,locale);
					else if(action == ACTION_DOWNLOAD_LANGUAGES)
						formDownloadService.downloadLocales(dis, dosTemp,serializer);
					else if(action == ACTION_DOWNLOAD_MENU_TEXT)
						formDownloadService.downloadMenuText(dis, dosTemp,serializer,locale);
					else if(action == ACTION_DOWNLOAD_STUDY_FORMS)
						formDownloadService.downloadForms(dis.readInt(),dos,serializer,locale);
					else if (action == ACTION_DOWNLOAD_USERS_AND_ALL_FORMS)
						downloadUsersAndAllForms(dosTemp,serializer,locale);
					
					responseStatus = ResponseStatus.STATUS_SUCCESS;
				}
				
				dos.writeByte(responseStatus);
				
				if (responseStatus == ResponseStatus.STATUS_SUCCESS) {
					dos.write(baos.toByteArray());
				}
			} catch (OpenXDataSecurityException ex) {
				log.error("Security Exception for user '"+user.getName()+"' :" + ex.getMessage());
				dos.writeByte(ResponseStatus.STATUS_PERMISSION_DENIED);
			} catch (Exception ex) {
				log.error(ex.getMessage(),ex);
				dos.writeByte(responseStatus);
			}
			finally {
				dos.flush();
				gzip.finish();
			}
		} catch (IOException e) {
			// this is for exceptions occurring in the catch or finally clauses.
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * Saves xforms xml models.
	 * 
	 * @param dis - the stream to read from.
	 * @param dos - the stream to write to.
	 */
	private void submitXforms(DataInputStream dis, DataOutputStream dos, String serializer) {
		formDownloadService.submitForms(dis, dos,serializer);
	}

	/**
	 * Downloads a list of users and xforms.
	 * 
	 * @param dos - the stream to write to.
	 */
	private void downloadUsersAndForms(int studyId, DataOutputStream dos, String serializer,String locale) {
		formDownloadService.downloadUsers(dos,serializer);
		formDownloadService.downloadForms(studyId,dos,serializer,locale);
	}

	/**
	 * Downloads a list of users and all xforms.
	 * 
	 * @param dos - the stream to write to.
	 */
	private void downloadUsersAndAllForms(DataOutputStream dos, String serializer,String locale) {
		formDownloadService.downloadUsers(dos,serializer);
		formDownloadService.downloadAllForms(dos,serializer,locale);
	}
}
