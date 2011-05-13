package org.openxdata.server.admin.model;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests slightly complicated methods of the FormDefVersion class.
 */
public class FormDefVersionTest {

	
	@Test
	public void isDirty_shouldReturnTrueForFormVersionWithDirtyFormVersionText() {

		FormDefVersion formDefVersion = new FormDefVersion();
		FormDefVersionText formDefVersionText = new FormDefVersionText();
		formDefVersion.addVersionText(formDefVersionText);
		
		Assert.assertFalse(formDefVersion.isDirty());
		formDefVersionText.setDirty(true);
		
		Assert.assertTrue(formDefVersion.isDirty());
	}
	
	
	@Test
	public void isNew_shouldReturnTrueForFormWithNewFormVersionText() {

		FormDefVersion formDefVersion = new FormDefVersion();
		formDefVersion.setFormDefVersionId(1);
		Assert.assertFalse(formDefVersion.isNew());
		
		formDefVersion.addVersionText(new FormDefVersionText());
		
		Assert.assertTrue(formDefVersion.isNew());
	}
}
