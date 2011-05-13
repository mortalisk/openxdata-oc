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

import java.util.Date;
import java.util.List;

import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.StudyDef;


/**
 * Provides data access services to the data export service.
 * 
 * @author daniel
 *
 */
public interface DataExportDAO {

	/**
	 * Gets a form definition version as identified by the id.
	 * 
	 * @param formDefVersionId the form definition version id.
	 * @return the form definition version object.
	 */
	FormDefVersion getFormDefVersion(Integer formDefVersionId);
	
	/**
	 * Gets a form definition as identified by the id.
	 * 
	 * @param formId the form definition id.
	 * @return the form definition object.
	 */
	FormDef getFormDef(Integer formId);
	
	/**
	 * Gets a study as identified by the id.
	 * 
	 * @param studyId the study id.
	 * @return the study object.
	 */
	StudyDef getStudyDef(Integer studyId);
	
    /**
	 * Gets a list of xforms xml data models as collected for a given form definition version and 
	 * submitted for a given date range and by a given user.
	 *  
	 * @param formDefVersionId the id of the form definition version.
	 * @param fromDate the lower limit of the submission date.
	 * @param toDate the upper limit of the submission date.
	 * @param userId the user who submitted the data.
	 * @return the list of xforms xml models.
	 */
	List<Object[]> getFormDataWithAuditing(Integer formDefVersionId, Date fromDate, Date toDate, Integer userId);
	
	/**
     * Gets a list of all data submitted to the database which has not yet been exported
	 * 
	 * @param exporterBitFlag flag indicating the bit used by this exporter
	 * @return a list of form data objects.
	 * @see FormData.exported
	 */
	List<FormData> getFormDataToExport(Integer exporterBitFlag);
	
	/**
	 * Marks data which has been successfully exported to the destination format.
	 * 
	 * @param formData the form data to mark as exported.
	 * @param exporterBitFlag flag indicating the bit used by this exporter
	 * @see FormData.exported
	 */
	void setFormDataExported(FormData formData, Integer exporterBitFlag);
}
