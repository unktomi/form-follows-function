@echo off

echo ===========================
echo = NativeFmodEx.jar script =
echo ===========================

set SOURCE_PATH=../../Src/NativeFmodEx/Java/
set DEFAULT_PACKAGE=org/jouvieje/FmodEx/
set DEFAULT_FOLDER=org\jouvieje\FmodEx\

echo .
echo =============
echo = Compiling =
echo =============
javac -d ./ -sourcepath %SOURCE_PATH% %SOURCE_PATH%%DEFAULT_PACKAGE%*.java %SOURCE_PATH%%DEFAULT_PACKAGE%Callbacks/*.java %SOURCE_PATH%%DEFAULT_PACKAGE%Defines/*.java %SOURCE_PATH%%DEFAULT_PACKAGE%Enumerations/*.java %SOURCE_PATH%%DEFAULT_PACKAGE%Exceptions/*.java %SOURCE_PATH%%DEFAULT_PACKAGE%Misc/*.java %SOURCE_PATH%%DEFAULT_PACKAGE%Structures/*.java

echo .
echo ================
echo = Creating JAR =
echo ================
jar -cf NativeFmodEx.jar %DEFAULT_PACKAGE%*.class %DEFAULT_PACKAGE%Callbacks/*.class %DEFAULT_PACKAGE%Defines/*.class  %DEFAULT_PACKAGE%Enumerations/*.class  %DEFAULT_PACKAGE%Exceptions/*.class %DEFAULT_PACKAGE%Misc/*.class %DEFAULT_PACKAGE%Structures/*.class

echo .
echo ============
echo = Cleaning =
echo ============
del %DEFAULT_FOLDER%*.class
del %DEFAULT_FOLDER%Callbacks\*.class
del %DEFAULT_FOLDER%Defines\*.class
del %DEFAULT_FOLDER%Enumerations\*.class
del %DEFAULT_FOLDER%Exceptions\*.class
del %DEFAULT_FOLDER%Misc\*.class
del %DEFAULT_FOLDER%Structures\*.class

rmdir %DEFAULT_FOLDER%Callbacks
rmdir %DEFAULT_FOLDER%Defines
rmdir %DEFAULT_FOLDER%Enumerations
rmdir %DEFAULT_FOLDER%Exceptions
rmdir %DEFAULT_FOLDER%Misc
rmdir %DEFAULT_FOLDER%Structures
rmdir %DEFAULT_FOLDER%
rmdir %DEFAULT_FOLDER%..\
rmdir %DEFAULT_FOLDER%..\..\

echo Finish !

pause