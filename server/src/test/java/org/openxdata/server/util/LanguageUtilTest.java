package org.openxdata.server.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link LanguageUtilTest} class.
 * 
 * @author brent
 * 
 */
public class LanguageUtilTest {

	private String formDef;
	private String oldLang;
	private String newLang;

	@Before
	public void setUp() throws IOException {
		InputStream formStream = LanguageUtilTest.class
				.getResourceAsStream("minimalform.xml");
		InputStream oldLangStream = LanguageUtilTest.class
				.getResourceAsStream("oldlangdef.xml");
		InputStream newLangStream = LanguageUtilTest.class
				.getResourceAsStream("newlangdef.xml");
		formDef = IOUtils.toString(formStream);
		oldLang = IOUtils.toString(oldLangStream);
		newLang = IOUtils.toString(newLangStream);
	}

	/**
	 * Verifies that we can translate the xforms using both the old and new
	 * translation text format.
	 */
	@Test
	public void testOXD484() {

		String oldTranslation = LanguageUtil.translate(formDef, oldLang);
		String newTranslation = LanguageUtil.translate(formDef, newLang);

		assertEquals("translations should be equivalent", oldTranslation,
				newTranslation);

		// Lower so we can compare easily
		String loweredDef = formDef.toLowerCase();
		String[] loweredXlations = { oldTranslation.toLowerCase(),
				newTranslation.toLowerCase() };

		String[] tokens = { "untranslated form", "untranslated page",
				"untranslated question" };

		for (String token : tokens)
			assertTrue("formdef should contain token '" + token + "'",
					loweredDef.contains(token));

		for (String xlation : loweredXlations) {
			for (String token : tokens)
				assertFalse("translation should not contain token '" + token
						+ "'", xlation.contains(token));
		}
	}
}
