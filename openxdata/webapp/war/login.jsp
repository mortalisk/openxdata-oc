<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<%@ taglib prefix='fmt' uri='http://java.sun.com/jsp/jstl/fmt' %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="org.springframework.security.ui.AbstractProcessingFilter" %>
<%@ page import="org.springframework.security.ui.webapp.AuthenticationProcessingFilter" %>
<%@ page import="org.springframework.security.AuthenticationException" %>

<html>
  <head>
    <%@ include file="locale.jsp" %>
    <fmt:setBundle basename='org.openxdata.client.AppMessages'/>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title><fmt:message key="title" /></title>
    
    <link type="text/css" rel="stylesheet" href="Emit.css">
    <link rel="stylesheet" type="text/css" href="extjs/resources/css/ext-all.css" />
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

        Ext.QuickTips.init();
        
        var loginForm = new Ext.form.FormPanel({
            formId: 'appLoginForm',
            labelWidth: 120,
            frame:false, 
            defaultType: 'textfield',
            monitorValid: false,
            keys:[
                {
                     key : Ext.EventObject.ENTER, 
                     fn: function() {
                        loginForm.getForm().submit();
                     }
                }],
            standardSubmit: true,
            items:[{
                    id: 'message',
                    xtype: 'panel',
                    html: '<div style="text-align: center;">&nbsp;<c:choose><c:when test="${!empty param.resetPassword}"><fmt:message key="resetPasswordMessage"/></c:when><c:otherwise><c:if test="${!empty SPRING_SECURITY_LAST_EXCEPTION.message}"><font color="red"><fmt:message key="failedLoginMessage"/></font></c:if></c:otherwise></c:choose></div>'
                },{
                    fieldLabel: '<fmt:message key="username"/>',
                    name: 'j_username',
                    allowBlank: false
                },{
                    fieldLabel: '<fmt:message key="password"/>',
                    name: 'j_password',
                    allowBlank: false,
                    inputType: 'password'
                }
            ],
            buttonAlign: 'center',
            buttons:[
                {
                    text: '<fmt:message key="login"/>', 
                    type: 'submit', 
                    id: 'submitButton', 
                    formBind: true, 
                    handler: function() {
                        loginForm.getForm().submit();
                    }
                }]
        });

        var loginPanel = new Ext.Panel({
            frame:true,  
            title:'<fmt:message key="loginTitle"/>',
            items:[
                   loginForm,
              {
                   id: 'forgot',
                   xtype: 'panel',
                   html: '<div style="text-align: center;"><a href="resetPassword.html"><fmt:message key="forgotMyPassword"/></a></div>'
              }]
        });

        // This just creates a window to wrap the login pane;. 
        // The login object is passed to the items collection.       
        var win = new Ext.Window({
            modal: true,
            width:300,
            closable: false,
            resizable: false,
            draggable: false,
            plain: true,
            border: false,
            items: [loginPanel]                                  
        });
        win.show();

        //win.setFocusWidget(loginForm.getForm().findField('j_username'));
        //var username = Ext.getCmp('j_username');
        //username.getEl().focus(true, 50);
        //username.clearInvalid().defer(25);
        //loginForm.getForm().findField('j_username').getEl().focus(true);
       
        loginForm.render('loginForm');
        loginForm.getForm().getEl().dom.action = "j_spring_security_check";

    });
    </script> 

  </head>

  <body>
    <div id="loginForm"></div>
  </body>
</html>
