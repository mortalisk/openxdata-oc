package org.openxdata.server.dao.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.dao.FormDAO;
import org.openxdata.server.dao.UserFormMapDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author Angel
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

    @Override
	public void saveUserMappedForm(UserFormMap map) {
		save(map);
	}
    
    @Override
	@SuppressWarnings("unchecked")
    public List<FormDef> getFormsForUser(User user) {
    	// gets all the forms for the specified user
    	if (user.hasAdministrativePrivileges()) {
    		return formDAO.getForms();
    	} else {
    		Session session = getSession();
			Query query = session.createQuery(
					"select distinct fd from FormDef as fd, User as u" +
					" where u.name = :name" +
					" and (u in elements(fd.users) or u in elements(fd.study.users))");
			query.setString("name", user.getName());
			List<FormDef> forms = query.list();
			return forms;
    	}
    }
    
    @Override
	@SuppressWarnings("unchecked")
    public List<FormDef> getFormsForUser(User user, Integer studyDefId) {
    	// gets all the forms for the specified user
    	List<FormDef> forms = null;
    	Session session = getSession();
    	if (user.hasAdministrativePrivileges()) {
    		forms = session.createCriteria(FormDef.class).createAlias("study", "s").add(Restrictions.eq("s.studyId", studyDefId)).list();
    	} else {
			Query query = session.createQuery(
					"select distinct fd from FormDef as fd, User as u" +
					" where u.name = :name and fd.study.studyId = :studyId" +
					" and (u in elements(fd.users) or u in elements(fd.study.users))");
			query.setString("name", user.getName());
			query.setInteger("studyId", studyDefId);
			forms = query.list();
    	}
    	
    	return forms;
    }
}
