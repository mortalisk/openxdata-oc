@ECHO OFF
ECHO Importing database...
"%{INSTALL_PATH}\script\mysql.exe" -u%{mysql.username} --password=%{mysql.password} -P%{mysql.port} < "%{INSTALL_PATH}\script\create-database.sql"
ECHO Database import complete
verify > nul
