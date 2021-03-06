package org.openxdata.server.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.openxdata.oc.model.ConvertedOpenclinicaStudy;
import org.openxdata.oc.transport.OpenClinicaSoapClient;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.OpenclinicaStudy;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.dao.EditableDAO;
import org.openxdata.server.dao.FormDataDAO;
import org.openxdata.server.dao.StudyDAO;
import org.openxdata.server.service.StudyManagerService;

@RunWith(MockitoJUnitRunner.class)
public class OpenClinicaServiceImplTest {
	
	@Mock private StudyDAO studyDAO;
	
	@Mock private FormDataDAO formDataDAO;
	
	@Mock private EditableDAO editableDAO;
   
    @Mock private StudyManagerService studyService;
   
    @Mock private OpenClinicaSoapClient soapClient;
    
    @InjectMocks private OpenclinicaServiceImpl openClinicaService = new OpenclinicaServiceImpl();

	
    List<String> subjects = new ArrayList<String>();
    List<StudyDef> studies = new ArrayList<StudyDef>();
    List<FormData> formDataList = new ArrayList<FormData>();
    List<ConvertedOpenclinicaStudy> openClinicaConvertedStudies = new ArrayList<ConvertedOpenclinicaStudy>();
	
    @SuppressWarnings("unchecked")
	@Before public void setUp() throws Exception {
    	
    	initSubjects();
    	initFormDataList();
    	initStudyDefinitions();
    	initConvertedOpenClinicaStudies();
    	
    	StudyDef study = createStudy();
				
		Mockito.when(studyDAO.getStudies()).thenReturn(studies);
    	Mockito.when(studyService.getStudyKey(Mockito.anyInt())).thenReturn("key");
    	Mockito.when(studyDAO.getStudy(Mockito.anyString())).thenReturn(study);
    	Mockito.when(editableDAO.hasEditableData(Mockito.any(Editable.class))).thenReturn(Boolean.TRUE);
    	
    	Mockito.when(soapClient.listAll()).thenReturn(openClinicaConvertedStudies);
    	Mockito.when(soapClient.getSubjectKeys(Mockito.anyString())).thenReturn(subjects);
    	
    	URL resource = this.getClass().getClassLoader().getResource("org/openxdata/server/service/impl/openclinicaGetMetaDataSoapResponse.xml");
		String odmMetaData = FileUtils.readFileToString(new File(resource.toURI()), "UTF-8");
		Mockito.when(soapClient.getMetadata(Mockito.anyString())).thenReturn(odmMetaData);
				
		Mockito.when(formDataDAO.getFormDataList(Mockito.any(FormDef.class))).thenReturn(formDataList);
		Mockito.when(soapClient.importData(Mockito.anyCollection())).thenReturn("Success");
				
    }

	private void initFormDataList() {
		
		FormData formData = new FormData();
		FormData formData2 = new FormData();
		
		formDataList.add(formData);
		formDataList.add(formData2);
	}

	private StudyDef createStudy() {
		
		StudyDef study = new StudyDef();
		
		FormDef form = new FormDef();

		study.addForm(form);

		return study;
	}

	private void initSubjects() {
		subjects.add("Jorn");
		subjects.add("Janne");
		subjects.add("Morten");
		subjects.add("Jonny");
	}
	
	private void initStudyDefinitions() {
		// Study definitions
		StudyDef study = new StudyDef();
		study.setName("study");
		study.setStudyKey("oid");
		
		studies.add(study);
		
	}

	private void initConvertedOpenClinicaStudies() {
		ConvertedOpenclinicaStudy convertedStudy = new ConvertedOpenclinicaStudy();
		convertedStudy.setIdentifier("id");
		convertedStudy.setOID("oid");
		convertedStudy.setName("study");
		
		ConvertedOpenclinicaStudy convertedStudy2 = new ConvertedOpenclinicaStudy();
		convertedStudy2.setIdentifier("id2");
		convertedStudy2.setOID("oid2");
		convertedStudy2.setName("study2");
		
		openClinicaConvertedStudies.add(convertedStudy);
		openClinicaConvertedStudies.add(convertedStudy2);
	}
    
	@Test public void testHasStudyData(){
		
		String studyKey = studyService.getStudyKey(1);
		assertTrue(openClinicaService.hasStudyData(studyKey));
		
		Mockito.when(editableDAO.hasEditableData(Mockito.any(Editable.class))).thenReturn(Boolean.FALSE);
		String studyKey2 = studyService.getStudyKey(2);
		assertFalse(openClinicaService.hasStudyData(studyKey2));
	}
	
	@Test public void testGetOpenclinicaStudiesMustReturnCorrectNumberOfStudies(){
		
		List<OpenclinicaStudy> studies = openClinicaService.getOpenClinicaStudies();
		
		assertEquals(2, studies.size());
		assertEquals("study", studies.get(0).getName());
		assertEquals("study2", studies.get(1).getName());
	}
	
	@Test public void testGetOpenClinicaStudiesMustNotReturnDuplicateStudies(){
		
		List<OpenclinicaStudy> ocStudies = null;
		
		StudyDef study2 = new StudyDef();
		study2.setName("study2");
		study2.setStudyKey("oid2");
		
		studies.add(study2);
		
		ocStudies = openClinicaService.getOpenClinicaStudies();
		assertEquals(2, ocStudies.size());
		
	}
	
	@Test public void testGetSubjectsShouldReturnCorrectNumberOfSubjects(){
		
		List<String> studySubjects = openClinicaService.getStudySubjects("studyOID");
		
		assertEquals(4, studySubjects.size());
	}
	
	@Test public void testGetSubjectsShouldRetursValidSubjectKeys(){
		
		List<String> studySubjects = openClinicaService.getStudySubjects("studyOID");
		
		assertEquals("Jorn", studySubjects.get(0));
		assertEquals("Janne", studySubjects.get(1));
		assertEquals("Morten", studySubjects.get(2));
		assertEquals("Jonny", studySubjects.get(3));
	}
	
	@Test public void testExportDataShouldReturnSuccessMessage() {

		String message = openClinicaService.exportOpenClinicaStudyData("oid");
		assertEquals("Success", message);
	}
	
	@SuppressWarnings("unchecked")
	@Test public void testExportDataShouldShouldFailOnEmptyInstanceDataWithMessage() {

		Mockito.when(soapClient.importData(Mockito.anyCollection())).thenReturn("Fail");
		String message = openClinicaService.exportOpenClinicaStudyData("oid");
		assertEquals("Fail", message);
	}
}
