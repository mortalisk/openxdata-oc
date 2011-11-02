package org.openxdata.server.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
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

    Set<OpenclinicaStudy> studies = new HashSet<OpenclinicaStudy>();
	
    @Before public void setUp() {
    	
    	OpenclinicaStudy study = new OpenclinicaStudy();
		study.setIdentifier("id");
		study.setOID("oid");
		study.setName("study");
		
		OpenclinicaStudy study2 = new OpenclinicaStudy();
		study2.setIdentifier("id2");
		study2.setOID("oid2");
		study2.setName("study2");
		
		studies.add(study);
		studies.add(study);
		
    	Mockito.when(studyService.getStudyKey(Mockito.anyInt())).thenReturn("key");
    	Mockito.when(studyDAO.getStudy(Mockito.anyString())).thenReturn(new StudyDef());
    	Mockito.when(editableDAO.hasEditableData(Mockito.any(Editable.class))).thenReturn(true);
    	
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
		
	}
}
