package org.openxdata.server.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
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
import org.openxdata.server.admin.model.OpenclinicaStudy;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.dao.EditableDAO;
import org.openxdata.server.dao.StudyDAO;
import org.openxdata.server.service.StudyManagerService;

@RunWith(MockitoJUnitRunner.class)
public class OpenClinicaServiceImplTest {
	
	@Mock private StudyDAO studyDAO;
	
	@Mock private EditableDAO editableDAO;
   
    @Mock private StudyManagerService studyService;
   
    @Mock private OpenClinicaSoapClient soapClient;
    
    @InjectMocks private OpenclinicaServiceImpl openClinicaService = new OpenclinicaServiceImpl();

    List<String> subjects = new ArrayList<String>();
    List<StudyDef> studies = new ArrayList<StudyDef>();
    List<ConvertedOpenclinicaStudy> openClinicaConvertedStudies = new ArrayList<ConvertedOpenclinicaStudy>();
	
    @Before public void setUp() throws Exception {
    	
    	initSubjects();
    	initStudyDefinitions();
    	initConvertedOpenClinicaStudies();
				
		Mockito.when(studyDAO.getStudies()).thenReturn(studies);
    	Mockito.when(studyService.getStudyKey(Mockito.anyInt())).thenReturn("key");
    	Mockito.when(studyDAO.getStudy(Mockito.anyString())).thenReturn(new StudyDef());
    	Mockito.when(editableDAO.hasEditableData(Mockito.any(Editable.class))).thenReturn(Boolean.TRUE);
    	
    	Mockito.when(soapClient.listAll()).thenReturn(openClinicaConvertedStudies);
    	Mockito.when(soapClient.getSubjectKeys(Mockito.anyString())).thenReturn(subjects);
    	
    	URL resource = this.getClass().getClassLoader().getResource("org/openxdata/server/service/impl/openclinicaGetMetaDataSoapResponse.xml");
		String odmMetaData = FileUtils.readFileToString(new File(resource.toURI()), "UTF-8");
		Mockito.when(soapClient.getMetadata(Mockito.anyString())).thenReturn(odmMetaData);
		
		URL resource2 = this.getClass().getClassLoader().getResource("org/openxdata/server/service/impl/convertedOpenXdataSampleForm.xml");
		String convertedStudyXML = FileUtils.readFileToString(new File(resource2.toURI()), "UTF-8");
		Mockito.when(soapClient.getOpenxdataForm(Mockito.anyString())).thenReturn(convertedStudyXML);
    				
    }

	private void initSubjects() {
		subjects.add("J¿rn");
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
	
	@Test public void testGetOpenclinicaStudiesMustNotReturnNull(){
		
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
	
	@Test public void testGetSubjectsShouldNotReturnNull(){
		
		List<String> studySubjects = openClinicaService.getStudySubjects("studyOID");
		
		assertEquals(4, studySubjects.size());
		assertEquals("J¿rn", studySubjects.get(0));
		assertEquals("Janne", studySubjects.get(1));
		assertEquals("Morten", studySubjects.get(2));
		assertEquals("Jonny", studySubjects.get(3));
	}
	
	@Test public void testImportOpenClinicaStudyShouldReturnCorrectXML() throws IOException, URISyntaxException{

		String convertedStudyXML = openClinicaService.importOpenClinicaStudy("oid");
		assertNotNull(convertedStudyXML);
	}
}
