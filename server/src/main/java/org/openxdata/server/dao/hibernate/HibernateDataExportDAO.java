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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.openxdata.server.OpenXDataConstants;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.dao.DataExportDAO;
import org.openxdata.server.dao.SettingDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Provides a hibernate implementation of the data export data access interface.
 * 
 * @author daniel
 * @author dagmar@cell-life.org.za
 * @author Jonny Heggheim
 *
 */
@Repository("dataExportDAO")
public class HibernateDataExportDAO extends BaseDAOImpl<Editable> implements DataExportDAO {
    
    @Autowired
    private SettingDAO settingDAO;

    @Override
	public FormDefVersion getFormDefVersion(Integer formDefVersionId) {
        Session session = getSessionFactory().getCurrentSession();
        Query query = session.createQuery("from FormDefVersion where formDefVersionId = :formDefVersionId");
        query.setParameter("formDefVersionId", formDefVersionId);
        return (FormDefVersion) query.uniqueResult();
    }

    @Override
	@SuppressWarnings("unchecked")
	public List<Object[]> getFormDataWithAuditing(Integer formDefVersionId, Date fromDate, Date toDate, Integer userId) {
        Session session = getSessionFactory().getCurrentSession();
        String sql = "select d.form_data_id, u.user_name, d.date_created, d.data from form_data d, users u";
        // FIXME: see HibernateEditableDAO.getFormData
        String filter = null;
        if (!(formDefVersionId == null && fromDate == null && toDate == null && userId == null)) {
            filter = addIntegerFilter(filter, "d.form_definition_version_id", formDefVersionId);
            filter = addIntegerFilter(filter, "d.creator", userId);
            filter = addDateFilter(filter, "d.date_created", fromDate, toDate);
            if (!filter.equals("")) {
                sql += filter += " and u.user_id = d.creator;";
            } else {
                sql += " where u.user_id = d.creator;";
            }
        }

        SQLQuery query = session.createSQLQuery(sql);

        query.addScalar("form_data_id", Hibernate.INTEGER);
        query.addScalar("user_name", Hibernate.STRING);
        query.addScalar("date_created", Hibernate.DATE);
        query.addScalar("data", Hibernate.STRING);

        List<Object[]> items = query.list();
        return items;
    }

    private String addIntegerFilter(String filter, String fieldName, Integer value) {
        if (value != null) {
            if (filter == null) {
                filter = " where " + fieldName;
            } else {
                filter += " and " + fieldName;
            }

            filter += " = " + value;
        }
        return filter;
    }

    /**
     * Adds a date where clause to an sql statement.
     *
     * @param filter the sql statement.
     * @param fieldName the name of the field to have the where clause.
     * @param fromValue the mimimum value for the field.
     * @param toValue the maximum value for the field.
     * @return the new sql statement.
     */
    private String addDateFilter(String filter, String fieldName, Date fromValue, Date toValue) {
        if (fromValue != null) {
            if (filter == null) {
                filter = " where " + fieldName;
            } else {
                filter += " and " + fieldName;
            }

            filter += " >= '" + fromDate2DisplayString(fromValue) + "'";
        }

        if (toValue != null) {
            if (filter == null) {
                filter = " where " + fieldName;
            } else {
                filter += " and " + fieldName;
            }

            filter += " <= '" + fromDate2DisplayString(toValue) + "'";
        }

        return filter;
    }
    
    /**
     * Converts a date to a display string.
     * 
     * @param date the date to convert.
     * @return the string representation of the date.
     */
    private String fromDate2DisplayString(Date date) {
        String format = getDateFormat();
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}

    private String getDateFormat() {
        String key = OpenXDataConstants.SETTING_NAME_SUBMIT_DATETIME_FORMAT;
        String defaultFormat = OpenXDataConstants.DEFAULT_DATE_SUBMIT_FORMAT;

        return settingDAO.getSetting(key, defaultFormat);
    }

    @Override
	@SuppressWarnings("unchecked")
	public List<FormData> getFormDataToExport(Integer exporterBitFlag) {
        SQLQuery query = getSessionFactory().getCurrentSession().createSQLQuery("select * from form_data  where (exported & " + exporterBitFlag + ") <> " + exporterBitFlag).addEntity(FormData.class);
        List<FormData> formData = query.list();
        return formData;
    }

    @Override
	public void setFormDataExported(FormData exportedFormData, Integer exporterBitFlag) {
        exportedFormData.setExportedFlag(exporterBitFlag);
        Session session = getSessionFactory().getCurrentSession();
        session.saveOrUpdate(exportedFormData);
    }

    @Override
	public FormDef getFormDef(Integer formId) {
        return (FormDef) getSessionFactory().getCurrentSession().createQuery("from FormDef where formId=" + formId).uniqueResult();
    }

    @Override
	public StudyDef getStudyDef(Integer studyId) {
        return (StudyDef) getSessionFactory().getCurrentSession().createQuery("from StudyDef where studyId=" + studyId).uniqueResult();
    }
}