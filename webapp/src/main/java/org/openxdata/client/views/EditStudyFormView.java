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

	private final TextField<String> studyName = new TextField<String>();
	private final TextField<String> studyDescription = new TextField<String>();
	private UserAccessListField userAccessToStudy;
	private Button designFormButton1;

	private final TextField<String> formName = new TextField<String>();
	private final TextField<String> formDescription = new TextField<String>();
	private UserAccessListField userAccessToForm;
	private Button designFormButton2;

	private final TextField<String> formVersion = new TextField<String>();
	private final TextField<String> formVersionDescription = new TextField<String>();
	private CheckBox published;
	private Button designFormButton3;
	
	private List<User> users; // FIXME: users need to be actually paginated and not all loaded in memory
	private List<UserStudyMap> mappedStudies;
	
	FormDesignerView editFormFormDesignerView = new FormDesignerView(this);
	private final EditStudyFormController controller = (EditStudyFormController) this.getController();

	public EditStudyFormView(EditStudyFormController controller) {
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
		// nothing special to do here
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
		studyName.setWidth(300);
		studyName.setAllowBlank(false);
		formPanel.add(studyName);

		studyDescription.setFieldLabel(appMessages.studyDescription());
		studyDescription.setWidth(300);
		formPanel.add(studyDescription);
		userAccessToStudy = new UserAccessListField(appMessages.usersWithAccessToStudy());
		userAccessToStudy.mask();
		userAccessToStudy.setExpanded(false);
		formPanel.add(userAccessToStudy);
		formPanel.setButtonAlign(HorizontalAlignment.LEFT);
		designFormButton1 = getDesignFormButton(appMessages.designForm());
		formPanel.add(designFormButton1);
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
		formName.setWidth(300);
		formPanel.add(formName);

		formDescription.setFieldLabel(appMessages.formDescription());
		formDescription.setWidth(300);
		formPanel.add(formDescription);
		userAccessToForm = new UserAccessListField(appMessages.usersWithAccessToForm());
		userAccessToForm.mask();
		userAccessToForm.setExpanded(false);
		formPanel.add(userAccessToForm);
		formPanel.setButtonAlign(HorizontalAlignment.LEFT);
		designFormButton2 = getDesignFormButton(appMessages.designForm());
		formPanel.add(designFormButton2);
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

		formVersionDescription.setFieldLabel(appMessages.formVersionDescription());
		formPanel.add(formVersionDescription);

		published = new CheckBox();
		published.setBoxLabel("");
		published.setLabelSeparator("");
		published.setFieldLabel(appMessages.formVersionDefault());
		formPanel.add(published);
		designFormButton3 = getDesignFormButton(appMessages.designForm());
		formPanel.add(designFormButton3);
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
			
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					ProgressIndicator.showProgressBar();
					try {
						controller.getUsers();
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

			designFormButton1.setText(appMessages.designForm() + " (" + form.getName() + " " + formDefVersion.getName() + ")");
			designFormButton2.setText(appMessages.designForm() + " (" + formDefVersion.getName() + ")");
		}
		showWindow(appMessages.editStudyOrForm(), 555, 400);
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
				controller.saveForm(form);
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
					controller.getUserMappedStudies(formDefVersion.getFormDef().getStudy().getId());
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
					controller.getUserMappedForms(formDefVersion.getFormDef().getId());
				} finally {
					ProgressIndicator.hideProgressBar();
				}
			}
		});
		userAccessToStudy.setUserStudyMap(formDefVersion.getFormDef().getStudy(), users, mappedStudies);
		userAccessToStudy.unmask();
	}

	public void setUserMappedForms(List<UserFormMap> mappedForms) {
		// FIXME: mappedStudies should be the actually currently mapped users to studies (which could have changed in step 1)
		userAccessToForm.setUserFormMap(formDefVersion.getFormDef(), users, mappedForms, mappedStudies);
		userAccessToForm.unmask();
	}

	public void onFormDataCheckComplete(Boolean hasData) {
		if (hasData) {
			new StudyFormHasDataChoiceView().show();
		} else {
			launchDesigner(false);
		}
	}
	
	public void createNewVersionForFormWithData(){
		
		int versionSize = formDefVersion.getFormDef().getVersions().size();
		FormDefVersion version = new FormDefVersion();
		
		version.setFormDef(formDefVersion.getFormDef());
		version.setLayout(formDefVersion.getLayout());
		version.setXform(formDefVersion.getXform());
		version.setName("v" + (versionSize + 1));
		
		// 
		editFormFormDesignerView.openFormForEditing(version, false);
	}
	
	public void launchDesigner(boolean readOnly) {
		editFormFormDesignerView.openFormForEditing(formDefVersion, readOnly);
	}

	public Button getDesignFormButton(String label) {
		Button designFormButton = new Button(label);
		designFormButton.addListener(Events.Select,
				new Listener<ButtonEvent>() {

					@Override
					public void handleEvent(ButtonEvent be) {
						controller.formHasData(formDefVersion);
					}
				});
		return designFormButton;
	}
}