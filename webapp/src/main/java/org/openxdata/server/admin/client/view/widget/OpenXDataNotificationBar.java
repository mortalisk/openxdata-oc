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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * Bound to the <tt>MainView</tt> to display notifications to the <tt>User.</tt>
 * 
 * @author Angel
 *
 */
public class OpenXDataNotificationBar extends Composite {

    /** Horizontal Panel for organizing widgets.*/
    private HorizontalPanel panel;
    OpenXDataLabel notificationLabel = new OpenXDataLabel(" ");

    /**
     * Constructs an instance of this <tt>class.</tt>
     */
    public OpenXDataNotificationBar() {
        setUp();
    }

    /**
     * Initializes this <tt>Widget.</tt>
     */
    private void setUp() {

        // Panel to hold notification Label
        panel = new HorizontalPanel();

        // Initialize widget.
        initWidget(getPanel());
    }

    /**
     * Returns the Panel for holding notification messages.
     *
     * @return the panel
     */
    public HorizontalPanel getPanel() {
        return constructPanel();
    }

    /**
     * Constructs the <tt>Panel</tt>
     *
     * @return instance of {@link HorizontalPanel} with <tt>Label.</tt>
     */
    HorizontalPanel constructPanel() {
        if (panel == null)
            panel = new HorizontalPanel();

        //Align the text to center
        panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        panel.setCellHorizontalAlignment(panel, HasHorizontalAlignment.ALIGN_CENTER);

        panel.setWidth("100%");
        panel.add(notificationLabel);

        return panel;
    }

    public void setText(String text) {
        notificationLabel.setText(text);
    }

    public void setFailureText(String errorMessage) {
        notificationLabel.setFailureText(errorMessage);
    }

    public void setDefaultText() {
        notificationLabel.setDefaultText();
    }
}
