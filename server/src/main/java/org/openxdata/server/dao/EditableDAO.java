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

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.FormDataHeader;
import org.openxdata.server.admin.model.FormDef;

/**
 * Provides data access services to the study manager service.
 * 
 * @author daniel
 * @author Angel
 *
 */
public interface EditableDAO extends BaseDAO<FormDef> {	
		
	/** 
	 * Checks if a <code>Editable</code> has data collected for it. 
	 *  
	 * @param item  Can be <code>Study, Form, or Form Version.</code> 
	 * @return true if it has, else false. 
	 */ 
	 Boolean hasEditableData(Editable item); 

	/**
	 * Gets a list of headers for form data submitted to the database.
	 * 
	 * @param formDefId
	 * @param userId the user who submitted the data. If you want all users, pass null.
	 * @param fromDate the submission date from which to start the search. To include all dates, pass null.
	 * @param toDate the submission date up to which to do the search. To include all dates, pass null.
	 * @return the form data header list.
	 */
	List<FormDataHeader> getFormData(Integer formDefId, Integer userId, Date fromDate, Date toDate);
		
	/**
	 * Gets the number of responses for the specified form definition version
	 * @param formDefId Integer form definition identifier
	 * @return Integer count of form_data
	 */
	Integer getFormDataCount(Integer formDefId);

	
	/**
	 * Get the response data for a form 
	 * @param formBinding the binding for the form (translates to the table name)
	 * @param questionBindings the quesions in the form (translates to the table column names)
	 * @param offset paging offset
	 * @param limit paging limit
	 * @param sortField
	 * @param ascending
	 * @return 
	 */
	List<Object[]> getResponseData(String formBinding, String[] questionBindings, int offset,
			int limit, String sortField, boolean ascending);

	/**
	 * Get the number of responses for a form
	 * 
	 * @param formBinding
	 * @return
	 */
	BigInteger getNumberOfResponses(String formBinding);
}
