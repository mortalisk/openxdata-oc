@ECHO OFF
rem "%{INSTALL_PATH}\Tomcat6.0\bin\service.bat" remove
echo Assuming openXtomcat6 is installed
echo stopping service openXtomcat6
net stop openXtomcat6
echo deleting service openXtomcat6
sc delete openXtomcat6
verify > nul
