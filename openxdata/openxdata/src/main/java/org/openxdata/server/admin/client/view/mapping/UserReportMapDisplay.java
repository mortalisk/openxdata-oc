
package org.openxdata.server.admin.client.view.mapping;

import org.openxdata.server.admin.client.permissions.UIViewLabels;
import org.openxdata.server.admin.client.presenter.UserReportMapPresenter;
import org.openxdata.server.admin.model.Report;

/**
 *
 * @author kay
 */
public class UserReportMapDisplay extends BaseMapDiplay<Report> implements UserReportMapPresenter.Display{

    @Override
    protected UIViewLabels getMapViewLabels() {
         UIViewLabels labels = new UIViewLabels();
        labels.setLabel("User Reports");
        labels.setRightListBoxLabel("System Reports");
        labels.setLeftListBoxLabel("User Reports");
        labels.setMapButtonText("Add Report");
        labels.setUnMapButtonText("Remove Report");
        labels.setAddButtonTitle("Adds the selected Report to the User.");
        labels.setRemoveButtonTitle("Removes the selected Report from the User.");
        return labels;
    }

    @Override
    protected String getName(Report report) {
      return report.getName();
    }

}
