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
echo Step 5: Creating WEB-INF structure...
REM Создаем структуру папок в webapp если их нет
if not exist webapp\WEB-INF\views mkdir webapp\WEB-INF\views
if not exist webapp\WEB-INF\lib mkdir webapp\WEB-INF\lib
if not exist webapp\css mkdir webapp\css
if not exist webapp\js mkdir webapp\js

echo.
echo Step 6: Copying JSTL and other libraries...
REM Копируем JSTL библиотеку в WEB-INF/lib
if exist "jstl-1.2.jar" copy "jstl-1.2.jar" "webapp\WEB-INF\lib\" >nul
if exist "lib\jstl-1.2.jar" copy "lib\jstl-1.2.jar" "webapp\WEB-INF\lib\" >nul

REM Копируем другие необходимые библиотеки
if exist "lib\gson-2.8.9.jar" copy "lib\gson-2.8.9.jar" "webapp\WEB-INF\lib\" >nul
if exist "lib\mysql-connector-j-8.0.33.jar" copy "lib\mysql-connector-j-8.0.33.jar" "webapp\WEB-INF\lib\" >nul

echo.
echo Step 7: Copying classes to WEB-INF...
REM Копируем скомпилированные классы в структуру webapp
xcopy classes webapp\WEB-INF\classes /E /Y /Q >nul

echo.
echo ✅ Compilation successful!
echo.
echo Project structure:
echo C:\Users\v3231\IdeaProjects\playly\
tree /F
echo.
echo ========================================
echo Build complete!
echo ========================================
echo.
echo Next steps:
echo 1. Copy "webapp" folder to Tomcat webapps directory
echo 2. Restart Tomcat
echo 3. Open: http://localhost:8095/playly/
echo.
pause