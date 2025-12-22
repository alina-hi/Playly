@echo off
chcp 65001 > nul
echo ========================================
echo Building Playly Project
echo ========================================
echo.

REM Проверяем структуру
echo Checking project structure...
if not exist "src\com\playly\DatabaseConnection.java" (
    echo ERROR: DatabaseConnection.java not found!
    echo It should be in: src\com\playly\DatabaseConnection.java
    pause
    exit /b 1
)

REM Очищаем папку classes
echo Cleaning classes folder...
if exist classes rmdir /s /q classes
mkdir classes

REM Компилируем ВСЕ Java файлы с кодировкой UTF-8
echo Compiling Java files with UTF-8 encoding...

REM Компилируем все файлы сразу с правильной кодировкой
javac -encoding UTF-8 -cp "lib\*;src" -d classes ^
    src\com\playly\models\User.java ^
    src\com\playly\DatabaseConnection.java ^
    src\com\playly\dao\UserDAO.java ^
    src\com\playly\RegistrationServlet.java ^
    src\com\playly\*.java

REM Проверяем результат
if errorlevel 1 (
    echo.
    echo COMPILATION FAILED!
    echo.
    echo Trying step-by-step compilation...
    echo.
    goto stepbystep
)

echo.
echo Compilation successful!
goto success

:stepbystep
echo Step 1: Compiling User model...
javac -encoding UTF-8 -cp "lib\*" -d classes src\com\playly\models\User.java
if errorlevel 1 goto failed

echo Step 2: Compiling DatabaseConnection...
javac -encoding UTF-8 -cp "lib\*;classes" -d classes src\com\playly\DatabaseConnection.java
if errorlevel 1 goto failed

echo Step 3: Compiling UserDAO...
javac -encoding UTF-8 -cp "lib\*;classes" -d classes src\com\playly\dao\UserDAO.java
if errorlevel 1 goto failed

echo Step 4: Compiling RegistrationServlet...
javac -encoding UTF-8 -cp "lib\*;classes" -d classes src\com\playly\RegistrationServlet.java
if errorlevel 1 goto failed

echo Step 5: Compiling other files...
javac -encoding UTF-8 -cp "lib\*;classes" -d classes src\com\playly\*.java
if errorlevel 1 goto failed

goto success

:failed
echo.
echo ❌ Compilation failed!
echo.
echo Please check:
echo 1. DatabaseConnection.java exists in src\com\playly\
echo 2. UserDAO.java uses !Optional.isPresent() instead of .isEmpty()
echo 3. All files have correct package declarations
pause
exit /b 1

:success
echo.
echo ✅ Compilation successful!
echo.
echo Compiled classes:
dir classes\com\playly\ /b
if exist classes\com\playly\dao dir classes\com\playly\dao\ /b
if exist classes\com\playly\models dir classes\com\playly\models\ /b

echo.
echo ========================================
echo Build complete!
echo ========================================
pause