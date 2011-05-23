package org.openxdata.server.admin.client.view.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Tree.Resources;

/**
 * The OpenXData Image bundle to be used by the application.
 * 
 * @author Angel
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
	ImageResource open();
	ImageResource save();

    @ClientBundle.Source("org/freedesktop/tango/16x16/actions/go-up.png")
	ImageResource moveup();
    
    @ClientBundle.Source("org/freedesktop/tango/16x16/actions/go-down.png")
	ImageResource movedown();

    ImageResource add();
	ImageResource addchild();
	ImageResource delete();

    @ClientBundle.Source("org/freedesktop/tango/16x16/actions/format-justify-left.png")
	ImageResource justifyleft();

    @ClientBundle.Source("org/freedesktop/tango/16x16/actions/format-justify-right.png")
	ImageResource justifyright();

    @ClientBundle.Source("org/freedesktop/tango/16x16/actions/edit-cut.png")
	ImageResource cut();

    @ClientBundle.Source("org/freedesktop/tango/16x16/actions/edit-copy.png")
	ImageResource copy();

    @ClientBundle.Source("org/freedesktop/tango/16x16/actions/edit-paste.png")
	ImageResource paste();

    ImageResource task();
	ImageResource role();
	ImageResource edit();
	ImageResource play();
	ImageResource stop();

	ImageResource alignTop();
	ImageResource alignBottom();
	ImageResource samewidth();
	ImageResource sameheight();
	ImageResource samesize();

	ImageResource pdf();
	ImageResource logout();

    @ClientBundle.Source("org/freedesktop/tango/16x16/actions/view-refresh.png")
    ImageResource refresh();
	
}
