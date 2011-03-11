@ECHO off
rem just to avoid accidentally executing this file
if "%1"=="updateoxd" goto update
goto end
:update
copy "%{installed.dir}\Tomcat6.0\webapps\openXdata\OPENXDATA_SETTINGS.properties" "%{installed.dir}\Tomcat6.0\webapps\"
rd "%{installed.dir}\Tomcat6.0\webapps\openXdata" /s /q
mkdir "%{installed.dir}\Tomcat6.0\webapps\openXdata"
xcopy "%{INSTALL_PATH}\openXdata" "%{installed.dir}\Tomcat6.0\webapps\openXdata" /r /e /y
xcopy "%{INSTALL_PATH}\version" "%{installed.dir}\" /y
move "%{installed.dir}\Tomcat6.0\webapps\OPENXDATA_SETTINGS.properties" "%{installed.dir}\Tomcat6.0\webapps\openXdata\"
ECHO Deleting update files
rd "%{INSTALL_PATH}\openXdata" /s /q
:end
verify > nul