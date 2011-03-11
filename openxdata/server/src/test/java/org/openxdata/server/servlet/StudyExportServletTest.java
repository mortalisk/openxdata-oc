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
package org.openxdata.server.servlet;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openxdata.server.OpenXDataConstants;
import org.openxdata.server.export.FormExport;
import org.openxdata.server.export.StudyExport;
import org.openxdata.server.export.VersionExport;
import org.openxdata.server.service.DataExportService;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 *
 * @author Jonny Heggheim
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({StudyExport.class, FormExport.class, VersionExport.class})
public class StudyExportServletTest {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private StudyExportServlet servlet;
    private DataExportService dataExportMock;
    private static final String EXPECTED_CONTENT = "<export>xml</export>";

    @Before
    public void createServletAndMocks() {
        PowerMock.mockStatic(StudyExport.class);
        PowerMock.mockStatic(FormExport.class);
        PowerMock.mockStatic(VersionExport.class);
        dataExportMock = PowerMock.createMock(DataExportService.class);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        servlet = new StudyExportServlet();
        servlet.setDataExportService(dataExportMock);
    }

    private void setRequestParameters(String id, String filename, String type) {
        request.setParameter("id", id);
        request.setParameter("filename", filename);
        request.setParameter("type", type);
    }

    @SuppressWarnings("unchecked")
	private void assertResponseHeaders(String filename) {
        List<String> cacheControl = response.getHeaders("Cache-Control");
        assertTrue(cacheControl.contains("no-cache"));
        assertTrue(cacheControl.contains("no-store"));

        assertEquals("no-cache", response.getHeader("Pragma"));
        assertEquals(-1L, response.getHeader("Expires"));

        assertEquals(OpenXDataConstants.HTTP_HEADER_CONTENT_TYPE_XML, response.getContentType());

        String disposition = (String) response.getHeader("Content-Disposition");
        assertNotNull("Response should contain a Content-Disposition header", disposition);
        assertTrue(disposition.contains("attachment"));
        assertTrue(disposition.contains("filename=" + filename));
    }

    @Test
    public void givenNoParametersThenResultShouldContainAnErrorAndResponseBadRequest() throws Exception {
        servlet.doGet(request, response);

        //No mocks should not be called
        PowerMock.replayAll();

        assertFalse("Content should contain an error message", response.getContentAsString().isEmpty());
        assertEquals(SC_BAD_REQUEST, response.getStatus());

        PowerMock.verifyAll();
    }

    @Test
    public void givenWrongTypeThenResultShouldContainAnErrorAndResponseBadRequest() throws Exception {
        setRequestParameters("1", "file2", "xxx");

        PowerMock.replayAll();

        servlet.doGet(request, response);
        assertFalse("Content should contain an error message", response.getContentAsString().isEmpty());
        assertEquals(SC_BAD_REQUEST, response.getStatus());
        
        PowerMock.verifyAll();
    }

    @Test
    public void givenNoTypeThenResultShouldContainAnErrorAndResponseBadRequest() throws Exception {
        setRequestParameters("1", "file3", "");

        PowerMock.replayAll();

        servlet.doGet(request, response);
        assertFalse("Content should contain an error message", response.getContentAsString().isEmpty());
        assertEquals(SC_BAD_REQUEST, response.getStatus());
        
        PowerMock.verifyAll();
    }

    @Test
    public void givenEmptyFilenameForStudyThenResultShouldContainStudyAndResponseOkAndFilenameDataexport() throws Exception {
        setRequestParameters("1", "", "study");

        EasyMock.expect(dataExportMock.getStudyDef(1)).andReturn(null);
        EasyMock.expect(StudyExport.export(null)).andReturn(EXPECTED_CONTENT);
        PowerMock.replayAll();

        servlet.doGet(request, response);

        assertEquals(EXPECTED_CONTENT, response.getContentAsString());
        assertEquals(SC_OK, response.getStatus());
        assertResponseHeaders("dataexport.xml");
        PowerMock.verifyAll();
    }

    @Test
    public void testExportStudy() throws Exception {
        setRequestParameters("1", "file", "study");

        EasyMock.expect(dataExportMock.getStudyDef(1)).andReturn(null);
        EasyMock.expect(StudyExport.export(null)).andReturn(EXPECTED_CONTENT);
        PowerMock.replayAll();

        servlet.doGet(request, response);

        assertEquals(EXPECTED_CONTENT, response.getContentAsString());
        assertEquals(SC_OK, response.getStatus());
        assertResponseHeaders("file.xml");
        PowerMock.verifyAll();
    }

    @Test
    public void testExportForm() throws Exception {
        setRequestParameters("10", "file", "form");

        //Just pass null into FormExport
        EasyMock.expect(dataExportMock.getFormDef(10)).andReturn(null);
        EasyMock.expect(FormExport.export(null)).andReturn(EXPECTED_CONTENT);
        PowerMock.replayAll();

        servlet.doGet(request, response);

        assertEquals(EXPECTED_CONTENT, response.getContentAsString());
        assertEquals(SC_OK, response.getStatus());
        assertResponseHeaders("file.xml");
        PowerMock.verifyAll();
    }

    @Test
    public void testExportVersion() throws Exception {
        setRequestParameters("99", "version99", "version");

        //Just pass null into VersionExport
        EasyMock.expect(dataExportMock.getFormDefVersion(99)).andReturn(null);
        EasyMock.expect(VersionExport.export(null)).andReturn(EXPECTED_CONTENT);
        PowerMock.replayAll();

        servlet.doGet(request, response);

        assertEquals(EXPECTED_CONTENT, response.getContentAsString());
        assertEquals(SC_OK, response.getStatus());
        assertResponseHeaders("version99.xml");
        PowerMock.verifyAll();
    }
}
