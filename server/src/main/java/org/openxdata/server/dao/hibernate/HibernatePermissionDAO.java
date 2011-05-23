package org.openxdata.server.dao.hibernate;

import java.util.List;

import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.dao.PermissionDAO;
import org.springframework.stereotype.Repository;

/**
 * @author Angel
 *
 */
@Repository("permissionDAO")
public class HibernatePermissionDAO extends BaseDAOImpl<Permission> implements PermissionDAO {

	@Override
	public void deletePermission(Permission permission) {
		remove(permission);		
	}
	
	@Override
	public void savePermission(Permission permission) {
		save(permission);
	}
	
	@Override
	public List<Permission> getPermissions() {
		return findAll();
	}

	@Override
	public Permission getPermission(String name) {
        return searchUniqueByPropertyEqual("name", name);
	}
}
