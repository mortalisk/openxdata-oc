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

import java.util.Date;
import java.util.List;

import org.openxdata.server.admin.client.Context;
import org.openxdata.server.admin.client.view.MainView;
import org.openxdata.server.admin.model.Editable;

import com.google.gwt.user.client.ui.RootPanel;

/**
 * Utility <code>Class</code> to encapsulate common functionality that cross
 * cuts across all <code>View Controllers.</code>
 * 
 * <P>
 * Any operation to be used by more than one <code>View Controller</code> should
 * be in this <code>Class.</code>
 * </P>
 * 
 * @author Angel
 * 
 */
public class MainViewControllerUtil {
	
	/**
	 * Gets the number of dirty items in a list.
	 * 
	 * @param list
	 *            the list.
	 * @return the number of dirty items.
	 */
	public static int getDirtyCount(List<? extends Editable> editables) {
		int count = 0;
		for (Editable editable : editables) {
			if (editable.isDirty())
				count++;
		}
		return count;
	}
	
	/**
	 * Sets the user who changed the item and the date when he or she did so.
	 * 
	 * @param editable
	 *            the item whose properties to set.
	 */
	public static void setEditableProperties(Editable editable) {
		if (!editable.isNew()) {
			editable.setDateChanged(new Date());
			editable.setChangedBy(Context.getAuthenticatedUser());
		}
	}
	
	/**
	 * Removes all progress message windows.
	 * <p>
	 * This function is only a temporary hack for the problem of main view
	 * widget's loading of <tt>Studies, Users, Settings </tt>etc at startup and
	 * we have no progress window displaying leaving the <tt>User</tt> with no
	 * clue as to what is happening.
	 * </p>
	 */
	public static void removeAllProgressWindows() {
		int size = RootPanel.get().getWidgetCount();
		for (int index = 0; index < size; index++) {
			if (!(RootPanel.get().getWidget(index) instanceof MainView)) {
				RootPanel.get().remove(index);
				index--;
				size--;
			}
		}
	}
	
	public static void onSaveComplete(List<? extends Editable> modifiedList,
	        List<? extends Editable> deletedList) {
		
		// Remove all items which have successfully been deleted from the
		// database (have no errors)
		for (int i = 0; i < deletedList.size(); i++) {
			if (!((Editable) deletedList.get(i)).hasErrors()) {
				deletedList.remove(i);
				i--;
			}
		}
		
		// If we have saved any new item, then we are going to reload the list
		// in order to get the assigned database id. This may required some
		// Optimizations of not requiring to reload all items but only the new.
		// But as for now, we have traded off performance for complexity, after
		// all the performance is still acceptable.
		boolean refresh = false;
		for (int i = 0; i < modifiedList.size(); i++) {
			if (!refresh && modifiedList.get(i).isNew()) {
				refresh = true;
				break;
			}
		}
	}
}
