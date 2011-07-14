/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openxdata.client.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.openxdata.client.AppMessages;
import org.openxdata.client.model.UserSummary;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.admin.model.mapping.UserStudyMap;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.PagingModelMemoryProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.ListField;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.core.client.GWT;

/**
 * UserAccessGrid is used to create a DualFieldList, with search and paging functionality.
 * A DualFieldList is two lists where items can be moved from one list to the other.
 * TODO: If this class is to be re-used elsewhere, it should be made generic (see DualFieldList for inspiration)
 */
public class UserAccessListField extends FieldSet {

    private ListField<UserSummary> fromField = new ListField<UserSummary>();;
    private ListField<UserSummary> toField = new ListField<UserSummary>();;
    private List<UserSummary> leftList = new ArrayList<UserSummary>();
    private List<UserSummary> rightList = new ArrayList<UserSummary>();
    private int pageSize = 5;
    private PagingToolBar leftPagingToolBar = new SmallPagingToolBar(pageSize);
    private PagingToolBar rightPagingToolbar = new SmallPagingToolBar(pageSize);
    protected final AppMessages appMessages = GWT.create(AppMessages.class);
    private String category;
    private StudyDef study;
    private FormDef form;

    public UserAccessListField(String category) {
        this.category = category;
        init();
    }

    private void init() {
    	setAutoWidth(true);
        setHeading(category);
        setCollapsible(true);
        setExpanded(false);

        Button addUserBtn = new Button(appMessages.addUser());
        addUserBtn.setWidth(110); // note making the buttons manually the same width, so that different languages don't get extremely long buttons and mess up the width of the wizard
        addUserBtn.setAutoHeight(true); //note: this might need extra word wrapping testing
        addUserBtn.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {
            	onButtonRight(be);
            	refreshToolbars();
            }
        });
        Button addAllUserBtn = new Button(appMessages.addAllUsers());
        addAllUserBtn.setWidth(110);
        addAllUserBtn.setAutoHeight(true);
        addAllUserBtn.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {
            	onButtonAllRight(be);
            	refreshToolbars();
            }
        });

        Button removeUserBtn = new Button(appMessages.removeUser());
        removeUserBtn.setWidth(110);
        removeUserBtn.setAutoHeight(true);
        removeUserBtn.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {
                onButtonLeft(be);
                refreshToolbars();
            }
        });
        Button removeAllUserBtn = new Button(appMessages.removeAllUsers());
        removeAllUserBtn.setWidth(110);
        removeAllUserBtn.setAutoHeight(true);
        removeAllUserBtn.addListener(Events.Select, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {
                onButtonAllLeft(be);
                refreshToolbars();
            }
        });
        
        HorizontalPanel userTable = new HorizontalPanel();
        userTable.setVerticalAlign(VerticalAlignment.MIDDLE);
        //ContentPanel userTable = new ContentPanel();
        //HBoxLayout userTableLayout = new HBoxLayout();
        //userTableLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        //userTable.setLayout(userTableLayout);
        userTable.setBorders(false);
        
        userTable.add(createListPanel(appMessages.availableUsers(), leftList, fromField, leftPagingToolBar));
        
        VerticalPanel buttons = new VerticalPanel();
        buttons.setHorizontalAlign(HorizontalAlignment.CENTER);
        //LayoutContainer buttons = new LayoutContainer();
        //buttons.setLayout(new VBoxLayout(VBoxLayout.VBoxLayoutAlign.STRETCHMAX));
        buttons.setBorders(false);
        buttons.add(addUserBtn);
        buttons.add(addAllUserBtn);
        buttons.add(new Label(""));
        buttons.add(removeUserBtn);
        buttons.add(removeAllUserBtn);
        userTable.add(buttons);
        
        userTable.add(createListPanel(category, rightList, toField, rightPagingToolbar));

        add(userTable);
    }
    
    private void onButtonAllLeft(ButtonEvent be) {
        buttonLeft(toField.getStore().getModels());
    }

    private void onButtonLeft(ButtonEvent be) {
    	buttonLeft(toField.getSelection());
    }
    
    /**
     * delete from right(to), add to left (from)
     */
    private void buttonLeft(List<UserSummary> sel) {
    	List<User> users = new ArrayList<User>();
    	for (UserSummary summary : sel) {
    		if (summary.getUser() != null) { // user would be null for "study access" users
    			fromField.getStore().add(summary);
    			leftList.add(summary);
    			toField.getStore().remove(summary);
    			rightList.remove(summary);
    			users.add(summary.getUser());
    		}
    	}
    }

    private void onButtonAllRight(ButtonEvent be) {
        buttonRight(fromField.getStore().getModels());
    }
    
    private void onButtonRight(ButtonEvent be) {
        buttonRight(fromField.getSelection());
	}
    
    /**
     * add to right(to), delete from left(from)
     */
    private void buttonRight(List<UserSummary> sel) {
        List<User> users = new ArrayList<User>();
        for (UserSummary summary : sel) {
        	if (summary.getUser() != null) { // user would be null for "study access" users
        		toField.getStore().add(summary);
        		rightList.add(summary);
        		fromField.getStore().remove(summary);
        		leftList.remove(summary);
        		users.add(summary.getUser());
        	}
        }
    }

    private ContentPanel createListPanel(String heading, final List<UserSummary> userList, 
    		ListField<UserSummary> listField, PagingToolBar pagingToolBar) 
    {
        ContentPanel cp = new ContentPanel();
        cp.setHeading(heading);
        cp.setBorders(false);
        
        // filter to search for users
        final StoreFilterField<UserSummary> filterField = new StoreFilterField<UserSummary>() {
            @Override
            protected boolean doSelect(Store<UserSummary> store, UserSummary parent,
                    UserSummary record, String property, String filter) {
                String userName = record.getName().toLowerCase();
                if (userName.startsWith(filter.toLowerCase())) {
                    return true;
                }
                return false;
            }
        };
        filterField.setEmptyText(appMessages.searchForAUser());
        filterField.setWidth(185);

        // add paging support for a local collection of models
        PagingModelMemoryProxy proxy = new PagingModelMemoryProxy(userList);
        PagingLoader<PagingLoadResult<UserSummary>> loader = new BasePagingLoader<PagingLoadResult<UserSummary>>(proxy);
        /*PagingLoader<PagingLoadResult<UserSummary>> loader = new BasePagingLoader<PagingLoadResult<UserSummary>>(
                new RpcProxy<PagingLoadResult<UserSummary>>() {
                    @Override
                    public void load(Object loadConfig, final AsyncCallback<PagingLoadResult<UserSummary>> callback) {
                        final FilterPagingLoadConfig config = (FilterPagingLoadConfig)loadConfig;
                        // FIXME: this should eventually load via a serverside call, because client side paging isn't saving us much
                        // step 1: sort userList
                        Collections.sort(userList, new Comparator<UserSummary>() {
                            public int compare(UserSummary o1, UserSummary o2) {
                            	return o1.getName().compareTo(o2.getName());
                            }
                        });
                        // step 2: filter list (if necessary)
                        FilterConfig filter = config.getFilterConfigs().get(0);
                        List<UserSummary> filteredList = new ArrayList<UserSummary>();
                        for (UserSummary summary : userList) {
                        	if (summary.getUser() != null) { // will be null for "study access" users
                        		if (filter.isFiltered(summary, filterField.getValue(), "", summary.getUser().getName())) {
                        			filteredList.add(summary);
                        		}
                        	}
                        }
                        // step 3: get correct page
                        int start = config.getOffset();
                        int limit = userList.size();
                        if (config.getLimit() > 0) {
                          limit = Math.min(start + config.getLimit(), limit);
                        }
                        List<UserSummary> results = new ArrayList<UserSummary>();
                        for (int i = config.getOffset(); i < limit; i++) {
                            results.add((UserSummary) userList.get(i));
                        }
                        // return paged result
                        BasePagingLoadResult<UserSummary> result = new BasePagingLoadResult<UserSummary>(results, 
                        		config.getOffset(),  userList.size());
                        callback.onSuccess(result);
                    }
                }
        );*/
        loader.setRemoteSort(true);
        loader.setSortField("name");
        loader.setSortDir(SortDir.ASC);
        ListStore<UserSummary> store = new ListStore<UserSummary>(loader);
        
        filterField.bind(store);
        
        pagingToolBar.bind(loader);
        
        LayoutContainer bottomComponent = new LayoutContainer();
        bottomComponent.setBorders(false);
        bottomComponent.add(filterField);
        bottomComponent.add(pagingToolBar);
        cp.setBottomComponent(bottomComponent);

        loader.load(0, pageSize);

        listField.setStore(store);
        listField.setBorders(false);
        listField.setDisplayField("name");
        listField.setSize(185, 100);
        cp.setScrollMode(Scroll.AUTOY);
        cp.add(listField);

        return cp;
    }
    
    // create a comparator to make searching quicker
    Comparator<User> c = new Comparator<User>() {
        public int compare(User u1, User u2) {
          return ((Integer)u1.getId()).compareTo((Integer)u2.getId());
        }
      };

    
    /**
     * Load study names into left and right list boxes appropriately
     */
    public void setUserStudyMap(StudyDef study, List<User> users, List<UserStudyMap> mappedStudies) {
        clear();
        this.study = study;
        List<UserSummary> mappedUsers = new ArrayList<UserSummary>();
        List<UserSummary> unMappedUsers = new ArrayList<UserSummary>();
        List<User> myUserList = new ArrayList<User>(users); // copy the list because we remove items from it and don't want to affect the original list
        Collections.sort(myUserList, c); // sort the user list to make searching easier + quicker
        for (UserStudyMap map : mappedStudies) {
        	if (map.getStudyId() == study.getId()) { // only look at the current study to make the method quicker
        		int index = Collections.binarySearch(myUserList, new User(map.getUserId(), null), c);
        		if (index >= 0) {
        			// match found
        			mappedUsers.add(new UserSummary(myUserList.get(index)));
        			myUserList.remove(index);
        		}
        	}
        }
        for (User u : myUserList) {
        	// all of these users are unmapped
        	unMappedUsers.add(new UserSummary(u));
        }
        updateLists(unMappedUsers, mappedUsers);
    }
    
    /**
     * Load Form Definition names into left and right List Boxes appropriately
     * FIXME: this needs to also show users with access via the study....
     */
    public void setUserFormMap(FormDef form, List<User> users, List<UserFormMap> mappedForms, List<UserStudyMap> mappedStudies) {
        clear();
        this.form = form;
        List<UserSummary> mappedUsers = new ArrayList<UserSummary>();
        List<UserSummary> unMappedUsers = new ArrayList<UserSummary>();
        List<User> myUserList = new ArrayList<User>(users); // copy the list because we remove items from it and don't want to affect the original list
        Collections.sort(myUserList, c); // sort the user list to make searching easier + quicker
        for (UserFormMap map : mappedForms) {
        	if (map.getFormId() == form.getFormId()) {
        		int index = Collections.binarySearch(myUserList, new User(map.getUserId(), null), c);
        		if (index >= 0) {
        			// match found
        			mappedUsers.add(new UserSummary(myUserList.get(index)));
        			myUserList.remove(index);
        		}
        	}
        }
        for (UserStudyMap map : mappedStudies) {
        	int thisStudyId = form.getStudy().getId();
        	if (map.getStudyId() == thisStudyId) {
        		int index = Collections.binarySearch(myUserList, new User(map.getUserId(), null), c);
        		if (index >= 0) {
        			// match found
        			User user = myUserList.get(index);
        			mappedUsers.add(new UserSummary(null, user.getName()+" ("+appMessages.studyAccess()+")"));
        			myUserList.remove(index);
        		}
        	}
        }
        for (User u : myUserList) {
        	// all of these users are unmapped
        	unMappedUsers.add(new UserSummary(u));
        }
        updateLists(unMappedUsers, mappedUsers);
    }
    
    public List<User> getMappedUsers() {
    	List<User> users = new ArrayList<User>();
    	for (UserSummary summary : rightList) {
    		if (summary.getUser() != null) { // it would be null for "study access" users
    			users.add(summary.getUser());
    		}
    	}
    	return users;
    }
    
    public List<User> getUnMappedUsers() {
    	List<User> users = new ArrayList<User>();
    	for (UserSummary summary : leftList) {
    		users.add(summary.getUser());
    	}
    	return users;
    }
    
    public FormDef getForm() {
    	return form;
    }
    
    public StudyDef getStudy() {
    	return study;
    }

    private void refreshToolbars() {
        leftPagingToolBar.refresh();
        rightPagingToolbar.refresh();
    }

    private void updateLists(List<UserSummary> unmapped, List<UserSummary> mapped) {
    	fromField.getStore().add(unmapped);
        leftList.addAll(unmapped);
        toField.getStore().add(mapped);
        rightList.addAll(mapped);
        refreshToolbars();
    }

    private void clear() {
        toField.getStore().removeAll();
        fromField.getStore().removeAll();
        leftList.clear();
        rightList.clear();
    }
}
