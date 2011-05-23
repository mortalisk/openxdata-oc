package org.openxdata.server.admin.client.view.listeners;

/**
 * 
 * This is the <code>interface</code> defines a contract for members using <code>FormVersionOpenDialog</code>.
 * <p>
 * It returns the option selected by the 
 * <code>User</code> on the view to effect an action. 
 * An option can be <code>(Read only, Create New or Cancel)</code>.
 * </P>
 * 
 * @author Angel
 * 
 */
public interface FormVersionOpenDialogListener {
	
	/**
	 * 
	 * Method fired when an option has been selected on a widget.
	 * 
	 * @param option - the selected option from the widget
	 */
	public void onOptionSelected(int option);

}
