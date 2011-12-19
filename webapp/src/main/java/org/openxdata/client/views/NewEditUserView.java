package org.openxdata.client.views;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openxdata.client.Emit;
import org.openxdata.client.controllers.NewEditUserController;
import org.openxdata.client.model.FormSummary;
import org.openxdata.client.model.RoleSummary;
import org.openxdata.client.model.StudySummary;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.server.admin.client.Context;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.model.User;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldSetEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public class NewEditUserView extends WizardView {

	// input field for new user page
	FormPanel createUserPanel;
	private TextField<String> userName;
	private SimpleComboBox<String> status;
	private FieldSet passwordSet;
	private TextField<String> password;
	private TextField<String> confirmPassword;
	private TextField<String> firstName;
	private TextField<String> lastName;
	private TextField<String> middleName;
	private TextField<String> email;
	private TextField<String> phoneNo;
	
	private String checkedUserName;
	private boolean userNameUnique;
	private String checkedEmail;
	private boolean emailUnique;

	private ItemAccessListField<StudySummary> studyUserAccessListField;
	private ItemAccessListField<FormSummary> formUserAccessListField;
	private ItemAccessListField<RoleSummary> roleUserAccessListField;
	
	NewEditUserController controller = (NewEditUserController) NewEditUserView.this.getController();
	
	EventType userViewEvent;
	
	// keep track of created user
	private User user;

	public NewEditUserView(EventType userViewEvent, Controller controller) {
		super(controller);
		this.userViewEvent = userViewEvent;
	}

	@Override
	protected void createButtons() {
		super.createButtons();
	}

	@Override
	protected void display(int activePage, List<LayoutContainer> pages) {
		if (activePage == 0) {
			// user details
			nextButton.setText(appMessages.saveAndNext());
		} else if (activePage == 1) {
			// assign roles
			roleUserAccessListField.refresh();
			nextButton.setText(appMessages.next());
			finishButton.show();
			cancelButton.hide();
		} else if (activePage == 2) {
			// assign study perms
			studyUserAccessListField.refresh();
			nextButton.setText(appMessages.next());
			finishButton.show();
			cancelButton.hide();
		} else if (activePage == 3) {
			// assign form perms
			formUserAccessListField.refresh();
			nextButton.setText(appMessages.next());
			finishButton.show();
			cancelButton.hide();
		}
	}
	
	@Override
	protected void toggleButtons() {
		super.toggleButtons();
        if (activePage == 0) {
        	finishButton.hide();
        	cancelButton.show();
        	saveAndExitButton.show();
        	FormButtonBinding binding = new FormButtonBinding(createUserPanel);
        	saveAndExitButton.setType("submit");
        	binding.addButton(saveAndExitButton);
        	nextButton.setType("submit");
        	binding.addButton(nextButton);
        } else {
        	finishButton.show();
			cancelButton.hide();
			saveAndExitButton.hide();
        }
    }

	@Override
	protected List<LayoutContainer> createPages() {
		List<LayoutContainer> wizardPages = new ArrayList<LayoutContainer>();
		wizardPages.add(createNewUserPage());
		wizardPages.add(createAssignRolePage());
		wizardPages.add(createAssignStudyPage());
		wizardPages.add(createAssignFormPage());
		return wizardPages;
	}

	private LayoutContainer createNewUserPage() {
		createUserPanel = new FormPanel();
		createUserPanel.setStyleAttribute("padding", "10px");
		createUserPanel.setHeaderVisible(false);
		createUserPanel.setBodyBorder(false);
		createUserPanel.setLabelWidth(165);
		createUserPanel.setFieldWidth(300);

		userName = new TextField<String>();
		userName.setFieldLabel(appMessages.username());
		userName.setAllowBlank(false);
		userName.setValidator(new Validator() {
			@Override
			public String validate(Field<?> field, final String value) {
				if (userViewEvent == NewEditUserController.NEWUSER) {
					if (value != null) {
						if (!value.equals(checkedUserName)) { // make sure we don't make 101 RP calls unless the value has changed!
							Scheduler.get().scheduleDeferred(new ScheduledCommand() {
								@Override
								public void execute() {
									NewEditUserController controller = (NewEditUserController) NewEditUserView.this.getController();
									controller.isUserNameUnique(value);
								}
							});
						} else if (!userNameUnique) {
							return appMessages.usernameIsNotUnique();
						}
					}
				}
				return null; // either checking the server or username is unique
			}
		});
		createUserPanel.add(userName);
		if (userViewEvent == NewEditUserController.EDITUSER) {
			userName.setEnabled(false);
		}
		
		status = new SimpleComboBox<String>();
		status.add(Utilities.getUserStatusTypes());		
		status.setFieldLabel(appMessages.status());
		status.setTriggerAction(TriggerAction.ALL);
		status.setValue(status.getStore().getAt(2)); // Pending Approval
		createUserPanel.add(status);
		status.setValidator(new Validator() {
			@Override
			public String validate(Field<?> field, String value) {
				if (value != null) {
					if (status.getSelectedIndex() != 0) { // disabled or pending approval
						// check that the user does not have the role_administrator
						if (user != null && user.hasAdministrativePrivileges()) {
							return appMessages.cannotDisableRoleAdministrator();
						}
					} else {
						// need to validate email address when status is active to ensure it is unique
						email.validate();
					}
				}
				return null;
			}
		});
		
		passwordSet = new FieldSet();
		password = new TextField<String>();
		password.setPassword(true);
		password.setFieldLabel(appMessages.passWord());
		password.setValidator(new Validator() {
			@Override
			public String validate(Field<?> field, String value) {
				if (value != null) {
					String passwordLength = Context.getSetting("defaultPasswordLength", "6");
					if (value.length() < Integer.valueOf(passwordLength)) {
						return appMessages.passwordAtLeastXCharacters(passwordLength);
					}
				}
				return null;
			}
		});
		
		confirmPassword = new TextField<String>();
		confirmPassword.setPassword(true);
		confirmPassword.setFieldLabel(appMessages.confirmPassword());
		confirmPassword.setValidator(new Validator() {
			@Override
			public String validate(Field<?> field, String value) {
				if (value != null) {
					if (password.getValue() != null && !password.getValue().equals(value)) {
						return appMessages.passwordNotSame();
					}
				}
				return null;
			}
		});
		GWT.log("userViewEvent="+userViewEvent);
		if (userViewEvent == NewEditUserController.NEWUSER) {
			createUserPanel.add(password);
			createUserPanel.add(confirmPassword);
			password.setAllowBlank(false);
			confirmPassword.setAllowBlank(false);
		}
		if (userViewEvent == NewEditUserController.EDITUSER) {
			passwordSet.setHeading(appMessages.setPassword());
			passwordSet.addListener(Events.Expand, new Listener<FieldSetEvent>() {
				@Override
				public void handleEvent(FieldSetEvent be) {
					password.setAllowBlank(false);
					confirmPassword.setAllowBlank(false);
				}
			});
			passwordSet.addListener(Events.Collapse, new Listener<FieldSetEvent>() {
				@Override
				public void handleEvent(FieldSetEvent be) {
					password.setAllowBlank(true);
					confirmPassword.setAllowBlank(true);
				}
			});
			FormLayout formLayout = new FormLayout();
			formLayout.setLabelWidth(155);
			formLayout.setDefaultWidth(300);
			passwordSet.setLayout(formLayout);
			passwordSet.setCheckboxToggle(true);
			passwordSet.setExpanded(false);
			passwordSet.add(password);
			passwordSet.add(confirmPassword);
			createUserPanel.add(passwordSet);
		}
		
		firstName = new TextField<String>();
		firstName.setFieldLabel(appMessages.firstName());
		createUserPanel.add(firstName);
		middleName = new TextField<String>();
		middleName.setFieldLabel(appMessages.middleName());
		createUserPanel.add(middleName);
		lastName = new TextField<String>();
		lastName.setFieldLabel(appMessages.lastName());
		createUserPanel.add(lastName);
		email = new TextField<String>();
		email.setFieldLabel(appMessages.eMail());
		email.setRegex("^[\\w-]+(\\.[\\w-]+)*@(?:[\\w-]+\\.)+[a-zA-Z]{2,7}$");
		email.getMessages().setRegexText(appMessages.invalidEmailAddress());
		email.setValidator(new Validator() {
			@Override
			public String validate(Field<?> field, final String value) {
				GWT.log("validate:"+value+" checkedEmail="+checkedEmail+" status="+status.getSelectedIndex());
				if (value != null) {
					if (!value.equals(checkedEmail)) {
						// make sure we don't make 101 RP calls unless the value has changed!
						if (status.getSelectedIndex() == User.ACTIVE) {
							// only check email if user is active
							Scheduler.get().scheduleDeferred(new ScheduledCommand() {
								@Override
								public void execute() {
									NewEditUserController controller = (NewEditUserController) NewEditUserView.this.getController();
									controller.isEmailUnique(value);
								}
							});
						}
					} else if (!emailUnique) {
						return "Email address is already in use"; //appMessages.emailIsNotUnique();
					}
				}
				return null;
			}
		});
		createUserPanel.add(email);
		phoneNo = new TextField<String>();
		phoneNo.setFieldLabel(appMessages.phoneNo());
		createUserPanel.add(phoneNo);

		return createUserPanel;
	}
	
	private LayoutContainer createAssignRolePage() {
		FormPanel panel = new FormPanel();
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);
		FieldSet fs = new FieldSet();
		fs.setHeading(appMessages.setRolesForUser());
		ItemAccessListFieldMessages messages = new ItemAccessListFieldMessages("leftHeading="+appMessages.availableRoles()+"\n" +
        		"rightHeading="+appMessages.rolesAssignedToUser()+"\n" +
        		"addOne="+appMessages.addRole()+"\n" +
        		"addAll="+appMessages.addAllRoles()+"\n" +
        		"removeOne="+appMessages.removeRole()+"\n" +
        		"removeAll="+appMessages.removeAllRoles()+"\n" +
        		"search="+appMessages.searchForRole()+"\n" +
        		"loading="+appMessages.loading());
		roleUserAccessListField = new ItemAccessListField<RoleSummary>(messages,controller.getRoleUserAccessController(), 200);
		fs.add(roleUserAccessListField);
		panel.add(fs);
		return panel;
	}
	
	private LayoutContainer createAssignStudyPage() {
		FormPanel panel = new FormPanel();
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);
		FieldSet fs = new FieldSet();
		fs.setHeading(appMessages.setUserAccessToStudy());
		ItemAccessListFieldMessages messages = new ItemAccessListFieldMessages("leftHeading="+appMessages.availableStudies()+"\n" +
        		"rightHeading="+appMessages.studiesAssignedToUser()+"\n" +
        		"addOne="+appMessages.addStudy()+"\n" +
        		"addAll="+appMessages.addAllStudies()+"\n" +
        		"removeOne="+appMessages.removeStudy()+"\n" +
        		"removeAll="+appMessages.removeAllStudies()+"\n" +
        		"search="+appMessages.searchForStudy()+"\n" +
        		"loading="+appMessages.loading());
		studyUserAccessListField = new ItemAccessListField<StudySummary>(messages, controller.getStudyUserAccessController(), 200);
		fs.add(studyUserAccessListField);
		panel.add(fs);
		return panel;
	}
	
	private LayoutContainer createAssignFormPage() {
		FormPanel panel = new FormPanel();
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);
		FieldSet fs = new FieldSet();
		fs.setHeading(appMessages.setUserAccessToForm());
		ItemAccessListFieldMessages messages = new ItemAccessListFieldMessages("leftHeading="+appMessages.availableForms()+"\n" +
        		"rightHeading="+appMessages.formsAssignedToUser()+"\n" +
        		"addOne="+appMessages.addForm()+"\n" +
        		"addAll="+appMessages.addAllForms()+"\n" +
        		"removeOne="+appMessages.removeForm()+"\n" +
        		"removeAll="+appMessages.removeAllForms()+"\n" +
        		"search="+appMessages.searchForForm()+"\n" +
        		"loading="+appMessages.loading());
		formUserAccessListField = new ItemAccessListField<FormSummary>(messages, controller.getFormUserAccessController(), 200);
		fs.add(formUserAccessListField);
		panel.add(fs);
		return panel;
	}

	@Override
	protected void handleEvent(AppEvent event) {
		GWT.log("NewUserView : handleEvent");

		if (event.getType() == NewEditUserController.NEWUSER) {
			showWindow(appMessages.newUser(), 550, 400);
		}
		if (event.getType() == NewEditUserController.EDITUSER) {
			user = event.getData("user");
			showWindow(appMessages.editUser(user.getName()), 550, 400);
			setWizardValues();
			nextButton.setEnabled(true);
		}
	}
	
	public void setUserNameUnique(String userName, Boolean unique) {
		checkedUserName = userName;
		userNameUnique = unique;
		if (unique && this.userName.getValue().equals(userName)) {
			// username is valid
			this.userName.clearInvalid();
		} else {
			// username is not valid
			this.userName.markInvalid(appMessages.usernameIsNotUnique());
		}
	}
	
	public void setEmailUnique(String email, Boolean unique) {
		checkedEmail = email;
		emailUnique = unique;
		if (unique && this.email.getValue().equals(email)) {
			// email is valid
			this.email.clearInvalid();
		} else {
			// email is not valid
			this.email.markInvalid("Email is not unique");
		}
	}
	
	@Override
	protected void back() {
		if (activePage == 1) {
			// should reload the user object to ensure changes to access mappings have been reflected
			controller.getUser(user.getName());
		} else {
			GWT.log("Showing previous page");
			super.back();
		}
	}
	
	@Override
	protected void next() {
		if (activePage == 0) {
			// must save user if on the first page
			getWizardValues();
			save(true, true);
			GWT.log("Saving user="+user.getName());
		} else {
			GWT.log("Showing next page");
			super.next();
		}
	}
	
	public void saved(User user) {
		this.user = user; // get saved copy of user (with id)
		super.next();
	}
	
	public void loaded(User user) {
		this.user = user;
		setWizardValues(); // in case of update by another user
		super.back();
	}

	@Override
	protected void finish() {
		ProgressIndicator.hideProgressBar();
		closeWindow();
	}

	private void save(boolean triggerRefreshEvent, boolean notifyMe) {
		if (user == null) {
			return;
		}
		controller.saveUser(user, triggerRefreshEvent, notifyMe);
	}
   
	@Override
	protected void saveAndExit() {
		ProgressIndicator.showProgressBar();
		getWizardValues();
		save(true, false);
		closeWindow();
	}

	private void getWizardValues() {
		// page one (other components refresh automatically)
		if (user == null) {
			user = new User();
			user.setCreator((User)Registry.get(Emit.LOGGED_IN_USER_NAME));
			user.setDateCreated(new Date());
		}
		user.setName(userName.getValue());
		user.setFirstName(firstName.getValue());
		user.setMiddleName(middleName.getValue());
		user.setLastName(lastName.getValue());
		if (passwordSet.isExpanded()) {
			user.setClearTextPassword(password.getValue());
		}
		user.setEmail(email.getValue());
		user.setPhoneNo(phoneNo.getValue());
		user.setStatus(status.getSelectedIndex());
	}
	
	private void setWizardValues() {
		if (user != null) {
			userName.setValue(user.getName());
			firstName.setValue(user.getFirstName());
			middleName.setValue(user.getMiddleName());
			lastName.setValue(user.getLastName());
			email.setValue(user.getEmail());
			phoneNo.setValue(user.getPhoneNo());
			controller.setUserForAccessMapping(user);
			status.setValue(status.getStore().getAt(user.getStatus()));
			controller.setUserForAccessMapping(user);
		}
	}
}
