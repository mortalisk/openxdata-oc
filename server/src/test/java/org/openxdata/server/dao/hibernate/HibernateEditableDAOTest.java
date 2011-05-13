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
package org.openxdata.server.dao.hibernate;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.openxdata.server.admin.model.FormDataHeader;
import org.openxdata.server.dao.EditableDAO;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Jonny Heggheim
 */
public class HibernateEditableDAOTest extends BaseContextSensitiveTest {

    @Autowired
    private EditableDAO dao;

    @Test
    @Transactional(readOnly = true)
    public void getFormDataWithEmptyInput() {
        dao.getFormData(null, null, null, null);
    }
    
    @Test
    @Transactional(readOnly = true)
    public void getFormDataForAFormDef() {
        dao.getFormData(12, null, null, null);
    }
    
    @Test
    @Transactional(readOnly = true)
    public void getFormDataForUser() {
        dao.getFormData(12, 12, null, null);
    }
    
    @Test
    @Transactional(readOnly = true)
    public void getFormDataBetweenDates() {
        List<FormDataHeader> results = dao.getFormData(12, null, new Date(), new Date());
        for (FormDataHeader result : results) {
        	System.out.println("result = "+result.getFormDataId());
        }
    }

    @Test
    @Transactional(readOnly = true)
    public void getFormDataWithNonEmptyInput() {
        dao.getFormData(12, 12, new Date(), new Date());
    }
}
