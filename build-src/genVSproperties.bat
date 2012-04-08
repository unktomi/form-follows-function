
REM Windows bat file that runs vcvars32.bat for Visual Studio 2003
REM   and echos out a property file with the values of the environment
REM   variables we want, e.g. PATH, INCLUDE, LIB, and LIBPATH.

REM Clean out the current settings
set INCLUDE=
set LIB=
set LIBPATH=

REM Run the vsvars32.bat file, sending it's output to neverland.
set VSVARS32=%VS71COMNTOOLS%\vsvars32.bat
if "%VS71COMNTOOLS%"=="" (
    set VSVARS32=%VS80COMNTOOLS%\vsvars32.bat
    if "%VS80COMNTOOLS%"=="" set VSVARS32=%VS90COMNTOOLS%\vsvars32.bat
)
call "%VSVARS32%" > NUL

REM Create some vars that are not set with VS Express 2008
if "%MSVCDIR%"=="" set MSVCDIR=%VCINSTALLDIR%
REM Try using exe, com might be hanging in ssh environment?
REM     set DEVENVCMD=%DEVENVDIR%\devenv.exe
set DEVENVCMD=%DEVENVDIR%\devenv.com

REM Adjust for lack of devenv in express editions.  This needs more work.
REM VCExpress is the correct executable, but cmd line is different...
if not exist "%DEVENVCMD%" set DEVENVCMD=%DEVENVDIR%\VCExpress.exe

REM Make sure Cygwin is on the path
set PATH="C:\cygwin\bin;C:\cygwin\;%PATH%"

REM Echo out a properties file
echo ############################################################
echo # DO NOT EDIT: This is a generated file.
echo windows.vs.vsvars32.bat=%VSVARS32%
echo windows.vs.DEVENVDIR=%DEVENVDIR%
echo windows.vs.DEVENVCMD=%DEVENVCMD%
echo windows.vs.VCINSTALLDIR=%VCINSTALLDIR%
echo windows.vs.VSINSTALLDIR=%VSINSTALLDIR%
echo windows.vs.MSVCDIR=%MSVCDIR%
echo windows.vs.INCLUDE=%INCLUDE%
echo windows.vs.LIB=%LIB%
echo windows.vs.LIBPATH=%LIBPATH%
echo windows.vs.PATH=%PATH%
echo ############################################################

