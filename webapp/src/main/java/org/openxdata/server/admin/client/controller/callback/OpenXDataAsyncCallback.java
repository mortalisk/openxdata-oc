package org.openxdata.server.admin.client.controller.callback;

import org.openxdata.server.admin.client.util.AsyncCallBackUtil;
import org.openxdata.server.admin.model.exception.OpenXDataDisabledUserException;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.exception.OpenXDataSessionExpiredException;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.InvocationException;
import org.purc.purcforms.client.util.FormUtil;

/**
 * This class wraps the GWT AsyncCallback to provide a specific
 * central management of failures in the application.
 * 
 * <p>
 * Motivation came from handling session timed out errors
 * and spring security exceptions.
 * </P>
 * 
 * <p>
 * A call with a typical use of <code>OpenXDataAsyncCallback</code> might look like
 * this:
 * 
 * <pre class="code">
 * service.getStudies(new OpenXDataAsyncCallback<List<StudyDef>>( {
 *   public void onSuccess(Shape[] result) {
 *     // It's always safe to downcast to the known return type. 
 *     studiesTreeView.loadStudies(result);
 *   }
 * 
 *   public void onOtherFailure(Throwable caught) {
 *     // Convenient way to find out which exception was thrown.
 *     try {
 *       throw caught;
 *     } catch (IncompatibleRemoteServiceException e) {
 *       // this client is not compatible with the server; cleanup and refresh the 
 *       // browser
 *     } catch (InvocationException e) {
 *       // the call didn't complete cleanly
 *     } catch (OpenXDataException e) {
 *       // one of the 'throws' from the original method
 *     } catch (Throwable e) {
 *       // last resort -- a very unexpected exception
 *     }
 *   }
 * });
 * </pre><b>
 * It is advisable to make asynchronous calls in a deferred command as we might not know before hand how many sessions are running.
 * </b></p>
 * 
 *@param <T> The type of the return value that was declared in the synchronous
 *          version of the method. If the return type is a primitive, use the
 *          boxed version of that primitive (for example, an <code>int</code>
 *          return type becomes an {@link Integer} type argument, and a
 *          <code>void</code> return type becomes a {@link Void} type
 *          argument, which is always <code>null</code>).
 *          
 *
 */
public abstract class OpenXDataAsyncCallback<T> implements AsyncCallback<T> {

    /**
     * Implements some auto error handling for SpringSecurityException.
     */
    @Override
    final public void onFailure(Throwable throwable) {

        if (throwable instanceof OpenXDataSessionExpiredException) {
            AsyncCallBackUtil.handleSessionTimeoutException(throwable);
        } else if (throwable instanceof OpenXDataSecurityException) {
            AsyncCallBackUtil.handleGenericOpenXDataException(throwable);
        } else if (throwable instanceof OpenXDataDisabledUserException) {
            AsyncCallBackUtil.handleGenericOpenXDataException(throwable);
        } else {
            FormUtil.dlg.hide();
            onOtherFailure(throwable);
        }
    }

    /**
     * Called when an <code>asynchronous call</code> fails to complete normally 
     * and the error is not security related in the context of <code>OpenXData</code>. 
     * <p>
     * <code>{@link IncompatibleRemoteServiceException}s, {@link InvocationException}s,</code> or 
     * <code>checked exceptions thrown</code> by the <code>service methods</code> are 
     * examples of the type of <code>failures</code> that can be passed to this <code>method.</code>
     * </p><p>
     * If caught is an instance of an {@link IncompatibleRemoteServiceException}
     * the application should try to get into a state where a browser refresh can be safely done.
     * </p>
     *
     * @param throwable Other failure encountered while executing a remote procedure call.
     */
    public void onOtherFailure(Throwable throwable) {
        AsyncCallBackUtil.handleGenericOpenXDataException(throwable);
    }
}
