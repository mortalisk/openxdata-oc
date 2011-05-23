package org.openxdata.server.service;

import java.util.List;

import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.Role;

/**
 * This service is used for managing 
 * <code>Permissions</code> and <code>Roles</code>.
 * 
 *
 */
public interface RoleService {
	
	/**
	 * Gets a list of roles from the database.
	 * 
	 * @return the role list.
	 */
	List<Role> getRoles();
	
	/**
	 * Saves a role to the database.
	 * 
	 * @param role the role to save.
	 */
	void saveRole(Role role);
	
	/**
	 * Deletes a role from the database.
	 * 
	 * @param role the role to delete.
	 */
	void deleteRole(Role role);
	
	/**
	 * Gets a list of permissions from the database.
	 * 
	 * @return the permissions list.
	 */
	List<Permission> getPermissions();
	
	/**
	 * Saves a dirty permission
	 * 
	 * @param permission permission to save
	 */
	void savePermission(Permission permission);
	
	/**
	 * Deletes a given permission from the system
	 * 
	 * @param permission permission to delete
	 */
	void deletePermission(Permission permission);

	List<Role> getRolesByName(String name);
	
	Permission getPermission(String permissionName);
}
