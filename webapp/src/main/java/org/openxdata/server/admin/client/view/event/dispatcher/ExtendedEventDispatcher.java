package org.openxdata.server.admin.client.view.event.dispatcher;


/**
 * Defines a contract for an Extended <tt>Event Dispatcher.</tt> 
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
