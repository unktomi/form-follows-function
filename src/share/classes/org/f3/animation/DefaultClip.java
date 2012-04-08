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

import java.util.TimerTask;

/**
 * Implementation is not thread safe.
 * 
 * @author Stephen Chin <steveonjava@gmail.com>
 */
class DefaultClip implements Clip {
    private final long duration;
    private final TimingTarget target;
    private Interpolator interpolator;
    private int resolution = 1000 / 60;
    private boolean running;
    private boolean paused;
    private TimerTask task;
    private long startTime;
    private long pauseTime;
    private long pauseDuration;

    DefaultClip(long duration, TimingTarget target) {
        this.duration = duration;
        this.target = target;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void pause() {
        if (!running || paused) return;
        try {
            target.pause();
        } finally {
            pauseTime = System.currentTimeMillis();
            paused = true;
        }
    }

    @Override
    public void resume() {
        if (!running || !paused) return;
        try {
            target.resume();
        } finally {
            pauseDuration += System.currentTimeMillis() - pauseTime;
            paused = false;
        }
    }

    @Override
    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    @Override
    public void start() {
        if (running) return;
        try {
            target.begin();
        } finally {
            running = true;
            startTime = System.currentTimeMillis();
            pauseDuration = 0;
            paused = false;
            task = new TimerTask() {
                @Override
                public void run() {
                    // run this on the "main" thread
                    final Runnable runner = new Runnable() {
                            public void run() {
                                if (!paused) {
                                    long elapsed = System.currentTimeMillis() - startTime - pauseDuration;
                                    try {
                                        target.timingEvent(duration == 0 ? 1 : ((float) elapsed) / duration, elapsed);
                                    } catch (Throwable t) {
                                        t.printStackTrace(); // insulate the timer thread from exceptions
                                    }
                                    try {
                                        if (duration != INDEFINITE && elapsed > duration) {
                                            stop();
                                        }
                                    } catch (Throwable t) {
                                        t.printStackTrace(); // separate try/catch block to allow unstable animations to stop
                                    }
                                }
                            }
                        };
                    org.f3.runtime.Entry.deferAction(runner);
                }
            };
            SharedTimer.getInstance().schedule(task, 0, resolution);
        }
    }

    @Override
    public void stop() {
        if (!running) return;
        try {
            target.end();
        } finally {
            SharedTimer.getInstance().cancel(task);
            running = false;
            task = null;
        }
    }

    @Override
    public void setResolution(int resolution) {
        if (this.resolution != resolution) {
            this.resolution = resolution;
            if (running) {
                SharedTimer.getInstance().cancel(task);
                SharedTimer.getInstance().schedule(task, 0, resolution);
            }
        }
    }
    
}
