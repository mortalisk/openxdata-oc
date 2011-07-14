package org.openxdata.server.export.rdbms.engine;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openxdata.test.XFormsFixture;

public class StructureBuilderTest {

    @Test
    public void testSampleFormStructure() throws Exception {
        String escapeChar = Constants.ESCAPE_CHAR;
        List<TableQuery> structure = RdmsEngine.getStructureSql(XFormsFixture.getSampleForm());
        Assert.assertEquals("Two tables created - patientreq + kid", 2, 
                structure.size());
        
        TableQuery firstTable = structure.get(0); 
        Assert.assertEquals("First table is patientreg", "patientreg", firstTable.getTableName());
        String firstSql = firstTable.getSql();
        Assert.assertTrue("firstSql contains SQL to create patientreq table", 
                firstSql.startsWith("CREATE TABLE " + escapeChar + "patientreg" + escapeChar));
        Assert.assertTrue("firstSql contains SQL to create patientreq table", 
                firstSql.contains("openxdata_form_data_id VARCHAR(50)"));
        Assert.assertTrue("firstSql contains SQL to create patientreq table", 
                firstSql.contains("openxdata_user_id VARCHAR(50)"));
        Assert.assertTrue("firstSql contains SQL to create patientreq table", 
                firstSql.contains("openxdata_user_name VARCHAR(50)"));
        Assert.assertTrue("firstSql contains SQL to create patientreq table", 
                firstSql.contains(escapeChar + "patientid" + escapeChar + " VARCHAR(255)"));
        Assert.assertTrue("firstSql contains SQL to create patientreq table", 
                firstSql.contains(escapeChar + "title" + escapeChar + " VARCHAR(255)"));
        Assert.assertTrue("firstSql contains SQL to create patientreq table", 
                firstSql.contains(escapeChar + "endtime" + escapeChar + " TIME"));
        
        TableQuery secondTable = structure.get(1); 
        Assert.assertEquals("Second table is kid", "kid", secondTable.getTableName());
        String secondSql = secondTable.getSql();
        Assert.assertTrue("secondSql contains SQL to create kid table", 
                secondSql.startsWith("CREATE TABLE " + escapeChar + "kid" + escapeChar));
        Assert.assertTrue("secondSql contains SQL to create kid table", 
                secondSql.contains("Id VARCHAR(200) PRIMARY KEY"));
        Assert.assertTrue("secondSql contains SQL to create kid table", 
                secondSql.contains("Id VARCHAR(200) PRIMARY KEY"));
        Assert.assertTrue("secondSql contains SQL to create kid table", 
                secondSql.contains("openxdata_form_data_id VARCHAR(50)"));
        Assert.assertTrue("secondSql contains SQL to create kid table", 
                secondSql.contains("openxdata_user_id VARCHAR(50)"));
        Assert.assertTrue("secondSql contains SQL to create kid table", 
                secondSql.contains("openxdata_user_name VARCHAR(50)"));
        Assert.assertTrue("secondSql contains SQL to create kid table", 
                secondSql.contains(escapeChar + "kidname" + escapeChar + " VARCHAR(255)"));
        Assert.assertTrue("secondSql contains SQL to create kid table", 
                secondSql.contains(escapeChar + "kidsex" + escapeChar + " VARCHAR(255)"));
        
        Assert.assertTrue("firstSql ends with INNODB table definition", 
                firstSql.endsWith("Engine = INNODB;"));
        Assert.assertTrue("secondSql ends with INNODB table definition", 
                secondSql.endsWith("Engine = INNODB;"));
    }
    
	/**
	 * Tests for an NPE that occurs when type="barcode" is used in a binding.
	 * For now, it's not clear that it is supported. When it is, this should no
	 * longer be ignored.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testOXD330() throws Exception {
		String formDefString = XFormsFixture.getFormFromStudyExport(
						"/org/openxdata/server/export/rdbms/engine/BaselineSurvey-MalariaConsortium.xml",
						"Baseline Survey", "Household Questionnaire", "v1");
		List<TableQuery> queryList = RdmsEngine.getStructureSql(formDefString);
		// Should never get here due to NPE
		Assert.assertFalse(queryList.isEmpty());
	}
}