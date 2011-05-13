/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openxdata.server.admin.client.presenter.tree;

import com.google.gwt.resources.client.ImageResource;
import java.util.List;
import org.openxdata.server.admin.model.Editable;

/**
 *
 * @author kay
 */
public interface TreeItemWrapper extends Editable {

    public void addChild(TreeItemWrapper wrapper);

    public void removeChild(TreeItemWrapper wrapper);

    public String getName();

    public void setName(String name);

    public List<TreeItemWrapper> getChildren();

    public TreeItemWrapper getParent();

    public Object getObject();

    public ImageResource getImage();

    public boolean hasParent();

    public boolean contains(TreeItemWrapper wrapper);
}
