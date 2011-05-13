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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.openxdata.server.admin.model.FormDataHeader;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.service.FormDownloadService;
import org.openxdata.server.service.StudyManagerService;
import org.openxdata.server.service.UserService;
import org.openxdata.test.BaseContextSensitiveTest;
import org.openxdata.test.XFormsFixture;
import org.springframework.beans.factory.annotation.Autowired;

public class FormDownloadServiceTest extends BaseContextSensitiveTest {
	
	@Autowired
	protected FormDownloadService formDownloadService;
	
	@Autowired
	protected StudyManagerService studyManagerService;
	
	@Autowired
	protected UserService userService;
	
	@Test
	@Ignore("throws too many exceptions")
	public void testSubmitForms_noSerializer() throws Exception {
		
		// create the stream
    	final PipedOutputStream pout = new PipedOutputStream();
    	DataInputStream in = new DataInputStream(new PipedInputStream(pout));
    	Thread thread = new Thread(
		    new Runnable(){
		      @Override
			public void run(){
		      	DataOutput output = new DataOutputStream(pout);
		        try {
		        	output.writeByte(1);
					output.writeUTF(XFormsFixture.getSampleFormModelData());
				} catch (IOException e) {
					e.printStackTrace();
				}
		      }
		    }
		  );
    	thread.start();
    	DataOutputStream out = new DataOutputStream(new ByteArrayOutputStream());
    	
    	// run test
		formDownloadService.submitForms(in, out, null);
		
		// do checks afterwards
		List<FormDataHeader>  formData = studyManagerService.getFormData(12, null, null, null);
		Assert.assertEquals("after submit there is 1 form data", 1, formData.size());
	}
	
	@Test
	public void testGetStudyList_forUser() throws Exception {
		User user = userService.findUserByUsername("user");
		List<Object[]> studies = formDownloadService.getStudyList(user);
		Assert.assertNotNull("There are studies for ordinary user", studies);
		Assert.assertEquals("There are two studies", 2, studies.size());
		assertStudy(studies.get(0), new Integer(2), "Another Sample Study");
		assertStudy(studies.get(1), new Integer(1), "Sample Study");
	}
	
	@Test
	public void testGetStudyList_forAdmin() throws Exception {
		User user = userService.findUserByUsername("admin");
		List<Object[]> studies = formDownloadService.getStudyList(user);
		Assert.assertNotNull("There are studies for admin user", studies);
		Assert.assertEquals("There are four studies", 4, studies.size());
		assertStudy(studies.get(0), new Integer(2), "Another Sample Study");
		assertStudy(studies.get(1), new Integer(4), "More Sample Study");
		assertStudy(studies.get(2), new Integer(1), "Sample Study");
		assertStudy(studies.get(3), new Integer(3), "Yet Another Sample Study");
	}
	
	@Test
	public void testGetFormList_forUserStudy1() throws Exception {
		User user = userService.findUserByUsername("user");
		
		// user has study permissions for study1, so can see all forms even though they have only 1 form permission
		List<String> forms = formDownloadService.getFormsDefaultVersionXml(user, 1, null);
		Assert.assertNotNull("There are forms for user user", forms);
		Assert.assertEquals("There are two forms under study 1", 2, forms.size());
		Assert.assertTrue("xform is correct", forms.get(0).contains("<xf:instance id=\"patientreg\">"));
	}
	
	@Test
	public void testGetFormList_forUserStudy2() throws Exception {
		User user = userService.findUserByUsername("user");
		// user has only one form permission for study2
		List<String> forms = formDownloadService.getFormsDefaultVersionXml(user, 2, null);
		Assert.assertNotNull("There are forms for user user", forms);
		Assert.assertEquals("There is 1 forms under study 2", 1, forms.size());
	}
	
	@Test
	public void testGetFormList_forAdmin() throws Exception {
		User user = userService.findUserByUsername("admin");
		List<String> forms = formDownloadService.getFormsDefaultVersionXml(user, 1, null);
		Assert.assertNotNull("There are forms for admin user", forms);
		Assert.assertEquals("There are two forms under study 1", 2, forms.size());
		Assert.assertTrue("xform is correct", forms.get(0).contains("<xf:instance id=\"patientreg\">"));
		Assert.assertTrue("xform is correct", forms.get(1).contains("<xf:instance id=\"patientreg\">"));
	}
	
	private void assertStudy(Object[] study, Integer expectedId, String expectedName) {
		Assert.assertEquals("Study has Id "+expectedId, expectedId, study[0]);
		Assert.assertEquals("Study is called "+expectedName, expectedName, study[1]);
	}
}
