package org.openxdata.server.admin.client.view;

import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import org.openxdata.server.admin.client.presenter.TaskPresenter;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.model.TaskDef;

/**
 *
 * @author kay
 */
public class TaskDisplay extends BasePropertyDisplay implements TaskPresenter.Display {

    /** Widget for entering the name of the task. */
    private TextBox txtName = new TextBox();
    /** Widget for entering the description of the task. */
    private TextBox txtDescription = new TextBox();
    /** Widget for entering the class of the task. */
    private TextBox txtClass = new TextBox();
    /** Label to show whether a task is running or not. */
    private Label lblStarted = new Label("false");

    public TaskDisplay() {
        init();
    }

    private void init() {
        txtName = addTextProperty(constants.label_name());
        txtDescription = addTextProperty(constants.label_description());
        txtClass = addTextProperty(constants.label_class());
        table.setWidget(4, 0, new Label(constants.label_started()));
        table.setWidget(4, 1, lblStarted);
        super.init(constants.label_definition());

    }

    @Override
    public HasText getTxtName() {
        return txtName;
    }

    @Override
    public HasText getTxtDescription() {
        return txtDescription;
    }

    @Override
    public HasText getTxtClass() {
        return txtClass;
    }

    @Override
    public HasText getTxtStarted() {
        return lblStarted;
    }

    @Override
    public void setTaskDef(TaskDef taskDef) {
        if (taskDef == null) {
            clearTextBoxes();
            return;
        }
        txtClass.setText(taskDef.getTaskClass());
        txtName.setText(taskDef.getName());
        txtDescription.setText(taskDef.getDescription());
        lblStarted.setText(taskDef.isRunning() + "");
    }
}
