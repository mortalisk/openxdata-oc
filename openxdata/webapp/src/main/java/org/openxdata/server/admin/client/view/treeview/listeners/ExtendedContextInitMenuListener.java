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
 * 
 * Extends the contract of <code>IContextMenuInitListener</code>
 * to define a contract for <code>Objects</code> that intend to work with <code>Child Items</code>.
 * 
 * <p><code>E.g.
 * studies > forms > form versions, report groups > reports, Setting groups > setting
 * </code></p>
 * 
 * @author Angel
 * 
 */
public interface ExtendedContextInitMenuListener extends ContextMenuInitListener{

	/**
	 * Adds a new child item which is a child of the instance of the selected item on the tree view.
	 * The item can be a study, form, form version, user, role, settings, task, report or report group. 
	 * if a study is selected, a form will be created, if a form is selected, a form version will be created.
	 */
	void addNewChildItem();
}
