package org.openxdata.server.service.impl;

import java.io.IOException;
import java.util.List;

import org.fcitmuk.communication.sms.SMSServer;
import org.openxdata.server.admin.model.FormSmsArchive;
import org.openxdata.server.admin.model.exception.UnexpectedException;
import org.openxdata.server.dao.SmsDAO;
import org.openxdata.server.service.SmsService;
import org.smslib.GatewayException;
import org.smslib.OutboundMessage;
import org.smslib.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("smsService")
// FIXME: security for SMS?
public class SmsServiceImpl implements SmsService {

    @Autowired
    private SmsDAO smsDAO;
    @Autowired
    private SMSServer smsServer;

    void setSmsDAO(SmsDAO dao) {
        this.smsDAO = dao;
    }

    void setSmsServer(SMSServer smsServer) {
        this.smsServer = smsServer;
    }

    @Override
	@Transactional(readOnly = true)
    public List<FormSmsArchive> getFormSmsArchives() {
        return smsDAO.getFormSmsArchives();
    }

    @Override
	public void sendMessage(OutboundMessage msg) {
        try {
            if (msg == null) {
                return;
            }
            smsServer.sendMessage(msg);
        } catch (InterruptedException ex) {
            handleSmsException(ex);
        } catch (IOException ex) {
            handleSmsException(ex);
        } catch (TimeoutException ex) {
            handleSmsException(ex);
        } catch (GatewayException ex) {
            handleSmsException(ex);
        }
    }

    private void handleSmsException(Exception exception) {
        throw new UnexpectedException("Failed to send SMS message", exception);
    }
}
