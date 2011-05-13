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
package org.openxdata.server.admin.client.view.widget;

import com.google.gwt.user.client.ui.FlexTable;

/**
 * <code>Extends GWT FlexTable</code<> to add default style names.
 * 
 * @author Angel
 *
 */
public class OpenXDataFlexTable extends FlexTable {
	
	/**
	 * Constructs an instance of this <tt>class.</tt>
	 */
	public OpenXDataFlexTable(){
		setDefaultStyleNames();
	}

	/**
	 * Prepares the table with default style names.
	 */
	private void setDefaultStyleNames() {
		this.addStyleName("FlexTable2");
		this.addStyleName("FlexTable2");
		
		this.setStylePrimaryName("FlexTable2");
		this.getRowFormatter().setStylePrimaryName(0, "FlexTable-Header");
	}
}
