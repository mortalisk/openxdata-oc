package org.openxdata.server.dao;

import java.util.List;

import org.openxdata.server.admin.model.AbstractEditable;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;

import com.googlecode.genericdao.dao.hibernate.GenericDAO;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

public interface BaseDAO<T extends Editable> extends GenericDAO<T, Long> {

    List<T> searchByPropertyEqual(String property, Object value);

    T searchUniqueByPropertyEqual(String property, Object value);
    
    Search getSearchFromLoadConfig(Class<? extends AbstractEditable> entityClass, PagingLoadConfig loadConfig, String defaultSortField);
    
    PagingLoadResult<T> getPagingLoadResult(PagingLoadConfig loadConfig, SearchResult<T> searchResult);
}
