package org.openxdata.server.admin.client.view;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.TextBox;
import java.util.ArrayList;
import org.openxdata.server.admin.client.presenter.SettingPresenter;
import org.openxdata.server.admin.model.Setting;
import org.openxdata.server.admin.model.SettingGroup;

/**
 *
 * @author kay
 */
public class SettingDisplay extends BasePropertyDisplay implements SettingPresenter.Display {

    private TextBox txtSettingValue;
    private TextBox txtSettingName;
    private TextBox txtSettingDescription;
    private TextBox txtGroupName;
    private TextBox txtGroupDescription;
    private Setting setting;
    private SettingGroup group;
    private ArrayList<ValueChangeHandler<String>> handlers = new ArrayList<ValueChangeHandler<String>>();

    public SettingDisplay() {
        init();
    }

    private void init() {
        table.removeAllRows();
        if (hasSetting()) {
            initSettingText();
        } else {
            initGroupText();
        }
        super.init("Setting Properties");
    }

    private void initSettingText() {
        txtSettingName = addTextProperty(constants.label_name());
        txtSettingValue = addTextProperty(constants.label_value());
        txtSettingDescription = addTextProperty(constants.label_description());
        addHandlers();

    }

    private void initGroupText() {
        txtGroupName = addTextProperty(constants.label_name());
        txtGroupDescription = addTextProperty(constants.label_description());
        addHandlers();
    }

    @Override
    public void addGeneralChangeHadler(ValueChangeHandler<String> handler) {
        if (!handlers.contains(handler)) handlers.add(handler);
        super.addGeneralChangeHadler(handler);
    }

    private void addHandlers() {
        for (ValueChangeHandler<String> valueChangeHandler : handlers) {
            addGeneralChangeHadler(valueChangeHandler);
        }
    }

    @Override
    public String getSettingValue() {
        return txtSettingValue.getText();
    }

    @Override
    public String getSettingName() {
        return txtSettingName.getText();
    }

    @Override
    public String getSettingDescription() {
        return txtSettingDescription.getText();
    }

    @Override
    public String getGroupName() {
        return txtGroupName.getText();
    }

    @Override
    public String getGroupDescription() {
        return txtGroupDescription.getText();
    }

    @Override
    public boolean hasSetting() {
        return setting != null;
    }

    @Override
    public void setSetting(Setting setting) {
        this.setting = setting;
        this.group = null;
        init();
        txtSettingName.setText(setting.getName());
        txtSettingValue.setText(setting.getValue());
        txtSettingDescription.setText(setting.getDescription());
    }

    @Override
    public void setGroup(SettingGroup group) {
        this.group = group;
        this.setting = null;
        init();
        txtGroupName.setText(group.getName());
        txtGroupDescription.setText(group.getDescription());
    }

    @Override
    public SettingGroup getGroup() {
        return group;
    }

    @Override
    public Setting getSetting() {
        return setting;
    }
}
