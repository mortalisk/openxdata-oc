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
package org.openxdata.server.admin.client.view;

import java.util.Date;
import java.util.List;

import org.openxdata.server.admin.client.Context;
import org.openxdata.server.admin.client.controller.callback.OpenXDataAsyncCallback;
import org.openxdata.server.admin.client.controller.facade.MainViewControllerFacade;
import org.openxdata.server.admin.client.controller.observe.OpenXDataObservable;
import org.openxdata.server.admin.client.controller.observe.StudiesObserver;
import org.openxdata.server.admin.client.permissions.util.RolesListUtil;
import org.openxdata.server.admin.client.util.DataCheckUtil;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.constants.OpenXDataStackPanelConstants;
import org.openxdata.server.admin.client.view.event.ItemSelectedEvent;
import org.openxdata.server.admin.client.view.helper.StudyViewHelper;
import org.openxdata.server.admin.client.view.listeners.FormVersionOpenDialogListener;
import org.openxdata.server.admin.client.view.listeners.OnDataCheckListener;
import org.openxdata.server.admin.client.view.listeners.OpenXDataViewExtendedApplicationEventListener;
import org.openxdata.server.admin.client.view.treeview.StudiesTreeView;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDataHeader;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.FormDefVersionText;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.admin.model.mapping.UserStudyMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.xml.client.XMLParser;
import com.google.inject.Inject;
import org.openxdata.server.admin.client.view.event.DesignFormEvent;
import org.openxdata.server.admin.client.view.event.FormDataHeaderEvent;
import org.openxdata.designer.client.FormDesignerWidget;
import org.openxdata.designer.client.controller.IFormSaveListener;
import org.openxdata.designer.client.util.LanguageUtil;
import org.openxdata.runner.client.FormRunnerEntryPoint;
import org.openxdata.runner.client.widget.FormRunnerWidget;
import org.openxdata.server.admin.client.view.widget.factory.OpenXDataWidgetFactory;
import org.openxdata.sharedlib.client.controller.SubmitListener;
import org.openxdata.sharedlib.client.util.FormUtil;
import org.openxdata.sharedlib.client.view.FormRunnerView.Images;


/**
 * This widget displays properties of the selected study, form, or form version
 * and lets you edit them.
 * 
 * @author daniel
 * @author Angel
 * 
 */
public class StudyView extends OpenXDataBaseView implements
        SelectionHandler<Integer>, SubmitListener, IFormSaveListener,
        ResizeHandler, OnDataCheckListener, FormVersionOpenDialogListener,
        StudiesObserver, OpenXDataViewExtendedApplicationEventListener {
	
	/** The index of the tab for properties */
	private int tabIndexProperties = 0;
	/** The index of the tab for the form designer. */
	private int tabIndexFormDesigner = -1;
	/** The index of the tab for the data list. */
	private int tabIndexDataList = -1;
	/** The index of the tab for displaying form data. */
	private int tabIndexData = -1;
	/** Icon images. */
	private final Images images = GWT.create(Images.class);
	/**
	 * The widget for displaying and allow edit of the selected study, form or
	 * form version.
	 */
	private StudyPropertiesView propertiesView;
	/** The form designer widget. */
	private FormDesignerWidget formDesigner;
	/** The form runner widget. */
	private FormRunnerWidget formRunner;
	/** The widgets displaying a list of collected data. */
	private StudyDataView dataListView;
	/** The list of studies. */
	private List<StudyDef> studies;
	/** Flag telling whether the splitter widget has already been resized. */
	private boolean designSpliterSized = false;
	/** The current for data that is displayed in the form runner widget. */
	private FormData formData;
	/** The current form version. */
	private FormDefVersion formDefVersion;
	/** The XForm */
	private String xformXml;
	/** The Layout XML */
	private String layoutXml;
	/** The selected tab index. */
	private int selectedTabIndex = 0;
	/** The utility for checking if a form version has collected data. */
	private DataCheckUtil dataCheck = new DataCheckUtil(this);
	
	/**
	 * Creates a new instance of the study view.
	 * 
	 * @param itemChangeListener
	 *            listener to <tt>Study, form and form version</tt> property
	 *            changes.
	 * @param openXDataViewFactory
	 */
        @Inject
	public StudyView( OpenXDataWidgetFactory openXDataViewFactory) {
		super( openXDataViewFactory);
		
		initHandlers();
		setUp();
	}
	
	private void setUp() {
		/* keep trak of tab positions */
		int tabPositions = 0;
		boolean noTab = true;
		openxdataStackPanel = widgetFactory.getOpenXdataStackPanel();
		
		// Register this class with Event Dispatchers.
		super.registerWithEventDispatchers();
		
		table.setStyleName("cw-FlexTable");
		table.getRowFormatter().removeStyleName(0, "FlexTable-Header");
		
		// Check if User has permissions to View Studies.
		if (RolesListUtil.getPermissionResolver().isPermission("studies")) {
			tabIndexProperties = 0;
			tabPositions++;
			if (propertiesView == null) {
				propertiesView = new StudyPropertiesView(eventBus);
			}
			
			tabs.add(propertiesView, constants.label_properties());
			noTab = false;
		}
		
		// Check if User has permissions to design Forms.
		if (RolesListUtil.getPermissionResolver().isExtraPermission(
		        "form_design")) {
			tabIndexFormDesigner = tabPositions;
			tabPositions++;
			if (formDesigner == null) {
				
				// Need to mind the order of this and formRunner avoid the null
				// pointer exception at
				// at
				// org.purc.purcforms.client.view.FormRunnerView.submitData(FormRunnerView.java:830)
				// when submitting form data using the data list tab.
				formDesigner = new FormDesignerWidget(false, false, true);
			}
			
			formDesigner.setWidth("100%");
			formDesigner.setHeight("100%");
			formDesigner.removeLanguageTab();
			formDesigner.setFormSaveListener(this);
			
			tabs.add(formDesigner, constants.label_design());
			noTab = false;
		}
		
		// Check if User has permissions to view form data
		if (RolesListUtil.getPermissionResolver().isViewPermission(
		        "Perm_View_Form_Data")
		        || RolesListUtil.getPermissionResolver().isAddPermission(
		                "Perm_Add_Form_Data")) {
			tabIndexDataList = tabPositions;
			tabPositions++;
			if (dataListView == null) {
				dataListView = new StudyDataView(eventBus);//this
			}
			
			tabs.add(dataListView, constants.label_datalist());
			noTab = false;
		}
		
		// Check if User has permissions to edit collected data.
		if (RolesListUtil.getPermissionResolver().isEditPermission(
		        "Perm_Edit_Form_Data")
		        || RolesListUtil.getPermissionResolver().isAddPermission(
		                "Perm_Add_Form_Data")) {
			tabIndexData = tabPositions;
			tabPositions++;
			if (formRunner == null) {
				formRunner = new FormRunnerWidget(images);
			}
			
			formRunner.setSubmitListener(this);
			tabs.add(formRunner, constants.label_data());
			noTab = false;
		}
		
		// If User has no permissions, load a view that has no controls.
		if (noTab) {
			tabs.add(table, constants.ascertain_permissionTab());
			loadPermissionLessView();
		}
		
		// Finalize set up.
		finalizeSetUp();
	}
	
	/**
	 * Finalizes this Widget's set up operation.
	 */
	private void finalizeSetUp() {
		tabs.selectTab(0);
		tabs.setWidth("100%");
		tabs.setHeight("100%");
		
		initWidget(tabs);
		
		tabs.addSelectionHandler(this);
		Window.addResizeHandler(this);
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				resize(Window.getClientWidth(), Window.getClientHeight());
			}
		});
		
		// Replace the form designer's authentication call back with that of the
		// form runner.
		FormRunnerEntryPoint.registerAuthenticationCallback();
	}
	
	/**
	 * Load view for user without permissions
	 */
	private void loadPermissionLessView() {
		StudyViewHelper.loadPermissionLessView(table, tabs);
	}
	
	/**
	 * @see org.openxdata.server.admin.client.listeners.ItemSelectionListener#onItemSelected(Composite,
	 *      Object)
	 */
	
	public void onItemSelected(Composite sender, Object item) {
		selectedItem = item;
		
		if (sender == dataListView) {
			if (item instanceof FormDataHeader) {
				FormDataHeader formDataHeader = (FormDataHeader) item;
				if (formDataHeader.deleted) {
					formDataHeader.deleted = false; // reset just in case we get
					                                // an exception or bug which
					                                // can accidentally delete
					                                // user data without
					                                // warning.
					deleteFormDeffered(formDataHeader);
				} else {
					openFormDeffered(formDataHeader);
				}
			} else if (item instanceof FormDefVersion) {
				openNewFormDeffered((FormDefVersion) item);
			}
		} else {
			propertiesView.onItemSelected(sender, item);
			if (tabs.getTabBar().getSelectedTab() == tabIndexProperties) {
				propertiesView.setFocus();
			} else if (tabs.getTabBar().getSelectedTab() == tabIndexFormDesigner
			        && item instanceof FormDefVersion) {
				
				FormDefVersion formDef = (FormDefVersion) item;
				dataCheck.itemHasFormData(formDef);
			}
		}
	}
	
	/**
	 * Gets the form version object with the given id.
	 * 
	 * @param formDefVersionId
	 *            the form version id.
	 * @return the form version object.
	 */
	private FormDefVersion getFormDefVersion(int formDefVersionId) {
		return StudyViewHelper.getFormDefVersion(formDefVersionId, studies);
	}
	
	@Override
	public void onResize(ResizeEvent event) {
		int width = event.getWidth();
		int height = event.getHeight();
		resize(width, height);
	}
	
	private void resize(int width, int height) {
		if (formDesigner != null) {
			formDesigner.onWindowResized(width, height);
		}
	}
	
	@Override
	public void onSelection(SelectionEvent<Integer> event) {
		Integer tabIndex = event.getSelectedItem();
		if (tabIndex == tabIndexFormDesigner && formDesigner != null) {
			if (!designSpliterSized) {
				formDesigner.setSplitPos("25%");
				designSpliterSized = true;
				
				// TODO Possibly needs to be done from a central place.
				setDateTimeFormats();
			}
			
			// TODO This line fixes:
			// http://trac.openxdata.org/openxdata/ticket/179
			// But also means that every time the user navigates back to the
			// design tab, the split pos is reset
			// and looses whatever resizing the user had.
			formDesigner.setSplitPos("25%");
			
			formDesigner.onWindowResized(Window.getClientWidth(),
			        Window.getClientHeight());
			
			if (selectedTabIndex != tabIndex) {
				if (selectedItem instanceof FormDefVersion) {
					dataCheck.itemHasFormData((FormDefVersion) selectedItem);
				}
			}
			
		}
		
		selectedTabIndex = tabIndex;
	}
	
	/**
	 * Loads a form version into the form designer.
	 * 
	 * @param readOnly
	 *            set to true to load the form version in read only mode, else
	 *            set to false.
	 * @param item
	 *            the form version object to design.
	 */
	public void designItem(boolean readOnly, Object item) {
		if (item == null) {
			return;
		}
		
		// We only design form versions.
		if (!(item instanceof FormDefVersion)) {
			return;
		}
		
		FormDefVersion formDefVersion = (FormDefVersion) item;
		
		// We need to have the formVersionId from the database to identify
		// this xform. This is why we need to first save before designing the
		// form.
		// This is not the ideal way of doing things, but is what we have now.
		if (formDefVersion.isNew()) {
			// TODO add message for internationalization purposes
			Window.alert("Please first save the study.");
			return;
		}
		
		// Select the form designer tab, if not already selected.
		if (tabs.getTabBar().getSelectedTab() != tabIndexFormDesigner) {
			this.selectedTabIndex = tabIndexFormDesigner;
			tabs.selectTab(tabIndexFormDesigner);
		}
		
		// Get the xforms and layout xml.
		String xform = formDefVersion.getXform();
		String layout = formDefVersion.getLayout();
		
		// If at least the xform is not empty, load it for editing in the
		// designer.
		if (xform != null && xform.trim().length() > 0) {
			// TODO The line before needs investigation as the selectedItem can
			// at times point to FormDataHeader
			
			// If the form was localised for the current locale, then translate
			// it to the locale.
			FormDefVersionText text = ((FormDefVersion) item)
			        .getFormDefVersionText(Context.getLocale());
			if (text != null) {
				
				xform = LanguageUtil.translate(XMLParser.parse(xform),
				        XMLParser.parse(text.getXformText())
				                .getDocumentElement());
				
				if (layout != null && layout.trim().length() > 0) {
					layout = LanguageUtil.translate(XMLParser.parse(layout),
					        XMLParser.parse(text.getLayoutText())
					                .getDocumentElement());
				}
			}
			formDesigner.loadForm(formDefVersion.getFormDefVersionId(), xform,
			        layout, "", readOnly);
		} else {
			// No xforms xml found, so design it as new.
			formDesigner.addNewForm(formDefVersion.getFormDef().getName() + "_"
			        + formDefVersion.getName(),
			        getDefaultFormBinding(formDefVersion),
			        formDefVersion.getFormDefVersionId());
		}
	}
	
	/**
	 * Creates a unique form binding which is unique.
	 * 
	 * @param formDefVersion
	 *            the form version definition object.
	 * @return the new form binding.
	 */
	public String getDefaultFormBinding(FormDefVersion formDefVersion) {
		return StudyViewHelper.getDefaultFormBinding(formDefVersion);
	}
	
	/**
	 * Sets the list of users.
	 * 
	 * @param users
	 *            the users list.
	 */
	public void setUsers(List<User> users) {
		dataListView.setUsers(users);
	}
	
	/**
	 * Sets the list of studies.
	 * 
	 * @param studies
	 *            the sudies list.
	 */
	public void setStudies(List<StudyDef> studies) {
		this.studies = studies;
		if (dataListView != null) {
			dataListView.setStudies(studies);
		}
	}
	
	public void setMappedForms(List<UserFormMap> userMappedForms) {
		if (dataListView != null) {
			dataListView.setUserMappedForms(userMappedForms);
		}
		
	}
	
	/**
	 * Opens a form with existing data in the form runner.
	 * 
	 * @param formDataHeader
	 *            the form data header.
	 */
	public void openFormDeffered(FormDataHeader formDataHeader) {
		
		FormUtil.dlg.setText(constants.label_opening_form());
		FormUtil.dlg.center();
		
		final Integer formDataId = formDataHeader.getFormDataId();
		final Integer formDefVersionId = formDataHeader.getFormDefVersionId();
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				try {
					formDefVersion = getFormDefVersion(formDefVersionId);
					assert (formDefVersion != null); // Should not be deleted as
					                                 // long as it has data.
					
					xformXml = formDefVersion.getXform();
					assert (xformXml != null); // How could data have been
					                           // collected without the xform?
					
					layoutXml = formDefVersion.getLayout();
					if (layoutXml == null || layoutXml.trim().length() == 0) {
						FormUtil.dlg.hide();
						// TODO add message for internationalization purposes
						Window.alert("Please first define the form layout");
						return;
					}
					
					Context.getStudyManagerService().getFormData(formDataId,
					        new OpenXDataAsyncCallback<FormData>() {
						        
						        @Override
						        public void onOtherFailure(Throwable caught) {
							        formDefVersion = null;
							        FormUtil.dlg.hide();
							        Window.alert(caught.getMessage());
						        }
						        
						        @Override
						        public void onSuccess(FormData data) {
							        formData = data;
							        
							        tabs.selectTab(tabIndexData);
							        
							        setDateTimeFormats();
							        
							        FormDefVersionText text = formDefVersion
							                .getFormDefVersionText(Context
							                        .getLocale());
							        if (text != null) {
								        xformXml = LanguageUtil.translate(
								                XMLParser.parse(xformXml),
								                XMLParser.parse(
								                        text.getXformText())
								                        .getDocumentElement());
								        layoutXml = LanguageUtil.translate(
								                XMLParser.parse(layoutXml),
								                XMLParser.parse(
								                        text.getLayoutText())
								                        .getDocumentElement());
							        }
							        
							        formRunner.loadForm(
							                formData.getFormDataId(), xformXml,
							                formData.getData(), layoutXml, null);
							        
							        FormUtil.dlg.hide();
							        formDefVersion = null;
						        }
					        });
				} catch (Exception ex) {
					formDefVersion = null;
					FormUtil.dlg.hide();
					FormUtil.displayException(ex);
				}
			}
		});
	}
	
	/**
	 * Sets the date and time formats of the form designer and runner.
	 */
	private void setDateTimeFormats() {
		StudyViewHelper.setDateTimeFormats();
	}
	
	/**
	 * Opens a new form for data entry in the form runner.
	 * 
	 * @param formVersion
	 *            the form version of the new form to open.
	 */
	public void openNewFormDeffered(FormDefVersion formVersion) {
		
		formDefVersion = formVersion;
		formData = null;
		
		FormUtil.dlg.setText(constants.label_opening_form());
		FormUtil.dlg.center();
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				try {
					xformXml = formDefVersion.getXform();
					
					assert (xformXml != null); // How could data have been
					                           // collected without the xform?
					layoutXml = formDefVersion.getLayout();
					if (layoutXml == null || layoutXml.trim().length() == 0) {
						FormUtil.dlg.hide();
						// TODO add message for internationalization purposes
						Window.alert("Please first define the form layout");
						return;
					}
					tabs.selectTab(tabIndexData);
					
					// TODO Need not duplicate this code
					setDateTimeFormats();
					
					FormDefVersionText text = formDefVersion
					        .getFormDefVersionText(Context.getLocale());
					if (text != null) {
						xformXml = LanguageUtil.translate(XMLParser
						        .parse(xformXml),
						        XMLParser.parse(text.getXformText())
						                .getDocumentElement());
						layoutXml = LanguageUtil.translate(XMLParser
						        .parse(layoutXml),
						        XMLParser.parse(text.getLayoutText())
						                .getDocumentElement());
					}
					
					formRunner.loadForm(formDefVersion.getFormDefVersionId(),
					        xformXml, null, layoutXml, null);
					
					FormUtil.dlg.hide();
				} catch (Exception ex) {
					FormUtil.dlg.hide();
					FormUtil.displayException(ex);
				}
			}
		});
	}
	
	/**
	 * Deletes data collected in a form.
	 * 
	 * @param formDataHeader
	 *            the header for the form data.
	 */
	public void deleteFormDeffered(FormDataHeader formDataHeader) {
		
		FormUtil.dlg.setText("Deleting Form Data");
		FormUtil.dlg.center();
		
		final FormDataHeader frmDataHeader = formDataHeader;
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				try {
					Context.getStudyManagerService().deleteFormData(
					        frmDataHeader.getFormDataId(),
					        new OpenXDataAsyncCallback<Void>() {
						        
						        @Override
						        public void onOtherFailure(Throwable caught) {
							        formDefVersion = null;
							        FormUtil.dlg.hide();
							        Window.alert(caught.getMessage());
						        }
						        
						        @Override
						        public void onSuccess(Void result) {
							        formDefVersion = null;
							        FormUtil.dlg.hide();
							        Window.alert("Form data deleted successfully");
						        }
					        });
				} catch (Exception ex) {
					formDefVersion = null;
					FormUtil.dlg.hide();
					FormUtil.displayException(ex);
				}
			}
		});
	}
	
	/**
	 * @see org.purc.purcforms.client.controller.SubmitListener#onSubmit(String)
	 */
	@Override
	public void onSubmit(String xml) {
		if (formData != null) {
			saveExistingForm(xml);
		} else {
			saveNewForm(xml);
		}
	}
	
	/**
	 * @see org.purc.purcforms.client.controller.SubmitListener#onCancel()
	 */
	@Override
	public void onCancel() {
		tabs.selectTab(tabIndexDataList);
	}
	
	/**
	 * Saves data that has been edited in an existing form.
	 * 
	 * @param xml
	 *            the xforms model xml containing the data to save.
	 */
	private void saveExistingForm(String xml) {
		StudyViewHelper.saveExistingForm(xml, xformXml, formData);
		
		// Go back to the data list tab.
		tabs.selectTab(tabIndexDataList);
	}
	
	/**
	 * Saves data that has been entered in a new form.
	 * 
	 * @param xml
	 *            the xforms xml model containing the data to save.
	 */
	private void saveNewForm(String xml) {
		
		assert (formDefVersion != null);
		
		final String dataXml = xml;
		
		FormUtil.dlg.setText(constants.label_saving_form());
		FormUtil.dlg.center();
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				try {
					FormData data = new FormData();
					data.setData(dataXml);
					data.setDescription(Utilities.getDescriptionTemplate(
					        xformXml, dataXml));
					data.setCreator(Context.getAuthenticatedUser());
					data.setDateCreated(new Date());
					data.setFormDefVersionId(formDefVersion
					        .getFormDefVersionId());
					
					Context.getFormServiceAsync().saveFormData(data,
					        new OpenXDataAsyncCallback<FormData>() {
						        
						        @Override
						        public void onOtherFailure(Throwable caught) {
							        FormUtil.dlg.hide();
							        Window.alert(caught.getMessage());
						        }
						        
						        @Override
						        public void onSuccess(FormData result) {
							        FormUtil.dlg.hide();
							        tabs.selectTab(tabIndexDataList);
							        // TODO add message for internationalization
							        // purposes
							        Window.alert("Form Data Submitted Successfully");
						        }
					        });
				} catch (Exception ex) {
					FormUtil.dlg.hide();
					FormUtil.displayException(ex);
				}
			}
		});
	}
	
	/**
	 * @see org.purc.purcforms.client.controller.IFormSaveListener#onSaveForm(int,
	 *      String, String)
	 */
	@Override
	public boolean onSaveForm(int formId, String xformsXml, String layoutXml,
	        String javaScriptSrc) {
		return StudyViewHelper.onSaveForm(formId, xformsXml, layoutXml);
	}
	
	/**
	 * @see org.purc.purcforms.client.controller.IFormSaveListener#onSaveLocaleText(int,
	 *      String, String)
	 */
	@Override
	public void onSaveLocaleText(int formId, String xformsLocaleText,
	        String layoutLocaleText) {
		StudyViewHelper.onSaveLocaleText(formId, xformsLocaleText,
		        layoutLocaleText);
	}
	
	/**
	 * Check if the form designer in open.
	 * 
	 * @return true of the form designer is open, else false.
	 */
	public boolean isInFormDesignMode() {
		return (tabs.getTabBar().getSelectedTab() == tabIndexFormDesigner && formDesigner
		        .getSelectedForm() != null);
	}
	
	/**
	 * @see org.openxdata.server.admin.client.view.listeners.OpenXDataViewExtendedApplicationEventListener#format()
	 */
	@Override
	public void format() {
		if (formDesigner != null) {
			formDesigner.format();
		}
	}
	
	/**
	 * @see org.openxdata.server.admin.client.view.listeners.OpenXDataViewExtendedApplicationEventListener#alignLeft()
	 */
	@Override
	public void alignLeft() {
		if (formDesigner != null) {
			formDesigner.alignLeft();
		}
	}
	
	/**
	 * @see org.openxdata.server.admin.client.view.listeners.OpenXDataViewExtendedApplicationEventListener#alignRight()
	 */
	@Override
	public void alignRight() {
		if (formDesigner != null) {
			formDesigner.alignRight();
		}
	}
	
	/**
	 * @see org.openxdata.server.admin.client.view.listeners.OpenXDataViewExtendedApplicationEventListener#alignTop()
	 */
	@Override
	public void alignTop() {
		if (formDesigner != null) {
			formDesigner.alignTop();
		}
	}
	
	/**
	 * @see org.openxdata.server.admin.client.view.listeners.OpenXDataViewExtendedApplicationEventListener#makeSameSize()
	 */
	@Override
	public void makeSameSize() {
		if (formDesigner != null) {
			formDesigner.makeSameSize();
		}
	}
	
	/**
	 * @see org.openxdata.server.admin.client.view.listeners.OpenXDataViewExtendedApplicationEventListener#makeSameHeight()
	 */
	@Override
	public void makeSameHeight() {
		if (formDesigner != null) {
			formDesigner.makeSameHeight();
		}
	}
	
	/**
	 * @see org.openxdata.server.admin.client.view.listeners.OpenXDataViewExtendedApplicationEventListener#makeSameWidth()
	 */
	@Override
	public void makeSameWidth() {
		if (formDesigner != null) {
			formDesigner.makeSameWidth();
		}
	}
	
	/**
	 * @see org.openxdata.server.admin.client.view.listeners.OpenXDataViewExtendedApplicationEventListener#alignBottom()
	 */
	@Override
	public void alignBottom() {
		if (formDesigner != null) {
			formDesigner.alignBottom();
		}
	}
	
	/**
	 * @see org.openxdata.server.admin.client.view.listeners.OpenXDataViewExtendedApplicationEventListener#openForm()
	 */
	@Override
	public void openForm() {
		if (isInFormDesignMode()) {
			formDesigner.openForm();
		} else {
			designItem(true, selectedItem);
		}
	}
	
	/**
	 * @see org.openxdata.server.admin.client.view.listeners.OpenXDataViewExtendedApplicationEventListener#moveItemUp()
	 */
	@Override
	public void moveItemUp() {
		if (formDesigner != null) {
			formDesigner.moveItemUp();
		}
	}
	
	/**
	 * @see org.openxdata.server.admin.client.view.listeners.OpenXDataViewExtendedApplicationEventListener#moveItemDown()
	 */
	@Override
	public void moveItemDown() {
		if (formDesigner != null) {
			formDesigner.moveItemDown();
		}
	}
	
	/**
	 * @see org.openxdata.server.admin.client.view.listeners.OpenXDataViewExtendedApplicationEventListener#cutItem()
	 */
	@Override
	public void cutItem() {
		if (formDesigner != null) {
			formDesigner.cutItem();
		}
	}
	
	/**
	 * @see org.openxdata.server.admin.client.view.listeners.OpenXDataViewExtendedApplicationEventListener#copyItem()
	 */
	@Override
	public void copyItem() {
		if (formDesigner != null) {
			formDesigner.copyItem();
		}
	}
	
	/**
	 * @see org.openxdata.server.admin.client.view.listeners.OpenXDataViewExtendedApplicationEventListener#pasteItem()
	 */
	@Override
	public void pasteItem() {
		if (formDesigner != null) {
			formDesigner.pasteItem();
		}
	}
	
	/**
	 * @see org.openxdata.server.admin.client.view.event.dispatcher.EventDispatcher#onNewItem()
	 */
	public void addNewItem() {
		if (formDesigner != null) {
			formDesigner.addNewItem();
		}
	}
	
	/**
	 * @see org.openxdata.server.admin.client.view.event.dispatcher.EventDispatcher#onNewChildItem()
	 */
	public void addNewChildItem() {
		if (formDesigner != null) {
			formDesigner.addNewChildItem();
		}
	}
	
	/**
	 * @see org.openxdata.server.admin.client.view.listeners.OpenXDataViewExtendedApplicationEventListener#onDeleteItem()
	 */
	public void deleteSelectedItem() {
		if (formDesigner != null) {
			formDesigner.deleteSelectedItem();
		}
	}
	
	/**
	 * @see org.openxdata.server.admin.client.view.listeners.OpenXDataViewExtendedApplicationEventListener#onRefresh()
	 */
	public void refreshItem() {
		if (formDesigner != null) {
			formDesigner.refreshItem();
		}
	}
	
	/**
	 * Saves study contents.
	 * 
	 * @return true of the not in form designer mode, else false.
	 */
	public boolean saveStudies() {
		if (!isInFormDesignMode()) {
			return true;
		}
		
		formDesigner.saveSelectedForm();
		return false;
	}
	
	/**
	 * Switches to a given locale.
	 * 
	 * @param locale
	 *            the locale key.
	 */
	public void changeLocale(String locale) {
		
		if (!isInFormDesignMode()) {
			return;
		}
		
		StudyViewHelper.changeLocale(locale, selectedItem, formDesigner);
	}
	
	/**
	 * @see org.openxdata.server.admin.client.view.listeners.FormVersionOpenDialogListener#onOptionSelected(int)
	 */
	@Override
	public void onOptionSelected(int option) {
		if (option == 0) {//Create new version
			StudiesTreeView view = widgetFactory.getStudiesTreeView();//TODO This is a temporary hack
			FormDefVersion copyVersion = (FormDefVersion) selectedItem;
			view.addNewItem(copyVersion.getXform());
		} else if (option == 1) {
			designItem(true, selectedItem);
			
		} else if (option == 2) {
			return;
		} else {
			designItem(false, selectedItem);
		}
	}
	
	/**
	 * @see org.openxdata.server.admin.client.view.listeners.OnDataCheckListener#onDataCheckComplete(boolean,
	 *      String)
	 */
	@Override
	public void onDataCheckComplete(boolean hasData, String currentItem) {
		FormUtil.dlg.hide();
		
		if (hasData) {
			StudyViewHelper.onDataCheckComplete(this);
		} else {
			designItem(false, selectedItem);
		}
	}
	
	@Override
	public void update(OpenXDataObservable observable,
	        Object observedModelObjects) {
		// do nothing
	}
	
	@Override
	public void updateStudies(OpenXDataObservable observable,
	        List<StudyDef> studies) {
		setStudies(studies);
	}
	
	@Override
	public void updateUserMappedForms(OpenXDataObservable observable,
	        List<UserFormMap> userMappedForms) {
		setMappedForms(userMappedForms);
	}
	
	@Override
	public void updateUserMappedStudies(OpenXDataObservable observable,
	        List<UserStudyMap> userMappedStudies) {
		// do nothing
	}
	
	@Override
	public void exportAsPdf() {
		// do nothing
	}
	
	@Override
	public void onDeleteItem() {
		if (openxdataStackPanel.getSelectedIndex() == OpenXDataStackPanelConstants.INDEX_STUDIES) {
			if (RolesListUtil.getPermissionResolver().isDeleteStudies()) {
				if (isInFormDesignMode()) {
					deleteSelectedItem();
				}
			} else {
				Window.alert("You do not have sufficient priviledges to delete the selected Item! Contact your system administrator");
			}
		}
	}
	
	public void onExport() {
		// do nothing
	}
	
	public void onImport() {
		// do nothing
	}
	
	@Override
	public void onNewChildItem() {
		if (openxdataStackPanel.getSelectedIndex() == OpenXDataStackPanelConstants.INDEX_STUDIES) {
			if (RolesListUtil.getPermissionResolver().isAddStudies()) {
				if (isInFormDesignMode()) {
					addNewChildItem();
				}
			} else {
				Window.alert("You do not have sufficient priviledges to add items to the Study! Contact your system administrator");
			}
		}
	}
	
	@Override
	public void onNewItem() {
		if (openxdataStackPanel.getSelectedIndex() == OpenXDataStackPanelConstants.INDEX_STUDIES) {
			if (RolesListUtil.getPermissionResolver().isAddStudies()) {
				if (isInFormDesignMode()) {
					addNewItem();
				}
			} else {
				Window.alert("You do not have sufficient priviledges to add items to the Study! Contact your system administrator");
			}
		}
	}
	
	public void onOpen() {
		openForm();
	}
	
	@Override
	public void onRefresh() {
		MainViewControllerFacade.refreshData();
	}
	
	@Override
	public void onSave() {
		if (!this.saveStudies()) {
			return;
		}
	}
	
	/**
	 * Checks if the manipulated object has a specified property.
	 * 
	 * @param arg
	 *            - property to check on object being manipulated.
	 * @param userMappedPermissions
	 *            - List of available objects to check from.
	 * 
	 * @return <code> True only and only if(object.exists() == true)</code>
	 */
	public static boolean hasProperty(Object arg,
	        List<UserFormMap> userMappedPermissions) {
		
		boolean ret = false;
		for (UserFormMap x : userMappedPermissions) {
			if (x.getFormId() == ((FormDef) arg).getFormId()) {
				ret = true;
				break;
			}
		}
		
		return ret;
	}

     private void initHandlers() {
        ItemSelectedEvent.addHandler(eventBus, new ItemSelectedEvent.Handler<StudyDef>() {

            @Override
            public void onSelected(Composite sender, StudyDef item) {
                onItemSelected(sender, item);
            }
        }).forClass(StudyDef.class);
        ItemSelectedEvent.addHandler(eventBus, new ItemSelectedEvent.Handler<FormDef>() {

            @Override
            public void onSelected(Composite sender, FormDef item) {
                onItemSelected(sender, item);
            }
        }).forClass(FormDef.class);
        ItemSelectedEvent.addHandler(eventBus, new ItemSelectedEvent.Handler<FormDefVersion>() {

            @Override
            public void onSelected(Composite sender, FormDefVersion item) {
                onItemSelected(sender, item);
            }
        }).forClass(FormDefVersion.class);

        DesignFormEvent.Handler designHandler = new DesignFormEvent.Handler() {

            @Override
            public void onDesignReadOnly(FormDefVersion version) {
                designItem(true, version);
            }

            @Override
            public void onDesign(FormDefVersion version) {
                designItem(false, version);
            }
        };
        eventBus.addHandler(DesignFormEvent.TYPE, designHandler);

        FormDataHeaderEvent.Handler formDataHandler = new FormDataHeaderEvent.Handler() {

            @Override
            public void onViewRequest(FormDataHeader header) {
                openFormDeffered(header);
            }

            @Override
            public void onDelete(FormDataHeader header) {
                header.deleted = false;
                deleteFormDeffered(header);
            }

            @Override
            public void onCreateRequest(FormDefVersion version) {
                openNewFormDeffered(version);
            }
        };
        eventBus.addHandler(FormDataHeaderEvent.TYPE, formDataHandler);
    }
	
}
