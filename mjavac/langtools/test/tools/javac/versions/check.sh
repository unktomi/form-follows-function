#
# Copyright 2004-2006 Sun Microsystems, Inc.  All Rights Reserved.
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
# @bug 4981566 5028634 5094412 6304984
# @summary Check interpretation of -target and -source options
#
# @build CheckClassFileVersion
# @run shell check.sh

TESTJAVA=${TESTJAVA:?}
TC=${TESTCLASSES-.}

J="$TESTJAVA/bin/java" 
JC="$TESTJAVA/bin/javac" 
CFV="$J ${TESTVMOPTS} -cp $TC CheckClassFileVersion"

rm -f $TC/X.java $TC/X.java
echo 'public class X { }' > $TC/X.java
echo 'public enum Y { }' > $TC/Y.java


# Check class-file versions

check() {
  V=$1; shift
  echo "+ javac $* [$V]"
  $JC ${TESTTOOLVMOPTS} -d $TC $* $TC/X.java && $CFV $TC/X.class $V || exit 2
}

check 50.0
check 48.0 -source 1.4
check 49.0 -source 1.4 -target 1.5
check 49.0 -target 1.5
check 50.0 -source 1.5
check 50.0 -target 1.6
check 50.0 -target 6
check 50.0 -source 1.6
check 50.0 -source 6
check 51.0 -target 1.7
check 51.0 -target 7
check 51.0 -source 1.7
check 51.0 -source 7


# Check source versions

fail() {
  echo "+ javac $*"
  if $JC ${TESTTOOLVMOPTS} -d $TC $*; then
    echo "-- did not fail as expected"
    exit 3
  else
    echo "-- failed as expected"
  fi
}

pass() {
  echo "+ javac $*"
  if $JC ${TESTTOOLVMOPTS} -d $TC $*; then
    echo "-- passed"
  else
    echo "-- failed"
    exit 4
  fi
}

checksrc14() { pass $* $TC/X.java; fail $* $TC/Y.java; }
checksrc15() { pass $* $TC/X.java; pass $* $TC/Y.java; }
checksrc16() { checksrc15 $* ; }

checksrc14 -source 1.4
checksrc14 -source 1.4 -target 1.5

checksrc15
checksrc15 -target 1.5
checksrc15 -source 1.5

checksrc16 -target 1.6
checksrc16 -target 6
checksrc16 -source 1.6
checksrc16 -source 6

fail -source 1.5 -target 1.4 $TC/X.java
fail -source 1.6 -target 1.4 $TC/X.java
fail -source 6   -target 1.4 $TC/X.java
fail -source 1.6 -target 1.5 $TC/X.java
fail -source 6   -target 1.5 $TC/X.java
