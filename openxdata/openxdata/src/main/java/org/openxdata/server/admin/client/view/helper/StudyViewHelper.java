/*
 *  Licensed to the OpenXdata Foundation (OXDF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The OXDF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, 
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 *  Copyright 2010 http://www.openxdata.org.
 */
package org.openxdata.server.admin.client.view.helper;

import java.util.Date;
import java.util.List;

import org.openxdata.server.admin.client.Context;
import org.openxdata.server.admin.client.controller.callback.OpenXDataAsyncCallback;
import org.openxdata.server.admin.client.controller.facade.MainViewControllerFacade;
import org.openxdata.server.admin.client.internationalization.OpenXdataConstants;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.StudyView;
import org.openxdata.server.admin.client.view.dialogs.FormVersionOpenDialog;
import org.openxdata.server.admin.client.view.listeners.FormVersionOpenDialogListener;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.FormDefVersionText;
import org.openxdata.server.admin.model.StudyDef;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.Label;
import org.openxdata.designer.client.FormDesignerWidget;
import org.openxdata.designer.client.util.FormDesignerUtil;
import org.openxdata.sharedlib.client.model.Locale;
import org.openxdata.sharedlib.client.util.FormUtil;

/**
 * Helper class for the {@link StudyView}.
 * 
 * @author Angel 
 *
 */
public class StudyViewHelper {
    private static OpenXdataConstants constants = GWT.create(OpenXdataConstants.class);
	
	private StudyViewHelper(){}
	
	/**
	 * Load view for user without permissions
	 */
	public static void loadPermissionLessView(FlexTable table, DecoratedTabPanel tabs ) {
		table.setWidget(0, 0, new Label(constants.ascertain_permissionLessView() + "Studies"));
		FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
		cellFormatter.setWidth(0, 0, "20%");
		table.setStyleName("cw-FlexTable");
		
		Utilities.maximizeWidget(table);		
		tabs.add(table, constants.ascertain_permissionTab());
		Utilities.maximizeWidget(tabs);		
	}
	
	/**
	 * Gets the form version object with the given id.
	 * 
	 * @param formDefVersionId the form version id.
	 * @return the form version object.
	 */
	public static FormDefVersion getFormDefVersion(int formDefVersionId, List<StudyDef> studies){
		for(StudyDef studyDef : studies){
			for(FormDef formDef : studyDef.getForms()){
				for(FormDefVersion formDefVersion : formDef.getVersions()){
					if(formDefVersion.getFormDefVersionId() == formDefVersionId)
						return formDefVersion;
				}
			}
		}
		return null;
	}
	
	/**
	 * Creates a unique form binding which is unique.
	 * 
	 * @param formDefVersion the form version definition object.
	 * @return the new form binding.
	 */
	public static String getDefaultFormBinding(FormDefVersion formDefVersion){
		FormDef formDef = formDefVersion.getFormDef();
		String binding = formDef.getStudy().getName() + "_" + formDef.getName() + "_" + formDefVersion.getName();
		return FormDesignerUtil.getXmlTagName(binding);
	}
	
	/**
	 * Sets the date and time formats of the form designer and runner.
	 */
	public static void setDateTimeFormats(){
		FormUtil.setDateSubmitFormat(Context.getSetting("submitDateFormat", "yyyy-MM-dd"));
		FormUtil.setDateDisplayFormat(Context.getSetting("displayDateFormat","dd/MM/yyyy"));
		FormUtil.setDateTimeSubmitFormat(Context.getSetting("submitDateTimeFormat", "yyyy-MM-dd hh:mm:ss a"));
		FormUtil.setDateTimeDisplayFormat(Context.getSetting("displayDateTimeFormat","dd/MM/yyyy hh:mm:ss"));
		FormUtil.setTimeSubmitFormat("hh:mm:ss a");
		FormUtil.setTimeDisplayFormat("hh:mm:ss a");
	}
	
	/**
	 * 
	 * This method removes an extra xml declaration from the existing xForm
	 * <?xml version="1.0" encoding="UTF-8"?>
	 *  @author smuwanga at gmail.com
	 * */
	public static String removeString(String xml){
		String newXml="";
		
		int beginIndex = "<?xml version='1.0' encoding='UTF-8' ?>\n".length();
		newXml = xml.substring(beginIndex);
		return newXml;
	}
	
	/**
	 * Saves data that has been edited in an existing form.
	 * 
	 * @param xml the xforms model xml containing the data to save.
	 */
	public static void saveExistingForm(String xml, String xformXml, FormData formData){
		try{
			formData.setData(xml);
			formData.setDescription(Utilities.getDescriptionTemplate(xformXml,xml));
			formData.setChangedBy(Context.getAuthenticatedUser());
			formData.setDateChanged(new Date());

			Context.getFormServiceAsync().saveFormData(formData, new OpenXDataAsyncCallback<FormData>() {
				@Override
				public void onOtherFailure(Throwable caught) {
					Window.alert(caught.getMessage());
					
				}

				@Override
				public void onSuccess(FormData object) {
					//TODO add message for internationalization purposes
					Window.alert("Form Data Submitted Successfully");
				}
			});
		}
		catch(Exception ex){
			FormUtil.displayException(ex);
		}
	}

	/**
	 * Sets the properties of the <tt>FormDefVersion.</tt>
	 * 
	 * @param formId
	 * @param xformsXml
	 * @param layoutXml
	 * @return
	 */
	public static boolean onSaveForm(int formId, String xformsXml, String layoutXml) {
		try{
			FormDefVersion formDefVersion = getFormDefVersion(formId, Context.getStudies());

			if(formDefVersion == null){
				FormUtil.dlg.hide();
				//TODO add message for internationalization purposes
				Window.alert("Please remove the formId attribute from the xform");
				return false;
			}

			formDefVersion.setXform(xformsXml);
			formDefVersion.setLayout(layoutXml);
			formDefVersion.setDirty(true);

			return true;
			//We shall use the onSaveLocaleText() such that we avoid double saving
		}
		catch(Exception ex){
			FormUtil.dlg.hide();
			FormUtil.displayException(ex);
		}

		return false;
	}
	
	/**
	 * @see org.purc.purcforms.client.controller.IFormSaveListener#onSaveLocaleText(int, String, String)
	 */
	public static void onSaveLocaleText(int formId, String xformsLocaleText, String layoutLocaleText){
		try{
			FormDefVersion formDefVersion = getFormDefVersion(formId, Context.getStudies());

			if(formDefVersion == null){
				FormUtil.dlg.hide();
				
				//TODO add message for internationalization purposes
				Window.alert("Please select the form version first");
				return;
			}

			FormDefVersionText formDefVersionText = formDefVersion.getFormDefVersionText(Context.getLocale());
			if(formDefVersionText == null){
				formDefVersionText = new FormDefVersionText(Context.getLocale(),formDefVersion.getFormDefVersionId(),xformsLocaleText,layoutLocaleText);
				formDefVersion.addVersionText(formDefVersionText);
			}
			else{
				formDefVersionText.setXformText(xformsLocaleText);
				formDefVersionText.setLayoutText(layoutLocaleText);
			}

			formDefVersion.setDirty(true);

			MainViewControllerFacade.saveData();
		}
		catch(Exception ex){
			FormUtil.dlg.hide();
			FormUtil.displayException(ex);
		}
	}
	
	/**
	 * Switches to a given locale.
	 * 
	 * @param locale the locale key.
	 */
	public static void changeLocale(String locale, Object selectedItem, FormDesignerWidget formDesigner){
		if(selectedItem != null && selectedItem instanceof FormDefVersion){
			FormDefVersionText text = ((FormDefVersion)selectedItem).getFormDefVersionText(locale);
			if(text != null)
				formDesigner.setLocaleText(text.getFormDefVersionId(),locale, text.getXformText(), text.getLayoutText());
		}

		formDesigner.changeLocale(new Locale(locale,""));
	}
	
	/**
	 * @see org.openxdata.server.admin.client.view.listeners.OnDataCheckListener#onDataCheckComplete(boolean, String)
	 */
	public static void onDataCheckComplete(FormVersionOpenDialogListener dialogListener) {
		FormUtil.dlg.hide();
		FormVersionOpenDialog versionOpenDialog =  new FormVersionOpenDialog(dialogListener);
		versionOpenDialog.setTitle(constants.label_formversionedittitle());
		versionOpenDialog.center();
	}
}
