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
package org.openxdata.server.admin.client.view.listeners;

/**
 * Defines an extended contract for those views that intend to listen for advanced events like formatting, alignment and export/import.
 * <p>
 * Please review the {@link OpenXDataViewApplicationEventListener} contract.
 * </p>
 * 
 * @author Angel
 *
 */
public interface OpenXDataViewExtendedApplicationEventListener extends	OpenXDataViewApplicationEventListener {

	
	/**
	 * Aligns widgets on design surface to the left of the widget which was selected last.
	 */
	public void alignLeft();
	
	/**
	 * Aligns widgets on design surface to the right of the widget which was selected last.
	 */
	public void alignRight();
	
	/**
	 * Aligns widgets on design surface to the top of the widget which was selected last.
	 */
	public void alignTop();
	
	/**
	 * Makes widgets on design surface to be the same size as the widget which was selected last.
	 */
	public void makeSameSize();
	
	/**
	 * Exports a report to pdf format.
	 */
	public void exportAsPdf();
	
	/**
	 * Makes widgets on design surface to be the same height as the widget which was selected last.
	 */
	public void makeSameHeight();
	
	/**
	 * Makes widgets on design surface to be the same width as the widget which was selected last.
	 */
	public void makeSameWidth();
	
	/**
	 * Aligns widgets on design surface to the bottom of the widget which was selected last.
	 */
	public void alignBottom();
	
	/**
	 * Opens an existing XForm.
	 */
	public void openForm();
	
	/**
	 * Moves the selected item one position above.
	 */
	public void moveItemUp();
	
	/**
	 * Moves the selected item one position below.
	 */
	public void moveItemDown();
	
	/**
	 * Removes the selected item and copies it to the clip board.
	 */
	public void cutItem();
	
	/**
	 * Copies the selected item to the clip board.
	 */
	public void copyItem();
	
	/**
	 * Pastes the selected item from the clip board as a child of the selected item.
	 * If the clip board item is not a child of the selected item, then the paste
	 * command is ignored.
	 */
	public void pasteItem();
	
	/**
	 * Formats xml with easily readable indenting. The formated is what is currently 
	 * selected (Xforms source, layout xml, language xml or model xml
	 */
	public void format();
}
