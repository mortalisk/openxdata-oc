package org.openxdata.client;

import java.util.Date;
import java.util.List;

import org.openxdata.client.controllers.DataCaptureController;
import org.openxdata.client.controllers.DeleteStudyFormController;
import org.openxdata.client.controllers.EditStudyFormController;
import org.openxdata.client.controllers.FormDesignerController;
import org.openxdata.client.controllers.FormListController;
import org.openxdata.client.controllers.FormPrintController;
import org.openxdata.client.controllers.FormResponsesController;
import org.openxdata.client.controllers.ItemExportController;
import org.openxdata.client.controllers.ItemImportController;
import org.openxdata.client.controllers.LoginController;
import org.openxdata.client.controllers.NewEditUserController;
import org.openxdata.client.controllers.NewStudyFormController;
import org.openxdata.client.controllers.OpenClinicaStudyController;
import org.openxdata.client.controllers.UnprocessedDataController;
import org.openxdata.client.controllers.UserImportController;
import org.openxdata.client.controllers.UserListController;
import org.openxdata.client.controllers.UserProfileController;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.server.admin.client.service.FormServiceAsync;
import org.openxdata.server.admin.client.service.OpenclinicaService;
import org.openxdata.server.admin.client.service.OpenclinicaServiceAsync;
import org.openxdata.server.admin.client.service.RoleServiceAsync;
import org.openxdata.server.admin.client.service.SettingServiceAsync;
import org.openxdata.server.admin.client.service.StudyService;
import org.openxdata.server.admin.client.service.StudyServiceAsync;
import org.openxdata.server.admin.client.service.UserService;
import org.openxdata.server.admin.client.service.UserServiceAsync;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.Setting;
import org.openxdata.server.admin.model.SettingGroup;
import org.openxdata.server.admin.model.User;
import org.purc.purcforms.client.util.FormUtil;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.URL;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Emit implements EntryPoint, Refreshable {
	
    final AppMessages appMessages = GWT.create(AppMessages.class);	
    public static final String VIEWPORT = "viewport";
    public static final String PORTAL = "portal";
    public static final String LOGGED_IN_USER_NAME = "loggedInUser";
    public static final String GRID = "grid";
    
    // services
    FormServiceAsync formService;
    UserServiceAsync userService;
    SettingServiceAsync settingService;
    StudyServiceAsync studyService;
    OpenclinicaServiceAsync openclinicaService;
    RoleServiceAsync roleService;
    
    // top level UI components
    private Viewport viewport;
    private Portal portal;
    
    // user dependent UI components
    private Text userBanner;
    private Button admin;
    
    private static HandlerRegistration windowClosingRegistration;
    
    /**
     * This is the entry point method.
     */
    @Override
    public void onModuleLoad() {
        /* Install an UncaughtExceptionHandler which will
         * produce <code>FATAL</code> log messages
         */
    	GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
    		@Override
            public void onUncaughtException(final Throwable tracepoint) {
            	GWT.log("Uncaught Exception", tracepoint);
            	MessageBox.alert(appMessages.error(), appMessages.pleaseTryAgainLater(tracepoint.getMessage()), null);
            	ProgressIndicator.hideProgressBar();
            }
        });
    	
    	addWindowClosingHandler();

    	formService = FormServiceAsync.Util.getInstance();
    	settingService = SettingServiceAsync.Util.getInstance();
    	userService = (UserServiceAsync) GWT.create(UserService.class);
        studyService = (StudyServiceAsync)GWT.create(StudyService.class);
        openclinicaService = (OpenclinicaServiceAsync)GWT.create(OpenclinicaService.class);
        roleService = RoleServiceAsync.Util.getInstance();
        
        // determine the logged in user
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
        	@Override
            public void execute() {
            	userService.getLoggedInUser(new EmitAsyncCallback<User>() {
            		@Override
                    public void onSuccess(User usr) {
            			Registry.register(LOGGED_IN_USER_NAME, usr);
                    	setUserName(usr);
                    	toggleAdminButton(usr);
                    	
                    	Dispatcher.get().dispatch(FormListController.FORMLIST);

                    	if (usr.hasPermission(Permission.PERM_EDIT_USERS) || usr.hasPermission(Permission.PERM_ADD_USERS) || usr.hasPermission(Permission.PERM_VIEW_USERS)) {
                    		Dispatcher.get().dispatch(UserListController.USERLIST);
                    	}
                    	
                    	// Check if Admin changed default password
                    	Dispatcher.get().dispatch(LoginController.CHECKADMINPASS);
                    }
                });
            }
        });
        
        // determine the date settings
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
			public void execute() {
            	settingService.getSettingGroup("Date", new EmitAsyncCallback<SettingGroup>() {
    				@Override
    				public void onSuccess(SettingGroup result) {
    			        FormUtil.setDateDisplayFormat(getDateSetting(result, "displayDateFormat", "dd MMM yyyy"));
    			        FormUtil.setDateSubmitFormat(getDateSetting(result, "submitDateFormat", "yyyy-MM-dd"));
    			        FormUtil.setDateTimeDisplayFormat(getDateSetting(result, "displayDateTimeFormat", "dd MMM yyyy HH:mm:ss"));
    			        FormUtil.setDateTimeSubmitFormat(getDateSetting(result, "submitDateTimeFormat", "yyyy-MM-dd HH:mm:ss"));
    			        FormUtil.setTimeDisplayFormat(getDateSetting(result, "displayTimeFormat", "HH:mm:ss"));
    			        FormUtil.setTimeSubmitFormat(getDateSetting(result, "submitTimeFormat", "HH:mm:ss"));
    				}
    			});
            }
    	});
        
        initUI();
        
        RootPanel.get().setStylePrimaryName("body");
        
        Dispatcher dispatcher = Dispatcher.get();
        
        dispatcher.addController(new LoginController(userService));
        dispatcher.addController(new FormListController(formService));
        dispatcher.addController(new UserListController(userService));
        dispatcher.addController(new DataCaptureController(userService, formService));
        dispatcher.addController(new UserProfileController(userService));
        dispatcher.addController(new FormPrintController());
        dispatcher.addController(new FormResponsesController(formService, userService));
        dispatcher.addController(new NewStudyFormController(formService,studyService,userService));
        dispatcher.addController(new EditStudyFormController(studyService,formService, userService));
        dispatcher.addController(new DeleteStudyFormController(studyService, formService));
        dispatcher.addController(new ItemExportController());
        dispatcher.addController(new ItemImportController(studyService));
        dispatcher.addController(new FormDesignerController(studyService, formService));
        dispatcher.addController(new OpenClinicaStudyController(openclinicaService, settingService));
        dispatcher.addController(new NewEditUserController(userService, roleService, studyService, formService));
        dispatcher.addController(new UserImportController(userService));
        dispatcher.addController(new UnprocessedDataController(formService));
        
        RefreshablePublisher publisher = RefreshablePublisher.get();
        publisher.subscribe(RefreshableEvent.Type.NAME_CHANGE, this);
        
        // START OXD-464: Needed because we can load designer before Admin
        FormUtil.setupUncaughtExceptionHandler();
        FormUtil.retrieveUserDivParameters();
        // END OXD-464
        
        FormUtil.dlg.hide();
    }
    
    public static void addWindowClosingHandler() {
    	windowClosingRegistration = Window.addWindowClosingHandler(new ClosingHandler() {
            @Override
            public void onWindowClosing(ClosingEvent event) {
            	// this should handle backspaces and escape button presses
            	event.setMessage("openXdata");
            }
        });
    }
    
    public static void openWindow(String url) {
    	windowClosingRegistration.removeHandler();
    	com.google.gwt.user.client.Window.Location.replace(URL.encode(url));
    	addWindowClosingHandler();
    }
    
    private void initUI() {
        viewport = new Viewport();
        viewport.setLayout(new BorderLayout());
        viewport.addStyleName("emit-viewport");

        createNorth();
        createPortal();
        createDisclaimer();

        // registry serves as a global context
        Registry.register(VIEWPORT, viewport);
        Registry.register(PORTAL, portal);

        RootPanel.get().add(viewport);
    }

    private void createNorth() {
        HorizontalPanel northPanel = new HorizontalPanel();
        northPanel.setTableWidth("100%");
        northPanel.setBorders(false);
        
        TableData logoTableData = new TableData();
        logoTableData.setHorizontalAlign(HorizontalAlignment.LEFT);
        logoTableData.setVerticalAlign(VerticalAlignment.TOP);
        logoTableData.setWidth("250");
        Image logo = new Image(appMessages.logo());
        logo.setTitle(appMessages.title());

        logo.addClickHandler(new ClickHandler() {
        	@Override
            public void onClick(ClickEvent event) {
            	com.google.gwt.user.client.Window.open(appMessages.logoUrl(),"name","features");
              }
            });
        
        northPanel.add(logo, logoTableData); 

        TableData userBannerTableData = new TableData();
        userBannerTableData.setHorizontalAlign(HorizontalAlignment.CENTER);
        userBannerTableData.setVerticalAlign(VerticalAlignment.MIDDLE);
        userBanner = new Text("");
        userBanner.setStyleName("userBanner");
        northPanel.add(userBanner, userBannerTableData);
        
        admin = new Button(appMessages.admin());
        Menu adminMenu = new Menu();
        MenuItem adminMenuItem = new MenuItem(appMessages.admin());
        MenuItem unprocessedDataMenuItem = new MenuItem("Manage unprocessed data");
        adminMenu.add(adminMenuItem);
        adminMenu.add(unprocessedDataMenuItem);
        admin.setMenu(adminMenu);
        admin.hide();
        adminMenuItem.addListener(Events.Select, new Listener<MenuEvent>() {
        	@Override
        	public void handleEvent(MenuEvent be) {
        		forwardToAdmin(); 
        	}
        });
        unprocessedDataMenuItem.addListener(Events.Select, new Listener<MenuEvent>() {
        	@Override
        	public void handleEvent(MenuEvent be) {
        		Dispatcher.get().dispatch(UnprocessedDataController.UNPROCESSED_DATA);
        	}
        });

        Button myDetails = new Button(appMessages.myDetails());
        myDetails.addListener(Events.Select, new Listener<ButtonEvent>() {
        	@Override
            public void handleEvent(ButtonEvent be) {
                forwardToUserProfile();
            }
        }); 
        
        Button logout = new Button(appMessages.logout());
        logout.addListener(Events.Select, new Listener<ButtonEvent>() {
        	@Override
            public void handleEvent(ButtonEvent be) {
        		windowClosingRegistration.removeHandler();
                Window.Location.replace(GWT.getHostPageBaseURL()+"j_spring_security_logout"); 
            }
        });
        FlexTable ft = new FlexTable();
        ft.setBorderWidth(0);
        ft.setWidget(0, 0, myDetails);
        ft.setWidget(0, 1, admin);
        ft.setWidget(0, 2, logout);
        TableData buttonsTableData = new TableData();
        buttonsTableData.setHorizontalAlign(HorizontalAlignment.RIGHT);
        buttonsTableData.setVerticalAlign(VerticalAlignment.MIDDLE);
        buttonsTableData.setWidth("200");
        buttonsTableData.setHeight("40");
        northPanel.add(ft, buttonsTableData);
        
        BorderLayoutData data = new BorderLayoutData(LayoutRegion.NORTH, 50);
        data.setMargins(new Margins(10,40,10,14));
        viewport.add(northPanel, data);
    }

    private void createPortal() {
        LayoutContainer center = new LayoutContainer();
        center.setLayout(new FitLayout());
        
        portal = new Portal(1);
        portal.setSpacing(10);
        portal.setColumnWidth(0, .99);
        center.add(portal);

        BorderLayoutData data = new BorderLayoutData(LayoutRegion.CENTER);
        data.setMargins(new Margins(5, 5, 5, 5));

        viewport.add(center, data);
    }

	private void createDisclaimer() {        
        final LayoutContainer lc = new LayoutContainer();
        lc.setBorders(false);
        lc.setBounds(1, 1, 1, 1);
        
        VBoxLayout layout = new VBoxLayout();
        layout.setPadding(new Padding(0));  
        layout.setVBoxLayoutAlign(VBoxLayoutAlign.CENTER);  
        lc.setLayout(layout);

        final SimpleComboBox<String> languageSelector = new SimpleComboBox<String>();
        languageSelector.setEmptyText(appMessages.language());
        languageSelector.add("English");
        languageSelector.setData("English", "en");
        languageSelector.add("Portugu\u00EAs");
        languageSelector.setData("Portugu\u00EAs", "pt");
        languageSelector.add("Sesotho");
        languageSelector.setData("Sesotho", "st");
        languageSelector.add("Chichewa");
        languageSelector.setData("Chichewa", "ny");
        languageSelector.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {
        	@Override
        	public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {
        		String selectedValue = se.getSelectedItem().getValue();
        		String locale = languageSelector.getData(selectedValue);
        		if (locale != null && !locale.trim().equals("")) {
        			UrlBuilder urlBuilder = Window.Location.createUrlBuilder();
            		urlBuilder.setParameter("locale", locale);
            		windowClosingRegistration.removeHandler();
        			Window.Location.replace(urlBuilder.buildString());
        		}
        	}
        });
        lc.add(languageSelector);
        
        lc.add(new Text(""));

        ContentPanel panel = new ContentPanel();  
 
        panel.setLayout(new RowLayout(Orientation.HORIZONTAL));  
        panel.setSize(380, 50);  
        panel.setFrame(false);  
        panel.setCollapsible(false);
        panel.setHeaderVisible(false);
        panel.setBorders(false);
        panel.setBodyBorder(false);

        /*
         * None: &copy; 2011 openXdata
         * Only provider: <openXdata logo> provided by <provider logo>
         * Only branding: BrandName built on <openXdata logo>
         * Both: BrandName built on <openXdata logo> provided by <provider logo>
         */
		Boolean branded = Boolean.valueOf(appMessages.branded());
		Boolean provider = (appMessages.provider() == null || appMessages.provider().trim().equals("")) ? false : true;
	    StringBuilder sb = new StringBuilder();
	    sb.append("<table border=0 cellspacing=10><tr>");
        if (branded) {
        	// ${name} provided by
        	sb.append("<td>");
	        sb.append(appMessages.brandedBlurb());
	        sb.append("</td>");
        }
        if (branded || provider) {
	        // openXdata logo
	    	sb.append("<td valign=middle>");
	        sb.append("<a href=\"#\" onclick=\"window.open('http://www.openxdata.org');\" title=\"OpenXData : http://www.openxdata.org' style='cursor:hand;\">");
	        sb.append("<img width=\"145\" height=\"23\" src=\"images/emit/openxdata-logo-small.png\" valign=middle title=\"OpenXData\" style=\"cursor:hand;\"/>");        
	        sb.append("</a>");
	        sb.append("</td>");
        } else {
        	// default copyright notice
        	sb.append("<td valign=middle>");
        	sb.append("&copy; ");
        	sb.append(DateTimeFormat.getFormat("yyyy").format(new Date()));
        	sb.append(" ");
        	sb.append("<a href=\"#\" onclick=\"window.open('http://www.openxdata.org');\" title=\"OpenXData : http://www.openxdata.org' style='cursor:hand;\">");
	        sb.append("openXdata");        
	        sb.append("</a>");
	        sb.append("</td>");
        }
        if (provider) {
        	// provided by <provider logo>
        	sb.append("<td>");
	        sb.append(appMessages.providerBlurb()); 
	        sb.append("</td><td valign=middle>");
	        sb.append("<a href=\"#\" onclick=\"window.open('");
	        sb.append(appMessages.providerLink());
	        sb.append("');\" title=\"");
	        sb.append(appMessages.provider());
	        sb.append(" : ");
	        sb.append(appMessages.providerLink());
	        sb.append("' style='cursor:hand;\">");
	        sb.append("<img width=\"102\" height=\"45\" src=\"");
	        sb.append(appMessages.providerLogo());
	        sb.append("\" title=\"");
	        sb.append(appMessages.provider());
	        sb.append("\" style=\"cursor:hand;\"/>");
	        sb.append("</a>");
	        sb.append("</td>");
        }
        sb.append("</tr></table>");
        Html html = new Html(sb.toString());
        lc.add(html);
        
        BorderLayoutData data = new BorderLayoutData(LayoutRegion.SOUTH, 100);
        data.setMargins(new Margins(10,40,10,14));
        viewport.add(lc, data);
    }
    
    public void forwardToUserProfile(){
    	Dispatcher dispatcher = Dispatcher.get();
    	dispatcher.dispatch(UserProfileController.USERPROFILE);
    }
    
    public void forwardToAdmin(){
    	UrlBuilder urlBuilder = Window.Location.createUrlBuilder();
		Window.open(urlBuilder.buildString().replace("Emit.html", "OpenXDataServerAdmin/OpenXDataServerAdmin.html"), "Admin", null);
    }

	public void refresh(RefreshableEvent event) {
		User user = event.getData();
		Registry.register(LOGGED_IN_USER_NAME, user);
		GWT.log("Name change event for "+user.getFullName());
		setUserName(user);
	}
	
	public void setUserName(User user) {
		String userName = appMessages.user() + " : " + user.getFullName();
		userBanner.setText(userName);
	}
	
	public void toggleAdminButton(User user) {
		if (user.hasAdministrativePrivileges()) {
			admin.show();
		} else {
			admin.hide();
		}
	}

	public String getDateSetting(SettingGroup dateSettingGroup, String name, String defaultValue) {
		if (dateSettingGroup != null) {
			List<Setting> settings = dateSettingGroup.getSettings();
			for (Setting s : settings) {
				if (s.getName().equalsIgnoreCase(name)) {
					GWT.log("Found "+name+" setting="+s.getValue());
					return s.getValue();
				}
			}
		}
		GWT.log("Couldn't find any setting with name '"+name+"'. Using default value="+defaultValue);
		return defaultValue;
	}
}