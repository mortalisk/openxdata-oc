/*
 *  Licensed to the OpenXdata Foundation (OXDF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The OXDF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, 
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 *  Copyright 2010 http://www.openxdata.org.
 */
package org.openxdata.server.dao.hibernate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.FormSmsArchive;
import org.openxdata.server.admin.model.FormSmsError;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.dao.FormDownloadDAO;
import org.springframework.stereotype.Repository;


/**
 * The hibernate implementation of the form download data access interface.
 * 
 * @author daniel
 *
 */
@Repository("formDownloadDAO")
public class HibernateFormDownloadDAO extends BaseDAOImpl<Editable> implements FormDownloadDAO {

	private Logger log = Logger.getLogger(this.getClass());

	@Override
	@SuppressWarnings("unchecked")
	public List<Object[]> getStudyList(){
		
		Session session = getSessionFactory().getCurrentSession();
	
		SQLQuery query = session.createSQLQuery("select study_id, name from study order by name");
		query.addScalar("study_id", Hibernate.INTEGER);
		query.addScalar("name", Hibernate.STRING);
		return query.list();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Object[]> getStudyList(User user) {
		
		if(user == null)
			return null;
		
		Session session = getSessionFactory().getCurrentSession();

		Query query = session.createQuery("select distinct s.studyId, s.name from StudyDef as s, User as u " +
				"where u.name = :name and (u in elements(s.users) or u in elements(s.forms.users))" + "order by s.name");
		query.setString("name", user.getName());
		return query.list();
	}

	@Override
	public Map<Integer,String> getFormsDefaultVersionXml(User user){
		return getFormsVersionXml(user, true, null);
	}
	
	@Override
	public Map<Integer, String> getFormsDefaultVersionXml(User user, Integer studyId){
		return getFormsVersionXml(user, true, studyId);
	}

	@Override
	public Map<Integer,String> getFormsVersionXml(){
		return getFormsVersionXml(null, false, null);
	}
	
	/**
	 * Gets form version xml as returned by a given sql statement.
	 * 
	 * @param sql the sql statement.
	 * @return the map of form versions xforms xml keyed by form version id.
	 */
	@SuppressWarnings("unchecked")
	private Map<Integer,String> getFormsVersionXml(User user, boolean defaultForms, Integer studyId) {
		
		Map<Integer,String> forms = new LinkedHashMap<Integer,String>();
		List<Object[]> formList = null;
		
		Session session = getSessionFactory().getCurrentSession();
		
		if (user == null || user.hasAdministrativePrivileges()) {
			// existing query
			if (log.isDebugEnabled() && user != null) {
				log.debug("User "+user.getName()+" is an administrator, so all studies will be loaded");
			}			
			String sql = "select form_definition_version_id,xform from form_definition_version fdv" +
			" inner join form_definition fd on fd.form_definition_id=fdv.form_definition_id" +
			" where xform is not null" + (studyId != null ? " and fd.study_id="+studyId : "") +
			(defaultForms ? " and is_default=1" : "") + " order by fd.name";
			SQLQuery query = session.createSQLQuery(sql);
			query.addScalar("form_definition_version_id", Hibernate.INTEGER);
			query.addScalar("xform", Hibernate.STRING);
			formList = query.list();
		} else {
			// new query
			Query query = session.createQuery(
					"select distinct fdv.formDefVersionId, fdv.xform, fdv.formDef.name from FormDefVersion as fdv, User u" +
				    " where u.name = :name" +
					(defaultForms ? " and fdv.isDefault = :default" : "") +
					(studyId != null ? " and fdv.formDef.study.studyId = :studyId" : "") +
					" and (u in elements(fdv.formDef.users) or u in elements(fdv.formDef.study.users))" +
					" order by fdv.formDef.name"); 
			query.setString("name", user.getName());
			query.setBoolean("default", defaultForms);
			if (studyId != null) query.setInteger("studyId", studyId);
			formList = query.list();
		}

		// process results
		for(Object[] form : formList){
			String xform = (String)form[1];
			if (xform != null && xform.trim().length() > 0) {
				forms.put((Integer)form[0], xform);
			} else {
				log.info("Did not load xform with id "+form[0]+" due to empty xform");
			}
		}
		
		return forms;
	}

	@Override
	public String getXformLocaleText(Integer formId, String locale){
		Session session = getSessionFactory().getCurrentSession();
		
		SQLQuery query = session.createSQLQuery("select xform_text from form_definition_version_text where locale_key='"+ locale + "' and form_definition_version_id="+formId);
		query.addScalar("xform_text", Hibernate.STRING);
		
		String text = (String)query.uniqueResult();
		
		return text;
	}

	@Override
	public void saveFormSmsArchive(FormSmsArchive data){
		_save(data);
	}

	@Override
	public void saveFormSmsError(FormSmsError error){
		_save(error);
	}

	@Override
	public User getUserByPhoneNo(String phoneNo){
		return (User)getSessionFactory().getCurrentSession().createQuery("from User where phoneNo='" + phoneNo + "'").uniqueResult();
	}

	@Override
	public Integer getStudyIdWithKey(String studyKey){
		Session session = getSessionFactory().getCurrentSession();
		return (Integer)session.createSQLQuery("select study_id from study where study_key='" + studyKey + "'").uniqueResult();
	}
}
