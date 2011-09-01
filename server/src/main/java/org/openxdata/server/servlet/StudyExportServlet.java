package org.openxdata.server.servlet;

import static org.openxdata.server.servlet.Verify.isValidId;
import static org.openxdata.server.servlet.Verify.isValidType;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.openxdata.server.OpenXDataConstants;
import org.openxdata.server.export.FormExport;
import org.openxdata.server.export.StudyExport;
import org.openxdata.server.export.VersionExport;
import org.openxdata.server.service.DataExportService;
import org.openxdata.server.validation.FileValidations;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Servlet that handles export of studies.
 *
 * @author daniel
 * @author Jonny Heggheim
 * @author maimoona kausar
 */
public class StudyExportServlet extends HttpServlet {

	private DataExportService dataExportService;
	
    private static final long serialVersionUID = 1119111102030345L;
    
    @Override
    public void init() throws ServletException {
		ServletContext sctx = this.getServletContext();
		WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(sctx);
		dataExportService = (DataExportService) ctx.getBean("dataExportService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sid = request.getParameter("id");
        String type = request.getParameter("type");
        String filename = request.getParameter("filename");

        if (!isValidId(sid)) {
        	ServletUtils.setBadRequest(response, "Missing or wrong parameter for id");
            return;
        }

        if (!isValidType(type)) {
        	ServletUtils.setBadRequest(response, "Missing or empty parameter for type");
            return;
        }
        
        if (StringUtils.isBlank(filename)) {
            filename = "dataexport";
        }
        filename=filename.trim();
        if (!FileValidations.validateOutputFilename(filename)) {
        	ServletUtils.setBadRequest(response, "File name is either too long or contains special characters");
            return;        
        }
        String xml = "";
        Integer id = Integer.parseInt(sid);//all params below have been validated above so validation not needed
        if ("study".equals(type)) {
            xml = StudyExport.export(dataExportService.getStudyDef(id));
        } else if ("form".equals(type)) {
            xml = FormExport.export(dataExportService.getFormDef(id));
        } else if ("version".equals(type)) {
            xml = VersionExport.export(dataExportService.getFormDefVersion(id));
        } else {
        	ServletUtils.setBadRequest(response, type + " is not a valid valid type");
            return;
        }

        response.setContentType(OpenXDataConstants.HTTP_HEADER_CONTENT_TYPE_XML);
        response.setDateHeader("Expires", -1);
        response.setHeader("Content-Disposition", "attachment; filename=" + filename + ".xml");
        response.setHeader("Pragma", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        response.addHeader("Cache-Control", "no-store");

        response.getOutputStream().print(xml);
    }

    void setDataExportService(DataExportService dataExportService) {
        this.dataExportService = dataExportService;
    }
}