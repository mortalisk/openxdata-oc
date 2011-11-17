package org.openxdata.server.admin.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests slightly complicated methods of the StudyDef class.
 */
public class StudyDefTest {

	StudyDef studyDef;
	List<FormDef> forms = new ArrayList<FormDef>();

	@Before public void setUp(){
		studyDef = new StudyDef();
	}
	
	@Test
	public void testIsDirtyShouldReturnTrueForStudyWithDirtyForm() {

		FormDef formDef = new FormDef();
		studyDef.addForm(formDef);

		assertFalse(studyDef.isDirty());
		formDef.setDirty(true);

		assertTrue(studyDef.isDirty());
	}

	@Test
	public void testIsDirtyShouldReturnTrueForStudyWithDirtyFormVersion() {

		FormDef formDef = new FormDef();
		studyDef.addForm(formDef);
		FormDefVersion formDefVersion = new FormDefVersion();
		formDef.addVersion(formDefVersion);

		assertFalse(studyDef.isDirty());
		formDefVersion.setDirty(true);

		assertTrue(studyDef.isDirty());
	}

	@Test
	public void testIsDirtyShouldReturnTrueForStudyWithDirtyFormVersionText() {

		FormDef formDef = new FormDef();
		studyDef.addForm(formDef);
		FormDefVersion formDefVersion = new FormDefVersion();
		formDef.addVersion(formDefVersion);
		FormDefVersionText formDefVersionText = new FormDefVersionText();
		formDefVersion.addVersionText(formDefVersionText);

		assertFalse(studyDef.isDirty());
		formDefVersionText.setDirty(true);

		assertTrue(studyDef.isDirty());
	}

	@Test
	public void testIsNewShouldReturnTrueForStudyWithNewForm() {

		studyDef.setId(1);
		assertFalse(studyDef.isNew());

		studyDef.addForm(new FormDef());

		assertTrue(studyDef.isNew());
	}

	@Test
	public void testIsNewShouldReturnTrueForStudyWithNewFormVersion() {

		studyDef.setId(1);
		FormDef formDef = new FormDef();
		formDef.setId(1);
		studyDef.addForm(formDef);
		assertFalse(studyDef.isNew());

		formDef.addVersion(new FormDefVersion());

		assertTrue(studyDef.isNew());
	}

	@Test
	public void testIsNewShouldReturnTrueForStudyWithNewFormVersionText() {

		studyDef.setId(1);
		FormDef formDef = new FormDef();
		formDef.setId(1);
		studyDef.addForm(formDef);
		FormDefVersion formDefVersion = new FormDefVersion();
		formDefVersion.setId(1);
		formDef.addVersion(formDefVersion);
		assertFalse(studyDef.isNew());

		formDefVersion.addVersionText(new FormDefVersionText());

		assertTrue(studyDef.isNew());
	}
	
	@Test public void testGetFormsReturnsCorrectNumberOfForms(){
		List<FormDef> forms = new ArrayList<FormDef>();
		
		FormDef def = new FormDef();
		FormDef def2 = new FormDef();
		
		forms.add(def);
		forms.add(def2);
		
		studyDef.setForms(forms);
		
		assertEquals(2, studyDef.getForms().size());
	
	}
	
	@Test public void testGetFormsReturnsCorrectFormWithName(){
		List<FormDef> forms = new ArrayList<FormDef>();
		
		FormDef def = new FormDef();
		def.setName("foo");
		
		FormDef def2 = new FormDef();
		def2.setName("foo2");
		
		forms.add(def);
		forms.add(def2);
		
		studyDef.setForms(forms);
	
		FormDef form1 = studyDef.getForm("foo");
		assertEquals(def.getName(), form1.getName());
		
		FormDef form2 = studyDef.getForm("foo2");
		assertEquals(def2.getName(), form2.getName());
	}
}
