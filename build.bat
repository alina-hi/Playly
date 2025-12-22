@echo off
echo ========================================
echo Building Playly Project
echo ========================================
echo.

REM Очищаем старые классы
if exist classes rmdir /s /q classes
mkdir classes

echo Step 1: Compiling models...
javac -encoding UTF-8 -cp "lib\*;src" -d classes ^
    src\com\playly\models\*.java

echo Step 2: Compiling DatabaseConnection...
javac -encoding UTF-8 -cp "lib\*;src;classes" -d classes ^
    src\com\playly\DatabaseConnection.java

echo Step 3: Compiling DAO classes...
javac -encoding UTF-8 -cp "lib\*;src;classes" -d classes ^
    src\com\playly\dao\*.java

echo Step 4: Compiling servlets...
javac -encoding UTF-8 -cp "lib\*;src;classes" -d classes ^
    src\com\playly\servlets\*.java

REM Проверяем результат
if errorlevel 1 (
    echo.
    echo ❌ COMPILATION FAILED!
    echo.
    pause
    exit /b 1
)

echo.
echo ✅ Compilation successful!
echo.
echo Compiled classes:
tree classes /F
echo.
echo ========================================
echo Build complete!
echo ========================================
pause