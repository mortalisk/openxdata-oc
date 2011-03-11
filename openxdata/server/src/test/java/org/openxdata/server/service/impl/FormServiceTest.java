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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.openxdata.server.admin.model.ExportedDataType;
import org.openxdata.server.admin.model.ExportedFormData;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.dao.FormDataDAO;


/**
 * 
 * @author simon@cell-life.org
 *
 */
public class FormServiceTest {

	private static final String TEST_BINDING = "test-binding";

	@Test
	public void testPopulateFormDataWithNull(){
		FormServiceImpl service = new FormServiceImpl();
		ExportedFormData formData = new ExportedFormData();
		service.populateFormData(formData, null, TEST_BINDING);
		assertNull(formData.getExportedField(TEST_BINDING));
	}
	
	@Test
	public void testPopulateFormDataWithBigDecimal(){
		FormServiceImpl service = new FormServiceImpl();
		ExportedFormData formData = new ExportedFormData();
		BigDecimal expectedValue = BigDecimal.valueOf(5);
		service.populateFormData(formData, expectedValue, TEST_BINDING);
		ExportedDataType actual = formData.getExportedField(TEST_BINDING);
		assertEquals(expectedValue.doubleValue(),actual.getValue());
	}
	
	@Test
	public void testPopulateFormDataWithBigInteger(){
		FormServiceImpl service = new FormServiceImpl();
		ExportedFormData formData = new ExportedFormData();
		BigInteger expectedValue = BigInteger.valueOf(5);
		service.populateFormData(formData, expectedValue, TEST_BINDING);
		ExportedDataType actual = formData.getExportedField(TEST_BINDING);
		assertEquals(expectedValue.intValue(),actual.getValue());
	}
	
	@Test
	public void testPopulateFormDataWithBoolean(){
		FormServiceImpl service = new FormServiceImpl();
		ExportedFormData formData = new ExportedFormData();
		Boolean expectedValue = true;
		service.populateFormData(formData, expectedValue, TEST_BINDING);
		ExportedDataType actual = formData.getExportedField(TEST_BINDING);
		assertEquals(expectedValue,actual.getValue());
	}
	
	@Test
	public void testPopulateFormDataWithInteger(){
		FormServiceImpl service = new FormServiceImpl();
		ExportedFormData formData = new ExportedFormData();
		Integer expectedValue = Integer.valueOf(5);
		service.populateFormData(formData, expectedValue, TEST_BINDING);
		assertEquals(expectedValue,formData.getExportedField(TEST_BINDING).getValue());
	}
	
	@Test
	public void testPopulateFormDataWithString(){
		FormServiceImpl service = new FormServiceImpl();
		ExportedFormData formData = new ExportedFormData();
		String expectedValue = "some test string";
		service.populateFormData(formData, expectedValue, TEST_BINDING);
		ExportedDataType actual = formData.getExportedField(TEST_BINDING);
		assertEquals(expectedValue,actual.getValue());
	}
	
	@Test
	public void testPopulateFormDataWithDate(){
		FormServiceImpl service = new FormServiceImpl();
		ExportedFormData formData = new ExportedFormData();
		Date expectedValue = new Date();
		service.populateFormData(formData, expectedValue, TEST_BINDING);
		ExportedDataType actual = formData.getExportedField(TEST_BINDING);
		assertEquals(expectedValue,actual.getValue());
	}
	
	@Test
	public void testPopulateFormDataWithDouble(){
		FormServiceImpl service = new FormServiceImpl();
		ExportedFormData formData = new ExportedFormData();
		Double expectedValue = 0.23d;
		service.populateFormData(formData, expectedValue, TEST_BINDING);
		ExportedDataType actual = formData.getExportedField(TEST_BINDING);
		assertEquals(expectedValue,actual.getValue());
	}
	
	@Test
	public void testGetExportedFormData(){
		FormServiceImpl service = new FormServiceImpl();
		FormDataDAO formDataDAO = createMock(FormDataDAO.class);
		service.setFormDataDAO(formDataDAO);
		
		// create test data
		int numResponses = 3;
		String[] questionBindings = new String[] {"big-integer","big-decimal","integer","string","boolean","double","date"};
		List<Object[]> dataList = getResponseData(numResponses);

		// setup mock
		for (Object[] objects : dataList) {
			String object = (String) objects[0];
			Integer formDataId = Integer.valueOf(object);
			FormData formData = new FormData();
			formData.setFormDataId(formDataId);
			expect(formDataDAO.getFormData(formDataId)).andReturn(formData);
		}
		
		replay(formDataDAO);
		List<ExportedFormData> formDataList = service.getExportedFormData(questionBindings, dataList);
		
		// test returned data
		for (int i = 0; i < numResponses; i++) {
			ExportedFormData data = formDataList.get(i);
			Object[] expectedData = dataList.get(i);
			for (int j = 0; j < questionBindings.length; j++) {
				ExportedDataType exportedField = data.getExportedField(questionBindings[j]);
				Object object = expectedData[j+1];
				object = service.adaptUnsupportedDataTypes(object);
				assertEquals(object.toString(), exportedField.getValue().toString());
			}
		}
	}

	/**
	 * Generates and List<Oject[]> simulating data that would be returned from the database
	 * Returned List contains all supported data-types and uses random data
	 * 
	 * @param numResponses number of responses to generate
	 * @return
	 */
	private List<Object[]> getResponseData(int numResponses) {
		List<Object[]> responses = new ArrayList<Object[]>();
		for (int i = 0; i < numResponses; i++) {
			Object[] data = new Object[8];
			// formDataId as string
			data[0] = String.valueOf(Double.valueOf(Math.random()*Integer.MAX_VALUE).intValue());
			
			// data values
			data[1] = BigInteger.valueOf(Double.valueOf(Math.random()*Integer.MAX_VALUE).longValue());
			data[2] = BigDecimal.valueOf(Double.valueOf(Math.random()*Double.MAX_VALUE));
			data[3] = Double.valueOf(Math.random()*Integer.MAX_VALUE).intValue();
			data[4] = Double.valueOf(Math.random()*Double.MAX_VALUE).toString();
			data[5] = Boolean.valueOf(Math.random() > 0.5);
			data[6] = Double.valueOf(Math.random()*Double.MAX_VALUE);
			data[7] = new Date();
			responses.add(data);
		}
		return responses;
	}
	
}
