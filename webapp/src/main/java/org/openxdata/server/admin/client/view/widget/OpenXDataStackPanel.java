package org.openxdata.server.admin.client.view.widget;

import org.openxdata.server.admin.client.Context;
import org.openxdata.server.admin.client.listeners.StackPanelListener;
import org.openxdata.server.admin.client.util.Utilities;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.DecoratedStackPanel;

/**
 * This is a custom extension of the standard GWT DecoratedStackPanel. The only
 * reason for extending this widget is to be able to know when the panel
 * selection changes.
 * 
 * @author daniel
 * 
 */
public class OpenXDataStackPanel extends DecoratedStackPanel {
	
	/** Listener to stack panel selection events. */
	private StackPanelListener stackPanelListener;
	
	/**
	 * Creates a new stack panel.
	 * 
	 * @param stackPanelListener
	 *            listener to stack panel selection events.
	 */
        //@Inject
	public OpenXDataStackPanel(StackPanelListener stackPanelListener) {
		this.stackPanelListener = stackPanelListener;
	}

        public OpenXDataStackPanel() {
	}
	
	@Override
	public void onBrowserEvent(Event event) {
		if (DOM.eventGetType(event) == Event.ONMOUSEDOWN) {
			if ((event.getButton() & Event.BUTTON_RIGHT) != 0) {
				if ("true".equalsIgnoreCase(Context.getSetting(
				        "disableBrowserContextMenu", "true")))
					Utilities.disableContextMenu(getElement());
			}
		}
		
		int prevIndex = getSelectedIndex();
		super.onBrowserEvent(event);
		if (prevIndex != getSelectedIndex())
			stackPanelListener.onSelectedIndexChanged(getSelectedIndex());
	}

    public void setStackPanelListener(StackPanelListener stackPanelListener) {
        this.stackPanelListener = stackPanelListener;
    }

        
}
