package org.openxdata.server.admin.client.util;

import org.openxdata.server.admin.client.view.widget.ReLoginDialog;
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
