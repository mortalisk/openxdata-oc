package org.openxdata.client.views;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openxdata.client.Emit;
import org.openxdata.client.controllers.FormDesignerController;
import org.openxdata.client.controllers.NewStudyFormController;
import org.openxdata.client.model.FormSummary;
import org.openxdata.client.model.StudySummary;
import org.openxdata.client.model.UserSummary;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.FieldSetEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import org.openxdata.client.util.FormDefVersionUtil;

public class NewStudyFormView extends WizardView {

	// input field for new study page
	private TextField<String> newStudyName;
	private TextField<String> newStudyDescription;
	private ComboBox<StudySummary> existingStudyName;
	private TextField<String> existingStudyDescription;
	private RadioFieldSet createStudyFS;
	private Radio newStudy;
	private Radio existingStdyRdio;
	private FieldSet userStudyAccessListFieldSet;
	private ItemAccessListField<UserSummary> userStudyAccessListField;
	private FieldSet userFormAccessListFieldSet;
	private ItemAccessListField<UserSummary> userFormAccessListField;
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
	private CheckBox published;
	// keep track of created study/form
	private StudyDef studyDef;
	private FormDef formDef;
	private FormDefVersion formDefVersion;
	private Map<Integer, String> studyNames;
	private Map<Integer, String> formNames;

	ListStore<StudySummary> store;
	ListStore<FormSummary> formStore;
	
	// used to pre-select items in the drop down lists
	FormDef preselectForm;

	private int currentPage = 0;
	private boolean formVersionEditMode = false;
	
	private final NewStudyFormController controller = (NewStudyFormController) this.getController();

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
			saveAndExitButton.hide();
			if (createStudyFS.getSelectedRadio() == null) {
				nextButton.setEnabled(false);
			} else {
				nextButton.setEnabled(true);
			}
		} else if (activePage == 1) {
			saveAndExitButton.hide();
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
				userFormAccessListFieldSet.hide();
			} else if (createStudyFS.getSelectedRadio().equals(appMessages.existingStudy())) {
				// make sure all radio buttons are showing (if they were previous hidden by the code above)
				existingFormName.show();
				existingFormDescription.show();
				newForm.show();
				setStudyForms();
				existingForm.show();
				userFormAccessListFieldSet.show();
				if (existingFormName.getValue() == null) {
					nextButton.setEnabled(false);
				} else {
					nextButton.setEnabled(true);
				}
			}
		} else if (activePage == 2) {
			saveAndExitButton.show();
			saveAndExitButton.setEnabled(true);
			if (createFormFS.getSelectedRadio().equals(appMessages.addNewForm())) {
				formDefinitionVersionName.setValue("v1");
				published.setValue(true);
				published.setBoxLabel("");
			} else if (createFormFS.getSelectedRadio().equals(appMessages.existingForm())) {
				formDefinitionVersionName.setValue(formDef.getNextVersionName());
				formVersionEditMode = true;
				published.setBoxLabel(appMessages.publishedHelp());
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
		newStudyName.setAutoValidate(true);
		newStudyName.setValidationDelay(1000);
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
				userStudyAccessListFieldSet.setEnabled(false); // can't map a new study
				userStudyAccessListFieldSet.setExpanded(false);
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
						userStudyAccessListFieldSet.setExpanded(false); 
						userStudyAccessListFieldSet.setEnabled(true); 
						userStudyAccessListField.mask();
						studyDef = null; formDef = null;
						existingFormName.clearSelections();
						userFormAccessListFieldSet.setExpanded(false);
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
						newStudyName.setValue("");
						newStudyDescription.setValue("");
						// set the preselected study if applicable or if it there is only one study
						if (preselectForm != null) {
							StudyDef preselectStudy = preselectForm.getStudy();
							setStudyDef(preselectStudy);
							List<StudySummary> sss = store.getModels();
							for (StudySummary ss : sss) {
								if (ss.getId().equals(String.valueOf(preselectStudy.getId()))) {
									existingStudyName.setValue(ss);
									break;
								}
							}
						}else if(store.getModels().size()==1){
                                                    existingStudyName.setValue(store.getModels().get(0));
                                                }
					}
				});
		ItemAccessListFieldMessages messages = new ItemAccessListFieldMessages("leftHeading="+appMessages.availableUsers()+"\n" +
        		"rightHeading="+appMessages.usersWithAccessToStudy()+"\n" +
        		"addOne="+appMessages.addUser()+"\n" +
        		"addAll="+appMessages.addAllUsers()+"\n" +
        		"removeOne="+appMessages.removeUser()+"\n" +
        		"removeAll="+appMessages.removeAllUsers()+"\n" +
        		"search="+appMessages.searchForAUser()+"\n" +
        		"loading="+appMessages.loading());
		userStudyAccessListField = new ItemAccessListField<UserSummary>(messages, controller.getUserStudyAccessController());
		userStudyAccessListFieldSet = new FieldSet();
		userStudyAccessListFieldSet.setHeading(appMessages.setUserAccessToStudy());
		userStudyAccessListFieldSet.setCollapsible(true);
		userStudyAccessListFieldSet.setExpanded(false);
		userStudyAccessListFieldSet.add(userStudyAccessListField);
		userStudyAccessListFieldSet.addListener(Events.Expand, new Listener<FieldSetEvent>() {
			public void handleEvent(FieldSetEvent be) {
				userStudyAccessListField.refresh();
				userStudyAccessListField.unmask();
			}
		});
		userStudyAccessListFieldSet.setEnabled(false);
		createStudyFS.add(userStudyAccessListFieldSet);

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
		newFormName.setAutoValidate(true);
		newFormName.setValidationDelay(1000);
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
                                        saveAndExitButton.setEnabled(true);
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
                                saveAndExitButton.setEnabled(false);
				userFormAccessListFieldSet.setEnabled(false);
				userFormAccessListFieldSet.setExpanded(false);
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
						formDef = studyDef.getForm(new Integer(se.getSelectedItem().getId()));
						existingFormDescription.setValue(formDef.getDescription());
						nextButton.setEnabled(true);
                                                saveAndExitButton.setEnabled(true);
						Scheduler.get().scheduleDeferred(new ScheduledCommand() {
							@Override
							public void execute() {
								ProgressIndicator.showProgressBar();
								userFormAccessListFieldSet.setExpanded(false); 
								userFormAccessListFieldSet.setEnabled(true);
								userFormAccessListField.mask();
								controller.setFormForAccessControl(formDef);
								ProgressIndicator.hideProgressBar();
							}
						});
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
				newFormName.setValue("");
				newFormDescription.setValue("");
				// set preselected form if applicable
				if (preselectForm != null) {
					formDef = preselectForm;
					List<FormSummary> fss = formStore.getModels();
					for (FormSummary fs : fss) {
						if (fs.getId().equals(String.valueOf(preselectForm.getId()))) {
							existingFormName.setValue(fs);
							break;
						}
					}
				} else if (formStore.getModels().size() == 1) {
                                        existingFormName.setValue(formStore.getModels().get(0));
                                }
			}
		});
		ItemAccessListFieldMessages messages = new ItemAccessListFieldMessages("leftHeading="+appMessages.availableUsers()+"\n" +
        		"rightHeading="+appMessages.usersWithAccessToForm()+"\n" +
        		"addOne="+appMessages.addUser()+"\n" +
        		"addAll="+appMessages.addAllUsers()+"\n" +
        		"removeOne="+appMessages.removeUser()+"\n" +
        		"removeAll="+appMessages.removeAllUsers()+"\n" +
        		"search="+appMessages.searchForAUser()+"\n" +
        		"loading="+appMessages.loading());
		userFormAccessListField = new ItemAccessListField<UserSummary>(messages, controller.getUserFormAccessController());
		userFormAccessListFieldSet = new FieldSet();
		userFormAccessListFieldSet.setHeading(appMessages.setUserAccessToForm());
		userFormAccessListFieldSet.setCollapsible(true);
		userFormAccessListFieldSet.setExpanded(false);
		userFormAccessListFieldSet.add(userFormAccessListField);
		userFormAccessListFieldSet.addListener(Events.Expand, new Listener<FieldSetEvent>() {
			public void handleEvent(FieldSetEvent be) {
				userFormAccessListField.refresh();
				userFormAccessListField.unmask();
			}
		});
		userFormAccessListFieldSet.setEnabled(false);
		createFormFS.add(userFormAccessListFieldSet);

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
		published = new CheckBox();
		published.setBoxLabel("");
		published.setFieldLabel(appMessages.formVersionDefault());
		createFormVersionPanel.add(published);
		published.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				if (published.getValue()) {
					published.setBoxLabel("");
				} else {
					published.setBoxLabel(appMessages.publishedHelp());
				}
			}
		});

		return createFormVersionPanel;
	}

	@Override
	protected void handleEvent(AppEvent event) {
		GWT.log("NewStudyFormView : handleEvent");
		if (event.getType() == NewStudyFormController.NEWSTUDYFORM) {

			preselectForm = event.getData("formDef");
			
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					ProgressIndicator.showProgressBar();
					controller.getStudies();
					ProgressIndicator.hideProgressBar();
				}
			});

			showWindow(appMessages.newStudyFormOrVersionHeading(), 540, 500);
		}
	}

	@Override
	protected void finish() {
		ProgressIndicator.showProgressBar();
		getWizardValues();
		save(true, true);
	}

	private void save(boolean launchFormDesigner, boolean triggerRefreshEvent) {
		if (studyDef == null) {
			return;
		}
		controller.saveStudy(studyDef, launchFormDesigner, triggerRefreshEvent);
	}
	
	@Override
	public void closeWindow() {
		super.closeWindow();
		ProgressIndicator.hideProgressBar();
	}
	
	public void launchFormDesigner(StudyDef studyDef) {
		this.studyDef = studyDef;
		this.formDef = studyDef.getForm(formDef.getName());
		this.formDefVersion = formDef.getVersion(formDefVersion.getName());
        AppEvent event = new AppEvent(FormDesignerController.NEW_FORM);
        event.setData("formDefVersion", formDefVersion);
        Dispatcher.get().dispatch(event);
	}
   
	@Override
	protected void saveAndExit() {
		ProgressIndicator.showProgressBar();
		getWizardValues();
		save(false, true);
	}

	private void getWizardValues() {
		// page one
		if (createStudyFS.getSelectedRadio().equalsIgnoreCase(newStudy.getBoxLabel())) {
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
			if (createFormFS.getSelectedRadio().equalsIgnoreCase(newForm.getBoxLabel())) {
				formDef = new FormDef(0, newFormName.getValue(), studyDef);
				formDef.setDescription(newFormDescription.getValue());
				formDef.setCreator((User) Registry.get(Emit.LOGGED_IN_USER_NAME));
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
		GWT.log("currentPage="+currentPage+" formVersionEditMode="+formVersionEditMode);
		if (currentPage > 1) {
			if (formVersionEditMode) {
				formDefVersion = new FormDefVersion(0,formDefinitionVersionName.getValue(),formDef);
				FormDefVersion defaultForm = formDef.getDefaultVersion();
				if (defaultForm != null) {
					formDefVersion.setXform(defaultForm.getXform());
					FormDefVersionUtil.renameFormBinding(formDefVersion,
					FormDefVersionUtil.generateDefaultFormBinding(formDefVersion));
					FormDefVersionUtil.renameXformName(formDefVersion,formDefinitionVersionName.getValue());
					formDefVersion.setLayout(defaultForm.getLayout());
				}
			}
			else {
				formDefVersion = new FormDefVersion(0,
						formDefinitionVersionName.getValue(), formDef);
			}
			
			formDefVersion.setDescription(formDefinitionVersionDescription.getValue());
			formDefVersion.setCreator((User) Registry.get(Emit.LOGGED_IN_USER_NAME));
			formDefVersion.setDateCreated(new Date());

			if (published.getValue()) {
				GWT.log("turning off other defaults for "+formDefVersion.getName());
				formDefVersion.setIsDefault(true);
				formDef.turnOffOtherDefaults(formDefVersion);
			} else {
				formDefVersion.setIsDefault(false);
			}
			formDefVersion.setDirty(true);
			formDefVersion.setFormDef(formDef);
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
		controller.setStudyForAccessControl(studyDef);
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
