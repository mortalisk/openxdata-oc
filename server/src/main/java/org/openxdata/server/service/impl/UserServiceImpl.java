package org.openxdata.server.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.Role;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.exception.OpenXDataSessionExpiredException;
import org.openxdata.server.admin.model.exception.UnexpectedException;
import org.openxdata.server.admin.model.exception.UserNotFoundException;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.admin.model.mapping.UserStudyMap;
import org.openxdata.server.admin.model.paging.PagingLoadConfig;
import org.openxdata.server.admin.model.paging.PagingLoadResult;
import org.openxdata.server.dao.RoleDAO;
import org.openxdata.server.dao.SettingDAO;
import org.openxdata.server.dao.UserDAO;
import org.openxdata.server.security.OpenXDataSessionRegistry;
import org.openxdata.server.security.util.OpenXDataSecurityUtil;
import org.openxdata.server.service.MailService;
import org.openxdata.server.service.StudyManagerService;
import org.openxdata.server.service.UserService;
import org.openxdata.server.util.OpenXDataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Autowired
    private OpenXDataSessionRegistry sessionRegistry;
    
    @Autowired
    private StudyManagerService studyService;

    private Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
	@Transactional(readOnly = true)
	@Secured("Perm_View_Users")
    public List<User> getUsers() {
        return userDAO.getUsers();
    }
    
	@Override
	@Transactional(readOnly = true)
	@Secured("Perm_View_Users")
    public PagingLoadResult<User> getUsers(PagingLoadConfig pagingLoadConfig) {
		return userDAO.findAllByPage(pagingLoadConfig, "name");
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
    	
        checkAndSetUserLoginProperties(user);
		userDAO.saveUser(user);

		sessionRegistry.updateUserEntries(user);

		sendEmailToNewUser(user);
    }
    
    @Override
    @Secured({"Perm_Edit_My_User", "Perm_Add_Users"})
    public void saveMyUser(User user) {
    	String loggedInUserName = OpenXDataSecurityUtil.getLoggedInUser().getName();
    	if (loggedInUserName.equals(user.getName())) {
	    	checkAndSetUserLoginProperties(user);
			userDAO.saveUser(user);
			sessionRegistry.updateUserEntries(user);
    	} else {
    		throw new OpenXDataSecurityException("User "+user.getName()+" is not currently logged in user ("+loggedInUserName+")");
    	}
    }

	private void checkAndSetUserLoginProperties(User user) {
		if (user.hasNewPassword()) {
            user.setSalt(OpenXDataSecurityUtil.getRandomToken());
            user.setPassword(OpenXDataSecurityUtil.encodeString(user.getClearTextPassword() + user.getSalt()));
        }
	}

	private void sendEmailToNewUser(User user) {
		
		boolean newUser = user.getId() == 0 ? true : false;
		if (newUser && StringUtils.isNotEmpty(user.getEmail())) {
        	String enable = settingDAO.getSetting("enableNewUserEmail");
        	if (enable != null && enable.equalsIgnoreCase("true")) {
        		// FIXME: calculate user's actual locale (will have to be stored in their user profile)
        		Locale locale = Locale.ENGLISH; 
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

    @Override
    @Secured("Perm_Add_Users")
	public String importUsers(String filecontents) {
    	List<UserImportBean> list = getUsersToImport(filecontents);
    	 
        log.info("String import of " + list.size() + " users");
        File errorFile;
        CSVWriter csvWriter;
        try {
			errorFile = File.createTempFile("openxdata-","-userimporterrors.csv");
			FileWriter fileWriter = new FileWriter(errorFile);
			csvWriter = new CSVWriter(fileWriter, ',');
			csvWriter.writeNext(UserImportBean.getColumnHeaders());
		} catch (Exception e) {
			 throw new UnexpectedException("Error writing user import errors.", e);
		}
		
		User loggedInUser = getLoggedInUser();
		boolean hasErrors = false;
		for (int i = 0; i < list.size(); i++) {
			UserImportBean userImportBean = list.get(i);
			User user = userImportBean.getUser();
			List<String> errors = setRoles(user, userImportBean.getRoles());
			List<UserFormMap> userFormMaps = getUserFormMaps(userImportBean.getFormPermissions(), errors);
			List<UserStudyMap> userStudyMaps = getUserStudyMaps(userImportBean.getStudyPermissions(), errors);
			if (validate(user, errors)){
				 user.setCreator(loggedInUser);
	             user.setDateCreated(new Date());
	             saveUser(user);
	             for (UserStudyMap userStudyMap : userStudyMaps) {
	            	 userStudyMap.setUserId(user.getId());
	            	 studyService.saveUserMappedStudy(userStudyMap);
	             }
	             for (UserFormMap userFormMap : userFormMaps) {
	            	 userFormMap.setUserId(user.getId());
	            	 studyService.saveUserMappedForm(userFormMap);
	             }
			} else {
				hasErrors = true;
				userImportBean.setErrors(errors);
				csvWriter.writeNext(userImportBean.toStringArray());
			}
			log.debug("Processed " + i + " users for import");
		}
		
        try {
			csvWriter.close();
		} catch (IOException e) {
			 throw new UnexpectedException("Error writing user import errors.", e);
		}
		
		if (hasErrors){
			return errorFile.getAbsolutePath();
		} else {
			return null;
		}
    }
    
	private List<UserStudyMap> getUserStudyMaps(String studyPermissions,
			List<String> errors) {
		if (studyPermissions.trim().isEmpty()) {
			log.debug("No study permissions");
			return Collections.emptyList();
		}

		List<UserStudyMap> userStudyMaps = new ArrayList<UserStudyMap>();
		String[] studyArr = studyPermissions.split(",");
		for (String studyName : studyArr) {
			List<StudyDef> list = studyService.getStudyByName(studyName);
			if (list.size() == 1) {
				UserStudyMap map = new UserStudyMap();
				map.setStudyId(list.get(0).getId());
				userStudyMaps.add(map);
			} else if (list.size() > 1) {
				errors.add("More than one study matched study name: "
						+ studyName);
			} else if (list.isEmpty()) {
				errors.add("No study matched study name: " + studyName);
			}
		}
		return userStudyMaps;
	}

	private List<UserFormMap> getUserFormMaps(String formPermissions,
			List<String> errors) {
		if (formPermissions.trim().isEmpty()) {
			log.debug("No form permissions");
			return Collections.emptyList();
		}

		List<UserFormMap> maps = new ArrayList<UserFormMap>();
		String[] formArr = formPermissions.split(",");
		for (String formName : formArr) {
			List<FormDef> list = studyService.getFormByName(formName);
			if (list.size() == 1) {
				UserFormMap map = new UserFormMap();
				map.setFormId(list.get(0).getId());
				maps.add(map);
			} else if (list.size() > 1) {
				errors.add("More than one form matched form name: " + formName);
			} else if (list.isEmpty()) {
				errors.add("No form matched form name: " + formName);
			}
		}
		return maps;
	}
	
	private List<UserImportBean> getUsersToImport(String filecontents) {
		CSVReader reader = new CSVReader(new StringReader(filecontents));
		HeaderColumnNameMappingStrategy<UserImportBean> strat = new HeaderColumnNameMappingStrategy<UserImportBean>();
		strat.setType(UserImportBean.class);

		CsvToBean<UserImportBean> csv = new CsvToBean<UserImportBean>();
		List<UserImportBean> list = csv.parse(strat, reader);
		return list;
	}
	
	/**
	 * Takes a comma separated list of role names and converts it into a list of
	 * roles by doing a database lookup of the name.
	 * 
	 * @param user
	 *            The user to add the roles to
	 * @param roles
	 *            A comma separated list of role names
	 * @return a list error messages
	 **/
	private List<String> setRoles(User user, String roles) {
		List<String> errors = new ArrayList<String>();
		if (roles.trim().isEmpty()) {
			log.debug("No roles for import user: " + user.getName());
			errors.add("No roles specified");
			return errors;
		}

		String[] roleArr = roles.split(",");
		for (String roleName : roleArr) {
			List<Role> dbRoles = roleDAO.getRolesByName(roleName.trim());
			if (dbRoles.size() == 1) {
				user.addRole(dbRoles.get(0));
			} else if (dbRoles.size() > 1) {
				errors.add("More than one role matched role name: " + roleName);
			} else if (dbRoles.isEmpty()) {
				errors.add("No role matched role name: " + roleName);
			}
		}
		return errors;
	}
	    
	private boolean validate(User user, List<String> errors) {
		if (user.getName().isEmpty()) {
			errors.add("name is empty");
		}

		if (user.getClearTextPassword().isEmpty()) {
			errors.add("clearTextPassword is empty");
		}

		try {
			findUserByUsername(user.getName());
			errors.add("User with same username already exists");
		} catch (UserNotFoundException userShouldNotExist) {
			// ignore
		}

		return errors.isEmpty();
	}

    @Override
    // note: no security required for logout
	public void logout() {
        //Clear the Security Context
        if (SecurityContextHolder.getContext() != null) {
            String currentSession = OpenXDataSecurityUtil.getCurrentSession();
            sessionRegistry.removeSessionInformation(currentSession+"");
            SecurityContextHolder.clearContext();
        }
    }

	
    @Override
    @Secured("Perm_Add_Users")
	public void saveUsers(List<User> users) {
    	for(User user : users){
    		saveUser(user);
    	}
	}

	
    @Override
    @Secured("Perm_Delete_Users")
	public void deleteUsers(List<User> users) {
    	for(User user : users){
    		deleteUser(user);
    	}
	}
}
