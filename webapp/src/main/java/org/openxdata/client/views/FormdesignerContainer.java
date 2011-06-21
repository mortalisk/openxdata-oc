package org.openxdata.client.views;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import org.openxdata.server.admin.client.util.Utilities;

/**
 * Widget to display the formdesigner that is launched from gxt(Emit) interface.
 * This should resolve all window display issues that arise when trying to load
 * the Form designer in gxt windows
 */
public class FormdesignerContainer extends DialogBox {

    public FormdesignerContainer(Widget widget,String formName, final Widget emit) {
        setText(formName);
        Button closeButton = new Button("Close");
        closeButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                RootPanel.get().clear();
                RootPanel.get().remove(RootLayoutPanel.get());
                RootPanel.get().add(emit);
            }
        });
        setModal(true);
        FlexTable wrapper = new FlexTable();
        wrapper.setSize(Window.getClientWidth() + "px", Window.getClientHeight() + "px");
        wrapper.setWidget(0, 0, closeButton);
        wrapper.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        wrapper.getFlexCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_RIGHT);
        HorizontalPanel panel = new HorizontalPanel();
        Utilities.maximizeWidget(panel);
        panel.add(widget);
        Utilities.maximizeWidget(widget);
        wrapper.setWidget(1, 0, panel);
        wrapper.getFlexCellFormatter().setWidth(1, 0, "100%");
        setWidget(wrapper);

    }
}
