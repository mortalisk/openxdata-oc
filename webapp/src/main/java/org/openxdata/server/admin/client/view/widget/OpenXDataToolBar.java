package org.openxdata.server.admin.client.view.widget;

import java.util.List;
import java.util.Vector;

import org.openxdata.server.admin.client.Context;
import org.openxdata.server.admin.client.VersionInfo;
import org.openxdata.server.admin.client.controller.facade.MainViewControllerFacade;
import org.openxdata.server.admin.client.internationalization.OpenXdataConstants;
import org.openxdata.server.admin.client.permissions.util.RolesListUtil;
import org.openxdata.server.admin.client.view.event.LogOutEvent;
import org.openxdata.server.admin.client.view.event.MobileInstallEvent;
import org.openxdata.server.admin.client.view.event.dispatcher.ExtendedEventDispatcher;
import org.openxdata.server.admin.client.view.images.OpenXDataImages;
import org.openxdata.server.admin.client.view.listeners.OpenXDataEventListener;
import org.openxdata.server.admin.client.view.listeners.OpenXDataViewApplicationEventListener;
import org.openxdata.server.admin.model.User;
import org.purc.purcforms.client.util.FormUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;

/**
 * 
 * Encapsulates <tt>Tool Bar</tt> controls on the view.
 *  
 *
 */
public class OpenXDataToolBar extends Composite implements ExtendedEventDispatcher {

	/** The tool bar buttons. */
	private PushButton btnLogout;
	private PushButton btnRefresh;
	private PushButton btnNewItem;
	private PushButton btnSave;
	private PushButton btnAddNewChildItem;
	private PushButton btnMobileInstaller;

	/** Label to display the name of the currently logged on user. */
	private Label lblTitleUser;

	/** Main widget for this tool bar. */
	private static HorizontalPanel panel;
	
	/** List of registered <tt>Event Listeners.</tt>*/
        private List<OpenXDataEventListener> viewApplicationEventListeners;

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
		viewApplicationEventListeners = new Vector<OpenXDataEventListener>();
		
		// Set up event listeners
		setupClickListeners();
		
		// Initialize widget.
		initWidget(panel);
	}

    private void createButtons() {
        OpenXDataImages images = GWT.create(OpenXDataImages.class);

        btnNewItem = createButton(images.newform());
        btnSave = createButton(images.save());
        btnAddNewChildItem = createButton(images.addchild());

        btnRefresh = createButton(images.refresh());
        
        btnMobileInstaller = createButton(images.mobileInstaller());
        
        btnLogout = createButton(images.logout());
        
    }

    private void setButtonTitles() {
        OpenXdataConstants constants = GWT.create(OpenXdataConstants.class);

        btnNewItem.setTitle(constants.label_new());
        btnSave.setTitle(constants.label_save());
        btnAddNewChildItem.setTitle(constants.label_add_new_child());

        btnRefresh.setTitle(constants.label_refresh());
        btnMobileInstaller.setTitle(constants.label_mobile_installer());
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
		
		btnAddNewChildItem.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				notifyOnNewChildItemEventListeners();}});
		
		btnMobileInstaller.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				fireEvent(new MobileInstallEvent());}
			
		});	
		
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
		panel.add(btnSave);
		panel.add(btnAddNewChildItem);
        panel.add(btnRefresh);
	}

	private void addMobileInstallerButton() {
		panel.add(btnMobileInstaller);
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
		
		this.addMobileInstallerButton();
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
		
		 if (RolesListUtil.getPermissionResolver().isExtraPermission(
	                "Perm_Mobile_Installer")) {
			 this.addMobileInstallerButton();
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
	public void notifyOnNewItemEventListeners() {
		for(OpenXDataEventListener xViewAppEventListener : viewApplicationEventListeners){
			if(xViewAppEventListener instanceof OpenXDataViewApplicationEventListener){
				((OpenXDataViewApplicationEventListener)xViewAppEventListener).onNewItem();
			}
		}
	}

	@Override
	public void notifyOnRefreshEventListeners() {
		for(OpenXDataEventListener xViewAppEventListener : viewApplicationEventListeners){
			if(xViewAppEventListener instanceof OpenXDataViewApplicationEventListener){
				((OpenXDataViewApplicationEventListener)xViewAppEventListener).onRefresh();
			}
		}
	}

	@Override
	public void notifyOnSaveEventListeners() {
		for(OpenXDataEventListener xViewAppEventListener : viewApplicationEventListeners){
			if(xViewAppEventListener instanceof OpenXDataViewApplicationEventListener){
				((OpenXDataViewApplicationEventListener)xViewAppEventListener).onSave();
			}
		}
	}

	@Override
	public void notifyOnNewChildItemEventListeners() {
		for(OpenXDataEventListener xViewAppEventListener : viewApplicationEventListeners){
			if(xViewAppEventListener instanceof OpenXDataViewApplicationEventListener){
				((OpenXDataViewApplicationEventListener)xViewAppEventListener).onNewChildItem();
			}
		}
	}

	@Override
	public void notifyOnDeleteItemEventListeners() {
		for(OpenXDataEventListener xViewAppEventListener : viewApplicationEventListeners){
			if(xViewAppEventListener instanceof OpenXDataViewApplicationEventListener){
				((OpenXDataViewApplicationEventListener)xViewAppEventListener).onDeleteItem();
			}
		}
	}

}
