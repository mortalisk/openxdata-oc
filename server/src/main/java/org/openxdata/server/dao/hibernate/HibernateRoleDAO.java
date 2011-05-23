package org.openxdata.server.dao.hibernate;

import java.util.List;

import org.openxdata.server.admin.model.Role;
import org.openxdata.server.dao.RoleDAO;
import org.springframework.stereotype.Repository;

/**
 * Provides a hibernate implementation
 * of the <code>PermissionDAO</code> data access <code> interface.</code>
 * 
 *
 */
@Repository("roleDAO")
public class HibernateRoleDAO extends BaseDAOImpl<Role> implements RoleDAO {
	
	@Override
	public void deleteRole(Role role) {
		if(role.isDefaultAdminRole())
			return;
		else
			remove(role);
	}

	@Override
	public List<Role> getRoles() {
		return findAll();
	}

	@Override
	public List<Role> getRolesByName(String name) {
        return searchByPropertyEqual("name", name);
	}

	@Override
	public void saveRole(Role role) {
		save(role);
	}
}
