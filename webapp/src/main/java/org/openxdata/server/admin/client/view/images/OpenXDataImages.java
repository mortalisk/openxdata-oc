package org.openxdata.server.admin.client.view.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Tree.Resources;

/**
 * The OpenXData Image bundle to be used by the application.
 * 
 */
public interface OpenXDataImages extends Resources {
	
	ImageResource drafts();
	ImageResource markRead();
	ImageResource note();
	ImageResource lookup();

	ImageResource contactsgroup();
	ImageResource leftCorner();
	ImageResource rightCorner();
	ImageResource tasksgroup();    
	ImageResource home();
	ImageResource sent();
	ImageResource filtersgroup();
	
	ImageResource studies();
        @ClientBundle.Source("org/freedesktop/tango/16x16/apps/system-users.png")
	ImageResource user();
        ImageResource users();
	ImageResource roles();
        ImageResource settings();
        @ClientBundle.Source("org/freedesktop/tango/16x16/categories/preferences-system.png")
	ImageResource setting();
        @ClientBundle.Source("org/freedesktop/tango/16x16/places/folder.png")
        ImageResource folder();
	ImageResource tasks();
	ImageResource reports();

	ImageResource newform();
	ImageResource save();

    ImageResource add();
	ImageResource addchild();
	ImageResource delete();

    ImageResource task();
	ImageResource role();
	ImageResource edit();
	ImageResource play();
	ImageResource stop();

	ImageResource logout();

    @ClientBundle.Source("org/freedesktop/tango/16x16/actions/view-refresh.png")
    ImageResource refresh();
	
    @ClientBundle.Source("org/freedesktop/tango/16x16/devices/multimedia-player.png")
    ImageResource mobileInstaller();
}
