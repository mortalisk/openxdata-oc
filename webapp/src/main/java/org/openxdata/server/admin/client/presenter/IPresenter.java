package org.openxdata.server.admin.client.presenter;

import com.google.gwt.core.client.GWT;
import org.openxdata.server.admin.client.internationalization.OpenXdataConstants;
import org.openxdata.server.admin.client.permissions.PermissionResolver;
import org.openxdata.server.admin.client.permissions.util.RolesListUtil;

/**
 *
 * @author kay
 */
public interface IPresenter<T extends WidgetDisplay> {

    OpenXdataConstants CONSTANTS = GWT.create(OpenXdataConstants.class);
    PermissionResolver permissionResolver = RolesListUtil.getPermissionResolver();

    public T getDisplay();
}
