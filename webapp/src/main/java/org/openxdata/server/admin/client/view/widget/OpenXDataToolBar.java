package org.openxdata.server.admin.client.view.widget;

import com.google.gwt.core.client.GWT;
import java.util.List;
import java.util.Vector;

import org.openxdata.server.admin.client.Context;
import org.openxdata.server.admin.client.VersionInfo;
import org.openxdata.server.admin.client.controller.facade.MainViewControllerFacade;
import org.openxdata.server.admin.client.internationalization.OpenXdataConstants;
import org.openxdata.server.admin.client.permissions.util.RolesListUtil;
import org.openxdata.server.admin.client.view.event.dispatcher.ExtendedEventDispatcher;
import org.openxdata.server.admin.client.view.images.OpenXDataImages;
import org.openxdata.server.admin.client.view.listeners.OpenXDataExportImportApplicationEventListener;
import org.openxdata.server.admin.client.view.listeners.OpenXDataViewApplicationEventListener;
import org.openxdata.server.admin.client.view.listeners.OpenXDataViewExtendedApplicationEventListener;
import org.openxdata.server.admin.model.User;
import org.purc.purcforms.client.util.FormUtil;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import org.openxdata.server.admin.client.view.event.LogOutEvent;

/**
 * 
 * Encapsulates <tt>Tool Bar</tt> controls on the view.
 *  
 *
 */
public class OpenXDataToolBar extends Composite implements ExtendedEventDispatcher {

	/** The tool bar buttons. */
	private PushButton btnCut;
	private PushButton btnCopy;
	private PushButton btnPaste;
	private PushButton btnLogout;
	private PushButton btnRefresh;
	private PushButton btnNewItem;
	private PushButton btnSameSize;
	private PushButton btnAlignTop;
	private PushButton btnSave;
	private PushButton btnOpenForm;
	private PushButton btnAlignLeft;
	private PushButton btnSameWidth;
	private PushButton btnMoveItemUp;
	private PushButton btnAlignRight;
	private PushButton btnSameHeight;
	private PushButton btnAddNewItem;
	private PushButton btnAlignBottom;
	private PushButton btnMoveItemDown;
	private PushButton btnAddNewChildItem;

	/** The button to export the report to PDF format. */
	private PushButton btnPdf;

	/** Label to display the name of the currently logged on user. */
	private Label lblTitleUser;

	/** Main widget for this tool bar. */
	private static HorizontalPanel panel;
	
	/** List of registered <tt>Event Listeners.</tt>*/
        private List<OpenXDataViewApplicationEventListener> viewApplicationEventListeners;

        /** Constructs an instance of this <tt>class.</tt>*/
        public OpenXDataToolBar() {
                setUp();
        }

    private PushButton createButton(ImageResource image) {
        return new PushButton(FormUtil.createImage(image));
    }

	/** Initialize <tt>Tool Bar widgets</tt> before binding them to the <tt>Tool Bar.</tt> */
	private  void setUp(){
		//Initialize Panel to hold widgets.
		panel = new HorizontalPanel();
        panel.setSpacing(3);
		createButtons();
		setButtonTitles();
		
		//Holds the Application Event Listeners registered on this class.
		viewApplicationEventListeners = new Vector<OpenXDataViewApplicationEventListener>();
		
		// Set up event listeners
		setupClickListeners();
		
		// Initialize widget.
		initWidget(panel);
	}

    private void createButtons() {
        OpenXDataImages images = GWT.create(OpenXDataImages.class);

        btnNewItem = createButton(images.newform());
        btnOpenForm = createButton(images.open());
        btnSave = createButton(images.save());
        btnAddNewItem = createButton(images.add());
        btnAddNewChildItem = createButton(images.addchild());

        btnMoveItemUp = createButton(images.moveup());
        btnMoveItemDown = createButton(images.movedown());

        btnAlignLeft = createButton(images.justifyleft());
        btnAlignRight = createButton(images.justifyright());
        btnAlignTop = createButton(images.alignTop());
        btnAlignBottom = createButton(images.alignBottom());
        btnSameWidth = createButton(images.samewidth());
        btnSameHeight = createButton(images.sameheight());
        btnSameSize = createButton(images.samesize());

        btnCut = createButton(images.cut());
        btnCopy = createButton(images.copy());
        btnPaste = createButton(images.paste());
        btnRefresh = createButton(images.refresh());

        btnPdf = createButton(images.pdf());
        btnLogout = createButton(images.logout());
    }

    private void setButtonTitles() {
        OpenXdataConstants constants = GWT.create(OpenXdataConstants.class);

        btnNewItem.setTitle(constants.label_new());
        btnOpenForm.setTitle(constants.label_open());
        btnSave.setTitle(constants.label_save());
        btnAddNewItem.setTitle(constants.label_add_new());
        btnAddNewChildItem.setTitle(constants.label_add_new_child());

        btnMoveItemUp.setTitle(constants.label_move_up());
        btnMoveItemDown.setTitle(constants.label_move_down());

        btnCut.setTitle(constants.label_cut());
        btnCopy.setTitle(constants.label_copy());
        btnPaste.setTitle(constants.label_paste());
        btnRefresh.setTitle(constants.label_refresh());
        btnAlignLeft.setTitle(constants.label_align_left());
        btnAlignRight.setTitle(constants.label_align_right());
        btnAlignTop.setTitle(constants.label_align_top());
        btnAlignBottom.setTitle(constants.label_align_botton());
        btnSameWidth.setTitle(constants.label_make_same_width());
        btnSameHeight.setTitle(constants.label_make_same_height());
        btnSameSize.setTitle(constants.label_make_same_size());
        btnPdf.setTitle(constants.label_exporttopdf());
        btnLogout.setTitle(constants.label_logout());
    }

	/**
	 * Sets up event listeners for the buttons on the tool bar UI
	 */
	private void setupClickListeners(){
		
		btnNewItem.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				notifyOnNewItemEventListeners();}});
		
		btnSave.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				notifyOnSaveEventListeners();}});
		
		btnRefresh.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				notifyOnRefreshEventListeners();}});
		
		btnAddNewItem.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				notifyOnNewItemEventListeners();}});
		
		btnAddNewChildItem.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				notifyOnNewChildItemEventListeners();}});
		
		btnOpenForm.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				notifyOnOpenEventListeners();}});
		
		btnMoveItemUp.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				notifyMoveItemUpEventListeners();}});
		
		btnMoveItemDown.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				notifyMoveItemDownEventListeners();}});
		
		btnAlignLeft.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				notifyAlignLeftEventListeners();}});
		
		btnAlignRight.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				notifyAlignRightEventListeners();}});
		
		btnAlignTop.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				notifyAlignTopEventListeners();}});
		
		btnAlignBottom.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				notifyAlignBottomEventListeners();}});
		
		btnCut.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				notifyCutItemEventListeners();}});
		
		btnCopy.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				notifyCopyItemEventListeners();}});
		
		btnPaste.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				notifyPasteItemEventListeners();}});
		
		btnSameWidth.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				notifyMakeSameWidthEventListeners();}});
		
		btnSameHeight.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				notifyMakeSameHeightEventListeners();}});
		
		btnSameSize.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				notifyMakeSameSizeEventListeners();}});
		
		
		btnPdf.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				notifyOnExportAsPdfEventListeners();}});
		
		btnLogout.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				fireEvent(new LogOutEvent());}
			
		});	
		
	}

	/**
	 * Sets the title on the <tt>Tool Bar</tt> to the currently logged in <tt>User.</tt>
	 */
	private void setCurrentlyLoggedOnUser(){
		String name = "";
		lblTitleUser = new Label();
		User user = Context.getAuthenticatedUser();
		
		if(user != null)
			name = user.getFullName().trim();
		
		if(panel.getWidgetIndex(lblTitleUser) < 0){
			panel.add(lblTitleUser);
			panel.setCellWidth(lblTitleUser,"100%");
			panel.setCellHorizontalAlignment(lblTitleUser,HasHorizontalAlignment.ALIGN_CENTER);
		}
		
		lblTitleUser.setText("OpenXData " + VersionInfo.VERSION + "  (User:" + name +")");
	}
	
	/**
	 * Refreshes the dash board and reloads data
	 */
	public void refresh(){
		MainViewControllerFacade.refreshData();
	}

	/**
	 * Utility method to bind specific add buttons
	 */
	private void bindMenuBarFunctionalButtons() {
		panel.add(btnNewItem);
		panel.add(btnOpenForm);
		panel.add(btnSave);
		panel.add(btnAddNewItem);
		panel.add(btnAddNewChildItem);
                panel.add(btnRefresh);
	}

	private void addClipboardButtons() {
		panel.add(btnCut);
		panel.add(btnCopy);
		panel.add(btnPaste);
		
		panel.add(btnRefresh);
	}

	private void addAlignmentButtons() {
		panel.add(btnMoveItemUp);
		panel.add(btnMoveItemDown);
		
		panel.add(btnAlignLeft);
		panel.add(btnAlignRight);
		panel.add(btnAlignTop);
		panel.add(btnAlignBottom);
		panel.add(btnSameWidth);
		panel.add(btnSameHeight);
		panel.add(btnSameSize);
		
		panel.add(btnRefresh);
	}
	
	private void addExtraButtons(){
		panel.add(btnPdf);
	}

    private void addLogoutButton() {
		panel.add(btnLogout);
	}
		
	/**
	 * Creates an administrative <tt>Tool Bar.</tt>
	 * 
	 * @return instance of {@link OpenXDataToolBar}
	 */
	public OpenXDataToolBar instanceOfAdminUser() {
		this.bindMenuBarFunctionalButtons();
		this.addClipboardButtons();
		this.addAlignmentButtons();
		this.addExtraButtons();
		
		this.addLogoutButton();
		
		this.setCurrentlyLoggedOnUser();
		
		return this;
	}

	/**
	 * Creates a <tt>Tool Bar</tt> according to <tt>User Permissions.</tt>
	 * 
	 * @return instance of {@link OpenXDataToolBar}
	 */
	public OpenXDataToolBar instanceOfUserPermissions() {
		
		if(RolesListUtil.getPermissionResolver().isAddPermission()){
			this.bindMenuBarFunctionalButtons();			
		}	
		
		if(RolesListUtil.getPermissionResolver().isEditPermission()){
			this.addClipboardButtons();
		}
                //changed from isViewPermission.
		if(RolesListUtil.getPermissionResolver().isExtraPermission("form_design")){
			this.addAlignmentButtons();
			this.addExtraButtons();
		}
		
		this.addLogoutButton();
		
		this.setCurrentlyLoggedOnUser();
		
		return this;
	}
	
		
	@Override
	public void registerApplicationEventListener(OpenXDataViewApplicationEventListener eventListener){
		viewApplicationEventListeners.add(eventListener);
	}
	
	@Override
	public void removeApplicationEventListener(OpenXDataViewApplicationEventListener eventListener){
		viewApplicationEventListeners.remove(eventListener);
	}
	
	@Override
	public void registerAdvancedApplicationEventListener(OpenXDataViewExtendedApplicationEventListener eventListener){
		viewApplicationEventListeners.add(eventListener);
	}
	
	@Override
	public void removeAdvancedApplicationEventListener(OpenXDataViewExtendedApplicationEventListener eventListener){
		viewApplicationEventListeners.remove(eventListener);
	}

	@Override
	public void notifyAlignBottomEventListeners() {
		for(OpenXDataViewApplicationEventListener xViewAppEventListener : viewApplicationEventListeners){
			if(xViewAppEventListener instanceof OpenXDataViewExtendedApplicationEventListener){			
				((OpenXDataViewExtendedApplicationEventListener)xViewAppEventListener).alignBottom();
			}

		}
	}

	@Override
	public void notifyAlignLeftEventListeners() {
		for(OpenXDataViewApplicationEventListener xViewAppEventListener : viewApplicationEventListeners){
			if(xViewAppEventListener instanceof OpenXDataViewExtendedApplicationEventListener){
				((OpenXDataViewExtendedApplicationEventListener)xViewAppEventListener).alignLeft();		
			}

		}		
	}

	@Override
	public void notifyAlignRightEventListeners() {
		for(OpenXDataViewApplicationEventListener xViewAppEventListener : viewApplicationEventListeners){
			if(xViewAppEventListener instanceof OpenXDataViewExtendedApplicationEventListener){
				((OpenXDataViewExtendedApplicationEventListener)xViewAppEventListener).alignRight();				
			}

		}
	}

	@Override
	public void notifyAlignTopEventListeners() {
		for(OpenXDataViewApplicationEventListener xViewAppEventListener : viewApplicationEventListeners){
			if(xViewAppEventListener instanceof OpenXDataViewExtendedApplicationEventListener){
				((OpenXDataViewExtendedApplicationEventListener)xViewAppEventListener).alignTop();				
			}

		}
	}

	@Override
	public void notifyCopyItemEventListeners() {
		for(OpenXDataViewApplicationEventListener xViewAppEventListener : viewApplicationEventListeners){
			if(xViewAppEventListener instanceof OpenXDataViewExtendedApplicationEventListener){
				
				((OpenXDataViewExtendedApplicationEventListener)xViewAppEventListener).copyItem();				
			}

		}
	}

	@Override
	public void notifyCutItemEventListeners() {
		for(OpenXDataViewApplicationEventListener xViewAppEventListener : viewApplicationEventListeners){
			if(xViewAppEventListener instanceof OpenXDataViewExtendedApplicationEventListener){
				((OpenXDataViewExtendedApplicationEventListener)xViewAppEventListener).cutItem();				
			}

		}
	}

	@Override
	public void notifyMakeSameHeightEventListeners() {
		for(OpenXDataViewApplicationEventListener xViewAppEventListener : viewApplicationEventListeners){
			if(xViewAppEventListener instanceof OpenXDataViewExtendedApplicationEventListener){
				((OpenXDataViewExtendedApplicationEventListener)xViewAppEventListener).makeSameHeight();				
				

			}

			}		

		}

	@Override
	public void notifyMakeSameSizeEventListeners() {
		for(OpenXDataViewApplicationEventListener xViewAppEventListener : viewApplicationEventListeners){
			if(xViewAppEventListener instanceof OpenXDataViewExtendedApplicationEventListener){
				((OpenXDataViewExtendedApplicationEventListener)xViewAppEventListener).makeSameSize();				
			}

		}
	}

	@Override
	public void notifyMakeSameWidthEventListeners() {
		for(OpenXDataViewApplicationEventListener xViewAppEventListener : viewApplicationEventListeners){
			if(xViewAppEventListener instanceof OpenXDataViewExtendedApplicationEventListener){
				((OpenXDataViewExtendedApplicationEventListener)xViewAppEventListener).makeSameWidth();				
			}

		}
	}

	@Override
	public void notifyMoveItemDownEventListeners() {
		for(OpenXDataViewApplicationEventListener xViewAppEventListener : viewApplicationEventListeners){
			if(xViewAppEventListener instanceof OpenXDataViewExtendedApplicationEventListener){
				((OpenXDataViewExtendedApplicationEventListener)xViewAppEventListener).moveItemDown();				
			}

		}
	}

	@Override
	public void notifyMoveItemUpEventListeners() {
		for(OpenXDataViewApplicationEventListener xViewAppEventListener : viewApplicationEventListeners){
			if(xViewAppEventListener instanceof OpenXDataViewExtendedApplicationEventListener){
				((OpenXDataViewExtendedApplicationEventListener)xViewAppEventListener).moveItemUp();				
			}

		}
	}

	@Override
	public void notifyOpenFormEventListeners() {
		for(OpenXDataViewApplicationEventListener xViewAppEventListener : viewApplicationEventListeners){
			if(xViewAppEventListener instanceof OpenXDataViewExtendedApplicationEventListener){
				((OpenXDataViewExtendedApplicationEventListener)xViewAppEventListener).openForm();				
			}
		}
	}

	
	@Override
	public void notifyPasteItemEventListeners() {
		for(OpenXDataViewApplicationEventListener xViewAppEventListener : viewApplicationEventListeners){
			if(xViewAppEventListener instanceof OpenXDataViewExtendedApplicationEventListener){
				((OpenXDataViewExtendedApplicationEventListener)xViewAppEventListener).pasteItem();				
			}

		}
	}

	@Override
	public void notifyOnExportEventListeners() {
		for(OpenXDataViewApplicationEventListener xViewAppEventListener : viewApplicationEventListeners){
			if(xViewAppEventListener instanceof OpenXDataViewExtendedApplicationEventListener){
				((OpenXDataExportImportApplicationEventListener)xViewAppEventListener).onExport();				
			}
		}	
	}

	@Override
	public void notifyOnFormatEventListeners() {
		for(OpenXDataViewApplicationEventListener xViewAppEventListener : viewApplicationEventListeners){
			if(xViewAppEventListener instanceof OpenXDataViewExtendedApplicationEventListener){
				((OpenXDataViewExtendedApplicationEventListener)xViewAppEventListener).format();				
			}
		}
	}

	@Override
	public void notifyOnImportEventListeners() {
		for(OpenXDataViewApplicationEventListener xViewAppEventListener : viewApplicationEventListeners){
			if(xViewAppEventListener instanceof OpenXDataExportImportApplicationEventListener){
				((OpenXDataExportImportApplicationEventListener)xViewAppEventListener).onImport();				
			}
		}
	}

	@Override
	public void notifyOnNewItemEventListeners() {
		for(OpenXDataViewApplicationEventListener xViewAppEventListener : viewApplicationEventListeners){
			xViewAppEventListener.onNewItem();
		}
	}

	@Override
	public void notifyOnOpenEventListeners() {
		for(OpenXDataViewApplicationEventListener xViewAppEventListener : viewApplicationEventListeners){
			if(xViewAppEventListener instanceof OpenXDataExportImportApplicationEventListener){
				((OpenXDataExportImportApplicationEventListener)xViewAppEventListener).onOpen();
			}
		}		
	}

	
	@Override
	public void notifyOnRefreshEventListeners() {
		for(OpenXDataViewApplicationEventListener xViewAppEventListener : viewApplicationEventListeners){
			xViewAppEventListener.onRefresh();
		}
	}

	@Override
	public void notifyOnSaveEventListeners() {
		for(OpenXDataViewApplicationEventListener xViewAppEventListener : viewApplicationEventListeners){
			xViewAppEventListener.onSave();
		}
	}

	@Override
	public void notifyOnNewChildItemEventListeners() {
		for(OpenXDataViewApplicationEventListener xViewAppEventListener : viewApplicationEventListeners){
			xViewAppEventListener.onNewChildItem();
		}
	}

	@Override
	public void notifyOnExportAsPdfEventListeners() {
		for(OpenXDataViewApplicationEventListener xViewAppEventListener : viewApplicationEventListeners){
			if(xViewAppEventListener instanceof OpenXDataExportImportApplicationEventListener){
				((OpenXDataExportImportApplicationEventListener)xViewAppEventListener).exportAsPdf();				
			}
		}
	}

	@Override
	public void notifyOnDeleteItemEventListeners() {
		for(OpenXDataViewApplicationEventListener xViewAppEventListener : viewApplicationEventListeners){
			xViewAppEventListener.onDeleteItem();
		}
	}

	@Override
	public void registerExportImportApplicationEventListener(OpenXDataExportImportApplicationEventListener eventListener) {
		viewApplicationEventListeners.add(eventListener);
		
	}

	@Override
	public void removeExportImportApplicationEventListener(OpenXDataExportImportApplicationEventListener eventListener) {
		viewApplicationEventListeners.remove(eventListener);
		
	}

}
