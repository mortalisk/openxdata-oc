package org.openxdata.server.admin.model;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests slightly complicated methods of the FormDef class.
 */
public class FormDefTest {

	@Test
	public void isDirty_shouldReturnTrueForFormWithDirtyFormVersion() {

		FormDef formDef = new FormDef();
		FormDefVersion formDefVersion = new FormDefVersion();
		formDef.addVersion(formDefVersion);
		
		Assert.assertFalse(formDef.isDirty());
		formDefVersion.setDirty(true);
		
		Assert.assertTrue(formDef.isDirty());
	}
	
	
	@Test
	public void isDirty_shouldReturnTrueForFormWithDirtyFormVersionText() {

		FormDef formDef = new FormDef();
		FormDefVersion formDefVersion = new FormDefVersion();
		formDef.addVersion(formDefVersion);
		FormDefVersionText formDefVersionText = new FormDefVersionText();
		formDefVersion.addVersionText(formDefVersionText);
		
		Assert.assertFalse(formDef.isDirty());
		formDefVersionText.setDirty(true);
		
		Assert.assertTrue(formDef.isDirty());
	}
	
	
	@Test
	public void isNew_shouldReturnTrueForFormWithNewFormVersion() {

		FormDef formDef = new FormDef();
		formDef.setFormId(1);
		Assert.assertFalse(formDef.isNew());
		
		formDef.addVersion(new FormDefVersion());
		
		Assert.assertTrue(formDef.isNew());
	}
	
	
	@Test
	public void isNew_shouldReturnTrueForFormWithNewFormVersionText() {

		FormDef formDef = new FormDef();
		formDef.setFormId(1);
		FormDefVersion formDefVersion = new FormDefVersion();
		formDefVersion.setFormDefVersionId(1);
		formDef.addVersion(formDefVersion);
		Assert.assertFalse(formDef.isNew());
		
		formDefVersion.addVersionText(new FormDefVersionText());
		
		Assert.assertTrue(formDef.isNew());
	}
}
