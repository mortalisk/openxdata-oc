@ECHO OFF
%{mysql.script.comment} net start openXmysql
net start openXtomcat6
start http://localhost:%{tomcat.port}/openXdata/OpenXDataServerAdmin.html