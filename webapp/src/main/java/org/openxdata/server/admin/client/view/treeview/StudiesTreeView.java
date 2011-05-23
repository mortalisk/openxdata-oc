package org.openxdata.server.admin.client.view.treeview;

import java.util.List;

import org.openxdata.server.admin.client.Context;
import org.openxdata.server.admin.client.controller.facade.MainViewControllerFacade;
import org.openxdata.server.admin.client.listeners.GetFileNameDialogEventListener;
import org.openxdata.server.admin.client.permissions.UIViewLabels;
import org.openxdata.server.admin.client.permissions.util.RolesListUtil;
import org.openxdata.server.admin.client.util.DataCheckUtil;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.widget.OpenXDataMenuBar;
import org.openxdata.server.admin.client.view.constants.OpenXDataStackPanelConstants;
import org.openxdata.server.admin.client.view.widget.FormVersionOpenDialog;
import org.openxdata.server.admin.client.view.helper.StudiesTreeViewHelper;
import org.openxdata.server.admin.client.view.listeners.FormVersionOpenDialogListener;
import org.openxdata.server.admin.client.view.listeners.OnDataCheckListener;
import org.openxdata.server.admin.client.view.listeners.OpenXDataExportImportApplicationEventListener;
import org.openxdata.server.admin.client.view.treeview.listeners.ExtendedContextInitMenuListener;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.admin.model.mapping.UserStudyMap;
import org.purc.purcforms.client.util.FormUtil;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.inject.Inject;
import java.util.ArrayList;
import org.openxdata.server.admin.client.view.event.DesignFormEvent;
import org.openxdata.server.admin.client.view.event.EditableEvent;
import org.openxdata.server.admin.client.view.event.LoadRequetEvent;
import org.openxdata.server.admin.client.view.factory.OpenXDataWidgetFactory;
import org.purc.purcforms.client.controller.OpenFileDialogEventListener;


/**
 * This widgets displays studies, their forms and versions in a tree view
 * format.
 * 
 * @author daniel
 * @author Angel
 * 
 */
public class StudiesTreeView extends OpenXDataBaseTreeView implements
        OnDataCheckListener, FormVersionOpenDialogListener,
        GetFileNameDialogEventListener, OpenFileDialogEventListener,
        ExtendedContextInitMenuListener, OpenXDataExportImportApplicationEventListener {
	
	/** The list of studies. */
	private List<StudyDef> studies;
	
	/** Flag to indicate if */
	private boolean deleteData = false;
	
	/** The list of deleted studies. */
	private List<StudyDef> deletedStudies;
	
	/** The list of user mapped studies. */
	private List<UserStudyMap> mappedStudies;
	
	@SuppressWarnings("unused")
	private List<UserFormMap> mappedForms;

	/** Handle to data check utility instance of {@linkplain DataCheckUtil}. */
	private DataCheckUtil dataCheck = new DataCheckUtil(this);
	
	/**
	 * Creates a new instance of the studies tree view.
	 * 
	 * @param openXDataViewFactory
	 */
        @Inject
	public StudiesTreeView(OpenXDataWidgetFactory openXDataViewFactory) {
		super("Studies", openXDataViewFactory);
                initHandlers();
	}
	
	@Override
	public void setUp() {
		
		// Register this class with Event Dispatchers.
		super.registerWithEventDispatchers();
		
		openxdataStackPanel = widgetFactory.getOpenXdataStackPanel();
		
		// Initialize the Tree View
		tree = new Tree(images);
		tree.ensureSelectedItemVisible();
		
		// Setting Scroll Panel properties.
		scrollPanel.setWidget(tree);
		scrollPanel.setWidth("100%");
		scrollPanel.setHeight("100");
		
		// Make this class the Listener
		tree.addSelectionHandler(this);
		
		// Initialize the ScrollPanel to be the main widget for the Tree View
		initWidget(scrollPanel);
		
		// Maximize this widget
		Utilities.maximizeWidget(this);
		
		// Initialize the Context Menu.
		popup = initContextMenu();
		
		// Sets up the Helper class.
		initializeHelper();
		
	}
	
	/**
	 * Initializes the helper class.
	 */
	private void initializeHelper() {
		
		// Set the pop up Panel
		StudiesTreeViewHelper.setPopupPanel(popup);
		
		// Set the Tree
		StudiesTreeViewHelper.setTree(tree);
		
		// Inject WidgetFactory into StudiesTreeViewHelper
		StudiesTreeViewHelper.setWidgetFactory(widgetFactory);
	}
	
	/**
	 * Adds more studiesTreeView specific items
	 */
	protected PopupPanel initContextMenu() {
		PopupPanel popup = super.initContextMenu(this);
		if (popup != null) {
			MenuBar menuBar = (MenuBar) popup.getWidget();
			if (menuBar != null) {
				addAdminControlsToContextMenu(menuBar);
			}
		}
		
		return popup;
	}
	
	/**
	 * Adds <tt>StudiesTreeView</tt> specific controls to the
	 * <tt>Context Menu.</tt>
	 * 
	 * @param menuBar
	 *            {@link OpenXDataMenuBar} to add controls to.
	 */
	private void addAdminControlsToContextMenu(MenuBar menuBar) {
		
		if (RolesListUtil.getPermissionResolver().isExtraPermission(
		        "Design Form Version")) {
			menuBar.addSeparator();
			menuBar.addItem(Utilities.createHeaderHTML(images.add(),
			        "Design Form version"), true, new Command() {
				@Override
				public void execute() {
					popup.hide();
					designItem();
				}
			});
		}
		
		if (RolesListUtil.getPermissionResolver().isExtraPermission(
		        "Export Item")) {
			menuBar.addSeparator();
			menuBar.addItem(
			        Utilities.createHeaderHTML(images.add(), "Export Item"),
			        true, new Command() {
				        @Override
				        public void execute() {
					        popup.hide();
					        exportSelectedItem();
				        }
			        });
		}
		
		if (RolesListUtil.getPermissionResolver().isExtraPermission(
		        "Import Item")) {
			menuBar.addSeparator();
			menuBar.addItem(
			        Utilities.createHeaderHTML(images.add(), "Import Item"),
			        true, new Command() {
				        @Override
				        public void execute() {
					        popup.hide();
					        importSelectedItem();
				        }
			        });
		}
	}
	
	/**
	 * Displays a list of studies in the view.
	 * 
	 * @param studies
	 *            the study list.
	 */
	public void loadStudies(List<StudyDef> studies) {
		tree.clear();
		
		this.studies = studies;
		deletedStudies = new ArrayList<StudyDef>();
		User user = Context.getAuthenticatedUser();
		
		if (isLoadData()) {
			
			if (studies == null) {
				studies = new ArrayList<StudyDef>();
				studies = Context.getStudies();
				return;
			}
			
			if (Context.getAuthenticatedUser().hasAdministrativePrivileges()) {
				StudiesTreeViewHelper.loadAllStudies(studies);
			} else if (!Context.getAuthenticatedUser()
			        .hasAdministrativePrivileges()) {
				StudiesTreeViewHelper.loadStudiesAccordingToUserPrivileges(
				        studies, user, mappedStudies);
			}
		}
	}
	
	/**
	 * Displays a list of Forms in the <tt>Tree View.</tt>
	 * 
	 * @param mappedForms
	 *            the Forms list.
	 */
	private void loadMappedForms(List<UserFormMap> mappedForms) {
		
		this.mappedForms = mappedForms;
		User user = Context.getAuthenticatedUser();
		if (!user.hasAdministrativePrivileges()) {
			StudiesTreeViewHelper.loadMappedForms(user, mappedForms);
		}
	}
	
	/**
	 * Loads a study and its contents in this view.
	 * 
	 * @param studyDef
	 *            the study definition object.
	 */
	public void loadStudy(StudyDef studyDef) {
		StudiesTreeViewHelper.loadStudy(studyDef);
	}
	
	/**
	 * @see org.openxdata.server.admin.client.listeners.AppEventListener#onNewItem()
	 */
	@Override
	public void addNewItem() {
		addNewItem(null);
	}
	
	/**
	 * Adds a new item with an optional xform.
	 * 
	 * @param xForm
	 *            the xforms xml.
	 */
	public void addNewItem(String xForm) {
		StudiesTreeViewHelper.addNewItem(xForm, tree, studies, popup);
	}
	
	/**
	 * @see org.openxdata.server.admin.client.listeners.AppEventListener#onNewChildItem()
	 */
	@Override
	public void addNewChildItem() {
		StudiesTreeViewHelper.addNewChildItem(tree, studies, popup);
	}
	
	/**
	 * Designs the selected form version.
	 */
	private void designItem() {
                // TODO Should do this through an interface
                deleteData = false;
                dataCheck.itemHasFormData((Editable) tree.getSelectedItem().getUserObject());

        }
	
	/**
	 * Deletes the selected question.
	 */
	@Override
	public void deleteSelectedItem() {
		
		deleteData = true;
		TreeItem item = tree.getSelectedItem();
		if (item == null) {
			// TODO add message for internationalization purposes
			Window.alert("Please first select the item to delete");
			return;
		}
		
		dataCheck.itemHasFormData((Editable) item.getUserObject());
		
	}
	
	/**
	 * Deletes the selected study, form, or form version.
	 */
	private void deleteItem() {
		
		FormUtil.dlg.hide();
		
		TreeItem parent = item.getParentItem();
		
		boolean inCutMode = false;
		if (!inCutMode
		        && !Window.confirm(getDeleteMessage(item.getUserObject())))
			return;
		
		if (parent != null) {
			int index = parent.getChildIndex(item);
			
			// If last item is the one selected, the select the previous, else
			// the next.
			if (index == parent.getChildCount() - 1)
				index -= 1;
			
			removeFormDefItem(item, parent);
			
			// Remove the selected item.
			item.remove();
			
			// If no more kids, then select the parent.
			if (parent.getChildCount() == 0)
				tree.setSelectedItem(parent);
			else
				tree.setSelectedItem(parent.getChild(index));
		} else { // Must be the study root
			deletedStudies.add((StudyDef) item.getUserObject());
			studies.remove(item.getUserObject());
			Utilities.removeRootItem(tree, item);

		}
	}
	
	/**
	 * Gets the delete confirm message.
	 * 
	 * @param item
	 *            the item about to be deleted.
	 * @return the delete confirm message.
	 */
	private String getDeleteMessage(Object item) {
		return StudiesTreeViewHelper.getDeleteMessage(item, tree);
	}
	
	private void removeFormDefItem(TreeItem item, TreeItem parent) {
		StudiesTreeViewHelper.removeFormDefItem(item, parent);
	}
	
	@Override
	public void changeEditableProperties(Object item) {
		StudiesTreeViewHelper.changeEditableProperties(item, tree, popup);
	}
	
	public List<StudyDef> getDeletedStudies() {
		return deletedStudies;
	}
	
	/**
	 * Checks if the list of studies is valid for saving.
	 * 
	 * @return true if valid, else false.
	 */
	public boolean isValidStudyList() {
		return StudiesTreeViewHelper.isValidStudyList(tree);
	}
	
	@Override
	public void onOptionSelected(int option) {
		StudiesTreeViewHelper.onOptionSelected(option, tree, item,
		        eventBus, popup);
		
	}
	
	public void importSelectedItem() {
		StudiesTreeViewHelper.importSelectedItem(this);
	}
	
	public void exportSelectedItem() {
		StudiesTreeViewHelper.exportSelectedItem(item, this);
	}
	
	@Override
	public void onSetFileName(String fileName) {
		StudiesTreeViewHelper.onSetFileName(fileName, item);
	}
	
	@Override
	public void onSetFileContents(String contents) {
		StudiesTreeViewHelper.onSetFileContents(tree, item, contents, popup);
	}
	
	@Override
        public void onDataCheckComplete(boolean hasData, String currentItem) {

                FormUtil.dlg.hide();

                /*
                 * Dialog to determine that option to take in regard to Editable with
                 * data.
                 */
                FormVersionOpenDialog versionOpenDialog = new FormVersionOpenDialog(
                        this);

                if (!deleteData) {
                        if (hasData) {
                                versionOpenDialog.setTitle(constants.label_formversionedittitle());
                                versionOpenDialog.center();
                        } else {
                                Object userObject = tree.getSelectedItem().getUserObject();
                                if (userObject instanceof FormDefVersion)
                                        eventBus.fireEvent(new DesignFormEvent((FormDefVersion) userObject));
                                //((StudyView) itemSelectionListener).designItem(false, userObject);
                        }
                } else if (deleteData) {

                        if (hasData) {
                                versionOpenDialog.hide();
                                Window.alert("The selected { " + currentItem
                                        + " } has data and cannot be deleted!");
                        } else
                                deleteItem();
                }

        }
	
	@Override
	UIViewLabels getContextMenuLabels() {
		UIViewLabels labels = new UIViewLabels();
		
		labels.setAddLabel(constants.label_addnewstudy());
		labels.setAddChildItemLabel(constants.label_addnewform());
		
		labels.setDeleteLabel(constants.label_delete_selected());
		labels.setDeleteChildItemLabel(constants.label_deleteform());
		
		return labels;
	}
	
	public void updateStudies(List<StudyDef> studies) {
		loadStudies(studies);
                Context.setStudies(studies);
                eventBus.fireEvent(new EditableEvent<StudyDef>(studies,StudyDef.class));
	}
	
	
	public void updateUserMappedForms(List<UserFormMap> userMappedForms) {
		loadMappedForms(userMappedForms);
                eventBus.fireEvent(new EditableEvent<UserFormMap>(userMappedForms, UserFormMap.class));
	}
	
	
	public void updateUserMappedStudies(List<UserStudyMap> userMappedStudies) {
		this.mappedStudies = userMappedStudies;
                eventBus.fireEvent(new EditableEvent<UserStudyMap>(mappedStudies, UserStudyMap.class));
	}
	
	@Override
	public void exportAsPdf() {
		// do nothing
		
	}
	
	@Override
	public void onDeleteItem() {
		if (openxdataStackPanel.getSelectedIndex() == OpenXDataStackPanelConstants.INDEX_STUDIES) {
			if (RolesListUtil.getPermissionResolver().isDeleteStudies()) {
				deleteSelectedItem();
			} else {
				Window.alert("You do not have sufficient priviledges to delete Studies, forms and form versions! Contact your system administrator");
			}
		}
	}
	
	@Override
	public void onExport() {
		if (openxdataStackPanel.getSelectedIndex() == OpenXDataStackPanelConstants.INDEX_STUDIES) {
			if (RolesListUtil.getPermissionResolver().isExtraPermission(
			        "Perm_Export_Studies")) {
				exportSelectedItem();
			} else {
				Window.alert("You do not have sufficient privileges to export Studies");
			}
		}
		
	}
	
	@Override
	public void onImport() {
		if (openxdataStackPanel.getSelectedIndex() == OpenXDataStackPanelConstants.INDEX_STUDIES) {
			if (RolesListUtil.getPermissionResolver().isExtraPermission(
			        "Perm_Import_Studies")) {
				importSelectedItem();
			} else {
				Window.alert("You do not have sufficient privileges to import Users!");
			}
		}
		
	}
	
	@Override
	public void onNewChildItem() {
		if (openxdataStackPanel.getSelectedIndex() == OpenXDataStackPanelConstants.INDEX_STUDIES) {
			if (RolesListUtil.getPermissionResolver().isAddStudies()) {
				if (!(widgetFactory.getStudyView())
				        .isInFormDesignMode()) {
					addNewChildItem();
				}
			} else {
				Window.alert("You do not have sufficient priviledges to add Studies, forms and form versions! Contact your system administrator");
			}
		}
	}
	
	@Override
	public void onNewItem() {
		if (openxdataStackPanel.getSelectedIndex() == OpenXDataStackPanelConstants.INDEX_STUDIES) {
			if (RolesListUtil.getPermissionResolver().isAddStudies()) {
				if (!(widgetFactory.getStudyView())
				        .isInFormDesignMode()) {
					addNewItem();
				}
			} else {
				Window.alert("You do not have sufficient priviledges to add Studies, forms and form versions! Contact your system administrator");
			}
		}
	}
	
	@Override
	public void onOpen() {
		if ((widgetFactory.getStudyView()).isInFormDesignMode())
			(widgetFactory.getStudyView()).openForm();
		else
			onImport();
		
	}
	
	@Override
	public void onRefresh() {
		MainViewControllerFacade.refreshData();
		
	}
	
	@Override
	public void onSave() {
		if (openxdataStackPanel.getSelectedIndex() == OpenXDataStackPanelConstants.INDEX_STUDIES) {
			MainViewControllerFacade.saveStudies();
		}
	}

    private void initHandlers() {
        EditableEvent.HandlerAdaptor<StudyDef> studyHandler = new EditableEvent.HandlerAdaptor<StudyDef>() {

            @Override
            public void onChange(StudyDef item) {
                changeEditableProperties(item);
            }
        };
        EditableEvent.addHandler(eventBus, studyHandler).forClass(StudyDef.class);
        EditableEvent.HandlerAdaptor<FormDef> formDefHandler = new EditableEvent.HandlerAdaptor<FormDef>() {

            @Override
            public void onChange(FormDef item) {
                changeEditableProperties(item);
            }
        };
        EditableEvent.addHandler(eventBus, formDefHandler).forClass(FormDef.class);
        EditableEvent.HandlerAdaptor<FormDefVersion> forVerHandler = new EditableEvent.HandlerAdaptor<FormDefVersion>() {

            @Override
            public void onChange(FormDefVersion item) {
                changeEditableProperties(item);
            }
        };
        EditableEvent.addHandler(eventBus, forVerHandler).forClass(FormDefVersion.class);

        LoadRequetEvent.addHandler(eventBus, new LoadRequetEvent.Handler<UserStudyMap>() {

            @Override
            public void onLoadRequest() {
                MainViewControllerFacade.loadAllUserMappedStudies(true);
            }
        }).forClass(UserStudyMap.class);

        LoadRequetEvent.addHandler(eventBus, new LoadRequetEvent.Handler<UserFormMap>() {

            @Override
            public void onLoadRequest() {
                MainViewControllerFacade.loadAllUserMappedForms(true);
            }
        }).forClass(UserFormMap.class);
    }
}
