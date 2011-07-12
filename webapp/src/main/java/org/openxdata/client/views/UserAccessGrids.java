/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openxdata.client.views;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.client.AppMessages;
import org.openxdata.client.model.UserSummary;
import org.openxdata.server.admin.model.User;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.PagingModelMemoryProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.ListField;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.core.client.GWT;

/**
 * UserAccessGrid is used to create a DualFieldList, with search and paging functionality.
 * A DualFieldList is two lists where items can be moved from one list to the other.
 */
public class UserAccessGrids extends FieldSet {

    private ListField<UserSummary> fromField;
    private ListField<UserSummary> toField;
    private List<UserSummary> leftList = new ArrayList<UserSummary>();
    private List<UserSummary> rightList = new ArrayList<UserSummary>();
    private ListStore<UserSummary> userStore;
    private List<User> temporarilyMappedItems = new ArrayList<User>();
    private List<User> tempItemsToUnmap = new ArrayList<User>();
    private int pageSize = 5;
    private PagingToolBar leftGridToolBar;
    private PagingToolBar rightGridToolbar;
    private Button addUserBtn;
    private Button removeUserBtn;
    private HorizontalPanel userTable;
    protected final AppMessages appMessages = GWT.create(AppMessages.class);
    private final String category;

    public UserAccessGrids(String category) {
        this.category = category;
        init();
    }

    private void init() {
    	setAutoWidth(true);
        setHeading(category);
        setCollapsible(true);
        setExpanded(false);

        addUserBtn = new Button(appMessages.addUser());
        addUserBtn.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {
            	onButtonRight(be);
            	refreshToolbars();
            }
        });

        removeUserBtn = new Button(appMessages.removeUser());
        removeUserBtn.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {
                onButtonLeft(be);
                refreshToolbars();
            }
        });
        
        userTable = new HorizontalPanel();
        userTable.setVerticalAlign(VerticalAlignment.MIDDLE);
        //HBoxLayout userTableLayout = new HBoxLayout();
        //userTableLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        //userTable.setLayout(userTableLayout);
        userTable.setBorders(false);
        userTable.add(leftGridPanel(userStore, leftList));
        
        VerticalPanel buttons = new VerticalPanel();
        buttons.setHorizontalAlign(HorizontalAlignment.CENTER);
        //ContentPanel buttons = new ContentPanel();
        //buttons.setLayout(new VBoxLayout(VBoxLayout.VBoxLayoutAlign.STRETCHMAX));
        //buttons.setHeaderVisible(false);
        buttons.setSpacing(10);
        buttons.setBorders(false);
        buttons.add(addUserBtn);
        buttons.add(removeUserBtn);
        userTable.add(buttons);
        userTable.add(rightGridPanel(userStore, rightList));

        add(userTable);
    }
    
    private void onButtonRight(ButtonEvent be) {
    	//add to right(to), delete from left(from)
        List<UserSummary> sel = fromField.getSelection();
        leftList.removeAll(sel);
        List<User> users = new ArrayList<User>();
        for (UserSummary summary : sel) {
            fromField.getStore().remove(summary);
            users.add(summary.getUser());
        }
        toField.getStore().add(sel);
        rightList.addAll(sel);
        tempItemsToUnmap.removeAll(users);
        temporarilyMappedItems.addAll(users);
        
	}
    
    private void onButtonLeft(ButtonEvent be) {
    	//delete from right(to), add to left (from)
    	List<UserSummary> sel = toField.getSelection();
    	rightList.removeAll(sel);
    	List<User> users = new ArrayList<User>();
    	for (UserSummary summary : sel) {
    		toField.getStore().remove(summary);
    		users.add(summary.getUser());
    	}
    	fromField.getStore().add(sel);
    	leftList.addAll(sel);
    	tempItemsToUnmap.addAll(users);
        temporarilyMappedItems.removeAll(users);
    }
    
    protected void onButtonAllLeft(IconButtonEvent be) {
        List<UserSummary> sel = toField.getStore().getModels();
        fromField.getStore().add(sel);
        toField.getStore().removeAll();
    }

    protected void onButtonAllRight(IconButtonEvent be) {
        List<UserSummary> sel = fromField.getStore().getModels();
        toField.getStore().add(sel);
        fromField.getStore().removeAll();
    }
    
    

    public ContentPanel leftGridPanel(ListStore<UserSummary> store, List<UserSummary> userList) {
        PagingLoader<PagingLoadResult<ModelData>> loader;
        ContentPanel cp = new ContentPanel();
        cp.setHeading(appMessages.allUsers());
        cp.setBorders(false);

        // add paging support for a local collection of models
        PagingModelMemoryProxy proxy = new PagingModelMemoryProxy(userList);
        // loader
        loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);
        loader.setRemoteSort(true);
        store = new ListStore<UserSummary>(loader);
        // filter list
        StoreFilterField<UserSummary> filter = new StoreFilterField<UserSummary>() {
            @Override
            protected boolean doSelect(Store<UserSummary> store, UserSummary parent,
                    UserSummary record, String property, String filter) {
                String userName = record.getName();
                if(userName.startsWith(filter.toLowerCase())){
                    return true;
                }
                return false;
            }
        };
        filter.bind(store);
        filter.setEmptyText("Search for a User");
        filter.setWidth(185);

        leftGridToolBar = new SmallPagingToolBar(pageSize);
        leftGridToolBar.bind(loader);
        leftGridToolBar.setEnableOverflow(true);
        loader.load(0, pageSize);
        LayoutContainer bottomComponent = new LayoutContainer();
        bottomComponent.setBorders(false);
        bottomComponent.add(filter);
        bottomComponent.add(leftGridToolBar);
        cp.setBottomComponent(bottomComponent);
        fromField = new ListField<UserSummary>();
        fromField.setDisplayField("name");
        fromField.setStore(store);
        fromField.setBorders(false);
        fromField.setSize(185, 100);
        cp.setScrollMode(Scroll.AUTOY);
        cp.add(fromField);

        return cp;
    }

    public ContentPanel rightGridPanel(ListStore<UserSummary> store, List<UserSummary> userList) {
        PagingLoader<PagingLoadResult<ModelData>> loader;
        ContentPanel cp = new ContentPanel();
        cp.setHeading(category);
        cp.setBorders(false);

        // add paging support for a local collection of models
        PagingModelMemoryProxy proxy = new PagingModelMemoryProxy(userList);
        // loader
        loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);
        loader.setRemoteSort(true);
        store = new ListStore<UserSummary>(loader);
        // filter list
        StoreFilterField<UserSummary> filter = new StoreFilterField<UserSummary>() {
            @Override
            protected boolean doSelect(Store<UserSummary> store, UserSummary parent,
                    UserSummary record, String property, String filter) {
                String userName = record.getName();
                if(userName.startsWith(filter.toLowerCase())){
                    return true;
                }
                return false;
            }
        };
        filter.bind(store);
        filter.setEmptyText("Search for a User");
        filter.setWidth(185);
        
        rightGridToolbar = new SmallPagingToolBar(pageSize);
        rightGridToolbar.bind(loader);
        
        LayoutContainer bottomComponent = new LayoutContainer();
        bottomComponent.setBorders(false);
        bottomComponent.add(filter);
        bottomComponent.add(rightGridToolbar);
        cp.setBottomComponent(bottomComponent);

        loader.load(0, pageSize);

        toField = new ListField<UserSummary>();
        toField.setStore(store);
        toField.setBorders(false);
        toField.setDisplayField("name");
        toField.setSize(185, 100);
        cp.setScrollMode(Scroll.AUTOY);
        cp.add(toField);

        return cp;
    }

    // FIXME: i would like to remove all these methods, and combine this class with the usermaputilities class (it's weird)
    public void addUnmappedUser(UserSummary user) {
        fromField.getStore().add(user);
    }

    public void addMappedUser(UserSummary user) {
        toField.getStore().add(user);
    }

    public List<User> getTempMappedItems() {
        return temporarilyMappedItems;
    }

    public List<User> getTempItemstoUnmap() {
        return tempItemsToUnmap;
    }

    private void refreshToolbars() {
        leftGridToolBar.refresh();
        rightGridToolbar.refresh();
    }

    public void updateLists(List<UserSummary> leftList, List<UserSummary> rightList) {
        this.leftList.addAll(leftList);
        this.rightList.addAll(rightList);
        refreshToolbars();
    }

    public void clear() {
        toField.getStore().removeAll();
        fromField.getStore().removeAll();
        getTempMappedItems().clear();
        leftList.clear();
        rightList.clear();
    }
}
