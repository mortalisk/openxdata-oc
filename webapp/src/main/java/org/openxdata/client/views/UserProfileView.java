package org.openxdata.client.views;

import org.openxdata.client.AppMessages;
import org.openxdata.client.controllers.UserProfileController;
import org.openxdata.server.admin.model.User;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public class UserProfileView extends View {
	AppMessages appMessages = GWT.create(AppMessages.class);
	private FormPanel formPanel;
	private User user;
	final Window window = new Window();

	private Button cancelButton;
	private final TextField<String> username = new TextField<String>();
	private final FieldSet pwFieldSet = new FieldSet();
	private final TextField<String> password = new TextField<String>();
	private final TextField<String> newPassword = new TextField<String>();
	private final TextField<String> confirmPassword = new TextField<String>();
	private final TextField<String> firstName = new TextField<String>();
	private final TextField<String> lastName = new TextField<String>();
	private final TextField<String> phoneNo = new TextField<String>();
	private final TextField<String> email = new TextField<String>();

	public UserProfileView(Controller controller) {
		super(controller);
	}

	@Override
	protected void initialize() {
		GWT.log("UserProfileView : initialize");
		formPanel = new FormPanel();
		formPanel.setFrame(false);
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		formPanel.setHeaderVisible(false);
		FormLayout layout = new FormLayout();
		layout.setLabelWidth(150);
		formPanel.setLayout(layout);

		username.setFieldLabel(appMessages.username());
		username.setAllowBlank(false);
		username.disable();
		formPanel.add(username);

		pwFieldSet.setHeading(appMessages.changeMyPassword());
		pwFieldSet.setCheckboxToggle(true);
		FormLayout pwlayout = new FormLayout();
		pwlayout.setLabelWidth(150);
		pwFieldSet.setLayout(pwlayout);
		pwFieldSet.setExpanded(false);

		password.setFieldLabel(appMessages.oldPassWord());
		password.setPassword(true);
		pwFieldSet.add(password);

		newPassword.setFieldLabel(appMessages.newPassWord());
		newPassword.setPassword(true);
		pwFieldSet.add(newPassword);

		confirmPassword.setFieldLabel(appMessages.confirmPassword());
		confirmPassword.setPassword(true);
		pwFieldSet.add(confirmPassword);
		formPanel.add(pwFieldSet);

		firstName.setFieldLabel(appMessages.firstName());
		firstName.setAllowBlank(false);
		formPanel.add(firstName);

		lastName.setFieldLabel(appMessages.lastName());
		formPanel.add(lastName);

		phoneNo.setFieldLabel(appMessages.phoneNo());
		formPanel.add(phoneNo);

		email.setFieldLabel(appMessages.eMail());
		formPanel.add(email);

		formPanel.setButtonAlign(HorizontalAlignment.CENTER);
		Button save = new Button(appMessages.save());
		save.setType("submit");
		save.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				// Set the values of the User according the that of the form
				user.setName(username.getValue());
				user.setFirstName(firstName.getValue());
				user.setLastName(lastName.getValue());
				user.setPhoneNo(phoneNo.getValue());
				user.setEmail(email.getValue());
				if (pwFieldSet.isExpanded()) {
					GWT.log("user " + user.getName() + " changing password");
					user.setPassword(password.getValue());
					if (newPasswordMatch(newPassword.getValue(),
							confirmPassword.getValue())) {
						user.setClearTextPassword(newPassword.getValue());
						Scheduler.get().scheduleDeferred(
								new ScheduledCommand() {
									@Override
									public void execute() {
										((UserProfileController) controller)
												.checkPasswordSaveUser(user);
									}
								});
					} else {
						displayError(appMessages.passwordNotSame());
						newPassword.reset();
						confirmPassword.reset();
					}
				} else {
					// save without changing password
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							((UserProfileController) controller)
									.saveUserProfile(user);
						}
					});
				}
			}
		});

		cancelButton = new Button(appMessages.cancel());
		cancelButton.addListener(Events.Select, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						cancel();
					}
				});
			}
		});

		formPanel.addButton(save);
		formPanel.addButton(cancelButton);

		window.setModal(true);
	}

	private boolean newPasswordMatch(String password, String confirmPassword) {
		if (((password == null || password.equals("")) && (confirmPassword != null && !confirmPassword
				.equals("")))
				|| ((confirmPassword == null || confirmPassword.equals("")) && (password != null && !password
						.equals("")))) {
			// one is empty and the other not
			return false;
		} else if (password != null && !password.equals(confirmPassword)) {
			// they just don't match
			return false;
		} else {
			// match ok
			return true;
		}
	}

	public void displayError(String errorMessage) {
		MessageBox box = new MessageBox();
		box.setButtons(MessageBox.OK);
		box.setIcon(MessageBox.ERROR);
		box.setTitle(appMessages.resetPassword());
		box.setMessage(errorMessage);
		box.show();
	}

	public void closeWindow() {
		window.hide();
	}

	@Override
	protected void handleEvent(AppEvent event) {
		GWT.log("UserProfileView : handleEvent");
		if (event.getType() == UserProfileController.USERPROFILE) {
			user = event.getData();
			GWT.log("UserProfileView : UserProfileController.USERPROFILE : Edit User");

			// Set the values of the form to that of the User
			username.setValue(user.getName());
			password.setValue("");
			newPassword.setValue("");
			confirmPassword.setValue("");
			firstName.setValue(user.getFirstName());
			lastName.setValue(user.getLastName());
			phoneNo.setValue(user.getPhoneNo());
			email.setValue(user.getEmail());

			window.setAutoHeight(true);
			window.setWidth(425);
			window.setPlain(true);
			window.setHeading(appMessages.userProfile() + " : "
					+ user.getFullName());
			window.add(formPanel);
			window.setDraggable(true);
			window.setResizable(true);
			window.setScrollMode(Scroll.AUTO);
			window.show();
		}
	}

	public void cancel() {
		MessageBox.confirm(appMessages.cancel(), appMessages.areYouSure(),
				new Listener<MessageBoxEvent>() {
					@Override
					public void handleEvent(MessageBoxEvent be) {
						if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
							closeWindow();
						}
					}
				});
	}
}
