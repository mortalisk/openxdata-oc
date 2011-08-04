package org.openxdata.test;

import java.net.InetSocketAddress;

import org.apache.http.HttpHost;
import org.apache.http.localserver.LocalTestServer;
import org.apache.http.localserver.RequestBasicAuth;
import org.apache.http.localserver.ResponseBasicUnauthorized;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.junit.After;
import org.junit.Before;

public abstract class BaseHttpTest {

	private LocalTestServer localServer;

	public BaseHttpTest() {
		super();
	}

	@Before
	public void setup() throws Exception {
		BasicHttpProcessor httpproc = new BasicHttpProcessor();
		httpproc.addInterceptor(new ResponseDate());
		httpproc.addInterceptor(new ResponseServer());
		httpproc.addInterceptor(new ResponseContent());
		httpproc.addInterceptor(new ResponseConnControl());
		httpproc.addInterceptor(new RequestBasicAuth());
		httpproc.addInterceptor(new ResponseBasicUnauthorized());
	
		localServer = new LocalTestServer(httpproc, null);
		localServer.start();
		registerHandlers(localServer);
	}

	/**
	 * Override this method to add handlers to the localServer before each test
	 * @param localServer
	 */
	protected void registerHandlers(LocalTestServer localServer) {
		// do nothing
	}

	protected String getServerUrl() {
		return "http://"+ getServerHost().toHostString();
	}
	
	public LocalTestServer getLocalServer() {
		return localServer;
	}

	@After
	public void tearDown() throws Exception {
		if (localServer != null) {
			localServer.stop();
		}
	}

	/**
	 * Obtains the address of the local test server.
	 * 
	 * @return the test server host, with a scheme name of "http"
	 */
	protected HttpHost getServerHost() {
		InetSocketAddress address = (InetSocketAddress) localServer
				.getServiceAddress();
		return new HttpHost(address.getHostName(), address.getPort(), "http");
	}

}