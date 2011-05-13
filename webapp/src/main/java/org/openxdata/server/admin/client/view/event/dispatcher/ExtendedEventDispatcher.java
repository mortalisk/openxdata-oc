/*
 *  Licensed to the OpenXdata Foundation (OXDF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The OXDF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, 
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 *  Copyright 2010 http://www.openxdata.org.
 */
package org.openxdata.server.admin.client.view.event.dispatcher;

import org.openxdata.server.admin.client.view.listeners.OpenXDataViewExtendedApplicationEventListener;

/**
 * Defines a contract for an Extended <tt>Event Dispatcher.</tt> 
 * <p>
 * Please review the {@link OpenXDataViewExtendedApplicationEventListener} contract.
 * </p>
 * @author Angel
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
