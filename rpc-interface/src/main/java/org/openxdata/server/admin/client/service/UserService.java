package org.openxdata.server.admin.client.service;

import java.util.List;

import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.exception.UserNotFoundException;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Defines the client side contract for the User Service.
 */
@RemoteServiceRelativePath("user")
public interface UserService extends RemoteService {
	
    /**
     * Gets a user based on their user name
     * @param username String login name
     * @return User, or null if no match found
     * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
     * @throws UserNotFoundException if the user is not found in the system
     */
    User getUser(String username) throws OpenXDataSecurityException, UserNotFoundException;  
	
	/**
	 * Saves a new and modified users to the database.
	 * 
	 * @param user the user to save.
	 * @return user saved with id populated
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	User saveUser(User user) throws OpenXDataSecurityException;
	
	/**
	 * Saves a modified user to the database.
	 * Must be the user of the currently logged in user
	 * 
	 * @param user the user to save.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void saveMyUser(User user) throws OpenXDataSecurityException;
	
	/**
	 * Persists a given list of Users to the database.
	 * 
	 * @param users List of Users to persist.
	 */
	void saveUsers(List<User> users) throws OpenXDataSecurityException;
    
	/**
     * Gets a list of users in the database.
     * @deprecated Please use the paged list instead
     * 
     * @return the user list.
     * @throws OpenXDataSecurityException For any <tt>exception</tt> that occurs on the <tt>service layer.</tt>
     */
	@Deprecated
	List<User> getUsers() throws OpenXDataSecurityException;
	
	PagingLoadResult<User> getUsers(PagingLoadConfig pagingLoadConfig) throws OpenXDataSecurityException;
	
	/**
	 * Removes a user from the database.
	 * 
	 * @param user the user to remove.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt> 
	 */
	void deleteUser(User user) throws OpenXDataSecurityException;

	/**
	 * Deletes a given list of Users from the database.
	 * 
	 * @param users List of Users to delete.
	 */
	void deleteUsers(List<User> users) throws OpenXDataSecurityException;
	
	/**
	 * Ascertains if the Administrator changed the default password on initial login.
	 * 
	 * @return <code>True only and only if(checkIfUserChangedPassword(user) == true)</code> 
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt> 
	 */
	Boolean checkIfUserChangedPassword(User user) throws OpenXDataSecurityException;
	
	/**
	 * Logs the user out of the system
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void logout() throws OpenXDataSecurityException;
	
	/**
	 * Authenticates the user
	 * @param username String username
	 * @param password String hashed password
	 * @return User or null if not authenticated
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	User authenticate(String username, String password) throws OpenXDataSecurityException;
	
	/**
	 * Determines if the user has a valid password or not
	 * @param user User to validate
	 * @return boolean true if password is valid
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
    boolean validatePassword(User user) throws OpenXDataSecurityException;
    
    /**
     * 
     * @return
     * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
     */
    User getLoggedInUser() throws OpenXDataSecurityException;
    
    /**
     * 
     * @param email
     * @return
     * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
     * @throws UserNotFoundException if the user is not found in the system
     */
    User findUserByEmail(String email) throws OpenXDataSecurityException, UserNotFoundException;
    
    /**
     * 
     * @param user
     * @param size
     * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
     */
    void resetPassword(User user, int size) throws OpenXDataSecurityException;
    
    /**
     * Import Users from a CSV file
     * @param importFileContents String file contents
     * @return String filename of an error CSV file, null if there are no errors
     * @throws OpenXDataSecurityException
     */
    String importUsers(String importFileContents) throws OpenXDataSecurityException;
}
