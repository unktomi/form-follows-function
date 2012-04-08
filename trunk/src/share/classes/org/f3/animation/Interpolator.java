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
 * Interface that defines the single {@link #interpolate(float)} method,
 * which is used to control the timing of an animation.  Various built-in
 * implementations of this interface are offered by the {@link Interpolators}
 * class.  Applications may choose to implement their own Interpolator to
 * get custom interpolation behavior.
 *
 * @see Interpolators
 */
public interface Interpolator {

    /**
     * This function takes an input value between 0 and 1 and returns
     * another value, also between 0 and 1. The purpose of the function
     * is to define how time (represented as a (0-1) fraction of the
     * duration of an animation) is altered to derive different value
     * calculations during an animation.
     *
     * @param fraction a value between 0 and 1, representing the elapsed
     * fraction of a time interval (either an entire animation cycle or an
     * interval between two {@code KeyFrame}s, depending on where this
     * Interpolator is used)
     * @return a value between 0 and 1.  Values outside of this boundary may
     * be clamped to the interval [0,1] and cause undefined results.
     */
    public float interpolate(float fraction);
}
