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

/**
 * This class holds constants used in the OpenXdata.
 * 
 * @author daniel
 * @author Angel
 */
public class OpenXDataConstants {

	/** Value representing a not yet set status. */
    public static final byte STATUS_NULL = -1;
    
    /** Value representing success of an action. */
    public static final byte STATUS_SUCCESS = 1;

    /** Value representing failure of an action. */
    public static final byte STATUS_FAILURE = 0;
    
	/** The text value for boolean true. */
	public static final String TRUE_TEXT_VALUE = "true";
	
	/** The batchEntry request parameter. */
	public static final String REQUEST_PARAM_BATCH_ENTRY = "batchEntry";
	
	/** The xformentry request parameter. */
	public static final String REQUEST_PARAM_XFORM_ENTRY = "xformentry";
	
	/** The content disposition http header. */
	public static final String HTTP_HEADER_CONTENT_DISPOSITION = "Content-Disposition";
	
	/** The content type http header. */
	public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
	
	/** The text/html http content type. */
	public static final String HTTP_HEADER_CONTENT_TYPE_TEXT_HTML = "text/html; charset=utf-8";
	
	/** The application/xhtml+xml http content type. */
	public static final String HTTP_HEADER_CONTENT_TYPE_XML = "text/xml; charset=utf-8"; //"application/xhtml+xml; charset=utf-8";
	
	/** The pdf http content type header. */
	public static final String HTTP_HEADER_CONTENT_TYPE_PDF = "application/pdf"; //"application/x-pdf";
	
	public static final String HTTP_HEADER_CONTENT_DISPOSITION_VALUE = "attachment; filename=\"";

	/** The name of the setting for the date format.*/
	public static final String SETTING_NAME_SUBMIT_DATE_FORMAT = "submitDateFormat";
	
	/** The name of the setting for the submission date format. */
	public static final String SETTING_NAME_SUBMIT_DATETIME_FORMAT = "submitDateTimeFormat";
	
	/** The name of the setting for the submission time format. */
	public static final String SETTING_NAME_SUBMIT_TIME_FORMAT = "submitTimeFormat";
	
	/** The name of the setting for the dislay date format. */
	public static final String SETTING_NAME_DISPLAY_DATE_FORMAT = "displayDateFormat";
	
	/** The name of the setting for the displat date and time format. */
	public static final String SETTING_NAME_DISPLAY_DATETIME_FORMAT = "displayDateTimeFormat";
	
	/** The name of the setting for the display time format. */
	public static final String SETTING_NAME_DISPLAY_TIME_FORMAT = "displayTimeFormat";
	
	/** The name of the setting for the user serializer class.*/
	public static final String SETTING_NAME_USER_SERIALIZER= "userSerializer";
	
	/** The name of the setting for the user serializer class.*/
	public static final String SETTING_NAME_STUDY_SERIALIZER= "studySerializer";
		
	/** The name of the setting for the xform serializer class.*/
	public static final String SETTING_NAME_FORM_SERIALIZER = "formSerializer";
		
	/** The name of the setting for determining whether to include users when downloading xforms. */
	public static final String SETTING_NAME_INCLUDE_USERS_IN_XFORMS_DOWNLOAD = "includeUsersInXformsDownload";

	/** The default value for the user serializer class.*/
	public static final String DEFAULT_USER_SERIALIZER= "org.openxdata.server.serializer.DefaultXformSerializer";

	/** The default value for the xform serializer class.*/
	public static final String DEFAULT_FORM_SERIALIZER = "org.openxdata.server.serializer.DefaultXformSerializer";

	/** The default value for the study serializer class.*/
	public static final String DEFAULT_STUDY_SERIALIZER = "org.openxdata.server.serializer.DefaultXformSerializer";
	
	/** The name of the form id attribute. */
	public static final String ATTRIBUTE_NAME_FORMID = "id";
	
	/** The name of the description template attribute. */
	public static final String ATTRIBUTE_NAME_DESCRIPTION_TEMPLATE = "description-template";
	
	/** The action request parameter. */
	public static final String REQUEST_PARAMETER_ACTION = "action";
	
	/** The http request action to download studies. */
	public static final String ACTION_DOWNLOAD_STUDIES = "downloadstudies";
	
	/** The http request action to download forms. */
	public static final String REQUEST_ACTION_DOWNLOAD_FORMS = "downloadforms";
	
	/** The http request action to download users. */
	public static final String REQUEST_ACTION_DOWNLOAD_USERS = "downloadusers";
	
	/** The http request action to uploda data. */
	public static final String REQUEST_ACTION_UPLOAD_DATA = "uploaddata";
	
	/** The http request paramer for user name. */
	public static final String REQUEST_PARAM_USERNAME = "uname";
	
	/** The http request parameter for user password. */
	public static final String REQUEST_PARAM_PASSWORD = "pw";
	
	/** The http request parameter for the name of the setting used for serialization of forms. */
	public static final String REQUEST_PARAM_FORM_SERIALIZER = "formser";
	
	/** The http request parameter for the name of the setting used for serialization of studies. */
	public static final String REQUEST_PARAM_STUDY_SERIALIZER = "studyser";
	
	/** The http request parameter for the name of the setting used for serialization of users. */
	public static final String REQUEST_PARAM_USER_SERIALIZER = "userser";
	
	/** The http request parameter for the locale. */
	public static final String REQUEST_PARAM_LOCALE = "locale";

	/** The default submit date format. */
	public static final String DEFAULT_DATE_SUBMIT_FORMAT = "yyyy-MM-dd";
	
	/** The default submit datetime format. */
	public static final String DEFAULT_DATETIME_SUBMIT_FORMAT = "yyyy-MM-dd hh:mm:ss a";
	
	/** The default submit time format. */
	public static final String DEFAULT_TIME_SUBMIT_FORMAT = "hh:mm:ss a";
	
	/** The default display date format. */
	public static final String DEFAULT_DATE_DISPLAY_FORMAT = "dd-MM-yyyy";
	
	/** The default display date format. */
	public static final String DEFAULT_DATETIME_DISPLAY_FORMAT = "dd-MM-yyyy hh:mm:ss a";
	
	/** The default display date format. */
	public static final String DEFAULT_TIME_DISPLAY_FORMAT = "hh:mm:ss a";
	
	/** The Default password for administrator that ships with the system.*/
	public static final String DEFAULT_ADMINISTRATOR_PASSWORD = "7357bec928a1af86415f7b8c11245296ec1779d";
}
