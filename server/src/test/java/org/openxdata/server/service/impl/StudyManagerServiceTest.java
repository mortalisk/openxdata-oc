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
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDataHeader;
import org.openxdata.server.admin.model.FormDataVersion;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.service.FormService;
import org.openxdata.server.service.StudyManagerService;
import org.openxdata.server.service.UserService;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Tests methods in the StudyManagerService which deal with StudyDef.
 * 
 * @author daniel
 *
 */
public class StudyManagerServiceTest  extends BaseContextSensitiveTest {
	
	@Autowired
	protected FormService formService;
	
	@Autowired
	protected StudyManagerService studyManagerService;
	
	@Autowired	
	protected UserService userService;

    @Test
	public void getStudies_shouldReturnAllStudies() throws Exception {
		List<StudyDef> studies = studyManagerService.getStudies();
		
		Assert.assertNotNull(studies);
		Assert.assertEquals("There are 4 studies", 4, studies.size());
		Assert.assertNotNull(getStudy("Sample Study", studies));
	}
		
	@Test
	public void saveStudy_shouldSaveStudy() throws Exception {
		final String studyName = "StudyName";
		
		List<StudyDef> studies = studyManagerService.getStudies();
		Assert.assertEquals("There are 4 studies", 4, studies.size());
		Assert.assertNull(getStudy(studyName,studies));
		
		StudyDef study = new StudyDef();
		study.setName(studyName);
		study.setCreator(userService.getUsers().get(0));
		study.setDateCreated(new Date());
		
		studyManagerService.saveStudy(study);
		
		studies = studyManagerService.getStudies();
		Assert.assertEquals("Added 1 study, now there are 5", 5, studies.size());
		Assert.assertNotNull(getStudy(studyName,studies));
	}
	
	@Test
	public void saveFormData_editExistingData() throws Exception {
		int formDefVersionId = formService.getForms().get(0).getVersions().get(0).getFormDefVersionId();
		List<FormDataHeader> data = studyManagerService.getFormData(formDefVersionId, null, null, null);
		int dataCount = data.size();
		
		// create some form data
		FormData formData = new FormData();
		formData.setData("testing");
		formData.setFormDefVersionId(formDefVersionId);
		formData.setCreator(userService.getUsers().get(0));
		formData.setDateCreated(new Date());
		formService.saveFormData(formData);
		
		// check if the form data was created
		Assert.assertNotNull("FormData Id is set", formData.getId());
		data = studyManagerService.getFormData(formDefVersionId, null, null, null);
		Assert.assertEquals("One extra FormData", dataCount+1, data.size());
		dataCount = data.size();
		
		// try edit the form data and save
		formData.setData("testing updated");
		formData.setChangedBy(formData.getCreator());
		formData.setDateChanged(new Date());
		formService.saveFormData(formData);
		
		// check if the form data was updated
		data = studyManagerService.getFormData(formDefVersionId, null, null, null);
		Assert.assertEquals("No extra FormData", dataCount, data.size());
		FormData savedFormData = studyManagerService.getFormData(formData.getId());
		Assert.assertEquals("Data text is updated", "testing updated", savedFormData.getData());
		
		// check if the version was correctly saved
		List<FormDataVersion> versions = studyManagerService.getFormDataVersion(formData.getId());
		Assert.assertEquals("Only 1 previous version", 1, versions.size());
		Assert.assertEquals("Previous version data text is correct", "testing", versions.get(0).getData());
	}

	@Test
	public void deleteStudy_shouldDeleteGivenStudy() throws Exception {
		
		final String studyName = "Study Name";
		
		List<StudyDef> studies = studyManagerService.getStudies();
		Assert.assertEquals("There are 4 studies", 4, studies.size());
		Assert.assertNull(getStudy(studyName,studies));
	
		StudyDef study = new StudyDef();
		study.setName(studyName);
		study.setCreator(userService.getUsers().get(0));
		study.setDateCreated(new Date());
		
		studyManagerService.saveStudy(study);
		studies = studyManagerService.getStudies();
		Assert.assertEquals("Added 1 study, now there are 5", 5, studies.size());
		
		study = getStudy(studyName,studies);
		Assert.assertNotNull(study);

		studyManagerService.deleteStudy(study);
		
		studies = studyManagerService.getStudies();
		Assert.assertEquals("Deleted the study so there are 4 studies again", 4, studies.size());
		Assert.assertNull(getStudy(studyName,studies));
	}
	
	/**
	 * Gets a study object for a given name from a list of study objects.
	 * 
	 * @param name the name of the study to look for.
	 * @param studies the list of study objects.
	 * @return the study object that matches the given name.
	 */
	private StudyDef getStudy(String name, List<StudyDef> studies){
		for(StudyDef study : studies){
			if(study.getName().equals(name))
				return study;
		}
		
		return null;
	}
}