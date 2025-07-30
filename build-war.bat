@echo off
set SRC_DIR=src
set CLASSES_DIR=WEB-INF\classes
set WEB_XML=WEB-INF\web.xml
set WEB_INF_DIR=WEB-INF
set WEB_DIR=WebContent
set WAR_NAME=attendance-system.war
set TOMCAT_HOME=C:/Tomcat/apache-tomcat-9.0.106

echo Building WAR file for attendance system... & echo.

REM Clean classes directory
if exist %CLASSES_DIR% rmdir /s /q %CLASSES_DIR%
mkdir %CLASSES_DIR%

REM Compile Java source files to WEB-INF/classes
echo Compiling Java sources... & echo.
javac -d %CLASSES_DIR% -cp "%TOMCAT_HOME%\lib\servlet-api.jar" ^
  %SRC_DIR%\com\AttendanceManagementSystem\model\*.java ^
  %SRC_DIR%\com\AttendanceManagementSystem\storage\*.java ^
  %SRC_DIR%\com\AttendanceManagementSystem\servlet\*.java

if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

REM Remove existing WAR file
if exist %WAR_NAME% del /q %WAR_NAME%

REM Create temporary directory structure for WAR packaging
set TEMP_WAR_DIR=temp_war
if exist %TEMP_WAR_DIR% rmdir /s /q %TEMP_WAR_DIR%
mkdir %TEMP_WAR_DIR%

REM Copy web content (HTML, CSS, JS, JSP files, etc.)
echo Copying web content... & echo.
xcopy /E /I /Y %WEB_DIR%\* %TEMP_WAR_DIR%\

REM Create WEB-INF directory in temp structure
mkdir %TEMP_WAR_DIR%\WEB-INF

REM Copy web.xml
echo Copying web.xml... & echo.
copy /Y %WEB_XML% %TEMP_WAR_DIR%\WEB-INF\

REM Copy compiled classes maintaining package structure
echo Copying compiled classes... & echo.
xcopy /E /I /Y %CLASSES_DIR%\* %TEMP_WAR_DIR%\WEB-INF\classes\

REM Copy any libraries if they exist
if exist %WEB_INF_DIR%\lib (
    echo Copying libraries... & echo.
    xcopy /E /I /Y %WEB_INF_DIR%\lib %TEMP_WAR_DIR%\WEB-INF\lib\
)

REM Package WAR file from temporary directory
echo Creating WAR file... & echo.
cd %TEMP_WAR_DIR%
jar -cvf ..\%WAR_NAME% *
cd ..

REM Clean up temporary directory
rmdir /s /q %TEMP_WAR_DIR%

echo WAR file created: %WAR_NAME%

REM Deploy to Tomcat
echo Deploying to Tomcat...
copy /Y %WAR_NAME% "%TOMCAT_HOME%\webapps\"

if %ERRORLEVEL% equ 0 (
    echo WAR file successfully copied to Tomcat webapps directory.
) else (
    echo Failed to copy WAR file to Tomcat!
)

pause