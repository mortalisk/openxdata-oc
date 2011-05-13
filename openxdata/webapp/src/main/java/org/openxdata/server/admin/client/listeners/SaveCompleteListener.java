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

import java.util.List;
import org.openxdata.server.admin.model.Editable;

/**
 * Interface used to communicate to classes which want to listen
 * to the completion of saving of multiple items in a bulk.
 * This is called by the SaveAsyncCallback
 * 
 * @author daniel
 *
 */
public interface SaveCompleteListener {
	
	/**
	 * Called when the saving has completed.
	 * 
	 * @param modifiedList the list of items which were just edited or modified.
	 * @param deletedList the list of items which were deleted.
	 */
	public void onSaveComplete(List<? extends Editable> modifiedList, List<? extends Editable> deletedList);
}
