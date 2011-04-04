
package org.openxdata.server.admin.client.view.treeview;

import org.openxdata.server.admin.client.presenter.tree.SettingListPresenter;
import org.openxdata.server.admin.client.presenter.tree.SettingTreeWrapper;

/**
 *
 * @author kay
 */
public class SettingsTreeDisplay extends BaseTreeDisplay<SettingTreeWrapper> implements SettingListPresenter.Display{

    @Override
    protected String getTooltip(SettingTreeWrapper item) {
       return item.getName();
    }

    @Override
    protected String getTreeName() {
       return diplayName;
    }

    @Override
    public String getDisplayLabel(SettingTreeWrapper item) {
       return item.getName();
    }



}
