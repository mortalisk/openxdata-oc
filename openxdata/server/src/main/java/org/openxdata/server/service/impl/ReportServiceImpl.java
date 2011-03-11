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
package org.openxdata.server.service.impl;

import java.util.List;

import org.openxdata.server.admin.model.Report;
import org.openxdata.server.admin.model.ReportGroup;
import org.openxdata.server.admin.model.mapping.UserReportGroupMap;
import org.openxdata.server.admin.model.mapping.UserReportMap;
import org.openxdata.server.dao.ReportDAO;
import org.openxdata.server.dao.ReportGroupDAO;
import org.openxdata.server.dao.UserReportGroupMapDAO;
import org.openxdata.server.dao.UserReportMapDAO;
import org.openxdata.server.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation for <code>Report Service</code>.
 * @author Angel
 *
 */
@Service("reportService")
@Transactional(propagation=Propagation.REQUIRED)
public class ReportServiceImpl implements ReportService {
	
	@Autowired
	private ReportDAO reportDAO;
	
	@Autowired
	private ReportGroupDAO reportGroupDAO;
	
	@Autowired
	private UserReportMapDAO userReportMapDAO;
	
	@Autowired
	private UserReportGroupMapDAO userReportGroupMapDAO;
	
	@Override
	@Secured({"Perm_Delete_Reports", "Perm_Delete_Users"})
	public void deleteUserMappedReport(UserReportMap map) {
		userReportMapDAO.deleteUserMappedReport(map);
		
	}

	@Override
	@Secured("Perm_Delete_Reports")
	public void deleteReport(Report report) {
		reportDAO.deleteReport(report);
		
	}

	@Override
	@Secured("Perm_Delete_ReportGroups")
	public void deleteReportGroup(ReportGroup reportGroup) {
		reportGroupDAO.deleteReportGroup(reportGroup);
		
	}

	@Override
	@Transactional(readOnly=true)
	@Secured({"Perm_View_Reports", "Perm_View_Users"})
	public List<UserReportMap> getUserMappedReports() {
		return userReportMapDAO.getUserMappedReports();
	}

	@Override
	@Transactional(readOnly=true)
	@Secured("Perm_View_Reports")
	public List<Report> getReports() {
		return reportDAO.getReports();
	}
	
	@Override
	@Transactional(readOnly=true)
	@Secured("Perm_View_Reports")
	public ReportGroup getReportGroup(String groupName) {
		return reportGroupDAO.getReportGroup(groupName);
		
	}
	
	@Override
	@Transactional(readOnly=true)
	@Secured({"Perm_Add_Reports", "Perm_Add_Users"})
	public void saveUserMappedReport(UserReportMap map) {
		userReportMapDAO.saveUserMappedReport(map);
	}

	@Override
	@Secured("Perm_Add_Reports")
	public void saveReport(Report report) {
		reportDAO.saveReport(report);
	}

	@Override
	@Secured("Perm_Add_ReportGroups")
	public void saveReportGroup(ReportGroup reportGroup) {
		reportGroupDAO.saveReportGroup(reportGroup);		
	}

    @Override
	@Transactional(readOnly = true)
	@Secured({"Perm_View_Reports", "Perm_View_Users"})
	public List<UserReportGroupMap> getUserMappedReportGroups() {
		return userReportGroupMapDAO.getUserMappedReportGroups();
	}

	@Override
	@Secured({"Perm_Delete_ReportGroups", "Perm_Delete_Users"})
	public void deleteUserMappedReportGroup(UserReportGroupMap map) {
		userReportGroupMapDAO.deleteUserMappedReportGroup(map);
	}

	@Override
	@Secured({"Perm_Add_ReportGroups", "Perm_Add_Users"})
	public void saveUserMappedReportGroup(UserReportGroupMap map) {
		userReportGroupMapDAO.saveUserMappedReportGroup(map);
	}

	@Override
	public List<ReportGroup> getReportGroups() {
		return reportGroupDAO.getReportGroups();
	}
}