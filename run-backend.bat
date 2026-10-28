@echo off
setlocal
cd /d "%~dp0"
title Serendib Tourism - Student Version

echo ===============================================================
echo  SERENDIB TOURISM - YEAR 1 STUDENT VERSION
echo ===============================================================
echo.

where java >nul 2>nul
if errorlevel 1 (
    echo Java was not found.
    echo Install JDK 17 or newer, then try again.
    pause
    exit /b 1
)

set "APP_JAR=target\serendib-tourism-student-1.0.0.jar"

echo Building latest website files...
call mvnw.cmd clean package -DskipTests

if errorlevel 1 (
    echo Build failed!
    pause
    exit /b 1
)

echo Website:     http://localhost:8080/
echo Admin panel: http://localhost:8080/admin/
echo.
echo Keep this window open while using the website.
echo Press Ctrl+C to stop the server.
echo.

:: Automatically open browser once server initializes
start /b "" cmd /c "timeout /t 3 /nobreak >nul & start http://localhost:8080/"

java -jar "%APP_JAR%"
pause
