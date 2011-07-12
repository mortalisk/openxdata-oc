package org.openxdata.client.views;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.client.controllers.EditStudyFormController;
import org.openxdata.client.model.UserSummary;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.client.util.UsermapUtilities;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersionText;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.admin.model.mapping.UserStudyMap;
import org.openxdata.server.admin.model.state.EditableState;
import org.purc.purcforms.client.controller.IFormSaveListener;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;

/**
 * Encapsulates UI functionality for Editing a given Study/Form/Form version..
 * 
 */
public class EditStudyFormView extends WizardView implements IFormSaveListener {

	private FormDef form;

	private CheckBox published;
	private final TextField<String> studyName = new TextField<String>();
	private final TextField<String> studyDescription = new TextField<String>();

	private final TextField<String> formName = new TextField<String>();
	private final TextField<String> formDescription = new TextField<String>();

	private final TextField<String> formVersion = new TextField<String>();
	private final TextField<String> formVersionDescription = new TextField<String>();
	private UserAccessListField userAccessToStudy;
	private UserAccessListField userAccessToForm;
	private List<User> users;
	private final EditStudyFormController studyFormController;
	private UsermapUtilities utils;

	public EditStudyFormView(EditStudyFormController controller) {
		super(controller);
		this.studyFormController = controller;
		utils = new UsermapUtilities(studyFormController);
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
		if (activePage == 1) {
			userAccessToStudy.setExpanded(false);
			userAccessToForm.setExpanded(false);
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
		userAccessToStudy = new UserAccessListField(
				appMessages.usersWithAccessToStudy());
		formPanel.add(userAccessToStudy);
		formPanel.setButtonAlign(HorizontalAlignment.LEFT);
		editStudyPanel.add(formPanel);

		return editStudyPanel;
	}

	private LayoutContainer createEditFormPage() {
		final LayoutContainer editFormPanel = new LayoutContainer();
		editFormPanel.setLayout(new FitLayout());
		editFormPanel.setStyleAttribute("padding", "10px");
		FormPanel formPanel = getWizardFormPanel();
		formName.setFieldLabel(appMessages.formName());
		formName.setAllowBlank(false);
		formPanel.add(formName);

		formDescription.setFieldLabel(appMessages.formDescription());
		formPanel.add(formDescription);
		userAccessToForm = new UserAccessListField(
				appMessages.usersWithAccessToForm());
		formPanel.add(userAccessToForm);
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
		formVersion.setEnabled(false);
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

			studyFormController.getUsers();
			studyFormController.getUserMappedStudies();
			studyFormController.getUserMappedForms();
			ProgressIndicator.hideProgressBar();
			// Set the values of the form to that of the selected Form
			studyName.setValue(form.getStudy().getName());
			studyDescription.setValue(form.getStudy().getDescription());

			formName.setValue(form.getName());
			formDescription.setValue(form.getDescription());

			formVersion.setValue(form.getDefaultVersion().getName());
			formVersionDescription.setValue(form.getDefaultVersion()
					.getDescription());

			published.setEnabled(form.getDefaultVersion().getIsDefault());
		}
		showWindow(appMessages.editStudyOrForm(), 550, 400);
	}

	private void launchDesigner(boolean readOnly) {

		FormDesignerView editFormFormDesignerView = new FormDesignerView(this);
		editFormFormDesignerView.openFormForEditing(form.getDefaultVersion(), readOnly);
	}

	private void save() {

		if (form == null) {
			return;
		}
		
		if (form.getState() == EditableState.HASDATA) {
			MessageBox.info(appMessages.existingDataTitle(), appMessages.cannotSave(), new Listener<MessageBoxEvent>() {
				@Override
				public void handleEvent(MessageBoxEvent be) {
				}
			});
			return;
		}
		// update study/form/version information
		form.getStudy().setName(studyName.getValue());
		form.getStudy().setDescription(studyDescription.getValue());
		form.setName(formName.getValue());
		form.setDescription(formDescription.getValue());
		form.getDefaultVersion().setName(formVersion.getValue());
		form.getDefaultVersion().setDescription(
				formVersionDescription.getValue());
		studyFormController.saveForm(form);
	}

	@Override
	public void saveAndExit() {
		ProgressIndicator.showProgressBar();
		save();
		// save any mapped study or form
		utils.saveUserStudyMap(userAccessToStudy, form.getStudy(), users);
		utils.saveUserFormMap(userAccessToForm, form, users,
				utils.getUserMappedForms());
		ProgressIndicator.hideProgressBar();
	}

	@Override
	public boolean onSaveForm(int formId, String xformsXml, String layoutXml,
			String javaScriptSrc) {
		try {
			if (form.getDefaultVersion() == null) {
				MessageBox.alert(appMessages.error(),
						appMessages.removeFormIdAttribute(), null);
				
				return false;
			}

			form.getDefaultVersion().setXform(xformsXml);
			form.getDefaultVersion().setLayout(layoutXml);
			form.getDefaultVersion().setDirty(true);

			return true;
			// We shall use the onSaveLocaleText() such that we avoid double saving
		} catch (Exception ex) {
			MessageBox.alert(appMessages.error(), appMessages.pleaseTryAgainLater(ex.getMessage()), null);
			return false;
		}
	}

	@Override
	public void onSaveLocaleText(int formId, String xformsLocaleText,
			String layoutLocaleText) {
		if (form.getDefaultVersion() == null) {
			MessageBox.alert(appMessages.error(), appMessages.selectFormVersion(),
					null);
			return;
		}

		FormDefVersionText formDefVersionText = form.getDefaultVersion()
				.getFormDefVersionText("en");
		if (formDefVersionText == null) {
			formDefVersionText = new FormDefVersionText("en",
					xformsLocaleText, layoutLocaleText);
			form.getDefaultVersion().addVersionText(formDefVersionText);
		} else {
			formDefVersionText.setXformText(xformsLocaleText);
			formDefVersionText.setLayoutText(layoutLocaleText);
		}
		form.getDefaultVersion().setDirty(true);
		save();
	}

	public void setUsers(List<User> users) {
		this.users = users;
		List<UserSummary> unMappedUsers = new ArrayList<UserSummary>();
		for (User user : users) {
			userAccessToStudy.addUnmappedUser(new UserSummary(user));
			unMappedUsers.add(new UserSummary(user));
			userAccessToForm.addUnmappedUser(new UserSummary(user));
		}
		userAccessToStudy.updateLists(unMappedUsers,
				new ArrayList<UserSummary>());
		userAccessToForm.updateLists(unMappedUsers,
				new ArrayList<UserSummary>());
	}

	public void setUserMappedStudies(List<UserStudyMap> amappedStudies) {
		utils.setUserMappedStudies(amappedStudies);
	}

	public void setUserMappedForms(List<UserFormMap> amappedForms) {
		utils.setUserMappedForms(amappedForms);
	}

	public void onFormDataCheckComplete(Boolean hasData) {
		if (hasData) {
			MessageBox.confirm(appMessages.existingDataTitle(), appMessages.existingDataMessage(), new Listener<MessageBoxEvent>() {
				
				@Override
				public void handleEvent(MessageBoxEvent be) {
					if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
						form.setState(EditableState.HASDATA);
						launchDesigner(true);
					}
				}
			});
		} else {
			launchDesigner(false);
		}
	}

	public Button getDesignFormButton(String label) {
		Button designFormButton = new Button(label);
		designFormButton.addListener(Events.Select,
				new Listener<ButtonEvent>() {

					@Override
					public void handleEvent(ButtonEvent be) {
						studyFormController.formHasData(form);
					}
				});
		return designFormButton;
	}
}