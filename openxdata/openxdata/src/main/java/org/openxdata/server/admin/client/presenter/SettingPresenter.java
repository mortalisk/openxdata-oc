package org.openxdata.server.admin.client.presenter;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;

import org.openxdata.server.admin.client.view.BasePropertyDisplay;
import org.openxdata.server.admin.client.view.event.EditableEvent;
import org.openxdata.server.admin.client.view.event.ItemSelectedEvent;
import org.openxdata.server.admin.client.view.event.PresenterChangeEvent;
import org.openxdata.server.admin.client.view.event.ViewEvent;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.Setting;
import org.openxdata.server.admin.model.SettingGroup;

/**
 *
 * @author kay
 */
public class SettingPresenter implements IPresenter<SettingPresenter.Display> {

    public interface Display extends BasePropertyDisplay.Interface {

        public String getSettingValue();

        public String getSettingName();

        public String getSettingDescription();

        public String getGroupName();

        public String getGroupDescription();

        public boolean hasSetting();

        public void setSetting(Setting setting);

        public void setGroup(SettingGroup group);

        public SettingGroup getGroup();

        public Setting getSetting();
    }
    private EventBus eventBus;
    private Display display;
    private SettingPresenter thisPresenter = this;

    @Inject
    public SettingPresenter(EventBus eventBus, Display display) {
        this.eventBus = eventBus;
        this.display = display;
        if (permissionResolver.isViewPermission(Permission.PERM_VIEW_SETTINGS)) {
            if (permissionResolver.isEditPermission(Permission.PERM_EDIT_SETTINGS)) {
                bindUI();
            } else {
                display.disableAll();
            }
        }else{
            display.showNoPermissionView();
        }
        bindHandlers();
    }

    private void bindUI() {
        display.addGeneralChangeHadler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                syncChange();
            }
        });
    }

    private void bindHandlers() {
        ItemSelectedEvent.addHandler(eventBus, new ItemSelectedEvent.Handler<SettingGroup>() {

            @Override
            public void onSelected(Composite sender, SettingGroup item) {
                display.setGroup(item);
            }
        }).forClass(SettingGroup.class);

        ItemSelectedEvent.addHandler(eventBus, new ItemSelectedEvent.Handler<Setting>() {

            @Override
            public void onSelected(Composite sender, Setting item) {
                display.setSetting(item);
            }
        }).forClass(Setting.class);

        ViewEvent.addHandler(eventBus, new ViewEvent.Handler<SettingGroup>() {

            @Override
            public void onView() {
                eventBus.fireEvent(new PresenterChangeEvent(thisPresenter));
            }
        }).forClass(SettingGroup.class);
    }

    private void syncChange() {
        if (display.hasSetting()) {
            Setting setting = display.getSetting();
            setting.setName(display.getSettingName());
            setting.setValue(display.getSettingValue());
            setting.setDescription(display.getSettingDescription());
            eventBus.fireEvent(new EditableEvent<Setting>(setting));
        } else {
            SettingGroup group = display.getGroup();
            group.setName(display.getGroupName());
            group.setDescription(display.getGroupDescription());
            eventBus.fireEvent(new EditableEvent<SettingGroup>(group));
        }
    }

    @Override
    public Display getDisplay() {
        return display;
    }
}
