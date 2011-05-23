package org.openxdata.server.admin.client.view.treeview.listeners;

/**
 * 
 * Extends the contract of <code>IContextMenuInitListener</code>
 * to define a contract for <code>Objects</code> that intend to work with <code>Child Items</code>.
 * 
 * <p><code>E.g.
 * studies > forms > form versions, report groups > reports, Setting groups > setting
 * </code></p>
 * 
 * @author Angel
 * 
 */
public interface ExtendedContextInitMenuListener extends ContextMenuInitListener{

	/**
	 * Adds a new child item which is a child of the instance of the selected item on the tree view.
	 * The item can be a study, form, form version, user, role, settings, task, report or report group. 
	 * if a study is selected, a form will be created, if a form is selected, a form version will be created.
	 */
	void addNewChildItem();
}
