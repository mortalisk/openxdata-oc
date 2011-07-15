package org.openxdata.server.admin.client.view.helper;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openxdata.server.admin.client.Context;
import org.openxdata.server.admin.client.controller.facade.MainViewControllerFacade;
import org.openxdata.server.admin.client.internationalization.OpenXdataConstants;
import org.openxdata.server.admin.client.listeners.GetFileNameDialogEventListener;
import org.openxdata.server.admin.client.permissions.util.RolesListUtil;
import org.openxdata.server.admin.client.util.StudyImport;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.widget.GetFileNameDialog;
import org.openxdata.server.admin.client.view.images.OpenXDataImages;
import org.openxdata.server.admin.client.view.treeview.StudiesTreeView;
import org.openxdata.server.admin.client.view.widget.CompositeTreeItem;
import org.openxdata.server.admin.client.view.widget.TreeItemWidget;
import org.openxdata.server.admin.client.view.factory.OpenXDataWidgetFactory;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.admin.model.mapping.UserStudyMap;
import org.purc.purcforms.client.util.FormUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

import org.openxdata.server.admin.client.view.event.DesignFormEvent;

import org.purc.purcforms.client.controller.OpenFileDialogEventListener;
import org.purc.purcforms.client.view.OpenFileDialog;


/**
 * Helper class for the {@link StudiesTreeView}.
 *
 */
public class StudiesTreeViewHelper {
    private static OpenXdataConstants constants = GWT.create(OpenXdataConstants.class);
	private static OpenXDataImages images = GWT.create(OpenXDataImages.class);

	/** The Studies Tree View.*/
	protected static Tree tree;
	
	/** Popup Panel for the Tree.*/
	protected static PopupPanel popup;
	
	/** Handle to <tt>Widget Factory.</tt>*/
	protected static OpenXDataWidgetFactory widgetFactory; 
	
	/** Constructs an instance of this <tt>Class.</tt> */
	private StudiesTreeViewHelper(){}
	
	/**
	 * Checks if the list of studies is valid for saving.
	 * 
	 * @return true if valid, else false.
	 */
	public static boolean isValidStudyList(Tree tree)
	{
		int index = tree.getItemCount();
		Map <String,String> map = new HashMap<String,String>();

		for(int j=0;j<index;j++){
			
			TreeItem studyItem = tree.getItem(j);
			if(map.containsKey(tree.getItem(j).getText().toLowerCase())){

				tree.setSelectedItem(studyItem);
				//TODO add message for internationalization purposes
				Window.alert("A study with that same name already exists :" + studyItem.getText());
				return false;
			}
			else{
				map.put(studyItem.getText().toLowerCase(),studyItem.getText());
			}
			
			if(studyItem.getChildCount() > 0){
				
				Map <String,String> formMap = new HashMap<String,String>();
				int ct = studyItem.getChildCount();
				
				for(int k =0;k < ct;){
					TreeItem formItem = studyItem.getChild(k);
					if(formMap.containsKey(formItem.getText().toLowerCase())){

						tree.setSelectedItem(formItem);
						//TODO add message for internationalization purposes
						Window.alert("A form with that same name already exists :"+ formItem.getText());
						return false;
					}
					else{
						formMap.put(formItem.getText().toLowerCase(), formItem.getText());
					}
					
					return checkFormVersionItems(tree, formItem);
				}
			}
		}

		return true;
	}

	/**
	 * Checks if the <tt>FormDefVersion (s)</tt> on the <tt>Tree View</tt> are unique.
	 * 
	 * @param tree <tt>Tree</tt> we checking <tt>FormDef</tt> items from.
	 * @param formItem <tt>FormDef</tt> we checking for unique <tt>FormDefVersion (s).</tt>
	 */
	private static boolean checkFormVersionItems(Tree tree, TreeItem formItem) {
		
		if(formItem.getChildCount() > 0){
			Map <String,String> formVersionMap = new HashMap<String,String>();
			int ct1 = formItem.getChildCount();
			for (int l = 0; l < ct1;){
				
				TreeItem formVItem = formItem.getChild(l);
				if(formVersionMap.containsKey(formVItem.getText().toLowerCase())){
					tree.setSelectedItem(formVItem);
					//TODO add message for internationalization purposes
					Window.alert("A form version with that same name already exists :"+ formVItem.getText());
					return false;
				}
				else{
					formVersionMap.put(formVItem.getText().toLowerCase(), formVItem.getText());
					return true;
				}
					
			}
		}
		return true;
	}
	
	/**
	 * A helper method to simplify adding tree items that have attached images.
	 * {@link #addImageItem(TreeItem, String) code}
	 * 
	 * @param root the tree item to which the new item will be added.
	 * @param title the text associated with this item.
	 */
	private static TreeItem addImageItem(TreeItem root, String title, ImageResource imageProto, Object userObj, String helpText, PopupPanel popup) {
		
		TreeItem item = new CompositeTreeItem(new TreeItemWidget(imageProto, title, popup));
		item.setUserObject(userObj);
		item.setTitle(helpText);
		root.addItem(item);
		return item;
	}
	
	/**
	 * Imports an Item.
	 * 
	 * @param openFileDialogEventListener <tt>Event Listener</tt> for the Import operation.
	 */
	public static void importSelectedItem(OpenFileDialogEventListener openFileDialogEventListener){
		OpenFileDialog dlg = new OpenFileDialog(openFileDialogEventListener,"formopen");
		dlg.center();
	}
	
	/**
	 * Exported the selected item.
	 * 
	 * @param item Item to export.
	 * @param eventListener <tt>Event Listener</tt> for the export operation.
	 */
	public static void exportSelectedItem(TreeItem item, GetFileNameDialogEventListener eventListener){
		if(item == null){
			//TODO add message for internationalization purposes
			Window.alert("Please select the item to export.");
			return;
		}
		
		if(((Editable)item.getUserObject()).isNew()){
			//TODO add message for internationalization purposes
			Window.alert("Please first save this item.");
			return;
		}
		
		String name = "";
		Object studyItem = item.getUserObject();
		if(studyItem instanceof StudyDef)
			name = ((StudyDef)studyItem).getName();
		else if(studyItem instanceof FormDef)
			name = ((StudyDef)item.getParentItem().getUserObject()).getName()+"-"+((FormDef)studyItem).getName();
		else if(studyItem instanceof FormDefVersion)
			name = ((StudyDef)item.getParentItem().getParentItem().getUserObject()).getName()+"-"+((FormDef)item.getParentItem().getUserObject()).getName()+"-"+((FormDefVersion)studyItem).getName();

		name = name.replace(" ", "");
		new GetFileNameDialog(eventListener, constants.label_export_as(),constants.label_export(),name).center();
	}
	
	/**
	 * Adds a new Item according to selected item on the <tt>Tree View.</tt>
	 * <p>
	 * If a <Study</tt> is selected, a new <tt>Study</tt> will be added.</p>
	 * <p>
	 * If a <tt>Form</tt> is selected, a new <tt>Form</tt> will be added.</p>
	 * <p>
	 * If a <tt>Form Version</tt> is selected, a new <tt>Form Version</tt> will be added.</p>
	 */
	public static void addNewItem(String xForm, Tree tree, List<StudyDef> studies, PopupPanel popup){
		
		TreeItem item = tree.getSelectedItem();				
		if(item == null || item.getUserObject() instanceof StudyDef){
			StudyDef studyDef = new StudyDef(0,"New Study"+(tree.getItemCount()+1));
			studyDef.setCreator(Context.getAuthenticatedUser());
			studyDef.setDateCreated(new Date());
			studyDef.setDirty(true);

			TreeItem root = new CompositeTreeItem(new TreeItemWidget(images.note(), studyDef.getName(),popup));
			root.setUserObject(studyDef);
			tree.addItem(root);
			studies.add(studyDef);
			tree.setSelectedItem(root);

			//Automatically add a new form
			addNewChildItem(tree, studies, popup);
                        
		}
		else if(item.getUserObject() instanceof FormDef){
			TreeItem parent = item.getParentItem();
			FormDef formDef = new FormDef(0,"New Form"+(parent.getChildCount()+1),(StudyDef)parent.getUserObject());
			formDef.setCreator(Context.getAuthenticatedUser());
			formDef.setDateCreated(new Date());
			formDef.setDirty(true);

			item = addImageItem(parent, formDef.getName(), images.drafts(), formDef, null, popup);
			((StudyDef)parent.getUserObject()).addForm(formDef);
			tree.setSelectedItem(item);
			parent.setState(true);

			//Automatically add a new form version
			addNewChildItem(tree, studies, popup);
		}
		else if(item.getUserObject() instanceof FormDefVersion){
			TreeItem parent = item.getParentItem();
			FormDefVersion formDefVersion = new FormDefVersion(0,"v"+(parent.getChildCount()+1),(FormDef)parent.getUserObject());
			formDefVersion.setCreator(Context.getAuthenticatedUser());
			formDefVersion.setDateCreated(new Date());
			formDefVersion.getFormDef().turnOffOtherDefaults(formDefVersion);
			formDefVersion.setDirty(true);

			if(xForm != null)
				formDefVersion.setXform(xForm);

			item = addImageItem(parent, formDefVersion.getName(), images.markRead(),formDefVersion, null, popup);
			((FormDef)parent.getUserObject()).addVersion(formDefVersion);
			tree.setSelectedItem(item);
			parent.setState(true);
		}
	}

	/**
	 * Adds a Child Item to the selected item on the <tt>Tree View.</tt>
	 * <p>
	 * If a <tt>Study</tt> is selected, a <tt>Form</tt> will be added,
	 * <p>
	 * If a <tt>Form</tt> is selected, a <tt>Form Version</tt> will be added.
	 * </p>
	 * <p>
	 */
	public static void addNewChildItem(Tree tree, List<StudyDef> studies, PopupPanel popup ) {
		String studyName=""; //Name of study
		
		TreeItem item = tree.getSelectedItem();
		if(item == null){
			addNewItem(null, tree, studies, popup); //Automatically add a new study/form/version to better usability
			return;
		}

		if(item.getUserObject() instanceof StudyDef){
			studyName= ((StudyDef)item.getUserObject()).getName();//set the value of the study from here
			
			FormDef formDef = new FormDef(0,studyName+" Form"+ (item.getChildCount()+1),(StudyDef)item.getUserObject());
			formDef.setCreator(Context.getAuthenticatedUser());
			formDef.setDateCreated(new Date());
			formDef.setDirty(true);

			item = addImageItem(item, formDef.getName(), images.drafts(), formDef, null, popup);
			((StudyDef)item.getParentItem().getUserObject()).addForm(formDef);
			tree.setSelectedItem(item);
			item.getParentItem().setState(true);

			//Automatically add a new form version
			addNewChildItem(tree, studies, popup);
		}
		else if(item.getUserObject() instanceof FormDef){
			FormDefVersion formDefVersion = new FormDefVersion(0,"v"+ (item.getChildCount()+1),(FormDef)item.getUserObject());
			formDefVersion.setCreator(Context.getAuthenticatedUser());
			formDefVersion.setDateCreated(new Date());
			formDefVersion.getFormDef().turnOffOtherDefaults(formDefVersion);
			formDefVersion.setDirty(true);

			item = addImageItem(item, formDefVersion.getName(), images.markRead(), formDefVersion, null, popup);
			((FormDef)item.getParentItem().getUserObject()).addVersion(formDefVersion);
			tree.setSelectedItem(item);
			item.getParentItem().setState(true);
		}
		else
			addNewItem(null, tree, studies, popup);
		
	}
	
	/**
	 * Imports a <tt>FormDef.</tt>
	 * 
	 * @param tree <tt>Tree View</tt> we importing too.
	 * @param parent <tt>FormDef</tt> parent. Should be a <tt>StudyDef.</tt>
	 * @param formDef <tt>FormDef</tt> we are importing.
	 * @param popup <tt>Popup</tt> for the <tt>Tree View.</tt>
	 */
	public static void importForm(Tree tree, TreeItem parent, FormDef formDef, PopupPanel popup){
		formDef.setStudy((StudyDef)parent.getUserObject());
		
		formDef.setCreator(Context.getAuthenticatedUser());
		formDef.setDateCreated(new Date());
		formDef.setDirty(true);

		TreeItem item = addImageItem(parent, formDef.getName(), images.drafts(), formDef, null, popup);
		tree.setSelectedItem(item);
		item.getParentItem().setState(true);

		TreeItem versionParent = item;
		List<FormDefVersion> versions = formDef.getVersions();
		if(versions != null){
			for(FormDefVersion formDefVersion : versions)
				importFormVersion(tree, versionParent, formDefVersion, popup);
		}
	}

	/**
	 * Imports a Form Version.
	 * @param tree
	 * @param parent
	 * @param formDefVersion
	 * @param popup 
	 */
	public static void importFormVersion(Tree tree, TreeItem parent, FormDefVersion formDefVersion, PopupPanel popup) {
		formDefVersion.setFormDef((FormDef)parent.getUserObject());
		
		formDefVersion.setCreator(Context.getAuthenticatedUser());
		formDefVersion.setDateCreated(new Date());
		formDefVersion.setDirty(true);

		TreeItem item = addImageItem(parent, formDefVersion.getName(), images.markRead(), formDefVersion, null, popup);
		tree.setSelectedItem(item);
		item.getParentItem().setState(true);
		
	}
	
	/**
	 * @see void org.openxdata.server.admin.client.view.treeview.StudiesTreeView#onSetFileContents(String)
	 */
	public static void onSetFileContents(final Tree tree, final TreeItem item, String contents, 
			final PopupPanel popup) {

		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,"formopen");

		try{
			builder.sendRequest(null, new RequestCallback(){
				@Override
				public void onResponseReceived(Request request, Response response){

					FormUtil.dlg.setText("Importing.......");
					FormUtil.dlg.center();

					final Response resp = response;

					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							try{
								Editable editable = StudyImport.importStudyItem(resp.getText());
								if(editable == null){
									FormUtil.dlg.hide();
									Window.alert("Invalid Import File");
									return;
								}

								if(editable instanceof StudyDef){
									setStudyContent(tree, popup, editable);
								}
								else if(editable instanceof FormDef){
									setFormContents(tree, item, popup, editable);
								}
								else if(editable instanceof FormDefVersion){
									setFormDefVersionContents(tree, item, popup, editable);
								}

								FormUtil.dlg.hide();

								MainViewControllerFacade.saveData();
							}
							catch(Exception ex){
								FormUtil.dlg.hide();
								FormUtil.displayException(ex);
							}	
						}

		
					}); 
				}
				
				@Override
				public void onError(Request request, Throwable ex){
					FormUtil.displayException(ex);
				}
			}); 
		}
		catch(RequestException ex){
			FormUtil.displayException(ex);
		}
	}
	
	/**
	 * Bind <tt>FormDefVersion</tt> contents.
	 * 
	 * @param tree <tt>Tree View</tt> to bind <tt>FormDefVersion</tt> to.
	 * @param item <tt>FormDefVersion</tt> we binding.
	 * @param popup <tt>Popup</tt> to bind to the <tt>Tree View.</tt>
	 * @param editable <tt>Editable</tt> we are checking contents for.
	 */
	private static void setFormDefVersionContents(final Tree tree,	final TreeItem item, final PopupPanel popup, Editable editable) {
		if(item == null || !(item.getUserObject() instanceof FormDef))
			Window.alert("Please first select the form to import this version into");
		else{
			((FormDef)item.getUserObject()).addVersion((FormDefVersion)editable);
			importFormVersion(tree, item,(FormDefVersion)editable, popup);
		}
	}

	/**
	 * Bind <tt>FormDef</tt> contents.
	 * 
	 * @param tree <tt>Tree View</tt> to bind <tt>FormDefVersion</tt> to.
	 * @param item <tt>FormDefVersion</tt> we binding.
	 * @param popup <tt>Popup</tt> to bind to the <tt>Tree View.</tt>
	 * @param editable <tt>Editable</tt> we are checking contents for.
	 */
	private static void setFormContents(final Tree tree,
			final TreeItem item, final PopupPanel popup,
			Editable editable) {
		if(item == null || !(item.getUserObject() instanceof StudyDef))
			Window.alert("Please first select the study to import this form into");
		else{
			((StudyDef)item.getUserObject()).addForm((FormDef)editable);
			importForm(tree, item,(FormDef)editable, popup);
		}
	}

	/**
	 * Bind <tt>StudyDef</tt> contents.
	 * 
	 * @param tree <tt>Tree View</tt> to bind <tt>StudyDef</tt> to.
	 * @param popup <tt>Popup</tt> to bind to the <tt>Tree View.</tt>
	 * @param editable <tt>Editable</tt> we are checking contents for.
	 */
	private static void setStudyContent(final Tree tree, final PopupPanel popup, Editable editable) {
		StudyDef studyDef = (StudyDef)editable;
		studyDef.setCreator(Context.getAuthenticatedUser());
		studyDef.setDateCreated(new Date());
		studyDef.setDirty(true);

		TreeItem root = new CompositeTreeItem(new TreeItemWidget(images.note(), studyDef.getName(),popup));
		root.setUserObject(studyDef);
		tree.addItem(root);
		Context.getStudies().add(studyDef);
		tree.setSelectedItem(root);

		List<FormDef> forms = studyDef.getForms();
		if(forms != null){
			for(FormDef formDef : forms)
				importForm(tree, root, formDef, popup);
		}
	}
	
	/**
	 * Fired when an option is selected on the <tt>FormVersionOpenDialogListener.</tt>
	 * 
	 * @param option the selected option.
	 * @param tree <tt>Tree View</tt> we checking items from.
	 * @param item <tt>Tree Item</tt> we checking.
	 * @param eventBus <tt>ItemSelectionListener</tt> for the data check operation.
	 * @param popup <tt>Popup</tt> for the <tt>Tree View.</tt>
	 */
        public static void onOptionSelected(int option, Tree tree, TreeItem item, EventBus eventBus, PopupPanel popup) {
                if (option == 0) {//Create new form verson
                        if (item.getUserObject() instanceof FormDefVersion) {
                                FormDefVersion copyVersion = (FormDefVersion) item.getUserObject();
                                String xForm = copyVersion.getXform();
                                addNewItem(xForm, tree, Context.getStudies(), popup);
                        }
                } else if (option == 1) {//Open the dialog as read only
                        final Object userObject = tree.getSelectedItem().getUserObject();
                        //((StudyView)eventBus).designItem(true, tree.getSelectedItem().getUserObject());
                        if (userObject instanceof FormDefVersion)
                        	eventBus.fireEvent(new DesignFormEvent((FormDefVersion) userObject));
                } else if (option == 2) {//Cancel option
                        return;
                } else {
                        //((StudyView)eventBus).designItem(false, tree.getSelectedItem().getUserObject());
                        final Object userObject = tree.getSelectedItem().getUserObject();
                        if (userObject instanceof FormDefVersion)
                        	eventBus.fireEvent(new DesignFormEvent((FormDefVersion) userObject, true));
                }
        }
	
	/**
	 * Fired when the name of the exported <tt>StudyDef</tt> is called.
	 * 
	 * @param fileName Name set.
	 * @param item <tt>Tree Item</tt> we exporting.
	 */
	public static void onSetFileName(String fileName, TreeItem item){
		if(fileName != null && fileName.trim().length() > 0)
			exportItem(fileName, item);
	}
	
	/**
	 * Exports a study, form, or form version to a given file name.
	 * 
	 * @param fileName the file name.
	 * @param item <tt>Tree Item</tt> we are exporting.
	 */
	private static void exportItem(String fileName, final TreeItem item){

		final String name = fileName;

		FormUtil.dlg.setText("Exporting.......");
		FormUtil.dlg.center();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				try{
					Integer id = null;
					String type = "study";
					Editable editable = (Editable)item.getUserObject();
					if(editable instanceof FormDef){
						type = "form";
						id = ((FormDef)editable).getId();
					}
					else if(editable instanceof FormDefVersion){
						type = "version";
						id = ((FormDefVersion)editable).getFormDefVersionId();
					}
					else
						id = ((StudyDef)editable).getId();

					String url = "studyexport?";
					url += "type=" + type;
					url += "&id=" + id;
					url += "&filename=" + name;

					Window.Location.replace(URL.encode(url));

					FormUtil.dlg.hide();		
				}
				catch(Exception ex){
					FormUtil.dlg.hide();
					FormUtil.displayException(ex);
				}	
			}
		});
	}
	
	/**
	 * Removes the given <tt>Form Def Tree Item</tt> from the Parent.
	 * 
	 * @param item <tt>TreeItem</tt> to remove.
	 * @param parent <tt>Parent</tt> of <tt>Tree Item.</tt>
	 */
	public static void removeFormDefItem(TreeItem item, TreeItem parent){
		Object userObj = item.getUserObject();
		Object parentUserObj = parent.getUserObject();

		if(userObj instanceof FormDefVersion){
			((FormDef)parentUserObj).removeVersion((FormDefVersion)userObj);
			((FormDef)parentUserObj).setDirty(true);
			((StudyDef)parent.getParentItem().getUserObject()).setDirty(true);
		}
		else if(userObj instanceof FormDef){
			((StudyDef)parentUserObj).removeForm((FormDef)userObj);	
			((StudyDef)parentUserObj).setDirty(true);
		}
	}
	
	/**
	 * Sets the Properties of the selected Item on the <tt>Tree View.</tt>
	 * 
	 * @param item <tt>Tree Item</tt> to set properties for.
	 * @param tree <tt>Tree View</tt> to set properties on.
	 * @param popup <tt>Popup</tt> for the <tt>Tree View.</tt>
	 */
	public static void changeEditableProperties(Object item, Tree tree, PopupPanel popup){
		TreeItem treeItem = tree.getSelectedItem();
		if(item == null)
			return; //How can this happen?

		if(item instanceof StudyDef){
			StudyDef studyDef = (StudyDef)item;
			treeItem.setWidget(new TreeItemWidget(images.note(), studyDef.getName(),popup));
			treeItem.setTitle(studyDef.getDescription());
			studyDef.setDirty(true);
		}
		else if(item instanceof FormDef){
			FormDef formDef = (FormDef)item;
			treeItem.setWidget(new TreeItemWidget(images.drafts(), formDef.getName(),popup));
			treeItem.setTitle(formDef.getDescription());
			formDef.setDirty(true);
		}
		else if(item instanceof FormDefVersion){
			FormDefVersion formDefVersion = (FormDefVersion)item;
			treeItem.setWidget(new TreeItemWidget(images.markRead(), formDefVersion.getName(),popup));
			treeItem.setTitle(formDefVersion.getDescription());
			formDefVersion.setDirty(true);
		}
	}
	
	/**
	 * Constructs a delete message according to the selected Item on the <tt>Tree View.</tt>
	 * 
	 * @param item <tt>Tree item</tt> we intend to delete.
	 * @param tree <tt>Tree View</tt> on which <tt>Tree Item</tt> is bound.
	 * 
	 * @return deleted Message.
	 */
	public static String getDeleteMessage(Object item, Tree tree){
		//TODO add message for internationalization purposes
		String message = "Do you really want to delete the selected item and all its children (if any) ?";
		
		if(item instanceof StudyDef){
			//TODO add message for internationalization purposes
			message = "Do you really want to delete the study {" + ((StudyDef)item).getName() +"}";
			if(tree.getSelectedItem().getChildCount() > 0)
				message += " and all its forms ";
			message += " ?";
		}
		else if(item instanceof FormDef){
			//TODO add message for internationalization purposes
			message = "Do you really want to delete the form {" + ((FormDef)item).getName() +"}";
			if(tree.getSelectedItem().getChildCount() > 0)
				message += " and all its versions ";
			message += " ?";
		}
		else if(item instanceof FormDefVersion)
			//TODO add message for internationalization purposes
			message = "Do you really want to delete the form version {" + ((FormDefVersion)item).getName() +"} ?";

		return message;
	}
	
	/**
	 * Sets the <tt>Widget Factory.</tt>
	 * 
	 * @param widgetFactory <tt>Widget Factory to set.</tt>
	 */
	public static void setWidgetFactory(OpenXDataWidgetFactory widgetFactory) {
		StudiesTreeViewHelper.widgetFactory = widgetFactory;		
	}
	
	/**
	 * Loads a study and its contents in this view.
	 * 
	 * @param studyDef the study definition object.
	 */
	public static void loadStudy(StudyDef studyDef){

		TreeItem studyRoot = new CompositeTreeItem(new TreeItemWidget(images.note(), studyDef.getName(),popup));
		studyRoot.setUserObject(studyDef);
		tree.addItem(studyRoot);

		if(studyDef.getForms() != null){
			for(FormDef def : studyDef.getForms())
				loadForm(def,studyRoot,false);
		}

		Utilities.selectFirstItemOnTreeView(tree);
	}
	
	/**
	 * Loads a form and its contents in the view.
	 * 
	 * @param formDef the form definition object.
	 * @param studyRoot the tree item for the study which is the parent of this form.
	 * @param select set to true to automatically select the given study.
	 */
	private static void loadForm(FormDef formDef, TreeItem studyRoot, boolean select){

		TreeItem formRoot = StudiesTreeViewHelper.addImageItem(studyRoot, formDef.getName(), images.drafts(), formDef, "", popup);
		
		List<FormDefVersion> versions = formDef.getVersions();
		if(versions != null){
			for(FormDefVersion versionDef : versions){
				addImageItem(formRoot, versionDef.getName(), images.markRead(),versionDef, null, popup);
			}
		}

		if(select){
			tree.setSelectedItem(studyRoot);
			studyRoot.setState(true);
		}
	}
	
	/**
	 * Load all the returned <code>Studies</code>.
	 * 
	 * @param studies <code>List</code> of <code>Studies</code> to load.
	 */
	public static void loadAllStudies(List<StudyDef> studies) {		
		
		if(studies == null)
			studies = Context.getStudies();
		
		for(StudyDef def : studies){
			loadStudy(def);
		}
	}
	
	/**
	 * Loads User Mapped Studies.
	 * 
	 * @param studies List of <tt>StudyDefs</tt> from which to filter out <tt>UserStudyMaps.</tt>
	 * 
	 * @param user <tt>User</tt> to load <tt>StudyDefs</tt> for.
	 */
	public static void loadStudiesAccordingToUserPrivileges(List<StudyDef> studies, User user, List<UserStudyMap> mappedStudies) {
		if(mappedStudies != null){
			List<UserStudyMap> userMappedStudies = RolesListUtil.getPermissionResolver().getUserMappedStudies(user, mappedStudies);					
			if(userMappedStudies != null && userMappedStudies.size() > 0){
				for(UserStudyMap x : userMappedStudies){
					for(StudyDef def : studies){	
						if(x.getStudyId() == def.getId())
							loadStudy(def);
					}
				}
			}
		}
	}
	
	/**
	 * Loads the <code>User Mapped Forms.</code>
	 * @param mappedForms <code>List of <code>Mapped Forms</code> to Load.
	 */
	public static void loadMappedForms(User user, List<UserFormMap> mappedForms) {
		tree.clear();
		if(mappedForms != null && mappedForms.size() > 0){
			List<UserFormMap> userMappedForms = RolesListUtil.getPermissionResolver().getUserMappedForms(user, mappedForms);
			for(UserFormMap map : userMappedForms){
				for(FormDef xForm : Context.getForms()){
					if(map.getFormId() == xForm.getId()){
						bindMappedFormToTreeView(xForm);
					}
				}
			}
		}
	}
	
	/**
	 * Binds a <code>User Mapped Form</code> to the <code>Tree view.</code>
	 * 
	 * @param formDef <code>FormDef</code> to bind.
	 */
	private static void bindMappedFormToTreeView(FormDef formDef) {
		StudyDef xDef = formDef.getStudy();
		if(xDef != null){
			TreeItem formRoot = constructTreeItem(xDef);
			loadForm(formDef, formRoot, false);
		}
		
		Utilities.initializeTreeView(tree);
	}
	
	/**
	 * Constructs a <code>StudyDef Tree item root</code> to bind other items to.
	 * 
	 * @param xDef <code>StudyDef</code> for whom to create root.
	 * @return Constructed <code>Tree Item.</code>
	 */
	private static TreeItem constructTreeItem(StudyDef xDef) {
		TreeItem root = new CompositeTreeItem(new TreeItemWidget(images.note(), xDef.getName(), popup));
		if(root.getUserObject() != null){
			if(!root.getUserObject().equals(xDef)){
				root.setUserObject(xDef);
				tree.addItem(root);
			}
		}
		else{
			root.setUserObject(xDef);
			tree.addItem(root);
		}
		
		return root;
	}

	/**
	 * Sets the popup panel for the Tree.
	 * 
	 * @param popup popup to set.
	 */
	public static void setPopupPanel(PopupPanel popup) {
		StudiesTreeViewHelper.popup = popup;
	}

	/**
	 * Sets the Tree View.
	 * 
	 * @param tree Tree View to set.
	 */
	public static void setTree(Tree tree) {
		StudiesTreeViewHelper.tree = tree;		
	}
}
