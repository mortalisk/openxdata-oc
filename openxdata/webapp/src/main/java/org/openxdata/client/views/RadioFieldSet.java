package org.openxdata.client.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.Element;

/**
 * Similar to the GXT FieldSet, this component allows you to create a Radio
 * button set where form components are enabled/disabled when the parent radio
 * button is selected.
 * 
 * @author dagmar@cell-life.org
 */
public class RadioFieldSet extends LayoutContainer {
	
	RadioGroup radioGroup = new RadioGroup();
	Map<Radio, Component> radioSet = new HashMap<Radio, Component>();
	
	public RadioFieldSet() {
		baseStyle = "x-radiofieldset";
		enableLayout = true;
	}
	
	public void addRadio(final Radio radio, Component component) {
		radioGroup.add(radio);
		radioSet.put(radio, component);
		this.add(radio);
		this.add(component);
		
		radio.addListener(Events.Change, new Listener<FieldEvent>() {
			public void handleEvent(FieldEvent be) {
				for (Radio option : radioSet.keySet()) {
					Component component = radioSet.get(option);
					if (radio.getValue()) {
						if (option != radio) {
							enable(component, false);
						} else {
							enable(component, true);
						}
					} else {
						if (option != radio) {
							enable(component, true);
						} else {
							enable(component, false);
						}
					}
				}
			}
		});
	}
	
	private void enable(Component component, boolean enable) {
		if (component instanceof LayoutContainer) {
			List<Component> items = ((LayoutContainer) component).getItems();
			for (Component item : items) {
				item.setEnabled(enable);
			}
		} else {
			component.setEnabled(enable);
		}
	}
	
	public Radio addRadio(String name, String label, Component... component) {
		Radio radio = new Radio();
		radio.setName(name);
		radio.setBoxLabel(label);
		radio.setHideLabel(true);
		
		FormPanel componentContainer = new FormPanel();
		componentContainer.setLabelAlign(LabelAlign.RIGHT);
		componentContainer.setHeaderVisible(false);
		componentContainer.setBorders(false);
		componentContainer.setBodyBorder(false);
		componentContainer.setLabelSeparator("");
		componentContainer.setLabelWidth(125);
		for (Component c : component) {
			componentContainer.add(c);
			c.setEnabled(false);
		}
		addRadio(radio, componentContainer);
		
		return radio;
	}
	
	@Override
	protected void doAttachChildren() {
		super.doAttachChildren();
		for (Component c : radioSet.values()) {
			ComponentHelper.doAttach(c);
		}
	}
	
	@Override
	protected void doDetachChildren() {
		super.doDetachChildren();
		for (Component c : radioSet.values()) {
			ComponentHelper.doDetach(c);
		}
	}
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		FormLayout formLayout = new FormLayout();
		formLayout.setLabelSeparator("");
		formLayout.setLabelWidth(240);
		setLayout(formLayout);
		layout();
	}
	
	public String getSelectedRadio() {
		return this.radioGroup.getValue().getBoxLabel();
	}
}