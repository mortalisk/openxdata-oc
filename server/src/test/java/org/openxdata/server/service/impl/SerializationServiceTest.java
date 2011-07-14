package org.openxdata.server.service.impl;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.openxdata.server.dao.SettingDAO;
import org.openxdata.server.serializer.DefaultXformSerializer;
import org.openxdata.server.serializer.XformSerializer;
import org.openxdata.test.BaseContextSensitiveTest;

public class SerializationServiceTest extends BaseContextSensitiveTest {

	private SettingDAO settingDAOMock;	
	private SerializationServiceImpl serializationService;
	
	@Before
	public void createMocks() {
		settingDAOMock = mock(SettingDAO.class);
		serializationService = new SerializationServiceImpl(settingDAOMock);
	}
	
	@Test
	public void testGetFormSerializerShouldReturnDefaultSerializer() {
		XformSerializer ser = serializationService.getFormSerializer("XXX");
		
		assertSame(ser.getClass(), DefaultXformSerializer.class);
	}
}
