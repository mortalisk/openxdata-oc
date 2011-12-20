package org.openxdata.server.dao;

import java.util.List;

import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.UserHeader;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;

/**
 * Provides data access 
 * services to the <code>User service</code>.
 * 
 *
 */
public interface UserDAO extends BaseDAO<User> {
	
	/**
	 * Gets a list of users in the database.
	 * 
	 * @return the user list.
	 */
	List<User> getUsers();
	
	/**
	 * Retrieves a user by their log in name
	 * 
	 * @param username String
	 * @return User, or null if no match found
	 */
	User getUser(String username);
	
	/**
	 * Retrieves a user by their email
	 * 
	 * @param email String
	 * @return User, or null if no match found
	 */	
	User findUserByEmail(String email);
	
	/**
	 * Retrieves a user by their phone number
	 * 
	 * @param phoneNo String
	 * @return User, or null if no match found
	 */	
	User findUserByPhoneNo(String phoneNo);
	
	/**
	 * Retrieves user by identifier
	 * @param id
	 * @return
	 */
	User getUser(Integer id);
	
	/**
	 * Saves a user to the database.
	 * 
	 * @param user the user to save.
	 */
	void saveUser(User user);
	
	/**
	 * Removes a user from the database.
	 * 
	 * @param user the user to remove.
	 * 
	 */
	void deleteUser(User user);
	
	/**
	 * This method is supposed to be only called in the authentication 
	 * method and should not be annotated with spring security @secured annotation 
	 * because it is required to set the User's online status when the User logs in. The User 
	 * might not necessarily have permissions to call the method {@link #saveUser(User)}and yet we intend to have his online status saved.
	 * 
	 * @param user User to set online status for.
	 */
	void saveOnlineStatus(User user);

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
	 * Get a page of Users mapped to a specific form 
	 * @param form
	 * @param loadConfig
	 * @return
	 */
	PagingLoadResult<UserHeader> getMappedFormUserNames(FormDef form, PagingLoadConfig loadConfig);

	/**
	 * Get a page of Users NOT mapped to the specified form
	 * @param form
	 * @param loadConfig
	 * @return
	 */
	PagingLoadResult<UserHeader> getUnmappedFormUserNames(FormDef form, PagingLoadConfig loadConfig);
}
