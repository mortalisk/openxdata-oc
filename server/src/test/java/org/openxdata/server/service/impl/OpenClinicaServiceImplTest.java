package org.openxdata.server.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
   
    @Mock OpenClinicaSoapClient openClinicaSoapClient;
    
    @InjectMocks private OpenclinicaServiceImpl openClinicaService = new OpenclinicaServiceImpl();

    List<ConvertedOpenclinicaStudy> openClinicaConvertedStudies = new ArrayList<ConvertedOpenclinicaStudy>();
	
    @Before public void setUp() {
    	
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
				
		List<StudyDef> studies = new ArrayList<StudyDef>();
		StudyDef study = new StudyDef();
		study.setName("nameX");
		study.setStudyKey("keyX");
		
		StudyDef study2 = new StudyDef();
		study2.setName("nameX2");
		study2.setStudyKey("keyX2");
		
		studies.add(study);
		studies.add(study2);
				
		Mockito.when(studyDAO.getStudies()).thenReturn(studies);
    	Mockito.when(studyService.getStudyKey(Mockito.anyInt())).thenReturn("key");
    	Mockito.when(studyDAO.getStudy(Mockito.anyString())).thenReturn(new StudyDef());
    	Mockito.when(editableDAO.hasEditableData(Mockito.any(Editable.class))).thenReturn(true);
    	
    	Mockito.when(openClinicaSoapClient.listAll()).thenReturn(openClinicaConvertedStudies);
    	
    	openClinicaService.setStudyDAO(studyDAO);
    	openClinicaService.setEditableDAO(editableDAO);
				
    }
    
	@Test public void testHasStudyData(){
		
		String studyKey = studyService.getStudyKey(1);
		assertTrue(openClinicaService.hasStudyData(studyKey));
		
		Mockito.when(editableDAO.hasEditableData(Mockito.any(Editable.class))).thenReturn(false);
		String studyKey2 = studyService.getStudyKey(2);
		assertFalse(openClinicaService.hasStudyData(studyKey2));
	}
	
	@Test public void testGetOpenclinicaStudiesMustNotReturnNull(){
		
		List<OpenclinicaStudy> studies = openClinicaService.getOpenClinicaStudies();
		
		assertEquals(2, studies.size());
		assertEquals("study2", ocStudies.get(0).getName());
		assertEquals("study", ocStudies.get(1).getName());
	}
}
