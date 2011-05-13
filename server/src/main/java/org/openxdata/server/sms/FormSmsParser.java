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
package org.openxdata.server.sms;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.openxdata.model.Condition;
import org.openxdata.model.OpenXdataConstants;
import org.openxdata.model.FormDef;
import org.openxdata.model.OptionData;
import org.openxdata.model.OptionDef;
import org.openxdata.model.PageData;
import org.openxdata.model.QuestionData;
import org.openxdata.model.QuestionDef;
import org.openxdata.model.SkipRule;
import org.openxdata.model.ValidationRule;
import org.openxdata.server.OpenXDataConstants;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.OpenXDataParsingException;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;
import org.openxdata.server.admin.model.exception.OpenXDataValidationException;
import org.openxdata.server.Context;
import org.openxdata.server.serializer.KxmlSerializerUtil;
import org.openxdata.server.service.AuthenticationService;
import org.openxdata.server.service.FormDownloadService;
import org.openxdata.server.service.SettingService;
import org.openxdata.server.service.UserService;
import org.openxdata.server.util.XmlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/**
 * Responsible for parsing form data submitted as text sms.
 * 
 * @author daniel
 *
 */
@Component
public class FormSmsParser {

	/** The separator for different fields in the sms text. */
	private String FIELD_SEP_CHAR = "=";

	/** Determines if each sms is expected to contain a user name and password. */
	private boolean smsValidateNamePassword = true;

	/** Determines if we should accept only those phone numbers attached to user accounts. */
	private boolean smsValidatePhoneNo = false;

	private HashMap<String,String> formXml;

	private HashMap<String,FormDef> formDefs;
	
	@Autowired
	private FormDownloadService formDownloadService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SettingService settingService;

	org.openxdata.model.FormData formData;
	
	@Autowired
	private AuthenticationService authenticationService;

	//Response messages
	private String MSG_SMS_SHOULD_NOT_BE_EMPTY = "sms should not be empty";
	private String MSG_MISSING_SPACE_AFTER_USERNAME = "Expected space after username";
	private String MSG_MISSING_SPACE_AFTER_PASSWORD = "Expected space after password";
	private String MSG_ACCESS_DENIED = "Access denied for user";
	private String MSG_NUMBER_NOT_ATTACHED_TO_ANY_USER = "This phone number is not attached to any user account";
	private String MSG_MISSING_SPACE_AFTER_FORMID = "Expected space after form identifier";
	private String MSG_NO_FORM_WITH_IDENTIFIER = "No form found with identifier";
	private String MSG_NO_QUESTION_AT_POSITION = "Form has not question at position";
	private String MSG_IS_OUT_OF_RANGE_FOR = "is out of range for";
	private String MSG_FORM_HAS_NO_QUESTIONS = "Form has no questions";
	private String MSG_ANSWER_REQUIRED_FOR = "An answer is required for";
	private String MSG_SHOULD_BE_DATE = "should be a date";
	private String MSG_SHOULD_BE_IN_LIST = "should be in list {1,2,y,n,yes,no}";
	private String MSG_SHOULD_BE_DATE_TIME = "should be a date and time";
	private String MSG_SHOULD_BE_TIME = "should be time";
	private String MSG_SHOULD_BE_NUMBER = "should be a number";
	private String MSG_NO_ANSWER_EXPECTED_FOR = "no answer is expected for";
	private String MSG_DUE_TO_ANSWER_FOR = "Due to the answer for";
	private String MSG_USER_NOT_REGISTERED_FOR_NUMBER = "is not registered for this phone number";
	private static final String ERROR_PARSING_SMS_INTO_XFORM_MODEL = "Error parsing sms into xform model.";


	public FormSmsParser() {
	}

    void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    void setSettingService(SettingService settingService) {
        this.settingService = settingService;
    }

    void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setFormDownloadService(FormDownloadService formDownloadService) {
        this.formDownloadService = formDownloadService;
    }

	public void init() {
		formDefs = new HashMap<String,FormDef>();
		formXml = new HashMap<String,String>();

		//Prefetch a list of all form definitions from the server such that we do
		//not have to do it at each sms received, hence making a performance boost in
		//sms processing. The trade of is that if someone creates a new form after
		//the sms server has already been started, they have to restart it in order
		//to pick up the new form definitions.
		
		String val = settingService.getSetting("smsFieldSepChar");
		if(val != null)
			FIELD_SEP_CHAR = val;

		val = settingService.getSetting("smsValidateNamePassword");
		if("false".equalsIgnoreCase(val))
			smsValidateNamePassword = false;

		val = settingService.getSetting("smsValidatePhoneNo");
		if("true".equalsIgnoreCase(val))
			smsValidatePhoneNo = true;

		//We some how need to get the user who tries to submit data and hence we need to at least have one of these properties.
		if(smsValidateNamePassword == false && smsValidatePhoneNo == false)
			smsValidateNamePassword = true;

		loadCustomErrorMessages(settingService);

		//TODO Need to use proper locale
		List<String> forms = formDownloadService.getFormsDefaultVersionXml(null,"en");

		for(String xml : forms){
			try{
				FormDef formDef = KxmlSerializerUtil.fromXform2FormDef(new StringReader(xml));
				formDefs.put(formDef.getVariableName(), formDef);
				formXml.put(formDef.getVariableName(), xml);
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Creates a form data item from a text sms.
	 * sample sms= guyzb daniel123 newform 1=Daniel Kayiwa 2=67.8 3=m 4=1,3,4
	 * 
	 * @param sender
	 * @param text
	 * @return
	 * @throws OpenXDataParsingException 
	 * @throws OpenXDataValidationException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public FormData sms2FormData(String sender, String text) throws OpenXDataParsingException, OpenXDataValidationException {

		if(text == null || text.trim().length() == 0)
			throw new OpenXDataParsingException(MSG_SMS_SHOULD_NOT_BE_EMPTY);

		//Turned on for now to prevent requiring a restart of the sms server after a change in
		//settings or form definition. May introduce an unnecessary performance penalty.
		init();

		//First get off closing spaces if any.
		text = text.trim();

		//Authenticate the user
		text = authenticateUser(sender,text);

		//Create an epihandy form data object.
		text = initFormData(text);

		//Zero or more spaces, followed by one or more digits, followed by zero or more spaces, followed by equal sign
		String[] values = text.split("\\s*\\d+\\s*"+getRegexFriendlyFieldSep());

		//Set the values from the sms
		List<String> errors = new ArrayList<String>();
		int pos,startindex = 0;
		for(int index = 1; index < values.length; index++){
			String value = values[index];
			pos = text.indexOf(value, text.indexOf(FIELD_SEP_CHAR, startindex)+1);
			String key = text.substring(startindex,pos);  //eg 1=,2=,3=,4=
			startindex = pos + value.length();

			QuestionData questionData = getQuestion(key,formData,errors);
			if(questionData != null)
				setQuestionAnswer(questionData,formData,value.trim(),errors);
		}

		//Start with skip logic because it can make some fields mandatory
		//hence giving validation rules a chance to also catch these
		//fields whose mandatority is conditional.
		Vector<QuestionData> ruleRequiredQtns = new Vector<QuestionData>();
		String errorMsgs = getSkipErrorMsg(formData,ruleRequiredQtns);

		errorMsgs = addErrorMsg(errorMsgs,getValidationErrorMsg(formData,ruleRequiredQtns));

		for(String error : errors)
			errorMsgs = addErrorMsg(errorMsgs, error);

		if(errorMsgs != null)
			throw new OpenXDataValidationException(errorMsgs);

		//Get the xform model xml that is filled with data.
		String xml;
		try {
			String variableName = formData.getDef().getVariableName();
			StringReader stringReader = new StringReader(formXml.get(variableName));
			org.kxml2.kdom.Document document = KxmlSerializerUtil.getDocument(stringReader);
			xml = KxmlSerializerUtil.updateXformModel(document,formData);
		} catch (Exception e) {
			throw new OpenXDataParsingException(ERROR_PARSING_SMS_INTO_XFORM_MODEL);
		}

		//Create an openxdata form data object and fill values before returning to the caller.
		FormData data = new FormData();
		data.setData(xml);
		data.setFormDefVersionId(formData.getDef().getId());
		setFormDataDescription(xml,data);
		data.setDateCreated(new Date());
		data.setCreator(userService.getLoggedInUser());

		return data;
	}

	private String getRegexFriendlyFieldSep(){
		String  regSep = FIELD_SEP_CHAR;

		regSep = regSep.replace("\\", "\\\\");
		regSep = regSep.replace("^", "\\^");
		regSep = regSep.replace("*", "\\*");
		regSep = regSep.replace("+", "\\+");
		regSep = regSep.replace("$", "\\$");

		regSep = regSep.replace("?", "\\?");
		regSep = regSep.replace(".", "\\.");
		regSep = regSep.replace("(", "\\(");
		regSep = regSep.replace(")", "\\)");
		regSep = regSep.replace("{", "\\{");
		regSep = regSep.replace("}", "\\}");

		regSep = regSep.replace("[", "\\[");
		regSep = regSep.replace("]", "\\]");
		regSep = regSep.replace("|", "\\|");

		return regSep;
	}

    private void setFormDataDescription(String xml, FormData formData) throws OpenXDataParsingException {
        Document doc = XmlUtil.fromString2Doc(xml);
        String descTemplate = doc.getDocumentElement().getAttribute(OpenXDataConstants.ATTRIBUTE_NAME_DESCRIPTION_TEMPLATE);

        formData.setDescription(XmlUtil.getDescriptionTemplate(doc.getDocumentElement(), descTemplate));
    }

	private String authenticateUser(String sender, String text) throws OpenXDataValidationException {

		int pos = 0;

		if(smsValidateNamePassword){
			pos = text.indexOf(' ');
			if(pos < 0)
				throw new OpenXDataValidationException(MSG_MISSING_SPACE_AFTER_USERNAME);

			String username = text.substring(0,pos).trim();

			text = text.substring(pos).trim();
			pos = text.indexOf(' ');
			if(pos < 0)
				throw new OpenXDataValidationException(MSG_MISSING_SPACE_AFTER_PASSWORD);
			String password = text.substring(0,pos).trim();

			User user = null;
			user = authenticationService.authenticate(username, password);

			if(user == null)
				throw new OpenXDataSecurityException(MSG_ACCESS_DENIED + " "+username);
			if(smsValidatePhoneNo && !sender.equals(user.getPhoneNo()))
				throw new OpenXDataSecurityException("User "+username+" " + MSG_USER_NOT_REGISTERED_FOR_NUMBER);
		}
		else{
			assert(smsValidatePhoneNo);// Both smsValidateNamePassword and smsValidatePhoneNo cant be false

			User user = formDownloadService.getUserByPhoneNo(sender);
			if(user == null)
				throw new OpenXDataSecurityException(MSG_NUMBER_NOT_ATTACHED_TO_ANY_USER);

			Context.setAuthenticatedUser(user);
		}

		return text.substring(pos).trim();
	}

	private String initFormData(String text) throws OpenXDataValidationException{

		//Get the form identifier.
		int pos = text.indexOf(' ');
		if(pos < 0)
			throw new OpenXDataValidationException(MSG_MISSING_SPACE_AFTER_FORMID);

		String formid = text.substring(0,pos).trim();
		FormDef formDef = formDefs.get(formid);
		if(formDef == null){
			Integer id = getFormId(formid);
			Collection<FormDef> forms = formDefs.values();
			for(FormDef form : forms){
				if(form.getId() == id.intValue()){
					formDef = form;
					break;
				}
			}
		}

		if(formDef == null)
			throw new OpenXDataValidationException(MSG_NO_FORM_WITH_IDENTIFIER+"="+formid);

		formData = new org.openxdata.model.FormData(formDef);

		return text.substring(pos);
	}

	private QuestionData getQuestion(String idtext,org.openxdata.model.FormData formData,List<String> errors) {

		String text = idtext.substring(0,idtext.indexOf(FIELD_SEP_CHAR)).trim();
		int id = Integer.parseInt(text);
		QuestionData questionData = formData.getQuestion((byte)id);
		if(questionData == null)
			errors.add(MSG_NO_QUESTION_AT_POSITION+" "+id);
		return questionData;
	}

	private void setQuestionAnswer(QuestionData questionData, org.openxdata.model.FormData formData, String answer,List<String> errors){
		//TODO May need to handle dynamic optiondef
		QuestionDef questionDef = questionData.getDef();
		if(questionDef.getType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE || questionDef.getType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC){
			questionData.setAnswer(getOptionData(questionDef,answer,errors));
			formData.updateDynamicOptions(questionData,false);
		}
		else if(questionDef.getType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE){
			Vector<OptionData> optionAnswers = new Vector<OptionData>();
			String values[] = answer.split(" ");
			for(int index = 0; index < values.length; index++)
				optionAnswers.addElement(getOptionData(questionDef,values[index],errors));
			questionData.setAnswer(optionAnswers);
		}
		else if(questionDef.getType() == QuestionDef.QTN_TYPE_BOOLEAN)
			questionData.setAnswer(answer);
		else
			questionData.setTextAnswer(answer);
	}

	private OptionData getOptionData(QuestionDef questionDef,String answer,List<String> errors){
		OptionDef optionDef = questionDef.getOptionWithValue(answer);
		if(optionDef == null){
			try{
				int val = Integer.parseInt(answer) - 1;
				if(val < questionDef.getOptions().size() && val >= 0)
					optionDef = (OptionDef)questionDef.getOptions().elementAt(val);
			}
			catch(Exception ex){}
		}

		if(optionDef == null){
			errors.add(answer + " " + MSG_IS_OUT_OF_RANGE_FOR + " " + questionDef.getText());

			//Since we have an out of range error message,we do not need to also report the 
			//required error, hence making the error report sms as small as possible.
			//This will not result into bugs only on condition that on each sms received,
			//a new formdef is constructed.
			questionDef.setMandatory(false); 

			return null;
		}

		return new OptionData(optionDef);
	}

	/**
	 * Get the validation error messages in a filled form.
	 * 
	 * @param formData the form data to validate.
	 * @param ruleRequiredQtns a list of questions which become required after a firing of some rules.
	 * @return a comma separated list of validation error messages if any, else null.
	 */
	@SuppressWarnings("unchecked")
	private String getValidationErrorMsg(org.openxdata.model.FormData formData,Vector<QuestionData> ruleRequiredQtns){
		String sErrors = null;

		//Check if form has any questions.
		Vector<PageData> pages = formData.getPages();
		if(pages == null || pages.size() == 0){
			sErrors = MSG_FORM_HAS_NO_QUESTIONS;
			return sErrors;
		}

		//First get error messages for required fields which have not been answered
		//and answers not allowed for the data type.
		for(byte i=0; i<pages.size(); i++){
			PageData page = (PageData)pages.elementAt(i);
			for(byte j=0; j<page.getQuestions().size(); j++){
				QuestionData qtn = (QuestionData)page.getQuestions().elementAt(j);
				if(!ruleRequiredQtns.contains(qtn) && !qtn.isValid())
					sErrors = addErrorMsg(sErrors,MSG_ANSWER_REQUIRED_FOR + " "+qtn.getDef().getText());

				//Do data type validation
				String msg = getTypeErrorMsg(qtn);
				if(msg != null)
					sErrors = addErrorMsg(sErrors,msg);
			}
		}

		//Check if form has any validation rules.
		Vector<ValidationRule> rules = formData.getDef().getValidationRules();
		if(rules == null)
			return sErrors;

		//Deal with the user supplied validation rules
		for(int index = 0; index < rules.size(); index++){
			ValidationRule rule = (ValidationRule)rules.elementAt(index);
			rule.setFormData(formData);
			if(!rule.isValid())
				sErrors = addErrorMsg(sErrors,rule.getErrorMessage());
		}

		return sErrors;
	}

	private String addErrorMsg(String errorMsgs, String msg){
		if(msg != null){
			if(errorMsgs == null)
				errorMsgs = "";
			else
				errorMsgs += ", ";
			errorMsgs += msg;
		}

		return errorMsgs;
	}

	private String getTypeErrorMsg(QuestionData questionData){
		if(questionData.getAnswer() == null)
			return null;

		QuestionDef questionDef = questionData.getDef();
		switch(questionDef.getType()){
		case QuestionDef.QTN_TYPE_BOOLEAN:
			if(questionData.getAnswer() == null)
				return null;
			else if("1".equals(questionData.getAnswer()) || "yes".equals(questionData.getAnswer()) || "y".equals(questionData.getAnswer())){
				questionData.setTextAnswer(QuestionData.TRUE_VALUE);
				return null;
			}
			else if("2".equals(questionData.getAnswer()) || "no".equals(questionData.getAnswer()) || "n".equals(questionData.getAnswer())){
				questionData.setTextAnswer(QuestionData.FALSE_VALUE);
				return null;
			}
			else
				return questionData.getAnswer() + " " + questionDef.getText() + " " + MSG_SHOULD_BE_IN_LIST;
		case QuestionDef.QTN_TYPE_TEXT:
			return null;
		case QuestionDef.QTN_TYPE_DATE:
			try{
				Date date = null;
				if(questionData.isDateFunction(questionData.getAnswer()))
					date = new Date();
				else
					date = fromDisplayString2Date(questionData.getAnswer().toString());
				questionData.setTextAnswer(fromDate2SubmitString(date));
				return null;
			}
			catch(Exception ex){
				return questionData.getAnswer() + " " + questionDef.getText() + " " + MSG_SHOULD_BE_DATE;
			}
		case QuestionDef.QTN_TYPE_DATE_TIME:
			try{
				Date date = null;
				if(questionData.isDateFunction(questionData.getAnswer()))
					date = new Date();
				else
					date = fromDisplayString2DateTime(questionData.getAnswer().toString());
				questionData.setTextAnswer(fromDateTime2SubmitString(date));
				return null;
			}
			catch(Exception ex){
				return questionData.getAnswer() + " " + questionDef.getText() + " " + MSG_SHOULD_BE_DATE_TIME;
			}
		case QuestionDef.QTN_TYPE_TIME:
			try{
				Date date = null;
				if(questionData.isDateFunction(questionData.getAnswer()))
					date = new Date();
				else
					date = fromDisplayString2Time(questionData.getAnswer().toString());
				questionData.setTextAnswer(fromTime2SubmitString(date));
				return null;
			}
			catch(Exception ex){
				return questionData.getAnswer() + " " + questionDef.getText() + " " + MSG_SHOULD_BE_TIME;
			}
		case QuestionDef.QTN_TYPE_LIST_EXCLUSIVE:
		case QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC:
			return null;
		case QuestionDef.QTN_TYPE_LIST_MULTIPLE:
			return null;
		case QuestionDef.QTN_TYPE_DECIMAL:
		case QuestionDef.QTN_TYPE_NUMERIC:
			try{
				Double.parseDouble(questionData.getAnswer().toString());
				return null;
			}
			catch(Exception ex){
				return questionData.getAnswer() + " " + questionDef.getText() + " " + MSG_SHOULD_BE_NUMBER;
			}
		}

		return null;
	}
	
    /**
     * Converts a date and time display string to a date object.
     * 
     * @param dateTime the date and time display string.
     * @return the date object.
     */
    private Date fromDisplayString2DateTime(String dateTime) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(settingService.getSetting(OpenXDataConstants.SETTING_NAME_DISPLAY_DATETIME_FORMAT,OpenXDataConstants.DEFAULT_DATETIME_DISPLAY_FORMAT));
		return dateFormat.parse(dateTime);
	}
    
    /**
     * Converts a date display string to a date object.
     * 
     * @param date the date display string.
     * @return the date object.
     */
    private Date fromDisplayString2Date(String date) throws ParseException{
		SimpleDateFormat dateFormat = new SimpleDateFormat(settingService.getSetting(OpenXDataConstants.SETTING_NAME_DISPLAY_DATE_FORMAT,OpenXDataConstants.DEFAULT_DATE_DISPLAY_FORMAT));
		return dateFormat.parse(date);
	}
	
    /**
     * Converts time to its xml text representation.
     * 
     * @param time time object to convert.
     * @return the xml text representation of the time.
     */
    private String fromTime2SubmitString(Date time) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(settingService.getSetting(OpenXDataConstants.SETTING_NAME_SUBMIT_TIME_FORMAT,OpenXDataConstants.DEFAULT_TIME_SUBMIT_FORMAT));
		return dateFormat.format(time);
	}
    
    /**
     * Converts a date to its xml text representation.
     * 
     * @param date the date to convert.
     * @return the xml text representation of the date.
     */
    private String fromDate2SubmitString(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(settingService.getSetting(OpenXDataConstants.SETTING_NAME_SUBMIT_DATE_FORMAT,OpenXDataConstants.DEFAULT_DATE_SUBMIT_FORMAT));
		return dateFormat.format(date);
	}
    
    /**
     * Converts a time display string to a date object.
     * 
     * @param time the time display string.
     * @return the date object.
     */
    private Date fromDisplayString2Time(String time) throws ParseException{
		SimpleDateFormat dateFormat = new SimpleDateFormat(settingService.getSetting(OpenXDataConstants.SETTING_NAME_DISPLAY_TIME_FORMAT,OpenXDataConstants.DEFAULT_TIME_DISPLAY_FORMAT));
		return dateFormat.parse(time);
	}
    
    /**
     * Converts a date and time object to its xml text representation.
     * 
     * @param dateTime the date and time object to convert.
     * @return the xml text representation of the date and time.
     */
    private String fromDateTime2SubmitString(Date dateTime) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(settingService.getSetting(OpenXDataConstants.SETTING_NAME_SUBMIT_DATETIME_FORMAT,OpenXDataConstants.DEFAULT_DATETIME_SUBMIT_FORMAT));
		return dateFormat.format(dateTime);
	}

	private Integer getFormId(String id){
		try{
			return Integer.parseInt(id);
		}catch(Exception ex){}

		return null;
	}

	/**
	 * Get the skip logic error messages in a filled form. (eg pregnant males)
	 * 
	 * @param formData the form data to validate.
	 * @param ruleRequiredQtns a list of questions which become required after rule firing.
	 * @return a comma separated list of validation error messages if any, else null.
	 */
	@SuppressWarnings("unchecked")
	private String getSkipErrorMsg(org.openxdata.model.FormData formData,Vector<QuestionData> ruleRequiredQtns){
		String sErrors = null;

		//Check if form has any questions.
		Vector<PageData> pages = formData.getPages();
		if(pages == null || pages.size() == 0){
			sErrors = MSG_FORM_HAS_NO_QUESTIONS;
			return sErrors;
		}

		//Check if form has any skip rules.
		Vector<SkipRule> rules = formData.getDef().getSkipRules();
		if(rules == null)
			return sErrors;

		//Deal with the user supplied validation rules
		for(int index = 0; index < rules.size(); index++){
			SkipRule rule = (SkipRule)rules.elementAt(index);
			Vector<QuestionData> answeredQtns = getAnsweredQuestions(formData,rule.getActionTargets());

			rule.fire(formData);

			//Get the question text of the first condition. This could be improved with a user supplied skip logic error message, in future.
			String qtnText = formData.getQuestion(((Condition)rule.getConditions().elementAt(0)).getQuestionId()).getText();

			boolean mandatoryRule = (rule.getAction() & OpenXdataConstants.ACTION_MAKE_MANDATORY) != 0;

			Vector<Byte> ids = rule.getActionTargets();
			for(byte i=0; i<ids.size(); i++){
				QuestionData questionData = formData.getQuestion(Byte.parseByte(ids.elementAt(i).toString()));

				//Check if the user answered a question they were supposed to skip.
				if(!questionData.isAnswered() && answeredQtns.contains(questionData))
					sErrors = addErrorMsg(sErrors,MSG_DUE_TO_ANSWER_FOR + " " + qtnText + ", " + MSG_NO_ANSWER_EXPECTED_FOR + " "+questionData.getDef().getText());

				//Check is the user has not answered a question which has become required after an answer to some other question.
				if(mandatoryRule && questionData.getDef().isMandatory() && !questionData.isAnswered())
					sErrors = addErrorMsg(sErrors,MSG_DUE_TO_ANSWER_FOR + " " + qtnText + ", " + MSG_NO_ANSWER_EXPECTED_FOR +" "+questionData.getDef().getText());

				if(mandatoryRule)
					ruleRequiredQtns.add(questionData);
			}
		}

		return sErrors;
	}

	/**
	 * Gets a list of questions which have been answered.
	 * 
	 * @param formData the form data.
	 * @param ids
	 * @return list of answered questions
	 */
	private Vector<QuestionData> getAnsweredQuestions(org.openxdata.model.FormData formData, Vector<Byte> ids){
		Vector<QuestionData> qtns = new Vector<QuestionData>();

		for(byte i=0; i<ids.size(); i++){
			QuestionData questionData = formData.getQuestion(Byte.parseByte(ids.elementAt(i).toString()));
			if(questionData.isAnswered())
				qtns.add(questionData);
		}

		return qtns;
	}

	private void loadCustomErrorMessages(SettingService service) {
		String val = service.getSetting("MSG_MISSING_SPACE_AFTER_USERNAME");
		if(val != null && val.trim().length() > 0)
			MSG_MISSING_SPACE_AFTER_USERNAME = val;

		val = service.getSetting("MSG_MISSING_SPACE_AFTER_PASSWORD");
		if(val != null && val.trim().length() > 0)
			MSG_MISSING_SPACE_AFTER_PASSWORD = val;

		val = service.getSetting("MSG_ACCESS_DENIED");
		if(val != null && val.trim().length() > 0)
			MSG_ACCESS_DENIED = val;

		val = service.getSetting("MSG_NUMBER_NOT_ATTACHED_TO_ANY_USER");
		if(val != null && val.trim().length() > 0)
			MSG_NUMBER_NOT_ATTACHED_TO_ANY_USER = val;

		val = service.getSetting("MSG_MISSING_SPACE_AFTER_FORMID");
		if(val != null && val.trim().length() > 0)
			MSG_MISSING_SPACE_AFTER_FORMID = val;

		val = service.getSetting("MSG_NO_FORM_WITH_IDENTIFIER");
		if(val != null && val.trim().length() > 0)
			MSG_NO_FORM_WITH_IDENTIFIER = val;

		val = service.getSetting("MSG_NO_QUESTION_AT_POSITION");
		if(val != null && val.trim().length() > 0)
			MSG_NO_QUESTION_AT_POSITION = val;

		val = service.getSetting("MSG_IS_OUT_OF_RANGE_FOR");
		if(val != null && val.trim().length() > 0)
			MSG_IS_OUT_OF_RANGE_FOR = val;

		val = service.getSetting("MSG_FORM_HAS_NO_QUESTIONS");
		if(val != null && val.trim().length() > 0)
			MSG_FORM_HAS_NO_QUESTIONS = val;

		val = service.getSetting("MSG_ANSWER_REQUIRED_FOR");
		if(val != null && val.trim().length() > 0)
			MSG_ANSWER_REQUIRED_FOR = val;

		val = service.getSetting("MSG_SHOULD_BE_DATE");
		if(val != null && val.trim().length() > 0)
			MSG_SHOULD_BE_DATE = val;

		val = service.getSetting("MSG_SHOULD_BE_IN_LIST");
		if(val != null && val.trim().length() > 0)
			MSG_SHOULD_BE_IN_LIST = val;

		val = service.getSetting("MSG_SHOULD_BE_DATE_TIME");
		if(val != null && val.trim().length() > 0)
			MSG_SHOULD_BE_DATE_TIME = val;

		val = service.getSetting("MSG_SHOULD_BE_TIME");
		if(val != null && val.trim().length() > 0)
			MSG_SHOULD_BE_TIME = val;

		val = service.getSetting("MSG_SHOULD_BE_NUMBER");
		if(val != null && val.trim().length() > 0)
			MSG_SHOULD_BE_NUMBER = val;

		val = service.getSetting("MSG_NO_ANSWER_EXPECTED_FOR");
		if(val != null && val.trim().length() > 0)
			MSG_NO_ANSWER_EXPECTED_FOR = val;

		val = service.getSetting("MSG_DUE_TO_ANSWER_FOR");
		if(val != null && val.trim().length() > 0)
			MSG_DUE_TO_ANSWER_FOR = val;

		val = service.getSetting("MSG_USER_NOT_REGISTERED_FOR_NUMBER");
		if(val != null && val.trim().length() > 0)
			MSG_USER_NOT_REGISTERED_FOR_NUMBER = val;

		val = service.getSetting("MSG_SMS_SHOULD_NOT_BE_EMPTY");
		if(val != null && val.trim().length() > 0)
			MSG_SMS_SHOULD_NOT_BE_EMPTY = val;
	}
}
