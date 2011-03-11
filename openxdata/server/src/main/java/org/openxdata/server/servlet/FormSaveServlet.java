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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openxdata.server.OpenXDataConstants;
import org.openxdata.server.validation.FileValidations;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;


/**
 * Servlet that handles saving of files.
 * 
 * @author daniel
 * @author maimoona kausar
 *
 */
public class FormSaveServlet extends HttpServlet{

	public static final long serialVersionUID = 111111111111112L;

	private static final String KEY_FILE_CONTENTS = "FileContents";
	private static final String KEY_FILE_NAME = "FileNname";
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
			String filecontents = null;
			CommonsMultipartResolver multipartResover = new CommonsMultipartResolver(/*this.getServletContext()*/);
			if(multipartResover.isMultipart(request)){
				MultipartHttpServletRequest multipartRequest = multipartResover.resolveMultipart(request);
				filecontents = multipartRequest.getParameter("filecontents");
				if (filecontents == null || filecontents.trim().length() == 0)
					return;
			}
			String filename= request.getParameter("filename");
			if(StringUtils.isBlank(filename)){
				filename = "filename.xml";
			}
			filename=filename.trim();
			if(!FileValidations.validateOutputFilename(filename)){
				ServletUtils.setBadRequest(response, "filename was found to be invalid");
                return;
			}
			filename = filename.replaceAll("\\p{Zs}", "-");

			HttpSession session = request.getSession();			
			session.setAttribute(KEY_FILE_NAME, filename);
			session.setAttribute(KEY_FILE_CONTENTS, filecontents);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		HttpSession session = request.getSession();
		
		response.setHeader(OpenXDataConstants.HTTP_HEADER_CONTENT_DISPOSITION, OpenXDataConstants.HTTP_HEADER_CONTENT_DISPOSITION_VALUE + session.getAttribute(KEY_FILE_NAME));
		response.setContentType(OpenXDataConstants.HTTP_HEADER_CONTENT_TYPE_XML); 
		
		response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", -1);
        response.setHeader("Cache-Control", "no-store");
        
		response.getOutputStream().print((String)session.getAttribute(KEY_FILE_CONTENTS));
	}
}
