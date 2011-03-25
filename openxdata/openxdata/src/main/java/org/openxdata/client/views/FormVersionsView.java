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
package org.openxdata.client.views;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.RowEditorEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import java.util.ArrayList;
import java.util.List;
import org.openxdata.client.AppMessages;
import org.openxdata.client.controllers.FormVersionsController;
import org.openxdata.client.model.FormVersionSummary;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;

/**
 *UI for viewing all version of a selected form
 * @author victor
 */
public class FormVersionsView extends View {

    final AppMessages appMessages = GWT.create(AppMessages.class);
    private FormDef form;
    private Grid<FormVersionSummary> grid;
    private Window window;
    private ContentPanel cp;

    public FormVersionsView(Controller controller) {
        super(controller);
    }

    @Override
    protected void initialize() {
        GWT.log("FormVersionsView : initialize");
        // create column config - defines the column in the grid
        window = new Window();
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
        ColumnConfig column1 = new ColumnConfig("id", appMessages.id(), 20);
        TextField<String> textFld = new TextField<String>();
        textFld.setAllowBlank(false);
        column1.setEditor(new CellEditor(textFld));
        configs.add(column1);

        ColumnConfig column2 = new ColumnConfig("version", appMessages.formVersion(), 290);
        TextField<String> textFld2 = new TextField<String>();
        textFld2.setAllowBlank(false);
        column2.setEditor(new CellEditor(textFld2));
        configs.add(column2);
        
        ColumnConfig column3 = new ColumnConfig("description", appMessages.formVersionDescription(), 580);
        TextField<String> textFld3 = new TextField<String>();
        textFld3.setAllowBlank(false);
        column3.setEditor(new CellEditor(textFld3));
        configs.add(column3);

        ColumnModel cm = new ColumnModel(configs);
        cm.setHidden(0, true);
        ListStore<FormVersionSummary> store = new ListStore<FormVersionSummary>();
        RowEditor<FormVersionSummary> re = new RowEditor<FormVersionSummary>();
        re.addListener(Events.AfterEdit, new Listener<RowEditorEvent>() {

            @Override
            public void handleEvent(RowEditorEvent be) {
                GWT.log("Events.AfterEdit");
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

                    @Override
                    public void execute() {
                        FormVersionSummary summary = grid.getSelectionModel().getSelectedItem();
                        for(FormDefVersion version:form.getVersions()){
                            if(version.getName().equals(summary.getFormVersion())){
                                version.setName(summary.getFormVersion());
                                version.setDescription(summary.getDescription());
                            }
                        }
                        ((FormVersionsController)FormVersionsView.this.getController())
                                .saveFormDefVersion(form);
                    }
                });
            }
        });
        grid = new Grid<FormVersionSummary>(store, cm);
        grid.setAutoExpandColumn("description");
        grid.setBorders(true);
        grid.addPlugin(re);
        cp = new ContentPanel();
        cp.setLayout(new FitLayout());
        cp.add(grid);
    }

    @Override
    protected void handleEvent(AppEvent event) {
        GWT.log("FormVersionsView : handleEvent");
        if (event.getType() == FormVersionsController.FORMVERSIONLIST) {
            GWT.log("FormVersionsView : FormVersionsView.FORMVERSIONLIST: view");
            form = event.getData();

            // Initialize Window
            window.setModal(true);
            window.setPlain(true);
            window.setHeading(form.getName());
            window.setMaximizable(true);
            window.setDraggable(true);
            window.setResizable(true);
            window.setScrollMode(Scroll.AUTO);
            window.setLayout(new FitLayout());
            for (FormDefVersion version : form.getVersions()) {
                grid.getStore().add(new FormVersionSummary(String.valueOf(version.getFormDefVersionId()), version.getName(),version.getDescription(), version.getFormDef().getName(), version.getCreator().getName(), version.getDateChanged()));
            }
            window.add(cp);
            window.setDraggable(true);
            window.setResizable(true);

            window.addButton(new Button(appMessages.cancel(), new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    closeWindow();
                }
            }));
            window.setSize(600, 400);
            window.show();
        }
    }

    public void closeWindow() {
        window.hide();
    }
}