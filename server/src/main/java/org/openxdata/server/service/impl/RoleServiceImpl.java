package org.openxdata.server.service.impl;

import java.util.List;

import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.Role;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;
import org.openxdata.server.dao.PermissionDAO;
import org.openxdata.server.dao.RoleDAO;
import org.openxdata.server.dao.UserDAO;
import org.openxdata.server.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation for <code>Permission Service</code>.
 * 
 *
 */
@Service("roleService")
@Transactional
public class RoleServiceImpl implements RoleService {

	@Autowired
    private RoleDAO roleDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private PermissionDAO permissionDAO;

    public void setRoleDAO(RoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }

    @Override
    @Secured("Perm_Delete_Roles")
	public void deleteRole(Role role) {
        roleDAO.deleteRole(role);
    }

    @Override
	@Transactional(readOnly=true)
	@Secured("Perm_View_Roles")
    public List<Role> getRoles() {
        return roleDAO.getRoles();
    }

    @Override
    @Secured("Perm_View_Roles")
	public List<Role> getRolesByName(String name) {
        return roleDAO.getRolesByName(name);
    }

    @Override
    @Secured("Perm_Add_Roles")
	public void saveRole(Role role) {
        roleDAO.saveRole(role);
    }

    @Override
	@Transactional(readOnly=true)
	// note: no security because it is used during authentication
    public List<Permission> getPermissions() {
        return permissionDAO.getPermissions();
    }

    @Override
    @Secured("Perm_Delete_Permissions")
	public void deletePermission(Permission permission) {
    	permissionDAO.deletePermission(permission);
    }

    @Override
    @Secured("Perm_Add_Permissions")
	public void savePermission(Permission permission) {
    	permissionDAO.savePermission(permission);
    }
    
    @Override
	@Transactional(readOnly=true)
    public Permission getPermission(String permissionName) {
    	return permissionDAO.getPermission(permissionName);
    }

	@Override
	@Secured("Perm_View_Roles")
    public PagingLoadResult<Role> getMappedRoles(Integer userId, PagingLoadConfig pagingLoadConfig) {
		return roleDAO.getMappedRoles(userId, pagingLoadConfig);
    }

	@Override
	@Secured("Perm_View_Roles")
    public PagingLoadResult<Role> getUnMappedRoles(Integer userId, PagingLoadConfig pagingLoadConfig) {
	    return roleDAO.getUnMappedRoles(userId, pagingLoadConfig);
    }

	@Override
	@Secured("Perm_Add_Users")
    public void saveMappedRoles(Integer userId, List<Role> rolesToAdd, List<Role> rolesToDelete) {
		User user = userDAO.getUser(userId);
		if (rolesToAdd != null) {
			for (Role r : rolesToAdd) {
				user.addRole(r);
			}
		}
		if (rolesToDelete != null) {
			for (Role r : rolesToDelete) {
				user.removeRole(r);
			}
		}
		userDAO.save(user);
    }
}
