/*
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package org.f3.runtime.sequence;
import org.f3.runtime.F3Base;
import org.f3.runtime.F3Object;

/**
 *
 * @param <T> Result element type
 * @param <PT> Induction type
 */
public abstract class BoundFor<T, PT> extends F3Base {

    /** 
     * The bfElem class in the design document implements this interface.
     */
    public static interface F3ForPart<PT> extends F3Object {
        /**
         * Get the indexof variable
         */
        public int getIndex$();

        /**
         * Adjust the indexof variable
         * May cause re-calculation.
         */
        public void adjustIndex$(int value$);

        /**
         * Set the induction variable
         * May cause re-calculation.
         */
        public void setInductionVar$(PT value$);
    };

    protected static final int BOUND_FOR_STATE_UNINITIALIZED = 0;
    protected static final int BOUND_FOR_STATE_PARTS_STABLE  = 1;
    protected static final int BOUND_FOR_STATE_PARTS_INVALID = 2;
    protected static final int BOUND_FOR_STATE_PARTS_UPDATED = 3;

    protected byte state = BOUND_FOR_STATE_UNINITIALIZED;
    protected final boolean dependsOnIndex;
    protected boolean inWholesaleUpdate = true; // ignore initial individual updates

    protected final F3Object container;
    protected final int forVarNum;
    protected final int inductionSeqVarNum;

    public int partResultVarNum; // This gets magically assigned when a part is created

    protected F3ForPart<PT>[] parts;
    protected int numParts;
    protected int lowestInvalidPart;            // lowest part index that is invalid
    protected int highestInvalidPart = -1;      // highest part index that is invalid, negative means none
    protected int pendingTriggers = 0;          // number of invalidations seen minus number of triggers seen
    protected int sizeAtLastTrigger = 0;        // size the previous time we did trigger phase invalidate

    protected static final boolean DEBUG = false;

    public BoundFor(F3Object container, int forVarNum, int inductionSeqVarNum, boolean dependsOnIndex) {
        this.container = container;
        this.forVarNum = forVarNum;
        this.inductionSeqVarNum = inductionSeqVarNum;
        this.dependsOnIndex = dependsOnIndex;
    }

    // Required public interface

    public abstract F3ForPart makeForPart$(int index);

    /**
     * Called by invalidate when the result of a part changes.
     */
    @Override
    public boolean update$(F3Object src, final int depNum, int startPos, int endPos, int newLength, final int phase) {
        if (state == BOUND_FOR_STATE_UNINITIALIZED || inWholesaleUpdate)
            return true;
        int ipart = ((F3ForPart) src).getIndex$();
        if ((phase & PHASE_TRANS$PHASE) == PHASE$INVALIDATE) {
            if (DEBUG) System.err.println("inv update$ id: " + forVarNum + ", ipart: " + ipart + ", " + lowestInvalidPart + " ... " + highestInvalidPart);
            if (highestInvalidPart < 0) {
                // No outstanding invalid region, mark this as the beginning and end of region
                // and send a blanket invalidation
                highestInvalidPart = lowestInvalidPart = ipart;
                pendingTriggers = 1;
                if (state == BOUND_FOR_STATE_PARTS_STABLE) {
                    blanketInvalidationOfBoundFor();
                }
            } else {
                // Already have invalid parts, encompass ours
                ++pendingTriggers;
                if (ipart < lowestInvalidPart) {
                    lowestInvalidPart = ipart;
                }
                if (ipart > highestInvalidPart) {
                    highestInvalidPart = ipart;
                }
            }
            return true;
        }
        if (highestInvalidPart < 0) {
            if (DEBUG) System.err.println("*trig spurious trailing id: " + forVarNum + ", ipart: " + ipart);
            return true;
        }
        --pendingTriggers;
        if (DEBUG) System.err.println("+trig update$ id: " + forVarNum + ", ipart: " + ipart + ", " + lowestInvalidPart + " ... " + highestInvalidPart);

        if (pendingTriggers <= 0) {
            // All the part triggers have come in
            assert pendingTriggers == 0;
            if (DEBUG) System.err.println(".trig update$ id: " + forVarNum + ", ipart: " + ipart + ", " + lowestInvalidPart + " ... " + highestInvalidPart);

            if (state == BOUND_FOR_STATE_PARTS_INVALID) {
                // Replace parts will handle
                return true;
            } else if (state == BOUND_FOR_STATE_PARTS_UPDATED) {
                triggerAll();
                return true;
            }
            return trigger(ipart, startPos, endPos, newLength);
        }
        return true;
    }

    /**
     * Do invalidation for all currently invalid parts
     * Sequence version can override to optimize
     */
    protected boolean trigger(int ignoreIpart, int ignoreStartPos, int ignoreEndPos, int ignoreNewLength) {
        int oldStartPos = cachedCumLength(lowestInvalidPart);
        int oldEndPos = cachedCumLength(highestInvalidPart + 1);

        restoreValidState(lowestInvalidPart, highestInvalidPart + 1);
        decacheLengths();
        int newEndPos = cumLength(highestInvalidPart + 1);
        fireTrigger(oldStartPos, oldEndPos, newEndPos);
        return true;
    }

    // Trying to change parts and do individual update at the same time -- invalidate everything
    protected final void triggerAll() {
        assert pendingTriggers == 0;
        int previousSize = decacheLengths();
        restoreValidState(0, numParts);
        if (DEBUG) System.err.println("!trig all id: " + forVarNum + ", previousSize: " + previousSize + ", sizeAtLastTrigger: " + sizeAtLastTrigger);
        fireTrigger(0, previousSize, sizeAtLastTrigger);
    }

    /**
     * Set-up for exit
     * Fire the trigger
     */
    protected final void fireTrigger(int invStartPos, int invEndPos, int newEndPos) {
        if (DEBUG) System.err.println("-fireTrigger id: " + forVarNum + ", invStartPos: " + invStartPos + ", invEndPos: " + invEndPos + ", len: " + (newEndPos - invStartPos));

        state = BOUND_FOR_STATE_PARTS_STABLE;
        highestInvalidPart = -1;
        inWholesaleUpdate = false;
        pendingTriggers = 0;  // just in case

        container.invalidate$(forVarNum,
                invStartPos,
                invEndPos,
                newEndPos - invStartPos,
                F3Object.PHASE_TRANS$CASCADE_TRIGGER);

    }

    /**]
     * Called by invalidate when the input sequence changes.
     */
    public void replaceParts(int startPart, int endPart, int insertedParts, int phase) {
        if (state == BOUND_FOR_STATE_UNINITIALIZED)
            return;
        boolean outstandingInvalidations = highestInvalidPart >= 0;
        if ((phase & PHASE_TRANS$PHASE) == PHASE$INVALIDATE) {
            if (DEBUG) System.err.println("inv replaceParts id: " + forVarNum + ", state: " + state);
            if (state == BOUND_FOR_STATE_PARTS_STABLE && !outstandingInvalidations) {
                blanketInvalidationOfBoundFor();
            }
            state = BOUND_FOR_STATE_PARTS_INVALID;
            return;
        }
        if (DEBUG) System.err.println("+trig replaceParts id: " + forVarNum + ", startPart: " + startPart + ", endPart: " + endPart + ", insertedParts: " + insertedParts);

        if (startPart < 0) {
            // This is a no-change trigger
            if (outstandingInvalidations) {
                // We collected part updates during this no-change invalidation, proceed using them
                startPart = lowestInvalidPart;
                endPart = highestInvalidPart;
                insertedParts = highestInvalidPart - lowestInvalidPart;
            } else {
                // Pass on this no-change trigger
                container.invalidate$(forVarNum, SequencesBase.UNDEFINED_MARKER_INT, SequencesBase.UNDEFINED_MARKER_INT, 0, F3Object.PHASE_TRANS$CASCADE_INVALIDATE);
                return;
            }
        }

        int newEndPart = startPart + insertedParts;
        int deltaParts = newEndPart - endPart;
        int newNumParts = numParts + deltaParts;

        int oldStartPos;
        int oldEndPos;
        int trailingLength;

        // Don't generate individual updates
        inWholesaleUpdate = true;

        if (parts == null) {
            assert startPart == 0;
            assert endPart == 0;
            oldStartPos = 0;
            oldEndPos = 0;
            trailingLength = 0;

            // Allocate the new elements
            F3ForPart<PT>[] newParts = (F3ForPart<PT>[]) new F3ForPart[newNumParts];

            // Install new parts
            parts = newParts;
            numParts = newNumParts;

            // Fill in the new parts
            buildParts(0, insertedParts);
        } else {
            // Remember old positions (for invalidate)
            oldStartPos = cachedCumLength(startPart);
            oldEndPos = cachedCumLength(endPart);
            trailingLength = numParts - endPart;

            if (DEBUG) System.err.println(".trig replaceParts id: " + forVarNum + ", parts.len: " + parts.length + ", start: " + startPart + ", end: " + endPart +
                        ", newNumParts: " + newNumParts + ", s+i: " + (startPart + insertedParts) + ", trail: " + trailingLength);

            int endPartCopy = (newEndPart < endPart)? newEndPart : endPart;

            // In-place modification.  Update reused induction vars (if any) Invalidation from parts.
            for (int ips = startPart; ips < endPartCopy; ++ips) {
                syncInductionVar(ips);
            }

            if (newNumParts != numParts) {
                for (int ips = endPartCopy; ips < endPart; ++ips) {
                    removeDependent$(parts[ips], partResultVarNum, this);
                }

                // Allocate the new elements
                F3ForPart<PT>[] newParts = (F3ForPart<PT>[]) new F3ForPart[newNumParts];

                // Copy the existing parts
                System.arraycopy(parts, 0, newParts, 0, endPartCopy);
                System.arraycopy(parts, endPart, newParts, newEndPart, trailingLength);

                // Install new parts
                parts = newParts;
                numParts = newNumParts;

                // Fill in the new parts (if any)
                buildParts(endPartCopy, newEndPart);
            }
        }

        // Update the trailing indices -- need indices for internal bookkeeping, always update
        assert startPart + insertedParts + trailingLength == numParts;
        for (int ips = newEndPart; ips < numParts; ++ips) {
            getPart(ips).adjustIndex$(deltaParts);
        }

        state = BOUND_FOR_STATE_PARTS_UPDATED;
        inWholesaleUpdate = false;

        if (pendingTriggers == 0) {
            assert pendingTriggers == 0;

            if (outstandingInvalidations) {
                // Trying to change parts and do individual update at the same time -- invalidate everything
                triggerAll();
            } else {
                int previousSize = decacheLengths();
                if (dependsOnIndex) {
                    // We depend on indices, everything after the start point is invalid
                    restoreValidState(startPart, numParts);
                    fireTrigger(oldStartPos, previousSize, sizeAtLastTrigger);
                } else {
                    // Calculate the inserted length (in the new parts)
                    restoreValidState(startPart, newEndPart);
                    fireTrigger(oldStartPos, oldEndPos, cumLength(newEndPart));
                }
            }
         }
    }

    protected abstract int decacheLengths();

    void showStates(String label) {
        for (int ips = 0; ips < numParts; ++ips) {
            System.err.print(getPart(ips).getFlags$(partResultVarNum) & VFLGS$STATE_MASK);
        }
        System.err.println(" - " + label);
    }

    // Shared implementation interface (optional)

    protected abstract int cumLength(int ipart);

    protected abstract int cachedCumLength(int ipart);

    protected abstract void restoreValidState(int lowPart, int highPart);

    protected void initializeIfNeeded() {
        if (state == BOUND_FOR_STATE_UNINITIALIZED) {
            // Init the induction sequence
            int sz = container.size$(inductionSeqVarNum);

            state = BOUND_FOR_STATE_PARTS_STABLE;
            sizeAtLastTrigger = 0;
            replaceParts(0, 0, sz, F3Object.PHASE_TRANS$CASCADE_INVALIDATE);
            replaceParts(0, 0, sz, F3Object.PHASE_TRANS$CASCADE_TRIGGER);
        }
    }

    protected F3ForPart<PT> getPart(int ipart) {
        return parts[ipart];
    }

    protected final void blanketInvalidationOfBoundFor() {
        container.invalidate$(forVarNum, 0, SequencesBase.UNDEFINED_MARKER_INT, SequencesBase.UNDEFINED_MARKER_INT, F3Object.PHASE_TRANS$CASCADE_INVALIDATE);
    }

    protected void syncInductionVar(int ipart) {
        F3ForPart part = getPart(ipart);
        part.setInductionVar$(container.elem$(inductionSeqVarNum, ipart));
    }

    protected void buildParts(int ipFrom, int ipTo) {
        for (int ips = ipFrom; ips < ipTo; ++ips) {
            F3ForPart part = makeForPart$(ips);
            parts[ips] = part;
            syncInductionVar(ips);
            addDependent$(part, partResultVarNum, this, 0);
        }
    }
}

