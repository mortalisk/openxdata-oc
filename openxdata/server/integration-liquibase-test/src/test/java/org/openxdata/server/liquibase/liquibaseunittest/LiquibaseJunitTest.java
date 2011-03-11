package org.openxdata.server.liquibase.liquibaseunittest;

import org.openxdata.server.liquibase.liquibasetest.TestLiquibase;
import org.openxdata.server.liquibase.liquibasetest.Main;
import java.io.IOException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for Main.
 */
public class LiquibaseJunitTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    private TestLiquibase testmigration;

    public LiquibaseJunitTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws IOException {
        testmigration = new TestLiquibase();
    }
    public void testRunMasterChangeLog() {
        boolean expected = true;
        assertEquals(expected, testmigration.runChangeLog(Main.masterLog));
    }
    public void testDeleteDatabase(){
        boolean expected = true;
        assertEquals(expected, testmigration.deleteDatabase());
    }
    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(LiquibaseJunitTest.class);
    }
}
