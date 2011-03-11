package org.openxdata.server.admin.client.presenter.tree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import java.util.ArrayList;
import java.util.List;
import org.openxdata.server.admin.client.controller.callback.SaveAsyncCallback;
import org.openxdata.server.admin.client.listeners.SaveCompleteListener;
import org.openxdata.server.admin.client.presenter.IPresenter;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.event.ItemSelectedEvent;
import org.openxdata.server.admin.client.view.event.EditableEvent;
import org.openxdata.server.admin.client.view.event.LoadRequetEvent;
import org.openxdata.server.admin.client.view.event.ViewAppListenerChangeEvent;
import org.openxdata.server.admin.client.view.event.ViewEvent;
import org.openxdata.server.admin.client.view.listeners.OpenXDataViewApplicationEventListener;
import org.openxdata.server.admin.client.view.treeview.listeners.ContextMenuInitListener;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.sharedlib.client.util.FormUtil;

/**
 *
 * @author kay
 */
public abstract class BaseTreePresenter<E extends Editable, D extends IBaseTreeDisplay<E>> implements IPresenter<D> {

    protected D display;
    protected List<E> items;
    protected List<E> deletedItems = new ArrayList<E>();
    protected EventBus eventBus;
    private final Class<E> clazz;

    public BaseTreePresenter(D Display, EventBus bus, Class<E> clazz) {
        this.display = Display;
        this.eventBus = bus;
        this.clazz = clazz;
        bindUI();
        bindHandlers();
    }

    private void bindHandlers() {
        ViewEvent.addHandler(eventBus, new ViewEvent.Handler<E>() {

            @Override
            public void onView() {
                loadItemIfNone();
            }
        }).forClass(clazz);

        EditableEvent.addHandler(eventBus, new EditableEvent.HandlerAdaptor<E>() {

            @Override
            public void onChange(E item) {
                item.setDirty(true);
                display.update(item);
            }
        }).forClass(clazz);

        LoadRequetEvent.addHandler(eventBus, new LoadRequetEvent.Handler<E>() {

            @Override
            public void onLoadRequest() {
                GWT.log("Handling Load request: " + clazz);
                loadItems();
            }
        }).forClass(clazz);
    }

    @SuppressWarnings("unchecked")
    private void bindUI() {
        display.getList().addSelectionHandler(new SelectionHandler() {

            @Override
            public void onSelection(SelectionEvent event) {
                eventBus.fireEvent(new ItemSelectedEvent<E>(display.getSelected()));
            }
        });

        display.addContextMenuHandler(new ContextMenuInitListener() {

            @Override
            public void addNewItem() {
                addItem();
            }

            @Override
            public void deleteSelectedItem() {
                deleteItem();
            }
        });

    }

    protected void deleteItem() {
        if (!canDelete()) return;
        if (!Window.confirm("Are you sure you want delete")) return;

        E item = display.getSelected();
        if (item == null) {
            Utilities.displayMessage("Please Select an Item");
            return;
        }
        if (beforeDeleteItem(item)) return;
        GWT.log("Base class did not beforeDeleteItem");
        if (!item.isNew())
            deletedItems.add(item);
        items.remove(item);
        display.delete(item);
    }

    protected void addItem() {
        if (!canAdd()) return;
        E item = getNewItem();
        items.add(item);
        display.add(item);
    }

    protected void loadItemIfNone() {
        //Fire event to listen to toolBar events
        eventBus.fireEvent(new ViewAppListenerChangeEvent(getAppListener()));
        if (!canView()) return;
        if (items == null || items.isEmpty())
            loadItems();
    }

    protected abstract E getNewItem();

    protected abstract boolean canView();

    protected abstract boolean canDelete();

    protected abstract boolean canAdd();

    protected abstract void loadItems();

    protected abstract String getFailedMessage();

    protected abstract boolean saveDirtyItems(List<E> dirtyItems, SaveAsyncCallback callback);

    protected abstract void persistDelete(List<E> deletedItems, SaveAsyncCallback callback);

    protected abstract String getSuccessMessage();

    protected void save() {

        List<E> dirtyEditables = Utilities.getDirtyEditables(items);
        final int count = dirtyEditables.size() + deletedItems.size();

        SaveAsyncCallback callback = new SaveAsyncCallback(count,
                getSuccessMessage(),
                getFailedMessage(), items, deletedItems,
                getSaveCompleteListener());

        if (count > 0) Utilities.showProgress("Saving...");

        if (saveDirtyItems(dirtyEditables, callback)) {//Delete if save was handled
            persistDelete(deletedItems, callback);
        } else {
            FormUtil.dlg.hide();
        }

    }

    private void refreshDisplay() {
        deletedItems.clear();
        display.setItems(new ArrayList<E>(items));
    }

    public void setItems(List<E> items) {
        this.items = items;
        refreshDisplay();
    }

    protected OpenXDataViewApplicationEventListener getAppListener() {
        return new OpenXDataViewApplicationEventListener() {

            @Override
            public void onSave() {
                save();
            }

            @Override
            public void onNewItem() {
                addItem();
            }

            @Override
            public void onDeleteItem() {
                deleteItem();
            }

            @Override
            public void onNewChildItem() {
                addItem();
            }

            @Override
            public void onRefresh() {
                if (canView()) loadItems();
            }
        };
    }

    private SaveCompleteListener getSaveCompleteListener() {
        return new SaveCompleteListener() {

            @Override
            public void onSaveComplete(List<? extends Editable> modifiedList,
                    List<? extends Editable> deletedList) {
                FormUtil.dlg.hide();
                if (Utilities.hasNewItems(modifiedList))
                    loadItems();
                else
                    Utilities.displayNotificationMessage(getSuccessMessage());
            }
        };
    }

    protected boolean beforeDeleteItem(final E selected) {
        return false;
    }

    @Override
    public D getDisplay() {
        return display;
    }
}
