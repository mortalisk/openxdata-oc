package org.openxdata.client.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openxdata.client.AppMessages;
import org.openxdata.client.controllers.OpenClinicaStudyController;
import org.openxdata.client.model.OpenclinicaStudySummary;
import org.openxdata.server.admin.model.OpenclinicaStudy;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;

public class OpenClincaStudyView extends View {

	private Window window;
	private ContentPanel cp;
	private Button importStudies, closelButton, exportButton;

	private Grid<OpenclinicaStudySummary> grid;
	private ListStore<OpenclinicaStudySummary> store;

	private ColumnModel cm;
	final AppMessages appMessages = GWT.create(AppMessages.class);

	List<OpenclinicaStudySummary> studies = new ArrayList<OpenclinicaStudySummary>();

	public OpenClincaStudyView(Controller controller) {
		super(controller);
	}

	@Override
	protected void initialize() {

		GWT.log("OpenClincaStudyView : initialize");

		GridCellRenderer<OpenclinicaStudySummary> nameRenderer = new GridCellRenderer<OpenclinicaStudySummary>() {
			public String render(OpenclinicaStudySummary study,
					String property, ColumnData config, int rowIndex,
					int colIndex, ListStore<OpenclinicaStudySummary> store,
					Grid<OpenclinicaStudySummary> grid) {
				return "<span>" + study.getName() + "</span>";
			}
		};

		GridCellRenderer<OpenclinicaStudySummary> oidRenderer = new GridCellRenderer<OpenclinicaStudySummary>() {
			public String render(OpenclinicaStudySummary study,
					String property, ColumnData config, int rowIndex,
					int colIndex, ListStore<OpenclinicaStudySummary> store,
					Grid<OpenclinicaStudySummary> grid) {
				return "<span>" + study.getOID() + "</span>";
			}
		};

		GridCellRenderer<OpenclinicaStudySummary> identifierRenderer = new GridCellRenderer<OpenclinicaStudySummary>() {
			public String render(OpenclinicaStudySummary study,
					String property, ColumnData config, int rowIndex,
					int colIndex, ListStore<OpenclinicaStudySummary> store,
					Grid<OpenclinicaStudySummary> grid) {
				return "<span>" + study.getIdentifier() + "</span>";
			}
		};

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();
		column.setId("name");
		column.setHeader("Name");
		column.setWidth(200);
		column.setRowHeader(true);
		configs.add(column);
		column.setRenderer(nameRenderer);

		column = new ColumnConfig();
		column.setId("oid");
		column.setHeader("OID");
		column.setWidth(100);
		configs.add(column);
		column.setRenderer(oidRenderer);

		column = new ColumnConfig();
		column.setId("identifier");
		column.setHeader("Identifier");
		column.setAlignment(HorizontalAlignment.RIGHT);
		column.setWidth(75);
		configs.add(column);
		column.setRenderer(identifierRenderer);

		store = new ListStore<OpenclinicaStudySummary>();

		cm = new ColumnModel(configs);

		cp = new ContentPanel();
		cp.setBodyBorder(true);
		cp.setHeading("Available Studies");
		cp.setButtonAlign(HorizontalAlignment.CENTER);
		cp.setLayout(new FitLayout());
		cp.getHeader().setIconAltText("Grid Icon");
		cp.setSize(600, 300);

		grid = new Grid<OpenclinicaStudySummary>(store, cm);
		grid.setStyleAttribute("borderTop", "none");
		grid.setAutoExpandColumn("name");
		grid.setBorders(false);
		grid.setStripeRows(true);
		grid.setColumnLines(true);
		grid.setColumnReordering(true);
		grid.getAriaSupport().setLabelledBy(cp.getHeader().getId() + "-label");
		cp.add(grid);

		importStudies = new Button(appMessages.importX());
		importStudies.addListener(Events.Select, new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(ButtonEvent be) {
				importOpenClinicaStudy();
			}

		});

		exportButton = new Button(appMessages.export());
		exportButton.addListener(Events.Select, new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(ButtonEvent be) {
				exportOpenClinicaStudyData();
			}

		});

		closelButton = new Button(appMessages.close());
		closelButton.addListener(Events.Select, new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(ButtonEvent be) {
				window.hide();
			}

		});
		cp.addButton(importStudies);
		cp.addButton(exportButton);
		cp.addButton(closelButton);
	}

	protected void exportOpenClinicaStudyData() {
		OpenClinicaStudyController controller = (OpenClinicaStudyController) this.controller;
		OpenclinicaStudySummary studySummary = grid.getSelectionModel()
				.getSelectedItem();

		controller.exportStudyToOpenclinica(studySummary);

	}

	protected void importOpenClinicaStudy() {
		OpenclinicaStudySummary studySummary = grid.getSelectionModel()
				.getSelectedItem();
		((OpenClinicaStudyController) this.controller)
				.importOpenClinicaStudy(studySummary.getIdentifier());
	}

	protected void hide() {
		window.hide();
	}

	private void showWindow(ContentPanel cp) {
		GWT.log("OpenClincaStudyView : createWindow");

		if (window == null) {
			window = new Window();
			window.setModal(true);
			window.setPlain(true);
			window.setHeading("OpenClinica Study Management");
			window.setMaximizable(true);
			window.setDraggable(true);
			window.setResizable(true);
			window.setScrollMode(Scroll.AUTO);
			window.setLayout(new FitLayout());
			window.add(cp);
			window.setSize(600, 450);
		}
		window.show();
	}

	@Override
	protected void handleEvent(AppEvent event) {
		GWT.log("OpenClinicaStudyView: handleEvent");
		showWindow(cp);
	}

	public void setStudies(Set<OpenclinicaStudy> studies) {
		this.studies.clear();
		for (OpenclinicaStudy s : studies) {
			this.studies.clear();
			this.studies.add(new OpenclinicaStudySummary(s));
		}
		store.removeAll();
		store.add(this.studies);
	}
}
