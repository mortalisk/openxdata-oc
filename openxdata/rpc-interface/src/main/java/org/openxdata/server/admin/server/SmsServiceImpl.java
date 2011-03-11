package org.openxdata.server.admin.server;

import java.util.List;

import javax.servlet.ServletException;

import org.openxdata.server.admin.client.service.SmsService;
import org.openxdata.server.admin.model.FormSmsArchive;
import org.openxdata.server.rpc.OxdPersistentRemoteService;
import org.springframework.web.context.WebApplicationContext;

/**
 * Default Implementation for the <code>SMS Interface.</code>
 */
public class SmsServiceImpl extends OxdPersistentRemoteService implements SmsService{

	private static final long serialVersionUID = 5348921955381512163L;
	private org.openxdata.server.service.SmsService smsService;
	
	public SmsServiceImpl(){
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
		WebApplicationContext ctx = getApplicationContext();
		smsService = (org.openxdata.server.service.SmsService)ctx.getBean("smsService");
	}
	
	@Override
	public List<FormSmsArchive> getFormSmsArchives() {
		return smsService.getFormSmsArchives();
	}
}
