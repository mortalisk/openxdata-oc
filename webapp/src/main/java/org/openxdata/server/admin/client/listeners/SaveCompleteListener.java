package org.openxdata.server.admin.client.listeners;

import java.util.List;
import org.openxdata.server.admin.model.Editable;

/**
 * Interface used to communicate to classes which want to listen
 * to the completion of saving of multiple items in a bulk.
 * This is called by the SaveAsyncCallback
 * 
 * @author daniel
 *
 */
public interface SaveCompleteListener {
	
	/**
	 * Called when the saving has completed.
	 * 
	 * @param modifiedList the list of items which were just edited or modified.
	 * @param deletedList the list of items which were deleted.
	 */
	public void onSaveComplete(List<? extends Editable> modifiedList, List<? extends Editable> deletedList);
}
