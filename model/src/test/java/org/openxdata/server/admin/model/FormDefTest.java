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
		formDef.setId(1);
		Assert.assertFalse(formDef.isNew());

		formDef.addVersion(new FormDefVersion());

		Assert.assertTrue(formDef.isNew());
	}

	@Test
	public void isNew_shouldReturnTrueForFormWithNewFormVersionText() {

		FormDef formDef = new FormDef();
		formDef.setId(1);
		FormDefVersion formDefVersion = new FormDefVersion();
		formDefVersion.setId(1);
		formDef.addVersion(formDefVersion);
		Assert.assertFalse(formDef.isNew());

		formDefVersion.addVersionText(new FormDefVersionText());

		Assert.assertTrue(formDef.isNew());
	}
	
	@Test
	public void testGetNextVersionName2nd() {
		FormDef formDef = new FormDef();
		FormDefVersion version1 = new FormDefVersion(1, "v1", formDef);
		formDef.addVersion(version1);
		Assert.assertEquals("version incremented", formDef.getNextVersionName(), "v2");
	}
	
	@Test
	public void testGetNextVersionName4th() {
		FormDef formDef = new FormDef();
		FormDefVersion version1 = new FormDefVersion(1, "v1", formDef);
		FormDefVersion version3 = new FormDefVersion(3, "v3", formDef);
		formDef.addVersion(version1);
		formDef.addVersion(version3);
		Assert.assertEquals("version incremented", formDef.getNextVersionName(), "v4");
	}
}
