package org.openxdata.server.dao;

import java.util.List;

import org.openxdata.server.admin.model.Editable;

import com.trg.dao.hibernate.GenericDAO;

public interface BaseDAO<T extends Editable> extends GenericDAO<T, Long> {

    List<T> searchByPropertyEqual(String property, Object value);

    T searchUniqueByPropertyEqual(String property, Object value);
}
