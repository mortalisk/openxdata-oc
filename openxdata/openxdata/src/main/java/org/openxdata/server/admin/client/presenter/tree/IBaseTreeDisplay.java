package org.openxdata.server.admin.client.presenter.tree;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import java.util.List;
import org.openxdata.server.admin.client.presenter.WidgetDisplay;
import org.openxdata.server.admin.client.view.treeview.listeners.ContextMenuInitListener;
import org.openxdata.server.admin.model.Editable;

/**
 *
 * @author kay
 */
public interface IBaseTreeDisplay<E extends Editable> extends WidgetDisplay {

    public HasSelectionHandlers<?> getList();

    public void setItems(List<E> items);

    public void update(E item);

    public void add(E item);

    public E getSelected();

    public void addContextMenuHandler(ContextMenuInitListener menuInitListener);

    public void delete(E item);

    public void addCommand(String tile, Command cmd, ImageResource image);

    public void addChild(E item);
}
