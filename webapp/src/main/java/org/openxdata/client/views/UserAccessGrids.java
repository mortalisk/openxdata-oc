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
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

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
    private String gridHeight = "150px";
    private ListStore<UserSummary> userStore;
    private String itemTomap = "Study";
    private Button addUserBtn;
    private Button removeUserBtn;
    private FlexTable userTable;
    private List<User> temporalyMappedItems = new ArrayList<User>();
    private List<User> tempItemsToUnmap = new ArrayList<User>();
    protected final AppMessages appMessages = GWT.create(AppMessages.class);
    private String category = appMessages.usersWithAccessToStudy();

    public UserAccessGrids(String category) {
        this.category = category;
        init();
    }

    private void init() {

        setWidth(805);
        setHeading(appMessages.setUserAccessToStudy());
        if(category.equals(appMessages.usersWithAccessToForm()))
            setHeading(appMessages.setUserAccessToForm());
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
                        temporalyMappedItems.add(summary.getUser());
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
                UnMapItem(summary.getUser(), temporalyMappedItems);
                tempItemsToUnmap.add(summary.getUser());
                rightPanelGrid.getStore().remove(summary);
                leftList.add(summary);
                rightList.remove(getSelecetedIndex(summary, rightList));
                refreshToolbars();
            }
        });

        userTable = new FlexTable();
        userTable.setWidth("788");
        userTable.setCellSpacing(5);
        userTable.setWidget(0, 0, new Label(appMessages.allUsers()));
        userTable.setWidget(0, 2, new Label(category));
        userTable.setWidget(1, 0, leftGridPanel(userStore, leftList));
        userTable.getFlexCellFormatter().setRowSpan(1, 0, 4);
        userTable.getFlexCellFormatter().setWidth(1, 0, "300");
        userTable.getFlexCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
        userTable.getFlexCellFormatter().setAlignment(1, 1, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
        userTable.setWidget(1, 2, rightGridPanel(userStore, rightList));
        userTable.getFlexCellFormatter().setRowSpan(1, 2, 4);
        userTable.getFlexCellFormatter().setWidth(1, 2, "300");
        userTable.getFlexCellFormatter().setVerticalAlignment(1, 2, HasVerticalAlignment.ALIGN_TOP);
        userTable.setWidget(2, 0, addUserBtn);
        userTable.getFlexCellFormatter().setAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
        userTable.getFlexCellFormatter().setAlignment(3, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
        userTable.setWidget(4, 0, removeUserBtn);
        userTable.getFlexCellFormatter().setAlignment(4, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);

        setLayout(new FormLayout());
        add(userTable);
    }
    private PagingToolBar leftGridToolBar;

    public ContentPanel leftGridPanel(ListStore<UserSummary> store, List<UserSummary> userList) {
        PagingLoader<PagingLoadResult<ModelData>> loader;
        ColumnModel cm;
        ContentPanel cp = new ContentPanel();
        cp.setLayout(new FitLayout());
        cp.setBorders(false);
//        cp.setSize(600, 200); 

        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
        ColumnConfig column1 = new ColumnConfig("id", "id", 20);
        configs.add(column1);
        ColumnConfig column2 = new ColumnConfig("name", "Available users", 100);
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

        //toolbar on top of the grid to hold the filter
        ToolBar toolBar = new ToolBar();
        toolBar.add(new LabelToolItem("Search for a User: "));
        toolBar.add(filter);
        leftGridToolBar = new PagingToolBar(pageSize);
        leftGridToolBar.bind(loader);
        loader.load(0, pageSize);
        cp.setBottomComponent(leftGridToolBar);
        cp.setTopComponent(toolBar);
        leftPanelGrid = new Grid<UserSummary>(store, cm);
        leftPanelGrid.setAutoExpandColumn("name");
        leftPanelGrid.setHeight(gridHeight);
        leftPanelGrid.setBorders(true);
        cp.add(leftPanelGrid);

        return cp;
    }
    private PagingToolBar rightGridToolbar;

    public ContentPanel rightGridPanel(ListStore<UserSummary> store, List<UserSummary> userList) {
        PagingLoader<PagingLoadResult<ModelData>> loader;
        ColumnModel cm;
        ContentPanel cp = new ContentPanel();
        cp.setLayout(new FitLayout());
        cp.setBorders(false);

        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
        ColumnConfig column1 = new ColumnConfig("id", "id", 20);
        configs.add(column1);
        ColumnConfig column2 = new ColumnConfig("name", "Users with Access To " + itemTomap, 100);
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

        //toolbar on top of the grid to hold the filter
        ToolBar toolBar = new ToolBar();
        toolBar.add(new LabelToolItem("Search for a User: "));
        toolBar.add(filter);
        rightGridToolbar = new PagingToolBar(pageSize);
        rightGridToolbar.bind(loader);
        loader.load(0, pageSize);
        cp.setBottomComponent(rightGridToolbar);
        cp.setTopComponent(toolBar);

        rightPanelGrid = new Grid<UserSummary>(store, cm);
        rightPanelGrid.setAutoExpandColumn("name");
        rightPanelGrid.setHeight(gridHeight);
        rightPanelGrid.setBorders(true);
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
        return temporalyMappedItems;
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
