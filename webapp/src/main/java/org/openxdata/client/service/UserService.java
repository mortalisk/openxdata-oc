package org.openxdata.client.service;

import java.util.List;

import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.exception.UserNotFoundException;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("user")
public interface UserService extends RemoteService {
    User authenticate(String username, String password) throws OpenXDataSecurityException;
    void saveUser(User user) throws OpenXDataSecurityException;
    void saveMyUser(User user) throws OpenXDataSecurityException;
    User getUser(String username) throws OpenXDataSecurityException, UserNotFoundException;
    boolean validatePassword(User user) throws OpenXDataSecurityException;
    User getLoggedInUser() throws OpenXDataSecurityException;
    User findUserByEmail(String email) throws OpenXDataSecurityException, UserNotFoundException;
    void resetPassword(User user, int size) throws OpenXDataSecurityException;
    List<User> getUsers() throws OpenXDataSecurityException;
    PagingLoadResult<User> getUsers(PagingLoadConfig pagingLoadConfig) throws OpenXDataSecurityException;
	Boolean checkIfUserChangedPassword(User user) throws OpenXDataSecurityException;
	void saveUsers(List<User> users) throws OpenXDataSecurityException;
	void deleteUsers(List<User> users) throws OpenXDataSecurityException;
}
