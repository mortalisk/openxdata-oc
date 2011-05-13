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

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openxdata.server.admin.model.Report;
import org.openxdata.server.admin.model.ReportGroup;
import org.openxdata.server.service.ReportService;
import org.openxdata.server.service.UserService;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Tests methods in the ReportService which deal with reports.
 * 
 * @author daniel
 *
 */
public class ReportServiceTest extends BaseContextSensitiveTest {
	
	@Autowired
	protected ReportService reportsService;
	
	@Autowired
	protected UserService userService;
	
	@Test
	public void getReports_shouldReturnAllReports() throws Exception {
		
		List<ReportGroup> reportGroups = reportsService.getReportGroups();
		
		Assert.assertNotNull(reportGroups);
		Assert.assertEquals(1, reportGroups.size());
		 
		ReportGroup reportGroup = reportGroups.get(0);
		Assert.assertEquals("General",reportGroup.getName());
		
		List<Report> reports = reportGroup.getReports();
		Assert.assertNotNull(reports);
		Assert.assertEquals(1, reports.size());
		
		Report report = reports.get(0);
		Assert.assertEquals("Report Name",report.getName());
	}
		
	@Test
	public void saveReportGroup_shouldSaveReport() throws Exception {
		final String reportName = "New Report";
		
		List<ReportGroup> reportGroups = reportsService.getReportGroups();
		Assert.assertNull("New Report does not exist", ReportGroup.getReport(reportName, reportGroups));
		
		ReportGroup reportGroup = reportGroups.get(0);
		Report report = new Report(reportName);
		report.setCreator(userService.getUsers().get(0));
		report.setDateCreated(new Date());
		report.setReportGroup(reportGroup);
		reportGroup.addReport(report);
		reportsService.saveReportGroup(reportGroup);
		
		Assert.assertNotNull("Report was saved and assigned an ID", report.getId());
		
		reportGroups = reportsService.getReportGroups();
		Assert.assertNotNull("Report saved in Group", ReportGroup.getReport(reportName, reportGroups));
	}

	@Test
	public void saveReportGroup_shouldSaveReportGroup() throws Exception {
		final String groupName = "New Report Group";
		
		List<ReportGroup> reportGroups = reportsService.getReportGroups();
		Assert.assertEquals(1, reportGroups.size());
		Assert.assertNull(getReportGroup(groupName,reportGroups));
		
		ReportGroup reportGroup = new ReportGroup(groupName); 
		reportGroup.setCreator(userService.getUsers().get(0));
		reportGroup.setDateCreated(new Date());
		reportsService.saveReportGroup(reportGroup);
		
		reportGroups = reportsService.getReportGroups();
		Assert.assertEquals(2, reportGroups.size());
		
		reportGroup = getReportGroup(groupName,reportGroups);
		Assert.assertNotNull(reportGroup);
		Assert.assertEquals(groupName, reportGroup.getName());
	}

	@Test
	public void saveReportGroup_shouldSaveReportAndGroup() throws Exception {
		final String groupName = "New Report Group";
		final String reportName = "New Report";
		
		List<ReportGroup> reportGroups = reportsService.getReportGroups();
		Assert.assertEquals(1, reportGroups.size());
		Assert.assertNull(ReportGroup.getReport(reportName, reportGroups));
		Assert.assertNull(getReportGroup(groupName,reportGroups));
		
		ReportGroup reportGroup = new ReportGroup(groupName); 
		reportGroup.setCreator(userService.getUsers().get(0));
		reportGroup.setDateCreated(new Date());
		
		Report report = new Report(reportName);
		report.setCreator(userService.getUsers().get(0));
		report.setDateCreated(new Date());
		report.setReportGroup(reportGroup);
		reportGroup.addReport(report);
		
		reportsService.saveReportGroup(reportGroup);
		
		reportGroups = reportsService.getReportGroups();
		Assert.assertEquals(2, reportGroups.size());
		
		reportGroup = getReportGroup(groupName,reportGroups);
		Assert.assertNotNull(reportGroup);
		Assert.assertEquals(1, reportGroup.getReports().size());
		Assert.assertEquals(groupName, reportGroup.getName());
		
		report = ReportGroup.getReport(reportName, reportGroups);
		Assert.assertNotNull(report);
	}
	
	@Test
	public void deleteReport_shouldDeleteGivenReport() throws Exception {
		List<ReportGroup> reportGroups = reportsService.getReportGroups();
		Assert.assertNotNull(reportGroups);
		Assert.assertTrue("at least 1 report group exists", reportGroups.size() >= 1);
		
		ReportGroup reportGroup = reportGroups.get(0);
		int reportsSize = reportGroup.getReports().size();
		Assert.assertTrue("at least 1 report exists", reportsSize >= 1);
		
		Report report = reportGroup.getReports().get(0);
		reportGroup.removeReport(report);
		reportsService.deleteReport(report);
		
		reportGroups = reportsService.getReportGroups();	
		reportGroup = reportGroups.get(0);
		Assert.assertEquals("after delete number of reports has decreased by 1", 
				(reportsSize-1), reportGroup.getReports().size());
	}

	@Test
	public void deleteReportGroup_shouldDeleteGivenReportGroup() throws Exception {
		List<ReportGroup> reportGroups = reportsService.getReportGroups();
		
		ReportGroup reportGroup  = getReportGroup("General",reportGroups);
		Assert.assertNotNull(reportGroup);
		
		reportsService.deleteReportGroup(reportGroup);
		
		reportGroups = reportsService.getReportGroups();
		Assert.assertNull(getReportGroup("General",reportGroups));
	}
	
	/**
	 * Gets a report object for a given name from a list of report objects.
	 * 
	 * @param name the name of the report to look for.
	 * @param reports the list of report objects.
	 * @return the report object that matches the given name.
	 */
	private ReportGroup getReportGroup(String name, List<ReportGroup> reportGroups){
		for(ReportGroup reportGroup : reportGroups){
			if(reportGroup.getName().equals(name))
				return reportGroup;
		}
		
		return null;
	}
}
