#!/bin/sh
export LIB=./lib
export DYLD_LIBRARY_PATH=$LIB/macosxuniversal
java  -d32 -Xmx512M -classpath ${LIB}/f3rt.jar:${LIB}/jogl.all.jar:${LIB}/gluegen-rt.jar:${LIB}/jogl.cg.jar:${LIB}/f3.svg.awt.jar:build/f3.cg.jogl.awt.jar org.f3.runtime.Main f3.jogl.awt.JoglStage $*