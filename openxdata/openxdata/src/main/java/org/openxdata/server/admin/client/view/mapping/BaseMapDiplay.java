package org.openxdata.server.admin.client.view.mapping;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.server.admin.client.permissions.UIViewLabels;
import org.openxdata.server.admin.client.presenter.BaseMapPresenter;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.widget.OpenXDataButton;
import org.openxdata.server.admin.client.view.widget.OpenXDataFlexTable;
import org.openxdata.server.admin.client.view.widget.SimpleFilterListBox;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author kay
 */
public abstract class BaseMapDiplay<T> implements BaseMapPresenter.BaseDisplay<T> {

    protected Button btnAdd;
    protected Button btnRemove;
    protected Button btnSave;
    protected SimpleFilterListBox mappedObjectsListBox = new SimpleFilterListBox();
    protected List<T> systemItems = new ArrayList<T>();
    protected FlexTable table = new OpenXDataFlexTable();
    protected SimpleFilterListBox unMappedObjectsListBox = new SimpleFilterListBox();
    protected List<T> userItems = new ArrayList<T>();
    protected VerticalPanel vPanel = new VerticalPanel();

    public BaseMapDiplay() {
        init();
    }

    @Override
    public Widget asWidget() {
        return table;
    }

    @Override
    public HasClickHandlers btnAddRole() {
        return btnAdd;
    }

    @Override
    public HasClickHandlers btnRemoveRole() {
        return btnRemove;
    }

    @Override
    public HasClickHandlers buttonSave() {
        return btnSave;
    }

    protected abstract UIViewLabels getMapViewLabels();

    protected abstract String getName(T role);

    protected T getRoleWithName(List<T> rolse, String name) {
        for (T role : rolse) {
            if (getName(role).equals(name)) return role;
        }
        return null;
    }

    @Override
    public T getSystemItem() {
        int selectedIndex = unMappedObjectsListBox.getSelectedIndex();
        if (selectedIndex == -1) return null;
        String itemText = unMappedObjectsListBox.getItemText(selectedIndex);
        return getRoleWithName(systemItems, itemText);
    }

    @Override
    public T getUserItem() {
        int selectedIndex = mappedObjectsListBox.getSelectedIndex();
        if (selectedIndex == -1) return null;
        String itemText = mappedObjectsListBox.getItemText(selectedIndex);
        return getRoleWithName(userItems, itemText);
    }

    private void init() {
        btnAdd = new OpenXDataButton(getMapViewLabels().getMapButtonText());
        btnAdd.setWidth("160px");
        btnAdd.setTitle(getMapViewLabels().getAddButtonTitle());
        //Setting the properties of the Remove Button
        btnRemove = new OpenXDataButton(getMapViewLabels().getUnMapButtonText());
        btnRemove.setWidth("160px");
        btnRemove.setTitle(getMapViewLabels().getRemoveButtonTitle());
        vPanel.add(btnAdd);
        vPanel.add(btnRemove);
        vPanel.setSpacing(10);
        vPanel.setWidth("100%");
        //Maximize the mapping boxes.
        mappedObjectsListBox.setWidth("100%");
        unMappedObjectsListBox.setWidth("100%");
        mappedObjectsListBox.setVisibleItemCount(15);
        unMappedObjectsListBox.setVisibleItemCount(15);
        //Setting the properties of the Save Button.
        btnSave = new OpenXDataButton("Save");
        btnSave.setWidth("160px");
        btnSave.setTitle("Saves the individual Map that has just been made.");
        //Add widgets to the table
        table.setWidget(0, 0, new Label(getMapViewLabels().getRightListBoxLabel()));
        table.setWidget(1, 0, unMappedObjectsListBox);
        table.setWidget(1, 2, vPanel);
        table.setWidget(0, 3, new Label(getMapViewLabels().getLeftListBoxLabel()));
        table.setWidget(1, 3, mappedObjectsListBox);
        table.setWidget(2, 0, btnSave);
        FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
        cellFormatter.setWidth(0, 0, "50%");
        cellFormatter.setWidth(2, 3, "100%");
        cellFormatter.setColSpan(2, 3, 3);
        cellFormatter.setAlignment(2, 3, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE);
        //Maximizing the table
        Utilities.maximizeWidget(table);
    }

    @Override
    public void refresh() {
        mappedObjectsListBox.clear();
        unMappedObjectsListBox.clear();
        List<String> userItemStrings = renderUserMappedObjects();
        for (T role : systemItems) {
            if (!userItemStrings.contains(getName(role)))
                unMappedObjectsListBox.addItem(getName(role));
        }
    }

    private List<String> renderUserMappedObjects() {
        List<String> userItemStrings = new ArrayList<String>();
        for (T role : userItems) {
            mappedObjectsListBox.addItem(getName(role));
            userItemStrings.add(getName(role));
        }
        return userItemStrings;
    }

    @Override
    public void setSystemItems(List<T> roles) {
        this.systemItems = roles;
        refresh();
    }

    @Override
    public void setUserItems(List<T> roles) {
        this.userItems = roles;
        refresh();
    }
}
