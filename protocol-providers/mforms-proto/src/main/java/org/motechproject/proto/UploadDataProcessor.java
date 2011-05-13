package org.motechproject.proto;

import java.util.ArrayList;
import java.util.List;

import org.fcitmuk.epihandy.DeserializationListenerAdapter;
import org.fcitmuk.epihandy.FormData;
import org.fcitmuk.epihandy.StudyData;

/**
 * Implements the DeserializationListener which is invoked during the deserialisation of Form Data.
 */
public class UploadDataProcessor extends DeserializationListenerAdapter {

	int formsProcessed = 0;
	List<List<String>> studyFormXmlList = new ArrayList<List<String>>();

	@Override
	public void processingStudy(StudyData studyData) {
		List<String> formList = new ArrayList<String>();
		studyFormXmlList.add(formList);
	}

	@Override
	public void formProcessed(StudyData studyData, FormData formData, String xml) {
		List<String> lastFormList = studyFormXmlList.get(studyFormXmlList.size() - 1);
		lastFormList.add(xml);
		formsProcessed++;
	}

	/**
	 * @return int total number of forms processed
	 */
	public int getFormsProcessed() {
		return formsProcessed;
	}

	/**
	 * @return 2-dimensional array indexed by study, then form
	 */
	public String[][] getConvertedStudies() {
		String[][] studies = new String[studyFormXmlList.size()][];
		for (int i = 0; i < studies.length; i++)
			studies[i] = studyFormXmlList.get(i).toArray(new String[] {});
		return studies;
	}
}