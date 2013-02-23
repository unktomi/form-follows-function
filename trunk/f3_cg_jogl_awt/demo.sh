#!/bin/sh
ln -s -f /Library/Frameworks/Awesomium.framework .
export LIB=./lib
export DYLD_LIBRARY_PATH=$LIB/macosxuniversal
java  -Df3.debug.anim=false -d32 -Xmx512M -classpath ${LIB}/f3rt.jar:${LIB}/jogl.all.jar:${LIB}/gluegen-rt.jar:${LIB}/jogl.cg.jar:${LIB}/f3.svg.awt.jar:${LIB}/f3.awesomium.jogl.jar:build/f3.cg.jogl.awt.jar org.f3.runtime.Main f3.jogl.awt.JoglStage $*