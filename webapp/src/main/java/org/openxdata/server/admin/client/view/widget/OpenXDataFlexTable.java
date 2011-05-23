package org.openxdata.server.admin.client.view.widget;

import com.google.gwt.user.client.ui.FlexTable;

/**
 * <code>Extends GWT FlexTable</code<> to add default style names.
 * 
 * @author Angel
 *
 */
public class OpenXDataFlexTable extends FlexTable {
	
	/**
	 * Constructs an instance of this <tt>class.</tt>
	 */
	public OpenXDataFlexTable(){
		setDefaultStyleNames();
	}

	/**
	 * Prepares the table with default style names.
	 */
	private void setDefaultStyleNames() {
		this.addStyleName("FlexTable2");
		this.addStyleName("FlexTable2");
		
		this.setStylePrimaryName("FlexTable2");
		this.getRowFormatter().setStylePrimaryName(0, "FlexTable-Header");
	}
}
