package org.openxdata.server.liquibase.liquibasetest;

/**
 * Tests Liquibase changesets against a target database
 * if there is no database, testdb is created as
 * defined in openxdata\server\integration-liquibase-test\src\main\resources/MYSQL_SETTIGS.properties file.
 *
 */
public class Main {

    private String dbName;

    public void setDbName(String name) {
        this.dbName = name;
    }

    public String getDbName() {
        return dbName;
    }
    public static String masterLog =  "database/mysql/liquibase-master.xml";
    public static void main(String[] args) throws Exception {
        TestLiquibase testMigration = new TestLiquibase();
        testMigration.showStatus(masterLog);
        testMigration.deleteDatabase();

    }
}
