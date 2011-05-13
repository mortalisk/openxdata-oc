package org.openxdata.server.admin.client.service;

import java.util.List;

import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.Role;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * This interface defines the client side contract for the Permission Service.
 */
public interface RoleService extends RemoteService {
	
	/**
	 * Fetches all the system <tt>Roles.</tt>
	 * 
	 * @return <tt>List</tt> of <tt>Roles.</tt>
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	List<Role> getRoles() throws OpenXDataSecurityException;
	
	/**
	 * Saves a dirty or new <tt>Role</tt>.
	 * 
	 * @param role <tt>Role</tt> to save.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void saveRole(Role role) throws OpenXDataSecurityException;
	
	/**
	 * Deletes a given <tt>Role</tt>.
	 * 
	 * @param role <tt>Role</tt> to delete.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void deleteRole(Role role) throws OpenXDataSecurityException;
	
	/**
	 * Fetches all the system <tt>Permissions.</tt>
	 * 
	 * @return <tt>List</tt> of <tt>Permissions.</tt>
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	List<Permission> getPermissions() throws OpenXDataSecurityException;
	
	/**
	 * Saves a dirty or new <tt>Permission.</tt>
	 * 
	 * @param permission <tt>Permission</tt> to save.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void savePermission(Permission permission) throws OpenXDataSecurityException;
	
	/**
	 * Deletes a given <tt>Permission.</tt>
	 * 
	 * @param permission <tt>Permission</tt> to delete.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void deletePermission(Permission permission) throws OpenXDataSecurityException;
}
