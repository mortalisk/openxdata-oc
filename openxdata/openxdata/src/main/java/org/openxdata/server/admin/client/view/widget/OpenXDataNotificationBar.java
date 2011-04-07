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

import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.widget.factory.OpenXDataWidgetFactory;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.inject.Inject;

/**
 * Bound to the <tt>MainView</tt> to display notifications to the <tt>User.</tt>
 * 
 * @author Angel
 *
 */
public class OpenXDataNotificationBar extends Composite {

        /** Horizontal Panel for organizing widgets.*/
        private HorizontalPanel panel;
        /** Handle to <tt>Widget Factory.</tt>*/
       
        private OpenXDataWidgetFactory widgetFactory;

        /**
         * Constructs an instance of this <tt>class.</tt>
         */
        @Inject
        public OpenXDataNotificationBar( OpenXDataWidgetFactory widgetFactory) {
                this.widgetFactory =widgetFactory;
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

                //Setting this text makes the notification bar to be displayed without text.
                Utilities.displayNotificationMessage(" ");

                //Align the text to center
                panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
                panel.setCellHorizontalAlignment(panel, HasHorizontalAlignment.ALIGN_CENTER);

                panel.setWidth("100%");
                panel.add(widgetFactory.getNotificationLabel());

                return panel;
        }
}
