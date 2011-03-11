package org.openxdata.server.admin.client.view.event;

import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;

/**
 *
 * @author kay
 */
public class EditableEvent<T> extends GwtEvent<EditableEvent.Handler<T>> {

    public interface Handler<T> extends BaseHandler<T> {

        public void onChange(T item);

        public void onCreated(T item);

        public void onDeleted(T item);

        public void onLoaded(List<T> items);
    }

    public static abstract class HandlerAdaptor<T> implements Handler<T> {

        @Override
        public void onChange(T item) {
        }

        @Override
        public void onCreated(T item) {
        }

        @Override
        public void onDeleted(T item) {
        }

        @Override
        public void onLoaded(List<T> items) {
        }
    }
    private T item;
    private EventType type;
    private List<T> items;
    private Class<?> clazz;

    public EditableEvent(T item) {
        this.item = item;
        type = EventType.EDITED;
        clazz = item.getClass();
    }

    public EditableEvent(T item, EventType type) {
        this.item = item;
        this.type = type;
        clazz = item.getClass();
    }

    public EditableEvent(List<T> items, Class<T> clazz) {
        this.items = items;
        this.clazz = clazz;
    }

    @Override
    protected void dispatch(Handler<T> handler) {
        if (items != null)
            handler.onLoaded(items);
        else if (type == EventType.DELETED)
            handler.onDeleted(item);
        else if (type == EventType.EDITED)
            handler.onChange(item);
        else if (type == EventType.CREATED)
            handler.onCreated(item);
    }

    @Override
    public Type<Handler<T>> getAssociatedType() {
        return EventRegistration.getType(EditableEvent.class, clazz);
    }

    public static <T> EventRegistration<T, EditableEvent.Handler<T>> addHandler(EventBus eventBus, Handler<T> handler) {
        return new EventRegistration<T, EditableEvent.Handler<T>>(eventBus, handler, EditableEvent.class);
    }
}
