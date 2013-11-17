echo "Usage  : sh runFmodDesginer.sh <example_name>"
echo "Example: sh runFmodDesginer.sh SimpleEvent"
echo Running $1 ...
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:../lib/
java -Djava.library.path=../lib/ -classpath .:./:./NativeFmodEx-Examples.jar:../lib/NativeFmodDesigner.jar:../lib/NativeFmodDesigner.jar org.jouvieje.FmodDesigner.Examples.$1