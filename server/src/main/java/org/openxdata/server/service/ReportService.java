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
package org.openxdata.server.service;

import java.util.List;

import org.openxdata.server.admin.model.Report;
import org.openxdata.server.admin.model.ReportGroup;
import org.openxdata.server.admin.model.mapping.UserReportGroupMap;
import org.openxdata.server.admin.model.mapping.UserReportMap;

/**
 * This service is used for 
 * managing <code>Reports</code>.
 * 
 * @author Angel
 *
 */
public interface ReportService {

    /**
     * Gets a list of report definitions from the database.
     *
     * @return the report definition list.
     */
    List<Report> getReports();
    
    List<ReportGroup> getReportGroups();

    /**
     * Saves a report definition to the database.
     *
     * @param report the report definition to save.
     */
    void saveReport(Report report);

    /**
     * Saves a report group to the database.
     *
     * @param reportGroup the report group to save.
     */
    void saveReportGroup(ReportGroup reportGroup);

    /**
     * Deletes a report definition from the database.
     *
     * @param report the report definition to delete.
     */
    void deleteReport(Report report);

    /**
     * Deletes a report group from the database.
     *
     * @param reportGroup the report group to delete.
     */
    void deleteReportGroup(ReportGroup reportGroup);

    /**
     * Gets a list of all <code>UserReportMap objects</code> from the database.
     *
     * @return List of <code>UserReportMap objects.</code>
     */
    List<UserReportMap> getUserMappedReports();

    /**
     * Saves a dirty ReportUserMap object
     * @param map map to save
     *
     */
    void saveUserMappedReport(UserReportMap map);

    /**
     * Deletes a ReportUserMap object
     * @param map map to delete
     *
     */
    void deleteUserMappedReport(UserReportMap map);

    /**
     * Returns a list of all <code>UserReportGroupMap objects</code> persisted in the database.
     * @return List of <code>UserReportGroupMap objects.</code>
     */
    List<UserReportGroupMap> getUserMappedReportGroups();

    /**
     * Save a <code>UserReportGroupMap</code> object.
     * @param map map to persist
     */
    void saveUserMappedReportGroup(UserReportGroupMap map);

    /**
     * Deletes a <code>UserReportGroupMap</codd> object.
     * @param map map to delete.
     */
    void deleteUserMappedReportGroup(UserReportGroupMap map);

    
	/**
	 * Get a <code>ReportGroup</code> with the given name.
	 * 
	 * @param groupName
	 */
	ReportGroup getReportGroup(String groupName);
}
