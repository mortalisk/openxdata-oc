package org.openxdata.client.controllers;

import java.util.List;

import org.openxdata.client.views.ItemAccessListField;
import org.openxdata.server.admin.model.exception.OpenXDataValidationException;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ItemAccessController<M extends ModelData> {

	/**
	 * Retrieves a page of the items currently mapped
	 * @param pagingLoadConfig  GXT PagingLoadConfig to specify the page size and number
	 * @param callback containing a PagingLoadResult for M to display in the next page
	 */
	public void getMappedData(PagingLoadConfig pagingLoadConfig, 
			final AsyncCallback<com.extjs.gxt.ui.client.data.PagingLoadResult<M>> callback);

	
	/**
	 * Retrieves a page of the items currently NOT mapped
	 * @param pagingLoadConfig GXT PagingLoadConfig to specify the page size and number
	 * @param callback containing a PagingLoadResult for M to display in the next page
	 */
	public void getUnMappedData(PagingLoadConfig pagingLoadConfig, 
			final AsyncCallback<com.extjs.gxt.ui.client.data.PagingLoadResult<M>> callback);
	
	/**
	 * Adds specified items to the mapped list and removes from the unmapped list
	 * @param itemsToAdd List of M to add to the mapping
	 * @param itemAccessListField UI component used for callback updates
	 * @throws OpenXDataValidationException if any validation exception occurs when adding mapping
	 */
	public void addMapping(List<M> itemsToAdd, final ItemAccessListField<M> itemAccessListField) throws OpenXDataValidationException ;
	
	/**
	 * Deletes specified items from the mapped list and adds to the unmapped list
	 * @param itemsToDelete List of M to remove from the mapping
	 * @param itemAccessListField UI component used for callback updates
	 * @throws OpenXDataValidationException if any validation exception occurs when deleting mapping
	 */
	public void deleteMapping(List<M> itemsToDelete, final ItemAccessListField<M> itemAccessListField) throws OpenXDataValidationException ;
	
}
