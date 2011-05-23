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
