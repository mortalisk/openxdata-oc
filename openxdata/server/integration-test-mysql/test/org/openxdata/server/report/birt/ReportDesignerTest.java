package org.openxdata.server.report.birt;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 
 * @author daniel
 *
 */
public class ReportDesignerTest {

    //TODO jonny this test should not depend on MySQL!
    private final static String PROPERTY_FILE = "integration.properties";
    private final static String PROPERTY_MYSQL_URL = "mysql.integration.url";
    private static String connectionUrl;

    @BeforeClass
    public static void setDbUrl() throws IOException {
        InputStream input = ClassLoader.getSystemResourceAsStream(PROPERTY_FILE);
        Properties properties = new Properties();
        properties.load(input);

        String url = properties.getProperty(PROPERTY_MYSQL_URL);
        if (url.startsWith("${")) {
            fail(PROPERTY_MYSQL_URL + " is not set");
        }
        connectionUrl = url;
    }
    /*public void testGetReportDesign(){

    ReportDesigner reportDesigner = new ReportDesigner();

    List<ReportColumn> cols = new ArrayList<ReportColumn>();
    cols.add(new ReportColumn("name","name","string"));
    cols.add(new ReportColumn("description","description","string"));
    cols.add(new ReportColumn("value","value","string"));

    reportDesigner.setDbUrl("jdbc:mysql://localhost:3306/openxdata?autoReconnect=true");
    String xml = reportDesigner.getReportDesign(1,cols,"select * from setting");
    assert(xml != null);

    System.out.println(xml);
    }*/

    @Test
    public void testGetLineChartReportDesign() {

        ReportDesigner reportDesigner = new ReportDesigner();

        List<ReportColumn> cols = new ArrayList<ReportColumn>();
        cols.add(new ReportColumn("name", "name", "string"));
        cols.add(new ReportColumn("description", "description", "string"));
        cols.add(new ReportColumn("value", "value", "string"));

        reportDesigner.setDbUrl(connectionUrl);
        String xml = reportDesigner.createReportDesign("2", null, "", "", cols, "select * from setting", "", "");
        assertNotNull(xml);

        System.out.println(xml);
    }
    /*public void testGetBarChartReportDesign(){

    ReportDesigner reportDesigner = new ReportDesigner();

    List<ReportColumn> cols = new ArrayList<ReportColumn>();
    cols.add(new ReportColumn("name","name","string"));
    cols.add(new ReportColumn("description","description","string"));
    cols.add(new ReportColumn("value","value","string"));

    reportDesigner.setDbUrl("jdbc:mysql://localhost:3306/openxdata?autoReconnect=true");
    String xml = reportDesigner.getReportDesign(3,cols,"select * from setting");
    assert(xml != null);

    System.out.println(xml);
    }

    public void testGetPieChartReportDesign(){

    ReportDesigner reportDesigner = new ReportDesigner();

    List<ReportColumn> cols = new ArrayList<ReportColumn>();
    cols.add(new ReportColumn("name","name","string"));
    cols.add(new ReportColumn("description","description","string"));
    cols.add(new ReportColumn("value","value","string"));

    reportDesigner.setDbUrl("jdbc:mysql://localhost:3306/openxdata?autoReconnect=true");
    String xml = reportDesigner.getReportDesign(4,cols,"select * from setting");
    assert(xml != null);

    System.out.println(xml);
    }*/
}
