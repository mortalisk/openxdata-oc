package org.openxdata.proto;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * A class loader that intentionally breaks the Java2 delegation model so that
 * the protocol classes are given priority over the same classes loaded by a
 * parent class loader.
 * 
 * @author batkinson
 * 
 */
public class ProtocolClassLoader extends URLClassLoader {

	public ProtocolClassLoader(URL[] urls, ClassLoader parent,
			URLStreamHandlerFactory factory) {
		super(urls, parent, factory);
	}

	public ProtocolClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	public ProtocolClassLoader(URL[] urls) {
		super(urls);
	}

	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {

		// First, check if the class has already been loaded
		Class<?> c = findLoadedClass(name);

		// If not already loaded, first try protocol jar
		if (c == null) {
			try {
				c = findClass(name);
			} catch (ClassNotFoundException cnfe) {
				c = super.loadClass(name, false);
			}
		}
		if (resolve) {
			resolveClass(c);
		}
		return c;
	}
}
