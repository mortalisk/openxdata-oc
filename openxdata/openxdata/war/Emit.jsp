<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!-- The HTML 4.01 Transitional DOCTYPE declaration-->
<!-- above set at the top of the file will set     -->
<!-- the browser's rendering engine into           -->
<!-- "Quirks Mode". Replacing this declaration     -->
<!-- with a "Standards Mode" doctype is supported, -->
<!-- but may lead to some differences in layout.   -->
<%@ taglib prefix='fmt' uri='http://java.sun.com/jsp/jstl/fmt' %>

<html>
<head>
<%@ include file="locale.jsp" %>
<fmt:setBundle basename='org.openxdata.client.PurcformsText'/>
<fmt:setBundle basename='org.openxdata.client.AppMessages' var="appMessages" />
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta name="gwt:property" content="locale=${locale}">
<link rel="shortcut icon" href="favicon.ico" >
<!--                                           -->
<!-- stylesheet for gxt -->
<!--                                           -->
<link rel="stylesheet" type="text/css" href="gxt/css/gxt-all.css" />

<!--                                                               -->
<!-- Consider inlining CSS to reduce the number of requested files -->
<!--                                                               -->
<link type="text/css" rel="stylesheet" href="Emit.css">

<title><fmt:message key="title" bundle="${appMessages}"/></title>

<!--                                           -->
<!-- This script loads your compiled module.   -->
<!-- If you add any GWT meta tags, they must   -->
<!-- be added before this line.                -->
<!--                                           -->
<script type="text/javascript" language="javascript"
	src="emit/emit.nocache.js"></script>
	
<script type="text/javascript" language="javascript" src="gxt/flash/swfobject.js"></script>

<script language="javascript">
        var PurcformsText = {
    	    	file: "File",
    	    	view: "View",
    	    	item: "Item",
    	    	tools: "Tools",
    	    	help: "Help",
    	    	newItem: "New",
    	    	open: "<fmt:message key='open'/>",
    	    	save: "Save",
    	    	saveAs: "Save As",

    	    	openLayout: "Open Layout",
    	    	saveLayout: "Save Layout",
    	    	openLanguageText: "Open Language Text",
    	    	saveLanguageText: "Save Language Text",
                close: "<fmt:message key='close'/>",

    	    	refresh: "Refresh",
    	    	addNew: "Add New",
    	    	addNewChild: "Add New Child",
    	    	deleteSelected: "Delete Selected",
    	    	moveUp: "Move Up",
    	    	moveDown: "Move Down",
    	    	cut: "Cut",
    	    	copy: "Copy",
    	    	paste: "Paste",
    	    	
    	    	format: "Format",
    	    	languages: "Languanges",
    	    	options: "Options",

    	    	helpContents: "Help Contents",
    	    	about: "About",

    	    	forms: "Forms",
    	    	widgetProperties: "Widget Properties",
    	    	properties: "Properties",
    	    	xformsSource: "Xforms Source",
    	    	designSurface: "Design Surface",
    	    	layoutXml: "Layout Xml",
    	    	languageXml: "Language Xml",
    	    	preview: "Preview",
    	    	modelXml: "Model Xml",

    	    	text: "Text",
    	    	helpText: "Help Text",
    	    	type: "Type",
    	    	binding: "Binding",
    	    	visible: "Visible",
    	    	enabled: "Enabled",
    	    	locked: "Locked",
    	    	required: "Required",
    	    	defaultValue: "Default Value",
    	    	descriptionTemplate: "Description Template",

    	    	language: "Language",
    	    	skipLogic: "Skip Logic",
    	    	validationLogic: "Validation Logic",
    	    	dynamicLists: "Dynamic Lists",

    	    	valuesFor: "Values for: ",
    	    	whenAnswerFor: "when the answer for: ",
    	    	isEqualTo: "is equal to: ",
    	    	forQuestion: "For question: ",
    	    	enable: "Enable",
    	    	disable: "Disable",
    	    	show: "Show",
    	    	hide: "Hide",
    	    	makeRequired: "Make Required",

    	    	when: "When ",
    	    	ofTheFollowingApply: "of the following apply",
    	    	all: "all",
    	    	any: "any",
    	    	none: "none",
    	    	notAll: "not all",

    	    	addNewCondition: "Click here to add new condition",

    	    	isEqualTo: "is equal to",
    	    	isNotEqual: "is not equal to",
    	    	isLessThan: "is less than",
    	    	isLessThanOrEqual: "is less than or equal to",
    	    	isGreaterThan: "is greater than",
    	    	isGreaterThanOrEqual: "is greater than or equal to",
    	    	isNull: "is null",
    	    	isNotNull: "is not null",
    	    	isInList: "is in list",
    	    	isNotInList: "is not in list",
    	    	startsWith: "starts with",
    	    	doesNotStartWith: "does not start with",
    	    	contains: "contains",
    	    	doesNotContain: "does not contain",
    	    	isBetween: "is between",
    	    	isNotBetween: "is not between",

    			isValidWhen: "is valid when:",
                errorMessage: "<fmt:message key='errorMessage'/>",
                question: "<fmt:message key='question'/>",

                addField: "Add Field",
    			submit: "Submit",
    			addWidget: "Add Widget",
    			newTab: "New Tab",
    			deleteTab: "Delete Tab",
    			selectAll: "Select All",
    			load: "Load",
    			loading: "Loading",
    			
    			label: "Label",
    			textBox: "TextBox",
    			checkBox: "CheckBox",
    			radioButton: "RadioButton",
    			dropdownList: "DropdownList",
    			textArea: "TextArea",
    			button: "Button",
    			datePicker: "Date Picker",
    			groupBox: "Group Box",
    			listBox: "ListBox",
    			repeatSection: "Repeat Section",
    			picture: "Picture",
    			videoAudio: "Video/Audio",
    			timeWidget: "Time Widget",
    			dateTimeWidget: "Date Time Widget",

    			deleteWidgetPrompt: "Do you really want to delete the selected Widget (s)?",
    			deleteTreeItemPrompt: "Do you really want to remove the selected item and all its children (if any) ?",
    			selectDeleteItem: "Please first select the item to delete",

    			selectedPage: "The selected page [",
    			shouldNotSharePageBinding: "] should not share the same page number/binding with page [",
    			selectedQuestion: "The selected question [",
    			shouldNotShareQuestionBinding: "] should not share the same binding with question [",
    			selectedOption: "The selected option [",
    			shouldNotShareOptionBinding: "] should not share the same binding with option [",
    			newForm: "New Form",
    			page: "<fmt:message key='page'/>",
    			option: "Option",
                noDataFound: "No data found.",

                formSaveSuccess: "Form saved successfully",
    			selectSaveItem: "Please select the item to save.",
    			deleteAllWidgetsFirst: "Please first delete all the widgets.",
    			deleteAllTabWidgetsFirst: "This tab has one or more widgets, please first delete them",
    			cantDeleteAllTabs: "This tab cannot be deleted because there should be atleast one tab.",
                noFormId: "No formId or ",
                divFound: " div found",
                noFormLayout: "No form layout found. Please first design and save the form.",
                formSubmitSuccess: "Form Data Submitted Successfully",
                missingDataNode: "Missing data node for :",
                
                openingForm: "<fmt:message key='openingForm'/>",
                openingFormLayout: "Opening Form Layout",
    			savingForm: "Saving Form",
    			savingFormLayout: "Saving Form Layout",
    			refreshingForm: "Refresing Form",
    			translatingFormLanguage: "Translating Form Language",
    			savingLanguageText: "Saving Language Text",
    			refreshingDesignSurface: "Refreshing Design Surface",
    			loadingDesignSurface: "Loading Design Surface",
    			refreshingPreview: "Refreshing Preview",

    			count: "Count",
                clickToPlay: "<fmt:message key='clickToPlay'/>",
                loadingPreview: "<fmt:message key='loadingPreview'/>",
                unexpectedFailure: "<fmt:message key='unexpectedFailure'/>",
                uncaughtException: "Uncaught exception: ",
                causedBy: "Caused by: ",
                openFile: "<fmt:message key='openFile'/>",
                saveFileAs: "<fmt:message key='saveFileAs'/>",

                alignLeft: "Align Left",
    			alignRight: "Align Right",
    			alignTop: "Align Top",
    			alignBottom: "Align Bottom",
    			makeSameWidth: "Make Same Width",
    			makeSameHeight: "Make Same Height",
    			makeSameSize: "Make Same Size",
    		    layout: "Layout",
    		    deleteTabPrompt: "Do you really want to delete this tab?",

    		    text: "Text",
    		    toolTip: "Tooltip",
    		    childBinding: "Child Binding",
    		    width: "Width",
    		    height: "Height",
    		    left: "Left",
    		    top: "Top",
    		    tabIndex: "Tab Index",
    		    repeat: "Repeat",
    		    externalSource: "External Source",
    		    displayField: "Display Field",
    		    valueField: "Value Field",
    		    fontFamily: "Font Family",
    		    foreColor: "Fore Color",
    		    fontWeight: "Font Weight",
    		    fontStyle: "Font Style",
    		    fontSize: "Font Size",
    		    textDecoration: "Text Decoration",
    		    textAlign: "Text Align",
    		    backgroundColor: "Background Color",
    		    borderStyle: "Border Style",
    		    borderWidth: "Border Width",
    		    borderColor: "Border Color",
    		    aboutMessage: "This is a form designer widget based on GWT",
                more: "<fmt:message key='more'/>",
                requiredErrorMsg: "<fmt:message key='requiredErrorMsg'/>",
                questionTextDesc: "The question text.",
    		    questionDescDesc: "The question description.",
    		    questionIdDesc: "The question internal identifier. For Questions, it should be a valid xml node name.",
    		    defaultValDesc: "The default value or answer",
    		    questionTypeDesc: "The type of question or type of expected answers.",

    		    qtnTypeText: "Text",
    		    qtnTypeNumber: "Number",
    		    qtnTypeDecimal: "Decimal",
    		    qtnTypeDate: "Date",
    		    qtnTypeTime: "Time",
    		    qtnTypeDateTime: "Date and Time",
    		    qtnTypeBoolean: "Boolean",
    		    qtnTypeSingleSelect: "Single Select",
    		    qtnTypeMultSelect: "Multiple Select",
    		    qtnTypeRepeat: "Repeat",
    		    qtnTypePicture: "Picture",
    		    qtnTypeVideo: "Video",
    		    qtnTypeAudio: "Audio",
    		    qtnTypeSingleSelectDynamic: "Single Select Dynamic",
        		deleteCondition: "Delete Condition",
        		addCondition: "Add Condition",
        		value: "Value",
        		questionValue: "Question value",
        		and: "and",
        		deleteItemPrompt: "Do you really want to delete this item?",
        		changeWidgetTypePrompt: "Do you really want to change to this type and lose all the options created, if any?",
        		removeRowPrompt: "Do you really want to remove this row?",
                remove: "<fmt:message key='remove'/>",
                browse: "<fmt:message key='browse'/>",
                clear: "<fmt:message key='clear'/>",
                deleteItem: "<fmt:message key='deleteItem'/>",
                cancel: "<fmt:message key='cancel'/>",

                clickToAddNewCondition: "< Click here to add new condition >",
            	qtnTypeGPS: "GPS",
            	qtnTypeBarcode: "Barcode",
            	palette: "Palette",
        		saveAsXhtml: "Save As XHTML",
        		groupWidgets: "Group Widgets",
        		action: "Action",
                submitting: "<fmt:message key='submitting'/>",
        		welcome: "Welcome to openXdata server",
        		authenticationPrompt: "Please enter your user name and password",
        		invalidUser: "Invalid UserName or Password",
        		login: "Login",
        		userName: "User Name",
        		password: "Password",
                noSelection: "<fmt:message key='noSelection'/>",
                cancelFormPrompt: "<fmt:message key='cancelFormPrompt'/>",
                print: "<fmt:message key='print'/>",
                yes: "<fmt:message key='yes'/>",
                no: "<fmt:message key='no'/>",
                searchServer: "<fmt:message key='searchServer'/>",
                recording: "<fmt:message key='recording'/>",
                search: "<fmt:message key='search'/>",
                processingMsg: "<fmt:message key='processingMsg'/>",
           		length: "Length",
           		clickForOtherQuestions: "< Click here for other questions >",
                ok: "<fmt:message key='ok'/>",
                undo: "Undo",
           		redo: "Redo",           		
                loading: "<fmt:message key='loading'/>",
           		allQuestions: "All Questions",
           		selectedQuestions: "Selected Questions",
           		otherQuestions: "Other Questions",
                wrongFormat: "<fmt:message key='wrongFormat'/>",
           		lockWidgets: "Lock",
           		unLockWidgets: "Unlock",
           		changeWidgetH: "Change Widget H",
           		changeWidgetV: "Change Widget V",
           		saveAsPurcForm: "Save As PurcForm",
           		localeChangePrompt: "Changing locale will lead to loss of any unsaved changes. Do you want to go ahead and change?",
           		javaScriptSource: "JavaScript",
           		calculation: "Calculation",
           		id: "ID",
           		formKey: "Form Key",
           		filterField: "Filter Field",
           		invalidDefaultValueForQuestionType: "The default value specified is invalid with regard to the question type selected. It contains invalid characters. You cannot enter characters where numbers are expected. Please check to proceed!"
          };

          function isUserAuthenticated(){
              authenticationCallback(true);
              return true; //Not expected to be used in the design mode.
          }

          function authenticateUser(username, password){
              return true; //Not expected to be used in the design mode.
          }

          function searchExternal(key,value,parentElement,textElement,valueElement,filterField){
        
          }

          function initialize(){

          }
         
    </script>

</head>
<body>

<!-- weird purcforms variables (no other way to set them, sigh) -->
<div id="defaultFontSize" style="visibility:hidden;">12</div>
<div id="defaultFontFamily" style="visibility:hidden;">Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif</div>

<!-- history support -->
<iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1'
	style="width: 0; height: 0; border: 0;"></iframe>
</body>
</html>