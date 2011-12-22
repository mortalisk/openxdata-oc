package org.openxdata.server.service;

import java.util.List;

import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.UserHeader;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.exception.OpenXDataSessionExpiredException;
import org.openxdata.server.admin.model.exception.UserNotFoundException;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;

/**
 * This service is used for 
 * managing <code>Users</code> and provide user related functionality.
 * 
 * @author dagmar@cell-life.org.za
 */
public interface UserService {

    /**
     * Gets a user based on their user name
     * @param username String login name
     * @return User, or null if no match found
     */
    User findUserByUsername(String username) throws UserNotFoundException;

    /**
     * Finds a user based on their email
     * @param email String 
     * @return User, or null if no match found
     */
    User findUserByEmail(String email) throws UserNotFoundException;
    
	/**
	 * Finds a user based on their phone number (msisdn)
	 * @param phoneNo String
	 * @return User, or null if no match found
	 */
	User findUserByPhoneNo(String phoneNo) throws UserNotFoundException;
    	
	/**
	 * Saves a new and modified users to the database.
	 * 
	 * @param user the user to save.
	 * @return user saved user with id populated
	 */
	User saveUser(User user);
	
	/**
	 * Saves modified user (must be currently logged in user) to the database.
	 * 
	 * @param user the user to save.
	 */
	void saveMyUser(User user);
	
    /**
     * Resets the user's password and saves the user
     *
     */
    void resetPassword(User user, int size);
    
	/**
     * Gets a list of users in the database.
     * 
     * @return the user list.
     */
	List<User> getUsers();
	
	/**
	 * Gets a Paginated list of users in the database
	 * 
	 * @param pagingLoadConfig
	 * @return
	 */
	PagingLoadResult<User> getUsers(PagingLoadConfig pagingLoadConfig);
	
	/**
	 * Removes a user from the database.
	 * 
	 * @param user the user to remove.
	 */
	void deleteUser(User user);
	
	/**
	 * Gets the currently logged in <code>User</code>.
	 * 
	 * @return <code>User</code> only and only <code>if(Context.getAuthenticatedUser() != null)</code>
	 * @throws OpenXDataSessionExpiredException <code>If(Context.getAuthenticatedUser == null)</code>
	 */
	User getLoggedInUser() throws OpenXDataSessionExpiredException;
	
	/**
	 * Ascertains if the Administrator changed the default password on initial login.
	 * 
	 * @return <code>True only and only if(OpenXDataUtil.checkIfAdministratorChangedPasswordOnInitialLogin() == true)</code>
	 */
	Boolean checkIfUserChangedPassword(User user);
	
	/**
	 * Import users from a CSV string of user data.
	 * <p>Expected columns are:
	 * <ul>
	 *   <li>name (compulsory)</li>
	 *   <li>clearTextPassword (compulsory)</li>
	 *   <li>firstName</li>
	 *   <li>middleName</li>
	 *   <li>lastName</li>
	 *   <li>phoneNo</li>
	 *   <li>email</li>
	 *   <li>roles (compulsory, comma separated list of role names)</li>
	 *   <li>formPermissions (comma separated list of form names to give the user access to)</li>
	 *   <li>studyPermissions (comma separated list of study names to give the user access to)</li>
	 * </ul>
	 * Column headings must match exactly.
	 * The order of columns is not important and non-compulsory columns
	 * may be excluded.
	 * 
	 * @param userdata
	 * @return String containing error rows
	 */
	String importUsers(String userdata);
	
	/**
	 * Log the current user out of the system 
	 */
	void logout();

	/**
	 * Persists a given list of Users to the database.
	 * 
	 * @param users List of Users to persist.
	 */
	void saveUsers(List<User> users);

	/**
	 * Deletes a given list of Users from the database.
	 * 
	 * @param users List of Users to delete.
	 */
	void deleteUsers(List<User> users);

	/**
	 * Get a page of Users mapped to a specific study 
	 * @param studyId
	 * @param loadConfig
	 * @return
	 */
	PagingLoadResult<UserHeader> getMappedStudyUserNames(Integer studyId, PagingLoadConfig loadConfig);
	
	/**
	 * Get a page of Users NOT mapped to the specified study
	 * @param studyId
	 * @param loadConfig
	 * @return
	 */
	PagingLoadResult<UserHeader> getUnmappedStudyUserNames(Integer studyId, PagingLoadConfig loadConfig);
	
	/**
	 * Updates the users currently mapped to the specified study.
	 * @param studyId Integer id of specified study
	 * @param usersToAdd List of users to add to the study mapping
	 * @param usersToDelete List of users to delete from the study mapping
	 * @throws OpenXDataSecurityException
	 */
	void saveMappedStudyUserNames(Integer studyId, List<UserHeader> usersToAdd, List<UserHeader> usersToDelete);

	/**
	 * Get a page of Users mapped to a specific form 
	 * @param formId
	 * @param loadConfig
	 * @return
	 */
	PagingLoadResult<UserHeader> getMappedFormUserNames(Integer formId, PagingLoadConfig loadConfig);
	
	/**
	 * Get a page of Users NOT mapped to the specified form
	 * @param studyId
	 * @param loadConfig
	 * @return
	 */
	PagingLoadResult<UserHeader> getUnmappedFormUserNames(Integer formId, PagingLoadConfig loadConfig);
	
	/**
	 * Updates the users currently mapped to the specified form.
	 * @param formId Integer id of specified form
	 * @param usersToAdd List of users to add to the study mapping
	 * @param usersToDelete List of users to delete from the study mapping
	 */
	void saveMappedFormUserNames(Integer formId, List<UserHeader> usersToAdd, List<UserHeader> usersToDelete);
}