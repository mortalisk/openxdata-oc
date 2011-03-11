package org.openxdata.server.admin.client.view.mapping;

import org.openxdata.server.admin.client.permissions.UIViewLabels;
import org.openxdata.server.admin.client.presenter.UserStudyMapPresenter;
import org.openxdata.server.admin.model.StudyDef;

/**
 *
 * @author kay
 */
public class UserStudyMapDisplay extends BaseMapDiplay<StudyDef> implements UserStudyMapPresenter.Display {

    @Override
    protected String getName(StudyDef role) {
        return role.getName();
    }

    @Override
    protected UIViewLabels getMapViewLabels() {
        UIViewLabels labels = new UIViewLabels();
        labels.setLabel("User Studies");
        labels.setRightListBoxLabel("System Studies");
        labels.setLeftListBoxLabel("User Studies");
        labels.setMapButtonText("Add Study");
        labels.setUnMapButtonText("Remove Study");
        labels.setAddButtonTitle("Adds the selected Study to the User.");
        labels.setRemoveButtonTitle("Removes the selected Study from the User.");
        return labels;
    }
}
