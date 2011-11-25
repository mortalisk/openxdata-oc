package org.openxdata.server.admin.client.view.event.dispatcher;

import org.openxdata.server.admin.client.view.listeners.OpenXDataViewApplicationEventListener;

/**
 * Defines a contract for <tt>Event Dispatcher.</tt> 
 * <p>
 * Please review the {@link OpenXDataViewApplicationEventListener} contract.
 * </p>
 *
 */
public interface EventDispatcher {
	

	/**
	 * Registers an {@link OpenXDataViewApplicationEventListener} to an <tt>Event Dispatcher.</tt>
	 * 
	 * @param eventListener <tt>OpenXDataViewApplicationEventListener</tt> to register.
	 */
	void registerApplicationEventListener(OpenXDataViewApplicationEventListener eventListener);
	
	/**
	 * Removes a given {@link OpenXDataViewApplicationEventListener} from the list of event listeners registered on an <tt>Event Dispatcher.</tt>
	 * 
	 * @param eventListener <tt>OpenXDataViewApplicationEventListener</tt> to de-register.
	 */
	void removeApplicationEventListener(OpenXDataViewApplicationEventListener eventListener);
	
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>onSave Event click.</tt>
	 */
	void notifyOnSaveEventListeners();
	
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>onRefresh Event click.</tt>
	 */
	void notifyOnRefreshEventListeners();
	
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>onNewItem Event click.</tt>
	 */
	void notifyOnNewItemEventListeners();

}
