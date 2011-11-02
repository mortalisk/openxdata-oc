package org.openxdata.server.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openxdata.oc.model.ConvertedOpenclinicaStudy;
import org.openxdata.oc.transport.OpenClinicaSoapClient;
import org.openxdata.oc.transport.factory.ConnectionURLFactory;
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
			
			ConnectionURLFactory factory = new ConnectionURLFactory(host);
			
			client = new OpenClinicaSoapClientImpl(userName, password);
			client.setConnectionFactory(factory);
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
	public Set<OpenclinicaStudy> getOpenClinicaStudies() {
		
		List<ConvertedOpenclinicaStudy> studies = getClient().listAll();
		Set<OpenclinicaStudy> returnStudies = new HashSet<OpenclinicaStudy>();
		
		try{
			
			List<StudyDef> openxdataStudies = studyDAO.getStudies();
			List<ConvertedOpenclinicaStudy> uniqueStudies = new ArrayList<ConvertedOpenclinicaStudy>();
			
			// Add only unique studies not previously downloaded.
			for (ConvertedOpenclinicaStudy study : studies) {
				if(!isStudyDownloaded(openxdataStudies, study)){
					uniqueStudies.add(study);
				}
			}

			convertToOpenXDataOCStudy(returnStudies, uniqueStudies);
			
		}catch(Exception ex){
			throw new UnexpectedException(ex.getMessage());
		}

		return returnStudies;
	}

	private void convertToOpenXDataOCStudy(Set<OpenclinicaStudy> returnStudies,
			List<ConvertedOpenclinicaStudy> uniqueStudies) {
		for (ConvertedOpenclinicaStudy study : uniqueStudies) {
			OpenclinicaStudy ocStudy = new OpenclinicaStudy();
			ocStudy.setName(study.getName());
			ocStudy.setOID(study.getOID());
			ocStudy.setIdentifier(study.getIdentifier());
			
			Collection<String> subjects = getClient().getSubjectKeys(study.getIdentifier());
			ocStudy.setSubjects(subjects);

			returnStudies.add(ocStudy);
		}
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
		
		Collection<String> subjectKeys = getStudySubjects(identifier);
		
		String xml = "";
		if(subjectKeys != null && subjectKeys.size() > 0){
			xml = (String) getClient().getOpenxdataForm(identifier);
		}
		
		return xml;
	}
	
	@Override
	public Set<String> getStudySubjects(String studyOID) throws UnexpectedException {
		Set<String> subjects = new HashSet<String>();
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
	public void exportOpenClinicaStudyData(String studyKey) {
		StudyDef study = studyDAO.getStudy(studyKey);
		List<String> allData = new ArrayList<String>(); 
		for (FormDef form : study.getForms()) {
			List<FormData> dataList = formDataDAO.getFormDataList(form);
			for (FormData formData: dataList) {
				allData.add(formData.getData());
			}
		}
		getClient().importData(allData);	
	}

	@Override
	public void setClient(OpenClinicaSoapClient client) {
		this.client = (OpenClinicaSoapClientImpl) client;
	}

	
	public void setStudyDAO(StudyDAO studyDAO) {
		this.studyDAO = studyDAO;
	}

	public void setEditableDAO(EditableDAO editableDAO) {
		this.editableDAO = editableDAO;
	}
}
