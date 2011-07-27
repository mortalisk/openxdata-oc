package org.openxdata.server.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    		forms = session.createCriteria(FormDef.class).createAlias("study", "s").add(Restrictions.eq("s.id", studyDefId)).list();
    	} else {
			Query query = session.createQuery(
					"select distinct fd from FormDef as fd, User as u" +
					" where u.name = :name and fd.study.id = :studyId" +
					" and (u in elements(fd.users) or u in elements(fd.study.users))");
			query.setString("name", user.getName());
			query.setInteger("studyId", studyDefId);
			forms = query.list();
    	}
    	
    	return forms;
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
}
