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
package org.openxdata.server.export.rdbms.task;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.dao.RdmsExporterDAO;
import org.openxdata.server.export.ExportConstants;
import org.openxdata.server.export.rdbms.engine.DataQuery;
import org.openxdata.server.service.DataExportService;
import org.openxdata.test.XFormsFixture;
import org.springframework.beans.factory.annotation.Autowired;

public class RdmsDataExportTaskTest {
	
    RdmsDataExportTask st;
    RdmsExporterDAO exporter;
    DataExportService exportService;
    
    @Autowired
    DataExportService oldExportService;
    
    @Before
    public void setup() {
    	st = new RdmsDataExportTask();
    	exportService = EasyMock.createMock(DataExportService.class);
    	st.setDataExportService(exportService);
        exporter = EasyMock.createMock(RdmsExporterDAO.class);
        st.setRdmsExporterDAO(exporter);
    }
    
    @After
    public void teardown() {
    	oldExportService = null;
    }

    @SuppressWarnings("unchecked")
	@Test
    public void testFirstTimeExport() throws Exception {
        // set up test data
        FormData formData = getFormData();
        FormDefVersion formDefVersion = getFormDefVersion();
        //List<String> updateSql = new ArrayList<String>();
        //updateSql.add("INSERT INTO patientreg \\(sex,starttime,openxdata_user_id,weight,openxdata_user_name,nokids,openxdata_form_data_id,lastname,firstname,country,picture,title,height,village,patientid,arvs,continent,birthdate,pregnant,Id,district,recordvideo,coughsound\\) VALUES \\(female,NULL,23,61,dagmar,1,1,gggg,ddd,uganda,NULL,mrs,6,kisenyi,123,azt,africa,1977-08-20,false,.*,kampala,NULL,NULL\\);");
        //updateSql.add("INSERT INTO kid \\(openxdata_user_id,openxdata_user_name,openxdata_form_data_id,kidage,Id,kidsex,kidname,ParentId\\) VALUES \\(23,dagmar,1,1,.*,female,clara,.*\\);");
        
        // set up mock
        EasyMock.expect(exporter.tableExists("", "patientreg")).andReturn(Boolean.FALSE);
        EasyMock.expect(exporter.tableExists("", "kid")).andReturn(Boolean.FALSE);
        exporter.executeSql((String)EasyMock.anyObject()); // difficult to specify the create table SQL parameter
        EasyMock.expectLastCall().times(2);
        // NB: because the tables were just created, no data existence check is performed
        exporter.executeSql((List<DataQuery>)EasyMock.anyObject()); // ditto here - insert data is difficult
        EasyMock.expectLastCall();
        EasyMock.replay(exporter);
        exportService.setFormDataExported(formData, ExportConstants.EXPORT_BIT_RDBMS);
        EasyMock.expectLastCall();
        EasyMock.replay(exportService);
        
        // run test
        st.exportFormData(formData, formDefVersion);
        
        // verify mock methods were called + assert test ran correctly
        EasyMock.verify(exporter);
        EasyMock.verify(exportService);
    }
    
    @Test
    public void testSecondTimeExportUpdate() throws Exception {
        // set up test data
        FormData formData = getFormData();
        FormDefVersion formDefVersion = getFormDefVersion();
        List<DataQuery> updateSql = new ArrayList<DataQuery>();
        Object[] param1 = new Object[] { Time.valueOf("10:44:44"), "female", Time.valueOf("09:44:44"), "0", 61.0, "dagmar", 1, "gggg", "ddd", "uganda", null, "mrs", 6, "kisenyi", "123", "azt", "africa" ,java.sql.Date.valueOf("1977-08-20"),"false","kampala", null, null, "1" };
        String sql1 = "UPDATE `patientreg` SET `endtime`=?,`sex`=?,`starttime`=?,`openxdata_user_id`=?,`weight`=?,`openxdata_user_name`=?,`nokids`=?,`lastname`=?,`firstname`=?,`country`=?,`picture`=?,`title`=?,`height`=?,`village`=?,`patientid`=?,`arvs`=?,`continent`=?,`birthdate`=?,`pregnant`=?,`district`=?,`recordvideo`=?,`coughsound`=? WHERE openxdata_form_data_id=?;";
        updateSql.add(new DataQuery(sql1, param1));
        Object[] param2 = new Object[] { "0", "dagmar", 1, "female", "clara", "1", "1" };
        String sql2 = "UPDATE `kid` SET `openxdata_user_id`=?,`openxdata_user_name`=?,`kidage`=?,`kidsex`=?,`kidname`=? WHERE openxdata_form_data_id=? AND parentId = (select Id from patientreg where openxdata_form_data_id=?);";
        updateSql.add(new DataQuery(sql2, param2));
        
        // set up mock
        EasyMock.expect(exporter.tableExists("", "patientreg")).andReturn(Boolean.TRUE);
        EasyMock.expect(exporter.tableExists("", "kid")).andReturn(Boolean.TRUE);
        // NB: because the tables are already existing, no create table methods, but now we have data existence check
        EasyMock.expect(exporter.dataExists(1, "patientreg")).andReturn(true);
        EasyMock.expect(exporter.dataExists(1, "kid")).andReturn(true);
        exporter.executeSql(updateSql);
        EasyMock.expectLastCall();
        EasyMock.replay(exporter);
        exportService.setFormDataExported(formData, ExportConstants.EXPORT_BIT_RDBMS);
        EasyMock.expectLastCall();
        EasyMock.replay(exportService);
        
        // run test
        st.exportFormData(formData, formDefVersion);
        
        // verify mock methods were called + assert test ran correctly
        EasyMock.verify(exporter);
        EasyMock.verify(exportService);
    }
    
    private FormData getFormData() {
        FormData formData = new FormData();
        formData.setData(XFormsFixture.getSampleFormModelData());
        formData.setFormDataId(1);
        formData.setCreator(new User("dagmar","password"));
        formData.setDateCreated(new Date());
        return formData;
    }
    
    private FormDefVersion getFormDefVersion() {
        FormDefVersion formDefVersion = new FormDefVersion();
        formDefVersion.setXform(XFormsFixture.getSampleForm());
        return formDefVersion;
    }
}
