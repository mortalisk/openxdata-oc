package org.openxdata.server.admin.client.service;

import java.util.List;

import org.openxdata.server.admin.model.Setting;
import org.openxdata.server.admin.model.SettingGroup;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Defines the client side contract for the Setting Service.
 */
public interface SettingService extends RemoteService {

	/**
	 * Retrieves a <tt>Setting.</tt>
	 * 
	 * @param name Name of <tt>Setting</tt> to retrieve.
	 * @return <tt>Setting</tt> if it exists.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	String getSetting(String name) throws OpenXDataSecurityException;
	
	/**
	 * Fetches all the <tt>Settings</tt> in the system.
	 * 
	 * @return <tt>List</tt> of <tt>Settings.</tt>
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	List<SettingGroup> getSettings() throws OpenXDataSecurityException;
	
	/**
	 * Saves a dirty or new <tt>Setting.</tt>
	 * 
	 * @param setting <tt>Setting</tt> to save.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void saveSetting(Setting setting) throws OpenXDataSecurityException;
	
	/**
	 * Saves a dirty or new <tt>Setting Group.</tt>
	 * 
	 * @param settingGroup <tt>Setting Group</tt> to save.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void saveSettingGroup(SettingGroup settingGroup) throws OpenXDataSecurityException;
	
	/**
	 * Deletes a given <tt>Setting.</tt>
	 * 
	 * @param setting <tt>Setting</tt> to delete.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void deleteSetting(Setting setting) throws OpenXDataSecurityException;
	
	/**
	 * Deletes a given <tt>Setting Group.</tt>
	 * 
	 * @param settingGroup <tt>Setting Group</tt> to delete.
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	void deleteSettingGroup(SettingGroup settingGroup) throws OpenXDataSecurityException;
	
	/**
	 * Retrieves the SettingGroup with the specified name
	 * @param name String name of group
	 * @return SettingGroup or null if none found
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	SettingGroup getSettingGroup(String name) throws OpenXDataSecurityException;
}
