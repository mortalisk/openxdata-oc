package org.openxdata.client.views;

import org.openxdata.client.AppMessages;

import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.google.gwt.core.client.GWT;

/**
 * Extends the GXT PagingToolBar and adds functionality to
 * adjust the page size
 */
public class AdjustablePagingToolBar extends PagingToolBar {
	
	final AppMessages appMessages = GWT.create(AppMessages.class);

	public AdjustablePagingToolBar(int pageSize) {
		super(pageSize);

        SimpleComboBox<String> pageSizeCombo = new SimpleComboBox<String>();
        pageSizeCombo.setAutoWidth(true);
        pageSizeCombo.setTriggerAction(TriggerAction.ALL);
        pageSizeCombo.setEmptyText(appMessages.itemsPerPage(""));
        pageSizeCombo.add(appMessages.itemsPerPage("10"));
        pageSizeCombo.add(appMessages.itemsPerPage("20"));
        pageSizeCombo.add(appMessages.itemsPerPage("30"));
        pageSizeCombo.add(appMessages.itemsPerPage("40"));
        pageSizeCombo.add(appMessages.itemsPerPage("50"));

        pageSizeCombo.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {
            @Override
			public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {
                String[] pageSizeText = se.getSelectedItem().getValue().split("[^\\d]");
                if (pageSizeText.length > 0) {
                    try {
                        Integer newPageSize = Integer.parseInt(pageSizeText[0]);
                        AdjustablePagingToolBar.this.setPageSize(newPageSize);
                        AdjustablePagingToolBar.this.refresh();
                    } catch (NumberFormatException e) {
                        GWT.log("Page size "+pageSizeText[0]+" is not a number", e);
                    }
                } else {
                    GWT.log("Page size could not be found in the selected text " + se.getSelectedItem().getValue());
                }
            }
        });
        
        add(new SeparatorToolItem());
        add(pageSizeCombo);
	}
	
	@Override
	protected void doLoadRequest(int offset, int limit) {
		GWT.log("limit="+limit+", pageSize="+pageSize);
	    if (isReuseConfig() && config != null) {
	      config.setOffset(offset);
	      config.setLimit(limit);
	      loader.load(config);
	    } else {
	      loader.setLimit(limit);
	      loader.load(offset, limit);
	    }
	 }
}