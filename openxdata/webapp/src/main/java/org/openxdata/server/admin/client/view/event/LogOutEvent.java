package org.openxdata.server.admin.client.view.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 *
 * @author kay
 */
public class LogOutEvent extends GwtEvent<LogOutEvent.Handler> {

        public interface Handler extends EventHandler {

                public void onLogout();
        }
        public static Type<Handler> TYPE = new Type<Handler>();

        @Override
        public Type<Handler> getAssociatedType() {
                return TYPE;
        }

        @Override
        protected void dispatch(Handler handler) {
                handler.onLogout();
        }
}
