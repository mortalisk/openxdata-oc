package org.openxdata.server.admin.model;

public class Setting extends AbstractEditable{

	private static final long serialVersionUID = -9195376892324311798L;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getValue() {
		return value;
	}

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
