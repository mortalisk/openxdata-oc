package org.openxdata.server.admin.client.view.event;

import org.openxdata.server.admin.model.FormDataHeader;
import org.openxdata.server.admin.model.FormDefVersion;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 *
 * @author kay
 */
public class FormDataHeaderEvent extends GwtEvent<FormDataHeaderEvent.Handler> {

        public static interface Handler extends EventHandler {

                public void onViewRequest(FormDataHeader header);

                public void onDelete(FormDataHeader header);

                public void onCreateRequest(FormDefVersion version);
        }
        public static Type<Handler> TYPE = new Type<Handler>();
        private FormDataHeader formDataHeader;
        private EventType type;
        private FormDefVersion version;

        public FormDataHeaderEvent(FormDataHeader FormDataHeader) {
                this.formDataHeader = FormDataHeader;
                type = EventType.EDIT;
        }

        public FormDataHeaderEvent(FormDataHeader formDataHeader, EventType type) {
                this.formDataHeader = formDataHeader;
                this.type = type;
        }

        public FormDataHeaderEvent(FormDefVersion version) {
                this.version = version;
        }

        @Override
        public Type<Handler> getAssociatedType() {
                return TYPE;
        }

        @Override
        protected void dispatch(Handler handler) {

                if (version != null)
                        handler.onCreateRequest(version);
                else if (type == EventType.EDIT)
                        handler.onViewRequest(formDataHeader);
                else if (type == EventType.DELETE)
                        handler.onDelete(formDataHeader);
        }
}
