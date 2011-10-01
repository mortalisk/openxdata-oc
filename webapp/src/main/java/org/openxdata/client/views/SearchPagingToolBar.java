package org.openxdata.client.views;

import org.openxdata.client.AppMessages;

import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.google.gwt.core.client.GWT;

/**
 * Extends the AdjustablePagingToolbar and adds search functionality
 */
public class SearchPagingToolBar<M extends ModelData> extends AdjustablePagingToolBar {
	
	final AppMessages appMessages = GWT.create(AppMessages.class);
	
	RemoteStoreFilterField<ModelData> filterField;
	String filterValue = null;

	public SearchPagingToolBar(int pageSize) {
		super(pageSize);

		// filter to search for users
        filterField = new RemoteStoreFilterField<ModelData> () {
           @Override
           protected void handleOnFilter(String filterValue) {
        	   // handle filtering - this is a call after each key pressed - it might be improved */
        	   SearchPagingToolBar.this.filterValue = filterValue;
        	   loader.load(new BasePagingLoadConfig(0, getPageSize()));
           }
           
           @Override
           protected void handleCancelFilter () {
        	   SearchPagingToolBar.this.filterValue = null;
        	   loader.load(new BasePagingLoadConfig(0, getPageSize()));
           }
        };
        filterField.setEmptyText("Search");
        filterField.setWidth(185);

        insert(new SeparatorToolItem(), 0);
        insert(filterField, 0);
	}
	
	/**
	 * Binds the filterField to the store
	 * @param store
	 */
	public void bind(Store<ModelData> store) {
		filterField.bind(store);
	}
	
	/**
	 * @return String text entered in the filter field box (can be null)
	 */
	public String getSearchFilterValue() {
		return filterValue;
	}
}