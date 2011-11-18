package org.openxdata.server.service.impl;

import groovy.util.slurpersupport.NodeChild;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.openxdata.oc.model.ConvertedOpenclinicaStudy;
import org.openxdata.oc.transport.OpenClinicaSoapClient;
import org.openxdata.oc.transport.factory.ConnectionFactory;
import org.openxdata.oc.transport.impl.OpenClinicaSoapClientImpl;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.OpenclinicaStudy;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.UnexpectedException;
import org.openxdata.server.dao.EditableDAO;
import org.openxdata.server.dao.FormDataDAO;
import org.openxdata.server.dao.SettingDAO;
import org.openxdata.server.dao.StudyDAO;
import org.openxdata.server.security.util.OpenXDataSecurityUtil;
import org.openxdata.server.service.OpenclinicaService;
import org.openxdata.server.xform.StudyImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private Logger log = LoggerFactory.getLogger(OpenclinicaServiceImpl.class);
	
	private OpenClinicaSoapClient getClient() {
		
		if(client == null){
			
			log.info("OXD: Initializing client for first time use.");
			
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
		
		log.info("OXD: Fetching available OpenClinica studies.");
		
		List<ConvertedOpenclinicaStudy> studies = getClient().listAll();
		
		log.info("OXD: " + studies.size() + "studies and returned to OpenXdata.");
		
		List<OpenclinicaStudy> returnStudies = new ArrayList<OpenclinicaStudy>();
		
		try{
			
			List<StudyDef> openxdataStudies = studyDAO.getStudies();
			List<ConvertedOpenclinicaStudy> uniqueStudies = new ArrayList<ConvertedOpenclinicaStudy>();
			
			for (ConvertedOpenclinicaStudy study : studies) {
				log.info("OXD: Checking duplicate studies.");
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
		
		log.info("OXD: Appending " + subjects.size() + "subjects to the study");
		
		ocStudy.setSubjects(subjects);
	}

	private boolean isStudyDownloaded(List<StudyDef> studies, ConvertedOpenclinicaStudy study) {
				
		for (StudyDef def : studies) {
			
			String oxdStudyName = def.getName();
			String oxdStudyIdentifier = def.getStudyKey();
			
			String ocStudyName = study.getName();
			String ocStudyIdentifier = study.getIdentifier();
			
			if(oxdStudyName.equals(ocStudyName) && 
					oxdStudyIdentifier == ocStudyIdentifier) {
				
				return true;
			}
		}
		return false;
	}

	@Override
	public StudyDef importOpenClinicaStudy(String identifier) throws UnexpectedException {
				
		NodeChild xml = (NodeChild) getClient().getOpenxdataForm(identifier);
		
		log.info("OXD: Converting Xform to study definition.");
		StudyImporter importer = new StudyImporter(xml);
		StudyDef study = createStudy(importer);
		
		log.info("OXD: Saving converted study definition.");
		studyDAO.saveStudy(study);
		
		return study;
	}

	private StudyDef createStudy(StudyImporter importer) {
		
		Date dateCreated = new Date();
		User creator = OpenXDataSecurityUtil.getLoggedInUser();
		
		StudyDef study = (StudyDef) importer.extractStudy();
		study.setCreator(creator);
		study.setDateCreated(dateCreated);
		
		List<FormDef> forms = study.getForms();
		
		for(FormDef form : forms) {
			
			form.setStudy(study);
			form.setCreator(creator);
			form.setDateCreated(dateCreated);
			
			setFormVersionProperties(form, dateCreated, creator);
		}
		
		return study;
	}
	
	private void setFormVersionProperties(FormDef form, Date dateCreated, User creator) {
		List<FormDefVersion> versions = form.getVersions();
		
		for(FormDefVersion version : versions) {
			
			version.setFormDef(form);
			version.setCreator(creator);
			version.setDateCreated(dateCreated);
		}
	}

	@Override
	public List<String> getStudySubjects(String studyOID) throws UnexpectedException {
		try{
			List<String> subjects = fetchSubjects(studyOID);
			return subjects;
		}catch(Exception ex){
			throw new UnexpectedException(ex);
		}
	}

	private List<String> fetchSubjects(String studyOID) {
		
		List<String> subjects = new ArrayList<String>();

		log.info("OXD: Fetching subjects.");
		
		Collection<String> returnedSubjects = getClient().getSubjectKeys(studyOID);
		for(String x : returnedSubjects){
			subjects.add(x);
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
