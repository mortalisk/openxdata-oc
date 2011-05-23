package org.openxdata.server.dao;

import java.util.List;

import org.openxdata.server.admin.model.Setting;

/**
 * Provides data access 
 * services to the <code>Setting service</code>.
 * 
 *
 */
public interface SettingDAO extends BaseDAO<Setting> {

	/**
	 * Gets the value of a setting with a given name.
	 * 
	 * @param name the name of the setting whose value to get.
	 * @return the value of the setting.
	 */
	List<Setting> getSettings();
	
	/**
	 * Saves a setting to the database.
	 * 
	 * @param setting the setting to save.
	 */
	void saveSetting(Setting setting);
	
	/**
	 * Deletes a setting from the database.
	 * 
	 * @param setting the setting to delete.
	 */
	void deleteSetting(Setting setting);
	
	/**
	 * Gets the value of a setting with a given name.
	 * 
	 * @param name the setting name.
	 * @return the value of the setting.
	 */
	public String getSetting(String name);
	
	/**
	 * Gets the value of a setting with a given name.
	 * 
	 * @param name the setting name.
	 * @param the default Value to return if the setting is not found.
	 * 
	 * @return the value of the setting.
	 */
	public String getSetting(String name, String defaultValue);
	
}
