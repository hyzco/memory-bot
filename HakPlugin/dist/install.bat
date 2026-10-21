@echo off
setlocal
title AstralPlugin - Install

echo ============================================
echo   AstralPlugin - Installer
echo ============================================
echo.
echo This will install the Astral Gate plugin
echo into your DarkBot plugins folder.
echo.

:: Try to find DarkBot location
set "INSTALL_DIR="

:: Check if we're already inside the DarkBot folder tree
if exist "%~dp0..\plugins" (
    set "INSTALL_DIR=%~dp0.."
    goto :found
)

:: Check common locations
for %%D in (
    "%USERPROFILE%\Desktop\DarkBot"
    "%USERPROFILE%\Desktop\DO"
    "%USERPROFILE%\Downloads\DarkBot"
    "D:\DO"
    "C:\DarkBot"
    "C:\DO"
) do (
    if exist "%%~D\plugins" (
        set "INSTALL_DIR=%%~D"
        goto :found
    )
)

:: Ask user
echo Could not find DarkBot automatically.
echo Drag your DarkBot folder here, or type the path:
echo.
set /p "INSTALL_DIR=DarkBot path: "
if not exist "%INSTALL_DIR%\plugins" (
    echo ERROR: "%INSTALL_DIR%\plugins" does not exist.
    echo Make sure you point to the main DarkBot folder.
    pause
    exit /b 1
)

:found
set "PLUGINS_DIR=%INSTALL_DIR%\plugins"
echo Found DarkBot at: %INSTALL_DIR%
echo.

:: Copy JAR
echo [1/2] Copying AstralPlugin.jar to plugins...
copy /y "%~dp0AstralPlugin.jar" "%PLUGINS_DIR%\AstralPlugin.jar" >nul
if errorlevel 1 (
    echo FAILED - Is DarkBot running? Close it first.
    pause
    exit /b 1
)
echo       OK

:: Update trust hash
echo [2/2] Adding trust hash...
set "TRUST_FILE=%PLUGINS_DIR%\trusted-plugins.txt"
for /f "delims=" %%H in ('certutil -hashfile "%PLUGINS_DIR%\AstralPlugin.jar" SHA256 ^| findstr /r "^[0-9a-f]"') do set "HASH=%%H"
set "HASH=%HASH: =%"

if not exist "%TRUST_FILE%" (
    echo # Locally trusted plugin hashes> "%TRUST_FILE%"
)
findstr /v /c:"AstralPlugin.jar" "%TRUST_FILE%" > "%TRUST_FILE%.tmp" 2>nul
move /y "%TRUST_FILE%.tmp" "%TRUST_FILE%" >nul 2>nul
echo %HASH% AstralPlugin.jar>> "%TRUST_FILE%"
echo       OK

echo.
echo ============================================
echo   Installed! Restart DarkBot to use it.
echo.
echo   Select "Astral Gate" as your module in
echo   the DarkBot dropdown menu.
echo ============================================
echo.
pause
