package org.openxdata.server.dao;

import java.util.List;

import org.openxdata.server.admin.model.Role;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;

/**
 * Provides data access 
 * services to the <code>Permission service</code>.
 * 
 *
 */
public interface RoleDAO extends BaseDAO<Role> {
	
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
	 * Get roles with the specified name
	 */
	List<Role> getRolesByName(String name);
	
	/**
	 * Get a paged list of the roles mapped to the specified user
	 * @param userId
	 * @param pagingLoadConfig
	 * @return
	 */
    PagingLoadResult<Role> getMappedRoles(Integer userId, PagingLoadConfig pagingLoadConfig);

    /**
     * Get a paged list of the roles NOT mapped to the specified user
     * @param userId
     * @param pagingLoadConfig
     * @return
     */
    public PagingLoadResult<Role> getUnMappedRoles(Integer userId, PagingLoadConfig pagingLoadConfig);
}
