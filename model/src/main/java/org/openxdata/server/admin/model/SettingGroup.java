package org.openxdata.server.admin.model;

import java.util.ArrayList;
import java.util.List;

public class SettingGroup extends AbstractEditable{

	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = 1181738467655537369L;
	private int settingGroupId = 0;
	private SettingGroup parentSettingGroup;
	private String name;
	private String description;
	private List<SettingGroup> groups = new ArrayList<SettingGroup>();
	private List<Setting> settings = new ArrayList<Setting>();

	
	public SettingGroup(){
		
	}
	
	public SettingGroup(String name){
		this.name = name;
	}
	
	public int getSettingGroupId() {
		return settingGroupId;
	}
	
	@Override
	public int getId() {
		return settingGroupId;
	}
	
	public void setSettingGroupId(int settingGroupId) {
		this.settingGroupId = settingGroupId;
	}
	
	public SettingGroup getParentSettingGroup() {
		return parentSettingGroup;
	}
	
	public void setParentSettingGroup(SettingGroup parentSettingGroup) {
		this.parentSettingGroup = parentSettingGroup;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<SettingGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<SettingGroup> groups) {
		this.groups = groups;
	}

	public void addSettingGroup(SettingGroup settingGroup){
		groups.add(settingGroup);
	}
	
	public void addSetting(Setting setting){
		settings.add(setting);
	}
	
	public List<Setting> getSettings() {
		return settings;
	}

	public void setSettings(List<Setting> settings) {
		this.settings = settings;
	}
	
	public void removeSetting(Setting setting){
		settings.remove(setting);
	}

	@Override
	public boolean isNew(){
		if(settingGroupId == 0)
			return true;
		
		if(settings != null){
			for(Setting setting : settings){
				if(setting.isNew())
					return true;
			}
		}
		
		if(groups != null){
			for(SettingGroup group : groups){
				if(group.isNew())
					return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void setDirty(boolean dirty){
		super.setDirty(dirty);
		
		if(parentSettingGroup != null)
			parentSettingGroup.setDirty(dirty);
	}
	
	@Override
	public String toString(){
		return name;
	}
	
	
	/**
	 * Gets the value of a setting with a given name from a given list of setting groups.
	 * 
	 * @param name the setting name.
	 * @param settingGroups the setting groups list.
	 * @param defaultValue the default value to return if the setting does not exist.
	 * @return the setting value if found, else defaultValue.
	 */
	public static String getSetting(String name, List<SettingGroup> settingGroups, String defaultValue){
		Setting setting = getSettingFromGroups(settingGroups,name);
		if(setting != null)
			return setting.getValue();
		
		return defaultValue;
	}

	/**
	 * Gets a setting with a given name from a setting groups list.
	 * 
	 * @param settingGroups the setting groups list.
	 * @param name the setting name.
	 * @return the setting if found, else null.
	 */
	public static Setting getSettingFromGroups(List<SettingGroup> settingGroups, String name){
		if(settingGroups == null)
			return null;

		for(SettingGroup settingGroup : settingGroups){
			Setting setting  = getSetting(settingGroup.getSettings(),name);
			if(setting != null)
				return setting;
		}

		return null;
	}

	/**
	 * Gets a setting with a given name from a settings list.
	 * 
	 * @param settings the settings list.
	 * @param name the setting name.
	 * @return the setting if found, else null.
	 */
	private static Setting getSetting(List<Setting> settings, String name){
		if(settings == null)
			return null;

		for(Setting setting : settings){
			if(setting.getName().equals(name))
				return setting;
		}

		return null;
	}
}
