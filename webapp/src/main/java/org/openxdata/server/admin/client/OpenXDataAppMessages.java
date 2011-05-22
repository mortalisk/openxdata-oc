package org.openxdata.server.admin.client;

import com.google.gwt.i18n.client.Messages;

public interface OpenXDataAppMessages extends Messages {
	
	String datasets();
	
	String addAnotherDatasetGroup();
	
	String newDatasetGroup();
	
	String pleaseAddDatasetGroup();
	
	String newDataset();
	
	String selectItemDelete();
	
	String deleteConfirmation();
	
	String deleteDataset();
	
	String noPrivilegesDeleteDatasets();

	String noPrivilegesAddDatasets();
	
	String addDatasetGroup();
	
	String deleteDatasetGroup();
	
	String addDataset();
	
	String formSource();
	
	String datasetFields();
	
	String formHasNoQuestions();

	String accountDisabled();

	String invalidUnameOrPass();

	String noPermissions();

	String securityAdminChangePassInfo();

	String resumeCredentials();

	String uname();

	String pass();

	String login();

	String enterNameProceed();
	
	String enterPassProceed();

	String wrongCredentials();

	String authenticationFailure();

	String enterDetailsToChangePassword();

	String oldPassword();

	String reenterPassword();

	String newPassword();

	String cancel();

	String mismatchPasswords();

	String notAdminPassword();

	String sameAdminPassword();

	String lessThanDefaultLengthPassword();

	String adminDefaultPasswordChangeCancel();

	String passwordChangeSuccessful();
}
