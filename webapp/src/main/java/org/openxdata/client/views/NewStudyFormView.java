package org.openxdata.client.views;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openxdata.client.Emit;
import org.openxdata.client.controllers.NewStudyFormController;
import org.openxdata.client.model.FormSummary;
import org.openxdata.client.model.StudySummary;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.FormDefVersionText;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;
import org.purc.purcforms.client.controller.IFormSaveListener;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public class NewStudyFormView extends WizardView implements IFormSaveListener {

	// input field for new study page
	private TextField<String> newStudyName;
	private TextField<String> newStudyDescription;
	private ComboBox<StudySummary> existingStudyName;
	private TextField<String> existingStudyDescription;
	private RadioFieldSet createStudyFS;
	private Radio newStudy;
	private Radio existingStdyRdio;
	private UserAccessListField userStudyAccessListField;
	private UserAccessListField userFormAccessListField;
	// input fields for new form page
	private RadioFieldSet createFormFS;
	private TextField<String> newFormName;
	private TextField<String> newFormDescription;
	private ComboBox<FormSummary> existingFormName;
	private TextField<String> existingFormDescription;
	private Radio newForm;
	private Radio existingForm;
	// input fields for form versions
	private TextField<String> formDefinitionVersionName;
	private TextField<String> formDefinitionVersionDescription;
	private CheckBox formVersionDefault;
	// keep track of created study/form
	private StudyDef studyDef;
	private FormDef formDef;
	private FormDefVersion formDefVersion;
	private Map<Integer, String> studyNames;
	private Map<Integer, String> formNames;

	ListStore<StudySummary> store;
	ListStore<FormSummary> formStore;

	private int currentPage = 0;
	private FormDesignerView formDesignerView;
	private boolean formVersionEditMode = false;

	public NewStudyFormView(Controller controller) {
		super(controller);
	}

	@Override
	protected void createButtons() {
		super.createButtons();
		finishButton.setText(appMessages.design());
	}

	@Override
	protected void display(int activePage, List<LayoutContainer> pages) {
		currentPage = activePage;
		if (activePage == 0) {
			userStudyAccessListField.setExpanded(false);
			if (createStudyFS.getSelectedRadio() == null) {
				nextButton.setEnabled(false);
			} else {
				nextButton.setEnabled(true);
			}
		} else if (activePage == 1) {
			if (createFormFS.getSelectedRadio() == null) {
				nextButton.setEnabled(false);
			} else {
				nextButton.setEnabled(true);
			}
			// check what was selected in the page before
			if (createStudyFS.getSelectedRadio().equals(appMessages.addNewStudy())) {
				// remove select existing form from page 2 if the user has selected to create a new study on page 1
				existingFormName.hide();
				existingFormDescription.hide();
				newForm.hide();
				newForm.setValue(true);
				existingForm.hide();
				userFormAccessListField.hide();
			} else if (createStudyFS.getSelectedRadio().equals(appMessages.existingStudy())) {
				// make sure all radio buttons are showing (if they were previous hidden by the code above)
				existingFormName.show();
				existingFormDescription.show();
				newForm.show();
				setStudyForms();
				existingForm.show();
				userFormAccessListField.show();
			}
			userFormAccessListField.setExpanded(false);
		} else if (activePage == 2) {
			if (createFormFS.getSelectedRadio().equals(appMessages.addNewForm())) {
				formDefinitionVersionName.setValue("v1");
			} else  if (createFormFS.getSelectedRadio().equals(appMessages.existingForm())) {
				int versions = formDef.getVersions().size();
				formDefinitionVersionName.setValue("v" + (versions + 1));
				formVersionEditMode = true;
			}
		}
	}

	protected void setStudyForms() {
		formStore.removeAll();
		List<FormDef> studyForms = studyDef.getForms();
		for (FormDef form : studyForms) {
			formStore.add(new FormSummary(String.valueOf(form.getId()), form.getName()));
		}
	}

	@Override
	protected List<LayoutContainer> createPages() {
		List<LayoutContainer> wizardPages = new ArrayList<LayoutContainer>();
		wizardPages.add(createNewStudyPage());
		wizardPages.add(createNewFormPage());
		wizardPages.add(createNewFormVersionPage());
		return wizardPages;
	}

	private LayoutContainer createNewStudyPage() {
		final LayoutContainer createStudyPanel = new LayoutContainer();
		createStudyPanel.setLayout(new FitLayout());
		createStudyPanel.setStyleAttribute("padding", "10px");

		createStudyFS = new RadioFieldSet(300);
		createStudyPanel.add(createStudyFS);

		newStudyName = new TextField<String>();
		newStudyName.setFieldLabel(appMessages.studyName());
		newStudyName.setAllowBlank(false);
		newStudyName.setValidator(new Validator() {

			@Override
			public String validate(Field<?> field, String value) {
				if (value != null) {
					// check that new study is unique
					if (checkStudyExistance(value, studyNames)) {
						return appMessages.studyNameUnique();
					}
					nextButton.setEnabled(true);
				}
				return null;
			}
		});
		newStudyDescription = new TextField<String>();
		newStudyDescription.setFieldLabel(appMessages.studyDescription());
		newStudy = createStudyFS.addRadio("study", appMessages.addNewStudy(),
				newStudyName, newStudyDescription);
		newStudy.addListener(Events.OnClick, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				nextButton.setEnabled(false);
				userStudyAccessListField.setEnabled(false); // can't map a new study
				if (existingStudyName.getValue() != null) {
					existingStudyName.clearSelections();
					existingStudyDescription.setValue("");
				}
			}
		});

		store = new ListStore<StudySummary>();
		existingStudyName = new ComboBox<StudySummary>();
		existingStudyName.setEnabled(false);
		existingStudyName.setFieldLabel(appMessages.studyName());
		existingStudyName.setDisplayField("study");
		existingStudyName.setTriggerAction(TriggerAction.ALL);
		existingStudyName.setStore(store);
		existingStudyName.setAllowBlank(false);
		existingStudyName.addSelectionChangedListener(new SelectionChangedListener<StudySummary>() {
					@Override
					public void selectionChanged(SelectionChangedEvent<StudySummary> se) {
						ProgressIndicator.showProgressBar();
						Integer studyId = new Integer(se.getSelectedItem().getId());
						nextButton.setEnabled(false);
						userStudyAccessListField.mask();
						studyDef = null; formDef = null;
						existingFormName.clearSelections();
						NewStudyFormController controller = (NewStudyFormController) NewStudyFormView.this.getController();
						controller.getStudyDef(studyId);
						controller.getForms(studyId);
					}
				});
		existingStudyDescription = new TextField<String>();
		existingStudyDescription.setFieldLabel(appMessages.studyDescription());
		existingStdyRdio = createStudyFS.addRadio("study",
				appMessages.existingStudy(), existingStudyName,
				existingStudyDescription);
		existingStdyRdio.addListener(Events.OnClick, new Listener<FieldEvent>() {
					@Override
					public void handleEvent(FieldEvent be) {
						nextButton.setEnabled(false);
						userStudyAccessListField.setEnabled(true);
						newStudyName.setValue("");
						newStudyDescription.setValue("");
					}
				});
		userStudyAccessListField = new UserAccessListField(UserAccessListField.Category.STUDY, (NewStudyFormController)controller);
		userStudyAccessListField.setEnabled(false);
		createStudyFS.add(userStudyAccessListField);

		return createStudyPanel;
	}

	private LayoutContainer createNewFormPage() {
		LayoutContainer createFormPanel = new LayoutContainer();
		createFormPanel.setLayout(new FitLayout());
		createFormPanel.setStyleAttribute("padding", "10px");

		createFormFS = new RadioFieldSet(300);
		createFormPanel.add(createFormFS);

		newFormName = new TextField<String>();
		newFormName.setFieldLabel(appMessages.formName());
		newFormName.setAllowBlank(false);
		newFormName.setValidator(new Validator() {
			@Override
			public String validate(Field<?> field, String value) {
				if (value != null) {
					if (createStudyFS.getSelectedRadio().equals(appMessages.existingStudy())) {
						// check that new form is unique within the selected study
						if (checkFormExistance(value, formNames)) {
							return appMessages.formNameUnique();
						}
					}
					nextButton.setEnabled(true);
				}
				return null;
			}
		});
		newFormDescription = new TextField<String>();
		newFormDescription.setFieldLabel(appMessages.formDescription());
		newFormDescription.setName("newFormDescription");
		newForm = createFormFS.addRadio("form", appMessages.addNewForm(),
				newFormName, newFormDescription);
		newForm.addListener(Events.OnClick, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				nextButton.setEnabled(false);
				userFormAccessListField.setEnabled(false);
				if (existingFormName.getValue() != null) {
					existingFormName.clearSelections();
					existingFormDescription.setValue("");
				}
			}
		});

		formStore = new ListStore<FormSummary>();
		existingFormName = new ComboBox<FormSummary>();
		existingFormName.setEnabled(false);
		existingFormName.setFieldLabel(appMessages.formName());
		existingFormName.setDisplayField("form");
		existingFormName.setTriggerAction(TriggerAction.ALL);
		existingFormName.setStore(formStore);
		existingFormName.setAllowBlank(false);
		existingFormName.addSelectionChangedListener(new SelectionChangedListener<FormSummary>() {
					@Override
					public void selectionChanged(SelectionChangedEvent<FormSummary> se) {
						ProgressIndicator.showProgressBar();
						Integer formId = new Integer(se.getSelectedItem().getId());
						nextButton.setEnabled(false);
						userFormAccessListField.mask();
						formDef = null;
						NewStudyFormController controller = (NewStudyFormController) NewStudyFormView.this.getController();
						controller.getFormDef(formId);
					}
				});
		existingFormDescription = new TextField<String>();
		existingFormDescription.setFieldLabel(appMessages.formDescription());
		existingForm = createFormFS.addRadio("form",
				appMessages.existingForm(), existingFormName,
				existingFormDescription);
		existingForm.addListener(Events.OnClick, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				nextButton.setEnabled(false);
				userFormAccessListField.setEnabled(true);
				newFormName.setValue("");
				newFormDescription.setValue("");
			}
		});
		userFormAccessListField = new UserAccessListField(UserAccessListField.Category.FORM, (NewStudyFormController)controller);
		userFormAccessListField.setEnabled(false);
		createFormFS.add(userFormAccessListField);

		return createFormPanel;
	}

	private LayoutContainer createNewFormVersionPage() {
		LayoutContainer createFormVersionPanel = new LayoutContainer();
		createFormVersionPanel.setStyleAttribute("padding", "10px");

		FormLayout formLayout = new FormLayout();
		formLayout.setLabelWidth(125);
		formLayout.setLabelSeparator("");
		formLayout.setLabelAlign(LabelAlign.RIGHT);
		createFormVersionPanel.setLayout(formLayout);
		createFormVersionPanel.add(createFormVersionPanel);

		formDefinitionVersionName = new TextField<String>();
		formDefinitionVersionName.setFieldLabel(appMessages.formVersionName());
		formDefinitionVersionName.setName("formVersionName");
		formDefinitionVersionName.setAllowBlank(false);
		formDefinitionVersionName.setEnabled(false);
		createFormVersionPanel.add(formDefinitionVersionName);
		
		formDefinitionVersionDescription = new TextField<String>();
		formDefinitionVersionDescription.setFieldLabel(appMessages.formVersionDescription());
		formDefinitionVersionDescription.setName("formVersionDescription");
		
		createFormVersionPanel.add(formDefinitionVersionDescription);
		formVersionDefault = new CheckBox();
		formVersionDefault.setBoxLabel("");
		formVersionDefault.setFieldLabel(appMessages.formVersionDefault());
		createFormVersionPanel.add(formVersionDefault);

		return createFormVersionPanel;
	}

	@Override
	protected void handleEvent(AppEvent event) {
		GWT.log("NewStudyFormView : handleEvent");
		if (event.getType() == NewStudyFormController.NEWSTUDYFORM) {

			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					ProgressIndicator.showProgressBar();
					NewStudyFormController controller = (NewStudyFormController) NewStudyFormView.this.getController();
					controller.getStudies();
					ProgressIndicator.hideProgressBar();
				}
			});

			showWindow(appMessages.newStudyFormOrVersionHeading(), 540, 500);
		}
	}

	@Override
	protected void finish() {
		getWizardValues();

		formDesignerView = new FormDesignerView(this);

		formDesignerView.openForNewForm(formDefVersion);

		ProgressIndicator.hideProgressBar();
	}

	private void save() {
		if (studyDef == null) {
			return;
		}
		NewStudyFormController controller = (NewStudyFormController) NewStudyFormView.this.getController();
		controller.saveStudy(studyDef);
	}
   
	@Override
	protected void saveAndExit() {
		ProgressIndicator.showProgressBar();
		getWizardValues();
		save();
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
			// We shall use the onSaveLocaleText() such that we avoid double
			// saving
		} catch (Exception ex) {
			MessageBox.alert(appMessages.error(), appMessages.pleaseTryAgainLater(ex.getMessage()), null);
			return false;
		}
	}

	@Override
	public void onSaveLocaleText(int formId, String xformsLocaleText,
			String layoutLocaleText) {
		try {
			if (formDefVersion == null) {
				MessageBox.alert(appMessages.error(), appMessages.selectFormVersion(), null);
				return;
			}

			FormDefVersionText formDefVersionText = formDefVersion
					.getFormDefVersionText("en");
			if (formDefVersionText == null) {
				formDefVersionText = new FormDefVersionText("en", xformsLocaleText,
						layoutLocaleText);
				formDefVersion.addVersionText(formDefVersionText);
			} else {
				formDefVersionText.setXformText(xformsLocaleText);
				formDefVersionText.setLayoutText(layoutLocaleText);
			}
			formDefVersion.setDirty(true);
			save();
			// if this a new form,then save and close
			if (formId == 0) {
                formDesignerView.hide();
				ProgressIndicator.hideProgressBar();
				closeWindow();
			}
		} catch (Exception ex) {
			MessageBox.alert(appMessages.error(), appMessages.pleaseTryAgainLater(ex.getMessage()), null);
		}
	}

	private void getWizardValues() {
		// page one
		if (createStudyFS.getSelectedRadio().equalsIgnoreCase(
				newStudy.getBoxLabel())) {
			studyDef = new StudyDef(0, newStudyName.getValue());
			studyDef.setDescription(newStudyDescription.getValue());
			studyDef.setCreator((User) Registry.get(Emit.LOGGED_IN_USER_NAME));
			studyDef.setDateCreated(new Date());
			studyDef.setDirty(true);
		} else {
			if (studyDef == null) {
				GWT.log("ERROR - studyDef is null!");
				// studyDef = existingStudyName.getValue().getStudyDefinition();
			} else {
				studyDef.setDescription(existingStudyDescription.getValue());
			}
		}
		// page 2
		if (currentPage > 0) {
			if (createFormFS.getSelectedRadio().equalsIgnoreCase(
					newForm.getBoxLabel())) {
				formDef = new FormDef(0, newFormName.getValue(), studyDef);
				formDef.setDescription(newFormDescription.getValue());
				formDef.setCreator((User) Registry
						.get(Emit.LOGGED_IN_USER_NAME));
				formDef.setDateCreated(new Date());
				formDef.setDirty(true);
				formDef.setVersions(new ArrayList<FormDefVersion>());
				studyDef.addForm(formDef);
			} else {
				if (formDef == null) {
					GWT.log("ERROR - formDef is null!");
					//formDef = studyDef.getForm(existingFormName.getValue()
					//	.getFormDefinition().getId());
				} else {
					formDef.setDescription(existingFormDescription.getValue());
				}
			}
		}
		// page 3
		if (currentPage > 1) {
			if(formVersionEditMode){
				formDefVersion = new FormDefVersion(0,"v"+(formDef.getVersions().size()+1),formDef);
                                if(formDef.getDefaultVersion()!= null){
                                    formDefVersion.setXform(formDef.getDefaultVersion().getXform());
                                }
			}
			else{
				formDefVersion = new FormDefVersion(0,
						formDefinitionVersionName.getValue(), formDef);
			}
			
			formDefVersion.setDescription(formDefinitionVersionDescription.getValue());
			formDefVersion.setCreator((User) Registry.get(Emit.LOGGED_IN_USER_NAME));
			formDefVersion.setDateCreated(new Date());
			
			
			if (formVersionDefault.getValue()) {
				formDefVersion.getFormDef().turnOffOtherDefaults(formDefVersion);
			}
			formDefVersion.setDirty(true);
			formDef.addVersion(formDefVersion);
		}

	}

	public void setStudyNames(Map<Integer, String> studyNames) {
		this.studyNames = studyNames;
		for (Integer studyId : studyNames.keySet()) {
			store.add(new StudySummary(studyId.toString(), studyNames.get(studyId)));
		}
	}
	
	public void setStudyDef(StudyDef studyDef) {
		this.studyDef = studyDef;
		existingStudyDescription.setValue(studyDef.getDescription());
		userStudyAccessListField.setStudy(studyDef);
		userStudyAccessListField.refresh();
		userStudyAccessListField.unmask();
		nextButton.setEnabled(true);
		ProgressIndicator.hideProgressBar();
	}
	
	public void setFormDef(FormDef formDef) {
		this.formDef = formDef;
		existingFormDescription.setValue(formDef.getDescription());
		userFormAccessListField.setForm(formDef);
		userFormAccessListField.refresh();
		userFormAccessListField.unmask();
		nextButton.setEnabled(true);
		ProgressIndicator.hideProgressBar();
	}

	public void setFormNames(Map<Integer, String> formNames) {
		this.formNames = formNames;
		for (Integer formId : formNames.keySet()) {
			formStore.add(new FormSummary(formId.toString(), formNames.get(formId)));
		}
	}

	private boolean checkStudyExistance(String name, Map<Integer, String> items) {
		boolean isFound = false;
		for (String x : items.values()) {
			if (x.equalsIgnoreCase(name)) {
				isFound = true;
				break;
			}
		}
		return isFound;
	}

	private boolean checkFormExistance(String name, Map<Integer, String> items) {
		boolean isFound = false;
		for (String x : items.values()) {
			if (x.equalsIgnoreCase(name)) {
				isFound = true;
				break;
			}
		}
		return isFound;
	}
}
