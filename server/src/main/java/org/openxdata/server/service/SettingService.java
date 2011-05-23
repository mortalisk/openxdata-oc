package org.openxdata.server.service;

import java.util.List;

import org.openxdata.server.admin.model.Setting;
import org.openxdata.server.admin.model.SettingGroup;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;

/**
 * This service is used for 
 * managing <code>Settings</code>.
 * 
 * @author Angel
 *
 */
public interface SettingService {
	
	/**
	 * Gets the value of a setting with a given name.
	 * 
	 * @param name the name of the setting whose value to get.
	 * @return the value of the setting.
	 */
	String getSetting(String name);
	
	/**
	 * Gets a list of settings together with their groups.
	 * 
	 * @return the setting group list.
	 */
	List<SettingGroup> getSettings();
	
	/**
	 * Saves a setting to the database.
	 * 
	 * @param setting the setting to save.
	 */
	void saveSetting(Setting setting);
	
	/**
	 * Saves a setting group to the database.
	 * 
	 * @param settingGroup the setting group to save.
	 */
	void saveSettingGroup(SettingGroup settingGroup);
	
	/**
	 * Deletes a setting from the database.
	 * 
	 * @param setting the setting to delete.
	 */
	void deleteSetting(Setting setting);
	
	/**
	 * Deletes a setting group from the database.
	 * 
	 * @param settingGroup the setting group to delete.
	 */
	void deleteSettingGroup(SettingGroup settingGroup);

	/**
	 * Retrieves a setting given a name.
	 * 
	 * @param settingName
	 * @param defaultValue
	 * @return Setting or defaultValue.
	 */
	String getSetting(String settingName, String defaultValue);

	/**
	 * Retrieves the SettingGroup with the specified name
	 * @param name String name of group
	 * @return SettingGroup or null if none found
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	SettingGroup getSettingGroup(String name);
}