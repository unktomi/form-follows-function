@echo off

if "%F3_HOME%"=="" goto noF3Home

if not exist "%BTRACE_HOME%\build\btrace-agent.jar" goto noBTraceHome

javac -cp "%BTRACE_HOME%\build\btrace-client.jar;%F3_HOME%\lib\shared\f3rt.jar" %*
goto end

:noBTraceHome
  echo Please set BTRACE_HOME before running this script
  goto end

:noF3Home
  echo Please set F3_HOME before running this script
:end
