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
package org.openxdata.server.admin.client.view.treeview.listeners;

/** 
 * Defines the contract for operations that can be done on an 
 * <code>Object</code> Like <p><code>Study, Form, Form version, user, role reports settings</code>
 * through the context menu that is loaded when the <code>User</code> right clicks on the <code>Objects.</code>
 * </P>
 * 
 * @author Angel
 * 
 */
public interface ContextMenuInitListener {
	
	/**
	 * Adds a new item which is an instance of the selected item on the tree view.
	 * The item can be a study, form, form version, user, role, settings, task, report or report group
	 */
	void addNewItem();
	
	/**
	 * Deletes a new item which is an instance of the selected item on the tree view.
	 * The item can be a study, form, form version, user, role, settings, task, report or report group
	 */
	void deleteSelectedItem();
}
