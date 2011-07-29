package org.openxdata.server.admin.client.view;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.openxdata.server.admin.client.listeners.StackPanelListener;
import org.openxdata.server.admin.client.presenter.MainPresenter;
import org.openxdata.server.admin.client.presenter.WidgetDisplay;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.event.EventRegistration;
import org.openxdata.server.admin.client.view.widget.OpenXDataMenuBar;
import org.openxdata.server.admin.client.view.event.LogOutEvent;
import org.openxdata.server.admin.client.view.event.MobileInstallEvent;
import org.openxdata.server.admin.client.view.event.MobileInstallEvent.Handler;
import org.openxdata.server.admin.client.view.event.ViewEvent;
import org.openxdata.server.admin.client.view.listeners.OpenXDataViewApplicationEventListener;
import org.openxdata.server.admin.client.view.widget.OpenXDataNotificationBar;
import org.openxdata.server.admin.client.view.widget.OpenXDataStackPanel;
import org.openxdata.server.admin.client.view.widget.OpenXDataToolBar;
import org.openxdata.server.admin.model.FormDefVersion;

/**
 *
 * @author kay
 */
@SuppressWarnings("deprecation")
public class MainDisplay implements MainPresenter.Display, StackPanelListener, ResizeHandler {

    private OpenXDataStackPanel stackPanel;
    private StackPanelListener stackPanelListener;
	private HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
    private VerticalPanel verticalPanel = new VerticalPanel();
    private OpenXDataMenuBar menuBar;
    private OpenXDataToolBar toolBar;
    private OpenXDataNotificationBar notificationBar;
    private DockLayoutPanel dockPanel = new DockLayoutPanel(Unit.EM);

    @Inject
    public MainDisplay(OpenXDataMenuBar menuBar, OpenXDataToolBar toolBar, OpenXDataNotificationBar notificationBar, OpenXDataStackPanel stackPanel) {
        this.menuBar = menuBar;
        this.toolBar = toolBar;
        this.notificationBar = notificationBar;//TODO StackPanelWidget Should not be injected
        this.stackPanel = stackPanel;
        init();
    }

    private void init() {

        stackPanel.setStackPanelListener(this);
        stackPanel.setWidth("100%");

        splitPanel.setSplitPosition("20%");
        splitPanel.setLeftWidget(stackPanel);

        verticalPanel.setWidth("100%");
        verticalPanel.add(menuBar);
        verticalPanel.add(notificationBar);
        verticalPanel.add(toolBar);
        verticalPanel.add(splitPanel);


        dockPanel.add(verticalPanel);
        Utilities.maximizeWidget(dockPanel);
        Window.addResizeHandler(this);
    }

    @Override
    public Widget asWidget() {
        return dockPanel;
    }

    @Override
    public void addStack(WidgetDisplay display, String stackText) {
        stackPanel.add(display.asWidget(), stackText, true);
    }

    @Override
    public void setCurrentDisplay(WidgetDisplay display) {
        splitPanel.setRightWidget(display.asWidget());
    }

    @Override
    public void addMobileInstallHandler(Handler handler) {
        menuBar.addHandler(handler, MobileInstallEvent.TYPE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addViewDataHandler(ViewEvent.Handler<FormDefVersion> handler) {
        menuBar.addHandler(handler, EventRegistration.getType(ViewEvent.class, FormDefVersion.class));
    }

    @Override
    public void addLogoutHandler(LogOutEvent.Handler handler) {
        toolBar.addHandler(handler, LogOutEvent.TYPE);
        toolBar.addHandler(handler, LogOutEvent.TYPE);
    }

    @Override
    public void setStackPanelListener(StackPanelListener stackListener) {
        this.stackPanelListener = stackListener;
    }

    @Override
    public void onSelectedIndexChanged(int newIndex) {
        if (stackPanelListener != null)
            stackPanelListener.onSelectedIndexChanged(newIndex);
    }

    @Override
    public void setApplicationEventListener(OpenXDataViewApplicationEventListener listenr) {
        menuBar.registerApplicationEventListener(listenr);
        toolBar.registerApplicationEventListener(listenr);
    }

    @Override
    public void onResize(ResizeEvent event) {
        int width = event.getWidth();
        int height = event.getHeight();
       resize(width, height);
    }

    @Override
    public void resize(int width, int height) {
         splitPanel.setSize(width + "px", (height - 50) + "px");
        int shortcutHeight = height - stackPanel.getAbsoluteTop();// 8;
        if (shortcutHeight < 1) shortcutHeight = 1;
        stackPanel.setHeight(shortcutHeight + "px");
    }
}
