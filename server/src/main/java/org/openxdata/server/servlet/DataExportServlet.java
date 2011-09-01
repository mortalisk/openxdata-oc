package org.openxdata.server.servlet;

import static org.openxdata.server.servlet.Verify.isValidDate;
import static org.openxdata.server.servlet.Verify.isValidId;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.openxdata.server.export.DataExport;
import org.openxdata.server.validation.FileValidations;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Servlet that handles export of collected data. For now export are in CSV.
 *
 * @author daniel
 * @author Jonny Heggheim
 * @author maimoona kausar
 */
//TODO This class should be renamed to something like CSVDataExportServlet
//I agree - Jonny Heggheim
public class DataExportServlet extends HttpServlet {

	private DataExport dataExport;
    private static final long serialVersionUID = 1239141102030345L;
    
    @Override
	public void init() {
    	
		ServletContext sctx = this.getServletContext();
		WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(sctx);
		
		dataExport = (DataExport) ctx.getBean("cvsDataExport");
    	
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sFormId = request.getParameter("formId");
        String sFromDate = request.getParameter("fromDate");
        String sToDate = request.getParameter("toDate");
        String sUserId = request.getParameter("userId");
        String filename = request.getParameter("filename");

        if (!isValidId(sFormId)) {
        	ServletUtils.setBadRequest(response, "Missing or wrong parameter formId");
            return;
        }

        Date fromDate = null;
        if (isValidDate(sFromDate)) {
            fromDate = new Date(Long.parseLong(sFromDate));
        }

        Date toDate = null;
        if (isValidDate(sToDate)) {
            toDate = new Date(Long.parseLong(sToDate));
            toDate = DateUtils.addDays(toDate, 1);
        }

        Integer userId = null;
        if (isValidId(sUserId)) {
            userId = Integer.parseInt(sUserId);
        }

        if (StringUtils.isBlank(filename)) {
            filename = "dataexport";
        }
        filename=filename.trim();
        if (!FileValidations.validateOutputFilename(filename)) {
        	ServletUtils.setBadRequest(response, "File name is either too long or contains special characters");
            return;        
        }
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setDateHeader("Expires", -1);
        response.setHeader("Pragma", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        response.addHeader("Cache-Control", "no-store");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename + ".csv");

        dataExport.export(response.getWriter(), Integer.parseInt(sFormId), fromDate, toDate, userId);
    }

	/**
	 * @param dataExport
	 */
	public void setDataExport(DataExport dataExport) {
		this.dataExport = dataExport;
		
	}
}
