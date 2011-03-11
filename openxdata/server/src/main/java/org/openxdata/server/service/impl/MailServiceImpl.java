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

import java.util.Properties;

import org.openxdata.server.OpenXDataPropertyPlaceholderConfigurer;
import org.openxdata.server.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author dagmar@cell-life.org
 */
@Transactional
@Service("mailService")
public class MailServiceImpl implements MailService {

    @Autowired
    private MailSender mailSender;
    @Autowired
    private OpenXDataPropertyPlaceholderConfigurer appSettings;
    private String fromEmailAddress = null;

    public void init() {
        Properties props = appSettings.getResolvedProps();
        fromEmailAddress = props.getProperty("mailSender.from");
    }

    @Override
    public void sendEmail(String to, String subject, String text) throws MailException {
        if (fromEmailAddress == null) {
            init();
        }
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        msg.setFrom(fromEmailAddress);
        mailSender.send(msg);
    }
}
