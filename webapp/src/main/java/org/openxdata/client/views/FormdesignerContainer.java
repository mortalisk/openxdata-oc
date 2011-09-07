package org.openxdata.client.views;

import org.openxdata.client.AppMessages;
import org.openxdata.client.Emit;
import org.openxdata.client.model.FormSummary;
import org.openxdata.server.admin.client.util.Utilities;
import org.purc.purcforms.client.FormDesignerWidget;
import org.purc.purcforms.client.locale.LocaleText;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Widget to display the formdesigner that is launched from gxt(Emit) interface.
 * This should resolve all window display issues that arise when trying to load
 * the Form designer in gxt windows
 */
public class FormdesignerContainer extends DialogBox {
	
	final AppMessages appMessages = GWT.create(AppMessages.class);

    public FormdesignerContainer(final FormDesignerWidget widget, String formName) {
        Button closeButton = new Button("Close");
        closeButton.addClickHandler(new ClickHandler() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(ClickEvent event) {
            	// would be nice to have some way to determine if there are unsaved changes in the formDesigner
            	MessageBox.confirm(appMessages.cancel(), LocaleText.get("cancelFormPrompt"), new Listener<MessageBoxEvent>() {
	    			@Override
	    			public void handleEvent(MessageBoxEvent be) {
	    				if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
			            	RootPanel.get().remove(FormdesignerContainer.this);
			            	((Viewport)Registry.get(Emit.VIEWPORT)).show();
			            	((Grid<FormSummary>)Registry.get(Emit.GRID)).getView().refresh(true);
	    				}
	    			};
            	});
            }
        });
        setModal(true);
        setSize(Window.getClientWidth() + "px", Window.getClientHeight() + "px");
        FlexTable wrapper = new FlexTable();
        wrapper.setSize(Window.getClientWidth()-20 + "px", Window.getClientHeight()-50 + "px");
        wrapper.setWidget(0, 0, new Label(formName));
        wrapper.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        wrapper.getFlexCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
        wrapper.setWidget(0, 1, closeButton);
        wrapper.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
        wrapper.getFlexCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_RIGHT);
        HorizontalPanel panel = new HorizontalPanel();
        Utilities.maximizeWidget(panel);
        panel.add(widget);
        wrapper.setWidget(1, 0, panel);
        wrapper.getFlexCellFormatter().setColSpan(1, 0, 2);
        wrapper.getFlexCellFormatter().setWidth(1, 0, "100%");
        setWidget(wrapper);

    }
}
