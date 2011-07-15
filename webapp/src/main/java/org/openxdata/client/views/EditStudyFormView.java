package org.openxdata.client.views;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.client.controllers.EditStudyFormController;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.FormDefVersionText;
import org.openxdata.server.admin.model.StudyDef;
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
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

/**
 * Encapsulates UI functionality for Editing a given Study/Form/Form version..
 * 
 */
public class EditStudyFormView extends WizardView implements IFormSaveListener {

	private FormDefVersion formDefVersion;

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
	private List<UserStudyMap> mappedStudies;
	private final EditStudyFormController studyFormController;

	public EditStudyFormView(EditStudyFormController controller) {
		super(controller);
		studyFormController = controller;
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
		formPanel.add(getDesignFormButton(appMessages.designForm()));
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

			formDefVersion = event.getData("formVersion");
			FormDef form = formDefVersion.getFormDef();
			StudyDef study = form.getStudy();
			
			GWT.log("EditStudyFormView : EditStudyFormController.EDITSTUDYFORM : Edit");

			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					ProgressIndicator.showProgressBar();
					try {
						studyFormController.getUsers();
					} finally {
						ProgressIndicator.hideProgressBar();
					}
				}
			});

			// Set the values of the form to that of the selected Form
			studyName.setValue(study.getName());
			studyDescription.setValue(study.getDescription());
			formName.setValue(form.getName());
			formDescription.setValue(form.getDescription());
			formVersion.setValue(formDefVersion.getName());
			formVersionDescription.setValue(formDefVersion.getDescription());
			published.setEnabled(formDefVersion.getIsDefault());
		}
		showWindow(appMessages.editStudyOrForm(), 555, 400);
	}

	private void launchDesigner(boolean readOnly) {

		FormDesignerView editFormFormDesignerView = new FormDesignerView(this);
		editFormFormDesignerView.openFormForEditing(formDefVersion, readOnly);
	}

	private void save() {

		if (formDefVersion == null) {
			return;
		}
		
		if (formDefVersion.getState() == EditableState.HASDATA) {
			MessageBox.info(appMessages.existingDataTitle(), appMessages.cannotSave(), new Listener<MessageBoxEvent>() {
				@Override
				public void handleEvent(MessageBoxEvent be) {
				}
			});
			return;
		}
		// update study/form/version information
		final FormDef form = formDefVersion.getFormDef();
		form.getStudy().setName(studyName.getValue());
		form.getStudy().setDescription(studyDescription.getValue());
		form.setName(formName.getValue());
		form.setDescription(formDescription.getValue());
		formDefVersion.setName(formVersion.getValue());
		formDefVersion.setDescription(formVersionDescription.getValue());
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				studyFormController.saveForm(form);
			}
		});
	}

	@Override
	public void saveAndExit() {
		ProgressIndicator.showProgressBar();
		try {
			save();
			if (userAccessToForm.isDirty()) {
				((EditStudyFormController)controller).saveUserMappedForms(formDefVersion.getFormDef(), userAccessToForm.getMappedUsers());
			}
			if (userAccessToStudy.isDirty()) {
				((EditStudyFormController)controller).saveUserMappedStudies(formDefVersion.getFormDef().getStudy(), userAccessToStudy.getMappedUsers());
			}
		} finally {
			ProgressIndicator.hideProgressBar();
		}
	}

	@Override
	public boolean onSaveForm(int formId, String xformsXml, String layoutXml,
			String javaScriptSrc) {
		try {
			if (formDefVersion == null) {
				MessageBox.alert(appMessages.error(),
						appMessages.removeFormIdAttribute(), null);
				
				return false;
			}

			formDefVersion.setXform(xformsXml);
			formDefVersion.setLayout(layoutXml);
			formDefVersion.setDirty(true);

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
		if (formDefVersion == null) {
			MessageBox.alert(appMessages.error(), appMessages.selectFormVersion(),
					null);
			return;
		}

		FormDefVersionText formDefVersionText = formDefVersion.getFormDefVersionText("en");
		if (formDefVersionText == null) {
			formDefVersionText = new FormDefVersionText("en",
					xformsLocaleText, layoutLocaleText);
			formDefVersion.addVersionText(formDefVersionText);
		} else {
			formDefVersionText.setXformText(xformsLocaleText);
			formDefVersionText.setLayoutText(layoutLocaleText);
		}
		formDefVersion.setDirty(true);
		save();
	}

	public void setUsers(List<User> users) {
		this.users = users;
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				ProgressIndicator.showProgressBar();
				try {
					// note: calling these methods now to ensure that the users are populated before the mapping, 
					// otherwise we might get an intermittent bug if they happen in the wrong order. 
					studyFormController.getUserMappedStudies();
				} finally {
					ProgressIndicator.hideProgressBar();
				}
			}
		});
	}

	public void setUserMappedStudies(List<UserStudyMap> mappedStudies) {
		this.mappedStudies = mappedStudies;
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				ProgressIndicator.showProgressBar();
				try {
					// note: calling these methods now to ensure that the users are populated before the mapping, 
					// otherwise we might get an intermittent bug if they happen in the wrong order. 
					studyFormController.getUserMappedForms();
				} finally {
					ProgressIndicator.hideProgressBar();
				}
			}
		});
		userAccessToStudy.setUserStudyMap(formDefVersion.getFormDef().getStudy(), users, mappedStudies);
	}

	public void setUserMappedForms(List<UserFormMap> mappedForms) {
		userAccessToForm.setUserFormMap(formDefVersion.getFormDef(), users, mappedForms, mappedStudies);
	}

	public void onFormDataCheckComplete(Boolean hasData) {
		if (hasData) {
			MessageBox.confirm(appMessages.existingDataTitle(), appMessages.existingDataMessage(), new Listener<MessageBoxEvent>() {
				
				@Override
				public void handleEvent(MessageBoxEvent be) {
					if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
						formDefVersion.setState(EditableState.HASDATA);
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
						studyFormController.formHasData(formDefVersion);
					}
				});
		return designFormButton;
	}
}