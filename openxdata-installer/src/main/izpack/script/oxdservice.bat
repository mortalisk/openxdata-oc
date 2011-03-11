@ECHO off
IF "%1" == "" GOTO usage
IF "%1" == "restart" GOTO restart
IF "%1" == "stop" GOTO stop
if "%1" == "start" GOTO start

:usage
ECHO Usage oxdservice [start : stop : restart]
GOTO end

:restart
echo Stopping Services
%{mysql.script.comment} net stop openXmysql
net stop openXtomcat6
GOTO start

:stop
echo Stopping Services
%{mysql.script.comment} net stop openXmysql
net stop openXtomcat6
GOTO end

:start
echo Starting Services
%{mysql.script.comment} net start openXmysql
net start openXtomcat6

:end