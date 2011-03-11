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
package org.openxdata.server.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openxdata.server.admin.model.ExportedFormDataList;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.security.OpenXDataUserDetails;
import org.openxdata.server.service.FormService;
import org.openxdata.server.service.RoleService;
import org.openxdata.server.service.StudyManagerService;
import org.openxdata.server.service.UserService;
import org.openxdata.test.XFormsFixture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.context.SecurityContextImpl;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:openxdata-test-applicationContext.xml"})
public class LoadTest extends AbstractJUnit4SpringContextTests {
    
    User user = null;
    OpenXDataUserDetails oxdUser = null;
    FormDefVersion form = null;
    
    @Autowired
    FormService formService;
    
    @Autowired
    StudyManagerService studyManagerService;
    
    @Autowired
    UserService userService;
    
    @Autowired
    RoleService permissionService;
    
    @Before
    public void createSpringSecurityContext() throws Exception {
        // set up the user (with all permissions) and inserts into the spring security context 
        // so the security (on the service calls) doesn't fail with "access denied error"
        if (user == null) {
            user = userService.findUserByUsername("admin");
        }
        if (oxdUser == null) {
            List<Permission> permissions = permissionService.getPermissions();
            GrantedAuthority[] authorities = new GrantedAuthority[permissions.size()];
            int i=0;
            for (Permission p : permissions) {
                authorities[i++] = new GrantedAuthorityImpl(p.getName());
            }        
            oxdUser = new OpenXDataUserDetails(user, false, false, false, false, authorities);
        }
        Authentication auth = new UsernamePasswordAuthenticationToken(oxdUser, "9bdf333f8aa67cb49c1a4331a8a24e891e1933", oxdUser.getAuthorities());
        SecurityContext sc = new SecurityContextImpl();
        sc.setAuthentication(auth);
        SecurityContextHolder.setContext(sc);
    }
    
    @Test
    @Ignore("this test creates 100,000 form_data records!")
    public void testLotsOfData() throws Exception {
        long start = System.currentTimeMillis();
        System.out.println("Start time: "+new Date());
        // first create all the forms + form_data
        System.out.println("Creating the form");
        for (int i=0; i<20; i++) {
            form = createForm(i);
        }
        System.out.print("Creating the form data");
        for (int i = 0; i < 100; i++) {
            long start1 = System.currentTimeMillis();
            createFormData(form, i);
            long end2 = System.currentTimeMillis();
            if (i%1000 == 0) 
                System.out.println("." + (end2 - start1) + "ms");
        }
        long end = System.currentTimeMillis();
        System.out.println("Time to run test: "+(end-start)+"ms");
    }
    
    @Test
    @Ignore("this test runs a lot of threads performing random service operations - i.e. takes a long time")
    public void testSimultaneousRandomConnections() throws Exception {
        long start = System.currentTimeMillis();
        System.out.println("Start time: "+new Date());

        // find the last test form created (it contains all the test data)
        List<FormDef> forms = formService.getForms();
        if (forms != null) {
            for (FormDef form : forms) {
                FormDefVersion version = form.getDefaultVersion();
                if (version != null && version.getName() != null && 
                        version.getName().startsWith("dagmar-testformversion-")) {
                    if (this.form == null 
                            || this.form.getFormDefVersionId() < version.getFormDefVersionId()) {
                        // first time around, or this version was created later
                        this.form = version;
                    }
                }
            }
            if (form == null) {
                // we didn't find a match in the loop above ... 
                // (perhaps the "testLotsOfData" test was never run)
                form = forms.get(forms.size()-1).getDefaultVersion();
            }
            System.out.println("Selected form "+form.getName());
        } else {
            Assert.fail(); // can't continue the test further
        }
        
        // now try a lot of simultaneous random connections
        System.out.println("Creating the threads");
        List<RandomExecutionThread> threads = new ArrayList<RandomExecutionThread>();
        for (int i=0; i<125; i++) {
            threads.add(new RandomExecutionThread(i+1));
        }
        for (Thread thread : threads) {
            // ensures that the test only stops once the last thread is finished
            thread.join();
        }

        long end = System.currentTimeMillis();
        System.out.println("Time to run test: "+(end-start)+"ms");
        
        int successfulCount = 0;
        int failedCount = 0;
        for (RandomExecutionThread thread : threads) {
            if (thread.isSuccessful()) {
                successfulCount++; 
            } else {
                failedCount++;
            }
        }
        System.out.println("Successful calls: "+successfulCount+" Unsuccessful calls: "+failedCount);
    }
    
    private FormDefVersion createForm(int number) throws Exception {
        // study
        StudyDef study = new StudyDef();
        study.setName("dagmar-teststudy-"+number);
        study.setCreator(user);
        study.setDateCreated(new Date());
        study.addUser(user);
        studyManagerService.saveStudy(study);
        // form
        FormDef form = new FormDef();
        form.setName("dagmar-testform-"+number);
        form.setDescription("test form created by dagmar");
        form.setCreator(user);
        form.setDateCreated(new Date());
        form.setStudy(study);
        study.addForm(form);
        // form version
        FormDefVersion formV = new FormDefVersion();
        formV.setName("dagmar-testformversion-"+number);
        formV.setDescription("test form version created by dagmar");
        formV.setCreator(user);
        formV.setDateCreated(new Date());
        formV.setXform(XFormsFixture.getSampleForm());
        formV.setIsDefault(true);
        formV.setFormDef(form);
        form.addVersion(formV);
        // save the form
        studyManagerService.saveForm(form);
        return formV;
    }
    
    private FormData createFormData(FormDefVersion form, int number) throws Exception {
        FormData formData = new FormData();
        formData.setCreator(user);
        formData.setDateCreated(new Date());
        formData.setFormDefVersionId(form.getFormDefVersionId());
        formData.setData(XFormsFixture.getSampleFormModelData());
        formService.saveFormData(formData);
        return formData;
    }
    
    public class RandomExecutionThread extends Thread {

        long sleep = (long)(Math.random()*10000);
        int threadNumber;
        boolean successful = true;
        
        public RandomExecutionThread(int threadNumber) {
            this.threadNumber = threadNumber;
            start();
        }

        @Override
		public void run() {
            try {
                //System.out.println("started thread "+threadNumber+" with "+sleep+"ms until startup");
                sleep(sleep);
                createSpringSecurityContext();
                int selector = (int)(Math.random()*6);
                switch (selector) {
                    case 0 : 
                        List<FormDef> forms = formService.getForms();
                        System.out.println("getForms returned: "+(forms != null ? forms.size() : null)); 
                        break;
                    case 1 : 
                        forms = formService.getFormsForCurrentUser();
                        System.out.println("getFormsForCurrentUser returned: "+(forms != null ? forms.size() : null)); 
                        break;
                    case 2 : 
                        //List<FormData> formData = formService.getFormData(form.getFormDefVersionId());
                        //System.out.println("getFormData returned: "+(formData != null ? formData.size() : null)); 
                        break;
                    case 3 : 
                        Integer count = formService.getFormResponseCount(form.getFormDefVersionId());
                        System.out.println("getFormResponseCount returned: "+count);
                        break;
                    case 4 : 
                        ExportedFormDataList list = formService.getFormDataList("patientreg", 
                            new String[] { "patientid", "title", "firstname", "lastname", "sex", "birthdate", "weight", "height", "pregnant", "arvs", "continent", "country", "district", "village", "nokids", "starttime", "endtime"}, 
                            0, 50, "patientid", true);
                        System.out.println("getFormDataList returned: "+(list != null ? list.getExportedFormData().size() : null)); 
                        break;
                    case 5 :  
                        createFormData(form, 77);
                        System.out.println("createFormData");
                        break;
                    default : 
                        System.out.println("Something went wrong with the " +
                        		"random number generator ("+selector+")");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail(e.getMessage());
                successful = false;
            }
        }
        
        public boolean isSuccessful() {
            return successful;
        }
    }
}
