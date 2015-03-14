How to run (only macosx)

You can just download the [Demo App](http://code.google.com/p/form-follows-function/downloads/detail?name=demo.app.dmg&can=2&q=).

Otherwise, to build it yourself:

svn checkout https://form-follows-function.googlecode.com/svn/trunk/ form-follows-function



cd form-follows-function

ant

(Optionally)
> Download and install Awesomium http://awesomium.com/download/ in /Library/Frameworks

> cd f3\_awesomium\_jogl

> ant

> cd ..

(End Optionally)

cd f3\_cg\_jogl\_awt

ant

./demo.sh

Then drag & drop .svg or .ma (maya ascii)  or .png files on the window - or if you installed Awesomium as described above, you can drop in any file that can be displayed by a web browser.  Switch cameras with the number keys (camera 0 is the free camera). Camera 9 is a 2d camera (cpu rasterizer).