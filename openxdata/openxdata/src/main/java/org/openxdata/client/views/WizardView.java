package org.openxdata.client.views;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.client.AppMessages;
import org.openxdata.client.util.ProgressIndicator;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.CardPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.client.Scheduler;

/**
 * Abstract Wizard View which creates a GXT framework for Wizard type Windows (using CardLayout).
 * This class handles the next,previous,finish,cancel (etc) functionality for you.
 *
 * @author dagmar@cell-life.org
 */
public abstract class WizardView extends View {
	
    protected final AppMessages appMessages = GWT.create(AppMessages.class);
	
    private String heading;
    private Window window;
	private CardLayout wizardLayout;
	
	protected Button nextButton;
	protected Button backButton;
	protected Button saveAndExitButton;
	protected Button finishButton;
	protected Button cancelButton;
	
	private int activePage = 0;
	private List<LayoutContainer> pages = new ArrayList<LayoutContainer>();

    public WizardView(Controller controller) {
        super(controller);
    }

    @Override
    protected void initialize() {
        GWT.log("WizardView : initialize");
        wizardLayout = new CardLayout();

        pages = createPages();
        createButtons();
    }
    
    protected abstract List<LayoutContainer> createPages();
    protected abstract void display(int activePage, List<LayoutContainer> pages);

	protected void createButtons() {
        backButton = new Button(appMessages.back());
        backButton.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
			public void handleEvent(ButtonEvent be) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
					public void execute() {
                    	back();
                    }
                });
             }
         });
        nextButton = new Button(appMessages.next());
        nextButton.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
			public void handleEvent(ButtonEvent be) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
					public void execute() {
                    	next();
                    }
                });
             }
         });
        finishButton = new Button(appMessages.finish());
        finishButton.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
			public void handleEvent(ButtonEvent be) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
					public void execute() {
                    	finish();
                    }
                });
             }
         });
        saveAndExitButton = new Button(appMessages.saveAndExit());
        saveAndExitButton.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
			public void handleEvent(ButtonEvent be) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
					public void execute() {
                    	saveAndExit();
                    }
                });
             }
         });
        cancelButton = new Button(appMessages.cancel());
        cancelButton.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
			public void handleEvent(ButtonEvent be) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
					public void execute() {
                    	cancel();
                    }
                });
             }
         });
	}
	
    public void showWindow(String heading, int width, int height) {
    	GWT.log("FormResponsesView : createWindow");
    	this.heading = heading;
        window = new Window();
        window.setModal(true);
        window.setPlain(true);
        window.setMaximizable(true);
        window.setDraggable(true);
        window.setResizable(true);
        window.setScrollMode(Scroll.AUTOY);
        window.setLayout(new FitLayout());
        CardPanel cp = new CardPanel();
        cp.setLayout(wizardLayout);
        setActiveItem();
        cp.setBorders(false);
        for (LayoutContainer page : pages) {
        	cp.add(page);
        }
        window.add(cp);
        window.addButton(backButton);
        window.addButton(nextButton);
        window.addButton(finishButton);
        window.addButton(saveAndExitButton);
        window.addButton(cancelButton);
        toggleButtons();
        window.setSize(width, height);
        window.addListener(Events.BeforeHide, windowListener);
	    window.show();
    }
    public void resizeWindow(int height){
        window.setHeight(window.getHeight()+height);
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
    
    private void toggleButtons() {
        if (activePage == 0) {
        	backButton.hide();
        } else {
        	backButton.show();
        }
        if (activePage == pages.size()-1) {
        	nextButton.hide();
        	finishButton.show();
        } else {
        	nextButton.show();
        	finishButton.hide();
        }
    }
    
    private void next() {
    	ProgressIndicator.showProgressBar();
    	activePage++;
    	if (activePage != pages.size()) {
    		setActiveItem();
	    	toggleButtons();
    	} else {
    		// shouldn't be possible, but a double click before the next page is rendered could cause this
    		activePage = pages.size() - 1;
    	}
    	ProgressIndicator.hideProgressBar();
    }
    
    private void back() {
    	ProgressIndicator.showProgressBar();
    	activePage--;
    	if (activePage >= 0) {
    		setActiveItem();
	    	toggleButtons();
    	} else {
    		// shouldn't be possible, but a double click before the next page is rendered could cause this
    		activePage = 0;
    	}
    	ProgressIndicator.hideProgressBar();
    }
    
    private void cancel() {
    	MessageBox.confirm(appMessages.cancel(), appMessages.areYouSureWizard(), new Listener<MessageBoxEvent>() {
			@Override
			public void handleEvent(MessageBoxEvent be) {
				if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
					closeWindow();
				}
			}
    	});
    }
    
    private void setActiveItem() {
    	display(activePage, pages);
    	wizardLayout.setActiveItem(pages.get(activePage));
        window.setHeading(heading + " " + appMessages.stepOf((activePage + 1), pages.size()));
    }
    
    public void closeWindow() {
    	window.removeListener(Events.BeforeHide, windowListener);
		window.hide();
		ProgressIndicator.hideProgressBar();
		window.addListener(Events.BeforeHide, windowListener);
    }
    
    protected abstract void finish();
    
    protected abstract void saveAndExit();
}