package org.openxdata.server.admin.client.listeners;

/**
 * This interface is used for listening to events in the file
 * selection dialog boxes or screens.
 * 
 * @author daniel
 *
 */
public interface GetFileNameDialogEventListener {
	
	/**
	 * Called when a file name has been selected.
	 * 
	 * @param fileName the selected file name.
	 */
	public void onSetFileName(String fileName);
}