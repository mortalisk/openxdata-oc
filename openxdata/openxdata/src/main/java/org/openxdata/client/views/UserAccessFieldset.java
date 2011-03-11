package org.openxdata.client.views;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.ListBox;
import java.util.ArrayList;
import java.util.List;
import org.openxdata.client.AppMessages;

/**
 * This class is used to paint usermapping to study or form
 * @author victor
 */
public class UserAccessFieldset extends FieldSet {

    private Button addAllUsersBtn;
    private Button addUserBtn;
    private Button removeAllUsersBtn;
    private Button removeUserBtn;
    private FlexTable userTable;
    private List<String> temporalyMappedItems = new ArrayList<String>();
    private List<String> tempItemsToUnmap = new ArrayList<String>();
    public ListBox allUsersListBox;
    public ListBox mappedUsersListBox;
    protected final AppMessages appMessages = GWT.create(AppMessages.class);

    public UserAccessFieldset() {
        init();
    }

    private void init() {

        setWidth(450);
        setHeading(appMessages.setUserAccessToStudy());
//        userAccessFieldSet.setCheckboxToggle(true);
        setCollapsible(true);
        setExpanded(false);
        allUsersListBox = new ListBox(true);
        allUsersListBox.setHeight("90px");
        allUsersListBox.setWidth("160px");
        mappedUsersListBox = new ListBox(true);
        mappedUsersListBox.setHeight("90px");
        mappedUsersListBox.setWidth("160px");
        addAllUsersBtn = new Button(appMessages.addAllUsers());
        addAllUsersBtn.addListener(Events.Select, new Listener<ButtonEvent>() {

            @Override
            public void handleEvent(ButtonEvent be) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                    @Override
                    public void execute() {
                        //add all users to the study
                    }
                });
            }
        });
        addUserBtn = new Button(appMessages.addUser());
        addUserBtn.addListener(Events.Select, new Listener<ButtonEvent>() {

            @Override
            public void handleEvent(ButtonEvent be) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                    @Override
                    public void execute() {
                        if (allUsersListBox.getSelectedIndex() > -1) {
                            mappedUsersListBox.addItem(allUsersListBox.getValue(allUsersListBox.getSelectedIndex()));
                            //add user to list of temporary mapped users
                            temporalyMappedItems.add(allUsersListBox.getValue(allUsersListBox.getSelectedIndex()));
                            allUsersListBox.removeItem(allUsersListBox.getSelectedIndex());
                        }
                    }
                });
            }
        });
        removeAllUsersBtn = new Button(appMessages.removeAllUsers());
        removeAllUsersBtn.addListener(Events.Select, new Listener<ButtonEvent>() {

            @Override
            public void handleEvent(ButtonEvent be) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                    @Override
                    public void execute() {
                        //
                    }
                });
            }
        });
        removeUserBtn = new Button(appMessages.removeUser());
        removeUserBtn.addListener(Events.Select, new Listener<ButtonEvent>() {

            @Override
            public void handleEvent(ButtonEvent be) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                    @Override
                    public void execute() {
                        if (mappedUsersListBox.getSelectedIndex() > -1) {
                            allUsersListBox.addItem(mappedUsersListBox.getValue(mappedUsersListBox.getSelectedIndex()));
                            //also remove it from the cashed unsaved maps
                            UnMapItem(mappedUsersListBox.getValue(mappedUsersListBox.getSelectedIndex()), temporalyMappedItems);
                            tempItemsToUnmap.add(mappedUsersListBox.getValue(mappedUsersListBox.getSelectedIndex()));
                            mappedUsersListBox.removeItem(mappedUsersListBox.getSelectedIndex());
                        }
                    }
                });
            }
        });
        userTable = new FlexTable();
        userTable.setWidget(0, 0, new Label(appMessages.allUsers()));
        userTable.setWidget(0, 2, new Label(appMessages.usersWithAccessToStudy()));
        userTable.setWidget(1, 0, allUsersListBox);
        userTable.getFlexCellFormatter().setRowSpan(1, 0, 4);
        userTable.getFlexCellFormatter().setWidth(1, 0, "150");
        userTable.getFlexCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
        userTable.setWidget(1, 1, addAllUsersBtn);
        userTable.setWidget(1, 2, mappedUsersListBox);
        userTable.getFlexCellFormatter().setRowSpan(1, 2, 4);
        userTable.getFlexCellFormatter().setWidth(1, 2, "150");
        userTable.getFlexCellFormatter().setVerticalAlignment(1, 2, HasVerticalAlignment.ALIGN_TOP);
        userTable.setWidget(2, 0, addUserBtn);
        userTable.setWidget(3, 0, removeAllUsersBtn);
        userTable.setWidget(4, 0, removeUserBtn);

        setLayout(new FormLayout());
        add(userTable);
    }

    public void addUnmappedUser(String name) {
        allUsersListBox.addItem(name);
    }

    public void addMappedUser(String name) {
        mappedUsersListBox.addItem(name);
    }

    public List<String> getTempMappedItems() {
        return temporalyMappedItems;
    }
    public List<String> getTempItemstoUnmap() {
        return tempItemsToUnmap;
    }
    public void UnMapItem(String userName, List<String> fromList) {
        for (int i = 0; i < fromList.size(); ++i) {
            if (fromList.get(i).toString().equals(userName)) {
                fromList.remove(userName);
            }
        }
    }

    public ListBox getUnmappedItemListbox() {
        return allUsersListBox;
    }

    public ListBox getMappedItemListbox(){
        return mappedUsersListBox;
    }
}
