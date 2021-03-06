#!/bin/sh

#
# Copyright 2005 Sun Microsystems, Inc.  All Rights Reserved.
# DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
#
# This code is free software; you can redistribute it and/or modify it
# under the terms of the GNU General Public License version 2 only, as
# published by the Free Software Foundation.
#
# This code is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
# FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
# version 2 for more details (a copy is included in the LICENSE file that
# accompanied this code).
#
# You should have received a copy of the GNU General Public License version
# 2 along with this work; if not, write to the Free Software Foundation,
# Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
#
# Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
# CA 95054 USA or visit www.sun.com if you need additional information or
# have any questions.
#


# @test
# @bug 4461214 6227587
# @summary support-version and -fullversion
# @run shell versionOpt.sh

if [ "${TESTJAVA}" = "" ]
then
  echo "TESTJAVA not set.  Test cannot execute.  Failed."
  exit 1
fi
echo "TESTJAVA=${TESTJAVA}"

# set platform-dependent variables
OS=`uname -s`
case "$OS" in
  SunOS | Linux )
    NULL=/dev/null
    PS=":"
    FS="/"
    ;;
  Windows* )
    NULL=NUL
    PS=";"
    FS="\\"
    ;;
  * )
    echo "Unrecognized system!"
    exit 1;
    ;;
esac

# create reference files based on java values
"${TESTJAVA}${FS}bin${FS}java" ${TESTVMOPTS} -version 2>&1 | \
    sed -e 's/java version "\([^"]*\)"/javac \1/' -e '2,$d' > version.ref.out

"${TESTJAVA}${FS}bin${FS}java" ${TESTVMOPTS} -fullversion 2>&1 | \
    sed -e 's/java full version/javac full version/' -e '2,$d' > fullversion.ref.out

# run javac
"${TESTJAVA}${FS}bin${FS}javac" ${TESTTOOLVMOPTS} -version 2> version.out
cat version.out
diff -c version.ref.out version.out
version_result=$?

"${TESTJAVA}${FS}bin${FS}javac" ${TESTTOOLVMOPTS} -fullversion 2> fullversion.out
cat fullversion.out
diff -c fullversion.ref.out fullversion.out
fullversion_result=$?

if [ $version_result -eq 0 -a $fullversion_result -eq 0 ]
then
  echo "Passed"
  exit 0
else
  echo "Failed"
  exit 1
fi




