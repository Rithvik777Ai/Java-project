@echo off
set "SCRIPT_DIR=%~dp0"
if "%JAVA_HOME%"=="" (
    if exist "C:\Program Files\Java\jdk-26.0.2" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-26.0.2"
    )
)
"%SCRIPT_DIR%apache-maven-3.9.6\bin\mvn.cmd" %*
