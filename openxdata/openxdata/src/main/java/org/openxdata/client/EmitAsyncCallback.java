package org.openxdata.client;

import org.openxdata.client.controllers.LoginController;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.exception.OpenXDataSessionExpiredException;


import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Emit application AsynCallback that automatically handles errors that occur
 * 
 * @author dagmar@cell-life.org.za
 * @param <T>
 */
public abstract class EmitAsyncCallback<T> implements AsyncCallback<T> {
    
    AppMessages appMessages = GWT.create(AppMessages.class);
    
    /**
     * Method to override if there is some custom post exception handling work to be done.
     * Called after client has been notified of errors.
     * @param throwable
     */
    public void onFailurePostProcessing(Throwable throwable) {
    }

    /**
     * Implements some auto error handling for SpringSecurityException and general errors.
     */
    @Override
	final public void onFailure(Throwable throwable) {
        GWT.log("Error caught while performing an action on the server: "+throwable.getMessage(), throwable);
        if (throwable instanceof OpenXDataSessionExpiredException) {
            // allow the user to login again (show a login popup so they can continue where they left off)
            Dispatcher.get().dispatch(LoginController.LOGIN);
        } else if (throwable instanceof OpenXDataSecurityException) {
            // access denied
            MessageBox.alert(appMessages.error(), appMessages.accessDeniedError(), null);
        } else {
            // all other errors
            MessageBox.alert(appMessages.error(), appMessages.pleaseTryAgainLater(throwable.getMessage()), null);
        }
        onFailurePostProcessing(throwable);
    }
    

}