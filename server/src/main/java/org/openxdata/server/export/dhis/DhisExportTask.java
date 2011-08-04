package org.openxdata.server.export.dhis;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.TaskDef;
import org.openxdata.server.export.http.HttpPostExportTask;

public class DhisExportTask extends HttpPostExportTask {
	
	public DhisExportTask() {
		super();
	}

	public DhisExportTask(TaskDef taskDef) {
		super(taskDef);
	}

	@Override
	protected String getData(FormData formData, FormDefVersion formDefVersion) {
		try {
			InputStream inputStream = this.getClass().getResourceAsStream("transform.xslt");
			Source xmlSource = new StreamSource(new StringReader(formData.getData()));
			Source xsltSource = new StreamSource(inputStream);

			StringWriter output = new StringWriter();
			Result result = new StreamResult(output);

			TransformerFactory transFact = TransformerFactory.newInstance();
			Transformer trans = transFact.newTransformer(xsltSource);

			// assumes that the orgunitid is stored in the user.phoneNo field
			trans.setParameter("orgunitid", formData.getCreator().getPhoneNo());
			
			trans.transform(xmlSource, result);
			return output.toString();
			
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		return formData.getData();
	}

}