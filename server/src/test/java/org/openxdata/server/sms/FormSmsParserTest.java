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
package org.openxdata.server.sms;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.openxdata.server.admin.model.exception.OpenXDataParsingException;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.exception.OpenXDataValidationException;
import org.openxdata.server.service.AuthenticationService;
import org.openxdata.server.service.FormDownloadService;
import org.openxdata.server.service.SettingService;
import org.openxdata.server.service.UserService;

/**
 * Tests parsing of sms text to fill its corresponding xform.
 * 
 * @author daniel
 * @author Jonny Heggheim
 *
 */
public class FormSmsParserTest {

    private final static String SENDER = "+256782380638";
    private SettingService settingServiceMock;
    private FormSmsParser parser;
    private UserService userServiceMock;
    private AuthenticationService authenticationMock;
    private FormDownloadService formDownloadMock;

    @Before
    public void createFormParserAndMocks() {
        parser = new FormSmsParser();
        settingServiceMock = mock(SettingService.class);
        userServiceMock = mock(UserService.class);
        authenticationMock = mock(AuthenticationService.class);
        formDownloadMock = mock(FormDownloadService.class);

        parser.setSettingService(settingServiceMock);
        parser.setUserService(userServiceMock);
        parser.setAuthenticationService(authenticationMock);
        parser.setFormDownloadService(formDownloadMock);
    }

    @Test(expected = OpenXDataParsingException.class)
    public void sms2FormData_shouldThrowExceptionForEmptySms() throws Exception {
        parser.sms2FormData(SENDER, "");
    }

    @Test(expected = OpenXDataValidationException.class)
    public void sms2FormData_shouldThrowMissingSpaceAfterUserNameException() throws Exception {
        parser.sms2FormData(SENDER, "guyzb");
    }

    @Test(expected = OpenXDataValidationException.class)
    public void sms2FormData_shouldThrowMissingSpaceAfterPasswordException() throws Exception {
        parser.sms2FormData(SENDER, "guyzb daniel123");
    }

    @Test(expected = OpenXDataSecurityException.class)
    public void sms2FormData_shouldThrowAccessDeniedException() throws Exception {
        setSettingValue("smsValidateNamePassword", "true");
        parser.sms2FormData(SENDER, "guyzb daniel124 newform");
    }

    @Test(expected = OpenXDataSecurityException.class)
    public void sms2FormData_shouldThrowNumberNotAttachedToUserException() throws Exception {
        setSettingValue("smsValidateNamePassword", "false");
        setSettingValue("smsValidatePhoneNo", "true");

        parser.sms2FormData(SENDER, "+256772330330 message text");
    }

    private void setSettingValue(String name, String value) throws Exception {
        when(settingServiceMock.getSetting(name)).thenReturn(value);
    }
}
