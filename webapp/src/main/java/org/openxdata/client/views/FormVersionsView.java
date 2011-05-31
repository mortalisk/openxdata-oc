package org.openxdata.client.views;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
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
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Element;
import java.util.ArrayList;
import java.util.List;
import org.openxdata.client.AppMessages;
import org.openxdata.client.controllers.FormVersionsController;
import org.openxdata.client.model.FormVersionSummary;
import org.openxdata.client.util.ProgressIndicator;
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
    private Button publishBtn;

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
        publishBtn = new Button(appMessages.formVersionDefault());
        RowEditor<FormVersionSummary> re = new RowEditor<FormVersionSummary>(){
        @Override
			protected void onRender(Element target, int index) {
				super.onRender(target, index);
				if (btns != null) {
					btns.setLayout(new TableLayout(3));
					publishBtn.setMinWidth(getMinButtonWidth());
					btns.add(publishBtn);
					btns.layout(true);
				}
			}
            @Override
			protected void afterRender() {
				super.afterRender();
				if (renderButtons) {
				      btns.setWidth((getMinButtonWidth() * 3) + (5 * 3) + (3 * 4));
				}
			}
        };
        publishBtn.addListener(Events.Select, new Listener<ButtonEvent>(){

            @Override
            public void handleEvent(ButtonEvent be) {
                FormVersionSummary summary = grid.getSelectionModel().getSelectedItem();
                        for(FormDefVersion version:form.getVersions()){
                            if(version.getName().equals(summary.getFormVersion())){
                                version.setName(summary.getFormVersion());
                                version.setDescription(summary.getDescription());
                                version.getFormDef().turnOffOtherDefaults(version);
                            }
                        }
                        ((FormVersionsController)FormVersionsView.this.getController())
                                .saveFormDefVersion(form);
            }
        });
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
