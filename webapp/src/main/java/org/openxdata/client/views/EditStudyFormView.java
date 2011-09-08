package org.openxdata.client.views;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openxdata.client.Emit;
import org.openxdata.client.controllers.EditStudyFormController;
import org.openxdata.client.controllers.FormDesignerController;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.state.EditableState;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
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
public class EditStudyFormView extends WizardView {

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
		userAccessToStudy = new UserAccessListField(UserAccessListField.Category.STUDY, controller);
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
		userAccessToForm = new UserAccessListField(UserAccessListField.Category.FORM, controller);
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

			// Set the values of the form to that of the selected Form
			// page 1
			studyName.setValue(study.getName());
			studyDescription.setValue(study.getDescription());
			userAccessToStudy.setStudy(formDefVersion.getFormDef().getStudy());
			userAccessToStudy.unmask();
			
			// page 2
			formName.setValue(form.getName());
			formDescription.setValue(form.getDescription());
			userAccessToForm.setForm(formDefVersion.getFormDef());
			userAccessToForm.unmask();
			
			// page 3
			formVersion.setValue(formDefVersion.getName());
			formVersionDescription.setValue(formDefVersion.getDescription());
			published.setValue(formDefVersion.getIsDefault());

			designFormButton1.setText(appMessages.designForm() + " (" + form.getName() + " " + formDefVersion.getName() + ")");
			designFormButton2.setText(appMessages.designForm() + " (" + formDefVersion.getName() + ")");
		}
		showWindow(appMessages.editStudyOrForm(), 555, 400);
	}

	private void save(final boolean triggerRefreshEvent) {

		if (formDefVersion == null) {
			return;
		}
		
		if (formDefVersion.getState() == EditableState.HASDATA) {
			MessageBox.info(appMessages.existingDataTitle(), appMessages.cannotSave(), null);
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
		formDefVersion.setIsDefault(published.getValue());
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				controller.saveForm(form, triggerRefreshEvent);
			}
		});
	}

	@Override
	public void saveAndExit() {
		ProgressIndicator.showProgressBar();
		try {
			save(true);
		} finally {
			ProgressIndicator.hideProgressBar();
		}
	}

	public void onFormDataCheckComplete(Boolean hasData) {
		if (hasData) {
			closeWindow();
			new StudyFormHasDataChoiceView().show();
		} else {
			closeWindow();
			launchDesigner(false);
		}
	}
	
	public FormDefVersion createNewVersionForFormWithData() {
		FormDef form = formDefVersion.getFormDef();
		FormDefVersion version = new FormDefVersion();
		form.addVersion(version);
		version.setFormDef(form);
		version.setIsDefault(false);
		version.setCreator((User) Registry.get(Emit.LOGGED_IN_USER_NAME));
		version.setDateCreated(new Date());
		version.setFormDef(formDefVersion.getFormDef());
		version.setLayout(formDefVersion.getLayout());
		version.setXform(formDefVersion.getXform());
		formVersion.setValue(form.getNextVersionName()); // this value is used in the save
		version.setName(formVersion.getValue());
		version.setDescription(formDefVersion.getDescription());
		formDefVersion = version; // replace selected version with the new one so future saves will work
		return version;
	}
	
	public void launchDesigner(FormDefVersion formDefVersion, boolean readOnly) {
		this.formDefVersion = formDefVersion;
        launchDesigner(readOnly);
	}
	
	public void launchDesigner(boolean readOnly) {
		AppEvent event = new AppEvent((readOnly ? FormDesignerController.READONLY_FORM : FormDesignerController.EDIT_FORM));
        event.setData("formDefVersion", formDefVersion);
        Dispatcher.get().dispatch(event);
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