package org.openxdata.server.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;
import org.openxdata.server.dao.FormDAO;
import org.openxdata.server.dao.UserFormMapDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

/**
 *
 */
@Repository("userFormMapDAO")
public class HibernateUserFormMapDAO extends BaseDAOImpl<UserFormMap> implements UserFormMapDAO {

	@Autowired
	private FormDAO formDAO;
	
    @Override
	public void deleteUserMappedForm(UserFormMap map) {
		remove(map);		
	}

    @Override
	public List<UserFormMap> getUserMappedForms() {
		return findAll();
	}
    
    public List<UserFormMap> getUserMappedForms(Integer formId) {
    	return searchByPropertyEqual("formId", formId);
    }
    
	@Override
    public UserFormMap getUserMappedForm(Integer userId, Integer formId) {
		Search search = new Search();
    	search.addFilterEqual("userId", userId);
    	search.addFilterEqual("formId", formId);
    	return searchUnique(search);
    }

    @Override
	public void saveUserMappedForm(UserFormMap map) {
		save(map);
	}
        
    @Override
	@SuppressWarnings({ "unchecked" })
    public Map<Integer,String> getFormNamesForUser(User user, Integer studyDefId) {
    	// gets all the forms for the specified user
    	Map<Integer, String> formNames = new HashMap<Integer, String>();
    	Session session = getSession();
    	Query query = null;
    	if (user.hasAdministrativePrivileges()) {
    		query = session.createQuery(
					"select distinct fd.id, fd.name from FormDef as fd" +
					" where fd.study.id = :studyId");
			query.setInteger("studyId", studyDefId);
    	} else {
			query = session.createQuery(
					"select distinct fd.id, fd.name from FormDef as fd, User as u" +
					" where u.name = :name and fd.study.id = :studyId" +
					" and (u in elements(fd.users) or u in elements(fd.study.users))");
			query.setString("name", user.getName());
			query.setInteger("studyId", studyDefId);
    	}
    	List<Object[]> result = query.list();
		for (Object[] obj : result) {
			formNames.put((Integer)obj[0], (String)obj[1]);
		}
    	return formNames;
    }

	@Override
    public PagingLoadResult<FormDef> getUserMappedForms(User user, PagingLoadConfig loadConfig) {
    	// gets all the forms for the specified user
    	if (user.hasAdministrativePrivileges()) {
    		return formDAO.getForms(loadConfig);
    	} else {
    		Search formSearch = getSearchFromLoadConfig(loadConfig, "name");
    		formSearch.addFilterSome("users", Filter.equal("id", user.getId()));
    		formSearch.addFilterSome("study.users", Filter.equal("id", user.getId()));
    	    SearchResult<FormDef> result = searchAndCount(formSearch);
    		List<FormDef> list = result.getResult();
    		int totalNum = result.getTotalCount();
    		int offset = loadConfig == null ? 0 : loadConfig.getOffset();
    		return new PagingLoadResult<FormDef>(list, offset, list.size(), totalNum);
    	}
    }

	@Override
    public void deleteUserMappedForms(int formId) {
    	Session session = getSession();
		Query query = session.createQuery("delete from UserFormMap where formId = :formId");
		query.setInteger("formId", formId);
		query.executeUpdate();
    }
}

