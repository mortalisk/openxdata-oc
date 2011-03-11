package org.openxdata.server.admin.model;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests slightly complicated methods of the StudyDef class.
 */
public class StudyDefTest {

	@Test
	public void isDirty_shouldReturnTrueForStudyWithDirtyForm() {

		StudyDef studyDef = new StudyDef();
		FormDef formDef = new FormDef();
		studyDef.addForm(formDef);
		
		Assert.assertFalse(studyDef.isDirty());
		formDef.setDirty(true);
		
		Assert.assertTrue(studyDef.isDirty());
	}
	
	
	@Test
	public void isDirty_shouldReturnTrueForStudyWithDirtyFormVersion() {

		StudyDef studyDef = new StudyDef();
		FormDef formDef = new FormDef();
		studyDef.addForm(formDef);
		FormDefVersion formDefVersion = new FormDefVersion();
		formDef.addVersion(formDefVersion);
		
		Assert.assertFalse(studyDef.isDirty());
		formDefVersion.setDirty(true);
		
		Assert.assertTrue(studyDef.isDirty());
	}
	
	
	@Test
	public void isDirty_shouldReturnTrueForStudyWithDirtyFormVersionText() {

		StudyDef studyDef = new StudyDef();
		FormDef formDef = new FormDef();
		studyDef.addForm(formDef);
		FormDefVersion formDefVersion = new FormDefVersion();
		formDef.addVersion(formDefVersion);
		FormDefVersionText formDefVersionText = new FormDefVersionText();
		formDefVersion.addVersionText(formDefVersionText);
		
		Assert.assertFalse(studyDef.isDirty());
		formDefVersionText.setDirty(true);
		
		Assert.assertTrue(studyDef.isDirty());
	}
	
	
	@Test
	public void isNew_shouldReturnTrueForStudyWithNewForm() {

		StudyDef studyDef = new StudyDef();
		studyDef.setStudyId(1);
		Assert.assertFalse(studyDef.isNew());

		studyDef.addForm(new FormDef());
		
		Assert.assertTrue(studyDef.isNew());
	}
	
	
	@Test
	public void isNew_shouldReturnTrueForStudyWithNewFormVersion() {

		StudyDef studyDef = new StudyDef();
		studyDef.setStudyId(1);
		FormDef formDef = new FormDef();
		formDef.setFormId(1);
		studyDef.addForm(formDef);
		Assert.assertFalse(studyDef.isNew());
		
		formDef.addVersion(new FormDefVersion());
		
		Assert.assertTrue(studyDef.isNew());
	}
	
	
	@Test
	public void isNew_shouldReturnTrueForStudyWithNewFormVersionText() {

		StudyDef studyDef = new StudyDef();
		studyDef.setStudyId(1);
		FormDef formDef = new FormDef();
		formDef.setFormId(1);
		studyDef.addForm(formDef);
		FormDefVersion formDefVersion = new FormDefVersion();
		formDefVersion.setFormDefVersionId(1);
		formDef.addVersion(formDefVersion);
		Assert.assertFalse(studyDef.isNew());
		
		formDefVersion.addVersionText(new FormDefVersionText());
		
		Assert.assertTrue(studyDef.isNew());
	}
}
