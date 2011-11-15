package org.openxdata.server.admin.client.view.event.dispatcher;

import org.openxdata.server.admin.client.view.listeners.OpenXDataViewExtendedApplicationEventListener;

/**
 * Defines a contract for an Extended <tt>Event Dispatcher.</tt> 
 * <p>
 * Please review the {@link OpenXDataViewExtendedApplicationEventListener} contract.
 * </p>
 *
 */
public interface ExtendedEventDispatcher extends EventDispatcher {
	
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>onNewChildItem Event click.</tt>
	 */
	void notifyOnNewChildItemEventListeners();
	
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>onDeleteItem Event click.</tt>
	 */
	void notifyOnDeleteItemEventListeners();

}
