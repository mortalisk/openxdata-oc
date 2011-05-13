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
package org.openxdata.server.dao;

import java.util.List;
import java.util.Map;

import org.openxdata.server.admin.model.FormSmsArchive;
import org.openxdata.server.admin.model.FormSmsError;
import org.openxdata.server.admin.model.User;


/**
 * Provides data access services to the form download service.
 * 
 * @author daniel
 *
 */
public interface FormDownloadDAO {


	/**
	 * Gets a list of studies.
	 * 
	 * @return the study list.
	 */
	List<Object[]> getStudyList();
	
	/**
	 * Gets a list of studies. Each item in this list being an array
	 * of two objects. The first is an Integer which is the study id, and the second
	 * is a String which is the study name.
	 * 
	 * @param User user requesting the study list
	 * @return the study list.
	 */
	List<Object[]> getStudyList(User user);
	
	/**
	 * Gets a map of default form versions keyed by the form version id.
	 * @param User user requesting the form list
	 * @return the map of xml texts for each default form version keyed by the form version id.
	 */
	Map<Integer,String> getFormsDefaultVersionXml(User user);
	
	/**
	 * Gets a map of all form versions keyed by the form version id.
	 * 
	 * @return the map of xml texts for each form version keyed by the form version id.
	 */
	Map<Integer,String> getFormsVersionXml();
	
	/**
	 * Gets a map of default form versions, in a given study, keyed by the form version id.
	 * 
	 * @param User user requesting the form list
	 * @param studyId the study identifier.
	 * @return the map of xml texts for each default form version keyed by the form version id.
	 */
	Map<Integer, String> getFormsDefaultVersionXml(User user, Integer studyId);
	
	/**
	 * Archives successfully processed sms.
	 * 
	 * @param data the sms data archive object.
	 */
	void saveFormSmsArchive(FormSmsArchive data);
	
	/**
	 * Saves sms which has resulted into errors during its processing.
	 * 
	 * @param error the sms error object.
	 */
	void saveFormSmsError(FormSmsError error);
	
	/**
	 * Gets a locale xml text for a given form.
	 * 
	 * @param formId the form identifier.
	 * @param locale the locale key.
	 * @return the locale xml text.
	 */
	String getXformLocaleText(Integer formId, String locale);
	
	/**
	 * Gets a user registered with a given phone number.
	 * 
	 * @param phoneNo the phone number.
	 * @return the user.
	 */
	User getUserByPhoneNo(String phoneNo);
	
	Integer getStudyIdWithKey(String studyKey);
}
