package org.openxdata.server.dao.hibernate;

import java.util.List;

import org.openxdata.server.admin.model.User;
import org.openxdata.server.dao.UserDAO;
import org.springframework.stereotype.Repository;

import com.googlecode.genericdao.search.Search;

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
}
