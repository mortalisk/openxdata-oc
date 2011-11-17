package org.motechproject.proto;

import java.util.ArrayList;
import java.util.List;

import org.fcitmuk.epihandy.DeserializationListenerAdapter;
import org.fcitmuk.epihandy.EpihandyConstants;
import org.fcitmuk.epihandy.FormData;
import org.fcitmuk.epihandy.StudyData;

/**
 * Implements the DeserializationListener which is invoked during the deserialisation of Form Data.
 */
public class UploadDataProcessor extends DeserializationListenerAdapter {

	int formsProcessed = 0;
	List<List<UploadData>> studyFormXmlList = new ArrayList<List<UploadData>>();

	@Override
	public void processingStudy(StudyData studyData) {
		List<UploadData> formList = new ArrayList<UploadData>();
		studyFormXmlList.add(formList);
	}

	@Override
	public void formProcessed(StudyData studyData, FormData formData, String xml) {
		List<UploadData> lastFormList = studyFormXmlList.get(studyFormXmlList.size() - 1);
		if (formData.getDataId() == EpihandyConstants.NULL_ID) {
			lastFormList.add(new UploadData(xml));
		} else {
			lastFormList.add(new UploadData(formData.getDataId(), xml));
		}
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
	public UploadData[][] getConvertedStudies() {
		UploadData[][] studies = new UploadData[studyFormXmlList.size()][];
		for (int i = 0; i < studies.length; i++)
			studies[i] = studyFormXmlList.get(i).toArray(new UploadData[] {});
		return studies;
	}
}