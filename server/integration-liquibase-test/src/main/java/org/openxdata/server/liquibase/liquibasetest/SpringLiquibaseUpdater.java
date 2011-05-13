package org.openxdata.server.liquibase.liquibasetest;

import javax.sql.DataSource;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;

public class SpringLiquibaseUpdater implements ResourceLoaderAware, InitializingBean {

    SpringFileOpener fileopener = null;

    public void afterPropertiesSet() throws Exception {
//        Liquibase liquibase = new Liquibase(changeLog, new SpringFileOpener(changeLog, getResourceLoader()), getDatabase());
    }
    private DataSource dataSource;
    private String changeLog;
    private String contexts;
    private ResourceLoader resourceLoader;

    public Database getDatabase() {
        try {
            Database databaseImplementation = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(dataSource.getConnection());
            return databaseImplementation;
        } catch (Exception e) {
            throw new RuntimeException("Error getting database", e);
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

    public String getContexts() {
        return contexts;
    }

    public void setContexts(String contexts) {
        this.contexts = contexts;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {

        this.resourceLoader = resourceLoader;
    }
}
