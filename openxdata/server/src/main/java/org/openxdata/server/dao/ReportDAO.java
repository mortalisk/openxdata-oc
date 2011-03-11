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

import org.openxdata.server.admin.model.Report;

/**
 * Provides data access 
 * services to the <code>Report service</code>.
 * 
 * @author Angel
 *
 */
public interface ReportDAO extends BaseDAO<Report> {

	/** Gets a list of report definitions from the database.
	 * 
	 * @return the report definition list.
	 */
	List<Report> getReports();
	
	/**
	 * Gets a report definition object from the database.
	 * 
	 * @param reportId the report definition identifier.
	 * @return the report definition object.
	 */
	Report getReport(Integer reportId) ;
	
	/**
	 * Saves a report definition to the database.
	 * 
	 * @param report the report definition to save.
	 */
	void saveReport(Report report);
	
	/**
	 * Deletes a report definition from the database.
	 * 
	 * @param report the report definition to delete.
	 */
	void deleteReport(Report report);
	
}
