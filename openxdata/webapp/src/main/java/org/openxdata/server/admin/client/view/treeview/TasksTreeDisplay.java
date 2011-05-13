package org.openxdata.server.admin.client.view.treeview;

import com.google.gwt.resources.client.ImageResource;
import org.openxdata.server.admin.client.presenter.tree.TasksListPresenter;
import org.openxdata.server.admin.model.TaskDef;

/**
 *
 * @author kay
 */
public class TasksTreeDisplay extends BaseTreeDisplay<TaskDef> implements TasksListPresenter.Display {

    @Override
    protected String getTooltip(TaskDef item) {
        String tooltip = item.getName()
                + (item.isRunning() ? " Running" : "");
        return tooltip;
    }

    @Override
    protected String getTreeName() {
        return diplayName;
    }

    @Override
    public String getDisplayLabel(TaskDef item) {
        return item.getName();
    }

    @Override
    protected ImageResource getImage(TaskDef item) {
        return images.markRead();
    }
}
