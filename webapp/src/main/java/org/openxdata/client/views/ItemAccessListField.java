package org.openxdata.client.views;

import java.util.List;

import org.openxdata.client.controllers.ItemAccessController;
import org.openxdata.client.util.ProgressIndicator;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
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
import com.extjs.gxt.ui.client.widget.form.ListField;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * ItemAccessGrid is used to create a DualFieldList, with search and paging functionality.
 * (A DualFieldList is two lists where items can be moved from one list to the other.)
 * This component is used to map items (e.g. Forms, Studies) to an object (e.g. User)
 */
public class ItemAccessListField<M extends ModelData> extends LayoutContainer {
	
	protected ItemAccessListFieldMessages messages;
	
	private ItemAccessController<M> controller;

    private int pageSize = 20;   
    
    private PagingFilterListField fromField;
    private PagingFilterListField toField;


    public ItemAccessListField(ItemAccessListFieldMessages messages, ItemAccessController<M> controller) {
    	this.messages = messages;
        this.controller = controller;
        init();
    }

    private void init() {
    	setAutoWidth(true);

        Button addUserBtn = new Button(messages.getAddOne());
        addUserBtn.setMinWidth(110);
        addUserBtn.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {
            	onButtonRight(be);
            }
        });
        Button addAllUserBtn = new Button(messages.getAddAll());
        addAllUserBtn.setMinWidth(110);
        addAllUserBtn.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {
            	onButtonAllRight(be);
            }
        });

        Button removeUserBtn = new Button(messages.getRemoveOne());
        removeUserBtn.setMinWidth(110);
        removeUserBtn.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {
                onButtonLeft(be);
            }
        });
        Button removeAllUserBtn = new Button(messages.getRemoveAll());
        removeAllUserBtn.setMinWidth(110);
        removeAllUserBtn.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {
                onButtonAllLeft(be);
            }
        });
        
        HorizontalPanel userTable = new HorizontalPanel();
        userTable.setVerticalAlign(VerticalAlignment.MIDDLE);
        userTable.setBorders(false);
        
        fromField = new PagingFilterListField(messages.getLeftHeading()) {
            void loadData(PagingToolBar pagingToolBar, PagingLoadConfig pagingLoadConfig, AsyncCallback<PagingLoadResult<M>> callback) {
    				controller.getUnMappedData(pagingLoadConfig, callback);
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

        toField = new PagingFilterListField(messages.getRightHeading()) {
            void loadData(PagingToolBar pagingToolBar, PagingLoadConfig pagingLoadConfig, AsyncCallback<PagingLoadResult<M>> callback) {
        		controller.getMappedData(pagingLoadConfig, callback);
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
    private void buttonLeft(final List<M> sel) {
    	ProgressIndicator.showProgressBar();
    	Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
			public void execute() {
            	controller.deleteMapping(sel, ItemAccessListField.this);
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
    private void buttonRight(final List<M> sel) {
        ProgressIndicator.showProgressBar();
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
			public void execute() {
		    	controller.addMapping(sel, ItemAccessListField.this);
            }
        });
    }

    public void refresh() {
        fromField.refresh();
        toField.refresh();
        ProgressIndicator.hideProgressBar();
    }
    
    abstract class PagingFilterListField extends ContentPanel {
    	ListField<M> field = new ListField<M>();
        PagingToolBar pagingToolBar = new SmallPagingToolBar(pageSize);
        PagingLoader<PagingLoadResult<M>> loader;
        String filterValue;
        
        void loadData() {
        	loader.load();
        }
        
        PagingFilterListField(String heading) {
        	super();
        	
            setHeading(heading);
            setBorders(false);
            
            loader = new BasePagingLoader<PagingLoadResult<M>>(
                    new RpcProxy<PagingLoadResult<M>>() {
                        @Override
                        public void load(Object loadConfig, final AsyncCallback<PagingLoadResult<M>> callback) {
                            GWT.log("ItemAccessListField load data");
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
            ListStore<M> store = new ListStore<M>(loader);
            
            // filter to search for users
            final RemoteStoreFilterField<M> filterField = new RemoteStoreFilterField<M> () {
               @Override
               protected void handleOnFilter(String filterValue) {
            	   // handle filtering - this is a call after each key pressed - it might be improved */
            	   PagingFilterListField.this.filterValue = filterValue;
            	   loader.load(0, pageSize);
               }
               
               @Override
               protected void handleCancelFilter () {
            	   PagingFilterListField.this.filterValue = null;
            	   loader.load(0, pageSize);
               }
            };
            filterField.setEmptyText("Search");
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
            field.getListView().setLoadingText(messages.getLoading());
            
            setScrollMode(Scroll.AUTOY);
            add(field);
        }
        
        void refresh() {
        	pagingToolBar.enable();
        	pagingToolBar.unmask();
        	pagingToolBar.refresh();
        }
        
        abstract void loadData(PagingToolBar pagingToolBar, PagingLoadConfig pagingLoadConfig, AsyncCallback<PagingLoadResult<M>> callback);
        
    }
}
