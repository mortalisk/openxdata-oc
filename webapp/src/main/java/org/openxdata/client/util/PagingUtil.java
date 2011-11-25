package org.openxdata.client.util;

import java.util.List;

import org.openxdata.client.views.RemoteStoreFilterField;
import org.openxdata.server.admin.model.paging.FilterComparison;
import org.openxdata.server.admin.model.paging.FilterConfig;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.FilterPagingLoadConfig;

public class PagingUtil {

	public static PagingLoadConfig createPagingLoadConfig(com.extjs.gxt.ui.client.data.PagingLoadConfig gxtPagingConfig) {
		
		Boolean sortDescending = false;
    	if (gxtPagingConfig.getSortDir() == SortDir.DESC) {
    		sortDescending = true;
    	} else if (gxtPagingConfig.getSortDir() == SortDir.ASC) {
    		sortDescending = false;
    	}
		
    	PagingLoadConfig ourPagingConfig = new PagingLoadConfig(
    			gxtPagingConfig.getOffset(), gxtPagingConfig.getLimit(), 
    			gxtPagingConfig.getSortField(), sortDescending);
    	
    	Object filterValue = gxtPagingConfig.get(RemoteStoreFilterField.PARM_QUERY);
		String rawField = gxtPagingConfig.get(RemoteStoreFilterField.PARM_FIELD);

		if (filterValue != null && rawField != null) {
			ourPagingConfig.addFilter(new FilterConfig(rawField, (String) filterValue, FilterComparison.LIKE));
		}
		
		if (gxtPagingConfig instanceof FilterPagingLoadConfig){
			if (((FilterPagingLoadConfig)gxtPagingConfig).getFilterConfigs() != null) {
				List<com.extjs.gxt.ui.client.data.FilterConfig> gxtFilters = ((FilterPagingLoadConfig) gxtPagingConfig).getFilterConfigs();
				for (int i = 0; i < gxtFilters.size(); i++) {
						ourPagingConfig.addFilter(new FilterConfig(gxtFilters.get(i).getField(), gxtFilters.get(i).getValue(), FilterComparison.valueOf(gxtFilters.get(i)
								.getComparison())));
				
				}
			}		
		}
    	
    	return ourPagingConfig;
	}
	
	public static <Data> PagingLoadResult<Data> createPagingLoadResult(com.extjs.gxt.ui.client.data.PagingLoadResult<Data> gxtPagingResult) {
		PagingLoadResult<Data> ourPagingResult = new PagingLoadResult<Data>(
				gxtPagingResult.getData(), gxtPagingResult.getOffset(), gxtPagingResult.getTotalLength());
		return ourPagingResult;
	}
}
