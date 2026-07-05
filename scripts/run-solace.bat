@echo off
setlocal EnableDelayedExpansion

set "JAR="
for %%F in ("%~dp0..\client\loader\build\libs\solace-*.jar") do set "JAR=%%~fF"

if not defined JAR (
    echo No solace fat jar found in client\loader\build\libs\
    echo Build it first: gradlew buildClient
    pause
    exit /b 1
)

java -jar "!JAR!" %*
