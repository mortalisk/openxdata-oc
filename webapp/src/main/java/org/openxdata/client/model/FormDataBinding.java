package org.openxdata.client.model;

import java.util.List;
import java.util.Map;
import org.purc.purcforms.client.model.FormDef;
import org.purc.purcforms.client.model.QuestionDef;

/**
 * Defines the bindings for the xform data
 * 
 * @author dagmar@cell-life.org.za
 */
public class FormDataBinding {

    String formBinding;
    FormDef formDef;
    Map<String, String> questionBinding;
    List<String> questionBindingKeys;
    Map<String, QuestionDef> questionDef;
    
    public FormDataBinding(FormDef formDef, 
            Map<String, String> questionBinding,
            Map<String, QuestionDef> questionDef,
            List<String> questionBindingKeys) {
    	this.formDef = formDef;
        this.formBinding = formDef.getBinding();
        this.questionBinding = questionBinding;
        this.questionBindingKeys = questionBindingKeys;
        this.questionDef = questionDef;
    }

    /**
     * Gets the variable name of the form itself
     * @return String
     */
    public String getFormBinding() {
        return formBinding;
    }

    /**
     * Gets the Question/QuestionText map
     * @return Map of question code/binding/variablename to question text
     */
    public Map<String, String> getQuestionBinding() {
        return questionBinding;
    }
    
    /**
     * Gets the Question text given the binding
     * @param questionBindingKey
     * @return
     */
    public String getQuestionText(String questionBindingKey) {
        return questionBinding.get(questionBindingKey);
    }
    
    /**
     * Gets the Question keys in the original order
     * @return List of String containing question code/binding/variablename
     */
    public List<String> getQuestionBindingKeys() {
        return questionBindingKeys;
    }

    /**
     * Gets the map of QuestionDef to question key (binding)
     * @return Map of String key to QuestionDef values
     */
    public Map<String, QuestionDef> getQuestionDef() {
        return questionDef;
    }

    /**
     * Gets the QuestionDef given the binding
     * @param questionBindingKey String question variable
     * @return QuestionDef
     */
    public QuestionDef getQuestionDef(String questionBindingKey) {
        return questionDef.get(questionBindingKey);
    }  
 
    /**
     * Gets the FormDef defining this form
     * @return
     */
    public FormDef getFormDef() {
    	return formDef;
    }
}
