package org.openxdata.server.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openxdata.oc.transport.OpenClinicaSoapClientImpl;
import org.openxdata.oc.transport.factory.ConnectionURLFactory;
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
@Service("OpenClinicaService")
public class OpenclinicaServiceImpl implements OpenclinicaService {

	@Autowired
	private StudyDAO studyDAO;	
	
	@Autowired
	private FormDataDAO formDataDAO;
	
	@Autowired
	private EditableDAO editableDAO;
	
	@Autowired
	private SettingDAO settingDAO;
	
	private OpenClinicaSoapClientImpl getClient() {
		
		OpenClinicaSoapClientImpl client = null;
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
		
		Set<OpenclinicaStudy> returnStudies = new HashSet<OpenclinicaStudy>();
		List<org.openxdata.oc.model.OpenclinicaStudy> studies = getClient().listAll();
		List<StudyDef> oxdStudies = studyDAO.getStudies();
		
		try{
			
			// Add only unique studies not previously downloaded.
			for (org.openxdata.oc.model.OpenclinicaStudy xStudy : studies) {
				for (StudyDef def : oxdStudies) {
					if (!def.getName().equals(xStudy.getName())) {
						studies.remove(xStudy);
					}
				}
			}

			for (org.openxdata.oc.model.OpenclinicaStudy xStudy : studies) {
				OpenclinicaStudy ocStudy = new OpenclinicaStudy();
				ocStudy.setName(xStudy.getName());
				ocStudy.setOID(xStudy.getOID());
				ocStudy.setIdentifier(xStudy.getIdentifier());
				ocStudy.setSubjects(getClient().getSubjectKeys(xStudy.getIdentifier()));

				returnStudies.add(ocStudy);
			}
		}catch(Exception ex){
			throw new UnexpectedException(ex.getMessage());
		}

		return returnStudies;
	}

	@Override
	public String importOpenClinicaStudy(String identifier) throws UnexpectedException {
		
		Collection<String> subjectKeys = getStudySubjects(identifier);
		
		String xml = "";
		if(subjectKeys != null && subjectKeys.size() > 0){
			xml = getClient().getOpenxdataForm(identifier, subjectKeys);
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
}
