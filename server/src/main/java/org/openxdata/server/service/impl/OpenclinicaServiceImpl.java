package org.openxdata.server.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openxdata.oc.model.ConvertedOpenclinicaStudy;
import org.openxdata.oc.transport.OpenClinicaSoapClient;
import org.openxdata.oc.transport.factory.ConnectionFactory;
import org.openxdata.oc.transport.impl.OpenClinicaSoapClientImpl;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.OpenclinicaStudy;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.exception.UnexpectedException;
import org.openxdata.server.dao.EditableDAO;
import org.openxdata.server.dao.FormDataDAO;
import org.openxdata.server.dao.SettingDAO;
import org.openxdata.server.dao.StudyDAO;
import org.openxdata.server.service.OpenclinicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service("openClinicaService")
public class OpenclinicaServiceImpl implements OpenclinicaService {

	@Autowired
	private StudyDAO studyDAO;	
	
	@Autowired
	private FormDataDAO formDataDAO;
	
	@Autowired
	private EditableDAO editableDAO;
	
	@Autowired
	private SettingDAO settingDAO;

	private OpenClinicaSoapClient client;
	
	private OpenClinicaSoapClient getClient() {
		
		if(client == null){
			String host = settingDAO.getSetting("openClinicaWebServiceHost");
			String userName = settingDAO.getSetting("OpenClinicaUserName");
			String password = settingDAO.getSetting("OpenClinicaUserHashedPassword");
			
			ConnectionFactory factory = new ConnectionFactory(host);
			
			client = new OpenClinicaSoapClientImpl(userName, password, factory);
		}
		return client;
	}
	
	@Override
	@Transactional(readOnly=true)
	@Secured("Perm_View_Form_Data")
	public Boolean hasStudyData(String studyKey) {
		StudyDef study = studyDAO.getStudy(studyKey);
		return editableDAO.hasEditableData(study);
	}
	
	@Override
	public List<OpenclinicaStudy> getOpenClinicaStudies() {
		
		List<ConvertedOpenclinicaStudy> studies = getClient().listAll();
		List<OpenclinicaStudy> returnStudies = new ArrayList<OpenclinicaStudy>();
		
		try{
			
			List<StudyDef> openxdataStudies = studyDAO.getStudies();
			List<ConvertedOpenclinicaStudy> uniqueStudies = new ArrayList<ConvertedOpenclinicaStudy>();
			
			for (ConvertedOpenclinicaStudy study : studies) {
				if(!isStudyDownloaded(openxdataStudies, study)){
					uniqueStudies.add(study);
				}
			}

			convertToOpenXDataOCStudy(returnStudies, uniqueStudies);
			
		}catch(Exception ex){
			throw new UnexpectedException(ex);
		}

		return returnStudies;
	}

	private void convertToOpenXDataOCStudy(List<OpenclinicaStudy> returnStudies, List<ConvertedOpenclinicaStudy> uniqueStudies) {
		
		for (ConvertedOpenclinicaStudy study : uniqueStudies) {
			OpenclinicaStudy ocStudy = new OpenclinicaStudy();
			ocStudy.setName(study.getName());
			ocStudy.setOID(study.getOID());
			ocStudy.setIdentifier(study.getIdentifier());
			
			appendSubjects(study, ocStudy);

			returnStudies.add(ocStudy);
		}
	}

	private void appendSubjects(ConvertedOpenclinicaStudy study, OpenclinicaStudy ocStudy) {
		Collection<String> subjects = getClient().getSubjectKeys(study.getIdentifier());
		ocStudy.setSubjects(subjects);
	}

	private boolean isStudyDownloaded(List<StudyDef> studies, ConvertedOpenclinicaStudy study) {
				
		for (StudyDef def : studies) {
			
			String oxdStudyName = def.getName();
			String oxdStudyIdentifier = def.getStudyKey();
			
			String ocStudyName = study.getName();
			String ocStudyIdentifier = study.getIdentifier();
			
			if(oxdStudyName.equals(ocStudyName) && 
					oxdStudyIdentifier == ocStudyIdentifier){
				
				return true;
			}
		}
		return false;
	}

	@Override
	public String importOpenClinicaStudy(String identifier) throws UnexpectedException {
				
		String xml = (String) getClient().getOpenxdataForm(identifier);
		
		return xml;
	}
	
	@Override
	public List<String> getStudySubjects(String studyOID) throws UnexpectedException {
		List<String> subjects = new ArrayList<String>();
		try{
			Collection<String> returnedSubjects = getClient().getSubjectKeys(studyOID);
			for(String x : returnedSubjects){
				subjects.add(x);
			}
		}catch(Exception ex){
			throw new UnexpectedException(ex);
		}
		
		return subjects;
	}

	@Override
	public String exportOpenClinicaStudyData(String studyKey) {
		StudyDef study = studyDAO.getStudy(studyKey);
		List<String> allData = new ArrayList<String>(); 
		for (FormDef form : study.getForms()) {
			List<FormData> dataList = formDataDAO.getFormDataList(form);
			for (FormData formData: dataList) {
				allData.add(formData.getData());
			}
		}
		return (String) getClient().importData(allData);	
	}
}
