package org.openxdata.server.admin.client.listeners;


/**
 * This interface is implemented for those who want to listen to events which
 * take place on the GWT stack panel. This interface was created because of the
 * inability of the stack panel to notify us when the selected panel changes.
 * And we therefore subclass it and use this interface to communicate with
 * whoever is interested in listening to panel selection change events.
 * 
 * @author daniel
 *
 */
public interface StackPanelListener {

	/**
	 * Called when the selected panel changes.
	 * 
	 * @param newIndex the index of the newly selected panel.
	 */
	void onSelectedIndexChanged(int newIndex);
}
