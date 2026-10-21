@echo off
setlocal

set "BASE=%~dp0"
set "JAR="
set "JAVA_EXE=%ProgramFiles%\Eclipse Adoptium\jdk-17.0.18.8-hotspot\bin\java.exe"
if not exist "%JAVA_EXE%" set "JAVA_EXE=%ProgramFiles%\Eclipse Adoptium\jdk-11.0.30.7-hotspot\bin\java.exe"

if not exist "%JAVA_EXE%" set "JAVA_EXE=java"

for %%F in ("%BASE%DarkBot-*.jar") do set "JAR=%%~fF"
if not defined JAR for %%F in ("%BASE%DarkBot\build\libs\DarkBot*.jar") do set "JAR=%%~fF"

if not defined JAR (
    echo DarkBot jar was not found in "%BASE%" or "%BASE%DarkBot\build\libs".
    echo Put a DarkBot-*.jar next to this bat file, or build from "%BASE%DarkBot".
    exit /b 1
)

"%JAVA_EXE%" -jar "%JAR%" -astral-ui -hide -start %*
