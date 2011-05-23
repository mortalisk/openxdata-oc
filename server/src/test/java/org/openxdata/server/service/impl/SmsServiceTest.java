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
