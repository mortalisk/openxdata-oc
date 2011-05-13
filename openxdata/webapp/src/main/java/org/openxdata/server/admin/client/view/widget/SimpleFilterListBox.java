package org.openxdata.server.admin.client.view.widget;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.ArrayList;
import java.util.List;
import org.openxdata.server.admin.client.util.Utilities;

/**
 *
 * @author kay
 */
public class SimpleFilterListBox extends Composite {

    private ListBox listBox = new ListBox();
    private VerticalPanel verticalPanel = new VerticalPanel();
    private Label filter = new Label("Search");
    private TextBox txtBox = new TextBox();
    List<ValueText> items = new ArrayList<ValueText>();

    public SimpleFilterListBox() {
        HorizontalPanel panel = new HorizontalPanel();
        filter.setWidth("40%");
        txtBox.setWidth("60%");
        panel.add(filter);
        panel.add(txtBox);
        panel.setWidth("100%");
        verticalPanel.add(panel);
        verticalPanel.add(listBox);
        Utilities.maximizeWidget(listBox);
        Utilities.maximizeWidget(verticalPanel);
        initWidget(verticalPanel);
        txtBox.addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                filterItems();
            }
        });
    }

    private void filterItems() {
        String text = txtBox.getText().toLowerCase();
        listBox.clear();
        if (text.isEmpty()) {
            for (ValueText valueTxt : items) {
                listBox.addItem(valueTxt.text, valueTxt.value);
            }
        } else {
            for (ValueText valuTxt : items) {
                String lowCaseStr = valuTxt.text.toLowerCase();
                if (matches(lowCaseStr, text))
                    listBox.addItem(valuTxt.text, valuTxt.value);
            }
        }
    }

    private boolean matches(String lowCaseStr, String text) {
        return lowCaseStr.contains(text);
    }

    public void setVisibleItemCount(int visibleItems) {
        listBox.setVisibleItemCount(visibleItems);
    }

    public void setSelectedIndex(int index) {
        listBox.setSelectedIndex(index);
    }

    public void setItemText(int index, String text) {
        listBox.setItemText(index, text);
    }

    public void setItemSelected(int index, boolean selected) {
        listBox.setItemSelected(index, selected);
    }

    public boolean isMultipleSelect() {
        return listBox.isMultipleSelect();
    }

    public boolean isItemSelected(int index) {
        return listBox.isItemSelected(index);
    }

    public int getVisibleItemCount() {
        return listBox.getVisibleItemCount();
    }

    public String getValue(int index) {
        return listBox.getValue(index);
    }

    public int getSelectedIndex() {
        return listBox.getSelectedIndex();
    }

    public String getItemText(int index) {
        return listBox.getItemText(index);
    }

    public int getItemCount() {
        return listBox.getItemCount();
    }

    public void clear() {
        items.clear();
        txtBox.setText("");
        listBox.clear();
    }

    public void addItem(String item) {
        items.add(new ValueText(item));
        listBox.addItem(item);
    }

    public void addItem(String item, String value) {
        items.add(new ValueText(item, value));
        listBox.addItem(item, value);
    }

    private class ValueText {

        String value;
        String text;

        public ValueText(String text, String value) {
            this.text = text;
            this.value = value;
        }

        public ValueText(String value) {
            this(value, value);
        }
    }
}
