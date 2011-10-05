package org.openxdata.server.dao.hibernate;

import java.util.List;

import org.openxdata.server.admin.model.Role;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;
import org.openxdata.server.dao.RoleDAO;
import org.springframework.stereotype.Repository;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

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

	@Override
    public PagingLoadResult<Role> getMappedRoles(Integer userId, PagingLoadConfig pagingLoadConfig) {
		Search roleSearch = getSearchFromLoadConfig(pagingLoadConfig, "name");
		roleSearch.addFilterSome("users", Filter.equal("id", userId));
	    SearchResult<Role> result = searchAndCount(roleSearch);
	    return getPagingLoadResult(pagingLoadConfig, result);
    }

	@Override
    public PagingLoadResult<Role> getUnMappedRoles(Integer userId, PagingLoadConfig pagingLoadConfig) {
		Search roleSearch = getSearchFromLoadConfig(pagingLoadConfig, "name");
		roleSearch.addFilterAll("users", Filter.notEqual("id", userId));
	    SearchResult<Role> result = searchAndCount(roleSearch);
	    return getPagingLoadResult(pagingLoadConfig, result);
    }
}
