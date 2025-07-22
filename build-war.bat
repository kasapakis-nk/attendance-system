@echo off

REM Create WAR file from Vite build
jar -cvf attendance-system.war -C dist . WEB-INF\

echo WAR file created: attendance-system.war
pause