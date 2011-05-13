package org.openxdata.server.admin.client.view.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import org.openxdata.server.admin.client.view.listeners.OpenXDataViewApplicationEventListener;

/**
 *
 * @author kay
 */
public class ViewAppListenerChangeEvent extends GwtEvent<ViewAppListenerChangeEvent.Handler> {

    public interface Handler extends EventHandler {

        public void onChange(OpenXDataViewApplicationEventListener listener);
    }
    public static Type<Handler> TYPE = new Type<Handler>();
    private OpenXDataViewApplicationEventListener listener;

    public ViewAppListenerChangeEvent(OpenXDataViewApplicationEventListener listener) {
        this.listener = listener;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onChange(listener);
    }
}
