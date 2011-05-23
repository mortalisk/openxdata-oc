package org.openxdata.server.admin.client.view.widget;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.MenuBar;

/**
 * <code>Base class</code> for <code>Menu Bar widgets</code> in OpenXData
 * <code>overriding</code> the <code>onBrowserEvent()</code> to do custom checking of items.
 * 
 *
 */
public class OpenXDataBaseMenuBar extends MenuBar {
	
	/**
	 * Sole constructor. (For invocation by subclass constructors, typically implicit.)
	 * 
	 * @param <code>vertical</code> to determine vertical orientation of the menu bar.
	 * <p><code>if(vertical) orientation = vertical 
	 * <p>else vertical orientation = false</p>.</code></p>
	 */
	public OpenXDataBaseMenuBar(boolean vertical) {
		super(vertical);
	}

	/**
	 * <code>Overridden Method</code> fired for <code>events</code> 
	 * on the <code>browser</code> related to the <code>Menu Bar</code>.
	 */
	@Override
	public void onBrowserEvent(Event event) {
	
		//Ascertain if Menu Bar has items and then show
		//Otherwise, hide it to prevent IndexOutOfBounds Exception.
		if(this.getItems().size() > 0) {
			super.onBrowserEvent(event);
		}
		else {
			if(isVisible()) {
				this.setVisible(false);
			}
		}
	}
}
