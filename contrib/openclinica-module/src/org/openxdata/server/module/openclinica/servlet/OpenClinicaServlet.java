package org.openxdata.server.module.openclinica.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openxdata.server.module.openclinica.OpenClinicaServer;


/**
 * 
 * @author daniel
 *
 */
public class OpenClinicaServlet extends HttpServlet {

	private static final long serialVersionUID = 1239111102012345L;
	
		
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		new OpenClinicaServer().processConnection(request.getInputStream(), response.getOutputStream());
	}
}
