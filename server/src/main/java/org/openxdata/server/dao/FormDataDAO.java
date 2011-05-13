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

import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDataVersion;

/**
 * @author Angel
 *
 */
public interface FormDataDAO extends BaseDAO<FormData> {

	/**
	 * Deletes a row of data from the database.
	 * 
	 * @param formDataId the identifier for the row of data to delete.
	 */
	void deleteFormData(Integer formDataId);
	
	/**
	 * Gets form data as identified by the id.
	 * 
	 * @param formDataId the form data identifier.
	 * @return the form data.
	 */
	FormData getFormData(Integer formDataId);
	
	/**
	 * Saves form data.
	 * 
	 * @param formData the form data to save.
	 */
	void saveFormData(FormData formData);
	
    /**
     * Creates a FormDataVersion given the FormData being backed up
     * @param formData FormData to be versioned
     */
    void saveFormDataVersion(FormData formData);
    
	/**
     * Saves form data version (backup).
     * 
     * @param formDataVersion the form data version to save.
     */
    void saveFormDataVersion(FormDataVersion formDataVersion);
    
    /**
     * Retrieves the history of the specified FormData object
     * 
     * @param formDataId Integer FormData identifier
     * @return List of FormDataVersion
     */
    List<FormDataVersion> getFormDataVersion(Integer formDataId);
}