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

import java.util.List;
import java.util.ArrayList;
import org.fcitmuk.communication.sms.SMSServer;
import org.junit.Before;
import org.junit.Test;
import org.openxdata.server.admin.model.FormSmsArchive;
import org.openxdata.server.admin.model.exception.UnexpectedException;
import org.openxdata.server.dao.SmsDAO;
import org.smslib.GatewayException;
import org.smslib.OutboundMessage;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Jonny Heggheim
 *
 */
public class SmsServiceTest {

    private SmsDAO daoMock;
    private SmsServiceImpl serviceImpl;
    private SMSServer smsServerMock;
    private OutboundMessage dummyMessage;

    @Before
    public void createServiceAndMocks() {
        serviceImpl = new SmsServiceImpl();
        daoMock = mock(SmsDAO.class);
        smsServerMock = mock(SMSServer.class);

        serviceImpl.setSmsDAO(daoMock);
        serviceImpl.setSmsServer(smsServerMock);

        dummyMessage = new OutboundMessage("a", "b");
    }

    @Test
    public void getFormSMSArchivesShouldJustForwardCallToDao() {
        List<FormSmsArchive> expected = new ArrayList<FormSmsArchive>();
        expected.add(new FormSmsArchive());

        when(daoMock.getFormSmsArchives()).thenReturn(expected);

        List<FormSmsArchive> actual = serviceImpl.getFormSmsArchives();
        assertEquals(expected, actual);

        verify(daoMock).getFormSmsArchives();
        verifyZeroInteractions(smsServerMock);
    }

    @Test
    public void sendMessageShouldDoNothingIfMessageIsNull() {
        serviceImpl.sendMessage(null);
        verifyZeroInteractions(daoMock, smsServerMock);
    }

    @Test
    public void testSendMessage() throws Exception {
        serviceImpl.sendMessage(dummyMessage);
        verify(smsServerMock).sendMessage(dummyMessage);
    }

    @Test(expected = UnexpectedException.class)
    public void sendMessageShouldThrowAnUnexpectedExceptionWhenGatewayException() throws Exception {
        doThrow(new GatewayException("test")).when(smsServerMock).sendMessage(dummyMessage);
        serviceImpl.sendMessage(dummyMessage);
    }

    @Test(expected = UnexpectedException.class)
    public void sendMessageShouldThrowAnUnexpectedExceptionWhenInterruptedException() throws Exception {
        doThrow(new InterruptedException()).when(smsServerMock).sendMessage(dummyMessage);
        serviceImpl.sendMessage(dummyMessage);
    }
}
