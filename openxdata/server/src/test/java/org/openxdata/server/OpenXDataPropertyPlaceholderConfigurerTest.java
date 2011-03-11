package org.openxdata.server;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockServletContext;

public class OpenXDataPropertyPlaceholderConfigurerTest {

	private static final String TEST_CONTEXT_PATH = "/TEST_CONTEXT_PATH";
	private OpenXDataPropertyPlaceholderConfigurer configurer = new OpenXDataPropertyPlaceholderConfigurer();

	@Test
	public void testGetSettingsFile_nullEnvVariable() {
		File settingsFile = configurer.getSettingsFile("NON_EXISTENT_ENVIRONMENT_VARIABLE_PREFIX");
		assertNull(settingsFile);
	}

	@Test
	public void testGetSettingsFile_fromFile() throws IOException {
		File tempFile = File.createTempFile("OXD_TEST", "");
		setSettingVariable(tempFile.getAbsolutePath());

		File settingsFile = configurer.getSettingsFile(TEST_CONTEXT_PATH);

		clearProperty();
		assertEquals(tempFile, settingsFile);
	}

	@Test
	public void testGetEnvironmentVariableFromContextPath() {
		String env = configurer.getEnvironmentVariableFromContextPath("/OpenXdata-1.0");
		assertEquals("OPENXDATA_1_0_SETTINGS", env);
	}

	@Test
	public void testLoadContextBasedProperties_nullServletContext() throws URISyntaxException,
			IOException {
		Properties props = new Properties();
		configurer.loadContextBasedProperties(props);
		assertTrue(props.isEmpty());
	}

	@Test
	public void testLoadContextBasedProperties_nonEmptyProps() throws URISyntaxException,
			IOException {
		OpenXDataPropertyPlaceholderConfigurer isolatedConfigurer = new OpenXDataPropertyPlaceholderConfigurer();

		Properties props = new Properties();
		String key = "key";
		String value = "vlaue";
		props.put(key, value);
		isolatedConfigurer.setServletContext(new MockServletContext());
		isolatedConfigurer.loadContextBasedProperties(props);
		assertTrue(props.size() == 1);
		assertEquals(value, props.get(key));
	}

	@Test()
	public void testLoadFromEnvironmentVariable_noEnvVariable() throws URISyntaxException,
			IOException {
		OpenXDataPropertyPlaceholderConfigurer isolatedConfigurer = new OpenXDataPropertyPlaceholderConfigurer();

		Properties props = new Properties();
		MockServletContext servletContext = getServletContext();
		isolatedConfigurer.setServletContext(servletContext);
		isolatedConfigurer.loadFromEnvironmentVariable(props);
		assertTrue(props.isEmpty());
	}

	@Test
	public void testLoadFromEnvironmentVariable_withEnvVariableSet() throws URISyntaxException,
			IOException {
		OpenXDataPropertyPlaceholderConfigurer isolatedConfigurer = new OpenXDataPropertyPlaceholderConfigurer();

		Properties props = new Properties();

		File testPropertiesFile = getTestPropertiesFile();
		setSettingVariable(testPropertiesFile.getAbsolutePath());

		MockServletContext servletContext = getServletContext();
		isolatedConfigurer.setServletContext(servletContext);
		isolatedConfigurer.loadFromEnvironmentVariable(props);

		clearProperty();

		assertTrue(!props.isEmpty());
	}

	@Test(expected = FileNotFoundException.class)
	public void testLoadFromWebappFolder_nonExistantClasspath() throws IOException {
		OpenXDataPropertyPlaceholderConfigurer isolatedConfigurer = new OpenXDataPropertyPlaceholderConfigurer();
		isolatedConfigurer.setServletContext(getServletContext());
		Properties props = new Properties();
		isolatedConfigurer.loadFromWebappFolder(props, "non-existant-path");
		assertTrue(props.isEmpty());
	}

	@Test
	public void testLoadFromWebappFolder_existingClasspath() throws IOException {
		OpenXDataPropertyPlaceholderConfigurer isolatedConfigurer = new OpenXDataPropertyPlaceholderConfigurer();
		MockServletContext servletContext = getServletContext();
		isolatedConfigurer.setServletContext(servletContext);
		Properties props = new Properties();
		isolatedConfigurer.loadFromWebappFolder(props, "test.properties");
		assertTrue(props.size() > 0);
	}

	@Test
	public void testLoadProperties_emptyLocationList() throws IOException {
		Properties props = new Properties();
		configurer.setLocations(new Resource[] {});
		configurer.loadProperties(props);
		assertTrue(props.isEmpty());
	}

	@Test
	public void testLoadProperties_nullLocationList() throws IOException {
		Properties props = new Properties();
		configurer.setLocations(null);
		configurer.loadProperties(props);
		assertTrue(props.isEmpty());
	}

	private MockServletContext getServletContext() {
		MockServletContext servletContext = new MockServletContext();
		servletContext.setContextPath(TEST_CONTEXT_PATH);
		return servletContext;
	}

	private void setSettingVariable(String value) {
		System.setProperty(TEST_CONTEXT_PATH.substring(1)
				+ OpenXDataPropertyPlaceholderConfigurer.SETTINGS_SUFFIX, value);
	}

	private void clearProperty() {
		System.clearProperty(TEST_CONTEXT_PATH
				+ OpenXDataPropertyPlaceholderConfigurer.SETTINGS_SUFFIX);
	}

	private File getTestPropertiesFile() throws URISyntaxException {
		URI propsUri = this.getClass().getClassLoader().getResource("test.properties").toURI();
		File settingsFile = new File(propsUri);
		return settingsFile;
	}
}
