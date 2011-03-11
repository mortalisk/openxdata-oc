package org.openxdata.server.servlet;

import com.dumbster.smtp.SmtpMessage;
import java.util.Iterator;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import org.springframework.test.context.ContextConfiguration;
import com.dumbster.smtp.SimpleSmtpServer;
import java.util.Locale;
import org.apache.commons.io.output.NullOutputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailSender;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Jonny Heggheim
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:openxdata-test-applicationContext.xml"})
public class ResetPasswordServletTest {

    private int SMTP_PORT = 5555;
    private Locale locale = Locale.ENGLISH;
    private SimpleSmtpServer smtpServer;
    private ResetPasswordServlet servlet;
    private UserService userServiceMock;
    private PrintWriter ignoredOutput;
    private MessageSource messageSourceMock;
    @Autowired
    private MailSender mailSender;

    @Before
    public void startSmtpServer() {
        smtpServer = SimpleSmtpServer.start(SMTP_PORT);
    }

    @After
    public void stopSmtpServer() {
        smtpServer.stop();
    }

    @Before
    public void initServlet() throws ServletException {
        servlet = new ResetPasswordServlet();
        userServiceMock = mock(UserService.class);
        messageSourceMock = mock(MessageSource.class);
        servlet.setUserService(userServiceMock);
        servlet.setMessageSource(messageSourceMock);
        servlet.setMailSender(mailSender);
        ignoredOutput = new PrintWriter(new NullOutputStream());
    }

    @Test
    public void normalTest() {
        User dummyUser = new User("Dummy", "password");
        String to = "to@user.com";
        String from = "from@openxdata.org";

        userServiceMock.resetPassword(eq(dummyUser), anyInt());
        setMessage("resetPasswordEmailSubject", "subject");
        setMessage("resetPasswordEmail", "body");
        servlet.resetPasswordAndSendEmail(dummyUser, to, from, locale, ignoredOutput);
        assertEquals(1, smtpServer.getReceivedEmailSize());
        Iterator<?> emailIter = smtpServer.getReceivedEmail();
        SmtpMessage email = (SmtpMessage) emailIter.next();
        assertEquals("subject", email.getHeaderValue("Subject"));
        assertEquals("body", email.getBody());
    }

    private void setMessage(String key, String message) {
        when(messageSourceMock.getMessage(eq(key), anyCollection().toArray(), eq(locale))).thenReturn(message);
    }
}
