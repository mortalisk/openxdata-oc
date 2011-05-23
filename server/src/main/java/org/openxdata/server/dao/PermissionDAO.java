package org.openxdata.server.dao;

import java.util.List;

import org.openxdata.server.admin.model.Permission;

/**
 *
 */
public interface PermissionDAO extends BaseDAO<Permission> {

	/**
	 * Gets a list of permissions from the database.
	 * 
	 * @return the permissions list.
	 */
	List<Permission> getPermissions();
	
	/**
	 * Saves permission to the database.
	 * 
	 * @param permission the permission to save.
	 */
	void savePermission(Permission permission);
	
	/**
	 * Deletes a permission from the database.
	 * 
	 * @param locale the permission to delete.
	 */
	void deletePermission(Permission permission);
	
	Permission getPermission(String name);
}
