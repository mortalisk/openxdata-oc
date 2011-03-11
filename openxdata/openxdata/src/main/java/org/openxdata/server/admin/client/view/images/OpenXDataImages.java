/*
 *  Licensed to the OpenXdata Foundation (OXDF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The OXDF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, 
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 *  Copyright 2010 http://www.openxdata.org.
 */
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
	ImageResource users();
	ImageResource roles();
	ImageResource settings();
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
