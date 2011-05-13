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
package org.openxdata.server.export.rdbms.engine;

import java.sql.Time;
import java.util.Date;
import java.util.List;

import org.junit.Before;

import org.junit.Test;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.User;
import org.openxdata.test.XFormsFixture;

import static org.junit.Assert.*;

public class DataBuilderTest {
    private FormData formData;

    @Before
    public void setupFormData() {
        formData = new FormData();
        formData.setFormDataId(1);
        formData.setCreator(new User("dagmar","password"));
        formData.setDateCreated(new Date());        
    }

    @Test
    public void testGetUpdateDataSql() throws Exception {
        formData.setData(XFormsFixture.getSampleFormModelData());
        List<DataQuery> stmt = RdmsEngine.getDataSql(XFormsFixture.getSampleForm(), formData, true);
        // check return value is what you expected
        assertEquals("Two sql statements", 2, stmt.size());
        String actualSql1 = stmt.get(0).getSql();
        String actualSql2 = stmt.get(1).getSql();        
        assertEquals("First sql statement for patientreg", 
                "UPDATE `patientreg` SET `endtime`=?,`sex`=?,`starttime`=?,`openxdata_user_id`=?,`weight`=?,`openxdata_user_name`=?,`nokids`=?,`lastname`=?,`firstname`=?,`country`=?,`picture`=?,`title`=?,`height`=?,`village`=?,`patientid`=?,`arvs`=?,`continent`=?,`birthdate`=?,`pregnant`=?,`district`=?,`recordvideo`=?,`coughsound`=? WHERE openxdata_form_data_id=?;",
                actualSql1);
        assertEquals("Second sql statement for kid", 
                "UPDATE `kid` SET `openxdata_user_id`=?,`openxdata_user_name`=?,`kidage`=?,`kidsex`=?,`kidname`=? WHERE openxdata_form_data_id=? AND parentId = (select Id from patientreg where openxdata_form_data_id=?);",
                actualSql2);
        List<Object> actualParam1 = stmt.get(0).getParameters();
        Object[] param1 = new Object[] { Time.valueOf("10:44:44"), "female", Time.valueOf("09:44:44"), "0", 61.0, "dagmar", 1, "gggg", "ddd", "uganda", null, "mrs", 6, "kisenyi", "123", "azt", "africa", java.sql.Date.valueOf("1977-08-20"),"false","kampala", null, null, "1" };
        for (int i=0; i<actualParam1.size(); i++) {
            assertEquals("First param list for patientreg, param="+param1[i], param1[i], actualParam1.get(i));
        }
        List<Object> actualParam2 = stmt.get(1).getParameters();
        Object[] param2 = new Object[] { "0", "dagmar", 1, "female", "clara", "1", "1" };
        for (int i=0; i<actualParam2.size(); i++) {
            assertEquals("Second param list for kid, param="+param2[i], param2[i], actualParam2.get(i));
        }
    }
    
    @Test
    public void testGetEscapedSql() throws Exception {
        formData.setData(XFormsFixture.getSampleFormModelDataWithSpecialChars());
        // run test
        List<DataQuery> stmt = RdmsEngine.getDataSql(XFormsFixture.getSampleForm(), formData, true);
        // check return values
        assertEquals("Two sql statements", 2, stmt.size());
        String actualSql1 = stmt.get(0).getSql();
        String sql1 = String.format(Constants.SQL_UPDATE,
                "`patientreg`",
                //"sex='female',starttime=NULL,openxdata_user_id=0,weight=61,openxdata_user_name='dagmar',nokids=1,lastname='gggg',firstname='ddd',country='uganda',picture=NULL,title='mrs',height=6,village='kisenyi',patientid='123',arvs='azt',continent='africa',birthdate=1977-08-20,pregnant='false',district='kampala',recordvideo=NULL,coughsound=NULL",
                "`endtime`=?,`sex`=?,`starttime`=?,`openxdata_user_id`=?,`weight`=?,`openxdata_user_name`=?,`nokids`=?,`lastname`=?,`firstname`=?,`country`=?,`picture`=?,`title`=?,`height`=?,`village`=?,`patientid`=?,`arvs`=?,`continent`=?,`birthdate`=?,`pregnant`=?,`district`=?,`recordvideo`=?,`coughsound`=?",
                "openxdata_form_data_id=?");
        assertEquals("First sql statement for patientreg", sql1, actualSql1);
        List<Object> actualParam1 = stmt.get(0).getParameters();
        Object[] param1 = new Object[] { Time.valueOf("10:44:44"), "female", Time.valueOf("09:44:44"), "0", 61.0, "dagmar", 1, "gggg's", "dd;dd", "uganda", null, "mrs", 6, "kisenyi", "123", "azt", "africa", java.sql.Date.valueOf("1977-08-20"),"false","kampala", null, null, "1" };
        for (int i=0; i<actualParam1.size(); i++) {
            assertEquals("First param list for patientreg, param="+param1[i], param1[i], actualParam1.get(i));
        }
    }
    
    @Test
    public void testGetCreateDataSql() throws Exception {
        formData.setData(XFormsFixture.getSampleFormModelData());
        formData.setCreator(new User(23, "dagmar","password", "salt"));

        List<DataQuery> stmt = RdmsEngine.getDataSql(XFormsFixture.getSampleForm(), formData, false);
        assertEquals("Two sql statements", 2, stmt.size());
        String actualSql1 = stmt.get(0).getSql();
        String actualSql2 = stmt.get(1).getSql();
        String patientReqSql = "INSERT INTO `patientreg` (`endtime`,`sex`,`starttime`,`openxdata_user_id`,`weight`,`openxdata_user_name`,`nokids`,`openxdata_form_data_id`,`lastname`,`firstname`,`country`,`picture`,`title`,`height`,`village`,`patientid`,`arvs`,`continent`,`birthdate`,`pregnant`,`Id`,`district`,`recordvideo`,`coughsound`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        Object[] param1 = new Object[] { Time.valueOf("10:44:44"), "female", Time.valueOf("09:44:44"), "23", 61.0, "dagmar", 1, "1", "gggg", "ddd", "uganda", null, "mrs", 6, "kisenyi", "123", "azt", "africa", java.sql.Date.valueOf("1977-08-20"),"false","*","kampala", null, null};
        List<Object> actualParam1 = stmt.get(0).getParameters();
        for (int i=0; i<actualParam1.size(); i++) {
            if (param1[i] == null || !param1[i].equals("*"))
                assertEquals("First param list for patientreg, param="+param1[i], param1[i], actualParam1.get(i));
        }
        assertEquals("PatientReg insert is ok", patientReqSql, actualSql1);
        String kidSql = "INSERT INTO `kid` (`openxdata_user_id`,`openxdata_user_name`,`openxdata_form_data_id`,`kidage`,`Id`,`kidsex`,`kidname`,ParentId) VALUES (?,?,?,?,?,?,?,?);";
        assertEquals("Kid insert is ok", kidSql, actualSql2);
        List<Object> actualParam2 = stmt.get(1).getParameters();
        Object[] param2 = new Object[] { "23", "dagmar", "1", 1, "*", "female", "clara", "*" };
        for (int i=0; i<actualParam2.size(); i++) {
            if (param2[i] == null || !param2[i].equals("*"))
                assertEquals("Second param list for kid, param="+param2[i], param2[i], actualParam2.get(i));
        }
    }

}