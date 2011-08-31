package org.openxdata.server.dao;

import java.util.List;

import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;

import com.googlecode.genericdao.dao.hibernate.GenericDAO;

public interface BaseDAO<T extends Editable> extends GenericDAO<T, Long> {

    List<T> searchByPropertyEqual(String property, Object value);

    T searchUniqueByPropertyEqual(String property, Object value);
    
    PagingLoadResult<T> findAllByPage(PagingLoadConfig pagingLoadConfig, String defaultSortField);

}
