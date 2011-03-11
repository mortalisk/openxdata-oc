@ECHO OFF
rem "%{INSTALL_PATH}\mysql5\bin\mysqld-nt.exe" --uninstall openXmysql
echo Assuming openXmysql is installed
echo stopping service openXmysql
net stop openXmysql
echo deleting service openxmysql
sc delete openXmysql
verify > nul