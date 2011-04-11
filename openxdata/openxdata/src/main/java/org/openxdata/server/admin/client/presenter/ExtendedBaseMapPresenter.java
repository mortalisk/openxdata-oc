package org.openxdata.server.admin.client.presenter;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.server.admin.client.controller.callback.SaveAsyncCallback;
import org.openxdata.server.admin.client.listeners.SaveCompleteListener;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.event.EditableEvent;
import org.openxdata.server.admin.model.Editable;
import org.purc.purcforms.client.util.FormUtil;

import com.google.gwt.event.shared.EventBus;
import org.openxdata.server.admin.client.view.event.LoadRequetEvent;

/**
 *
 * @author kay
 */
public abstract class ExtendedBaseMapPresenter<M extends Editable, U extends Editable, T extends Editable> extends BaseMapPresenter<U, T> {

    protected List<M> maps;
    protected List<M> deletedMaps;
    private Class<M> mapClazz;

    public ExtendedBaseMapPresenter(BaseDisplay<T> display, EventBus eventBus,
            Class<U> clazz, Class<T> otherClazz, Class<M> mapClazz) {
        super(display, eventBus, clazz, otherClazz);
        this.mapClazz = mapClazz;
        deletedMaps = new ArrayList<M>();
        bindHandlers();
    }

    private void bindHandlers() {
        EditableEvent.addHandler(eventBus, new EditableEvent.HandlerAdaptor<M>() {

            @Override
            public void onLoaded(List<M> items) {
                maps = items;
            }
        }).forClass(mapClazz);
    }

    @Override
    protected List<T> getSelectedItemzMap(U user) {
        List<T> studies = getSystemItems();
        List<T> studiesWithUSer = new ArrayList<T>();
        for (T studyDef : studies) {
            if (isMappedToItem(user, studyDef)) {
                studiesWithUSer.add(studyDef);
            }
        }
        return studiesWithUSer;
    }

    @Override
    protected boolean mapToItem(T systemItem) {
        if (isMappedToItem(selectedItem, systemItem)) return false;
        M userStudyMap = searchMap(selectedItem.getId(), systemItem.getId(), deletedMaps);
        if (userStudyMap == null) {
            userStudyMap = createNewMap(selectedItem, systemItem);
            userStudyMap.setDirty(true);
        } else {
            deletedMaps.remove(userStudyMap);
        }
        maps.add(userStudyMap);
        return true;
    }

    @Override
    protected void save() {
        List<M> dirtyEditables = Utilities.getDirtyEditables(maps);
        int count = dirtyEditables.size() + deletedMaps.size();
        SaveAsyncCallback saveAsync = new SaveAsyncCallback(count, "Map Saved",
                "Save Failed", dirtyEditables, deletedMaps, new SaveCompleteListener() {

            @Override
            public void onSaveComplete(List<? extends Editable> modifiedList, List<? extends Editable> deletedList) {
                deletedMaps.clear();
                FormUtil.dlg.hide();
                onMapSaveComplete(modifiedList);
            }
        });
        if (count > 0) Utilities.showProgress("Saving..");
        for (M userStudyMap : dirtyEditables) {
            saveAsync.setCurrentItem(userStudyMap);
            persistSave(userStudyMap, saveAsync);
        }
        for (M userStudyMap : deletedMaps) {
            saveAsync.setCurrentItem(userStudyMap);
            persistDelete(userStudyMap, saveAsync);
        }
    }

    @Override
    protected boolean unMapItem(T userItem) {
        M userStudyMap = searchMap(selectedItem.getId(), userItem.getId(), maps);
        if (userStudyMap != null) {
            maps.remove(userStudyMap);
            if (!userStudyMap.isNew()) deletedMaps.add(userStudyMap);
            return true;
        }
        return false;
    }

    @Override
    protected void loadOtherItems() {
        super.loadOtherItems();
        eventBus.fireEvent(new LoadRequetEvent<M>(mapClazz));
    }



    abstract protected boolean isMappedToItem(U user, T itemToMap);

    abstract protected M createNewMap(U selectedItem, T systemItem);

    abstract protected void onMapSaveComplete(List<? extends Editable> mapsAdded);

    abstract protected void persistDelete(M userStudyMap, SaveAsyncCallback saveAsync);

    abstract protected void persistSave(M userStudyMap, SaveAsyncCallback saveAsync);

    abstract public M searchMap(int userId, int studyId, List<M> userStudyMaps);
}
