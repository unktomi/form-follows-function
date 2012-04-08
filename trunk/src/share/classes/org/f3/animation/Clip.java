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
 * Interface for abstracting the Clip support used by the F3
 * runtime.
 *
 */
public interface Clip {

   /**
     * Returns whether this Clip object or any of its dependent Clips
     * are currently running
     */
    boolean isRunning();

    /**
     * This method pauses a running animation.
     *
     * @see #resume()
     * @see #isRunning()
     */
    void pause();

    /**
     * This method resumes a paused animation.
     *
     * @see #pause()
     */
    void resume();

    /**
     * Sets the interpolator for the animation cycle.  The default
     * interpolator is {@link InterpolatorFactory#getEasingInstance()}.
     * @param interpolator the interpolation to use each animation cycle
     * @throws IllegalStateException if animation is already running; this
     * parameter may only be changed prior to starting the animation or
     * after the animation has ended
     * @see #isRunning()
     */
    void setInterpolator(Interpolator interpolator);

    /**
     * Starts the animation.
     */
    void start();

    /**
     * Stops the animation.
     */
    void stop();

    /**
     * Sets the resolution of the animation
     */
    void setResolution(int resolution);

    /**
     * Used to specify unending duration or repeatCount
     * @see #setDuration
     * @see #setRepeatCount
     * */
    public static final int INDEFINITE = -1;
}
