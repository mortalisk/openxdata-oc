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
 * This is an interface defines a contract for a member that intends to check if an object has data.
 * 
 * @author Angel
 */
public interface OnDataCheckListener {

	/**
	 * Member fired when data check has completed on the server.
	 * 
	 * @param hasData - boolean parameter indicating if data is present
	 * @param editableName - item name that the action is being performed on
	 */
	void onDataCheckComplete(boolean hasData, String editableName);
	
}
