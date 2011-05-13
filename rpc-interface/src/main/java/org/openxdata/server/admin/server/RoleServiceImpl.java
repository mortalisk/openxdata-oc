package org.openxdata.server.admin.server;

import java.util.List;

import javax.servlet.ServletException;

import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.Role;
import org.openxdata.server.rpc.OxdPersistentRemoteService;
import org.springframework.web.context.WebApplicationContext;

/**
 * Default Implementation for the <code>PermissionService Interface.</code>
 */
public class RoleServiceImpl extends OxdPersistentRemoteService implements org.openxdata.server.admin.client.service.RoleService {

	private static final long serialVersionUID = -4262487595919371747L;
	private org.openxdata.server.service.RoleService roleService;
	
	public RoleServiceImpl() {}
	
	@Override
	public void init() throws ServletException {
		super.init();
		WebApplicationContext ctx = getApplicationContext();
		roleService = (org.openxdata.server.service.RoleService)ctx.getBean("roleService");
	}	

	@Override
	public void deletePermission(Permission permission) {
		roleService.deletePermission(permission);		
	}

	@Override
	public void deleteRole(Role role) {
		roleService.deleteRole(role);
		
	}

	@Override
	public List<Permission> getPermissions() {
		return roleService.getPermissions();
	}

	@Override
	public List<Role> getRoles() {
		return roleService.getRoles();
	}

	@Override
	public void savePermission(Permission permission) {
		roleService.savePermission(permission);
	}

	@Override
	public void saveRole(Role role) {
		roleService.saveRole(role);
	}
}
