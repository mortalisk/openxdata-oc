package org.openxdata.server.admin.client.view.event;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Composite;

/**
 * 
 * @author kay
 */
public class ItemSelectedEvent<T> extends GwtEvent<ItemSelectedEvent.Handler<T>> {

    public interface Handler<T> extends BaseHandler<T> {

        public void onSelected(Composite sender, T item);
    }
    private T item;

    public ItemSelectedEvent(T item) {
        this.item = item;
    }

    @SuppressWarnings("unchecked")
	@Override
    public Type<Handler<T>> getAssociatedType() {
        return EventRegistration.getType(ItemSelectedEvent.class, item.getClass());
    }

    @Override
    protected void dispatch(Handler<T> handler) {
        Object sender = getSource();
        Composite composite = (sender != null) && (sender instanceof Composite)
                ? (Composite) sender : null;
        handler.onSelected(composite, item);
    }

    public static <T> EventRegistration<T, ItemSelectedEvent.Handler<T>> addHandler(EventBus eventBus, Handler<T> handler) {
        return new EventRegistration<T, ItemSelectedEvent.Handler<T>>(eventBus, handler, ItemSelectedEvent.class);
    }
}
