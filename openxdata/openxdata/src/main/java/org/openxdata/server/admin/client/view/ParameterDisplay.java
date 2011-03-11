package org.openxdata.server.admin.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.openxdata.server.admin.client.presenter.ParameterPresenter;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.event.EditableEvent;
import org.openxdata.server.admin.client.view.event.EventType;
import org.openxdata.server.admin.client.view.widget.OpenXDataButton;
import org.openxdata.server.admin.client.view.widget.OpenXDataFlexTable;
import org.openxdata.server.admin.model.TaskParam;

/**
 *
 * @author kay
 */
public class ParameterDisplay implements ParameterPresenter.Display {

    /** Table to arrange parameter names and their values. */
    private FlexTable table = new OpenXDataFlexTable();
    /** Main widget. */
    private VerticalPanel panel = new VerticalPanel();
    /** Button to add new parameters. */
    private Button addNewParameter;

    public ParameterDisplay() {
        addNewParameter = new OpenXDataButton(constants.label_add_parameter());
        table.setWidget(0, 0, new Label(constants.label_name()));
        table.setWidget(0, 1, new Label(constants.label_value()));
        table.setWidget(0, 2, new Label(constants.label_action()));

        FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
        cellFormatter.setWidth(0, 2, "10%");
        table.setStyleName("cw-FlexTable");

        panel.add(table);
        panel.add(addNewParameter);
        addNewParameter.setEnabled(false);

        Utilities.maximizeWidget(panel);
    }

    @Override
    public void addParameter(final TaskParam param) {
        final int row = table.getRowCount();

        final TextBox txtName = new TextBox();
        TextBox txtValue = new TextBox();

        txtName.setText(param.getName());
        txtValue.setText(param.getValue());

        table.setWidget(row, 0, txtName);
        table.setWidget(row, 1, txtValue);
        Button removeParameter = new OpenXDataButton(constants.label_remove());

        table.setWidget(row, 2, removeParameter);

        table.getFlexCellFormatter().setWidth(row, 2, "10%");
        table.getWidget(row, 0).setWidth("100%");
        table.getWidget(row, 1).setWidth("100%");

        removeParameter.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                panel.fireEvent(new EditableEvent<TaskParam>(param, EventType.DELETED));
                table.removeRow(row);
            }
        });
        txtName.addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                if (event.getValue().isEmpty()) return;
                param.setName(event.getValue());
                panel.fireEvent(new EditableEvent<TaskParam>(param));
            }
        });

        txtValue.addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                if (event.getValue().isEmpty()) return;

                param.setValue(event.getValue());
                panel.fireEvent(new EditableEvent<TaskParam>(param));

            }
        });
    }

    @Override
    public HasClickHandlers btnAdd() {
        return addNewParameter;
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    @Override
    public <H extends EventHandler> void addHandler(H handler, Type<H> type) {
        panel.addHandler(handler, type);
    }
}
