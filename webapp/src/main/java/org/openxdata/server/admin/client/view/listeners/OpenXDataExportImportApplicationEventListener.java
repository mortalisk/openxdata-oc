package org.openxdata.server.admin.client.view.listeners;

/**
 * Defines an extended contract for those views that intend to handle Export and Import.
 * <p>
 * Please review the {@link OpenXDataViewApplicationEventListener} contract.
 * </p>
 * 
 * @author Angel
 *
 */
public interface OpenXDataExportImportApplicationEventListener extends OpenXDataViewApplicationEventListener {

	/**
	 * Enables you to import an <tt>Editable.</tt>
	 */
	void onImport();
	
	/**
	 * Exports the selected <tt>Editable</tt>.
	 * <p>
	 * The default export format is <tt>XML.</tt>
	 */
	void onExport();
	
	/** Exported the selected Item in PDF format.*/
	void exportAsPdf();
	
	/**
	 * Opens an existing XForm whose contents should be in the xforms source tab.
	 */
	void onOpen();
	
}
