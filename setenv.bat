@echo off
echo Setting up environment variables...

set PROJECT=C:\Users\v3231\IdeaProjects\playly
set CATALINA_HOME=D:\apache-tomcat\apache-tomcat-9.0.112
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_xxx  <-- Укажите ваш путь к JDK

echo PROJECT=%PROJECT%
echo CATALINA_HOME=%CATALINA_HOME%
echo.
echo Environment variables set. Run build.bat then deploy.bat
pause