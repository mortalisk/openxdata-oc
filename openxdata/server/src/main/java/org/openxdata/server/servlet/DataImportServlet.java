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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.UserNotFoundException;
import org.openxdata.server.security.OpenXDataUserDetails;
import org.openxdata.server.security.OpenXdataUserDetailsService;
import org.openxdata.server.security.util.OpenXDataSecurityUtil;
import org.openxdata.server.service.AuthenticationService;
import org.openxdata.server.service.FormDownloadService;
import org.openxdata.server.service.UserService;
import org.openxdata.server.validation.PersonIdentificationValidations;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Servlet that handles the import of data (data upload)
 * 
 * @author dagmar@cell-life.org.za
 * @author maimoona kausar
 */
public class DataImportServlet extends HttpServlet {

    private UserService userService;
    
    private UserDetailsService userDetailsService;

    private FormDownloadService formDownloadService;

    private AuthenticationService authenticationService;
    
    private Logger log = Logger.getLogger(this.getClass());
    
    private static final long serialVersionUID = 5669577564787538190L;
    
    @Override
	public void init() throws ServletException {
		
		ServletContext sctx = this.getServletContext();
		WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(sctx);
		
		// Manual Injection
		userService = (UserService) ctx.getBean("userService");
		userDetailsService = (UserDetailsService) ctx.getBean("userDetailsService");
		formDownloadService = (FormDownloadService) ctx.getBean("formDownloadService");
		authenticationService = (AuthenticationService) ctx.getBean("authenticationService");
	}

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletOutputStream out = response.getOutputStream();
        try {
            // authenticate user
            User user = getUser(request.getHeader("Authorization"));
            if (user != null) {
                log.info("authenticated user:");
                // check msisdn
                String msisdn = request.getParameter("msisdn");
                if (PersonIdentificationValidations.validateCellNumber(msisdn)) {
                    // if an msisdn is sent, then we retrieve the user with that phone number
                    authenticateUserBasedOnMsisd(msisdn);
                }else{
                    out.println("msisdn have invalid format");
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                // can be empty or null, then the default is used. this parameter is a key in the settings table indicating the classname of the serializer to use
                String serializer = request.getParameter("serializer");

                // input stream
                // first byte contains number of forms (x)
                // followed by x number of UTF strings (use writeUTF method in DataOutput)
                formDownloadService.submitForms(request.getInputStream(), out, serializer);

            } else {
                response.setHeader("WWW-Authenticate", "BASIC realm=\"openxdata\"");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (UserNotFoundException userNotFound) {
            out.println("Invalid msisdn");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        } catch (Exception e) {
            log.error("Could not import data", e);
            out.println(e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            out.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected User authenticateUserBasedOnMsisd(String msisdn) throws UserNotFoundException {
        User msisdnUser = userService.findUserByPhoneNo(msisdn);
        OpenXDataUserDetails userDetails = (OpenXDataUserDetails) userDetailsService.loadUserByUsername(msisdnUser.getName());
        OpenXDataSecurityUtil.setSecurityContext(userDetails);
        log.info("msisdn " + msisdn + " is user " + msisdnUser.getName());

        return msisdnUser;
    }

    protected User getUser(String auth) throws IOException {
        User user = null;

        if (auth != null) {
            // Get encoded user and password, comes after "BASIC "
            String userpassEncoded = auth.substring(6);

            // Decode it, using any base 64 decoder
            // NOTE: can't use MD5 here because unless the same salt is used to hash the password, there is no way a match can be done
            String userpassDecoded = new String(Base64.decodeBase64(userpassEncoded.getBytes()));

            // split the username and password
            String[] userpass = userpassDecoded.split(":");

            // authentication
            user = authenticationService.authenticate(userpass[0], userpass[1]);
        }

        // user will be null if authentication failed
        return user;
    }

    void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    void setUserService(UserService userService) {
        this.userService = userService;
    }

    void setUserDetailsService(OpenXdataUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}
