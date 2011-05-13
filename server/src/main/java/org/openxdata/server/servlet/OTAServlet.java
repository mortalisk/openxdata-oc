/*
 *  Licensed to the OpenXdata Foundation (OXDF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The OXDF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, 
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 *  Copyright 2010 http://www.openxdata.org.
 */
package org.openxdata.server.servlet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 
 * This servlet responds to OTA requests from mobile devices.
 * 
 * @author daniel
 *
 */
public class OTAServlet extends HttpServlet {

	/**
	 * Generated Serialisation ID
	 */
	private static final long serialVersionUID = 980226264108003690L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String filepath = null;
		String s = request.getRequestURI();

		if(s.endsWith(".jad") || s.endsWith(".jar"))
			filepath = getServletContext().getRealPath("") + File.separatorChar + "epihandymobile" + File.separatorChar + s.substring(s.lastIndexOf('/')+1);

		if(filepath == null){
			String wml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> "+
			"<!DOCTYPE wml PUBLIC \"-//WAPFORUM//DTD WML 1.1//EN\" "+
			" \"http://www.wapforum.org/DTD/wml_1.1.xml\"> "+
			"<wml> "+
			"   <card title=\"Download\" id=\"epihandymobile\"> "+
			"      <p> "+
			"         <a href=\"epihandymobile/epihandy-midlet.jad\">Download EpiHandyMobile</a> "+
			"      </p> "+
			"   </card> "+
			"</wml>";


			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Pragma", "no-cache");
			response.setDateHeader("Expires", -1);
			response.setHeader("Cache-Control", "no-store");

			//response.setContentType("text/vnd.wap.wml");
			response.getOutputStream().print(wml);
			
			System.out.println(wml);
		}
		else
			streamFile(new File(filepath),response);
	}

	/**
	 * Streams a JAD or JAR file to the client.
	 */
	private void streamFile( File file, HttpServletResponse response ) throws IOException, ServletException {

		response.setStatus( HttpServletResponse.SC_OK );
		response.setContentType(file.getPath().endsWith(".jad" ) ?
				"text/vnd.sun.j2me.app-descriptor" : "application/java-archive" );
		response.setContentLength( (int) file.length() );

		FileInputStream fis = new FileInputStream( file );
		BufferedInputStream bis = new BufferedInputStream( fis );
		OutputStream out = response.getOutputStream();
		int ch;

		while( ( ch = bis.read() ) != -1 ){
			out.write( ch );
		}

		bis.close();
		out.close();
	}
}
