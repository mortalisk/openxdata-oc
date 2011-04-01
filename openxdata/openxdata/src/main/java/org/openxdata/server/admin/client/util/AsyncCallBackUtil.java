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

import org.openxdata.server.admin.client.view.dialogs.ReLoginDialog;
import org.purc.purcforms.client.util.FormUtil;

/**
 * Utilities used by the <code>OpenXDataAsyncCallback</code>.
 * 
 * @author Angel
 *
 */
public class AsyncCallBackUtil {
	
	/**
	 * Keeps track of the last thrown <code>Exception.getLocalisedMessage()</code>.
	 */
	private static String throwableMessage = "";
	
	/**
	 * <code>Private constructor</code>
	 * to avoid <code>initialization</code> of this <code>Class</code>.
	 */
	private AsyncCallBackUtil(){}
	
	/**
	 * Sets up the dialog box and displays the message to the <code>User</code>.
	 * @param throwable - exception thrown.
	 */
	private static void showDialog(Throwable throwable) {
		Utilities.displayMessage(throwable.getLocalizedMessage());
	}
	
	/**
	 * Handles the <code>OpenXDataException.</code>
	 * 
	 * @param throwable <code>Exception</code> that has been thrown.
	 */
	public static void handleGenericOpenXDataException(Throwable throwable) {
		FormUtil.dlg.hide();  
		
		showDialog(throwable);
			
		AsyncCallBackUtil.throwableMessage = throwable.getLocalizedMessage();
	}

	/**
	 * Handles the <code>Session Time out exception.</code>
	 * <p>Displays a <code>Dialog</code> to allow the <code>User</code> to login again.
	 * <p>
	 * The <code>Exception</code> should be <code>instance of</code> <code>OpenXDataSessionExpiredException</code>.
	 * </p>
	 * @param throwable <code>Exception</code> that has been thrown.
	 */
	public static void handleSessionTimeoutException(Throwable throwable) {
		
		FormUtil.dlg.hide();  
		
		// Check if the last message is the one being thrown again.
		// This is to avoid the annoying many pop ups for the same Exception.
		if(throwableMessage.equals(throwable.getLocalizedMessage()))
			return;
		else{
			
			// Allow the user to login again 
			// (show a login pop up so they can continue where they left off)
			ReLoginDialog.instanceOfReLoginDialog("Session Timeout!").center();
		}
		
		AsyncCallBackUtil.throwableMessage = throwable.getLocalizedMessage();
	}
}
