<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt'%>
<%@ taglib prefix='fmt' uri='http://java.sun.com/jsp/jstl/fmt' %>
<%@ page import="org.springframework.security.ui.AbstractProcessingFilter"%>
<html>
<head>
<%@ include file="locale.jsp" %>
<fmt:setBundle basename='org.openxdata.client.AppMessages'/>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title><fmt:message key="title" /></title>
<link type="text/css" rel="stylesheet" href="Emit.css">
<link rel="stylesheet" type="text/css"
	href="extjs/resources/css/ext-all.css" />
<!-- extjs -->
<script type="text/javascript" src="extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="extjs/ext-all.js"></script>
<!-- note: choose the ext-lang js file containing the text for the selected locale -->
<c:choose>
<c:when test="${fn:contains(sessionScope.locale, '-')}" >
   <script type="text/javascript" src="extjs/ext-lang-${fn:substringBefore(sessionScope.locale,'-')}.js"></script>
</c:when>
<c:otherwise>
	<c:choose>
		<c:when test="${fn:contains(sessionScope.locale, '_')}" >
   			<script type="text/javascript" src="extjs/ext-lang-${fn:substringBefore(sessionScope.locale,'_')}.js"></script>
		</c:when>
		<c:otherwise>
 		<script type="text/javascript" src="extjs/ext-lang-${sessionScope.locale}.js"></script>
		</c:otherwise>
	</c:choose>
</c:otherwise>
</c:choose>

<script>
    Ext.onReady(function(){

        var submitFn = function() {
        	if(resetLoginForm.getForm().isValid()){
        			var responseMessage = document.getElementById("responseMessage");
                	responseMessage.innerHTML = '<img id=waitImg height=20 width=20 align=center src=images/wait30trans.gif> &nbsp; <fmt:message key="waitMessage"/>';
                	var formValues = resetLoginForm.getForm().getValues(true);
                	Ext.Ajax.request({
        			    url : 'resetPassword?'+formValues,
        				method: 'GET',
        			    success: function(objServerResponse){
        			    	var status = objServerResponse.responseText.replace(/^\s*/, "").replace(/\s*$/, "");
        			        if (status == "passwordResetSuccessful") {
                            	window.location.replace("login.html?resetPassword=1");
        			        } else if (status == "emailSendError" || status == "passwordResetError") {
        			        	responseMessage.innerHTML = '<SPAN style=color:red;><fmt:message key="generalErrorMessage" /></SPAN>';
        			        } else if (status == "validationError") {
        			            responseMessage.innerHTML = '<SPAN style=color:red;><fmt:message key="invalidEmailErrorMessage" /></SPAN>';
        			        }
        			    },
        			    failure : function(objServerResponse) {
        			        responseMessage.innerHTML = '<SPAN style=color:red;><fmt:message key="generalErrorMessage"/></SPAN>';
        			    }
        			});
    		} 
        }

        Ext.QuickTips.init();
        var resetLoginForm = new Ext.FormPanel({
            formId: 'appResetLoginForm',
            labelWidth: 120,
            frame:true, 
            title:'<fmt:message key="resetPasswordTitle"/>', 
            defaultType: 'textfield',
            monitorValid: true,
            keys:[
                {
                     key : Ext.EventObject.ENTER, 
                     handler: submitFn
                }],
            items:[{
                    id: 'message',
                    xtype: 'panel',
                    html: '<div style="text-align:center;font-weight;bold;" id=responseMessage></div>'
                },{
                    fieldLabel: '<fmt:message key="eMail"/>',
                    name: 'email',
                    id: 'email',
                    allowBlank: false,
                    inputType: 'text',
                    width: 190
                }
            ],
            buttons:[
                {
                    text: '<fmt:message key="resetPassword"/>', 
                    type: 'submit', 
                    id: 'submitButton', 
                    formBind: true, 
                    border: true,
                    handler: submitFn
                }]

        });
        
        // This just creates a window to wrap the login form. 
        // The login object is passed to the items collection.       
        var win = new Ext.Window({
            modal: true,
            width:350,
            closable: false,
            resizable: false,
            draggable: false,
            plain: true,
            border: false,
            items: [resetLoginForm]                                 
        });
        win.show();
        
    });
    </script>
</head>

<body>
<div id="resetLoginForm"></div>
</body>
</html>
