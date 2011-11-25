package org.openxdata.server.dao;

import java.math.BigInteger;
import java.util.List;

import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;

/**
 * Provides data access services to the study manager service.
 * 
 * @author daniel
 *
 */
public interface EditableDAO extends BaseDAO<FormDef> {	
		
	/** 
	 * Checks if a <code>Editable</code> has data collected for it. 
	 *  
	 * @param item  Can be <code>Study, Form, or Form Version.</code> 
	 * @return true if it has, else false. 
	 */ 
	 Boolean hasEditableData(Editable item); 

	
	/**
	 * Get the response data for a form 
	 * @param formBinding the binding for the form (translates to the table name)
	 * @param questionBindings the quesions in the form (translates to the table column names)
	 * @param pagingLoadConfig paging related settings
	 * @return 
	 */
	List<Object[]> getResponseData(String formBinding, String[] questionBindings, PagingLoadConfig pagingLoadConfig);

	/**
	 * Get the number of responses for a form
	 * 
	 * @param formBinding
	 * @return
	 */
	BigInteger getNumberOfResponses(String formBinding);
}
