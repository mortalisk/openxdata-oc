package org.openxdata.server.admin.client.service;

import java.util.List;

import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.exception.UserNotFoundException;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Defines the client side contract for the User Service.
 */
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
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void saveUser(User user) throws OpenXDataSecurityException;
    
	/**
     * Gets a list of users in the database.
     * 
     * @return the user list.
     * @throws OpenXDataSecurityException For any <tt>exception</tt> that occurs on the <tt>service layer.</tt>
     */
	List<User> getUsers() throws OpenXDataSecurityException;
	
	/**
	 * Removes a user from the database.
	 * 
	 * @param user the user to remove.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt> 
	 */
	void deleteUser(User user) throws OpenXDataSecurityException;
	
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
}
