package org.openxdata.client.util;

import org.openxdata.client.AppMessages;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.StudyDef;
import org.purc.purcforms.client.util.FormDesignerUtil;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;

/**
 * Encapsulates utility functions used by the PurcForms Designer.
 * 
 */
public class DesignerUtilities {

	static AppMessages appMessages = GWT.create(AppMessages.class);
	
	/**
	 * Creates a unique form binding.
	 * 
	 * @param formDefVersion the form definition-version object we manipulating.
	 * 
	 * @return the form binding.
	 */
	public static String getDefaultFormBinding(FormDefVersion formDefVersion){
		FormDef formDef = formDefVersion.getFormDef();
		String binding = formDef.getStudy().getName() + "_" + formDef.getName() + "_" + formDefVersion.getName();
		return FormDesignerUtil.getXmlTagName(binding);
	}
	
	/**
	 * Exports the given Editable. Right now we only export studies.
	 * 
	 * @param editable Editable to export.
	 * @param fileName File Name for the item to export.
	 */
	public static void exportEditable(final Editable editable, final String fileName){

		ProgressIndicator.showProgressBar();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				try{
					Integer id = null;
					String type = "study";
					if(editable instanceof FormDef){
						type = "form";
						id = ((FormDef)editable).getFormId();
					}
					else if(editable instanceof FormDefVersion){
						type = "version";
						id = ((FormDefVersion)editable).getFormDefVersionId();
					}
					else
						id = ((StudyDef)editable).getStudyId();

					String url = "studyexport?";
					url += "type=" + type;
					url += "&id=" + id;
					url += "&filename=" + fileName;

					Window.Location.replace(URL.encode(url));

					ProgressIndicator.hideProgressBar();	
				}
				catch(Exception ex){
					MessageBox.alert(appMessages.error(), appMessages.exportError(), null);
					ProgressIndicator.hideProgressBar();	
				}	
			}
		});
	}
}
