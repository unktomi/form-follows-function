/*
 * Copyright (c) 2010-2011, F3 Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name F3 nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package f3.lang;

/**
 * Length units are meant for expressing screen layouts.
 * This includes both physical and relative measurements.
 * <p>
 * Physical measurements are expressed in terms of the size of the
 * final rendered version.  They are appropriate where the screen
 * characteristics are well known, but suffer from scaling issues where
 * the distance to the display varies greatly (such as cell phone vs. TV).
 * Supported measures include:
 * <ul>
 * <li>Inches (mm)
 * <li>Centimeters (cm)
 * <li>Millimeters (mm)
 * <li>Points (pt)
 * <li>Picas (pc)
 * </ul>
 * <p>
 * Relative measurements are expressed in terms of other scaling factors such
 * as the font size, container size, or pixel density of the screen.
 * Supported measures include:
 * <ul>
 * <li>Ems (em)
 * <li>Pixels (px)
 * <li>Density-independent pixels (dp)
 * <li>Scale-independent pixels (sp)
 * <li>Percentage (%)
 * </ul>
 *
 * @author Stephen Chin <steveonjava@gmail.com>
 */
public enum LengthUnit {
    /**
     * 1in is 1 physical inch
     */
    INCH("in"),
    /**
     * 1cm is 1/2.54 of an inch
     */
    CENTIMETER("cm"),
    /**
     * 1mm is 1/10th of a centimeter
     */
    MILLIMETER("mm"),
    /**
     * 1pt is equal to 1/72 of an inch
     */
    POINT("pt"),
    /**
     * 1pc is equal to 12 points
     */
    PICA("pc"),
    /**
     * Length measure relative to text height.  Historically the height
     * of the "M" ligature in the given font, this usually refers to the reference
     * font height.
     */
    EM("em"),
    /**
     * An exact pixel on the given display device.  This will always be
     * precisely 1 device dependent pixel based on the resolution of the target device.
     * This measure is useful where exact pixel reproduction is required or the
     * device characteristics are known in advance.
     */
    PIXEL("px"),
    /**
     * Reference pixel for the target device.
     * Will scale to a whole or even fractional pixel value based on the device
     * viewing distance and density.  This is approximately one pixel on a device
     * with a density of 96dpi at arm's length.
     */
    DENSITY_INDEPENDENT_PIXEL("dp"),
    /**
     * A scale-independent pixel that is relative to the user chosen scaling
     * factor.  For the default scale 1sp equals 1dp, with fractional multiples up
     * or down based on the scaling factor.  This is most often used to update the
     * font sizes based on user scaling, but can also be used for layout and
     * to scale graphics.  On platforms that do not support scaling, 1sp will
     * always equal 1dp (and likely 1px).
     */
    SCALE_INDEPENDENT_PIXEL("sp"),
    /**
     * A length measure relative to the container.  100% would be the full
     * length of the container and values between 100 and 0 would be fractionally
     * smaller.  If there is no valid reference for the container, this will be
     * treated as a length of 0.
     */
    PERCENTAGE("%");

    private String suffix;

    LengthUnit(String suffix) {
        this.suffix = suffix;
    }

    /**
     * Returns the suffix used to express this length unit.
     * @return the unit suffix
     */
    public String getSuffix() {
        return suffix;
    }
}
