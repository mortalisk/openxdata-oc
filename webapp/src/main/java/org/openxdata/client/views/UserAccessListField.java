package org.openxdata.client.views;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.client.AppMessages;
import org.openxdata.client.controllers.UserAccessController;
import org.openxdata.client.model.UserSummary;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldSetEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.ListField;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * UserAccessGrid is used to create a DualFieldList, with search and paging functionality.
 * A DualFieldList is two lists where items can be moved from one list to the other.
 * TODO: If this class is to be re-used elsewhere, it should be made generic (see DualFieldList for inspiration)
 */
public class UserAccessListField extends FieldSet {
	
	private UserAccessController controller;

    private int pageSize = 20;
    protected final AppMessages appMessages = GWT.create(AppMessages.class);
    public enum Category { STUDY, FORM };
    private Category category;
    private StudyDef study;
    private FormDef form;
    
    private UserAccessList fromField;
    private UserAccessList toField;

    public UserAccessListField(Category category, UserAccessController controller) {
        this.category = category;
        this.controller = controller;
        init();
    }

    private void init() {
    	setAutoWidth(true);
    	if (category == Category.STUDY) {
    		setHeading(appMessages.setUserAccessToStudy());
    	} else {
    		setHeading(appMessages.setUserAccessToForm());
    	}
        setCollapsible(true);
        setExpanded(false);

        Button addUserBtn = new Button(appMessages.addUser());
        addUserBtn.setWidth(110); // note making the buttons manually the same width, so that different languages don't get extremely long buttons and mess up the width of the wizard
        addUserBtn.setAutoHeight(true); //note: this might need extra word wrapping testing
        addUserBtn.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {
            	onButtonRight(be);
            }
        });
        Button addAllUserBtn = new Button(appMessages.addAllUsers());
        addAllUserBtn.setWidth(110);
        addAllUserBtn.setAutoHeight(true);
        addAllUserBtn.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {
            	onButtonAllRight(be);
            }
        });

        Button removeUserBtn = new Button(appMessages.removeUser());
        removeUserBtn.setWidth(110);
        removeUserBtn.setAutoHeight(true);
        removeUserBtn.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {
                onButtonLeft(be);
            }
        });
        Button removeAllUserBtn = new Button(appMessages.removeAllUsers());
        removeAllUserBtn.setWidth(110);
        removeAllUserBtn.setAutoHeight(true);
        removeAllUserBtn.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {
                onButtonAllLeft(be);
            }
        });
        
        HorizontalPanel userTable = new HorizontalPanel();
        userTable.setVerticalAlign(VerticalAlignment.MIDDLE);
        userTable.setBorders(false);
        
        fromField = new UserAccessList(appMessages.availableUsers()) {
            void loadData(PagingToolBar pagingToolBar, PagingLoadConfig pagingLoadConfig, AsyncCallback<PagingLoadResult<UserSummary>> callback) {
        		// unmapped studies + forms
        		if (category == Category.STUDY) {
        			if (study != null) {
        				controller.getUnMappedStudyUsers(study.getId(), pagingLoadConfig, callback);
        			}
        		} else {
        			if (form != null) {
        				controller.getUnMappedFormUsers(form.getId(), pagingLoadConfig, callback);
        			}
        		}
            }
        };
        userTable.add(fromField);
        
        VerticalPanel buttons = new VerticalPanel();
        buttons.setHorizontalAlign(HorizontalAlignment.CENTER);
        buttons.setBorders(false);
        buttons.add(addUserBtn);
        buttons.add(addAllUserBtn);
        buttons.add(new Label(""));
        buttons.add(removeUserBtn);
        buttons.add(removeAllUserBtn);
        userTable.add(buttons);
        
        String heading = appMessages.usersWithAccessToStudy();
        if (category == Category.FORM) {
        	heading = appMessages.usersWithAccessToForm();
        }
        toField = new UserAccessList(heading) {
            void loadData(PagingToolBar pagingToolBar, PagingLoadConfig pagingLoadConfig, AsyncCallback<PagingLoadResult<UserSummary>> callback) {
        		// mapped studies + forms
        		if (category == Category.STUDY) {
        			if (study != null) {
        				controller.getMappedStudyUsers(study.getId(), pagingLoadConfig, callback);
        			}
        		} else {
        			if (form != null) {
        				controller.getMappedFormUsers(form.getId(), pagingLoadConfig, callback);
        			}
        		}
            }
        };
        userTable.add(toField);
        
		addListener(Events.Expand, new Listener<FieldSetEvent>() {
			public void handleEvent(FieldSetEvent be) {
				toField.loadData();
				fromField.loadData();
			}
		});

        add(userTable);
    }
    
    private void onButtonAllLeft(ButtonEvent be) {
        buttonLeft(toField.field.getStore().getModels());
    }

    private void onButtonLeft(ButtonEvent be) {
    	buttonLeft(toField.field.getSelection());
    }
    
    /**
     * delete from right(to), add to left (from)
     */
    private void buttonLeft(List<UserSummary> sel) {
    	final List<User> users = new ArrayList<User>();
    	for (UserSummary summary : sel) {
    		if (summary.getUser() != null) { // user would be null for "study access" users
    			users.add(summary.getUser());
    		}
    	}
    	ProgressIndicator.showProgressBar();
    	Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
			public void execute() {
		    	if (category == Category.STUDY) {
		    		controller.updateStudyMapping(study.getId(), null, users, UserAccessListField.this);
		    	} else {
		    		controller.updateFormMapping(form.getId(), null, users, UserAccessListField.this);
		    	}
            }
    	});
    }

    private void onButtonAllRight(ButtonEvent be) {
        buttonRight(fromField.field.getStore().getModels());
    }
    
    private void onButtonRight(ButtonEvent be) {
        buttonRight(fromField.field.getSelection());
	}
    
    /**
     * add to right(to), delete from left(from)
     */
    private void buttonRight(List<UserSummary> sel) {
        final List<User> users = new ArrayList<User>();
        for (UserSummary summary : sel) {
        	if (summary.getUser() != null) { // user would be null for "study access" users
        		users.add(summary.getUser());
        	}
        }
        ProgressIndicator.showProgressBar();
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
			public void execute() {
		        if (category == Category.STUDY) {
		    		controller.updateStudyMapping(study.getId(), users, null, UserAccessListField.this);
		    	} else {
		    		controller.updateFormMapping(form.getId(), users, null, UserAccessListField.this);
		    	}
            }
        });
    }
    
    public void setForm(FormDef form) {
    	this.form = form;
    }

    public FormDef getForm() {
    	return form;
    }
    
    public void setStudy(StudyDef study) {
    	this.study = study;
    }
    
    public StudyDef getStudy() {
    	return study;
    }

    public void refresh() {
        fromField.refresh();
        toField.refresh();
        ProgressIndicator.hideProgressBar();
    }
    
    abstract class UserAccessList extends ContentPanel {
    	ListField<UserSummary> field = new ListField<UserSummary>();
        PagingToolBar pagingToolBar = new SmallPagingToolBar(pageSize);
        PagingLoader<PagingLoadResult<UserSummary>> loader;
        String filterValue;
        
        void loadData() {
        	loader.load();
        }
        
        UserAccessList(String heading) {
        	super();
        	
            setHeading(heading);
            setBorders(false);
            
            loader = new BasePagingLoader<PagingLoadResult<UserSummary>>(
                    new RpcProxy<PagingLoadResult<UserSummary>>() {
                        @Override
                        public void load(Object loadConfig, final AsyncCallback<PagingLoadResult<UserSummary>> callback) {
                            GWT.log("UserAccessListField load data");
                            final PagingLoadConfig pagingLoadConfig = (PagingLoadConfig)loadConfig;
                            pagingLoadConfig.set(RemoteStoreFilterField.PARM_QUERY, filterValue);
                    		pagingLoadConfig.set(RemoteStoreFilterField.PARM_FIELD, "name");
                            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                                @Override
    							public void execute() {
                                	loadData(pagingToolBar, pagingLoadConfig, callback);
                                }
                            });
                        }
                    }
            );
            
            loader.setRemoteSort(true);
            loader.setSortField("name");
            loader.setSortDir(SortDir.ASC);
            
            pagingToolBar.bind(loader);
            ListStore<UserSummary> store = new ListStore<UserSummary>(loader);
            
            // filter to search for users
            final RemoteStoreFilterField<UserSummary> filterField = new RemoteStoreFilterField<UserSummary> () {
               @Override
               protected void handleOnFilter(String filterValue) {
            	   // handle filtering - this is a call after each key pressed - it might be improved */
            	   UserAccessList.this.filterValue = filterValue;
            	   loader.load(0, pageSize);
               }
               
               @Override
               protected void handleCancelFilter () {
            	   UserAccessList.this.filterValue = null;
            	   loader.load(0, pageSize);
               }
            };
            filterField.setEmptyText(appMessages.searchForAUser());
            filterField.setWidth(185);
            filterField.bind(store);
            
            LayoutContainer bottomComponent = new LayoutContainer();
            bottomComponent.setBorders(false);
            bottomComponent.add(filterField);
            bottomComponent.add(pagingToolBar);
            setBottomComponent(bottomComponent);

            field.setStore(store);
            field.setBorders(false);
            field.setDisplayField("name");
            field.setSize(185, 100);
            field.getListView().setLoadingText(appMessages.loading());
            
            setScrollMode(Scroll.AUTOY);
            add(field);
        }
        
        void refresh() {
        	pagingToolBar.enable();
        	pagingToolBar.unmask();
        	pagingToolBar.refresh();
        }
        
        abstract void loadData(PagingToolBar pagingToolBar, PagingLoadConfig pagingLoadConfig, AsyncCallback<PagingLoadResult<UserSummary>> callback);
        
    }
}
