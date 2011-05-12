package org.openxdata.client.views;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openxdata.client.Emit;
import org.openxdata.client.controllers.NewStudyFormController;
import org.openxdata.client.model.FormSummary;
import org.openxdata.client.model.StudySummary;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
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
import org.purc.purcforms.client.FormDesignerWidget;
import org.purc.purcforms.client.controller.IFormSaveListener;

import org.openxdata.client.model.UserSummary;
import org.openxdata.server.admin.model.FormDefVersionText;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.admin.model.mapping.UserStudyMap;
import org.purc.purcforms.client.locale.LocaleText;

public class NewStudyFormView extends WizardView implements IFormSaveListener {

	/** The form designer widget. */
	private FormDesignerWidget formDesigner;
	/** The formDesignerWindow for the form designer */
	Window formDesignerWindow;
	// input field for new study page
	private TextField<String> newStudyName;
	private TextField<String> newStudyDescription;
	private ComboBox<StudySummary> existingStudyName;
	private TextField<String> existingStudyDescription;
	private RadioFieldSet createStudyFS;
	private Radio newStudy;
	private Radio existingStdyRdio;
	private UserAccessGrids userStudyAccessGrid;
	private UserAccessGrids userFormAccessGrid;
	// input fields for new form page
	private RadioFieldSet createFormFS;
	private TextField<String> newFormName;
	private TextField<String> newFormDescription;
	private ComboBox<FormSummary> existingFormName;
	private TextField<String> existingFormDescription;
	private Radio newForm;
	private Radio existingForm;
	// input fields for form versions
	private TextField<String> formVersionNameTfld;
	private TextField<String> formVersionDescr;
	private CheckBox formVersionDefault;
	// keep track of created study/form
	private StudyDef studyDef;
	private FormDef formDef;
	private FormDefVersion formDefVersion;
	private List<StudyDef> studies;
	private List<FormDef> forms;
	private List<User> users;
	private List<UserStudyMap> mappedStudies;
	private List<UserFormMap> mappedForms;

	ListStore<StudySummary> store;
	ListStore<FormSummary> formStore;

	private int currentPage = 0;

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
		nextButton.setEnabled(false);
		// resize window if the previous window was expanded
		if (activePage == 0 && currentPage != 0) {
			resizeWindow(-90, getWizardWidth());
		}
		currentPage = activePage;
		if (activePage == 1) {
			// check what was selected in the page before
			if (createStudyFS.getSelectedRadio().equals(
					appMessages.addNewStudy())) {
				// remove select existing page from page 2
				existingFormName.hide();
				existingFormDescription.hide();
				newForm.hide();
				newForm.setValue(true);
				existingForm.hide();
			} else if (!newForm.isVisible()) {
				// make sure all radio buttons are showing
				existingFormName.show();
				existingFormDescription.show();
				newForm.show();
				newForm.setValue(false);
				setStudyForms();
				existingForm.show();
			}
			userStudyAccessGrid.setExpanded(false);
			userFormAccessGrid.setExpanded(false);
			resizeWindow(0, getWizardWidth());
		} else if (activePage == 2
				&& createFormFS.getSelectedRadio().equals(
						appMessages.addNewForm())) {
			formVersionNameTfld.setValue("v1");
		} else if (activePage == 2) {
			// TODO: set formVersionNameTfld
			if (activePage == 2
					&& createFormFS.getSelectedRadio().equals(
							appMessages.existingForm())) {
				int versions = existingFormName.getValue().getFormDefinition()
						.getVersions().size();
				formVersionNameTfld.setValue("v" + (versions + 1));
			}
		}
	}

	protected void setStudyForms() {
		String studyName = existingStudyName.getValue().getStudy();
		formStore.removeAll();
		for (FormDef form : forms) {
			if (form.getStudy().getName().equals(studyName)) {
				formStore.add(new FormSummary(form));
			}
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

		createStudyFS = new RadioFieldSet();
		createStudyPanel.add(createStudyFS);

		newStudyName = new TextField<String>();
		newStudyName.setFieldLabel(appMessages.studyName());
		newStudyName.setAllowBlank(false);
		newStudyName.setValidator(new Validator() {

			@Override
			public String validate(Field<?> field, String value) {
				if (value == null) {
					return "Field required";
				}
				nextButton.setEnabled(true);
				// check that new study is unique
				if (checkStudyExistance(value, studies)) {
					return "Study Must be Unique";
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
					public void selectionChanged(
							SelectionChangedEvent<StudySummary> se) {
						existingStudyDescription.setValue(se.getSelectedItem().getDescription());
						setUserStudyMap(se.getSelectedItem().getStudyDefinition(), users);
						nextButton.setEnabled(true);
					}
				});
		existingStudyDescription = new TextField<String>();
		existingStudyDescription.setFieldLabel(appMessages.studyDescription());
		existingStdyRdio = createStudyFS.addRadio("study",
				appMessages.existingStudy(), existingStudyName,
				existingStudyDescription);
		existingStdyRdio.addListener(Events.OnClick,
				new Listener<FieldEvent>() {

					@Override
					public void handleEvent(FieldEvent be) {
						nextButton.setEnabled(false);
						newStudyName.setValue("");
						newStudyDescription.setValue("");
					}
				});
		userStudyAccessGrid = new UserAccessGrids(
				appMessages.usersWithAccessToStudy());
		createStudyFS.add(userStudyAccessGrid);

		userStudyAccessGrid.addListener(Events.Expand,
				new Listener<ComponentEvent>() {

					@Override
					public void handleEvent(ComponentEvent be) {
						// hide upper controlls so that this controll fills up
						// the whole window
						newStudy.hide();
						newStudyName.hide();
						newStudyDescription.hide();
						existingStdyRdio.hide();
						existingStudyName.hide();
						existingStudyDescription.hide();
						resizeWindow(90, userStudyAccessGrid.getWidth() + 40);
						// hack just to resolve the icons overlapping issue.
						userStudyAccessGrid.refreshToolbars();
					}
				});
		userStudyAccessGrid.addListener(Events.BeforeCollapse,
				new Listener<ComponentEvent>() {

					@Override
					public void handleEvent(ComponentEvent be) {
						// be sure to check that it has been expanded
						// to avoid resizing the initial window
						if (userStudyAccessGrid.isExpanded()) {
							// show upper controlls
							newStudy.show();
							newStudyName.show();
							newStudyDescription.show();
							existingStdyRdio.show();
							existingStudyName.show();
							existingStudyDescription.show();
							resizeWindow((-90), getWizardWidth());
						}
					}
				});
		return createStudyPanel;
	}

	private LayoutContainer createNewFormPage() {
		LayoutContainer createFormPanel = new LayoutContainer();
		createFormPanel.setLayout(new FitLayout());
		createFormPanel.setStyleAttribute("padding", "10px");

		createFormFS = new RadioFieldSet();
		createFormPanel.add(createFormFS);

		newFormName = new TextField<String>();
		newFormName.setFieldLabel(appMessages.formName());
		newFormName.setAllowBlank(false);
		newFormName.setValidator(new Validator() {
			@Override
			public String validate(Field<?> field, String value) {
				if (value == null) {
					return "Field required";
				}
				nextButton.setEnabled(true);
				// check that new form is unique
				if (checkFormExistance(value, forms)) {
					return "Form Already Exists";
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
					public void selectionChanged(
							SelectionChangedEvent<FormSummary> se) {
						existingFormDescription.setValue(se.getSelectedItem().getFormDefinition().getDescription());
						setUserFormMap(se.getSelectedItem().getFormDefinition(), users);
						nextButton.setEnabled(true);
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
			}
		});
		userFormAccessGrid = new UserAccessGrids(
				appMessages.usersWithAccessToForm());
		createFormFS.add(userFormAccessGrid);
		userFormAccessGrid.addListener(Events.Expand,
				new Listener<ComponentEvent>() {

					@Override
					public void handleEvent(ComponentEvent be) {
						newForm.hide();
						newFormName.hide();
						newFormDescription.hide();
						existingForm.hide();
						existingFormName.hide();
						existingFormDescription.hide();
						resizeWindow(90, userFormAccessGrid.getWidth() + 40);
						// hack just to resolve the icons overlapping issue.
						userStudyAccessGrid.refreshToolbars();
					}
				});
		userFormAccessGrid.addListener(Events.BeforeCollapse,
				new Listener<ComponentEvent>() {

					@Override
					public void handleEvent(ComponentEvent be) {
						// be sure to check that it has been expanded
						// to avoid resizing the initial window
						if (userFormAccessGrid.isExpanded()) {
							// show upper controlls
							newForm.show();
							newFormName.show();
							newFormDescription.show();
							existingForm.show();
							existingFormName.show();
							existingFormDescription.show();
							resizeWindow((-90), getWizardWidth());
						}
					}
				});

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

		formVersionNameTfld = new TextField<String>();
		formVersionNameTfld.setFieldLabel(appMessages.formVersionName());
		formVersionNameTfld.setName("formVersionName");
		formVersionNameTfld.setAllowBlank(false);
		createFormVersionPanel.add(formVersionNameTfld);
		formVersionDescr = new TextField<String>();
		formVersionDescr.setFieldLabel(appMessages.formVersionDescription());
		formVersionDescr.setName("formVersionDescription");
		createFormVersionPanel.add(formVersionDescr);
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
					NewStudyFormController controller = (NewStudyFormController) NewStudyFormView.this
							.getController();
					controller.getStudies();
					controller.getForms();
					controller.getUsers();
					controller.getUserMappedStudies();
					controller.getUserMappedForms();
					ProgressIndicator.hideProgressBar();
				}
			});

			showWindow(appMessages.newStudyFormOrVersionHeading(), 500, 320);
		}
	}

	@Override
	protected void finish() {
		getWizardValues();
		String formName = formDef.getName();
		String formVersionName = formDefVersion.getName();
		Integer formVersionId = formDef.getDefaultVersion()
				.getFormDefVersionId();
		String formBinding = "binding";

		// launch purcforms designer
		if (formDesigner == null) {
			formDesigner = new FormDesignerWidget(false, true, true);
			formDesigner.setSplitPos("20%");
			formDesigner.setFormSaveListener(this);
		}

		formDesigner.addNewForm(formName + "_" + formVersionName, formBinding,
				formVersionId);

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
				newStudyFrmWindowListener);
		formDesignerWindow.setModal(true);

		formDesigner.onWindowResized(
				com.google.gwt.user.client.Window.getClientWidth() - 100,
				com.google.gwt.user.client.Window.getClientHeight() - 75);

		formDesignerWindow.show();
		formDesignerWindow.maximize();

		ProgressIndicator.hideProgressBar();
	}

	final Listener<ComponentEvent> newStudyFrmWindowListener = new WindowListener();

	class WindowListener implements Listener<ComponentEvent> {
		@Override
		public void handleEvent(ComponentEvent be) {
			be.setCancelled(true);
			be.stopEvent();
			MessageBox.confirm(appMessages.cancel(),
					LocaleText.get("cancelFormPrompt"),
					new Listener<MessageBoxEvent>() {
						@Override
						public void handleEvent(MessageBoxEvent be) {
							if (be.getButtonClicked().getItemId()
									.equals(Dialog.YES)) {
								formDesignerWindow.removeListener(
										Events.BeforeHide,
										newStudyFrmWindowListener);
								formDesignerWindow.hide();
								formDesignerWindow.addListener(
										Events.BeforeHide,
										newStudyFrmWindowListener);
							}
						}
					});
		}
	};

	private void save() {
		if (studyDef == null) {
			return;
		}
		NewStudyFormController controller2 = (NewStudyFormController) NewStudyFormView.this
				.getController();
		controller2.saveStudy(studyDef);
		// save any mapped study or form
		saveUserStudyMap();
		saveUserFormMap();
	}

	public void saveUserStudyMap() {
		if (!userStudyAccessGrid.getTempMappedItems().isEmpty()) {
			for (int i = 0; i < userStudyAccessGrid.getTempMappedItems().size(); ++i) {
				for (User user : users) {
					if (user.getName().equals(
							userStudyAccessGrid.getTempMappedItems().get(i)
									.getName())
							&& !(user.getName().equals(((User) Registry
									.get(Emit.LOGGED_IN_USER_NAME)).getName()))) {
						// check already mapped users to this study
						UserStudyMap map = new UserStudyMap();
						map.addStudy(studyDef);
						map.addUser(user);
						map.setDirty(true);
						((NewStudyFormController) NewStudyFormView.this
								.getController()).saveUserMappedStudy(map);
						break;
					}
				}
			}
		}
		if (!userStudyAccessGrid.getTempItemstoUnmap().isEmpty()) {
			for (int i = 0; i < userStudyAccessGrid.getTempItemstoUnmap()
					.size(); ++i) {
				for (UserStudyMap map : mappedStudies) {
					for (User user : users) {
						if ((user.getName().equals(userStudyAccessGrid
								.getTempItemstoUnmap().get(i).getName()))
								&& (user.getUserId() == map.getUserId())) {
							((NewStudyFormController) NewStudyFormView.this
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
		if (!userFormAccessGrid.getTempMappedItems().isEmpty()) {
			for (int i = 0; i < userFormAccessGrid.getTempMappedItems().size(); ++i) {
				for (User user : users) {
					if (user.getName().equals(
							userFormAccessGrid.getTempMappedItems().get(i)
									.getName())
							&& !(user.getName().equals(((User) Registry
									.get(Emit.LOGGED_IN_USER_NAME)).getName()))) {
						UserFormMap map = new UserFormMap();
						map.addForm(formDef);
						map.addUser(user);
						map.setDirty(true);
						((NewStudyFormController) NewStudyFormView.this
								.getController()).saveUserMappedForm(map);
						break;
					}
				}
			}
		}
		if (!userFormAccessGrid.getTempItemstoUnmap().isEmpty()) {
			for (int i = 0; i < userFormAccessGrid.getTempItemstoUnmap().size(); ++i) {
				for (UserFormMap map : mappedForms) {
					for (User user : users) {
						if ((user.getName().equals(userFormAccessGrid
								.getTempItemstoUnmap().get(i).getName()))
								&& (user.getUserId() == map.getUserId())) {
							((NewStudyFormController) NewStudyFormView.this
									.getController()).deleteUserMappedForm(map);
							break;
						}
					}
				}
			}
		}
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
		// TODO Auto-generated method stub
		try {
			if (formDefVersion == null) {
				MessageBox.alert("Error",
						"Please remove the formId attribute from the xform",
						null);
				return false;
			}

			formDefVersion.setXform(xformsXml);
			formDefVersion.setLayout(layoutXml);
			formDefVersion.setDirty(true);

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
			if (formDefVersion == null) {
				// TODO add message for internationalization purposes
				MessageBox.alert("Error",
						"Please select the form version first", null);
				return;
			}

			FormDefVersionText formDefVersionText = formDefVersion
					.getFormDefVersionText("en");
			if (formDefVersionText == null) {
				formDefVersionText = new FormDefVersionText("en",
						formDefVersion.getFormDefVersionId(), xformsLocaleText,
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
				formDesignerWindow.removeListener(Events.BeforeHide,
						newStudyFrmWindowListener);
				formDesignerWindow.hide();
				ProgressIndicator.hideProgressBar();
				closeWindow();
			}
		} catch (Exception ex) {
			//
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
			studyDef = existingStudyName.getValue().getStudyDefinition();
			studyDef.setDescription(existingStudyDescription.getValue());
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
				studyDef.addForm(formDef);
			} else {
				formDef = studyDef.getForm(existingFormName.getValue()
						.getFormDefinition().getFormId());
				formDef.setDescription(existingFormDescription.getValue());
			}
		}
		// page 3
		if (currentPage > 1) {
			formDefVersion = new FormDefVersion(0,
					formVersionNameTfld.getValue(), formDef);
			formDefVersion.setDescription(formVersionDescr.getValue());
			formDefVersion.setCreator((User) Registry
					.get(Emit.LOGGED_IN_USER_NAME));
			formDefVersion.setDateCreated(new Date());
			if (formVersionDefault.getValue()) {
				formDefVersion.getFormDef()
						.turnOffOtherDefaults(formDefVersion);
			}
			formDefVersion.setDirty(true);
			formDef.addVersion(formDefVersion);
		}

	}

	public void setStudies(List<StudyDef> studies) {
		this.studies = studies;
		for (StudyDef study : studies) {
			store.add(new StudySummary(study));
		}
	}

	public void setUserMappedStudies(List<UserStudyMap> amappedStudies) {
		this.mappedStudies = amappedStudies;
	}

	public void setUserMappedForms(List<UserFormMap> amappedForms) {
		this.mappedForms = amappedForms;
	}

	public void setForms(List<FormDef> forms) {
		this.forms = forms;
		for (FormDef form : this.forms) {
			formStore.add(new FormSummary(form));
		}
	}

	public void setUsers(List<User> users) {
		this.users = users;
		List<UserSummary> unMappedUsers = new ArrayList<UserSummary>();
		for (User user : users) {
			userStudyAccessGrid.addUnmappedUser(new UserSummary(user));
			unMappedUsers.add(new UserSummary(user));
			userFormAccessGrid.addUnmappedUser(new UserSummary(user));
		}
		userStudyAccessGrid.updateLists(unMappedUsers,
				new ArrayList<UserSummary>());
		userFormAccessGrid.updateLists(unMappedUsers,
				new ArrayList<UserSummary>());
	}

	private boolean checkStudyExistance(String name, List<StudyDef> items) {
		boolean isFound = false;
		for (StudyDef x : items) {
			if (x.getName().equalsIgnoreCase(name)) {
				isFound = true;
				break;
			}
		}
		return isFound;
	}

	private boolean checkFormExistance(String name, List<FormDef> items) {
		boolean isFound = false;
		for (FormDef x : items) {
			if (x.getName().equalsIgnoreCase(name)) {
				isFound = true;
				break;
			}
		}
		return isFound;
	}

	/*
	 * Load study names into left and right listboxes appropriately
	 */
	private void setUserStudyMap(StudyDef study, List<User> users) {
		userStudyAccessGrid.clear();

		List<UserSummary> mappedUsers = new ArrayList<UserSummary>();
		List<UserSummary> unMappedUsers = new ArrayList<UserSummary>();
		for (User u : users) {
			// check whether user is mapped to this study
			boolean found = false;
			for (UserStudyMap map : mappedStudies) {
				if ((map.getUserId() == u.getId())
						&& (map.getStudyId() == study.getStudyId())) {
					userStudyAccessGrid.addMappedUser(new UserSummary(u));
					mappedUsers.add(new UserSummary(u));
					found = true;
					break;
				}
			}
			if (!found) {
				userStudyAccessGrid.addUnmappedUser(new UserSummary(u));
				unMappedUsers.add(new UserSummary(u));
			}
		}
		userStudyAccessGrid.updateLists(unMappedUsers, mappedUsers);
	}

	/*
	 * Load formdefinition names into left and right listboxes appropriately
	 */
	private void setUserFormMap(FormDef form, List<User> users) {
		userFormAccessGrid.clear();

		List<UserSummary> mappedUsers = new ArrayList<UserSummary>();
		List<UserSummary> unMappedUsers = new ArrayList<UserSummary>();
		for (User user : users) {
			boolean found = false;
			for (UserFormMap map : mappedForms) {
				if ((map.getUserId() == user.getId())
						&& (map.getFormId() == form.getFormId())) {
					userFormAccessGrid.addMappedUser(new UserSummary(user));
					mappedUsers.add(new UserSummary(user));
					found = true;
					break;
				}
			}
			if (!found) {
				userFormAccessGrid.addUnmappedUser(new UserSummary(user));
				unMappedUsers.add(new UserSummary(user));
			}
		}
		userFormAccessGrid.updateLists(unMappedUsers, mappedUsers);
	}

}
