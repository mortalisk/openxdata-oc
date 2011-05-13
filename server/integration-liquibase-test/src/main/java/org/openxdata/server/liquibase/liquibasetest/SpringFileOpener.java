/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openxdata.server.liquibase.liquibasetest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import liquibase.FileOpener;

/**
 *
 * @author victor
 */
public class SpringFileOpener implements FileOpener {
    
    public SpringFileOpener(){}

    public InputStream getResourceAsStream(String resource) throws IOException {
        return toClassLoader().getResourceAsStream(resource);
    }

    public Enumeration<URL> getResources(String resources) throws IOException {
        return toClassLoader().getResources(resources);
    }

    public ClassLoader toClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
