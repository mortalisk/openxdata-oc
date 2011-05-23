package org.openxdata.server.admin.client.listeners;


/**
 * Used for communicating login attempts to whoever is interested.
 * 
 * @author daniel
 *
 */
public interface LoginListener {
	
	/**
	 * Called when one attempts to login the application.
	 * 
	 * @param userName the user name.
	 * @param password the user password.
	 */
	void onLogin(String userName, String password);
}
