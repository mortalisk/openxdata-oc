package org.openxdata.server.admin.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import java.util.ArrayList;
import java.util.List;
import org.openxdata.server.admin.client.view.event.EditableEvent;
import org.openxdata.server.admin.client.view.event.ItemSelectedEvent;
import org.openxdata.server.admin.client.view.event.LoadRequetEvent;
import org.openxdata.server.admin.model.Editable;

/**
 *
 * @author kay
 */
public abstract class BaseMapPresenter<U extends Editable, T extends Editable> implements IPresenter<BaseMapPresenter.BaseDisplay<T>> {

    public interface BaseDisplay<T> extends WidgetDisplay {

        public HasClickHandlers buttonSave();

        public HasClickHandlers btnAddRole();

        public HasClickHandlers btnRemoveRole();

        public T getSystemItem();

        public T getUserItem();

        public void setSystemItems(List<T> roles);

        public void setUserItems(List<T> roles);

        public void refresh();
    }
    protected BaseDisplay<T> display;
    protected EventBus eventBus;
    private List<T> systemItems;
    protected U selectedItem;
    private Class<U> clazz;
    private Class<T> itemsToMapClass;

    public BaseMapPresenter(BaseDisplay<T> display, EventBus eventBus,
            Class<U> clazz, Class<T> otherClazz) {
        this.display = display;
        this.eventBus = eventBus;
        this.clazz = clazz;
        this.itemsToMapClass = otherClazz;
        bindUI();
        bindHandlers();
    }

    private void bindUI() {
        display.buttonSave().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                save();
            }
        });
        display.btnAddRole().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                mapToItem();
            }
        });
        display.btnRemoveRole().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                unMapItem();
            }
        });
    }

    private void bindHandlers() {
        EditableEvent.addHandler(eventBus, new EditableEvent.HandlerAdaptor<T>() {

            @Override
            public void onLoaded(List<T> items) {
                setSystemItemsToMap(items);
            }
        }).forClass(itemsToMapClass);
        ItemSelectedEvent.addHandler(eventBus, new ItemSelectedEvent.Handler<U>() {

            @Override
            public void onSelected(Composite sender, U item) {
                if (systemItems == null) loadOtherItems();
                setSelectedItem(item);
            }
        }).forClass(clazz);
    }

    private void mapToItem() {
        T systemItem = display.getSystemItem();
        if (systemItem != null && mapToItem(systemItem)) {
            selectedItem.setDirty(true);
            refreshDisplaySelectedItemsMap(selectedItem);
        }
    }

    private void unMapItem() {
        T userItem = display.getUserItem();
        if (userItem != null && unMapItem(userItem)) {
            selectedItem.setDirty(true);
            refreshDisplaySelectedItemsMap(selectedItem);
        }

    }

    protected void refreshDisplaySelectedItemsMap(U item) {
        display.setUserItems(getSelectedItemzMap(item));
    }

    protected void loadOtherItems() {
        eventBus.fireEvent(new LoadRequetEvent<T>(itemsToMapClass));
    }

    @Override
    public BaseDisplay<T> getDisplay() {
        return display;
    }

    protected void setSystemItemsToMap(List<T> itemsToMap) {
        this.systemItems = itemsToMap;
        display.setSystemItems(itemsToMap);
    }

    private void setSelectedItem(U selectedItem) {
        this.selectedItem = selectedItem;
        refreshDisplaySelectedItemsMap(selectedItem);
    }

    public List<T> getSystemItems() {
        if(systemItems == null)
            return new ArrayList<T>();
        return systemItems;
    }

    protected abstract boolean unMapItem(T itemMapped);

    protected abstract boolean mapToItem(T systemItem);

    protected abstract void save();

    abstract protected List<T> getSelectedItemzMap(U selectedItem);
}
