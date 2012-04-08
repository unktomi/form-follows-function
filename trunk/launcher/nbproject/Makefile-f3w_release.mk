#
# Generated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add customized code.
#
# This makefile implements configuration specific macros and targets.


# Environment
MKDIR=mkdir
CP=cp
GREP=grep
NM=nm
CCADMIN=CCadmin
RANLIB=ranlib
CC=gcc
CCC=g++-3
CXX=g++-3
FC=gfortran
AS=as

# Macros
CND_PLATFORM=Cygwin_4.x-Windows
CND_CONF=f3w_release
CND_DISTDIR=dist
CND_BUILDDIR=build

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=${CND_BUILDDIR}/${CND_CONF}/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/src/f3doc.o \
	${OBJECTDIR}/src/f3.o \
	${OBJECTDIR}/src/f3c.o \
	${OBJECTDIR}/src/f3w.o \
	${OBJECTDIR}/src/util.o \
	${OBJECTDIR}/src/configuration.o


# C Compiler Flags
CFLAGS=

# CC Compiler Flags
CCFLAGS=-mwindows -mno-cygwin -s
CXXFLAGS=-mwindows -mno-cygwin -s

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk dist/Release/f3w.exe

dist/Release/f3w.exe: ${OBJECTFILES}
	${MKDIR} -p dist/Release
	${LINK.cc} -o dist/Release/f3w ${OBJECTFILES} ${LDLIBSOPTIONS} 

${OBJECTDIR}/src/f3doc.o: src/f3doc.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} $@.d
	$(COMPILE.cc) -O2 -DPROJECT_F3W -MMD -MP -MF $@.d -o ${OBJECTDIR}/src/f3doc.o src/f3doc.cpp

${OBJECTDIR}/src/f3.o: src/f3.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} $@.d
	$(COMPILE.cc) -O2 -DPROJECT_F3W -MMD -MP -MF $@.d -o ${OBJECTDIR}/src/f3.o src/f3.cpp

${OBJECTDIR}/src/f3c.o: src/f3c.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} $@.d
	$(COMPILE.cc) -O2 -DPROJECT_F3W -MMD -MP -MF $@.d -o ${OBJECTDIR}/src/f3c.o src/f3c.cpp

${OBJECTDIR}/src/f3w.o: src/f3w.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} $@.d
	$(COMPILE.cc) -O2 -DPROJECT_F3W -MMD -MP -MF $@.d -o ${OBJECTDIR}/src/f3w.o src/f3w.cpp

${OBJECTDIR}/src/util.o: src/util.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} $@.d
	$(COMPILE.cc) -O2 -DPROJECT_F3W -MMD -MP -MF $@.d -o ${OBJECTDIR}/src/util.o src/util.cpp

${OBJECTDIR}/src/configuration.o: src/configuration.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} $@.d
	$(COMPILE.cc) -O2 -DPROJECT_F3W -MMD -MP -MF $@.d -o ${OBJECTDIR}/src/configuration.o src/configuration.cpp

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}
	${RM} dist/Release/f3w.exe

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
