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
package org.openxdata.server.service.impl;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.OpenXDataSessionExpiredException;
import org.openxdata.server.admin.model.exception.UnexpectedException;
import org.openxdata.server.admin.model.exception.UserNotFoundException;
import org.openxdata.server.dao.RoleDAO;
import org.openxdata.server.dao.SettingDAO;
import org.openxdata.server.dao.UserDAO;
import org.openxdata.server.security.util.OpenXDataSecurityUtil;
import org.openxdata.server.service.MailService;
import org.openxdata.server.service.UserService;
import org.openxdata.server.util.OpenXDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.security.annotation.Secured;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameMappingStrategy;

/**
 * Default implementation for <code>UserService interface</code>.
 * 
 * @author dagmar@cell-life.org.za
 * @author Angel
 * 
 */
@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private RoleDAO roleDAO;
   
    @Autowired
    private MailService mailService;
    
    @Autowired
    private SettingDAO settingDAO;
    
    @Autowired
    private MessageSource messageSource;

    private Logger log = Logger.getLogger(this.getClass());

    @Override
	@Transactional(readOnly = true)
	@Secured("Perm_View_Users")
    public List<User> getUsers() {
        return userDAO.getUsers();
    }

    @Override
	@Transactional(readOnly = true)
	// note: no security on this method (can be reviewed)
    public User getLoggedInUser() throws OpenXDataSessionExpiredException {
        try {
        	// load a copy
            return findUserByUsername(OpenXDataSecurityUtil.getLoggedInUser().getName()); 
        } catch (UserNotFoundException ex) {
            throw new UnexpectedException("User logged in, but is not in database");
        }
    }

    private void verifyUserIsNotNull(User user, String criteria, String value) throws UserNotFoundException {
        if (user == null) {
            throw new UserNotFoundException(criteria, value);
        }
    }

    @Override
	@Transactional(readOnly = true)
	// note: no security for this method because it is used in authentication
    public User findUserByUsername(String username) throws UserNotFoundException {
        User user = userDAO.getUser(username);
        verifyUserIsNotNull(user, "username", username);
        return user;
    }

    @Override
	@Transactional(readOnly = true)
	// note: no security for this method because it is used by reset password functionality
    public User findUserByEmail(String email) throws UserNotFoundException {
        User user = userDAO.findUserByEmail(email);
        verifyUserIsNotNull(user, "email", email);
        return user;
    }

    @Override
	@Transactional(readOnly = true)
	// note: no security for now (can be reviewed). currently used in DataImportServlet
    public User findUserByPhoneNo(String phoneNo) throws UserNotFoundException {
        User user = userDAO.findUserByPhoneNo(phoneNo);
        verifyUserIsNotNull(user, "phoneNo", phoneNo);
        return user;
    }

    @Override
    @Secured("Perm_Add_Users")
	public void saveUser(User user) {
        if (user.hasNewPassword()) {
            user.setSalt(OpenXDataSecurityUtil.getRandomToken());
            user.setPassword(OpenXDataSecurityUtil.encodeString(user.getClearTextPassword() + user.getSalt()));
        }

        boolean newUser = user.getId() == 0 ? true : false;

        userDAO.saveUser(user);
        
        if (newUser && StringUtils.isNotEmpty(user.getEmail())) {
        	String enable = settingDAO.getSetting("enableNewUserEmail");
        	if (enable != null && enable.equalsIgnoreCase("true")) {
        		Locale locale = Locale.ENGLISH; // FIXME: calculate user's actual locale (will have to be stored in their user profile)
        		String serverUrl = settingDAO.getSetting("serverUrl");
		        String subject = messageSource.getMessage("newUserEmailSubject", new Object[]{}, locale);
		        String text = messageSource.getMessage("newUserEmail", 
		        		new Object[] { user.getFirstName(), user.getName(), user.getClearTextPassword(), serverUrl }, 
		        		locale);
		        try {
		        	mailService.sendEmail(user.getEmail(), subject, text);
		        } catch (MailException ex) {
		            log.error("Error while sending an email to the new user " + user.getName() 
		            		+ ". Please check your mail settings in the properties file.", ex);
		        }
        	}
        }
    }

    @Override
    // note: used during reset forgotten password, so security should be accessable to all users
	public void resetPassword(User user, int size) {
        log.debug("UserServiceImpl resetPassword");
        String password = UUID.randomUUID().toString();
        password = password.replace("-", "").substring(0, size);
        user.setClearTextPassword(password);
        saveUser(user);
    }

    @Override
    @Secured("Perm_Delete_Users")
	public void deleteUser(User user) {
        userDAO.deleteUser(user);
    }

    @Override
	@Transactional(readOnly = true)
	// note: no security - used during login for admin user
    public Boolean checkIfUserChangedPassword(User user) {
        return OpenXDataUtil.checkIfUserChangedPassword(user);
    }

    /**
     * @see org.openxdata.server.service.UserService#importUsers(java.lang.String)
     */
    @Override
    @Secured("Perm_Add_Users")
	public String importUsers(String filecontents) {
        CSVReader reader = new CSVReader(new StringReader(filecontents));
        UserWrapper.setRoleDAO(roleDAO);
        HeaderColumnNameMappingStrategy<UserWrapper> strat = new HeaderColumnNameMappingStrategy<UserWrapper>();
        strat.setType(UserWrapper.class);

        CsvToBean<UserWrapper> csv = new CsvToBean<UserWrapper>();
        List<UserWrapper> list = csv.parse(strat, reader);

        User loggedInUser = getLoggedInUser();
        List<UserWrapper> errors = saveUserList(list, loggedInUser);


        String errorCSV = getErrorCSV(errors);
        return errorCSV;
    }

    private String getErrorCSV(List<UserWrapper> errors) {
        if (!errors.isEmpty()) {
            try {
                StringWriter stringWriter = new StringWriter();
                CSVWriter writer = new CSVWriter(stringWriter, ',');
                String[] headers = getColumns();
                writer.writeNext(headers);
                for (UserWrapper user : errors) {
                    writer.writeNext(userToCSV(user));
                }
                writer.close();
                return stringWriter.toString();
            } catch (IOException e) {
                throw new UnexpectedException("Error writing user import errors.");
            }
        }
        return null;
    }

    private List<UserWrapper> saveUserList(List<UserWrapper> list, User loggedInUser) {
        List<UserWrapper> uersWithErrors = new ArrayList<UserWrapper>();

        for (UserWrapper user : list) {
            if (user.hasErrors()) {
                uersWithErrors.add(user);
            } else {
                user.setCreator(loggedInUser);
                user.setDateCreated(new Date());
                try {
                    findUserByUsername(user.getName());
                    user.getErrors().add("User with same username already exists");
                    uersWithErrors.add(user);
                } catch (UserNotFoundException userShouldNotExist) {
                    saveUser(user.getUser());
                }
            }
        }
        return uersWithErrors;
    }

    private String[] getColumns() {
        return new String[]{"name", "firstName", "middleName", "lastName", "phoneNo", "email",
                    "clearTextPassword", "roles", "error messages"};
    }

    private String[] userToCSV(UserWrapper user) {
        String errorString = user.getErrorString();
        return new String[]{user.getName(), user.getFirstName(), user.getMiddleName(),
                    user.getLastName(), user.getPhoneNo(), user.getEmail(),
                    user.getClearTextPassword(), user.getRoles(), errorString};
    }

    @Override
    // note: no security required for logout
	public void logout() {
        //Clear the Security Context
        if (SecurityContextHolder.getContext() != null) {
            SecurityContextHolder.clearContext();
        }
    }
}
