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
package org.openxdata.server.admin.client.view.bar;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.MenuBar;

/**
 * <code>Base class</code> for <code>Menu Bar widgets</code> in OpenXData
 * <code>overriding</code> the <code>onBrowserEvent()</code> to do custom checking of items.
 * 
 * @author Angel on Jan 15, 2010
 *
 */
public class OpenXDataBaseMenuBar extends MenuBar {
	
	/**
	 * Sole constructor. (For invocation by subclass constructors, typically implicit.)
	 * 
	 * @param <code>vertical</code> to determine vertical orientation of the menu bar.
	 * <p><code>if(vertical) orientation = vertical 
	 * <p>else vertical orientation = false</p>.</code></p>
	 */
	public OpenXDataBaseMenuBar(boolean vertical) {
		super(vertical);
	}

	/**
	 * <code>Overridden Method</code> fired for <code>events</code> 
	 * on the <code>browser</code> related to the <code>Menu Bar</code>.
	 */
	@Override
	public void onBrowserEvent(Event event) {
	
		//Ascertain if Menu Bar has items and then show
		//Otherwise, hide it to prevent IndexOutOfBounds Exception.
		if(this.getItems().size() > 0) {
			super.onBrowserEvent(event);
		}
		else {
			if(isVisible()) {
				this.setVisible(false);
			}
		}
	}
}
