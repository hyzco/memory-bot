@echo off
setlocal enabledelayedexpansion
title AstralPlugin - Build ^& Deploy

cd /d "%~dp0"

echo ============================================
echo   AstralPlugin - Build ^& Deploy
echo ============================================
echo.

:: Build
echo [1/3] Building...
call mvn -q -DskipTests package
if errorlevel 1 (
    echo BUILD FAILED
    pause
    exit /b 1
)
echo       OK

:: Deploy to plugins
set "PLUGINS_DIR=%~dp0..\plugins"
echo [2/4] Deploying to %PLUGINS_DIR%...
copy /y "target\AstralPlugin.jar" "%PLUGINS_DIR%\AstralPlugin.jar" >nul
echo       OK

:: Strip conflicting astral classes from DmPlugin.jar (prevents class shadowing)
echo [3/4] Cleaning DmPlugin.jar conflicts...
if exist "%PLUGINS_DIR%\DmPlugin.jar" (
    set "DM_TMP=%TEMP%\dm_repack_%RANDOM%"
    mkdir "%DM_TMP%" 2>nul
    pushd "%DM_TMP%"
    jar xf "%PLUGINS_DIR%\DmPlugin.jar"
    if exist "com\deeme\modules\astral" rd /s /q "com\deeme\modules\astral"
    if exist "com\deemeplus\modules\astral" rd /s /q "com\deemeplus\modules\astral"
    jar cf "%PLUGINS_DIR%\DmPlugin.jar" -C . .
    popd
    rd /s /q "%DM_TMP%"
    echo       OK  ^(astral classes removed from DmPlugin^)
) else (
    echo       SKIP  ^(DmPlugin.jar not found^)
)

:: Update trusted-plugins.txt hash
echo [4/4] Updating trusted hashes...
set "TRUST_FILE=%PLUGINS_DIR%\trusted-plugins.txt"
for /f "delims=" %%H in ('certutil -hashfile "%PLUGINS_DIR%\AstralPlugin.jar" SHA256 ^| findstr /r "^[0-9a-f]"') do set "HASH=%%H"
set "HASH=%HASH: =%"

:: Remove old entries and add new ones
if exist "%TRUST_FILE%" (
    findstr /v /c:"AstralPlugin.jar" "%TRUST_FILE%" > "%TRUST_FILE%.tmp"
    move /y "%TRUST_FILE%.tmp" "%TRUST_FILE%" >nul
)
echo %HASH% AstralPlugin.jar>> "%TRUST_FILE%"
:: Also update DmPlugin hash if it was modified
if exist "%PLUGINS_DIR%\DmPlugin.jar" (
    for /f "delims=" %%D in ('certutil -hashfile "%PLUGINS_DIR%\DmPlugin.jar" SHA256 ^| findstr /r "^[0-9a-f]"') do set "DM_HASH=%%D"
    set "DM_HASH=!DM_HASH: =!"
    if exist "%TRUST_FILE%" (
        findstr /v /c:"DmPlugin.jar" "%TRUST_FILE%" > "%TRUST_FILE%.tmp"
        move /y "%TRUST_FILE%.tmp" "%TRUST_FILE%" >nul
    )
    echo !DM_HASH! DmPlugin.jar>> "%TRUST_FILE%"
)
echo       OK  (hash: %HASH%)

echo.
echo ============================================
echo   Done! Restart DarkBot to load the plugin.
echo ============================================
echo.

:: Also copy to dist folder for sharing
if not exist "dist" mkdir dist
copy /y "target\AstralPlugin.jar" "dist\AstralPlugin.jar" >nul
copy /y "%~dp0install.bat" "dist\install.bat" >nul 2>nul
echo   Shareable copy in: dist\AstralPlugin.jar
echo.
pause
