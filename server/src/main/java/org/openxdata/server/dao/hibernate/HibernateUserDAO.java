package org.openxdata.server.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.UserHeader;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;
import org.openxdata.server.dao.UserDAO;
import org.springframework.stereotype.Repository;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

/**
 * Provides a hibernate implementation
 * of the <code>UserDAO</code> data access <code> interface.</code>
 * 
 * @author Jonny Heggheim
 *
 */
@Repository("userDAO")
public class HibernateUserDAO extends BaseDAOImpl<User> implements UserDAO {
	
	@Override
    public User getUser(Integer id) {
		return super.find(id);
    }

    @Override
	public void deleteUser(User user) {
        remove(user);
	}

	@Override
	public User findUserByEmail(String email) {
		Search search = new Search();
		search.addFilterEqual("email", email);
		search.addFilterEqual("status", User.ACTIVE);
		List<User> users =  search(search);
		if (users != null && users.size() > 0) {
			// note: email address should now be unique since there are UI checks in place, 
			// but historically might not be the case
			return users.get(0);
		}
		return null;
	}
	
	@Override
	public User findUserByPhoneNo(String phoneNo) {
        return searchUniqueByPropertyEqual("phoneNo", phoneNo);
	}
	
	@Override
	public User getUser(String username) {
        return searchUniqueByPropertyEqual("name", username);
	}
	
	@Override
	public List<User> getUsers() {
		Search s = new Search();
		s.addSort("name", false);
        return search(s);
	}

	@Override
	public void saveUser(User user) {		
        save(user);
	}
	
	@Override
	public void saveOnlineStatus(User user) {
		save(user);		
	}
	
	@Override
	public PagingLoadResult<UserHeader> getMappedStudyUserNames(Integer studyId, PagingLoadConfig loadConfig) {
		Search userSearch = getSearchFromLoadConfig(loadConfig, "name");
		userSearch.addFilterSome("mappedStudies", Filter.equal("studyId", studyId));
		SearchResult<User> result = searchAndCount(userSearch);
		return getUserHeaderPagingLoadResult(null, loadConfig, result);
	}

	@Override
	public PagingLoadResult<UserHeader> getUnmappedStudyUserNames(Integer studyId, PagingLoadConfig loadConfig) {
		Search userSearch = getSearchFromLoadConfig(loadConfig, "name");
		userSearch.addFilterSome("mappedStudies", Filter.notEqual("studyId", studyId));
		SearchResult<User> result = searchAndCount(userSearch);
		return getUserHeaderPagingLoadResult(null, loadConfig, result);
	}

	@Override
	public PagingLoadResult<UserHeader> getMappedFormUserNames(FormDef form, PagingLoadConfig loadConfig) {
		Search userSearch = getSearchFromLoadConfig(loadConfig, "name");
		userSearch.addFilterOr(
				Filter.some("mappedForms", Filter.equal("formId", form.getId())),
				Filter.some("mappedStudies", Filter.equal("studyId", form.getStudy().getId())));
		SearchResult<User> result = searchAndCount(userSearch);
		return getUserHeaderPagingLoadResult(form, loadConfig, result);
	}

	@Override
	public PagingLoadResult<UserHeader> getUnmappedFormUserNames(FormDef form, PagingLoadConfig loadConfig) {
		Search userSearch = getSearchFromLoadConfig(loadConfig, "name");
		userSearch.addFilterAnd(
				Filter.all("mappedForms", Filter.notEqual("formId", form.getId())),
				Filter.all("mappedStudies",Filter.notEqual("studyId", form.getStudy().getId())));
		SearchResult<User> result = searchAndCount(userSearch);
		return getUserHeaderPagingLoadResult(null, loadConfig, result);
	}

	private PagingLoadResult<UserHeader> getUserHeaderPagingLoadResult(FormDef form, PagingLoadConfig loadConfig, SearchResult<User> searchResult) {
		List<User> list = searchResult.getResult();
		List<UserHeader> headerList = new ArrayList<UserHeader>();
		if (list != null) {
			for (User u : list) {
				UserHeader header = new UserHeader(u.getId(), u.getName());
				if (form != null && !u.isMappedForm(form)) {
					header.setStudyAccess(true);
				}
				headerList.add(header);
			}
		}
		int totalNum = searchResult.getTotalCount();
		int offset = loadConfig == null ? 0 : loadConfig.getOffset();
		return new PagingLoadResult<UserHeader>(headerList, offset, list.size(), totalNum);
	}
}
