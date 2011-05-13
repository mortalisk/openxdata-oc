package org.openxdata.server.database;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import javax.sql.DataSource;

import liquibase.FileOpener;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.exception.LiquibaseException;

import org.apache.log4j.Logger;
import org.openxdata.server.admin.model.exception.UnexpectedException;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class SpringLiquibaseUpdater implements ResourceLoaderAware {

    private String contexts;
    private String changeLog;
    private DataSource dataSource;
    private ResourceLoader resourceLoader;
    private Logger log = Logger.getLogger(this.getClass());

    public void init() throws Exception {
        SpringFileOpener fileOpener = new SpringFileOpener(changeLog, getResourceLoader());
        try {
            fileOpener.getResourceAsStream(changeLog);
        } catch (Exception ignoreException) {
            log.warn("LiquibaseChangelog does not exist: " + changeLog);
            return;
        }
        Liquibase liquibase = new Liquibase(changeLog, fileOpener, getDatabase());

        try {
            liquibase.update(contexts);
        } catch (LiquibaseException e) {
            throw new UnexpectedException(
                    "Could not update database through Liquibase", e);
        }
    }
    
    private Database getDatabase() {
        try {
            Database databaseImplementation = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
                    dataSource.getConnection());
            return databaseImplementation;
        } catch (Exception e) {
            throw new UnexpectedException("Error getting database", e);
        }

    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(String changeLog) {
        this.changeLog = changeLog;
    }

    public void setContexts(String contexts) {
        this.contexts = contexts;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    class SpringFileOpener implements FileOpener {

        private String parentFile;
        private ResourceLoader resourceLoader;

        public SpringFileOpener(String parentFile, ResourceLoader resourceLoader) {
            this.parentFile = parentFile;
            this.resourceLoader = resourceLoader;
        }

        @Override
        public InputStream getResourceAsStream(String file) throws IOException {
            Resource resource = getResource(file);

            return resource.getInputStream();
        }

        @Override
        public Enumeration<URL> getResources(String packageName)
                throws IOException {
            Vector<URL> tmp = new Vector<URL>();
            tmp.add(getResource(packageName).getURL());
            return tmp.elements();
        }

        public Resource getResource(String file) {
            return getResourceLoader().getResource(adjustClasspath(file));
        }

        private String adjustClasspath(String file) {
            return isClasspathPrefixPresent(parentFile)
                    && !isClasspathPrefixPresent(file) ? ResourceLoader.CLASSPATH_URL_PREFIX
                    + file
                    : file;
        }

        public boolean isClasspathPrefixPresent(String file) {
            return file.startsWith(ResourceLoader.CLASSPATH_URL_PREFIX);
        }

        @Override
        public ClassLoader toClassLoader() {
            return getResourceLoader().getClassLoader();
        }

        public ResourceLoader getResourceLoader() {
            return resourceLoader;
        }

        public void setResourceLoader(ResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
        }
    }
}
