/*
 *  Licensed to the OpenXdata Foundation (OXDF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The OXDF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at <p>
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  </p>
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 *  Copyright 2010 http://www.openxdata.org.
 */
package org.openxdata.server.servlet;

import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;
import org.openxdata.server.export.DataExport;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static org.springframework.mock.web.MockHttpServletResponse.SC_BAD_REQUEST;
import static org.springframework.mock.web.MockHttpServletResponse.SC_OK;

/**
 *
 * @author Jonny Heggheim
 */
public class DataExportServletTest {

    private static final String CONTENT = "data1,data2";
    private static final Calendar APRIL_01_2010 = new GregorianCalendar(2010, 03, 01);
    private static final Calendar APRIL_15_2010 = new GregorianCalendar(2010, 03, 15);
    private static final int FORM_ID = 55;
    private static final int USER_ID = 101;

    private DataExportServlet servlet;
    private DataExport dataExportMock;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private final IAnswer<Object> responseAnswer = new IAnswer<Object>() {
        @Override
		public Object answer() throws Throwable {
            response.getWriter().write(CONTENT);
            return null;
        }
    };

    @Before
    public void createMocksAndServlet() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        dataExportMock = EasyMock.createMock(DataExport.class);
        servlet = new DataExportServlet();
        servlet.setDataExport(dataExportMock);

        String from = "" + APRIL_01_2010.getTimeInMillis();
        String to = "" + APRIL_15_2010.getTimeInMillis();
        setParameters("" + FORM_ID, from, to, "" + USER_ID, "file");
    }

    private void setParameters(String formId, String fromDate, String toDate, String userId, String filename) {
        request.setParameter("formId", formId);
        request.setParameter("fromDate", fromDate);
        request.setParameter("toDate", toDate);
        request.setParameter("filename", filename);
        request.setParameter("userId", userId);
    }

    @SuppressWarnings("unchecked")
	private void assertResponseHeaders(String filename) {
        List<String> cacheControl = response.getHeaders("Cache-Control");
        assertTrue(cacheControl.contains("no-cache"));
        assertTrue(cacheControl.contains("no-store"));

        assertEquals("no-cache", response.getHeader("Pragma"));
        assertEquals(-1L, response.getHeader("Expires"));

        assertEquals("text/csv", response.getContentType());

        String disposition = (String) response.getHeader("Content-Disposition");
        assertNotNull("Response should contain a Content-Disposition header", disposition);
        assertTrue(disposition.contains("attachment"));
        assertTrue(disposition.contains("filename=" + filename));
    }

    @Test
    public void givenNoFormIdThenContentShouldHaveAnErrorAndStatusBadRequest() throws Exception {
        request.setParameter("formId", "");
        replay(dataExportMock);

        servlet.doGet(request, response);

        assertFalse(response.getContentAsString().isEmpty());
        assertEquals(SC_BAD_REQUEST, response.getStatus());
        verify(dataExportMock);
    }

    @Test
    public void givenNoDateThenContentShouldReturnForAllDatesAndStatusOk() throws Exception {
        request.setParameter("fromDate", "");
        request.setParameter("toDate", "");

        dataExportMock.export(response.getWriter(), FORM_ID, null, null, USER_ID);
        expectLastCall().andAnswer(responseAnswer);
        replay(dataExportMock);

        servlet.doGet(request, response);

        assertEquals(CONTENT, response.getContentAsString());
        assertEquals(SC_OK, response.getStatus());
        assertResponseHeaders("file.csv");
        verify(dataExportMock);
    }

    @Test
    public void givenNoUserIdThenContentShouldReturnForAllUsersAndStatusOk() throws Exception {
        request.setParameter("userId", "");

        PrintWriter writer = EasyMock.eq(response.getWriter());
        Integer formId = EasyMock.eq(FORM_ID);
        Date from = EasyMock.eq(APRIL_01_2010.getTime());
        Date to = EasyMock.gt(APRIL_15_2010.getTime()); //add one day...?
        Integer userId = EasyMock.isNull();

        dataExportMock.export(writer, formId, from, to, userId);
        expectLastCall().andAnswer(responseAnswer);
        replay(dataExportMock);

        servlet.doGet(request, response);

        assertEquals(CONTENT, response.getContentAsString());
        assertEquals(SC_OK, response.getStatus());
        assertResponseHeaders("file.csv");
        verify(dataExportMock);
    }

    @Test
    public void givenNoFilenameThenContentShouldContainTheResultAndStatusOkAndFilenameDataexport() throws Exception {
        request.setParameter("filename", "");

        PrintWriter writer = EasyMock.eq(response.getWriter());
        Integer formId = EasyMock.eq(FORM_ID);
        Date from = EasyMock.eq(APRIL_01_2010.getTime());
        Date to = EasyMock.gt(APRIL_15_2010.getTime()); //add one day...?
        Integer userId = EasyMock.eq(USER_ID);

        dataExportMock.export(writer, formId, from, to, userId);
        expectLastCall().andAnswer(responseAnswer);
        replay(dataExportMock);

        servlet.doGet(request, response);

        assertEquals(CONTENT, response.getContentAsString());
        assertEquals(SC_OK, response.getStatus());
        assertResponseHeaders("dataexport.csv");
        verify(dataExportMock);
    }
}
