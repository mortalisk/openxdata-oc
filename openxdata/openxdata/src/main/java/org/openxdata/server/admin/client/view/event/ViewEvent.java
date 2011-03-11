package org.openxdata.server.admin.client.view.event;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author kay
 */
public class ViewEvent<T> extends GwtEvent<ViewEvent.Handler<T>> {

    public interface Handler<T> extends BaseHandler<T> {

        public void onView();
    }
    Class<T> item;

    public ViewEvent(Class<T> item) {
        this.item = item;
    }

    @Override
    public Type<Handler<T>> getAssociatedType() {
        return EventRegistration.getType(ViewEvent.class, item);
    }

    @Override
    protected void dispatch(Handler<T> handler) {
        GWT.log("Handling View Request: " + item);
        handler.onView();
    }

    public static <T> EventRegistration<T, ViewEvent.Handler<T>> addHandler(
            EventBus eventBus, Handler<T> handler) {
        return new EventRegistration<T, ViewEvent.Handler<T>>(eventBus,
                handler, ViewEvent.class);
    }
}
