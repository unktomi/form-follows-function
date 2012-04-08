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
package org.f3.animation;

/**
 *
 * @author Stephen Chin <steveonjava@gmail.com>
 */
public class DefaultEasingInterpolator implements Interpolator {

    private final float acceleration;
    private final float deceleration;

    DefaultEasingInterpolator(float acceleration, float deceleration) {
        if (acceleration < 0 || acceleration > 1) {
            throw new IllegalArgumentException("Acceleration cannot be less than zero or greater than 1");
        }
        if (deceleration < 0 || deceleration > 1) {
            throw new IllegalArgumentException("Deceleration cannot be less than zero or greater than 1");
        }
        if (deceleration + acceleration > 1) {
            throw new IllegalArgumentException("The sum of acceleration and deceleration cannot be great than 1");
        }
        this.acceleration = acceleration;
        this.deceleration = deceleration;
    }

    @Override
    public float interpolate(float fraction) {
        if (acceleration == 0 && deceleration == 0) {
            return fraction;
        }
        float runRate = 1f / (1 - (acceleration + deceleration) / 2);

        if (fraction < acceleration) {
            float averageRunRate = runRate * (fraction / acceleration) / 2;
            fraction *= averageRunRate;
        } else if (fraction > 1 - deceleration) {
            float tdec = fraction - (1 - deceleration);
            float pdec = tdec / deceleration;
            fraction = runRate * (1 - acceleration / 2 - deceleration + tdec * (2 - pdec) / 2);
        } else {
            fraction = runRate * (fraction - acceleration / 2);
        }

        if (fraction < 0) {
            fraction = 0;
        } else if (fraction > 1) {
            fraction = 1;
        }
        return fraction;
    }
}
