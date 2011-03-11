package org.openxdata.server.admin.model;

public class Setting extends AbstractEditable{

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -9195376892324311798L;
	//TODO Ensure that this is a value the persistence layer will not assign.
	private int id = 0;
	private String name;
	private String description;
	private String value;
	
	/** The group to which this setting belongs. */
	private SettingGroup settingGroup;
	
	public Setting(){
		
	}
	
	public Setting(String name) {
		super();
		this.name = name;
	}

	public Setting(String name, String description, String value) {
		super();
		this.name = name;
		this.description = description;
		this.value = value;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the id
	 */
	@Override
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	@Override
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public boolean isNew(){
		return id == 0;
	}
	
	@Override
	public void setDirty(boolean dirty){
		super.setDirty(dirty);
		
		if(settingGroup != null)
			settingGroup.setDirty(dirty);
	}
	
	public SettingGroup getSettingGroup() {
		return settingGroup;
	}

	public void setSettingGroup(SettingGroup settingGroup) {
		this.settingGroup = settingGroup;
	}
}
