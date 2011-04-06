package org.openxdata.server.admin.client.view.event;

import org.openxdata.server.admin.model.Editable;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Fire this Event if to load Give Item
 * @author kay
 */
public class LoadRequetEvent<T extends Editable> extends GwtEvent<LoadRequetEvent.Handler<T>> {

    private Class<T> clazz;

    public LoadRequetEvent(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    protected void dispatch(Handler<T> handler) {
        handler.onLoadRequest();
    }

    public interface Handler<T extends Editable> extends BaseHandler<T> {

        public void onLoadRequest();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Type<Handler<T>> getAssociatedType() {
        return EventRegistration.getType(LoadRequetEvent.class, clazz);
    }

    public static <T extends Editable> EventRegistration<T, LoadRequetEvent.Handler<T>> addHandler(EventBus eventBus, Handler<T> handler) {
        return new EventRegistration<T, LoadRequetEvent.Handler<T>>(eventBus, handler, LoadRequetEvent.class);
    }
}
