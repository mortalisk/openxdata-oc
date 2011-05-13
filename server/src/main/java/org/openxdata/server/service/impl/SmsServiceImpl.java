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
