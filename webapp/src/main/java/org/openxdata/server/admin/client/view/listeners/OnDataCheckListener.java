package org.openxdata.server.admin.client.view.listeners;

/**
 * 
 * This is an interface defines a contract for a member that intends to check if an object has data.
 * 
 */
public interface OnDataCheckListener {

	/**
	 * Member fired when data check has completed on the server.
	 * 
	 * @param hasData - boolean parameter indicating if data is present
	 * @param editableName - item name that the action is being performed on
	 */
	void onDataCheckComplete(boolean hasData, String editableName);
	
}
