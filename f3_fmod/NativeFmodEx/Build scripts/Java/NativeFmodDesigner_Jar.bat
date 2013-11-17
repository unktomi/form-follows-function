@echo off

echo =================================
echo = NativeFmodDesigner.jar script =
echo =================================

set SOURCE_PATH=../../Src/NativeFmodDesigner/Java/
set DEFAULT_PACKAGE=org/jouvieje/FmodDesigner/
set DEFAULT_FOLDER=org\jouvieje\FmodDesigner\

echo .
echo =============
echo = Compiling =
echo =============
javac -d ./ -classpath ./NativeFmodEx.jar -sourcepath %SOURCE_PATH% %SOURCE_PATH%%DEFAULT_PACKAGE%*.java %SOURCE_PATH%%DEFAULT_PACKAGE%Defines/*.java %SOURCE_PATH%%DEFAULT_PACKAGE%Enumerations/*.java

echo .
echo ================
echo = Creating JAR =
echo ================
jar -cf NativeFmodDesigner.jar %DEFAULT_PACKAGE%*.class %DEFAULT_PACKAGE%Defines/*.class %DEFAULT_PACKAGE%Enumerations/*.class

echo .
echo ============
echo = Cleaning =
echo ============
del %DEFAULT_FOLDER%*.class
del %DEFAULT_FOLDER%Defines\*.class

rmdir %DEFAULT_FOLDER%Defines
rmdir %DEFAULT_FOLDER%
rmdir %DEFAULT_FOLDER%..\
rmdir %DEFAULT_FOLDER%..\..\
rmdir %DEFAULT_FOLDER%..\..\..\

echo Finish !

pause