package org.openxdata.server.admin.model.paging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class PagingLoadConfig implements Serializable {
	
    private static final long serialVersionUID = 2673295549529166352L;
    
	int offset = 0;
	int limit = 0;
	
	Boolean sortDescending;
	String sortField;
	
	List<FilterConfig> filters = new ArrayList<FilterConfig>();
	
	public PagingLoadConfig() {
		// default constructor
	}
	
	public PagingLoadConfig(int offset, int limit) {
		this.offset = offset;
		this.limit = limit;
	}
	
	public PagingLoadConfig(int offset, int limit, String sortField, Boolean sortDescending) {
		this(offset, limit);
		this.sortField = sortField;
		this.sortDescending = sortDescending;
	}
	
	/**
	 * The index at which to start returning results from.
	 * For example, for page#1, offset will be 0, for page#2 the offset will be equal to the page size
	 * @return
	 */
	public int getOffset() {
    	return offset;
    }
	
	public void setOffset(int offset) {
    	this.offset = offset;
    }
	
	/**
	 * The number of results to return in a page (also known as page size)
	 * @return
	 */
	public int getLimit() {
    	return limit;
    }
	
	public void setLimit(int limit) {
    	this.limit = limit;
    }
	
	/**
	 * Indicates the order of sorting.
	 * @return FALSE for ascending, TRUE for descending and NULL for none
	 */
	public Boolean isSortDescending() {
    	return sortDescending;
    }

	public void setSortDescending(Boolean sortDescending) {
    	this.sortDescending = sortDescending;
    }

	/**
	 * The name of the field on which sorting must occur
	 * @return
	 */
	public String getSortField() {
    	return sortField;
    }
	
	public void setSortField(String sortField) {
    	this.sortField = sortField;
    }

	public List<FilterConfig> getFilters() {
    	return filters;
    }

	public void setFilters(List<FilterConfig> filters) {
    	this.filters = filters;
    }
	
	public void addFilter(FilterConfig filter) {
		if (filters == null) {
			filters = new ArrayList<FilterConfig>(); 
		}
		filters.add(filter);
	}
}
