package org.openxdata.client.views;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.client.AppMessages;
import org.openxdata.client.Emit;
import org.openxdata.client.Refreshable;
import org.openxdata.client.RefreshableEvent;
import org.openxdata.client.controllers.FormListController;
import org.openxdata.client.model.FormSummary;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public class FormListView extends View implements Refreshable {
	final AppMessages appMessages = GWT.create(AppMessages.class);

	private Portlet portlet;
	private Grid<FormSummary> grid;

	private boolean showAllFormVersions = true;
	private Button allVersions;

	public FormListView(Controller controller) {
		super(controller);
	}

	@Override
	protected void initialize() {
		GWT.log("FormListView : initialize");
		ProgressIndicator.showProgressBar();

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(new ColumnConfig("id", appMessages.id(), 20));
		configs.add(new ColumnConfig("organisation", appMessages.study(), 290));
		configs.add(new ColumnConfig("form", appMessages.form(), 580));
		ColumnConfig ver = new ColumnConfig("version", appMessages.version(),
				50);
		ver.setAlignment(HorizontalAlignment.CENTER);
		configs.add(ver);
		ColumnConfig responsesColConfig = new ColumnConfig("responses",
				appMessages.responses(), 70);
		responsesColConfig.setAlignment(HorizontalAlignment.RIGHT);
		configs.add(responsesColConfig);

		ColumnModel cm = new ColumnModel(configs);
		cm.setHidden(0, true); // hide ID column

		GroupingView view = new GroupingView();
		view.setShowGroupedColumn(false);

		GroupingStore<FormSummary> store = new GroupingStore<FormSummary>();
		store.groupBy("organisation", true);
		grid = new Grid<FormSummary>(store, cm);
		grid.setAutoExpandColumn("form");
		grid.setAutoExpandMax(10000);
		grid.setStripeRows(true);
		grid.setBorders(true);
		grid.setView(view);

		grid.addListener(Events.CellDoubleClick,
				new Listener<GridEvent<FormSummary>>() {
					@Override
					public void handleEvent(GridEvent<FormSummary> be) {
						if (be.getColIndex() == 2) {
							captureData();
						} else if (be.getColIndex() == 4) {
							browseResponses();
						}
					}
				});

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				ProgressIndicator.showProgressBar();
				((FormListController) FormListView.this.getController())
						.getForms();
			}
		});

		// new
		Button newButton = new Button(appMessages.newStudyOrForm());
		newButton.addListener(Events.Select, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				newStudyOrForm();
			}
		});
		newButton.hide();

		// edit
		Button edit = new Button(appMessages.edit());
		edit.addListener(Events.Select, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				editStudyOrForm();
			}
		});
		edit.hide();

		// delete
		Button delete = new Button(appMessages.delete());
		delete.addListener(Events.Select, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				deleteStudyOrForm();
			}
		});
		delete.hide();

		allVersions = new Button(appMessages.showAllVersions());
		allVersions.addListener(Events.Select, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				toggleAllFormVersions();
			}
		});
		allVersions.hide();

		Button capture = new Button(appMessages.captureData());
		capture.addListener(Events.Select, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				captureData();
			}
		});
		capture.hide();

		Button browseResponses = new Button(appMessages.viewResponses());
		browseResponses.addListener(Events.Select, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				browseResponses();
			}
		});
		browseResponses.hide();

		User loggedInUser = Registry.get(Emit.LOGGED_IN_USER_NAME);
		if (loggedInUser != null) {
			if (loggedInUser.hasPermission(Permission.PERM_ADD_STUDIES,
					Permission.PERM_ADD_FORMS,
					Permission.PERM_ADD_FORM_VERSIONS)) {
				newButton.show();
				allVersions.show();
			}
			if (loggedInUser.hasPermission(Permission.PERM_EDIT_STUDIES,
					Permission.PERM_EDIT_FORMS,
					Permission.PERM_EDIT_FORM_VERSIONS)) {
				edit.show();
				allVersions.show();
			}
			if (loggedInUser.hasPermission(Permission.PERM_DELETE_STUDIES,
					Permission.PERM_DELETE_FORMS,
					Permission.PERM_DELETE_FORM_VERSIONS)) {
				delete.show();
				allVersions.show();
			}
			if (loggedInUser.hasPermission(Permission.PERM_ADD_FORM_DATA)) {
				capture.show();
			}
			if (loggedInUser.hasPermission(Permission.PERM_VIEW_FORM_DATA)) {
				browseResponses.show();
			}
		} else {
			GWT.log("Could not find logged in user, so could not determine permissions");
		}

		LayoutContainer buttonBar = new LayoutContainer();
		buttonBar.setLayout(new HBoxLayout());
		buttonBar.add(newButton, new HBoxLayoutData(new Margins(5, 5, 0, 0)));
		buttonBar.add(edit, new HBoxLayoutData(new Margins(5, 5, 0, 0)));
		buttonBar.add(delete, new HBoxLayoutData(new Margins(5, 5, 0, 0)));
		buttonBar.add(allVersions, new HBoxLayoutData(new Margins(5, 5, 0, 0)));
		HBoxLayoutData flex = new HBoxLayoutData(new Margins(5, 5, 0, 0));
		flex.setFlex(1);
		buttonBar.add(new Text(), flex);
		buttonBar.add(capture, new HBoxLayoutData(new Margins(5, 5, 0, 0)));
		buttonBar.add(browseResponses, new HBoxLayoutData(new Margins(5, 0, 0,
				0)));

		portlet = new Portlet(new FitLayout());
		portlet.setHeading(appMessages.listOfForms());
		ContentPanel cp = new ContentPanel();
		cp.setLayout(new FitLayout());
		cp.setHeaderVisible(false);
		cp.add(grid);
		portlet.add(cp);
		portlet.setScrollMode(Scroll.AUTOY);
		portlet.setSize(725, 225);
		portlet.setBottomComponent(buttonBar);
		configPanel(portlet);
	}

	public void setFormData(List<FormDef> formDefs) {
		GWT.log("FormListView : setFormData");
		ProgressIndicator.showProgressBar();
		ListStore<FormSummary> store = grid.getStore();
		store.removeAll();
		for (FormDef formDef : formDefs) {
			final FormDefVersion formVersion = formDef.getDefaultVersion();
			if (formVersion != null) {
				FormSummary formSummary = new FormSummary(formDef);
				formSummary.setStatus(appMessages.loading());
				formSummary.setResponses(appMessages.loading());
				store.add(formSummary);
				// get response data
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						((FormListController) FormListView.this.getController())
								.hasFormData(formVersion);
					}
				});
			} else {
				// log.info("FormDef '"+formDef.getName()+"' has been ignored because it does not have any versions (or version marked default)");
			}
		}
		ProgressIndicator.hideProgressBar();
	}

	private void newStudyOrForm() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				FormListController controller = (FormListController) getController();
				controller.forwardToNewStudyFormWizard();
			}
		});
	}

	private void editStudyOrForm() {
		if (grid.getSelectionModel().getSelectedItem() != null) {
			ProgressIndicator.showProgressBar();
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					FormDef formDef = grid.getSelectionModel()
							.getSelectedItem().getFormDefinition();
					FormListController controller = (FormListController) getController();

					controller.forwardToEditStudyFormController(formDef);
				}
			});
		} else {
			MessageBox.alert(appMessages.viewResponses(),
					appMessages.formMustBeSelected(), null);
		}
	}

	private void deleteStudyOrForm() {
		if (grid.getSelectionModel().getSelectedItem() != null) {
			ProgressIndicator.showProgressBar();
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					FormListController controller = (FormListController) getController();
					controller.forwardToDeleteStudyFormController(grid
							.getSelectionModel().getSelectedItem()
							.getFormDefinition());
				}
			});
		} else {
			MessageBox.alert(appMessages.viewResponses(),
					appMessages.formMustBeSelected(), null);
		}
	}

	private void toggleAllFormVersions() {
		if (grid.getSelectionModel().getSelectedItem() != null) {
			ProgressIndicator.showProgressBar();
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					FormDef formDef = grid.getSelectionModel()
							.getSelectedItem().getFormDefinition();
					if (showAllFormVersions) {
						FormListController controller = (FormListController) getController();
						controller.forwardToFormVersionController(formDef);
					}
				}
			});
			ProgressIndicator.hideProgressBar();
		} else {
			MessageBox.alert(appMessages.showAllVersions(),
					appMessages.formMustBeSelected(), null);
		}
	}

	private void captureData() {
		if (grid.getSelectionModel().getSelectedItem() != null) {
			ProgressIndicator.showProgressBar();
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					FormDef formDef = grid.getSelectionModel()
							.getSelectedItem().getFormDefinition();
					FormListController controller = (FormListController) getController();
					controller.forwardToDataCapture(formDef);
				}
			});
		} else {
			MessageBox.alert(appMessages.viewResponses(),
					appMessages.formMustBeSelected(), null);
		}
	}

	private void browseResponses() {
		FormSummary summary = grid.getSelectionModel().getSelectedItem();
		if (summary != null) {
			if (!summary.getResponses().equals("0")) {
				ProgressIndicator.showProgressBar();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						FormSummary summary = grid.getSelectionModel()
								.getSelectedItem();
						FormListController controller = (FormListController) getController();
						controller.forwardToFormResponses(summary);
					}
				});
			} else {
				MessageBox.alert(appMessages.viewResponses(),
						appMessages.noResponses(), null);
			}
		} else {
			MessageBox.alert(appMessages.viewResponses(),
					appMessages.formMustBeSelected(), null);
		}
	}

	public void setNumberOfFormResponses(FormDef formDef,
			Integer numberOfResponses) {
		FormSummary formSummary = getFormSummary(formDef.getFormId());
		if (formSummary != null) {
			formSummary.setResponses(String.valueOf(numberOfResponses));
		}
		grid.getView().refresh(false);
	}

	public void setFormStatus(FormDef formDef, Boolean active) {
		FormSummary formSummary = getFormSummary(formDef.getFormId());
		if (formSummary != null) {
			if (active) {
				formSummary.setStatus(appMessages.active());
			} else {
				formSummary.setStatus(appMessages.design());
			}
		}
		grid.getView().refresh(false);
	}

	FormSummary getFormSummary(int formDefId) {
		String formDefIdStr = String.valueOf(formDefId);
		ListStore<FormSummary> store = grid.getStore();
		for (FormSummary formSummary : store.getModels()) {
			if (formDefIdStr.equals(formSummary.getId())) {
				return formSummary;
			}
		}
		return null;
	}

	@Override
	protected void handleEvent(AppEvent event) {
		GWT.log("FormListView : handleEvent");
		if (event.getType() == FormListController.FORMLIST) {
			Portal portal = Registry.get(Emit.PORTAL);
			portal.add(portlet, 0);
		}
	}

	@Override
	public void refresh(RefreshableEvent event) {
		GWT.log("Refreshing...");
		if (event.getEventType() == RefreshableEvent.Type.CAPTURE) {
			ListStore<FormSummary> store = grid.getStore();
			FormData data = event.getData();
			for (final FormSummary summary : store.getModels()) {
				FormDef formDef = summary.getFormDefinition();
				FormDefVersion formDefVer = formDef.getDefaultVersion();
				if (data.getFormDefVersionId().equals(
						formDefVer.getFormDefVersionId())) {
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							((FormListController) FormListView.this
									.getController()).hasFormData(summary
									.getFormDefinition().getDefaultVersion());
						}
					});
					break;
				}
			}
		} else if (event.getEventType() == RefreshableEvent.Type.UPDATE_STUDY) {
			StudyDef study = event.getData();
			ListStore<FormSummary> store = grid.getStore();
			for (FormDef form : study.getForms()) {
				FormSummary summary = store.findModel("id", form.getFormId());
				if (summary != null) {
					summary.updateFormDefinition(form);
					store.update(summary);
				} else {
					GWT.log("Could not find match for updated form "
							+ form.getName() + " using ID=" + form.getId());
				}
			}

		} else if (event.getEventType() == RefreshableEvent.Type.CREATE_STUDY) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					ProgressIndicator.showProgressBar();
					((FormListController) FormListView.this.getController())
							.getForms();
				}
			});
			// Note: because the Study in the event has not been updated, the
			// ids of new persistent objects are 0, therefore cannot be matched

		} else if (event.getEventType() == RefreshableEvent.Type.DELETE) {
			ListStore<FormSummary> store = grid.getStore();
			for (final FormSummary summary : store.getModels()) {
				if (event.getData() instanceof FormDef) {
					if (summary.getFormDefinition() == event.getData()) {
						store.remove(summary);
					}
				} else if (event.getData() instanceof StudyDef) {
					if (summary.getFormDefinition().getStudy() == event
							.getData()) {
						store.remove(summary);
					}
				} else if (event.getData() instanceof FormDefVersion) {
					if (summary.getFormDefinition().getDefaultVersion() == event
							.getData()) {
						store.remove(summary);
						break; // no chance to have more than one
					}
				}
			}
		}
	}

	private void configPanel(final ContentPanel panel) {
		panel.getHeader().addTool(
				new ToolButton("x-tool-restore",
						new SelectionListener<IconButtonEvent>() {
							@Override
							public void componentSelected(IconButtonEvent ce) {
								Portal p = (Portal) panel.getParent()
										.getParent();
								int height = p.getHeight() - 20;
								panel.setHeight(height);
							}

						}));
	}

	public TreeGrid<FormSummary> formTreeGrid() {
		TreeStore<FormSummary> store = new TreeStore<FormSummary>();

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(new ColumnConfig("id", appMessages.id(), 20));
		configs.add(new ColumnConfig("organisation", appMessages.study(), 290));
		configs.add(new ColumnConfig("form", appMessages.form(), 580));
		ColumnConfig ver = new ColumnConfig("version", appMessages.version(),
				50);
		ver.setAlignment(HorizontalAlignment.CENTER);
		configs.add(ver);
		ColumnConfig responsesColConfig = new ColumnConfig("responses",
				appMessages.responses(), 70);
		responsesColConfig.setAlignment(HorizontalAlignment.RIGHT);
		configs.add(responsesColConfig);

		ColumnModel cm = new ColumnModel(configs);
		cm.setHidden(0, true);

		TreeGrid<FormSummary> tree = new TreeGrid<FormSummary>(store, cm);
		tree.setBorders(true);
		tree.setAutoExpandColumn("form");

		return tree;
	}
}
