package org.openxdata.server.dao.hibernate;

import java.util.List;

import org.openxdata.server.admin.model.SettingGroup;
import org.openxdata.server.dao.SettingGroupDAO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 */
@Repository("settingGroupDAO")
public class HibernateSettingGroupDAO extends BaseDAOImpl<SettingGroup> implements SettingGroupDAO{

	@Override
	public void deleteSettingGroup(SettingGroup settingGroup) {
		remove(settingGroup);
	}

	@Override
	@Transactional(readOnly=true)
	public List<SettingGroup> getSettingGroups() {
		return findAll();
	}

	@Override
	public void saveSettingGroup(SettingGroup settingGroup) {
		save(settingGroup);		
	}

	@Override
	@Transactional(readOnly=true)
	public SettingGroup getSettingGroup(String name) {
		return searchUniqueByPropertyEqual("name", name);
	}
}
