@echo off
setlocal EnableExtensions EnableDelayedExpansion

set "ROOT=%~dp0"
set "SRC=%ROOT%src\main\java\net\solace\mappings\generator"
set "CLASSES=%ROOT%classes"
set "LIB=%ROOT%lib"
set "JARS=%ROOT%jars"
set "MAPPINGS=%ROOT%mappings"
set "OUTPUT=%ROOT%output"
set "CP=%CLASSES%;%LIB%\gson-2.8.9.jar;%LIB%\asm-9.2.jar;%LIB%\asm-tree-9.2.jar"

if not exist "%JARS%" mkdir "%JARS%"
if not exist "%MAPPINGS%" mkdir "%MAPPINGS%"
if not exist "%OUTPUT%" mkdir "%OUTPUT%"
if not exist "%CLASSES%" mkdir "%CLASSES%"

echo.
echo Solace standalone mappings generator
echo.
set /p VERSION=Enter RuneLite version: 
if "%VERSION%"=="" (
    echo No version entered.
    exit /b 2
)

set "NEW_JAR=%JARS%\injected-client-%VERSION%.jar"
if not exist "%NEW_JAR%" (
    echo.
    echo Missing new jar:
    echo   %NEW_JAR%
    echo Put injected-client-%VERSION%.jar in the jars folder and run again.
    exit /b 2
)

set "OLD_MAPPINGS=%MAPPINGS%\mappings.json"
if not exist "%OLD_MAPPINGS%" (
    for %%F in ("%MAPPINGS%\*.json") do (
        if not defined OLD_MAPPINGS_FOUND set "OLD_MAPPINGS_FOUND=%%~fF"
    )
    if defined OLD_MAPPINGS_FOUND set "OLD_MAPPINGS=!OLD_MAPPINGS_FOUND!"
)
if not exist "%OLD_MAPPINGS%" (
    echo.
    echo Missing reference mappings.
    echo Put the previous mappings.json in:
    echo   %MAPPINGS%
    exit /b 2
)

set "OLD_JAR="
set "OLD_COUNT=0"
for %%F in ("%JARS%\injected-client-*.jar") do (
    if /I not "%%~fF"=="%NEW_JAR%" (
        set /a OLD_COUNT+=1
        set "OLD_JAR=%%~fF"
    )
)

if "%OLD_COUNT%"=="0" (
    echo.
    echo Missing reference jar.
    echo Put the previous injected-client jar in:
    echo   %JARS%
    exit /b 2
)

if not "%OLD_COUNT%"=="1" (
    echo.
    echo Found multiple possible reference jars:
    for %%F in ("%JARS%\injected-client-*.jar") do (
        if /I not "%%~fF"=="%NEW_JAR%" echo   %%~nxF
    )
    echo.
    set /p OLD_NAME=Enter exact previous jar filename from jars folder: 
    set "OLD_JAR=%JARS%\!OLD_NAME!"
    if not exist "!OLD_JAR!" (
        echo Could not find !OLD_JAR!
        exit /b 2
    )
)

set "OUT_FILE=%OUTPUT%\mappings-%VERSION%.json"
set "LOG_FILE=%OUTPUT%\mapper-%VERSION%.log"

echo.
echo Reference jar:      %OLD_JAR%
echo Reference mappings: %OLD_MAPPINGS%
echo New jar:            %NEW_JAR%
echo Output:             %OUT_FILE%
echo Log:                %LOG_FILE%
echo.

echo Compiling standalone generator...
javac -cp "%LIB%\gson-2.8.9.jar;%LIB%\asm-9.2.jar;%LIB%\asm-tree-9.2.jar" -d "%CLASSES%" ^
    "%SRC%\dto\JClass.java" ^
    "%SRC%\dto\JField.java" ^
    "%SRC%\dto\JMethod.java" ^
    "%SRC%\MappingGenerator.java"
if errorlevel 1 (
    echo Compile failed.
    exit /b 1
)

echo Running mapper...
java -Dmapping.verbose=true -cp "%CP%" net.solace.mappings.generator.MappingGenerator "%OLD_JAR%" "%OLD_MAPPINGS%" "%NEW_JAR%" "%OUT_FILE%" "%LOG_FILE%"
if errorlevel 1 (
    echo Mapper failed.
    exit /b 1
)

echo.
echo Done:
echo   %OUT_FILE%
echo   %LOG_FILE%
pause
