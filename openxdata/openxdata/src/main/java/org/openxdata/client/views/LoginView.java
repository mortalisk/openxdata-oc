package org.openxdata.client.views;

import org.openxdata.client.AppMessages;
import org.openxdata.client.controllers.LoginController;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;

public class LoginView extends View {
    
    AppMessages appMessages = GWT.create(AppMessages.class);
    private FormPanel loginForm;
    private Window window;  

    public LoginView(Controller controller) {
        super(controller);
    }
    
    @Override
    protected void initialize() {
        loginForm = new FormPanel();
        loginForm.setHeaderVisible(false);
        loginForm.setFrame(false);
        loginForm.setBorders(false);
        loginForm.setBodyBorder(false);
        loginForm.setLayout(new FormLayout());
        
        final TextField<String> username = new TextField<String>();  
        username.setFieldLabel(appMessages.username());  
        username.setAllowBlank(false);  
        loginForm.add(username);
        
        final TextField<String> password = new TextField<String>();  
        password.setFieldLabel(appMessages.passWord());  
        password.setAllowBlank(false);
        password.setPassword(true);
        loginForm.add(password);
       
        Button submit = new Button(appMessages.login());
        submit.setType("submit");
        loginForm.addButton(submit);
        submit.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
			public void componentSelected(ButtonEvent ce) {
                ((LoginController)controller).performLogin(username.getValue(), password.getValue());
              }
        });
        
        loginForm.setButtonAlign(HorizontalAlignment.CENTER);
        FormButtonBinding binding = new FormButtonBinding(loginForm);  
        binding.addButton(submit);
        
        // this opens a new popup
        //String LOGIN_SERVICE_URI = "j_spring_security_check";
        //loginForm.setAction(GWT.getModuleBaseURL() + LOGIN_SERVICE_URI+"?ajax=true");
        //loginForm.setMethod(Method.POST);  
    }

    @Override
    protected void handleEvent(AppEvent event) {
        if (event.getType() == LoginController.LOGIN) {
            window = createWindow(appMessages.sessionExpired(), loginForm);
            window.show();
          }
    }
    
    public void reset() {
        loginForm.clear();
        loginForm.reset();
    }

    public void close(){
        reset();
        window.hide();
    }
    
    private Window createWindow(String title, Component component) {
        Window window = new Window();
        window.setAutoHeight(true);
        window.setWidth(400);
        window.setPlain(true);  
        window.setHeading(title);
        window.add(component);
        window.setClosable(false);           
        window.setDraggable(true);
        window.setResizable(true);
        window.setScrollMode(Scroll.AUTO);
        return window;
    }
}