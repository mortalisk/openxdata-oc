package org.openxdata.server.admin.client.view.treeview;

import com.google.gwt.user.client.ui.ScrollPanel;
import org.openxdata.server.admin.client.presenter.tree.IBaseTreeDisplay;
import org.openxdata.server.admin.model.Editable;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import java.util.List;
import org.openxdata.server.admin.client.permissions.UIViewLabels;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.contextmenu.OpenXDataContextMenu;
import org.openxdata.server.admin.client.view.treeview.listeners.ContextMenuInitListener;
import org.openxdata.server.admin.client.view.widget.CompositeTreeItem;
import org.openxdata.server.admin.client.view.widget.TreeItemWidget;

/**
 *
 * @author kay
 */
public abstract class BaseTreeDisplay<E extends Editable> implements IBaseTreeDisplay<E> {

    protected Tree tree;
    protected ScrollPanel scrollPanel = new ScrollPanel();
    protected PopupPanel contextMenu;

    public BaseTreeDisplay() {
        setUpView();

    }

    private TreeItem getTreeItemForUser(E item) {
        //First try the selected Item if failed then search
        TreeItem selectedItem = tree.getSelectedItem();
        if (selectedItem != null && item.equals(selectedItem.getUserObject()))
            return selectedItem;

        int itemCount = tree.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            TreeItem treeItem = tree.getItem(i);
            if (item.equals(treeItem.getUserObject()))
                return treeItem;

        }
        return null;
    }

    private void setUpView() {
        // Initialize the Tree View
        tree = new Tree(images);
        tree.ensureSelectedItemVisible();

        // Setting Scroll Panel properties.
        scrollPanel.setWidget(tree);
        scrollPanel.setWidth("100%");
        scrollPanel.setHeight("100");

        Utilities.maximizeWidget(scrollPanel);
    }

    private void renderUsers(List<E> users) {
        tree.clear();
        for (int i = 0; i < users.size(); i++) {
            E item = users.get(i);
            tree.addItem(wrapInTreeItem(item));
        }
    }

    @Override
    public HasSelectionHandlers<?> getList() {
        return tree;
    }

    @Override
    public void setItems(List<E> users) {
        renderUsers(users);
        tree.setSelectedItem(tree.getItem(0));
    }

    @Override
    public void update(E item) {
        TreeItem treeItem = getTreeItemForUser(item);
        if (treeItem == null) return;
        treeItem.setWidget(wrapInTreeItemWidget(item));
        treeItem.setTitle(getTooltip(item));
    }

    @Override
    public Widget asWidget() {
        return scrollPanel;
    }

    @Override
    public void addContextMenuHandler(ContextMenuInitListener menuInitListener) {
        contextMenu = new OpenXDataContextMenu().createContextMenu(menuInitListener, getContextMenuLabels(), getTreeName());
    }

    private UIViewLabels getContextMenuLabels() {
        UIViewLabels labels = new UIViewLabels();
        labels.setAddLabel(constants.label_addnewuser());
        labels.setDeleteLabel(constants.label_deleteuser());
        return labels;
    }

    @Override
    public void add(E item) {
        if (getTreeItemForUser(item) != null)
            return;
        TreeItem root = wrapInTreeItem(item);
        tree.addItem(root);
        tree.setSelectedItem(root);
    }

    protected TreeItem wrapInTreeItem(E item) {
        TreeItem root = new CompositeTreeItem(wrapInTreeItemWidget(item));
        root.setTitle(getTooltip(item));
        root.setUserObject(item);
        return root;
    }

    protected TreeItemWidget wrapInTreeItemWidget(E item) {
        return new TreeItemWidget(images.lookup(), getDisplayLabel(item), contextMenu);
    }

    @SuppressWarnings("unchecked")
    @Override
    public E getSelected() {
        TreeItem selectedItem = tree.getSelectedItem();
        Object userObject = selectedItem.getUserObject();
        return (E) userObject;
    }

    @Override
    public void delete(E item) {
        TreeItem treeItem = getTreeItemForUser(item);
        Utilities.removeRootItem(tree, treeItem);
    }

    @Override
    public void addCommand(String tile, final Command cmd, ImageResource image) {
        Widget widget = contextMenu.getWidget();
        MenuBar menuBar = (MenuBar) widget;
        menuBar.addSeparator();
        Command wrappedCommand = new Command() {

            @Override
            public void execute() {
                contextMenu.hide();
                cmd.execute();
            }
        };
        menuBar.addItem(Utilities.createHeaderHTML(image, tile), true, wrappedCommand);
    }

    protected abstract String getTooltip(E item);

    protected abstract String getTreeName();

    public abstract String getDisplayLabel(E item);
}
