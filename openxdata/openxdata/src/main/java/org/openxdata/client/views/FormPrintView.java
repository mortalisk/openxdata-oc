package org.openxdata.client.views;

import org.openxdata.client.AppMessages;
import org.openxdata.client.controllers.FormPrintController;


import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;

public class FormPrintView extends View {
    final AppMessages appMessages = GWT.create(AppMessages.class);    
	private VerticalPanel vp;
    private FormPanel formPanel;

    public FormPrintView(Controller controller) {
        super(controller);
    }
    
    @Override
    protected void initialize() {
    	GWT.log("FormPrintView : initialize");
        
        vp = new VerticalPanel();  
        vp.setSpacing(10);      
        
         formPanel = new FormPanel();  
         formPanel.setFrame(true);  
         formPanel.setWidth(350);  
         formPanel.setLayout(new FlowLayout());  
         formPanel.setHeading("Form : Personal Particulars");
        
         FieldSet fieldSet = new FieldSet();  
         fieldSet.setHeading("Personal Details");  
         fieldSet.setCollapsible(true);  

         FormLayout layout = new FormLayout();  
         layout.setLabelWidth(75);  
         fieldSet.setLayout(layout);  
         
         final Radio female = new Radio();
         female.setTitle("Female");
         female.setBoxLabel("Female");
         female.setName("gender");
         
         final Radio male = new Radio();
         male.setTitle("Male");
         male.setBoxLabel("Male");
         male.setName("gender");
         
         final RadioGroup radioGroup = new RadioGroup("gender");  
         radioGroup.setFieldLabel("Gender");
         radioGroup.add(female);  
         radioGroup.add(male);  
         fieldSet.add(radioGroup);        
         
         DateField dob = new DateField();
         dob.setFieldLabel("Date of Birth");
         fieldSet.add(dob);
         
         TextField<String> firstName = new TextField<String>();  
         firstName.setFieldLabel("First Name");  
         firstName.setAllowBlank(false);  
         fieldSet.add(firstName);  
       
         TextField<String> lastName = new TextField<String>();  
         lastName.setFieldLabel("Last Name");  
         fieldSet.add(lastName);  
         
         formPanel.add(fieldSet);  
         
         fieldSet = new FieldSet();  
         fieldSet.setHeading("Contact Details");  
         fieldSet.setCollapsible(true);  
         
         layout = new FormLayout();  
         layout.setLabelWidth(75);  
         fieldSet.setLayout(layout);  
         
         TextField<String> tel = new TextField<String>();  
         tel.setFieldLabel("Tel");  
         fieldSet.add(tel);  
         
         TextField<String> cell = new TextField<String>();  
         cell.setFieldLabel("Cell");  
         fieldSet.add(cell);  
       
         TextField<String> fax = new TextField<String>();  
         fax.setFieldLabel("Fax");  
         fieldSet.add(fax);  
         
         TextField<String> email = new TextField<String>();  
         email.setFieldLabel("Email");  
         fieldSet.add(email);  
         
         formPanel.add(fieldSet);  

         Button print = new Button("Print");
         print.addListener(Events.Select, new Listener<ButtonEvent>() {
             @Override
			public void handleEvent(ButtonEvent be) {
            	 System.out.println("Button Print");
             }
           });
         
         formPanel.setButtonAlign(HorizontalAlignment.CENTER);         
         formPanel.addButton(print);  
         
         vp.add(formPanel);        

    }

    @Override
    protected void handleEvent(AppEvent event) {
    	GWT.log("FormPrintView : handleEvent");
        if (event.getType() == FormPrintController.FORMPRINTVIEW) {
    	     final Window window = new Window();  
    	     window.setSize(500, 300);  
    	     window.setPlain(true);  
    	     window.setHeading("Form Print");  
    	     window.setLayout(new FitLayout());  
    	     window.add(formPanel);
    	     window.setMaximizable(true);
    	     window.setMinimizable(true);    	     
    	     window.setDraggable(true);
    	     window.setResizable(true);
    	     window.setScrollMode(Scroll.ALWAYS);
    	     window.show();
         }
    }
}
