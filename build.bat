@echo off
echo ============================================
echo BUILDING PLAYLY PROJECT
echo ============================================

set PROJECT=C:\Users\v3231\IdeaProjects\playly
set TOMCAT=D:\apache-tomcat\apache-tomcat-9.0.112

echo 1. Checking Java installation...
where javac
if errorlevel 1 (
    echo ERROR: javac not found. Check JAVA_HOME
    pause
    exit /b 1
)

echo.
echo 2. Checking source files...
dir "%PROJECT%\src\com\playly\*.java"

echo.
echo 3. Compiling Java files...
javac -cp "%PROJECT%\lib\*;%TOMCAT%\lib\servlet-api.jar" ^
    -d "%PROJECT%\classes" ^
    "%PROJECT%\src\com\playly\Playground.java" ^
    "%PROJECT%\src\com\playly\DatabaseConnection.java" ^
    "%PROJECT%\src\com\playly\PlaygroundDAO.java" ^
    "%PROJECT%\src\com\playly\ApiServlet.java"

if errorlevel 1 (
    echo.
    echo COMPILATION FAILED!
    echo Check your Java files for errors.
    pause
    exit /b 1
)

echo.
echo 4. Checking compiled classes...
dir "%PROJECT%\classes\com\playly\*.class"

echo.
echo ============================================
echo BUILD SUCCESSFUL!
echo ============================================
echo Run deploy.bat to deploy to Tomcat
pause