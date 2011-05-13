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
package org.openxdata.server.admin.client.util;

import org.openxdata.server.admin.client.Context;
import org.openxdata.server.admin.client.controller.callback.OpenXDataAsyncCallback;
import org.openxdata.server.admin.client.service.StudyManagerServiceAsync;
import org.openxdata.server.admin.client.view.listeners.OnDataCheckListener;
import org.openxdata.server.admin.client.view.listeners.OnDataReturnedListener;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.exception.OpenXDataException;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import org.purc.purcforms.client.util.FormUtil;

/**
 * Encapsulates the actions required to 
 * perform a data check on an <code>Editable</code> like <code>Study, Form or Form Version</code>.
 * 
 * @author Angel
 * 
 */
public class DataCheckUtil implements OnDataReturnedListener {
	
	/**
	 * The current instance of item being checked for data.
	 * Can be a study, form or form version.
	 */
	private String currentEditableName = null;
	
	/**
	 * Listener to listen when data is returned.
	 */
	private OnDataCheckListener dataListener;
	
	/**
	 * Constructor that takes the listener to be notified of data checking options.
	 * 
	 * @param dataListener listener to listen when data is returned.
	 */
	public DataCheckUtil(OnDataCheckListener dataListener){
		this.dataListener = dataListener;
	}

	/**
	 * member method to check for encapsulates which object it is that a check is being performed on.
	 * 
	 * The method also initiates the server call to check for data
	 */
	public void itemHasFormData(final Editable item){
		
		String progressMsg = null;
		
		if(item instanceof StudyDef){
			FormUtil.dlg.setText("Study Data Check");
			progressMsg = "Checking " + ((StudyDef)item).getName() + " Study for Data...";
			
			currentEditableName = ((StudyDef)item).getName() + " study";
		}
		else if(item instanceof FormDef){
			FormUtil.dlg.setText("Form Data Check");
			progressMsg = "Checking " + ((FormDef)item).getName() + " Form for Data...";
			
			currentEditableName = ((FormDef)item).getName() + " Form";
		}
		else if(item instanceof FormDefVersion){
			FormUtil.dlg.setText("Form Version Data Check");
			progressMsg = "Checking " + ((FormDefVersion)item).getName() + " Form version for Data...";
			
			currentEditableName = ((FormDefVersion)item).getName() + " Form Version";
		}
		
		FormUtil.dlg.center(progressMsg);
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				StudyManagerServiceAsync service = Context.getStudyManagerService();
				service.hasEditableData(item, new OpenXDataAsyncCallback<Boolean>() {

					@Override
					public void onOtherFailure(Throwable caught) {
						FormUtil.dlg.hide();
						Window.alert(caught.getLocalizedMessage());

					}
					
					@Override
					public void onSuccess(Boolean result) {
						try {
							dataReturned(result);
						} catch (OpenXDataException e) {
							onOtherFailure(e);
						}							
					}

				});

			}});
	}
	
	/**
	 * Receives the result from the server indicating if
	 * a particular <code>Editable</code> has data or not.
	 * 
	 * @param hasData Boolean indicating if <code>Editable</code> has data or not.
	 * 
	 * <p> if(hasData) == <code>Editable has data.</code></p>
	 * <p> if(!hasData) != <code>Editable does not have data</code>.</p>
	 * 
	 * @throws OpenXDataException <code>if(hasData) !discerned </code> internally.
	 * 
	 */
	@Override
	public void dataReturned(Boolean hasData) throws OpenXDataException {	
		dataListener.onDataCheckComplete(hasData, currentEditableName);
	}
}
