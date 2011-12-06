package org.openxdata.server.service.impl;

import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.openxdata.server.OpenXDataPropertyPlaceholderConfigurer;
import org.openxdata.server.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service("mailService")
public class MailServiceImpl implements MailService {
	
	private static Logger log = LoggerFactory.getLogger(MailServiceImpl.class);

    @Autowired
    private MailSender mailSender;
    @Autowired
    private OpenXDataPropertyPlaceholderConfigurer appSettings;
    private String fromEmailAddress = null;
    
    /** thread pool to manage the sending email threads - this ensures that too many threads don't get executed at once
     * (for example when 10,000 users are imported) */
    ThreadPoolExecutor tpe = new ThreadPoolExecutor(5,10, 3600, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

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
        
        tpe.execute(new SendEmailThread(msg));
    }
    
    /**
	 * Runnable class to handle sending the email (in case it takes
	 * some time to run)
	 */
	class SendEmailThread implements Runnable {
	    SimpleMailMessage msg;
	    public SendEmailThread(SimpleMailMessage msg) {
	        this.msg = msg;
	    }
        @Override
		public void run() {
        	if (log.isDebugEnabled()) {
        		log.debug("Sending email message from:"+msg.getFrom()+" to:"+msg.getTo()[0]+" subject:"+msg.getSubject());
        	}
        	mailSender.send(msg);
        }
	}
}
