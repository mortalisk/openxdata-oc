package org.openxdata.server.servlet;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.openxdata.server.OpenXDataConstants;

/**
 * This servlet receives content, saves it, and returns it as a downloadable file.
 */
public class FileDownloadServlet extends HttpServlet {

    private static final long serialVersionUID = 1119111102030345L;
    
    @Override
    public void init() throws ServletException {

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String fileName = request.getParameter("filename");
        
        response.setContentType(OpenXDataConstants.HTTP_HEADER_CONTENT_TYPE);
        response.setDateHeader("Expires", -1);
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        response.setHeader("Pragma", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        response.addHeader("Cache-Control", "no-store");
        
        HttpSession session = request.getSession(true);   
		response.getOutputStream().print(session.getAttribute("FileData").toString());
        
    }
    
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		       		
        InputStream input = request.getInputStream();
        String postData = IOUtils.toString(input, "UTF-8");
		       
        HttpSession session = request.getSession(true);
		session.setAttribute("FileData", postData);
       
	}

}