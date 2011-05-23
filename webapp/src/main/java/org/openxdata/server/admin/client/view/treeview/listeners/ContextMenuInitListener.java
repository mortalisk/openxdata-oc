package org.openxdata.server.admin.client.view.treeview.listeners;

/** 
 * Defines the contract for operations that can be done on an 
 * <code>Object</code> Like <p><code>Study, Form, Form version, user, role reports settings</code>
 * through the context menu that is loaded when the <code>User</code> right clicks on the <code>Objects.</code>
 * </P>
 * 
 * @author Angel
 * 
 */
public interface ContextMenuInitListener {
	
	/**
	 * Adds a new item which is an instance of the selected item on the tree view.
	 * The item can be a study, form, form version, user, role, settings, task, report or report group
	 */
	void addNewItem();
	
	/**
	 * Deletes a new item which is an instance of the selected item on the tree view.
	 * The item can be a study, form, form version, user, role, settings, task, report or report group
	 */
	void deleteSelectedItem();
}
