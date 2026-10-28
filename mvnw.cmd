@REM ----------------------------------------------------------------------------
@REM Serendib Maven Wrapper batch script
@REM ----------------------------------------------------------------------------
@echo off
set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set MVN_CMD=%DIRNAME%.tools\apache-maven-3.9.6\bin\mvn.cmd
if exist "%MVN_CMD%" (
    call "%MVN_CMD%" %*
) else (
    echo Maven tool not found in .tools directory, falling back to system mvn...
    mvn %*
)
