package org.openxdata.server.admin.client.service;

import java.util.List;

import org.openxdata.server.admin.model.Locale;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Defines the client side contract for the Utility Service.
 */
public interface UtilityService extends RemoteService {

	/**
	 * Fetches all the <tt>Locales</tt> in the system.
	 * 
	 * @return <tt>List</tt> of <tt>Locales.</tt>
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt> 
	 */
	List<Locale> getLocales() throws OpenXDataSecurityException;
	
	/**
	 * Saves dirty or new <tt>Locales.</tt>
	 * 
	 * @param locales <tt>List</tt> of <tt>Locales.</tt>
	 * @return <tt>True only and only if the Locales</tt> are saved successfully.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt> 
	 */
	void saveLocale(List<Locale> locales) throws OpenXDataSecurityException;
	
	/**
	 * Deletes a given <tt>Locale.</tt>
	 * 
	 * @param locale <tt>Locale</tt> to delete.
	 * @return <tt>True only and only if the Locales</tt> are saved successfully.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt> 
	 */
	void deleteLocale(Locale locale) throws OpenXDataSecurityException;
		
	/**
	 * Installs the Mobile Application onto the mobile devices.
	 * 
	 * @param phonenos List of phones numbers for the Mobile devices.
	 * @param url URL where they will pick the mobile application.
	 * @param modemComPort Modem Port to send the WAP PUSH SMS through.
	 * @param modemBaudRate The Baud Rate to User.
	 * @param promptText The prompt text that will appear on the <tt>User's</tt> device to prompt them to download tha application.
	 * @return
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt> 
	 */
	Boolean installMobileApp(List<String> phonenos, String url, String modemComPort, int modemBaudRate, 
			String promptText) throws OpenXDataSecurityException;
	
	/**
	 * Checks if the a given password matches the <tt>User's</tt> default password that matches with the system.
	 * 
	 * @param username <tt>User</tt> we matching on.
	 * @param password Password we checking for match against the old password.
	 * @return <tt>True only and only if the given password matches the password of the User.</tt>
	 * 
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt> 
	 */
	Boolean checkIfPasswordsMatchOnAdministrator(String username, String password) throws OpenXDataSecurityException;
	
}
