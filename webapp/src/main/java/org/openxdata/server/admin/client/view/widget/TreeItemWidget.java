package org.openxdata.server.admin.client.view.widget;

import org.openxdata.server.admin.client.Context;
import org.openxdata.server.admin.client.util.Utilities;
import org.purc.purcforms.client.util.FormUtil;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * Widget for tree items which gives them a context menu.
 * 
 * @author daniel
 *
 */
public class TreeItemWidget extends Composite{

	/** Pop up for the context menu. */
	private PopupPanel popup;

	/**
	 * Creates a new tree item widget. 
	 * 
	 * @param imageProto the icon image.
	 * @param caption the display text.
	 * @param popup  the context menu popup.
	 */
	public TreeItemWidget(ImageResource imageProto, String caption, PopupPanel popup){

		this.popup = popup;

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setSpacing(0);

		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hPanel.add(FormUtil.createImage(imageProto));
		Widget headerText = new Label(caption);
		hPanel.add(headerText);

		initWidget(hPanel);
		DOM.sinkEvents(getElement(), DOM.getEventsSunk(getElement()) | Event.ONMOUSEDOWN );
	}

	@Override
	public void onBrowserEvent(Event event) {
		if (DOM.eventGetType(event) == Event.ONMOUSEDOWN) {
			if( (event.getButton() & Event.BUTTON_RIGHT) != 0){	 
				
				//This setting aids in acquiring the xpath expressions for study nodes
				//to be used as locators during selenium tests.
				if("true".equalsIgnoreCase(Context.getSetting("disableBrowserContextMenu", "true"))){
					popup.setPopupPosition(event.getClientX(), event.getClientY());
					Utilities.disableContextMenu(popup.getElement());
					popup.show();
					return;
				}
			}
		}
		
		super.onBrowserEvent(event);
	}
}
