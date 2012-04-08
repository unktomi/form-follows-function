/*
 * Copyright 2008-2009 Sun Microsystems, Inc.  All Rights Reserved.
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

package org.f3.animation;

/**
 * This interface provides the methods which
 * are called by Clip during the course of a timing
 * sequence.  Applications
 * that wish to receive timing events will either create a subclass
 * of TimingTargetAdapter and override or they can create or use
 * an implementation of TimingTarget. A TimingTarget can be passed
 * into the constructor of Clip or set later with the
 * {@link Clip#addTarget(TimingTarget)}
 * method.  Any Clip may have multiple TimingTargets.
 */
public interface TimingTarget {
    /**
     * This method will receive all of the timing events from an Clip
     * during an animation.  The fraction is the percent elapsed (0 to 1)
     * of the current animation cycle.
     * @param fraction the fraction of completion between the start and
     * end of the current cycle.  Note that for reversing animations
     * ({@link Clip.Direction#REVERSE}) the fraction progresses in the
     * opposite direction (e.g. from 1 to 0).  Note also that animations
     * with a duration of {@link Clip#INDEFINITE INDEFINITE} will call
     * timingEvent with an undefined value for fraction, since there is
     * no fraction that makes sense if the animation has no defined length.
     * @param totalElapsed the amount of time that has elapsed relative
     * to the start time of a Clip, in milliseconds.
     */
    public void timingEvent(float fraction, long totalElapsed);

    /**
     * Called when the Clip's animation begins.  This provides a chance
     * for targets to perform any setup required at animation start time.
     */
    public void begin();

    /**
     * Called when the Clip's animation ends.
     */
    public void end();

    /**
     * Called when the Clip's animation is paused.
     */
    public void pause();

    /**
     * Called when the Clip's animation resumes from a paused state.
     */
    public void resume();
}
