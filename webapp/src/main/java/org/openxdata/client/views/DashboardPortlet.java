package org.openxdata.client.views;

import java.util.List;

import org.openxdata.client.Emit;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * Wrapper for the GXT Portlet to ensure that
 * all portlets in the OXD dashboard have a similar
 * appearance
 */
public class DashboardPortlet extends Portlet {
	
	/**
	 * Creates a collapsable portlet with Fitlayout
	 */
	public DashboardPortlet() {
		super(new FitLayout());
		setScrollMode(Scroll.AUTOY);
		setSize(725, 200);
		setCollapsible(true);
		setAnimCollapse(false);
		addListener(Events.Expand, new Listener<ComponentEvent>() {
			@Override
            public void handleEvent(ComponentEvent be) {
				minimiseOtherPortlets();
				maximise();
            }
		});
	}

	private void minimiseOtherPortlets() {
		List<Component> items = getAllPortlets();
		for (Component item : items) {
			Portlet portlet = (Portlet) item;
			if (portlet != this) {
				portlet.collapse();
			}
		}
	}
	
	private List<Component> getAllPortlets() {
		Portal portal = Registry.get(Emit.PORTAL);
		LayoutContainer column1 = portal.getItem(0); // we are only using column 1 at the moment
		return column1.getItems();
	}
	
	/**
	 * Maximise the portlet to the full screen but leaving
	 * enough space for the other portlets to be minimised.
	 */
	public void maximise() {
		Portal p = (Portal) getParent().getParent();
		int numberOfPortlets = 2;
		int height = p.getHeight() - (20 + (50*(numberOfPortlets-1)));
		setHeight(height);
		setExpanded(true);
	}
}
