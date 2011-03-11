@ECHO OFF
ECHO Installing Tomcat Service
SET CATALINA_HOME=%{tomcat.dir}
CALL "%{INSTALL_PATH}\script\tomcat-service.bat" install openXtomcat6
net start openxtomcat6
verify > nul
