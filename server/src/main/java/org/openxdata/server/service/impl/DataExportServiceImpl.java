package org.openxdata.server.service.impl;

import java.util.Date;
import java.util.List;

import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.SettingGroup;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.dao.DataExportDAO;
import org.openxdata.server.dao.SettingGroupDAO;
import org.openxdata.server.service.DataExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * The concrete implementation of the data export service.
 * Note: there is no security for this service because it is
 * used internally. Also when the task runs, it does not have access
 * to a logged in user.
 * 
 * @author daniel
 */
@Service("dataExportService")
@Transactional
public class DataExportServiceImpl implements DataExportService {

	@Autowired
    private DataExportDAO dataExportDao;
	
	@Autowired
	private SettingGroupDAO settingGroupDao;

    public DataExportServiceImpl() {
    }

	public void setDao(DataExportDAO dataExportDAO) {
		this.dataExportDao = dataExportDAO;
	}
	
	@Override
	@Transactional(readOnly=true)
	public FormDefVersion getFormDefVersion(Integer formDefVersionId){
		return dataExportDao.getFormDefVersion(formDefVersionId);
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<FormData> getFormDataToExport(Integer exporterBitFlag){
		return dataExportDao.getFormDataToExport(exporterBitFlag);
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<Object[]> getFormDataWithAuditing(Integer formDefVersionId, Date fromDate, Date toDate, Integer userId){
		return dataExportDao.getFormDataWithAuditing(formDefVersionId, fromDate, toDate, userId);
	}

	@Override
	public void setFormDataExported(FormData formData, Integer exporterBitFlag) {
		dataExportDao.setFormDataExported(formData, exporterBitFlag);
	}
	
	@Override
	@Transactional(readOnly=true)
	public FormDef getFormDef(Integer formId){
		return dataExportDao.getFormDef(formId);
	}
	
	@Override
	@Transactional(readOnly=true)
	public StudyDef getStudyDef(Integer studyId){
		return dataExportDao.getStudyDef(studyId);
	}

	@Override
	public SettingGroup getDateSettings() {
		return settingGroupDao.getSettingGroup("Date");
	}
}
