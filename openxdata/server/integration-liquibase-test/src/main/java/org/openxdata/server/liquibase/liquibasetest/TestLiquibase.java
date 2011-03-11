package org.openxdata.server.liquibase.liquibasetest;

import java.io.IOException;
import java.sql.*;
import liquibase.Liquibase;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Tests liquibase files against target
 * database
 * @author victor
 */
public class TestLiquibase {

    public DriverManagerDataSource dsource;
    public SpringLiquibaseUpdater liquibase;
    public Main app;
    private Logger log = Logger.getLogger(this.getClass());

    public TestLiquibase() throws IOException {
        DOMConfigurator.configure("src/main/resources/log4j.xml");
        ApplicationContext context = new FileSystemXmlApplicationContext("src/main/resources/applicationContext.xml");
        app = (Main) context.getBean("database");
        dsource = (DriverManagerDataSource) context.getBean("dataSource");
        liquibase = (SpringLiquibaseUpdater) context.getBean("liquibase");
        log.info("Testing database url ==== " + dsource.getUrl() + " database name " + app.getDbName());
        //creates database if does not exist else ignores and continues
        createDatabase();
    }

    private void createDatabase() {
        Connection conn = null;
        try {
            conn = dsource.getConnection();
            Statement stmt = conn.createStatement();
            log.info("[Debug] executing statement " + "CREATE DATABASE " + app.getDbName());
            stmt.executeUpdate("CREATE DATABASE " + app.getDbName());
            log.info("OK. 1 row(s) affacted");
            dsource.setUrl(dsource.getUrl() + app.getDbName() + "?autoReconnect=true");
            log.info("[debug] new connection Url: " + dsource.getUrl());
        } catch (SQLException ex) {
            log.info("[Error] " + ex.getMessage());
            log.info("Ignoring error..... continue with other operations");
            dsource.setUrl(dsource.getUrl() + app.getDbName() + "?autoReconnect=true");
            log.info("[debug] new connection Url: " + dsource.getUrl());
        }
        log.info("\n\n\n\nUsing Database ..........."+getDbName()+"\n\n\n\n");
    }

    public boolean  deleteDatabase() {
        try {
            Connection conn = dsource.getConnection();
            Statement stmt = conn.createStatement();
            System.out.println("[Debug] executing statement " + "DROP DATABASE " + app.getDbName());
            stmt.executeUpdate("DROP DATABASE " + app.getDbName());
            System.out.println("OK. Database "+app.getDbName()+" deleted\n\n\n");
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

//    run a change log
    public boolean runChangeLog(String changelog) {
        log.info("Testing change log " + changelog);
        try {
            liquibase.setChangeLog(changelog);
            Liquibase liq = new Liquibase(liquibase.getChangeLog(),
                    new SpringFileOpener(), liquibase.getDatabase());
            liq.update(liquibase.getContexts());
            log.info("[debug ========================= ] finished writting to target database");
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

    }

    public void showStatus(String changeLog) {
        if (runChangeLog(changeLog)) {
            log.info("Test Passed");
        } else {
            log.info("Test failed");
        }
    }

    public String getDbName() {
        String name = dsource.getUrl();
        int posn = name.lastIndexOf("/");
        int endposn = name.indexOf("?");
        return name.substring(posn + 1, endposn);
    }
}
