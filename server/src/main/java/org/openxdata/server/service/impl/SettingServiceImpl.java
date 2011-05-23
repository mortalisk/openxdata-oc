package org.openxdata.server.service.impl;

import java.util.List;

import org.openxdata.server.admin.model.Setting;
import org.openxdata.server.admin.model.SettingGroup;
import org.openxdata.server.dao.SettingDAO;
import org.openxdata.server.dao.SettingGroupDAO;
import org.openxdata.server.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation for <code>Setting Service</code>.
 * 
 *
 */
@Service("settingService")
@Transactional
public class SettingServiceImpl implements SettingService {

	@Autowired
    private SettingDAO settingDAO;
	
	@Autowired
	private SettingGroupDAO settingGroupDAO;

    public void setSettingDAO(SettingDAO settingDAO) {
        this.settingDAO = settingDAO;
    }

    /* (non-Javadoc)
     * @see org.openxdata.server.service.SettingService#deleteSetting(org.openxdata.server.admin.model.Setting)
     */
    @Override
    @Secured("Perm_Delete_Settings")
	public void deleteSetting(Setting setting) {
        settingDAO.deleteSetting(setting);
    }

    /* (non-Javadoc)
     * @see org.openxdata.server.service.SettingService#deleteSettingGroup(org.openxdata.server.admin.model.SettingGroup)
     */
    @Override
    @Secured("Perm_Delete_SettingsGroup")
	public void deleteSettingGroup(SettingGroup settingGroup) {
    	settingGroupDAO.deleteSettingGroup(settingGroup);
    }

    /* (non-Javadoc)
     * @see org.openxdata.server.service.SettingService#getSetting(java.lang.String)
     */
    @Override
	@Transactional(readOnly=true)
	// note: no security required to read settings
    public String getSetting(String name) {
        return settingDAO.getSetting(name);
    }

    /* (non-Javadoc)
     * @see org.openxdata.server.service.SettingService#getSettings()
     */
    @Override
	@Transactional(readOnly=true)
	// note: no security required to read settings
    public List<SettingGroup> getSettings() {
        return settingGroupDAO.getSettingGroups();
    }

    /* (non-Javadoc)
     * @see org.openxdata.server.service.SettingService#saveSetting(org.openxdata.server.admin.model.Setting)
     */
    @Override
    @Secured("Perm_Add_Settings")
	public void saveSetting(Setting setting) {
        settingDAO.saveSetting(setting);
    }

    /* (non-Javadoc)
     * @see org.openxdata.server.service.SettingService#saveSettingGroup(org.openxdata.server.admin.model.SettingGroup)
     */
    @Override
    @Secured("Perm_Add_SettingsGroup")
	public void saveSettingGroup(SettingGroup settingGroup) {
    	settingGroupDAO.saveSettingGroup(settingGroup);
    }
    
    @Override
	@Transactional(readOnly=true)
	// note: no security required to read settings
	public String getSetting(String name, String defaultValue){
		return settingDAO.getSetting(name, defaultValue);
	}

	@Override
	@Transactional(readOnly=true)
	// note: no security required to read settings
	public SettingGroup getSettingGroup(String name) {
		return settingGroupDAO.getSettingGroup(name);
	}
}
