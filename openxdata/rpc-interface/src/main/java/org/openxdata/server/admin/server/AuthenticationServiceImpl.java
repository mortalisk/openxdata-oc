package org.openxdata.server.admin.server;

import javax.servlet.ServletException;

import org.openxdata.server.admin.model.User;
import org.openxdata.server.rpc.OxdPersistentRemoteService;
import org.springframework.web.context.WebApplicationContext;

public class AuthenticationServiceImpl extends OxdPersistentRemoteService implements
org.openxdata.server.admin.client.service.AuthenticationService {
	
	private static final long serialVersionUID = 1L;
	
	private org.openxdata.server.service.AuthenticationService authenticationService;

	@Override
	public void init() throws ServletException {
		super.init();
		WebApplicationContext ctx = getApplicationContext();
		authenticationService = (org.openxdata.server.service.AuthenticationService)ctx.getBean("authenticationService");
	}
	
	@Override
	public User authenticate(String username, String password) {
		return authenticationService.authenticate(username, password);
	}
	
	@Override
	public Boolean isValidUserPassword(String username, String password) {
		return authenticationService.isValidUserPassword(username, password);
	}
}
