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
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.PagingModelMemoryProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.ListField;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.core.client.GWT;

/**
 * UserAccessGrid is used to create a DualFieldList, with search and paging functionality.
 * A DualFieldList is two lists where items can be moved from one list to the other.
 * TODO: If this class is to be re-used elsewhere, it should be made generic (see DualFieldList for inspiration)
 */
public class UserAccessGrids extends FieldSet {

    private ListField<UserSummary> fromField = new ListField<UserSummary>();;
    private ListField<UserSummary> toField = new ListField<UserSummary>();;
    private List<UserSummary> leftList = new ArrayList<UserSummary>();
    private List<UserSummary> rightList = new ArrayList<UserSummary>();
    private List<User> temporarilyMappedItems = new ArrayList<User>();
    private List<User> tempItemsToUnmap = new ArrayList<User>();
    private int pageSize = 5;
    private PagingToolBar leftPagingToolBar = new SmallPagingToolBar(pageSize);
    private PagingToolBar rightPagingToolbar = new SmallPagingToolBar(pageSize);
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

        Button addUserBtn = new Button(appMessages.addUser());
        addUserBtn.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {
            	onButtonRight(be);
            	refreshToolbars();
            }
        });
        Button addAllUserBtn = new Button(appMessages.addAllUsers());
        addAllUserBtn.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {
            	onButtonAllRight(be);
            	refreshToolbars();
            }
        });

        Button removeUserBtn = new Button(appMessages.removeUser());
        removeUserBtn.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {
                onButtonLeft(be);
                refreshToolbars();
            }
        });
        Button removeAllUserBtn = new Button(appMessages.removeAllUsers());
        removeAllUserBtn.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {
                onButtonAllLeft(be);
                refreshToolbars();
            }
        });
        
        HorizontalPanel userTable = new HorizontalPanel();
        userTable.setVerticalAlign(VerticalAlignment.MIDDLE);
        //ContentPanel userTable = new ContentPanel();
        //HBoxLayout userTableLayout = new HBoxLayout();
        //userTableLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        //userTable.setLayout(userTableLayout);
        userTable.setBorders(false);
        
        userTable.add(createListPanel(appMessages.availableUsers(), leftList, fromField, leftPagingToolBar));
        
        VerticalPanel buttons = new VerticalPanel();
        buttons.setHorizontalAlign(HorizontalAlignment.CENTER);
        //LayoutContainer buttons = new LayoutContainer();
        //buttons.setLayout(new VBoxLayout(VBoxLayout.VBoxLayoutAlign.STRETCHMAX));
        buttons.setBorders(false);
        buttons.add(addUserBtn);
        buttons.add(addAllUserBtn);
        buttons.add(new Label(""));
        buttons.add(removeUserBtn);
        buttons.add(removeAllUserBtn);
        userTable.add(buttons);
        
        userTable.add(createListPanel(category, rightList, toField, rightPagingToolbar));

        add(userTable);
    }
    
    protected void onButtonAllLeft(ButtonEvent be) {
        buttonLeft(toField.getStore().getModels());
    }

    private void onButtonLeft(ButtonEvent be) {
    	buttonLeft(toField.getSelection());
    }
    
    /**
     * delete from right(to), add to left (from)
     */
    private void buttonLeft(List<UserSummary> sel) {
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

    protected void onButtonAllRight(ButtonEvent be) {
        buttonRight(fromField.getStore().getModels());
    }
    
    private void onButtonRight(ButtonEvent be) {
        buttonRight(fromField.getSelection());
	}
    
    /**
     * add to right(to), delete from left(from)
     */
    private void buttonRight(List<UserSummary> sel) {
    	toField.getStore().add(sel);
        rightList.addAll(sel);
        List<User> users = new ArrayList<User>();
        for (UserSummary summary : sel) {
            fromField.getStore().remove(summary);
            users.add(summary.getUser());
        }
        leftList.removeAll(sel);
        tempItemsToUnmap.removeAll(users);
        temporarilyMappedItems.addAll(users);
    }

    private ContentPanel createListPanel(String heading, List<UserSummary> userList, 
    		ListField<UserSummary> listField, PagingToolBar pagingToolBar) 
    {
        ContentPanel cp = new ContentPanel();
        cp.setHeading(heading);
        cp.setBorders(false);

        // add paging support for a local collection of models
        PagingModelMemoryProxy proxy = new PagingModelMemoryProxy(userList);
        PagingLoader<PagingLoadResult<ModelData>> loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);
        loader.setRemoteSort(true);
        loader.setSortField("name");
        loader.setSortDir(SortDir.ASC);
        ListStore<UserSummary> store = new ListStore<UserSummary>(loader);
        // filter to search for users
        StoreFilterField<UserSummary> filter = new StoreFilterField<UserSummary>() {
            @Override
            protected boolean doSelect(Store<UserSummary> store, UserSummary parent,
                    UserSummary record, String property, String filter) {
                String userName = record.getName().toLowerCase();
                if (userName.startsWith(filter.toLowerCase())) {
                    return true;
                }
                return false;
            }
        };
        filter.bind(store);
        filter.setEmptyText(appMessages.searchForAUser());
        filter.setWidth(185);
        
        pagingToolBar.bind(loader);
        
        LayoutContainer bottomComponent = new LayoutContainer();
        bottomComponent.setBorders(false);
        bottomComponent.add(filter);
        bottomComponent.add(pagingToolBar);
        cp.setBottomComponent(bottomComponent);

        loader.load(0, pageSize);

        listField.setStore(store);
        listField.setBorders(false);
        listField.setDisplayField("name");
        listField.setSize(185, 100);
        cp.setScrollMode(Scroll.AUTOY);
        cp.add(listField);

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
        leftPagingToolBar.refresh();
        rightPagingToolbar.refresh();
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
        getTempItemstoUnmap().clear();
        leftList.clear();
        rightList.clear();
    }
}
