@echo off

if "%F3_HOME%"=="" goto noF3Home

if not exist "%BTRACE_HOME%\build\btrace-agent.jar" goto noBTraceHome

%F3_HOME%\bin\f3 -javaagent:%BTRACE_HOME%\build\btrace-agent.jar=dumpClasses=false,debug=false,unsafe=true,probeDescPath=.,noServer=true,script=%1 %2 %3 %4 %5 %6 %7 %8 %9
goto end

:noBTraceHome
  echo Please set BTRACE_HOME before running this script
  goto end

:noF3Home
  echo Please set F3_HOME before running this script
:end
