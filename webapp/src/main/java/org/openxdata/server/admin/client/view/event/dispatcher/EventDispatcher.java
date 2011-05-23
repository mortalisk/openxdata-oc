package org.openxdata.server.admin.client.view.event.dispatcher;

import org.openxdata.server.admin.client.view.listeners.OpenXDataExportImportApplicationEventListener;
import org.openxdata.server.admin.client.view.listeners.OpenXDataViewApplicationEventListener;
import org.openxdata.server.admin.client.view.listeners.OpenXDataViewExtendedApplicationEventListener;

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
	 * Registers an {@link OpenXDataViewExtendedApplicationEventListener} to an <tt>Event Dispatcher.</tt>
	 * 
	 * @param eventListener <tt>OpenXDataViewAdvancedApplicationEventListener</tt> to register.
	 */
	void registerAdvancedApplicationEventListener(OpenXDataViewExtendedApplicationEventListener eventListener);
	
	/**
	 * Removes a given {@link OpenXDataViewExtendedApplicationEventListener} from the list of event listeners registered on an <tt>Event Dispatcher.</tt>
	 * 
	 * @param eventListener <tt>OpenXDataViewAdvancedApplicationEventListener</tt> to de-register.
	 */
	void removeAdvancedApplicationEventListener(OpenXDataViewExtendedApplicationEventListener eventListener);
	
	/**
	 * Registers an {@link OpenXDataExportImportApplicationEventListener} to an <tt>Event Dispatcher.</tt>
	 * 
	 * @param eventListener <tt>OpenXDataExportImportApplicationEventListener</tt> to register.
	 */
	void registerExportImportApplicationEventListener(OpenXDataExportImportApplicationEventListener eventListener);
	
	/**
	 * Removes a given {@link OpenXDataExportImportApplicationEventListener} from the list of event listeners registered on an <tt>Event Dispatcher.</tt>
	 * 
	 * @param eventListener <tt>OpenXDataExportImportApplicationEventListener</tt> to de-register.
	 */
	 void removeExportImportApplicationEventListener(OpenXDataExportImportApplicationEventListener eventListener);
	 
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>onSave Event click.</tt>
	 */
	void notifyOnSaveEventListeners();
	
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>onOpen Event click.</tt>
	 */
	void notifyOnOpenEventListeners();
		
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>onExport Event click.</tt>
	 */
	void notifyOnExportEventListeners();
	
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>onImport Event click.</tt>
	 */
	void notifyOnImportEventListeners();
	
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>format Event click.</tt>
	 */
	void notifyOnFormatEventListeners();
	
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>onRefresh Event click.</tt>
	 */
	void notifyOnRefreshEventListeners();
	
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>onNewItem Event click.</tt>
	 */
	void notifyOnNewItemEventListeners();

}
