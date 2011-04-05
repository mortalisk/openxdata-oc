package org.openxdata.server.admin.client.presenter.tree;

import com.google.gwt.resources.client.ImageResource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.openxdata.server.admin.client.permissions.PermissionResolver;
import org.openxdata.server.admin.client.permissions.util.RolesListUtil;
import org.openxdata.server.admin.client.presenter.WidgetDisplay;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.Setting;
import org.openxdata.server.admin.model.SettingGroup;
import org.openxdata.server.admin.model.User;

/**
 *
 * @author kay
 */
public class SettingTreeWrapper extends SettingGroup implements TreeItemWrapper {

    private Setting setting;
    private SettingGroup settingGroup;
    private List<SettingTreeWrapper> wrappers = new ArrayList<SettingTreeWrapper>();
    private TreeItemWrapper parent;
    private PermissionResolver permissionResolver = RolesListUtil.getPermissionResolver();

    public SettingTreeWrapper(Setting setting, TreeItemWrapper parent) {
        this.setting = setting;
        this.parent = parent;
    }

    public SettingTreeWrapper(SettingGroup settingGroup, TreeItemWrapper parent) {
        this.settingGroup = settingGroup;
        this.parent = parent;
        populateChildren();
    }

    @Override
    public void addChild(TreeItemWrapper wrapper) {
        if (!isSetting(wrapper)) {
            return;
        }

        if (hasSetting()) {
            return;
        }
        SettingTreeWrapper tWrapper = (SettingTreeWrapper) wrapper;
        if (tWrapper.hasSetting()) {
            this.settingGroup.addSetting(tWrapper.setting);
            tWrapper.setting.setSettingGroup(settingGroup);
        } else {
            this.settingGroup.addSettingGroup(tWrapper.settingGroup);
            tWrapper.settingGroup.setParentSettingGroup(this.settingGroup);
        }
        wrappers.add(tWrapper);
        setDirty(true);
    }

    @Override
    public void removeChild(TreeItemWrapper wrapper) {
        if (!isSetting(wrapper)) {
            return;
        }
        if (hasSetting()) {
            return;
        }
        SettingTreeWrapper tWrapper = (SettingTreeWrapper) wrapper;
        if (tWrapper.hasSetting()) {
            settingGroup.removeSetting(tWrapper.setting);
        } else {
            settingGroup.getGroups().remove(tWrapper.settingGroup);
        }

        wrappers.remove(tWrapper);
        setDirty(true);

    }

    private void populateChildren() {
        List<Setting> settings = settingGroup.getSettings();
        for (Setting settin : settings) {
            this.wrappers.add(new SettingTreeWrapper(settin, this));
        }
        List<SettingGroup> groups = settingGroup.getGroups();
        for (SettingGroup group : groups) {
            this.wrappers.add(new SettingTreeWrapper(group, this));
        }

    }

    public SettingTreeWrapper addChild() {
        if (hasSetting())
            return null;
        Setting zetting = new Setting("Setting");
        SettingTreeWrapper treeWrapper = new SettingTreeWrapper(zetting, this);
        addChild(treeWrapper);
        return treeWrapper;
    }

    public SettingTreeWrapper addGroup() {
        if (hasSetting())
            return null;
        SettingTreeWrapper wrapper = new SettingTreeWrapper(new SettingGroup("Setting Group"), this);
        addChild(wrapper);
        return wrapper;
    }

    @Override
    public ImageResource getImage() {
        if (hasSetting())
            return WidgetDisplay.images.setting();
        return WidgetDisplay.images.folder();
    }

    @Override
    public String getName() {
        if (hasSetting()) {
            return setting.getName();
        } else {
            return settingGroup.getName();
        }
    }

    @Override
    public void setName(String name) {
        if (hasSetting()) {
            setting.setName(name);
        } else {
            settingGroup.setName(name);
        }
    }

    @Override
    public List<TreeItemWrapper> getChildren() {
        return new ArrayList(wrappers);
    }

    @Override
    public void setDirty(boolean dirty) {
        if (hasSetting()) {
            setting.setDirty(dirty);
        } else {
            settingGroup.setDirty(dirty);
        }
    }

    public boolean hasSetting() {
        return setting != null;
    }

    @Override
    public Object getObject() {
        if (hasSetting()) {
            return setting;
        }
        return settingGroup;
    }

    private boolean isSetting(TreeItemWrapper wrapper) {
        return wrapper instanceof SettingTreeWrapper;
    }

    @Override
    public boolean isNew() {
        if (hasSetting()) {
            return setting.isNew();
        }
        return settingGroup.isNew();
    }

    @Override
    public boolean isDirty() {
        if (hasSetting()) {
            return setting.isDirty();
        }
        return settingGroup.isDirty();
    }

    @Override
    public boolean hasErrors() {
        if (hasSetting()) {
            return setting.hasErrors();
        }
        return settingGroup.hasErrors();
    }

    @Override
    public void setHasErrors(boolean hasErrors) {
    }

    @Override
    public int getId() {
        if (hasSetting()) {
            return setting.getId();
        }
        return settingGroup.getId();
    }

    @Override
    public void setChangedBy(User changedBy) {
        if (hasSetting()) {
            setting.setChangedBy(changedBy);
        }
        settingGroup.setChangedBy(changedBy);
    }

    @Override
    public void setDateChanged(Date dateChanged) {
        if (hasSetting()) {
            setting.setDateChanged(dateChanged);
        }
        settingGroup.setDateChanged(dateChanged);
    }

    public boolean hasDeletePermission() {

        if (hasSetting()) {
            return permissionResolver.isDeletePermission(Permission.PERM_DELETE_SETTINGS);
        }
        return permissionResolver.isDeletePermission(Permission.PERM_DELETE_SETTINGSGROUP);
    }

    @Override
    public TreeItemWrapper getParent() {
        return parent;
    }

    @Override
    public boolean hasParent() {
        return parent != null;
    }

    @Override
    public String toString() {

        String type = hasSetting() ? "Setting" : "SettingGroup";
        return type + ":" + getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SettingTreeWrapper other = (SettingTreeWrapper) obj;
        if (this.setting != other.setting && (this.setting == null || !this.setting.equals(other.setting))) {
            return false;
        }
        if (this.settingGroup != other.settingGroup && (this.settingGroup == null || !this.settingGroup.equals(other.settingGroup))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.setting != null ? this.setting.hashCode() : 0);
        hash = 79 * hash + (this.settingGroup != null ? this.settingGroup.hashCode() : 0);
        return hash;
    }

    public boolean containDuplicates() {
        HashSet<String> settingNames = new HashSet<String>();
        HashSet<String> groupNames = new HashSet<String>();
        for (SettingGroup group : getAllGroups()) {
            if (groupNames.contains(group.getName()))
                return true;
            groupNames.add(group.getName());
        }

        for (Setting settng : gellAllSettings()) {
            if (settingNames.contains(settng.getName()))
                return true;
            settingNames.add(settng.getName());
        }
        return false;
    }

    public List<Setting> gellAllSettings() {
        List<Setting> settings = new ArrayList<Setting>();
        for (SettingTreeWrapper settingTreeWrapper : wrappers) {
            if (settingTreeWrapper.hasSetting())
                settings.add(settingTreeWrapper.setting);
            settings.addAll(settingTreeWrapper.gellAllSettings());
        }
        return settings;
    }

    public List<SettingGroup> getAllGroups() {
        List<SettingGroup> groups = new ArrayList<SettingGroup>();
        for (SettingTreeWrapper settingTreeWrapper : wrappers) {
            if (!settingTreeWrapper.hasSetting())
                groups.add(settingTreeWrapper.settingGroup);
            groups.addAll(settingTreeWrapper.getAllGroups());
        }
        return groups;
    }

    @Override
    public boolean contains(TreeItemWrapper otherWrapper) {
        if (!isSetting(otherWrapper) || otherWrapper == null) return false;

        for (SettingTreeWrapper thisWrapper : wrappers) {
            if (otherWrapper.equals(thisWrapper))
                return true;
            if (thisWrapper.contains(otherWrapper))
                return true;
        }
        return false;
    }
}
