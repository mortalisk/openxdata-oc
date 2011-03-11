@ECHO OFF
ECHO Installing Mysql service
"%{INSTALL_PATH}\mysql5\bin\mysqld-nt.exe" --install openXmysql
net start openxmysql
ping -n 10 localhost >NUL
verify > nul
