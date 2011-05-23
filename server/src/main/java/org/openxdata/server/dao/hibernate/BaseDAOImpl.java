package org.openxdata.server.dao.hibernate;

import java.util.List;

import org.hibernate.SessionFactory;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.dao.BaseDAO;
import org.springframework.beans.factory.annotation.Autowired;

import com.trg.dao.hibernate.GenericDAOImpl;
import com.trg.search.Search;

abstract class BaseDAOImpl<T extends Editable> extends GenericDAOImpl<T, Long> implements BaseDAO<T> {

    @Autowired
    @Override
    public void setSessionFactory(SessionFactory sessionFactory) {
        super.setSessionFactory(sessionFactory);
    }
    
	@Override
	public List<T> searchByPropertyEqual(String property, Object value){
		Search search = new Search();
		search.addFilterEqual(property, value);
		return search(search);
	}

	@Override
    @SuppressWarnings("unchecked")
	public T searchUniqueByPropertyEqual(String property, Object value) {
        Search search = new Search();
        search.addFilterEqual(property, value);
        return (T) searchUnique(search);
    }
}
