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
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

/**
 *
 * @author victor
 */
public class UserAccessGrids extends FieldSet {

    private Grid<UserSummary> leftPanelGrid;
    private Grid<UserSummary> rightPanelGrid;
    private List<UserSummary> leftList = new ArrayList<UserSummary>();
    private List<UserSummary> rightList = new ArrayList<UserSummary>();
    private int pageSize = 5;
    private ListStore<UserSummary> userStore;
    private String itemTomap = "Study";
    private Button addUserBtn;
    private Button removeUserBtn;
    private HorizontalPanel userTable;
    private List<User> temporarilyMappedItems = new ArrayList<User>();
    private List<User> tempItemsToUnmap = new ArrayList<User>();
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
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                    @Override
                    public void execute() {
                        UserSummary summary = leftPanelGrid.getSelectionModel().getSelectedItem();
                        rightPanelGrid.getStore().add(summary);
                        temporarilyMappedItems.add(summary.getUser());
                        leftPanelGrid.getStore().remove(summary);
                        rightList.add(summary);
                        leftList.remove(getSelecetedIndex(summary, leftList));
                        refreshToolbars();
                    }
                });
            }
        });

        removeUserBtn = new Button(appMessages.removeUser());
        removeUserBtn.addListener(Events.Select, new Listener<ButtonEvent>() {

            @Override
            public void handleEvent(ButtonEvent be) {
                UserSummary summary = rightPanelGrid.getSelectionModel().getSelectedItem();
                leftPanelGrid.getStore().add(summary);
                UnMapItem(summary.getUser(), temporarilyMappedItems);
                tempItemsToUnmap.add(summary.getUser());
                rightPanelGrid.getStore().remove(summary);
                leftList.add(summary);
                rightList.remove(getSelecetedIndex(summary, rightList));
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
    private PagingToolBar leftGridToolBar;

    public ContentPanel leftGridPanel(ListStore<UserSummary> store, List<UserSummary> userList) {
        PagingLoader<PagingLoadResult<ModelData>> loader;
        ColumnModel cm;
        ContentPanel cp = new ContentPanel();
        cp.setHeading(appMessages.allUsers());
        cp.setBorders(false);

        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
        ColumnConfig column1 = new ColumnConfig("id", "id", 20);
        configs.add(column1);
        ColumnConfig column2 = new ColumnConfig("name", "Available users", 160);
        configs.add(column2);

        cm = new ColumnModel(configs);
        cm.setHidden(0, true);
        // add paging support for a local collection of models
        PagingModelMemoryProxy proxy = new PagingModelMemoryProxy(userList);
        // loader
        loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);
        loader.setRemoteSort(true);
        store = new ListStore<UserSummary>(loader);
//filter list
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
        leftPanelGrid = new Grid<UserSummary>(store, cm);
        leftPanelGrid.setAutoExpandColumn("name");
        leftPanelGrid.setHideHeaders(true);
        leftPanelGrid.setBorders(false);
        leftPanelGrid.setHeight(100);
        cp.setScrollMode(Scroll.AUTOY);
        cp.add(leftPanelGrid);

        return cp;
    }
    private PagingToolBar rightGridToolbar;

    public ContentPanel rightGridPanel(ListStore<UserSummary> store, List<UserSummary> userList) {
        PagingLoader<PagingLoadResult<ModelData>> loader;
        ColumnModel cm;
        ContentPanel cp = new ContentPanel();
        cp.setHeading(category);
        cp.setBorders(false);

        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
        ColumnConfig column1 = new ColumnConfig("id", "id", 20);
        configs.add(column1);
        ColumnConfig column2 = new ColumnConfig("name", "Users with Access To " + itemTomap, 160);
        configs.add(column2);

        cm = new ColumnModel(configs);
        cm.setHidden(0, true);
        // add paging support for a local collection of models
        PagingModelMemoryProxy proxy = new PagingModelMemoryProxy(userList);
        // loader
        loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);
        loader.setRemoteSort(true);
        store = new ListStore<UserSummary>(loader);

//filter list
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

        rightPanelGrid = new Grid<UserSummary>(store, cm);
        rightPanelGrid.setAutoExpandColumn("name");
        rightPanelGrid.setHideHeaders(true);
        rightPanelGrid.setBorders(false);
        rightPanelGrid.setHeight(100);
        cp.setScrollMode(Scroll.AUTOY);
        cp.add(rightPanelGrid);

        return cp;
    }

    public void addUnmappedUser(UserSummary user) {
        leftPanelGrid.getStore().add(user);
    }

    public void addMappedUser(UserSummary user) {
        rightPanelGrid.getStore().add(user);
    }

    public List<User> getTempMappedItems() {
        return temporarilyMappedItems;
    }
    public List<UserSummary> getLeftList() {
        return leftList;
    }
    public List<UserSummary> getRightList() {
        return rightList;
    }

    public List<User> getTempItemstoUnmap() {
        return tempItemsToUnmap;
    }

    public void UnMapItem(User user, List<User> fromList) {
        for (int i = 0; i < fromList.size(); ++i) {
            if (fromList.get(i).getName().equals(user.getName())) {
                fromList.remove(user);
            }
        }
    }

    public Grid<UserSummary> getUnmappedItemGrid() {
        return leftPanelGrid;
    }

    public Grid<UserSummary> getMappedItemGrid() {
        return rightPanelGrid;
    }

    public void refreshToolbars() {
        leftGridToolBar.refresh();
        rightGridToolbar.refresh();
    }

    public void updateLists(List<UserSummary> leftList, List<UserSummary> rightList) {
        this.leftList.addAll(leftList);
        this.rightList.addAll(rightList);
        refreshToolbars();
    }

    private int getSelecetedIndex(UserSummary item, List<UserSummary> items) {
        int index = -1;
        for (int i = 0; i < items.size(); ++i) {
            if (items.get(i).getId().equals(item.getId())) {
                index = i;
            }
        }
        return index;
    }

    public void clear() {
        getUnmappedItemGrid().getStore().removeAll();
        getMappedItemGrid().getStore().removeAll();
        getTempMappedItems().clear();
        getLeftList().clear();
        getRightList().clear();
    }
}
