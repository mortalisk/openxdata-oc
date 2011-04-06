package org.openxdata.server.admin.client.view.event;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;
import java.util.HashMap;
import java.util.Map;

//FIXME Fix the Generics especially for getType
@SuppressWarnings("unchecked")
public class EventRegistration<T, H extends BaseHandler<T>> {

    private static Map<TypeHolder, Type<? extends EventHandler>> TYPES = new HashMap<TypeHolder, Type<? extends EventHandler>>();
    private EventBus eventBus;
    private H handler;
    private final Class<? extends GwtEvent> gwtEvtClazz;

    public EventRegistration(EventBus eventBus, H handler, Class<? extends GwtEvent> gwtEvtClazz) {
        this.eventBus = eventBus;
        this.handler = handler;
        this.gwtEvtClazz = gwtEvtClazz;

    }

    public void forClass(Class<? super T> clazz) {
        eventBus.addHandler(getType(gwtEvtClazz, clazz), handler);
    }

    public void forSuperClass(Class<? super  T> clazz) {
        eventBus.addHandler(getType(gwtEvtClazz, clazz), handler);
    }

    public static <H extends EventHandler, E extends GwtEvent<H>> Type<H> getType(Class<E> gwtEvtClazz, Class<?> clazz) {
        TypeHolder holder = new TypeHolder(gwtEvtClazz, clazz);
        if (!TYPES.containsKey(holder)) {
            TYPES.put(holder, new Type<H>());

        }
        return (Type<H>) TYPES.get(holder);
    }

    private static class TypeHolder {

        private Class<? extends GwtEvent> eventClazz;
        private Class<? extends Object> objectClazz;

        public TypeHolder(Class<? extends GwtEvent> eventClazz, Class<? extends Object> objectClazz) {
            this.eventClazz = eventClazz;
            this.objectClazz = objectClazz;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TypeHolder other = (TypeHolder) obj;
            if (this.eventClazz != other.eventClazz && (this.eventClazz == null
                    || !this.eventClazz.equals(other.eventClazz))) {
                return false;
            }
            if (this.objectClazz != other.objectClazz && (this.objectClazz == null
                    || !this.objectClazz.equals(other.objectClazz))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 53 * hash + (this.eventClazz != null ? this.eventClazz.hashCode() : 0);
            hash = 53 * hash + (this.objectClazz != null ? this.objectClazz.hashCode() : 0);
            return hash;
        }
    }
}
