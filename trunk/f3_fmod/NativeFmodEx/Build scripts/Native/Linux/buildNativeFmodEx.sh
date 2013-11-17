echo Remember to change PLATFORM=LINUX
echo Remember to change _msize by malloc_usable_size
echo Remember to make changes for showConfigDialog ...
g++ -O3 -o ../Export/libNativeFmodEx.so -shared -fPIC *.cpp libfmodex.so -pthread
