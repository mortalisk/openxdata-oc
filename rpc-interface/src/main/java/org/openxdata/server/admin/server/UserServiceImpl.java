package org.openxdata.server.admin.server;

import java.util.List;

import javax.servlet.ServletException;

import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.UserHeader;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.exception.UserNotFoundException;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;
import org.openxdata.server.rpc.OxdPersistentRemoteService;
import org.springframework.web.context.WebApplicationContext;

/**
 * Default Implementation for the <code>UserService Interface.</code>
 */
public class UserServiceImpl extends OxdPersistentRemoteService implements org.openxdata.server.admin.client.service.UserService {

	/** Generated serialization ID.*/
	private static final long serialVersionUID = 2600958435557581185L;
	
	private org.openxdata.server.service.UserService userService;
	private org.openxdata.server.service.AuthenticationService authenticationService;
	
	public UserServiceImpl() {}
	
	@Override
	public void init() throws ServletException {
		super.init();
		WebApplicationContext ctx = getApplicationContext();		
		userService = (org.openxdata.server.service.UserService)ctx.getBean("userService");
		authenticationService = (org.openxdata.server.service.AuthenticationService)ctx.getBean("authenticationService");
	}

	@Override
	public void deleteUser(User user) {
		userService.deleteUser(user);
	}

	@Override
	public List<User> getUsers() {
		return userService.getUsers();
	}

	@Override
	public User saveUser(User user) {
		return userService.saveUser(user);
	}
	
	@Override
	public void saveMyUser(User user) {
		userService.saveMyUser(user);
	}

	@Override
	public User getUser(String username) throws UserNotFoundException {
		return userService.findUserByUsername(username);
	}

	@Override
	public Boolean checkIfUserChangedPassword(User user) {
		return userService.checkIfUserChangedPassword(user);
	}
	
	@Override
	public void logout() {
		userService.logout();
	}

    @Override
	public User getLoggedInUser() {
        User myUser = userService.getLoggedInUser();
        return myUser;
    }

    @Override
	public User findUserByEmail(String email) throws UserNotFoundException {
        return userService.findUserByEmail(email);
    }
    
    @Override
	public void resetPassword(User user, int size) {
    	userService.resetPassword(user, size);
    }

    @Override
	public boolean validatePassword(User user) {
        return authenticationService.isValidUserPassword(user.getName(), user.getPassword());
    }
    
    @Override
	public User authenticate(String username, String password) {
        return authenticationService.authenticate(username, password);
    }

	
    @Override
	public void saveUsers(List<User> users) throws OpenXDataSecurityException {
    	userService.saveUsers(users);
	}

	
    @Override
	public void deleteUsers(List<User> users) throws OpenXDataSecurityException {
    	userService.deleteUsers(users);
	}

	@Override
    public PagingLoadResult<User> getUsers(PagingLoadConfig pagingLoadConfig)
            throws OpenXDataSecurityException {
	    return userService.getUsers(pagingLoadConfig);
    }

	@Override
    public String importUsers(String importFileContents)
            throws OpenXDataSecurityException {
	    return userService.importUsers(importFileContents);
    }

	@Override
    public PagingLoadResult<UserHeader> getMappedStudyUserNames(Integer studyId, PagingLoadConfig loadConfig) {
	    return userService.getMappedStudyUserNames(studyId, loadConfig);
    }

	@Override
    public PagingLoadResult<UserHeader> getUnmappedStudyUserNames(Integer studyId, PagingLoadConfig loadConfig) {
	    return userService.getUnmappedStudyUserNames(studyId, loadConfig);
    }
	
	@Override
    public void saveMappedStudyUserNames(Integer studyId, List<UserHeader> usersToAdd, List<UserHeader> usersToDelete) throws OpenXDataSecurityException {
		userService.saveMappedStudyUserNames(studyId, usersToAdd, usersToDelete);
    }

	@Override
    public PagingLoadResult<UserHeader> getMappedFormUserNames(Integer formId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException {
	    return userService.getMappedFormUserNames(formId, loadConfig);
    }

	@Override
    public PagingLoadResult<UserHeader> getUnmappedFormUserNames(Integer formId, PagingLoadConfig loadConfig) throws OpenXDataSecurityException {
	    return userService.getUnmappedFormUserNames(formId, loadConfig);
    }

	@Override
    public void saveMappedFormUserNames(Integer formId, List<UserHeader> usersToAdd, List<UserHeader> usersToDelete) throws OpenXDataSecurityException {
		userService.saveMappedFormUserNames(formId, usersToAdd, usersToDelete);
    }
}