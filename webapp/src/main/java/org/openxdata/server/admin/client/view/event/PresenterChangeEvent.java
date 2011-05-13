package org.openxdata.server.admin.client.view.event;

import com.google.gwt.core.client.GWT;
import org.openxdata.server.admin.client.presenter.IPresenter;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event to notify the main view about a presenter change. The presenter's vie
 * view will be display
 * @author kay
 */
public class PresenterChangeEvent extends GwtEvent<PresenterChangeEvent.Handler> {

    public interface Handler extends EventHandler {

        public void onChange(IPresenter<?> presenter);
    }
    public static Type<Handler> TYPE = new Type<Handler>();
    private IPresenter<?> presenter;

    public PresenterChangeEvent(IPresenter<?> presenter) {
        this.presenter = presenter;
    }
    

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(Handler handler) {
        GWT.log("Handling Presenter Change: "+presenter.getClass());
        handler.onChange(presenter);
    }
}
