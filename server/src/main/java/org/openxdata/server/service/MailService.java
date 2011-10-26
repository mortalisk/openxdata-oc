package org.openxdata.server.service;


/**
 * Service which can be used to send emails
 * @author dagmar@cell-life.org
 */
public interface MailService {

	/**
	 * Sends an email (on a thread)
	 * @param to String email address to which the email is sent
	 * @param subject String subject of the email
	 * @param text String email body text
	 */
    void sendEmail(String to, String subject, String text);
}
