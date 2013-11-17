echo "Usage  : sh runFmodEx.sh <example_name>"
echo "Example: sh runFmodEx.sh PlaySound"
echo Running $1 ...
java -Djava.library.path=../lib/ -classpath .:./:./NativeFmodEx-Examples.jar:../lib/NativeFmodEx.jar org.jouvieje.FmodEx.Examples.$1 $2