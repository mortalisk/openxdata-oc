package org.openxdata.client.views;

import org.openxdata.client.AppMessages;
import org.openxdata.client.util.ProgressIndicator;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

/**
 * A FormPanel that handles upload of a file
 */
public abstract class UploadFileFormPanel extends FormPanel {
	
	private AppMessages appMessages = GWT.create(AppMessages.class);
	
	private FileUploadField uploadFile;
	
	final String ACTION_URL = GWT.getModuleBaseURL() + "formopen";

	public UploadFileFormPanel() {
		setHeaderVisible(false);
		setFrame(false);
		setBorders(false);
		setBodyBorder(false);
		setFieldWidth(350);
		FormLayout layout = new FormLayout();
		layout.setDefaultWidth(350);
		setLayout(layout);

		setAction(ACTION_URL);
		setEncoding(Encoding.MULTIPART);
		setMethod(Method.POST);
		addListener(Events.Submit, new Listener<FormEvent>() {
			@Override
			public void handleEvent(FormEvent be) {
				handleUploadResponse();
			}
		});
	}
	
	public void initialize() {
		uploadFile =  new FileUploadField();
		uploadFile.setAllowBlank(false);
		uploadFile.setName("filecontents");
		uploadFile.setFieldLabel(appMessages.filename());
		
		add(uploadFile);		
	}
	
	public abstract void handleUploadedFile(String fileContents);
	
	public void uploadFile() {
		String action = ACTION_URL;
		if (action.contains("?")) {
			action += "&";
		} else {
			action += "?";
		}
		action += "pathname="+uploadFile.getValue();
		setAction(action);
		submit();
	}
	
	private void handleUploadResponse() {
		GWT.log("ItemImportView : Handling Imported Item Response.");
		
		ProgressIndicator.showProgressBar();
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,"formopen");

		try {
			builder.sendRequest(null, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, final Response response) {
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							try {
								ProgressIndicator.hideProgressBar();
								UploadFileFormPanel.this.handleUploadedFile(response.getText());
							}
							catch(Exception ex) {
								ProgressIndicator.hideProgressBar();
								MessageBox.alert(appMessages.error(), appMessages.importParseError(), null);
							}
						}
					});
				}
				
				@Override
				public void onError(Request request, Throwable ex) {
					ProgressIndicator.hideProgressBar();
					MessageBox.alert(appMessages.error(), ex.getLocalizedMessage(), null);
				}
			}); 
		}
		catch (RequestException ex) {
			ProgressIndicator.hideProgressBar();
			GWT.log(ex.getLocalizedMessage());
			MessageBox.alert(appMessages.error(), appMessages.importError(), null);
		}
	}
	
	public String getUploadFileName(){
		return uploadFile.getValue();
	}
	
}
