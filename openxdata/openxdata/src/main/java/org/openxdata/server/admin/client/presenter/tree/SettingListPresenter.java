package org.openxdata.server.admin.client.presenter.tree;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.openxdata.server.admin.client.controller.callback.OpenXDataAsyncCallback;
import org.openxdata.server.admin.client.controller.callback.SaveAsyncCallback;
import org.openxdata.server.admin.client.listeners.SaveCompleteListener;
import org.openxdata.server.admin.client.service.SettingServiceAsync;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.event.EditableEvent;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.Setting;
import org.openxdata.server.admin.model.SettingGroup;
import org.purc.purcforms.client.util.FormUtil;

/**
 *
 * @author kay
 */
public class SettingListPresenter extends BaseTreePresenter<SettingTreeWrapper, SettingListPresenter.Display> {

    public interface Display extends IBaseTreeDisplay<SettingTreeWrapper> {

        public static String diplayName = "Settings";
    }
    private SettingServiceAsync settingService;

    @Inject
    public SettingListPresenter(Display display, EventBus eventBus) {
        super(display, eventBus, SettingGroup.class);
        settingService = SettingServiceAsync.Util.getInstance();
        bindUI();
        bindHandlers();
    }

    private void bindUI() {
        if(!permissionResolver.isPermission(Permission.PERM_ADD_SETTINGS))
            return;
        display.addCommand("Add Child Group", new Command() {

            @Override
            public void execute() {
                addChildGroup();
            }
        }, Display.images.folder());
        display.addCommand("Add Setting", new Command() {

            @Override
            public void execute() {
                addItem(true);
            }
        }, Display.images.setting());

    }

    private void bindHandlers() {
        EditableEvent.addHandler(eventBus, new EditableEvent.HandlerAdaptor<Setting>() {

            @Override
            public void onChange(Setting item) {
                item.setDirty(true);
                display.update(new SettingTreeWrapper(item, null));
            }
        }).forClass(Setting.class);

    }

    private void addChildGroup() {
        SettingTreeWrapper selected = display.getSelected();
        SettingTreeWrapper wrapper = selected.addGroup();
        if (wrapper != null)
            addItem(wrapper);
    }

    @Override
    protected SettingTreeWrapper getNewItem() {
        return new SettingTreeWrapper(new SettingGroup("Setting Group"), null);
    }

    @Override
    protected SettingTreeWrapper getChild() {
        SettingTreeWrapper selected = display.getSelected();
        return selected.addChild();
    }

    @Override
    protected boolean canView() {
        return permissionResolver.isViewPermission(Permission.PERM_VIEW_SETTINGS);
    }

    @Override
    protected boolean canDelete() {
        SettingTreeWrapper selected = display.getSelected();
        if (selected != null)
            return selected.hasDeletePermission();
        return false;
    }

    @Override
    protected boolean canAdd() {
        return true;
    }

    @Override
    protected void loadItems() {
        Utilities.showProgress("Loading Settings...");
        settingService.getSettings(new OpenXDataAsyncCallback<List<SettingGroup>>() {

            @Override
            public void onSuccess(List<SettingGroup> result) {
                List<SettingTreeWrapper> list = new ArrayList<SettingTreeWrapper>();
                for (SettingGroup settingGroup : result) {
                    if (settingGroup.getParentSettingGroup() == null)
                        //Only add the items that have no parents coz
                        list.add(new SettingTreeWrapper(settingGroup, null));
                }
                setItems(list);
                FormUtil.dlg.hide();
            }
        });
    }

    @Override
    protected String getFailedMessage() {
        return "Failed";
    }

    @Override
    protected boolean saveDirtyItems(List<SettingTreeWrapper> dirtyItems, SaveAsyncCallback callback) {
        List<SettingGroup> groups = new ArrayList<SettingGroup>();
        for (SettingTreeWrapper settingTreeWrapper : dirtyItems) {
            groups.add((SettingGroup) settingTreeWrapper.getObject());
        }

        for (SettingGroup settingGroup : groups) {
            callback.setCurrentItem(settingGroup);
            settingService.saveSettingGroup(settingGroup, callback);
        }

        return true;
    }

    @Override
    protected void persistDelete(List<SettingTreeWrapper> deletedItems, SaveAsyncCallback callback) {
        for (SettingTreeWrapper treeWrap : deletedItems) {
            callback.setCurrentItem((Editable) treeWrap.getObject());
            if (treeWrap.hasSetting())
                settingService.deleteSetting((Setting) treeWrap.getObject(), callback);
            else
                settingService.deleteSettingGroup((SettingGroup) treeWrap.getObject(), callback);
        }
    }

    @Override
    protected String getSuccessMessage() {
        return "Success";
    }

    @Override
    protected SaveCompleteListener getSaveCompleteListener() {
        return new SaveCompleteListener() {

            @Override
            public void onSaveComplete(List<? extends Editable> modifiedList, List<? extends Editable> deletedList) {
                FormUtil.dlg.hide();
                if (Utilities.hasNewItems(modifiedList) || !deletedItems.isEmpty())
                    loadItems();
                else
                    Utilities.displayNotificationMessage(getSuccessMessage());
            }
        };
    }
}
