
package org.openxdata.server.admin.client.view.mapping;

import org.openxdata.server.admin.client.permissions.UIViewLabels;
import org.openxdata.server.admin.client.presenter.UserReportGroupMapPresenter;
import org.openxdata.server.admin.model.ReportGroup;

/**
 *
 * @author kay
 */
public class UserReportGroupMapDisplay extends BaseMapDiplay<ReportGroup> implements UserReportGroupMapPresenter.Display{

    @Override
    protected UIViewLabels getMapViewLabels() {
       UIViewLabels labels = new UIViewLabels();
        labels.setLabel("User Report Group");
        labels.setRightListBoxLabel("System Report Groups");
        labels.setLeftListBoxLabel("User Report Groups");
        labels.setMapButtonText("Add Report Groups");
        labels.setUnMapButtonText("Remove Report Group");
        labels.setAddButtonTitle("Adds the selected ReportGroup to the User.");
        labels.setRemoveButtonTitle("Removes the selected ReportGroup from the User.");
        return labels;
    }

    @Override
    protected String getName(ReportGroup role) {
        return role.getName();
    }

}
