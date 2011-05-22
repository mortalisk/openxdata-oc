package org.openxdata.client.views;

import org.openxdata.client.AppMessages;
import org.openxdata.client.Emit;
import org.openxdata.client.controllers.LoginController;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.core.client.GWT;

/**
 * A window with specialized support for enabling a User login after a server
 * session timeout.
 * 
 */
public class ReLoginView extends Dialog {

	AppMessages appMessages = GWT.create(AppMessages.class);

	protected TextField<String> username;
	protected TextField<String> password;
	protected Button login;

	protected Controller controller;

	public ReLoginView(Controller controller) {
		this.controller = controller;
		initialize();
	}

	private void initialize() {
		FormLayout layout = new FormLayout();
		layout.setLabelWidth(90);
		layout.setDefaultWidth(155);
		setLayout(layout);

		setButtonAlign(HorizontalAlignment.LEFT);
		setIcon(IconHelper.createStyle("user"));
		setBodyBorder(true);
		setBodyStyle("padding: 8px;background: none");

		setClosable(false);
		setHeading(appMessages.sessionExpired());
		setModal(true);
		setWidth(300);
		setResizable(false);

		username = new TextField<String>();
		username.setAllowBlank(false);
		username.setFieldLabel(appMessages.username());
		add(username);

		password = new TextField<String>();
		password.setAllowBlank(false);
		password.setPassword(true);
		password.setFieldLabel(appMessages.passWord());
		add(password);

		setFocusWidget(username);

	}
	
	@Override
	protected void createButtons() {
		getButtonBar().add(new FillToolItem());
		login = new Button("Login");
		login.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				if (validate()) {
					if (username.getValue() != null) {
						if (username.getValue().equals(
								equals(Registry.get(Emit.LOGGED_IN_USER_NAME)))) {
							MessageBox.info(appMessages.conflictingLogins(),
									appMessages.anotherLoggedInUser(), null);
							return;
						} else {
							((LoginController) controller).performLogin(
									username.getValue(), password.getValue());
						}
					}
				} else {
					reset();
					MessageBox.alert(appMessages.invalidUsernameOrPassword(),
							appMessages.rightCredentials(), null);
				}
			}
		});

		addButton(login);
	}

	protected void reset() {
		username.reset();
		password.reset();
	}

	protected boolean validate() {
		if (username.getValue() != null && username.getValue().isEmpty())
			return false;
		if (password.getValue() != null && password.getValue().isEmpty())
			return false;

		return true;
	}
}
