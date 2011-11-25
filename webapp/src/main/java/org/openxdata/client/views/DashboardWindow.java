package org.openxdata.client.views;

import org.openxdata.client.AppMessages;
import org.openxdata.client.util.ProgressIndicator;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.core.client.GWT;

/**
 * A pre-configured Dashboard GXT Window which will allow us to
 * have windows that look the same across the whole application
 * and also avoid code duplication
 */
public class DashboardWindow extends Window {
	
	protected final AppMessages appMessages = GWT.create(AppMessages.class);

	/**
	 * Creates a new Window with an exit "Are you sure" prompt
	 * @param heading
	 * @param width
	 * @param height
	 */
	public DashboardWindow(String heading, int width, int height) {
		this(heading, width, height, true);
	}
	
	/**
	 * Creates a new Window without any exit prompt
	 * @param heading
	 * @param width
	 * @param height
	 * @param exitPrompt
	 */
	public DashboardWindow(String heading, int width, int height, boolean exitPrompt) {
		setHeading(heading);
        setModal(true);
        setPlain(true);
        setMaximizable(true);
        setDraggable(true);
        setResizable(true);
        setScrollMode(Scroll.AUTOY);
        setSize(width, height);
        if (exitPrompt) {
        	addListener(Events.BeforeHide, windowListener);
        }
	}
	
	final Listener<ComponentEvent> windowListener = new WindowListener();
    class WindowListener implements Listener<ComponentEvent> {
    	@Override
		public void handleEvent(ComponentEvent be) {
			be.setCancelled(true);
			be.stopEvent();
			MessageBox.confirm(appMessages.cancel(), appMessages.areYouSure(), new Listener<MessageBoxEvent>() {
    			@Override
    			public void handleEvent(MessageBoxEvent be) {
    				if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
    					closeWindow();
    				}
    			}
        	});
    	}
    };
    
    public void closeWindow() {
    	removeListener(Events.BeforeHide, windowListener);
		hide();
		ProgressIndicator.hideProgressBar();
		addListener(Events.BeforeHide, windowListener);
		ProgressIndicator.hideProgressBar();
    }
}
