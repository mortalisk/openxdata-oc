
package org.openxdata.server.admin.client.view.mapping;

import org.openxdata.server.admin.client.permissions.UIViewLabels;
import org.openxdata.server.admin.client.presenter.UserFormMapPresenter;
import org.openxdata.server.admin.model.FormDef;

/**
 *
 * @author kay
 */
public class UserFormMapDisplay extends BaseMapDiplay<FormDef> implements UserFormMapPresenter.Display{

    @Override
    protected UIViewLabels getMapViewLabels() {
         UIViewLabels labels = new UIViewLabels();
        labels.setLabel("User Forms");
        labels.setRightListBoxLabel("System Forms");
        labels.setLeftListBoxLabel("User Forms");
        labels.setMapButtonText("Add Form");
        labels.setUnMapButtonText("Remove Form");
        labels.setAddButtonTitle("Adds the selected Form to the User.");
        labels.setRemoveButtonTitle("Removes the selected Form from the User.");
        return labels;
    }

    @Override
    protected String getName(FormDef role) {
       return role.getName();
    }

}
