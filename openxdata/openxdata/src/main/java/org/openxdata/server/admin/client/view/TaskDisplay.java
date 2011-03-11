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
        table.setWidget(0, 0, new Label(constants.label_name()));
        table.setWidget(1, 0, new Label(constants.label_description()));
        table.setWidget(2, 0, new Label(constants.label_class()));
        table.setWidget(3, 0, new Label(constants.label_started()));

        table.setWidget(0, 1, txtName);
        table.setWidget(1, 1, txtDescription);
        table.setWidget(2, 1, txtClass);
        table.setWidget(3, 1, lblStarted);

        txtName.setWidth("100%");
        txtDescription.setWidth("100%");
        txtClass.setWidth("100%");

        FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
        cellFormatter.setWidth(0, 0, "20%");
        cellFormatter.setColSpan(0, 1, 2);
        cellFormatter.setColSpan(1, 1, 2);
        cellFormatter.setColSpan(2, 1, 2);
        cellFormatter.setColSpan(3, 1, 2);

        table.getRowFormatter().removeStyleName(0, "FlexTable-Header");
        Utilities.maximizeWidget(table);

        tabs.add(table, constants.label_definition());

        Utilities.maximizeWidget(tabs);
        tabs.selectTab(0);
        super.setUpKeyHandlers();
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
        lblStarted.setText(taskDef.isRunning()+"");
    }
}
