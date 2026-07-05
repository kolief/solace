@echo off
setlocal EnableDelayedExpansion

set "JAR="
for %%F in ("%~dp0client\loader\build\libs\solace-*.jar") do set "JAR=%%~fF"

if not defined JAR (
    echo Build the client first: gradlew buildClient
    pause
    exit /b 1
)

java -jar "!JAR!" %*
if errorlevel 1 pause
