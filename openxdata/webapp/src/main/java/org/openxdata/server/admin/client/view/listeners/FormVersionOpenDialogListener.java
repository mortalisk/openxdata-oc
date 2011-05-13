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
 * 
 * This is the <code>interface</code> defines a contract for members using <code>FormVersionOpenDialog</code>.
 * <p>
 * It returns the option selected by the 
 * <code>User</code> on the view to effect an action. 
 * An option can be <code>(Read only, Create New or Cancel)</code>.
 * </P>
 * 
 * @author Angel
 * 
 */
public interface FormVersionOpenDialogListener {
	
	/**
	 * 
	 * Method fired when an option has been selected on a widget.
	 * 
	 * @param option - the selected option from the widget
	 */
	public void onOptionSelected(int option);

}
