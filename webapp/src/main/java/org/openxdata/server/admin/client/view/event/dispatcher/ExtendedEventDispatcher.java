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
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>alignLeft Event click.</tt>
	 */
	void notifyAlignLeftEventListeners();
	
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>alignRight Event click.</tt>
	 */
	public void notifyAlignRightEventListeners();
	
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>alignTop Event click.</tt>
	 */
	public void notifyAlignTopEventListeners();
	
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>makeSameSize Event click.</tt>
	 */
	public void notifyMakeSameSizeEventListeners();
	
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>makeSameHeight Event click.</tt>
	 */
	public void notifyMakeSameHeightEventListeners();
	
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>makeSameWidth Event click.</tt>
	 */
	public void notifyMakeSameWidthEventListeners();
	
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>alignBottom Event click.</tt>
	 */
	public void notifyAlignBottomEventListeners();
	
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>openForm Event click.</tt>
	 */
	public void notifyOpenFormEventListeners();
	
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>moveItemUp Event click.</tt>
	 */
	public void notifyMoveItemUpEventListeners();
	
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>moveItemDown Event click.</tt>
	 */
	public void notifyMoveItemDownEventListeners();
	
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>cutItem Event click.</tt>
	 */
	public void notifyCutItemEventListeners();
	
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>copyItem Event click.</tt>
	 */
	public void notifyCopyItemEventListeners();
	
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>pasteItem Event click.</tt>
	 */
	void notifyPasteItemEventListeners();
	
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>onNewChildItem Event click.</tt>
	 */
	void notifyOnNewChildItemEventListeners();
	
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>exportAsPdf Event click.</tt>
	 */
	void notifyOnExportAsPdfEventListeners();
	
	/**
	 * Notifies registered <tt>Event Listeners</tt> on the <tt>onDeleteItem Event click.</tt>
	 */
	void notifyOnDeleteItemEventListeners();

}
