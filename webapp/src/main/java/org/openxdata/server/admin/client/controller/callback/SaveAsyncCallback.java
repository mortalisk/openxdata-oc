package org.openxdata.server.admin.client.controller.callback;

import java.util.List;

import org.openxdata.server.admin.client.listeners.SaveCompleteListener;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.model.Editable;

/**
 * A custom call back used when saving data, in a batch, asynchronously.
 * This call back enables us to do multiple calls to save and then on
 * reaching the end of the batch, it automatically notifies the user about
 * either the success or failure of saving.
 * 
 * @author daniel
 * @author Angel
 *
 */
public class SaveAsyncCallback extends OpenXDataAsyncCallback<Void>{

	/** The success message. */
	private String successMessage;
	
	/** The failure message. */
	private String failureMessage;
	
	/** The total number of objects we are supposed to save. */
	private int objectCount;
	
	/** The total number of objects we have attempted to save so far. */
	private int currentCount = 0;
	
	/** The total number of object we have so far saved successfully. */
	private int successCount = 0;
	
	/** The item we are currently trying to save. */
	private Editable editable;
	
	/** The list of items that have been edited or are new (made dirty). */
	List<?extends Editable> modifiedList;
	
	/** The list of items that have been deleted. */
	List<?extends Editable> deletedList;
	
	/** Reference to the listener to call when we have finished attempting to save all items. */
	private SaveCompleteListener saveCompleteListener;	
	
	/**
	 * Creates a new instance of the call back.
	 * 
	 * @param objectCount the total number of objects we are trying to save.
	 * @param successMessage the success message.
	 * @param failureMessage the failure error message.
	 * @param modifiedList the list of items that have been modified or that are new.
	 * @param deletedList the list of items that have been deleted.
	 * @param saveCompleteListener the listener to the save completed event.
	 */
	public SaveAsyncCallback(int objectCount, String successMessage, String failureMessage,List<? extends Editable> modifiedList,List<? extends Editable> deletedList,
			SaveCompleteListener saveCompleteListener){
		
		this.objectCount = objectCount;
		this.successMessage = successMessage;
		this.failureMessage = failureMessage;
		this.modifiedList = modifiedList;
		this.deletedList = deletedList;
		this.saveCompleteListener = saveCompleteListener;
	}

	/**
	 * @see com.google.gwt.user.client.rpc#onSuccess(java.lang.Object)
	 */
    @Override
	public void onSuccess(Void result) {
    	
    	//Since object has been saved successfully, it is no longer dirty.
    	editable.setDirty(false);
    	editable.setHasErrors(false);
    	
    	currentCount++;
    	
    	//If all objects have been saved successfully, tell the user.
     	if(++successCount == objectCount)
    		Utilities.displayNotificationMessageAsynchronously(successMessage, "Done!", "_");
     	
     	//If we have finished attempting to save all the objects, 
     	//tell the API user that we are done.
     	if(currentCount == objectCount)
    		saveCompleteListener.onSaveComplete(modifiedList,deletedList);
    }
    
    /**
     * Sets the item we are currently trying to save.
     * 
     * @param editable the item we are saving.
     */
    public void setCurrentItem(Editable editable){
    	this.editable = editable;
    }

	@Override
	public void onOtherFailure(Throwable throwable) {
		currentCount++;
		editable.setHasErrors(true);
		
		failureMessage = throwable.getMessage();
		Utilities.displayFailureNotificationMessage(failureMessage);
    	
    	if(currentCount == objectCount)
    		saveCompleteListener.onSaveComplete(modifiedList,deletedList);
		
	}
}
