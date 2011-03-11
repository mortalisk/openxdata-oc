package org.openxdata.server.module.openclinica;

import org.junit.Test;

/**
 * 
 * @author daniel
 *
 */
public class OpenClinicaServerTest {

	@Test
	public void testWs(){
		OpenClinicaServer server = new OpenClinicaServer();
		server.processConnection(null, null);
	}
}
