package org.openxdata.proto;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.openxdata.proto.exception.ProtocolException;
import org.openxdata.proto.exception.ProtocolInstantiationException;
import org.openxdata.proto.exception.ProtocolNotFoundException;

/**
 * Demonstrates how to load multiple versions of a given protocol handler class
 * and use them.
 * 
 * @author batkinson
 * 
 */
public class ProtocolLoader {

	private static final String DEFAULT_HANDLER_CLASS = "org.openxdata.proto.ProtocolHandlerImpl";
	private static final String PROTO_PROPERTIES_PATH = "META-INF/protocol.properties";
	private static final String PROTO_CLASS_PROP_NAME = "handler.class";

	public ProtocolHandler loadHandler(URL protocolJarUrl)
			throws ProtocolException {

		ClassLoader cl = new ProtocolClassLoader(new URL[] { protocolJarUrl },
				Thread.currentThread().getContextClassLoader());
		String handlerClassName = DEFAULT_HANDLER_CLASS;

		// Load protocol class from protocol jar's properties
		try {
			Properties protoProps = new Properties();
			InputStream protoPropStream = cl
					.getResourceAsStream(PROTO_PROPERTIES_PATH);
			if (protoPropStream != null) {
				protoProps.load(protoPropStream);
				handlerClassName = protoProps
						.getProperty(PROTO_CLASS_PROP_NAME);
			}
		} catch (IOException e) {
			throw new ProtocolNotFoundException(
					"failed to load protocol properties", e);
		}

		// Load and return the handler
		try {
			Class<?> pc = Class.forName(handlerClassName, false, cl);
			return (ProtocolHandler) pc.newInstance();
		} catch (ClassNotFoundException e) {
			throw new ProtocolNotFoundException(
					"failed to load protocol class", e);
		} catch (InstantiationException e) {
			throw new ProtocolInstantiationException(
					"failed to instantiate protocol handler", e);
		} catch (IllegalAccessException e) {
			throw new ProtocolInstantiationException(
					"failed to instantiate protocol handler", e);
		}
	}
}
