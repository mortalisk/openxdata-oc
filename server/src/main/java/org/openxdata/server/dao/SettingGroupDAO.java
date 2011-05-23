package org.openxdata.server.dao;

import java.util.List;

import org.openxdata.server.admin.model.SettingGroup;

/**
 * @author Angel
 *
 */
public interface SettingGroupDAO extends BaseDAO<SettingGroup> {

	/**
	 * Gets the value of a setting with a given name.
	 * 
	 * @param name the name of the setting whose value to get.
	 * @return the value of the setting.
	 */
	List<SettingGroup> getSettingGroups();
	
	/**
	 * Saves a setting to the database.
	 * 
	 * @param setting the setting to save.
	 */
	void saveSettingGroup(SettingGroup settingGroup);
	
	/**
	 * Deletes a setting group from the database.
	 * 
	 * @param settingGroup the setting group to delete.
	 */
	void deleteSettingGroup(SettingGroup settingGroup);
	
	/**
	 * Gets the SettingGroup with the specified name
	 * @param name String name of the group
	 * @return SettingGroup, null if none found
	 */
	SettingGroup getSettingGroup(String name);
}
