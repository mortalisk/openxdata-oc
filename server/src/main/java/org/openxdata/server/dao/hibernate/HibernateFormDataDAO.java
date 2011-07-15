package org.openxdata.server.dao.hibernate;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDataVersion;
import org.openxdata.server.dao.FormDataDAO;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository("formDataDAO")
public class HibernateFormDataDAO extends BaseDAOImpl<FormData> implements FormDataDAO {

	@Override
	public void deleteFormData(Integer formDataId){
		removeById(formDataId);
	}

	@Override
	public FormData getFormData(Integer formDataId) {
		return find(formDataId);
	}

	@Override
	public void saveFormData(FormData formData){
		save(formData);
	}
	
	@Override
	public void saveFormDataVersion(FormData formData) {
		// note: must use SQL because we have to avoid using the hibernate cache
		// we want to retrieve the old form data for the backup, not the new one
		Query query = getSession()
			.createSQLQuery("select data from form_data where form_data_id = ?")
			.addScalar("data", Hibernate.TEXT)
			.setInteger(0, formData.getId());
		String oldData = ((String)query.uniqueResult());
        FormDataVersion backup = new FormDataVersion(formData, oldData, formData.getDateChanged(), formData.getChangedBy());
        saveFormDataVersion(backup);
	}
	
	@Override
    public void saveFormDataVersion(FormDataVersion formDataVersion) {
	    _save(formDataVersion);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<FormDataVersion> getFormDataVersion(Integer formDataId) {
		return getSession().createCriteria(FormDataVersion.class)
			.createAlias("formData", "fd").add(Restrictions.eq("fd.id", formDataId)).list();
	}
}
