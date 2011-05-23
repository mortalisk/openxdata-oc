package org.openxdata.server.permissions;

import org.junit.Test;
import org.openxdata.server.admin.model.Report;
import org.openxdata.server.admin.model.ReportGroup;
import org.openxdata.server.admin.model.exception.OpenXDataException;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;


/**
 * Tests permissions for accessing reports.
 * 
 * @author daniel
 *
 */
public class ReportPermissionsTest extends PermissionsTest {

    @Test(expected=OpenXDataSecurityException.class)
    public void getReports_shouldThrowOpenXDataSecurityException() throws OpenXDataException {
    	reportService.getReports();
    }
    
    @Test(expected=OpenXDataSecurityException.class)
    public void saveReport_shouldThrowOpenXDataSecurityException() throws OpenXDataException {
    	reportService.saveReport(new Report());
    }
    
    @Test(expected=OpenXDataSecurityException.class)
    public void saveReportGroup_shouldThrowOpenXDataSecurityException() throws OpenXDataException {
    	reportService.saveReportGroup(new ReportGroup());
    }
    
    @Test(expected=OpenXDataSecurityException.class)
    public void deleteReportGroup_shouldThrowOpenXDataSecurityException() throws OpenXDataException {
    	reportService.deleteReportGroup(new ReportGroup());
    }
    
    @Test(expected=OpenXDataSecurityException.class)
    public void deleteReport_shouldThrowOpenXDataSecurityException() throws OpenXDataException {
    	reportService.deleteReport(new Report());
    }
}
