package org.openxdata.server.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.openxdata.server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;


/**
 * Servlet that handles opening of files.
 * 
 * @author daniel
 *
 */
public class ImportServlet extends HttpServlet{

	private UserService userService;
	
	private Logger log = LoggerFactory.getLogger(ImportServlet.class);
	private static final long serialVersionUID = -5428881060119162407L;

    @Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		ServletContext sctx = this.getServletContext();
		WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(sctx);
		userService = (UserService) ctx.getBean("userService");
	}
    
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String errorFileName = "no file";
		response.setHeader("Content-Type", "text/html;charset=UTF-8");
		
		try{
			String importType = request.getParameter("type");
			String filecontents = null;
			
			CommonsMultipartResolver multipartResover = new CommonsMultipartResolver(/*this.getServletContext()*/);
			if(multipartResover.isMultipart(request)){
				MultipartHttpServletRequest multipartRequest = multipartResover.resolveMultipart(request);
				MultipartFile uploadedFile = multipartRequest.getFile("filecontents");
				if (uploadedFile != null && !uploadedFile.isEmpty()) {
					filecontents = IOUtils.toString(uploadedFile.getInputStream());
				}
			}
			
			if (filecontents != null){
				log.info("Starting import of type: " + importType);
				
				if (importType.equals("user")){
					errorFileName = userService.importUsers(filecontents);
				}else {
					log.warn("Unknown import type: " + importType);
				}
			}
			response.setStatus(HttpServletResponse.SC_OK);
			if (errorFileName != null){
				response.getOutputStream().print(errorFileName);
			}
		}
		catch(Exception ex){
			log.error("Error during import", ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getOutputStream().print("Error: " + ex.getMessage());
		}
		response.getOutputStream().flush();
	}
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String filename = request.getParameter("filename");
		if (filename == null || filename.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		}
		File file = new File(filename);
		if (file.getName().startsWith("openxdata-")) {
			if (!file.exists()) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			writeErrorsToResponse(
					IOUtils.toString(new FileInputStream(filename), "UTF-8"),
					response);
		} else {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
	}

	private void writeErrorsToResponse(String errors, HttpServletResponse response) throws IOException {
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", -1);
		response.setHeader("Cache-Control", "no-store");

		response.setHeader("Content-Type", "text/csv;charset=UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=importErrros.csv");	

		response.getWriter().write(errors);
	}
}