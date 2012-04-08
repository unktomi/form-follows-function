package org.f3.runtime;

/**
 * This class tests dependents maintainence for F3Base instances.
 */
public class DependentsTest extends F3TestCase {
    public void testAddRemove() {
        // create an F3Base with 2 fields.
        F3Base src = new F3Base() {
            @Override
            public int count$() { return 2; }
        };
        // fresh object, no dependents yet
        assertEquals(0, src.getListenerCount$());

        // create two dependent objects
        F3Base dep1 = new F3Base();
        F3Base dep2 = new F3Base();

        // check dependent addition
        src.addDependent$(0, dep1, 0);
        assertEquals(1, src.getListenerCount$());
        src.addDependent$(1, dep1, 1);
        assertEquals(2, src.getListenerCount$());
        src.addDependent$(0, dep2, 0);
        assertEquals(3, src.getListenerCount$());
        src.addDependent$(1, dep2, 1);
        assertEquals(4, src.getListenerCount$());

        // check removals
        src.removeDependent$(0, dep1);
        assertEquals(3, src.getListenerCount$());
        src.removeDependent$(1, dep1);
        assertEquals(2, src.getListenerCount$());
        src.removeDependent$(1, dep2);
        assertEquals(1, src.getListenerCount$());
        src.removeDependent$(0, dep2);
        assertEquals(0, src.getListenerCount$());
    }

    public void testUpdate() {
        // create an object with two variables
        final F3Base src = new F3Base() {
            @Override
            public int count$() { return 2; }
        };

        // check that update$ method is called as expected
        final int[] numTimesDep1Updated = new int[1];
        // create two dependents and register for different 'depNum's.
        final F3Base dep1 = new F3Base() {
            @Override
            public boolean update$(F3Object srcObj, int depNum, int startPos, int endPos, int newLength, int phase) {
                numTimesDep1Updated[0]++;
                assertSame(src, srcObj);
                assertEquals(0, depNum);
                return true;
            }
        };
        src.addDependent$(0, dep1, 0);
        final int[] numTimesDep2Updated = new int[1];
        final F3Base dep2 = new F3Base() {
            @Override
            public boolean update$(F3Object srcObj, int depNum, int startPos, int endPos, int newLength, int phase) {
                numTimesDep2Updated[0]++;
                assertSame(src, srcObj);
                assertEquals(1, depNum);
                return true;
            }
        };
        src.addDependent$(1, dep2, 1);

        // update zeroth var
        src.notifyDependents$(0, F3Object.PHASE_TRANS$CASCADE_INVALIDATE);
        // dep1's update$ should have been called
        // dep2's update$ should not have been called
        assertEquals(1, numTimesDep1Updated[0]);
        assertEquals(0, numTimesDep2Updated[0]);

        // update first var
        src.notifyDependents$(1, F3Object.PHASE_TRANS$CASCADE_INVALIDATE);
        // dep1's update$ should not have been called
        // dep2's update$ should have been called
        assertEquals(1, numTimesDep1Updated[0]);
        assertEquals(1, numTimesDep2Updated[0]);
    }

    public void testAddDuringNotification() {
        // create an object with two variables
        final F3Base src = new F3Base() {
            @Override
            public int count$() { return 2; }
        };

        // check that update$ method is called as expected
        final int[] numTimesDep1Updated = new int[1];
        // create two dependents and register for different 'depNum's.
        final F3Base dep1 = new F3Base() {
            @Override
            public boolean update$(F3Object srcObj, int depNum, int startPos, int endPos, int newLength, int phase) {
                numTimesDep1Updated[0]++;
                assertSame(src, srcObj);
                assertEquals(0, depNum);
                return true;
            }
        };
        final int[] numTimesDep2Updated = new int[1];
        final F3Base dep2 = new F3Base() {
            @Override
            public boolean update$(F3Object srcObj, int depNum, int startPos, int endPos, int newLength, int phase) {
                srcObj.addDependent$(0, dep1, 0);
                numTimesDep2Updated[0]++;
                assertSame(src, srcObj);
                assertEquals(1, depNum);
                return true;
            }
        };

        src.addDependent$(1, dep2, 1);
        src.notifyDependents$(1, F3Object.PHASE_TRANS$CASCADE_INVALIDATE);
        assertEquals(0, numTimesDep1Updated[0]);
        assertEquals(1, numTimesDep2Updated[0]);

        // dep2's update adds dep1 as dependent
        src.notifyDependents$(0, F3Object.PHASE_TRANS$CASCADE_INVALIDATE);
        assertEquals(1, numTimesDep1Updated[0]);
        assertEquals(1, numTimesDep2Updated[0]);
    }

    public void testRemoveDuringNotification() {
        // create an object with two variables
        final F3Base src = new F3Base() {
            @Override
            public int count$() { return 2; }
        };

        // check that update$ method is called as expected
        final int[] numTimesDep1Updated = new int[1];
        // create two dependents and register for different 'depNum's.
        final F3Base dep1 = new F3Base() {
            @Override
            public boolean update$(F3Object srcObj, int depNum, int startPos, int endPos, int newLength, int phase) {
                numTimesDep1Updated[0]++;
                assertSame(src, srcObj);
                assertEquals(0, depNum);
                return true;
            }
        };
        final int[] numTimesDep2Updated = new int[1];
        final F3Base dep2 = new F3Base() {
            @Override
            public boolean update$(F3Object srcObj, int depNum, int startPos, int endPos, int newLength, int phase) {
                srcObj.removeDependent$(0, dep1);
                numTimesDep2Updated[0]++;
                assertSame(src, srcObj);
                assertEquals(1, depNum);
                return true;
            }
        };

        src.addDependent$(0, dep1, 0);
        src.addDependent$(1, dep2, 1);

        src.notifyDependents$(0, F3Object.PHASE_TRANS$CASCADE_INVALIDATE);
        src.notifyDependents$(1, F3Object.PHASE_TRANS$CASCADE_INVALIDATE);
        assertEquals(1, numTimesDep1Updated[0]);
        assertEquals(1, numTimesDep2Updated[0]);

        // dep2's update removed dep1 as dependent
        // so, we should not get dep1.update$ call
        src.notifyDependents$(0, F3Object.PHASE_TRANS$CASCADE_INVALIDATE);
        src.notifyDependents$(1, F3Object.PHASE_TRANS$CASCADE_INVALIDATE);
        assertEquals(1, numTimesDep1Updated[0]);
        assertEquals(2, numTimesDep2Updated[0]);
    }

    public void testRemoveCurrentDuringNotification() {
        // create an object with two variables
        final F3Base src = new F3Base() {
            @Override
            public int count$() { return 2; }
        };

        final int[] numTimesDep1Updated = new int[1];
        // create two dependents and register for different 'depNum's.
        final F3Base dep1 = new F3Base() {
            @Override
            public boolean update$(F3Object srcObj, int depNum, int startPos, int endPos, int newLength, int phase) {
                numTimesDep1Updated[0]++;
                assertSame(src, srcObj);
                assertEquals(0, depNum);
                srcObj.removeDependent$(0, this);
                return true;
            }
        };

        final int[] numTimesDep2Updated = new int[1];
        // create two dependents and register for different 'depNum's.
        final F3Base dep2 = new F3Base() {
            @Override
            public boolean update$(F3Object srcObj, int depNum, int startPos, int endPos, int newLength, int phase) {
                numTimesDep2Updated[0]++;
                assertSame(src, srcObj);
                return true;
            }
        };

        src.addDependent$(0, dep2, 0);
        src.addDependent$(0, dep1, 0);
        src.addDependent$(1, dep2, 1);
        assertEquals(3, src.getListenerCount$());
        src.notifyDependents$(0, F3Object.PHASE_TRANS$CASCADE_INVALIDATE);
        assertEquals(1, numTimesDep1Updated[0]);
        assertEquals(1, numTimesDep2Updated[0]);
        // one dependent removed from notification loop
        assertEquals(2, src.getListenerCount$());
    }

    public void testRemoveAllDuringNotification() {
        // create an object with one variable
        final F3Base src = new F3Base() {
            @Override
            public int count$() { return 1; }
        };

        final int[] deleter = new int[1];
        final F3Base[] dependents = new F3Base[3];
        final F3Base dep0 = new F3Base() {
            @Override
            public boolean update$(F3Object srcObj, int depNum, int startPos, int endPos, int newLength, int phase) {
                if (deleter[0] == 0) {
                   for (F3Base f3 : dependents) {
                       srcObj.removeDependent$(0, f3);
                   }
                }
                return true;
            }
        };
        final F3Base dep1 = new F3Base() {
            @Override
            public boolean update$(F3Object srcObj, int depNum, int startPos, int endPos, int newLength, int phase) {
                if (deleter[0] == 1) {
                   for (F3Base f3 : dependents) {
                       srcObj.removeDependent$(0, f3);
                   }
                }
                return true;
            }
        };
        final F3Base dep2 = new F3Base() {
            @Override
            public boolean update$(F3Object srcObj, int depNum, int startPos, int endPos, int newLength, int phase) {
                if (deleter[0] == 2) {
                   for (F3Base f3 : dependents) {
                       srcObj.removeDependent$(0, f3);
                   }
                }
                return true;
            }
        };
        dependents[0] = dep0;
        dependents[1] = dep1;
        dependents[2] = dep2;

        src.addDependent$(0, dep0, 0);
        src.addDependent$(0, dep1, 0);
        src.addDependent$(0, dep2, 0);
        assertEquals(3, src.getListenerCount$());
        // remove all from the first inserted dependent
        deleter[0] = 0;
        src.notifyDependents$(0, F3Object.PHASE_TRANS$CASCADE_INVALIDATE);
        assertEquals(0, src.getListenerCount$());

        src.addDependent$(0, dep0, 0);
        src.addDependent$(0, dep1, 0);
        src.addDependent$(0, dep2, 0);
        assertEquals(3, src.getListenerCount$());
        // remove all from the second (middle) inserted dependent
        deleter[0] = 1;
        src.notifyDependents$(0, F3Object.PHASE_TRANS$CASCADE_INVALIDATE);
        assertEquals(0, src.getListenerCount$());

        src.addDependent$(0, dep0, 0);
        src.addDependent$(0, dep1, 0);
        src.addDependent$(0, dep2, 0);
        assertEquals(3, src.getListenerCount$());
        // removal all from the last inserted dependent
        deleter[0] = 2;
        src.notifyDependents$(0, F3Object.PHASE_TRANS$CASCADE_INVALIDATE);
        assertEquals(0, src.getListenerCount$());
    }

    public void testSwitchDependence() {
        // create an object with one variable
        final F3Base src1 = new F3Base() {
            @Override
            public int count$() { return 1; }
        };

        // create an object with one variable
        final F3Base src2 = new F3Base() {
            @Override
            public int count$() { return 1; }
        };

        // check that update$ method is called as expected
        final int[] numTimesDepUpdated = new int[1];
        final F3Base dep = new F3Base() {
            @Override
            public boolean update$(F3Object srcObj, int depNum, int startPos, int endPos, int newLength, int phase) {
                numTimesDepUpdated[0]++;
                assertEquals(0, depNum);
                return true;
            }
        };

        // no listeners for both sources
        assertEquals(0, src1.getListenerCount$());
        assertEquals(0, src2.getListenerCount$());

        // add one listener for "src1"
        src1.addDependent$(0, dep, 0);
        assertEquals(1, src1.getListenerCount$());
        src1.notifyDependents$(0, F3Object.PHASE_TRANS$CASCADE_INVALIDATE);
        assertEquals(1, numTimesDepUpdated[0]);

        // switch the dependence of "dep" from "src1" to "src2"
        dep.switchDependence$(src1, 0, src2, 0, 0);
        assertEquals(0, src1.getListenerCount$());
        assertEquals(1, src2.getListenerCount$());

        src2.notifyDependents$(0, F3Object.PHASE_TRANS$CASCADE_INVALIDATE);
        assertEquals(2, numTimesDepUpdated[0]);
    }

    public void testSwitchCurrentDuringNotification() {
        final F3Base src1 = new F3Base() {
            @Override
            public int count$() { return 1; }
        };

        final F3Base src2 = new F3Base() {
            @Override
            public int count$() { return 1; }
        };

        final F3Base dep = new F3Base() {
            @Override
            public boolean update$(F3Object srcObj, int depNum, int startPos, int endPos, int newLength, int phase) {
                // switch dependence of current object
                this.switchDependence$(src1, 0, src2, 0, 0);
                return true;
            }
        };
        src1.addDependent$(0, dep, 0);
        assertEquals(1, src1.getListenerCount$());
        assertEquals(0, src2.getListenerCount$());
        src1.notifyDependents$(0, F3Object.PHASE_TRANS$CASCADE_INVALIDATE);
        assertEquals(0, src1.getListenerCount$());
        assertEquals(1, src2.getListenerCount$());
    }

    public void testSwitchAllDuringNotification() {
        final F3Base src1 = new F3Base() {
            @Override
            public int count$() { return 1; }
        };
        final F3Base src2 = new F3Base() {
            @Override
            public int count$() { return 1; }
        };

        final int[] switcher = new int[1];
        final F3Base[] dependents = new F3Base[3];
        final F3Base dep0 = new F3Base() {
            @Override
            public boolean update$(F3Object srcObj, int depNum, int startPos, int endPos, int newLength, int phase) {
                if (switcher[0] == 0) {
                   for (F3Base f3 : dependents) {
                       f3.switchDependence$(src1, 0, src2, 0, 0);
                   }
                }
                return true;
            }
        };
        final F3Base dep1 = new F3Base() {
            @Override
            public boolean update$(F3Object srcObj, int depNum, int startPos, int endPos, int newLength, int phase) {
                if (switcher[0] == 1) {
                   for (F3Base f3 : dependents) {
                       f3.switchDependence$(src1, 0, src2, 0, 0);
                   }
                }
                return true;
            }
        };
        final F3Base dep2 = new F3Base() {
            @Override
            public boolean update$(F3Object srcObj, int depNum, int startPos, int endPos, int newLength, int phase) {
                if (switcher[0] == 2) {
                   for (F3Base f3 : dependents) {
                       f3.switchDependence$(src1, 0, src2, 0, 0);
                   }
                }
                return true;
            }
        };

        assertEquals(0, src1.getListenerCount$());
        assertEquals(0, src2.getListenerCount$());

        dependents[0] = dep0;
        dependents[1] = dep1;
        dependents[2] = dep2;
        src1.addDependent$(0, dep0, 0);
        src1.addDependent$(0, dep1, 0);
        src1.addDependent$(0, dep2, 0);
        assertEquals(3, src1.getListenerCount$());
        // switch all from the first inserted dependent
        switcher[0] = 0;
        src1.notifyDependents$(0, F3Object.PHASE_TRANS$CASCADE_INVALIDATE);
        assertEquals(0, src1.getListenerCount$());
        assertEquals(3, src2.getListenerCount$());
        for (F3Object d : dependents) {
            src2.removeDependent$(0, d);
        }

        src1.addDependent$(0, dep0, 0);
        src1.addDependent$(0, dep1, 0);
        src1.addDependent$(0, dep2, 0);
        assertEquals(3, src1.getListenerCount$());
        assertEquals(0, src2.getListenerCount$());
        // switch all from the second (middle) inserted dependent
        switcher[0] = 1;
        src1.notifyDependents$(0, F3Object.PHASE_TRANS$CASCADE_INVALIDATE);
        assertEquals(0, src1.getListenerCount$());
        assertEquals(3, src2.getListenerCount$());
        for (F3Object d : dependents) {
            src2.removeDependent$(0, d);
        }

        src1.addDependent$(0, dep0, 0);
        src1.addDependent$(0, dep1, 0);
        src1.addDependent$(0, dep2, 0);
        assertEquals(3, src1.getListenerCount$());
        // switch all from the last inserted dependent
        switcher[0] = 2;
        src1.notifyDependents$(0, F3Object.PHASE_TRANS$CASCADE_INVALIDATE);
        assertEquals(0, src1.getListenerCount$());
        assertEquals(3, src2.getListenerCount$());
    }

    public void testFakeRemove() {
        // create an F3Base with 2 fields.
        F3Base src = new F3Base() {
            @Override
            public int count$() { return 2; }
        };

        F3Base dep = new F3Base();
        src.addDependent$(0, dep, 0);
        src.addDependent$(1, dep, 1);
        assertEquals(2, src.getListenerCount$());

        // remove something that was *not* added as dependent
        F3Object fakeDep = new F3Base();
        src.removeDependent$(0, fakeDep);
        src.removeDependent$(1, fakeDep);
        // try invalid varNum too!
        src.removeDependent$(5, fakeDep);

        // still 2 dependents -- but made sure fake removals are handled ok
        assertEquals(2, src.getListenerCount$());
    }
}

