@echo off
echo ========================================
echo Deploying Playly to Tomcat
echo ========================================
echo.

REM Останавливаем Tomcat если запущен
echo Stopping Tomcat...
call D:\apache-tomcat\apache-tomcat-9.0.112\bin\shutdown.bat
timeout /t 3 /nobreak > nul

REM Очищаем старую версию
echo Cleaning old deployment...
rmdir /s /q "D:\apache-tomcat\apache-tomcat-9.0.112\webapps\playly" 2>nul

REM Создаем структуру
echo Creating directory structure...
mkdir "D:\apache-tomcat\apache-tomcat-9.0.112\webapps\playly"
mkdir "D:\apache-tomcat\apache-tomcat-9.0.112\webapps\playly\WEB-INF"
mkdir "D:\apache-tomcat\apache-tomcat-9.0.112\webapps\playly\WEB-INF\classes"
mkdir "D:\apache-tomcat\apache-tomcat-9.0.112\webapps\playly\WEB-INF\lib"

REM Копируем классы
echo Copying compiled classes...
xcopy "classes\*" "D:\apache-tomcat\apache-tomcat-9.0.112\webapps\playly\WEB-INF\classes\" /E /I /Y

REM Копируем библиотеки
echo Copying libraries...
xcopy "lib\*.jar" "D:\apache-tomcat\apache-tomcat-9.0.112\webapps\playly\WEB-INF\lib\" /Y

REM Копируем веб-файлы
echo Copying web files...
xcopy "webapp\*" "D:\apache-tomcat\apache-tomcat-9.0.112\webapps\playly\" /E /I /Y

REM Запускаем Tomcat
echo Starting Tomcat...
start "Tomcat" "D:\apache-tomcat\apache-tomcat-9.0.112\bin\startup.bat"

echo.
echo ✅ Deployment complete!
echo.
echo Your application is available at:
echo http://localhost:8095/playly/
echo http://localhost:8095/playly/register.html
echo.
echo Press any key to continue...
pause > nul