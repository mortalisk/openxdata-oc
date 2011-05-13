package org.openxdata.server;

import java.util.List;
import javax.servlet.ServletContext;

import org.openxdata.client.service.UserService;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.UserNotFoundException;
import org.openxdata.server.rpc.OxdPersistentRemoteService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class UserServiceImpl extends OxdPersistentRemoteService implements UserService {

    private static final long serialVersionUID = -5479724182957388216L;

    private org.openxdata.server.service.UserService userService;

	private org.openxdata.server.service.AuthenticationService authenticationService;

	@Override
	public void saveUser(User user) {
		getUserService().saveUser(user);
	}    
    
    @Override
	public User getLoggedInUser() {
        User myUser = getUserService().getLoggedInUser();
        return myUser;
    }

    @Override
	public User getUser(String username) throws UserNotFoundException {
        return getUserService().findUserByUsername(username);
    }

    @Override
	public User findUserByEmail(String email) throws UserNotFoundException {
        return getUserService().findUserByUsername(email);
    }
    
    @Override
	public void resetPassword(User user, int size) {
        getUserService().resetPassword(user, size);
    }

    @Override
	public boolean validatePassword(User user) {
        return getAuthenticationService().isValidUserPassword(user.getName(), user.getPassword());
    }
    
    @Override
	public User authenticate(String username, String password) {
        return getAuthenticationService().authenticate(username, password);
    }

    private org.openxdata.server.service.AuthenticationService getAuthenticationService() {
    	ServletContext sctx = this.getServletContext();
        WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(sctx);
        authenticationService = (org.openxdata.server.service.AuthenticationService)ctx.getBean("authenticationService");
        return authenticationService;
	}

	private org.openxdata.server.service.UserService getUserService() {
        ServletContext sctx = this.getServletContext();
        WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(sctx);
        userService = (org.openxdata.server.service.UserService)ctx.getBean("userService");
        return userService;
    }

    @Override
    public List<User> getUsers() {
        return getUserService().getUsers();
    }
}