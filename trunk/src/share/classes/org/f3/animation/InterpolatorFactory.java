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
 * Factory interface for creating instances of built-in Interpolator
 * implementations.
 * 
 */
public interface InterpolatorFactory {

    /**
     * Returns a trivial implementation of Interpolator that provides discrete
     * time interpolation (the return value of {@code interpolate()} is 1f
     * only when the input "t" is 1f, and 0f otherwise).
     *
     * @return an instance of Interpolator that provides simple discrete
     * time interpolation
     */
    Interpolator getDiscreteInstance();

    /**
     * Returns a Interpolator instance that provides ease in/out behavior, using
     * default values of 0.2f and 0.2f for the acceleration and deceleration
     * factors, respectively.  Calling this method is equivalent to calling
     * {@code getEasingInstance(0.2f, 0.2f)}.
     *
     * @return an instance of Interpolator that provides acceleration and
     * deceleration behavior
     */
    Interpolator getEasingInstance();

    /**
     * Returns a Interpolator instance that provides ease in/out behavior according
     * to the given acceleration and deceleration values.
     *
     * @param acceleration value in the range [0,1] indicating the fraction
     * of time spent accelerating at the beginning of a timing cycle
     * @param deceleration value in the range [0,1] indicating the fraction
     * of time spent decelerating at the end of a timing cycle
     * @return an instance of Interpolator that provides acceleration and
     * deceleration behavior
     * @throws IllegalArgumentException if either the acceleration or
     * deceleration value is outside the range [0,1]
     * @throws IllegalArgumentException if the acceleration value is
     * greater than (1 - deceleration)
     * @throws IllegalArgumentException if the deceleration value is
     * greater than (1 - acceleration)
     */
    Interpolator getEasingInstance(float acceleration, float deceleration);

    /**
     * Returns a trivial implementation of Interpolator that provides linear time
     * interpolation (the input "t" value is simply returned unmodified).
     *
     * @return an instance of Interpolator that provides simple linear
     * time interpolation
     */
    Interpolator getLinearInstance();

    /**
     * Returns a Interpolator instance that is shaped using the
     * spline control points defined by (x1, y1) and (x2, y2).  The anchor
     * points of the spline are implicitly defined as (0, 0) and (1, 1).
     *
     * @param x1 the X value of the first control point
     * @param y1 the Y value of the first control point
     * @param x2 the X value of the second control point
     * @param y2 the Y value of the second control point
     * @return an instance of Interpolator that is shaped by the given spline curve
     * @throws IllegalArgumentException if any of the control points
     * is outside the range [0,1]
     */
    Interpolator getSplineInstance(float x1, float y1, float x2, float y2);
}
