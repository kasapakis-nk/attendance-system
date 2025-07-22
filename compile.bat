@echo off
echo Compiling Java files...

set TOMCAT_PATH=C:\Tomcat\apache-tomcat-9.0.106

javac -cp "%TOMCAT_PATH%\lib\servlet-api.jar" -d WEB-INF\classes src\com\AttendanceManagementSystem\model\*.java
javac -cp "%TOMCAT_PATH%\lib\servlet-api.jar;WEB-INF\classes" -d WEB-INF\classes src\com\AttendanceManagementSystem\storage\*.java
javac -cp "%TOMCAT_PATH%\lib\servlet-api.jar;WEB-INF\classes" -d WEB-INF\classes src\com\AttendanceManagementSystem\servlet\*.java

echo Done!
pause