package org.openxdata.client.controllers;

import java.util.ArrayList;
import java.util.List;

import org.openxdata.client.AppMessages;
import org.openxdata.client.Emit;
import org.openxdata.client.EmitAsyncCallback;
import org.openxdata.client.model.RoleSummary;
import org.openxdata.client.util.PagingUtil;
import org.openxdata.client.util.ProgressIndicator;
import org.openxdata.client.views.ItemAccessListField;
import org.openxdata.server.admin.client.service.RoleServiceAsync;
import org.openxdata.server.admin.client.service.UserServiceAsync;
import org.openxdata.server.admin.model.Role;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.OpenXDataValidationException;
import org.openxdata.server.admin.model.paging.PagingLoadResult;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Controller for mapping access of Roles to a User.
 */
public class RoleUserAccessController implements ItemAccessController<RoleSummary> {
	
	private final AppMessages appMessages = GWT.create(AppMessages.class);
	private User user;
	private RoleServiceAsync roleService;
	
	public RoleUserAccessController(RoleServiceAsync roleService) {
		this.roleService = roleService;
	}
	
	public RoleUserAccessController(RoleServiceAsync roleService, UserServiceAsync userService, User user) {
		this.roleService = roleService;
		this.user = user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}

	@Override
    public void getMappedData(
            PagingLoadConfig pagingLoadConfig,
            final AsyncCallback<com.extjs.gxt.ui.client.data.PagingLoadResult<RoleSummary>> callback) {
		roleService.getMappedRoles(user.getId(), PagingUtil.createPagingLoadConfig(pagingLoadConfig), 
				new EmitAsyncCallback<PagingLoadResult<Role>>() {
            @Override
            public void onSuccess(PagingLoadResult<Role> result) {
            	ProgressIndicator.hideProgressBar();
            	callback.onSuccess(new BasePagingLoadResult<RoleSummary>(convertResults(result), 
            		   result.getOffset(), result.getTotalLength()));
            }
        });
    }

	@Override
    public void getUnMappedData(
            PagingLoadConfig pagingLoadConfig,
            final AsyncCallback<com.extjs.gxt.ui.client.data.PagingLoadResult<RoleSummary>> callback) {
		roleService.getUnMappedRoles(user.getId(), PagingUtil.createPagingLoadConfig(pagingLoadConfig), 
				new EmitAsyncCallback<PagingLoadResult<Role>>() {
            @Override
            public void onSuccess(PagingLoadResult<Role> result) {
            	ProgressIndicator.hideProgressBar();
            	callback.onSuccess(new BasePagingLoadResult<RoleSummary>(convertResults(result), 
            		   result.getOffset(), result.getTotalLength()));
            }
        });
    }
	
	private List<RoleSummary> convertResults(PagingLoadResult<Role> result) {
        List<RoleSummary> results = new ArrayList<RoleSummary>();
    	List<Role> roles = result.getData();
        for (Role r : roles) {
        	results.add(new RoleSummary(r));
        }
        return results;
    }

	@Override
    public void addMapping(List<RoleSummary> rolesToAdd,
            final ItemAccessListField<RoleSummary> itemAccessListField) {
		roleService.saveMappedRoles(user.getId(), convertRoles(rolesToAdd), null, new EmitAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
            	itemAccessListField.refresh();
            }
        });
    }

	@Override
    public void deleteMapping(List<RoleSummary> rolesToDelete, 
    		final ItemAccessListField<RoleSummary> itemAccessListField) throws OpenXDataValidationException {
		final List<Role> roles = convertRoles(rolesToDelete);
		// check if the user is the currently logged in user because we can't delete 
		// the admin role from the currently logged in user
		User loggedInUser = Registry.get(Emit.LOGGED_IN_USER_NAME);
    	if (loggedInUser.getName().equals(user.getName())) {
    		if (containsAdminRole(roles)) {
    			throw new OpenXDataValidationException(appMessages.cannotRemoveRoleAdministratorFromLoggedInUser());
    		}
    	}
		roleService.saveMappedRoles(user.getId(), null, roles, new EmitAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
            	itemAccessListField.refresh();
            }
        });
		
    }
	
	private boolean containsAdminRole(List<Role> roles) {
		for (Role role : roles) {
			if (role.isDefaultAdminRole()) {
				return true;
			}
		}
		return false;
	}
	
	private List<Role> convertRoles(List<RoleSummary> roleList) {
		List<Role> roles = new ArrayList<Role>();
		for (RoleSummary rs : roleList) {
			roles.add(rs.getRole());
		}
		return roles;
	}
}
