package org.openxdata.server.admin.client.view.listeners;

import org.openxdata.server.admin.client.view.OpenXDataBaseView;
import org.openxdata.server.admin.client.view.widget.OpenXDataMenuBar;
import org.openxdata.server.admin.client.view.widget.OpenXDataToolBar;

/**
 * Defines a contract for the {@linkplain OpenXDataBaseView}  
 * <tt>objects</tt> that want to listen to application level events like on the {@link OpenXDataToolBar} or {@link OpenXDataMenuBar}.</tt>
 * 
 * @author Angel
 *
 */
public interface OpenXDataViewApplicationEventListener {
	
	/**
	 * Saves the new or modified <tt>Editables.</tt> 
	 */
	void onSave();
	
	/**
	 * Called to add a new item at the same level as the selected item.
	 * For example if a study is selected, this will add a new new study.
	 * If a form version is selected, this will add a new form version at the same level.
	 * <p>
	 * And if the form designer is displayed, this command is passed over to and and it does things as follows:
	 * For instance if a form is selected, this should add a new form, if a question
	 * is selected, this should add a new question, if a page is selected, this should add a new page.
	 * </p>
	 * If there is no form in the forms panel, this should add a new form, page, and one question.
	 */
	void onNewItem();
	
	/**
	 * Called to delete the selected item. 
	 * This could be a study, form, page, question, or question option.
	 */
	public void onDeleteItem();
	
	/**
	 * Called to add a new item which is a child of the selected item.
	 * If there is no study, this will add a new study, form, and version at the same time.
	 * If a study is selected, this will add a form and version at the same time.
	 * If a form is selected, this will and a version to it.
	 * And if the form designer is displayed, this command is passed over to and and it does
	 * things as follows:
	 * If there is not form in the forms panel, or if the selected item should have no kids (eg text question) 
	 * then this does the same as addNewItem(). A form's child is a page, a page's child
	 * is a question, a single select, multiple select question has kids which are options.
	 * A repeat question has kids which are questions.
	 */
	public void onNewChildItem();
	
	/**
	 * Refreshed the currently selected object.*/
	public void onRefresh();
	
}
