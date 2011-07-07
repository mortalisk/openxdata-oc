package org.openxdata.client.views;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;

/**
 * Extended the PagingToolBar so it is possible to hide
 * some of the elements (e.g. refresh button and display text).
 * This is to be used in situations where space is limited and
 * not all functionality is required (e.g. UserAccessGrids)
 */
public class SmallPagingToolBar extends PagingToolBar {

	public SmallPagingToolBar(int pageSize) {
		super(pageSize);
		remove(refresh);
		remove(displayText);
		removeTrailingSpacers();
	}
	
	private void removeTrailingSpacers() {
		Component lastElement = getItem(this.getItemCount()-1);
		if (lastElement instanceof SeparatorToolItem || lastElement instanceof FillToolItem) {
			this.remove(lastElement);
			removeTrailingSpacers();
		}
	}
}
