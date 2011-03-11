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
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openxdata.server.OpenXDataPropertyPlaceholderConfigurer;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.UserNotFoundException;
import org.openxdata.server.security.OpenXDataUserDetails;
import org.openxdata.server.service.UserService;
import org.openxdata.server.validation.PersonIdentificationValidations;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.context.SecurityContextImpl;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ResetPasswordServlet extends HttpServlet {

    private static final long serialVersionUID = 2310768931768394086L;
    private Logger log = Logger.getLogger(this.getClass());
    private MailSender mailSender;
    private UserService userService;
    private UserDetailsService userDetailsService;
    private OpenXDataPropertyPlaceholderConfigurer appSettings;
    private MessageSource messageSource;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        ServletContext sctx = config.getServletContext();
        WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(sctx);
        mailSender = (MailSender) ctx.getBean("mailSender");
        userService = (UserService) ctx.getBean("userService");
        userDetailsService = (UserDetailsService) ctx.getBean("userDetailsService");
        appSettings = (OpenXDataPropertyPlaceholderConfigurer) ctx.getBean("appSettings");
        messageSource = (ResourceBundleMessageSource) ctx.getBean("messageSource");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //ajax response reference text
        PrintWriter out = response.getWriter();

        String email = request.getParameter("email");
        if (StringUtils.isBlank(email)) {
            out.println("noEmailSuppliedError");
            return;
        }
        email=email.trim();
        if(!PersonIdentificationValidations.validateEmail(email)){
            out.println("invalidEmailSuppliedError");
            return;
        }

        User user = null;
        try {
            user = userService.findUserByEmail(email);
        } catch (UserNotFoundException userNotFound) {
            out.println("validationError");
            return;
        }

        String locale = (String) request.getSession().getAttribute("locale");
        Locale userLocale = new Locale(locale);

        insertUserInSecurityContext(user); // this is so that it is possible to reset their password (security checks)

        Properties props = appSettings.getResolvedProps();
        String from = props.getProperty("mailSender.from");

        try {
            resetPasswordAndSendEmail(user, email, from, userLocale, out);
        } catch (Exception e) {
            log.error("Error while resetting the user " + user.getName() + "'s password", e);
            out.println("passwordResetError");
        }
    }

    void resetPasswordAndSendEmail(User user, String to, String from, Locale userLocale, PrintWriter out) throws NoSuchMessageException {
        userService.resetPassword(user, 6);
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(messageSource.getMessage("resetPasswordEmailSubject", new Object[]{user.getFullName()}, userLocale));
        msg.setText(messageSource.getMessage("resetPasswordEmail", new Object[]{user.getName(), user.getClearTextPassword()}, userLocale));
        msg.setFrom(from);
        try {
            mailSender.send(msg);
            out.println("passwordResetSuccessful");
        } catch (MailException ex) {
            log.error("Error while sending an email to the user " + user.getName() + " in order to reset their password.", ex);
            log.error("Password reset email:" + msg.toString());
            out.println("emailSendError");
        }
    }

    private void insertUserInSecurityContext(User user) {
        OpenXDataUserDetails userDetails = (OpenXDataUserDetails) userDetailsService.loadUserByUsername(user.getName());
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, userDetails, userDetails.getAuthorities());
        SecurityContext sc = new SecurityContextImpl();
        sc.setAuthentication(auth);
        SecurityContextHolder.setContext(sc);
    }

    void setUserService(UserService userService) {
        this.userService = userService;
    }

    void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    void setAppSettings(OpenXDataPropertyPlaceholderConfigurer propertyPlaceholder) {
        this.appSettings = propertyPlaceholder;
    }

    void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}
