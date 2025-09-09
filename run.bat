@echo off
setlocal enabledelayedexpansion

rem -------------------------------------------------------------------
rem  Parking Management System - Build & Run
rem -------------------------------------------------------------------

rem ===== Cosmetic setup =====
chcp 65001 >nul
title Parking Management System
color 0B
cls

rem ===== Banner =====
call :banner

rem ===== Config =====
set "BIN_DIR=bin"
set "MAIN_CLASS=Main"
if not "%~1"=="" set "MAIN_CLASS=%~1"

pushd "%~dp0"

rem ===== Find MySQL connector JAR =====
set "JAR_NAME="
for %%J in ("mysql-connector-j-*.jar") do (
  set "JAR_NAME=%%~nxJ"
  goto :jar_found
)
:jar_found
if not defined JAR_NAME (
  call :err "MySQL connector JAR not found (expected: mysql-connector-j-*.jar)"
  goto :end_fail
)

rem ===== Check JDK tools =====
where javac >nul 2>&1 || (call :err "javac not found on PATH. Install JDK / add to PATH." & goto :end_fail)

rem ===== Step: Clean =====
call :step "Cleaning old build"
if exist "%BIN_DIR%" rmdir /s /q "%BIN_DIR%"
mkdir "%BIN_DIR%" 1>nul 2>nul
call :ok

rem ===== Step: Collect sources =====
call :step "Discovering Java sources"
set "SRC_LIST=%TEMP%\java_sources_%RANDOM%_%RANDOM%.txt"
if exist "%SRC_LIST%" del /q "%SRC_LIST%" >nul 2>&1
for /r %%F in (*.java) do echo %%~fF>>"%SRC_LIST%"
if not exist "%SRC_LIST%" (
  call :err "No .java files found."
  goto :end_fail
)
for /f %%A in ('type "%SRC_LIST%" ^| find /c /v ""') do set "SRC_COUNT=%%A"
if "!SRC_COUNT!"=="0" (
  call :err "No .java files found."
  goto :end_fail
)
call :ok "Found !SRC_COUNT! files"

rem ===== Step: Compile =====
call :step "Compiling sources"
javac -cp ".;%JAR_NAME%" -d "%BIN_DIR%" @"%SRC_LIST%"
if errorlevel 1 (
  del /q "%SRC_LIST%" >nul 2>&1
  call :err "Compilation failed."
  goto :end_fail
)
del /q "%SRC_LIST%" >nul 2>&1
call :ok "Build complete"

rem ===== Step: Run =====
call :step "Launching application"
echo    Classpath: %BIN_DIR%;%JAR_NAME%
echo    Main     : %MAIN_CLASS%
echo.
java -cp "%BIN_DIR%;%JAR_NAME%" %MAIN_CLASS%
set "EXITCODE=%ERRORLEVEL%"
echo.

if "%EXITCODE%"=="0" (
  call :done "Exited successfully (code 0)"
  goto :end_ok
) else (
  call :err  "Application exited with code %EXITCODE%"
  goto :end_fail
)

:end_ok
echo.
echo Press any key to close...
pause >nul
popd
endlocal & exit /b 0

:end_fail
echo.
echo Press any key to close...
pause >nul
popd
endlocal & exit /b 1

rem ========================= Helpers =========================

:banner
echo.
echo ╔══════════════════════════════════════════════════════════════╗
echo ║                  Welcome To Parking Management System        ║
echo ╚══════════════════════════════════════════════════════════════╝
echo   %date%  %time%
echo.
goto :eof

:step
set "MSG=%~1"
set "EXTRA=%~2"
set /p "=→ %MSG%... "<nul
goto :eof

:ok
set "EXTRA=%~1"
echo [OK]
if not "%EXTRA%"=="" echo    %EXTRA%
goto :eof

:err
set "ERRMSG=%~1"
echo [FAIL]
echo    %ERRMSG%
goto :eof

:done
set "MSG=%~1"
echo ✓ %MSG%
goto :eof
