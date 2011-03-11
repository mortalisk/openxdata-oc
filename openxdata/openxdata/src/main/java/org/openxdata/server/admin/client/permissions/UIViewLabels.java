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
package org.openxdata.server.admin.client.permissions;

/**
 * Encapsulates the dynamic setting of the labels on the UI views like the Context Menu, Mapping dialogs.
 * 
 * @author Angel
 *
 */
public class UIViewLabels {
	
	private String label = "";
	private String addLabel = "";
	private String deleteLabel = "";
	private String mapButtonText = "";
	private String addButtonTitle = "";
	private String unMapButtonText = "";
	private String leftListBoxLabel = "";
	private String rightListBoxLabel = "";
	private String removeButtonTitle = "";
	private String addChildItemLabel = "";
	private String deleteChildItemLabel = "";
		
	public UIViewLabels(){}
	
	/**
	 *  Set the default add label for the context menu
	 * like Add User
	 * 
	 * @param addLabel label to set
	 */
	public void setAddLabel(String addLabel) {
		this.addLabel = addLabel;
	}
	
	/**
	 * Returns the add label set by the tree view
	 * 
	 * @return addLabel
	 */
	public String getAddLabel() {
		return addLabel;
	}
	
	/**
	 * Set the default delete label for the context menu
	 * like Delete User
	 * 
	 * @param deleteLabel delete label to set
	 */
	public void setDeleteLabel(String deleteLabel) {
		this.deleteLabel = deleteLabel;
	}
	
	/**
	 * Returns the default delete label set by tree view
	 * 
	 * @return deleteLabel
	 */
	public String getDeleteLabel() {
		return deleteLabel;
	}

	/**
	 * For context menus operating on child items, this sets the label for adding a child item
	 * like Add Form, Add Form Version on the study context menu
	 * 
	 * @param addChildItemLabel child item label
	 */
	public void setAddChildItemLabel(String addChildItemLabel) {
		this.addChildItemLabel = addChildItemLabel;
	}

	/**
	 * Returns the default add child item label set by the tree view
	 * 
	 * @return addChildItemLabel
	 */
	public String getAddChildItemLabel() {
		return addChildItemLabel;
	}

	/**
	 * For context menus operating on child items, this sets the label for deleting a child item
	 * like Delete Form, Delete Form Version on the study context menu
	 * 
	 * @param deleteChildItemLabel
	 */
	public void setDeleteChildItemLabel(String deleteChildItemLabel) {
		this.deleteChildItemLabel = deleteChildItemLabel;
	}

	/**
	 * For context menus operating on child items, this returns the label for deleting a child item
	 * like Delete Form, Delete Form Version on the study context menu
	 * 
	 * @return deleteChildItemLabel
	 */
	public String getDeleteChildItemLabel() {
		return deleteChildItemLabel;
	}

	/**
	 * @param leftListBoxLabel the leftListBoxLabel to set
	 */
	public void setLeftListBoxLabel(String leftListBoxLabel) {
		this.leftListBoxLabel = leftListBoxLabel;
	}

	/**
	 * @return the leftListBoxLabel
	 */
	public String getLeftListBoxLabel() {
		return leftListBoxLabel;
	}

	/**
	 * @param rightListBoxLabel the rightListBoxLabel to set
	 */
	public void setRightListBoxLabel(String rightListBoxLabel) {
		this.rightListBoxLabel = rightListBoxLabel;
	}

	/**
	 * @return the rightListBoxLabel
	 */
	public String getRightListBoxLabel() {
		return rightListBoxLabel;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Sets the <code>Button Text</code> for mapping an Item to the <code>User.</code>
	 * @param mapButtonText <code>Button Text to set.</code>
	 */
	public void setMapButtonText(String mapButtonText) {
		this.mapButtonText = mapButtonText;
		
	}
	
	/**
	 * Returns the <code>Map Button Text</code> for <code>buttons on mapping dialogs.</code>
	 * @return Set <code>Map Button Text.</code>
	 */
	public String getMapButtonText(){
		return mapButtonText;
	}

	/**
	 * Sets the <code>Button Text</code> for removing an Item from the <code>User.</code>
	 * @param unMapButtonText <code>Button Text to set.</code>
	 */
	public void setUnMapButtonText(String unMapButtonText) {
		this.unMapButtonText = unMapButtonText;
		
	}

	/**
	 * Returns the <code>UnMap Button Text</code> for <code>buttons on mapping dialogs.</code>
	 * @return Set <code>UnMap Button Text.</code>
	 */
	public String getUnMapButtonText() {
		return unMapButtonText;
	}

	/**
	 * Returns a title to be bound to the <tt>Add button</tt> on the <tt>Mapping View.</tt>
	 * @return title <tt>instanceof String.</tt>
	 */
	public String getAddButtonTitle() {
		return this.addButtonTitle;
	}
	
	/**
	 * Sets the title for <tt>Add Button.</tt>
	 * @param addButtonTitle title to set.
	 */
	public void setAddButtonTitle(String addButtonTitle){
		this.addButtonTitle = addButtonTitle;
	}
	
	/**
	 * Returns the title to be bound to the <tt>Remove button</tt> on the <tt>Mapping View.</tt?
	 * @return
	 */
	public String getRemoveButtonTitle(){
		return this.removeButtonTitle;
	}
	
	/**
	 * Sets the title for the <tt>Remove Button.</tt>
	 * @param removeButtonTitle title to set.
	 */
	public void setRemoveButtonTitle(String removeButtonTitle){
		this.removeButtonTitle = removeButtonTitle;
	}
}
