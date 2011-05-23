package org.openxdata.server.admin.client.listeners;

import java.util.List;

/**
 * Interface used to communicate to classes which want to listen
 * to the completion of saving of multiple items in bulk.
 * 
 * @author daniel
 *
 */
public interface ISaveCompleteListener {
	
	/**
	 * Called when the saving has completed.
	 * 
	 * @param modifiedList the list of items which are just edited or modified.
	 * @param deletedList the list of items which were deleted.
	 */
	public void onSaveComplete(List<?> modifiedList,List<?> deletedList);
}
