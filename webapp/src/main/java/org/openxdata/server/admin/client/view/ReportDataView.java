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

import org.openxdata.server.admin.client.Context;
import org.openxdata.server.admin.client.controller.callback.OpenXDataAsyncCallback;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.model.Report;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.purc.purcforms.client.util.FormUtil;

/**
 * This widget displays data in a report and also allows you export it to PDF
 * format.
 * 
 * @author daniel
 * 
 */
public class ReportDataView extends Composite {
	
	/** The widgets to display reports in html format. */
	private HTML reportWidget = new HTML();
	
	/** The current report we are displaying. */
	private Report report;
	
	/** The sql statement that feeds the report with data. */
	private String sql = null;
	
	// TODO May need to use this through an interface.
	/**
	 * Reference to the report properties view. For now the only use of this is
	 * to to get us the report definition sql.
	 */
	private ReportView reportView;
	
	/**
	 * Creates the report data view.
	 * 
	 * @param rptView
	 *            a reference to report properties view
	 */
	public ReportDataView(ReportView rptView) {
		this.reportView = rptView;
		
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.add(reportWidget);
		
		Utilities.maximizeWidget(verticalPanel);
		Utilities.maximizeWidget(reportWidget);
		
		initWidget(verticalPanel);
	}
	
	/**
	 * Reloads report data.
	 */
	public void refresh() {
		sql = reportView.getSql();
		if (report != null && sql != null && sql.trim().length() > 0)
			loadReportData();
	}
	
	/**
	 * Exports the selected report to PDF format.
	 */
	public void exportAsPdf() {
		sql = reportView.getSql();
		if (report != null && sql != null && sql.trim().length() > 0)
			exportPdf();
	}
	
	// TODO Doesn't this belong to a controller?.
	/**
	 * Loads report data from the server.
	 */
	private void loadReportData() {
		FormUtil.dlg.setText("Loading Report");
		FormUtil.dlg.center();
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				try {
					Context.getReportService().getReportData(report, "html",
					        new OpenXDataAsyncCallback<String>() {
						        @Override
						        public void onOtherFailure(Throwable caught) {
							        FormUtil.dlg.hide();
							        Window.alert(caught.getMessage());
						        }
						        
						        @Override
						        public void onSuccess(String html) {
							        loadReport(html);
							        FormUtil.dlg.hide();
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
	 * Exports the displayed report to PDF format.
	 */
	private void exportPdf() {
		FormUtil.dlg.setText("Exporting Report");
		FormUtil.dlg.center();
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				try {
					reportWidget
					        .setHTML("<object type=\"application/pdf\" data=\"report?format=pdf&name=rates.pdf&reportId="
					                + report.getReportId()
					                + "\" width=\"500\" height=\"650\" > </object>");
					FormUtil.dlg.hide();
				} catch (Exception ex) {
					FormUtil.dlg.hide();
					FormUtil.displayException(ex);
				}
			}
		});
	}
	
	/**
	 * Sets the report definition object for the report to display.
	 * 
	 * @param report
	 */
	public void setReport(Report report) {
		this.report = report;
		sql = null;
	}
	
	/**
	 * Displays report html.
	 * 
	 * @param html
	 *            the report html.
	 */
	public void loadReport(String html) {
		reportWidget.setHTML(html);
	}
}
