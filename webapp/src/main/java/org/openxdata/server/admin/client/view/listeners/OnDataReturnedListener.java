package org.openxdata.server.admin.client.view.listeners;

import org.openxdata.server.admin.model.exception.OpenXDataException;

/**
 * 
 * This is the interface defines a contract for a member that 
 * intends to receive notification that data has been returned from the server.
 * 
 * @author Angel
 */
public interface OnDataReturnedListener {
	
	/**
	 * 
	 * Member fired when data check is complete on the server side
	 * 
	 * @param hasData - result indicating has data or not
	 * @throws OpenXDataException <code>if(hasData) !discerned </code>
	 */
	void dataReturned(Boolean hasData) throws OpenXDataException;
}
