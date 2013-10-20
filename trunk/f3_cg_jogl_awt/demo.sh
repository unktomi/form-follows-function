#!/bin/sh
ln -s -f /Library/Frameworks/Awesomium.framework .
export LIB=./lib
export DYLD_LIBRARY_PATH=$LIB/macosxuniversal
#JAVA=/Library/Java/JavaVirtualMachines/jdk1.7.0_17.jdk/Contents/Home/bin/java
JAVA=java
$JAVA -Xcheck:jni -Df3.jogl.stage.enable.2d.renderer=false -Df3.jogl.stage.enable.textured.text=true -Df3.jogl.stage.debug.meshes=true -Dapple.awt.graphics.UseQuartz=true -Df3.debug.anim=false -d32 -Xmx1G -Xbootclasspath/p:lib/jsr166.jar -classpath ${LIB}/f3rt.jar:${LIB}/jogl-all.jar:${LIB}/gluegen-rt.jar:${LIB}/jogl-cg.jar:${LIB}/f3.svg.awt.jar:${LIB}/js.jar:${LIB}/f3.awesomium.jogl.jar:${LIB}/org.f3.media.video.corevideo.jar:build/f3.cg.jogl.awt.jar org.f3.runtime.Main f3.jogl.awt.JoglStage $*