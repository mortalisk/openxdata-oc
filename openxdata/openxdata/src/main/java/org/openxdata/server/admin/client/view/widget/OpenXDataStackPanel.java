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

import org.openxdata.server.admin.client.Context;
import org.openxdata.server.admin.client.listeners.StackPanelListener;
import org.openxdata.server.admin.client.util.Utilities;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.DecoratedStackPanel;
import com.google.inject.Inject;

/**
 * This is a custom extension of the standard GWT DecoratedStackPanel. The only
 * reason for extending this widget is to be able to know when the panel
 * selection changes.
 * 
 * @author daniel
 * 
 */
public class OpenXDataStackPanel extends DecoratedStackPanel {
	
	/** Listener to stack panel selection events. */
	private StackPanelListener stackPanelListener;
	
	/**
	 * Creates a new stack panel.
	 * 
	 * @param stackPanelListener
	 *            listener to stack panel selection events.
	 */
        //@Inject
	public OpenXDataStackPanel(StackPanelListener stackPanelListener) {
		this.stackPanelListener = stackPanelListener;
	}

        public OpenXDataStackPanel() {
	}
	
	@Override
	public void onBrowserEvent(Event event) {
		if (DOM.eventGetType(event) == Event.ONMOUSEDOWN) {
			if ((event.getButton() & Event.BUTTON_RIGHT) != 0) {
				if ("true".equalsIgnoreCase(Context.getSetting(
				        "disableBrowserContextMenu", "true")))
					Utilities.disableContextMenu(getElement());
			}
		}
		
		int prevIndex = getSelectedIndex();
		super.onBrowserEvent(event);
		if (prevIndex != getSelectedIndex())
			stackPanelListener.onSelectedIndexChanged(getSelectedIndex());
	}

    public void setStackPanelListener(StackPanelListener stackPanelListener) {
        this.stackPanelListener = stackPanelListener;
    }

        
}
