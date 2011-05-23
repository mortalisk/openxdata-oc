package org.openxdata.server.dao.hibernate;

import java.util.List;

import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.dao.FormDAO;
import org.springframework.stereotype.Repository;

/**
 * @author Angel
 *
 */
@Repository("formDAO")
public class HibernateFormDAO extends BaseDAOImpl<FormDef> implements FormDAO {

	@Override
	public void deleteForm(FormDef formDef) {
		remove(formDef);
	}
	
	@Override
	public void saveForm(FormDef formDef) {
		save(formDef);
	}
	
	/* (non-Javadoc)
	 * @see org.openxdata.server.dao.FormDAO#getForms()
	 */
	@Override
	public List<FormDef> getForms() {
		return findAll();
	}
}
