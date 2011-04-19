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
package org.openxdata.client.views;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.client.Emit;
import org.openxdata.client.controllers.EditStudyFormController;
import org.openxdata.client.util.ProgressIndicator;
import org.purc.purcforms.client.FormDesignerWidget;
import org.purc.purcforms.client.controller.IFormSaveListener;
import org.purc.purcforms.client.util.LanguageUtil;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersionText;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.admin.model.mapping.UserStudyMap;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.xml.client.XMLParser;
import org.openxdata.client.model.UserSummary;

/**
 * Encapsulates UI functionality for Editing a given Study/Form/Form version..
 * 
 * @author Angel
 * 
 */
public class EditStudyFormView extends WizardView implements IFormSaveListener {

	private FormDef form;
	/** The form designer widget. */
	private FormDesignerWidget formDesigner;

	Window formDesignerWindow = new Window();

	private CheckBox published;
	private final TextField<String> studyName = new TextField<String>();
	private final TextField<String> studyDescription = new TextField<String>();

	private final TextField<String> formNameTfld = new TextField<String>();
	private final TextField<String> formDescription = new TextField<String>();

	private final TextField<String> formVersion = new TextField<String>();
	private final TextField<String> formVersionDescription = new TextField<String>();
	private UserAccessGrids userAccessToStudy;
	private UserAccessGrids userAccessToForm;
	private List<User> users;
	private List<UserStudyMap> mappedStudies;
	private List<UserFormMap> mappedForms;
        private int currentPage = 0;

	/**
	 * @param controller
	 */
	public EditStudyFormView(Controller controller) {
		super(controller);
	}

	@Override
	protected void createButtons() {
		super.createButtons();
	}

	@Override
	protected List<LayoutContainer> createPages() {
		List<LayoutContainer> wizardPages = new ArrayList<LayoutContainer>();
		wizardPages.add(createEditStudyPage());
		wizardPages.add(createEditFormPage());
		wizardPages.add(createEditVersionPage());
		return wizardPages;
	}

	@Override
	protected void display(int activePage, List<LayoutContainer> pages) {
		 //resize window if the previous window was expanded
            if (activePage == 0 && currentPage != 0) {
                resizeWindow(-200, getWizardWidth());
            }
            currentPage = activePage;
            if (activePage == 1){
                userAccessToStudy.setExpanded(false);
                userAccessToForm.setExpanded(false);
                resizeWindow(0,getWizardWidth());
            }
	}

	@Override
	protected void finish() {
		saveAndExit();
	}

	private LayoutContainer createEditStudyPage() {
		final LayoutContainer editStudyPanel = new LayoutContainer();

		editStudyPanel.setLayout(new FitLayout());
		editStudyPanel.setStyleAttribute("padding", "10px");
		FormPanel formPanel = getWizardFormPanel();
		studyName.setFieldLabel(appMessages.studyName());
		studyName.setAllowBlank(false);
		formPanel.add(studyName);

		studyDescription.setFieldLabel(appMessages.studyDescription());
		formPanel.add(studyDescription);
		userAccessToStudy = new UserAccessGrids(appMessages.usersWithAccessToStudy());
		formPanel.add(userAccessToStudy);
		userAccessToStudy.addListener(Events.Expand,
				new Listener<ComponentEvent>() {

					@Override
					public void handleEvent(ComponentEvent be) {
                                            studyName.hide();
                                            studyDescription.hide();
                                            setUserStudyMap(form.getStudy(), users);
                                            resizeWindow(200,userAccessToStudy.getWidth()+40);
                                            userAccessToStudy.refreshToolbars();
					}
				});
		userAccessToStudy.addListener(Events.BeforeCollapse,
				new Listener<ComponentEvent>() {

					@Override
					public void handleEvent(ComponentEvent be) {
						// be sure to check that it has been expanded
						// to avoid resizing the initial window
						if (userAccessToStudy.isExpanded()){
                                                    studyName.show();
                                                    studyDescription.show();
                                                    resizeWindow((-200),getWizardWidth());
                                                    userAccessToStudy.refreshToolbars();
                                                }
					}
				});
		formPanel.setButtonAlign(HorizontalAlignment.LEFT);
		editStudyPanel.add(formPanel);

		return editStudyPanel;
	}

	private LayoutContainer createEditFormPage() {
		final LayoutContainer editFormPanel = new LayoutContainer();
		editFormPanel.setLayout(new FitLayout());
		editFormPanel.setStyleAttribute("padding", "10px");
		FormPanel formPanel = getWizardFormPanel();
		formNameTfld.setFieldLabel(appMessages.formName());
		formNameTfld.setAllowBlank(false);
		formPanel.add(formNameTfld);

		formDescription.setFieldLabel(appMessages.formDescription());
		formPanel.add(formDescription);
		userAccessToForm = new UserAccessGrids(appMessages.usersWithAccessToForm());
		formPanel.add(userAccessToForm);
		userAccessToForm.addListener(Events.Expand,
				new Listener<ComponentEvent>() {

					@Override
					public void handleEvent(ComponentEvent be) {
                                            setUserFormMap(form, users);
                                            resizeWindow(200,userAccessToForm.getWidth()+40);
                                            userAccessToForm.refreshToolbars();
					}
				});
		userAccessToForm.addListener(Events.BeforeCollapse,
				new Listener<ComponentEvent>() {

					@Override
					public void handleEvent(ComponentEvent be) {
						// be sure to check that it has been expanded
						// to avoid resizing the initial window
						if (userAccessToForm.isExpanded()){
                                                    resizeWindow((-200),getWizardWidth());
                                                    userAccessToForm.refreshToolbars();
                                                }
					}
				});
		formPanel.setButtonAlign(HorizontalAlignment.LEFT);
		formPanel.add(getDesignFormButton(appMessages.designForm()));
		editFormPanel.add(formPanel);

		return editFormPanel;
	}

	private LayoutContainer createEditVersionPage() {
		final LayoutContainer editVersionPage = new LayoutContainer();
		editVersionPage.setLayout(new FitLayout());
		editVersionPage.setStyleAttribute("padding", "10px");
		FormPanel formPanel = getWizardFormPanel();
		formVersion.setFieldLabel(appMessages.formVersion());
		formVersion.setAllowBlank(false);
		formPanel.add(formVersion);

		formVersionDescription.setFieldLabel(appMessages
				.formVersionDescription());
		formPanel.add(formVersionDescription);

		published = new CheckBox();
		published.setBoxLabel("");
		published.setLabelSeparator("");
		published.setFieldLabel(appMessages.formVersionDefault());
		formPanel.add(published);
                formPanel.add(getDesignFormButton(appMessages.designForm()));
		editVersionPage.add(formPanel);

		return editVersionPage;
	}

	private FormPanel getWizardFormPanel() {
		FormPanel formPanel = new FormPanel();
		formPanel.setFrame(false);
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		formPanel.setHeaderVisible(false);
		FormLayout layout = new FormLayout();
		layout.setLabelWidth(150);
		formPanel.setLayout(layout);
		return formPanel;
	}

	@Override
	protected void handleEvent(AppEvent event) {
		GWT.log("EditStudyFormView : handleEvent");
		if (event.getType() == EditStudyFormController.EDITSTUDYFORM) {

			form = event.getData("formDef");
			GWT.log("EditStudyFormView : EditStudyFormController.EDITSTUDYFORM : Edit");

			EditStudyFormController controll = (EditStudyFormController) EditStudyFormView.this
					.getController();
			controll.getUsers();
			controll.getUserMappedStudies();
			controll.getUserMappedForms();
			ProgressIndicator.hideProgressBar();
			// Set the values of the form to that of the selected Form
			studyName.setValue(form.getStudy().getName());
			studyDescription.setValue(form.getStudy().getDescription());

			formNameTfld.setValue(form.getName());
			formDescription.setValue(form.getDescription());

			formVersion.setValue(form.getDefaultVersion().getName());
			formVersionDescription.setValue(form.getDefaultVersion()
					.getDescription());

			published.setEnabled(form.getDefaultVersion().getIsDefault());
		}
		showWindow(appMessages.editStudyOrForm(), 500, 210);
	}

	private void launchDesigner(boolean readOnly) {
		ProgressIndicator.showProgressBar();

		String formName = form.getName();
		String formVersionName = form.getDefaultVersion().getName();
		String formBinding = "binding";

		// launch purcforms designer
		if (formDesigner == null) {
			formDesigner = new FormDesignerWidget(false, true, true);
			formDesigner.setSplitPos("20%");
			formDesigner.setFormSaveListener(this);
		}

		// get the xforms and layout xml
		String xform = form.getDefaultVersion().getXform();
		String layout = form.getDefaultVersion().getLayout();
		// if not empty load it in the formdesigner for editing
		if (xform != null && xform.trim().length() > 0) {
			// If the form was localised for the current locale, then translate
			// it to the locale.
			FormDefVersionText text = form.getDefaultVersion()
					.getFormDefVersionText("en");
			if (text != null) {

				xform = LanguageUtil.translate(XMLParser.parse(xform),
						XMLParser.parse(text.getXformText())
								.getDocumentElement());

				if (layout != null && layout.trim().length() > 0) {
					layout = LanguageUtil.translate(XMLParser.parse(layout),
							XMLParser.parse(text.getLayoutText())
									.getDocumentElement());
				}
			}
			formDesigner.loadForm(form.getDefaultVersion()
					.getFormDefVersionId(), xform, layout, "", readOnly);
		} else {
			formDesigner
					.addNewForm(formName + "_" + formVersionName, formBinding,
							form.getDefaultVersion().getFormDefVersionId());
		}
		formDesignerWindow = new Window();
		formDesignerWindow.setPlain(true);
		formDesignerWindow.setHeading(appMessages.designForm() + " : "
				+ formName);
		formDesignerWindow.setMaximizable(true);
		formDesignerWindow.setMinimizable(false);
		formDesignerWindow.setDraggable(false);
		formDesignerWindow.setResizable(false);
		formDesignerWindow.setModal(true);
		formDesignerWindow.setSize(
				com.google.gwt.user.client.Window.getClientWidth(),
				com.google.gwt.user.client.Window.getClientHeight());
		formDesignerWindow.add(formDesigner);
		// FIXME: note there are some issues with the purcform widget if you
		// allow the formDesignerWindow to be resized (i.e. more than one open
		// at a time)
		formDesignerWindow.setScrollMode(Scroll.AUTO);
		formDesignerWindow.addListener(Events.BeforeHide,
				editStudyFormWindowListener);
		formDesignerWindow.setModal(true);

		formDesigner.onWindowResized(
				com.google.gwt.user.client.Window.getClientWidth() - 100,
				com.google.gwt.user.client.Window.getClientHeight() - 75);

		formDesignerWindow.show();
		formDesignerWindow.maximize();
	}

	final Listener<ComponentEvent> editStudyFormWindowListener = new WindowListener();

	class WindowListener implements Listener<ComponentEvent> {
		@Override
		public void handleEvent(ComponentEvent be) {
			be.setCancelled(true);
			be.stopEvent();
			MessageBox.confirm(appMessages.cancel(), appMessages.areYouSure(),
					new Listener<MessageBoxEvent>() {
						@Override
						public void handleEvent(MessageBoxEvent be) {
							if (be.getButtonClicked().getItemId()
									.equals(Dialog.YES)) {
								// remove the form in the designer
								formDesignerWindow.removeListener(
										Events.BeforeHide,
										editStudyFormWindowListener);
								formDesignerWindow.hide();
								ProgressIndicator.hideProgressBar();
								formDesignerWindow.addListener(
										Events.BeforeHide,
										editStudyFormWindowListener);
								// clear the formdesigner from any pending
								// previous form
								// this is a hack to prevent the context from
								// referencing a form
								// even after closing the window,results are
								// context sees a new loaded
								// form with properties of a previously closed
								// form.
								org.purc.purcforms.client.Context
										.setFormDef(null);
							}
						}
					});
		}
	};

	private void save() {

		if (form == null) {
			return;
		}
		// update study/form/version information
		form.getStudy().setName(studyName.getValue());
		form.getStudy().setDescription(studyDescription.getValue());
		form.setName(formNameTfld.getValue());
		form.setDescription(formDescription.getValue());      
		form.getDefaultVersion().setName(formVersion.getValue());
		form.getDefaultVersion().setDescription(formVersionDescription.getValue());
		EditStudyFormController controller2 = (EditStudyFormController) EditStudyFormView.this
				.getController();
		controller2.saveForm(form);
	}

	@Override
	public void saveAndExit() {
		ProgressIndicator.showProgressBar();
		save();
		// save any mapped study or form
		saveUserStudyMap();
		saveUserFormMap();
	}

	@Override
	public boolean onSaveForm(int formId, String xformsXml, String layoutXml,
			String javaScriptSrc) {
		// TODO Auto-generated method stub
		try {
			if (form.getDefaultVersion() == null) {
				MessageBox.alert("Error",
						"Please remove the formId attribute from the xform",
						null);
				return false;
			}

			form.getDefaultVersion().setXform(xformsXml);
			form.getDefaultVersion().setLayout(layoutXml);
			form.getDefaultVersion().setDirty(true);

			return true;
			// We shall use the onSaveLocaleText() such that we avoid double
			// saving
		} catch (Exception ex) {
			//
		}

		return false;
	}

	@Override
	public void onSaveLocaleText(int formId, String xformsLocaleText,
			String layoutLocaleText) {
		// TODO Auto-generated method stub
		try {
			if (form.getDefaultVersion() == null) {
				// TODO add message for internationalization purposes
				MessageBox.alert("Error",
						"Please select the form version first", null);
				return;
			}

			FormDefVersionText formDefVersionText = form.getDefaultVersion()
					.getFormDefVersionText("en");
			if (formDefVersionText == null) {
				formDefVersionText = new FormDefVersionText("en", form
						.getDefaultVersion().getFormDefVersionId(),
						xformsLocaleText, layoutLocaleText);
				form.getDefaultVersion().addVersionText(formDefVersionText);
			} else {
				formDefVersionText.setXformText(xformsLocaleText);
				formDefVersionText.setLayoutText(layoutLocaleText);
			}
			form.getDefaultVersion().setDirty(true);
			save();
		} catch (Exception ex) {
			//
		}
	}

	public void setUsers(List<User> users) {
		this.users = users;
                List<UserSummary> unMappedUsers = new ArrayList<UserSummary>();
		for (User user : users) {
                        userAccessToStudy.addUnmappedUser(new UserSummary(user));
                        unMappedUsers.add(new UserSummary(user));
                        userAccessToForm.addUnmappedUser(new UserSummary(user));
		}
                userAccessToStudy.updateLists(unMappedUsers, new ArrayList<UserSummary>());
                userAccessToForm.updateLists(unMappedUsers, new ArrayList<UserSummary>());
	}

	public void setUserMappedStudies(List<UserStudyMap> amappedStudies) {
		this.mappedStudies = amappedStudies;
	}

	public void setUserMappedForms(List<UserFormMap> amappedForms) {
		this.mappedForms = amappedForms;
	}

	public void onFormDataCheckComplete(Boolean hasData) {
		if (hasData) {
			launchDesigner(true);
		} else {
			launchDesigner(false);
		}
	}

	/*
	 * Load study names into left and right listboxes appropriately
	 */
	private void setUserStudyMap(StudyDef study, List<User> users) {
                userAccessToStudy.clear();

                List<UserSummary> mappedUsers = new ArrayList<UserSummary>();
                List<UserSummary> unMappedUsers = new ArrayList<UserSummary>();
		for (User u : users) {
			// check whether user is mapped to this study
			boolean found = false;
			for (UserStudyMap map : mappedStudies) {
				if ((map.getUserId() == u.getId())
						&& (map.getStudyId() == study.getStudyId())) {
					userAccessToStudy.addMappedUser(new UserSummary(u));
                                        mappedUsers.add(new UserSummary(u));
					found = true;
					break;
				}
			}
			if (!found) {
				userAccessToStudy.addUnmappedUser(new UserSummary(u));
                                unMappedUsers.add(new UserSummary(u));
			}
		}
                userAccessToStudy.updateLists(unMappedUsers, mappedUsers);
	}

	/*
	 * Load formdefinition names into left and right listboxes appropriately
	 */
	private void setUserFormMap(FormDef form, List<User> users) {
                userAccessToForm.clear();

                List<UserSummary> mappedUsers = new ArrayList<UserSummary>();
                List<UserSummary> unMappedUsers = new ArrayList<UserSummary>();
		for (User user : users) {
			boolean found = false;
			for (UserFormMap map : mappedForms) {
				if ((map.getUserId() == user.getId())
						&& (map.getFormId() == form.getFormId())) {
					userAccessToForm.addMappedUser(new UserSummary(user));
                                        mappedUsers.add(new UserSummary(user));
					found = true;
					break;
				}
			}
			if (!found) {
				userAccessToForm.addUnmappedUser(new UserSummary(user));
                                unMappedUsers.add(new UserSummary(user));
			}
		}
                userAccessToForm.updateLists(unMappedUsers, mappedUsers);
	}

	public void saveUserStudyMap() {
		if (!userAccessToStudy.getTempMappedItems().isEmpty()) {
			for (int i = 0; i < userAccessToStudy.getTempMappedItems().size(); ++i) {
				for (User user : users) {
					if (user.getName().equals(
							userAccessToStudy.getTempMappedItems().get(i).getName())
							&& !(user.getName().equals(((User) Registry
									.get(Emit.LOGGED_IN_USER_NAME)).getName()))) {
						// check already mapped users to this study
						UserStudyMap map = new UserStudyMap();
						map.addStudy(form.getStudy());
						map.addUser(user);
						map.setDirty(true);
						((EditStudyFormController) EditStudyFormView.this
								.getController()).saveUserMappedStudy(map);
						break;
					}
				}
			}
		}
		if (!userAccessToStudy.getTempItemstoUnmap().isEmpty()) {
			for (int i = 0; i < userAccessToStudy.getTempItemstoUnmap().size(); ++i) {
				for (UserStudyMap map : mappedStudies) {
					for (User user : users) {
						if ((user.getName().equals(userAccessToStudy
								.getTempItemstoUnmap().get(i).getName()))
								&& (user.getUserId() == map.getUserId())) {
							((EditStudyFormController) EditStudyFormView.this
									.getController())
									.deleteUserMappedStudy(map);
							break;
						}
					}
				}
			}
		}
	}

	public void saveUserFormMap() {
		if (!userAccessToForm.getTempMappedItems().isEmpty()) {
			for (int i = 0; i < userAccessToForm.getTempMappedItems().size(); ++i) {
				for (User user : users) {
					if (user.getName().equals(
							userAccessToForm.getTempMappedItems().get(i).getName())
							&& !(user.getName().equals(((User) Registry
									.get(Emit.LOGGED_IN_USER_NAME)).getName()))) {
						UserFormMap map = new UserFormMap();
						map.addForm(form);
						map.addUser(user);
						map.setDirty(true);
						((EditStudyFormController) EditStudyFormView.this
								.getController()).saveUserMappedForm(map);
						break;
					}
				}
			}
		}
		if (!userAccessToForm.getTempItemstoUnmap().isEmpty()) {
			for (int i = 0; i < userAccessToForm.getTempItemstoUnmap().size(); ++i) {
				for (UserFormMap map : mappedForms) {
					for (User user : users) {
						if ((user.getName().equals(userAccessToForm
								.getTempItemstoUnmap().get(i).getName()))
								&& (user.getUserId() == map.getUserId())) {
							((EditStudyFormController) EditStudyFormView.this
									.getController()).deleteUserMappedForm(map);
							break;
						}
					}
				}
			}
		}
	}

	public Button getDesignFormButton(String label) {
		Button designFormButton = new Button(label);
		designFormButton.addListener(Events.Select,
				new Listener<ButtonEvent>() {

					@Override
					public void handleEvent(ButtonEvent be) {
						// launchDesigner();
						((EditStudyFormController) controller)
								.formHasData(form);
					}
				});
		return designFormButton;
	}
}