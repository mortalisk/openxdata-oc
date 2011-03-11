package org.openxdata.client.model;

import java.util.Date;
import java.util.Map;

import org.openxdata.server.admin.model.ExportedDataType;
import org.openxdata.server.admin.model.ExportedFormData;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDef;

import com.extjs.gxt.ui.client.data.BaseModel;

public class FormDataSummary extends BaseModel {

    private static final long serialVersionUID = 3842754006212589283L;

    private FormDef formDef;
    private ExportedFormData exportedFormData;
    
    public FormDataSummary(FormDef formDef, ExportedFormData exportedFormData) {
        this.formDef = formDef;
        this.exportedFormData = exportedFormData;
        convertFormData();
    }
    
    private void convertFormData() {
        FormData formData = exportedFormData.getFormData();
        if (formData != null) {
            setCapturer(formData.getCreator().getName());
            setCaptureDate(formData.getDateCreated());
        }
        setStatus("submitted");
        Map<String, ExportedDataType> data = exportedFormData.getExportedFields();
        for (String binding : data.keySet()) {
            setData(binding, data.get(binding).getValue());
        }
    }
    
    // convertFormDataUsing xml
    /*private void convertFormData() {
        // set general form summary data
        setCapturer(formData.getCreator().getName());
        setCaptureDate(formData.getDateCreated());
        setStatus("submitted");
        
        // convert the FormData using purcForms
        org.purc.purcforms.client.model.FormDef purcFormDef = XformParser.fromXform2FormDef(formDef.getDefaultVersion().getXform(), formData.getData());

        // go through all the pages in this form
        Iterator pageIt = purcFormDef.getPages().iterator();
        while (pageIt.hasNext()) {
            PageDef purcPageDef = (PageDef)pageIt.next();
            // now go through all the questions in this page
            Iterator questionIt = purcPageDef.getQuestions().iterator();
            while (questionIt.hasNext()) {
                QuestionDef purcQuestionDef = (QuestionDef)questionIt.next();
                // get the question text (for the model)
                String question = purcQuestionDef.getText();
                String answer = purcQuestionDef.getAnswer();
                // if this question has options, go through them to determine the correct answer text
                if (answer != null && purcQuestionDef.getOptionCount() > 0) {
                    StringBuilder answerBuilder = new StringBuilder();
                    for (String option : answer.split(" ")) {
                        OptionDef purcOptionDef = purcQuestionDef.getOptionWithValue(option);
                        if (answerBuilder.length() > 0) {
                            answerBuilder.append(" ");
                        }
                        answerBuilder.append(purcOptionDef.getText());
                    }
                    answer = answerBuilder.toString();
                }
                // finally, set the question/answer data
                setData(question, answer);
            }
        }
    }*/
    
    public void setCapturer(String capturer) {
        set("openxdata_user_name", capturer);
    }
    
    public void setCaptureDate(Date date) {
        set("openxdata_date_created", date);
    }
    
    public void setStatus(String status) {
        set("status", status);
    }
    
    public <X> X setData(String name, X value) {
        return set(name, value);
    }
    
    public FormDef getFormDef() {
        return formDef;
    }

    public ExportedFormData getExportedFormData() {
        return exportedFormData;
    }
    
    public FormDataSummary getUpdatedFormDef(){
    	return this;
    }
}