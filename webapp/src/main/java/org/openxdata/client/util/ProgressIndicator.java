package org.openxdata.client.util;

import org.openxdata.client.AppMessages;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;

/**
 * Emit Utility Class to show an "in progress" indicator
 * when the system is processing a user generated event.
 */
public class ProgressIndicator extends Window {
    static AppMessages appMessages = GWT.create(AppMessages.class);
    private static ProgressIndicator ref;

    private ProgressIndicator() { }
    
    /**
     * Show the progress bar in the center of the screen. 
     * Note: there is only one progress bar
     */
    public static void showProgressBar() {
        ProgressIndicator win = getProgressBar();
        win.show();
        int ver = (com.google.gwt.user.client.Window.getClientHeight() / 2) - 32;
        int hor = (com.google.gwt.user.client.Window.getClientWidth() / 2) - 32;
        ref.setPosition(hor, ver);
    }
    
    /**
     * Hides the progress bar
     */
    public static void hideProgressBar() {
        if (ref != null) {
            ref.hide();
        }
    }

    /**
     * Gets a reference to the ProgressIndicator,
     * and creates it if it is not already initialised
     * @return ProgressIndicator
     */
    private static ProgressIndicator getProgressBar()
    {
      if (ref == null) {
          ref = new ProgressIndicator();
          ref.setResizable(false);
          ref.setSize(32, 32);
          ref.setPlain(true);
          ref.setBodyBorder(false);
          ref.setBorders(false);
          ref.setShadow(false);
          ref.setFrame(false);
          ref.setHeaderVisible(false);
          // create the content panel which contains the progress stuff
          ContentPanel cp = new ContentPanel();
          cp.setLayout(new FitLayout());
          cp.setHeaderVisible(false);
          Image img = new Image("images/wait30trans.gif");
          cp.setWidth("32");
          cp.setHeight("32");
          cp.add(img);
      	  ref.add(cp);
      }
      
      return ref;
    }
}
