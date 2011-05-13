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
package org.openxdata.server.admin.client.listeners;


/**
 * This interface is implemented for those who want to listen to events which
 * take place on the GWT stack panel. This interface was created because of the
 * inability of the stack panel to notify us when the selected panel changes.
 * And we therefore subclass it and use this interface to communicate with
 * whoever is interested in listening to panel selection change events.
 * 
 * @author daniel
 *
 */
public interface StackPanelListener {

	/**
	 * Called when the selected panel changes.
	 * 
	 * @param newIndex the index of the newly selected panel.
	 */
	void onSelectedIndexChanged(int newIndex);
}
