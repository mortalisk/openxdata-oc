package org.openxdata.client;

import com.google.gwt.i18n.client.Messages;

public interface AppMessages extends Messages {
	String title();
	String logo();
	String logoUrl();
	String user();
	String login();
	String logout();
	String userProfile();	
	String myDetails();
	String username();
	String passWord();
	String oldPassWord();
	String changeMyPassword();
	String newPassWord();
	String confirmPassword();
	String resetPassword();
	String oldPasswordNotValid();
	String passwordNotSame();
	String profileNotSaved();
	String profileSaved();
	String userNotFound();
	String save();
	String cancel();
	String close();
	String firstName();
	String lastName();
	String phoneNo();
	String eMail();
	String language();
	
	String auditFields();
	String responseDataFields();
	String id();
	String form();
	String version();
	String status();
	String organisation();
	String creator();
	String changed();
	String responses();
	String listOfForms();
	String captureData();
	String formMustBeSelected();
	String noResponses();
	String printForm();
	String viewResponses();
	
	String exportToCSV();
	String dataCapture();
	String date();
	String capturer();
	String browseResponses();
	String editResponse();
	String showAllVersions();
	String showPublishedVersions();
	
	String dataSavedSucessfully(String sessionReference);
	String pleaseTryAgainLater(String technicalMessage);
	String errorWhileRetrievingForms();
	String accessDeniedError();
	String sessionExpired();
	String disclaimer();
	String and();
	
	String itemsPerPage(String pageSize);
	String unsuccessfulLogin();
	
	String loading();
	String success();
	String error();
	
	String admin();
	String study();
	String edit();
	String delete();
	String newStudyOrForm();
	String editStudyOrForm();
	String deleteStudyOrForm();
	
	String next();
	String back();
	String finish();
	String saveAndExit();
	String areYouSure();
	String areYouSureWizard();
	String stepOf(int step, int total);
	
	String newStudyFormOrVersionHeading();
	String design();
	String addNewStudy();
	String existingStudy();
	String studyName();
	String studyDescription();
	String addNewForm();
	String existingForm();
	String formName();
	String formDescription();
	String formVersion();
	String formVersionName();
	String formVersionDescription();
	String formVersionDefault();
	String designForm();

	String setUserAccessToStudy();

	String allUsers();

	String usersWithAccessToStudy();

	String addAllUsers();

	String addUser();

	String removeAllUsers();

	String removeUser();

	String setUserAccessToForm();

	String usersWithAccessToForm();

	String unableToDeleteFormWithData();

	String areYouSureDelete();
	
	String selectFormVersion();
}
