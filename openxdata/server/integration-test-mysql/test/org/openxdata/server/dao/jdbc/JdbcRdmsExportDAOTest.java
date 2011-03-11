package org.openxdata.server.dao.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openxdata.server.export.rdbms.engine.Constants;
import org.openxdata.server.export.rdbms.engine.DataQuery;

public class JdbcRdmsExportDAOTest {

    private static String CONNECTION_URL;
    private static String DATABASE;
    private final static String TABLE_NAME = "bob";
    private final static Integer FORM_DATA_ID = 1;
    private final static String DATA_ID = "86106b50-e416-11de-b3c6-00216b5d6d6a";
    private final static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ( Id VARCHAR(200) PRIMARY KEY, openxdata_form_data_id VARCHAR(50), openxdata_user_id VARCHAR(50), openxdata_user_name VARCHAR(50), kidname VARCHAR(255),kidsex VARCHAR(255),kidage INTEGER);";
    private final static String INSERT_DATA = "INSERT INTO " + TABLE_NAME + " (openxdata_user_id,openxdata_user_name,openxdata_form_data_id,kidage,Id,kidsex,kidname) VALUES (23,'dagmar'," + FORM_DATA_ID + ",1,'" + DATA_ID + "','female','clara');";
    private final static String INSERT_DATA_PS = "INSERT INTO " + TABLE_NAME + " (openxdata_user_id,openxdata_user_name,openxdata_form_data_id,kidage,Id,kidsex,kidname) VALUES (?,?,?,?,?,?,?);";
    private final static String DELETE_TABLE = "DROP TABLE " + TABLE_NAME + "";
    private final static String PROPERTY_FILE = "integration.properties";
    private final static String PROPERTY_MYSQL_URL = "mysql.integration.url";
    private final static String PROPERTY_MYSQL_DATABASE = "mysql.integration.database";
    private JdbcRdmsExporterDAO dao;

    @BeforeClass
    public static void loadProperties() throws IOException {
        InputStream input = ClassLoader.getSystemResourceAsStream(PROPERTY_FILE);
        Properties properties = new Properties();
        properties.load(input);

        String url = properties.getProperty(PROPERTY_MYSQL_URL);
        if (url.startsWith("${")) {
            fail(PROPERTY_MYSQL_URL + " is not set");
        }
        CONNECTION_URL = url;

        String database = properties.getProperty(PROPERTY_MYSQL_DATABASE);
        if (database.startsWith("${")) {
            fail(PROPERTY_MYSQL_URL + " is not set");
        }
        DATABASE = database;
    }

    @Before
    public void setup() throws Exception {
        dao = new JdbcRdmsExporterDAO(Constants.DB_MYSQL, CONNECTION_URL);
        dao.executeSql(CREATE_TABLE);
    }

    @After
    public void tearDown() throws Exception {
        dao.executeSql(DELETE_TABLE);
    }

    private static DataQuery insertDataQuery(String userName, int formDataId, String dataId, String kidSex, String kidName) {
        final int userId = 23;
        final int kidAge = 1;

        Object[] parameters = new Object[]{
            userId,
            userName,
            formDataId,
            kidAge,
            dataId,
            kidSex,
            kidName};

        return new DataQuery(INSERT_DATA_PS, parameters);
    }

    @Test
    public void testIntegrationNoDataExists() throws Exception {
        assertFalse("no data yet in table", dao.dataExists(FORM_DATA_ID, TABLE_NAME));
    }

    @Test
    public void testIntegrationDataExists() throws Exception {
        dao.executeSql(INSERT_DATA);
        assertTrue("data should have been inserted in the tables", dao.dataExists(FORM_DATA_ID, TABLE_NAME));
    }

    @Test
    public void testIntegrationTableExists() throws Exception {
        assertTrue("table " + TABLE_NAME + " already exists", dao.tableExists(DATABASE, TABLE_NAME));
    }

    @Test
    public void testIntegrationTableDoesNotExist() throws Exception {
        assertFalse("table bobby does not exist", dao.tableExists(DATABASE, "bobby"));
    }

    @Test
    public void testIntegrationExecuteListSQL() throws Exception {
        String userName = "dagmar'; DROP TABLE bob;";
        String kidSex = "fem;ale";
        String kidName = "clara";
        DataQuery dq = insertDataQuery(userName, FORM_DATA_ID, DATA_ID, kidSex, kidName);
        List<DataQuery> sqls = new ArrayList<DataQuery>();
        sqls.add(dq);
        dao.executeSql(sqls);
        assertTrue("Bob data was inserted ok (and bob table wasn't deleted by sql injection",
                dao.dataExists(FORM_DATA_ID, TABLE_NAME));
    }

    @Test
    //@Ignore("Rollback does not work on Mysql unless you configure INNODB")
    // See: http://dev.mysql.com/doc/refman/5.0/en/innodb-configuration.html
    public void testIntegrationExecuteListSQLRollback() throws Exception {
        final String userName = "dagmar";
        final String kidSex = "female";
        final String kidName = "clara";

        List<DataQuery> sqls = new ArrayList<DataQuery>();
        sqls.add(insertDataQuery(userName, FORM_DATA_ID, DATA_ID, kidSex, kidName));
        sqls.add(insertDataQuery(userName, FORM_DATA_ID + 1, DATA_ID + 1, kidSex, kidName));
        sqls.add(new DataQuery("invalid sql statement", new Object[]{}));
        try {
            dao.executeSql(sqls);
            fail("expected to have an exception because last sql statement will fail");
        } catch (Exception e) {
            //TODO should use the correct exception class. Not the generic Exception..
            //should always happen, if not; there will be assertions that will report!!
        }

        assertTrue("All data should have been rolled back when last statement failed",
                dao.dataExists(FORM_DATA_ID, TABLE_NAME));
    }
}
