package org.openxdata.server.admin.client.presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import org.openxdata.server.admin.client.internationalization.OpenXdataConstants;
import org.openxdata.server.admin.client.view.images.OpenXDataImages;

public interface WidgetDisplay {

    public static OpenXdataConstants constants = GWT.create(OpenXdataConstants.class);
    public static OpenXDataImages images = GWT.create(OpenXDataImages.class);

    public Widget asWidget();

}
