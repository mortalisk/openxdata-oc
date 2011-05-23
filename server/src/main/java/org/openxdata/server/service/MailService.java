package org.openxdata.server.service;


/**
 * Service which can be used to send emails
 * @author dagmar@cell-life.org
 */
public interface MailService {

    void sendEmail(String to, String subject, String text);
}
