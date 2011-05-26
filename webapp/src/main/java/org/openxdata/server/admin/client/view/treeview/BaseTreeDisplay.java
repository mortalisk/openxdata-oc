package org.openxdata.server.admin.client.view.treeview;

import com.google.gwt.user.client.ui.ScrollPanel;
import org.openxdata.server.admin.client.presenter.tree.IBaseTreeDisplay;
import org.openxdata.server.admin.client.presenter.tree.TreeItemWrapper;
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
        if (selectedItem != null && isEqual(item, selectedItem.getUserObject()))
            return selectedItem;


        int itemCount = tree.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            TreeItem treeItem = findAssocTreeItem(item, tree.getItem(i));
            if (treeItem != null) return treeItem;
        }
        return null;
    }

    private TreeItem findAssocTreeItem(E item, TreeItem treeItem) {
        if (isEqual(item, treeItem.getUserObject()))
            return treeItem;

        int childCount = treeItem.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TreeItem child = findAssocTreeItem(item, treeItem.getChild(i));
            if (child != null) return child;
        }
        return null;
    }

    private boolean isEqual(E item, Object obj) {
        return item.equals(obj);
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
            if (item instanceof TreeItemWrapper && ((TreeItemWrapper) item).hasParent())
                continue;
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

    @SuppressWarnings("unchecked")
	@Override
    public void update(E item) {
        TreeItem treeItem = getTreeItemForUser(item);
        if (treeItem == null) return;

        E itemSlctd = (E) treeItem.getUserObject();
        treeItem.setWidget(wrapInTreeItemWidget(itemSlctd));
        treeItem.setTitle(getTooltip(itemSlctd));
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
        labels.setAddLabel(constants.label_add_new());
        labels.setDeleteLabel(constants.label_delete());
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
        if (item instanceof TreeItemWrapper) {
            addChildItemsz(root, (TreeItemWrapper) item);
        }
        return root;
    }

    @SuppressWarnings("unchecked")
	public void addChildItemsz(TreeItem item, TreeItemWrapper wrapper) {
        List<TreeItemWrapper> children = wrapper.getChildren();
        for (TreeItemWrapper treeItemWrapper : children) {
            TreeItem wrappedItem = wrapInTreeItem((E) treeItemWrapper);
            item.addItem(wrappedItem);
        }
    }

    protected TreeItemWidget wrapInTreeItemWidget(E item) {
        return new TreeItemWidget(getImage(item), getDisplayLabel(item), contextMenu);
    }

    protected ImageResource getImage(E item) {
        if (item instanceof TreeItemWrapper)
            return ((TreeItemWrapper) item).getImage();
        else
            return images.lookup();
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
        Command cmdWrapper = new Command() {

            @Override
            public void execute() {
                contextMenu.hide();
                cmd.execute();
            }
        };
        menuBar.addItem(Utilities.createHeaderHTML(image, tile), true, cmdWrapper);
    }

    @Override
    public void addChild(E item) {
        TreeItem selectedItem = tree.getSelectedItem();
        if (selectedItem == null)
            return;
        TreeItem childItem = wrapInTreeItem(item);
        selectedItem.addItem(childItem);
        tree.setSelectedItem(childItem);
        tree.ensureSelectedItemVisible();
    }

    protected abstract String getTooltip(E item);

    protected abstract String getTreeName();

    public abstract String getDisplayLabel(E item);
}
