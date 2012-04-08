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

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Stephen Chin <steveonjava@gmail.com>
 */
public class SharedTimer {
    
    private Timer timer;
    private Set<TimerTask> tasks = new HashSet<TimerTask>();
    
    public static SharedTimer getInstance() {
        return SharedTimerHolder.INSTANCE;
    }

    public void schedule(TimerTask task, int start, int resolution) {
        synchronized (tasks) {
            if (timer == null) {
                timer = new Timer();
            }
            tasks.add(task);
            timer.schedule(task, start, resolution);
        }
    }
    
    public void cancel(TimerTask task) {
        synchronized (tasks) {
            task.cancel();
            tasks.remove(task);
            if (tasks.isEmpty()) {
                timer.cancel();
                timer = null;
            }
        }
    }
    
    public boolean hasActiveTasks() {
        synchronized (tasks) {
            return !tasks.isEmpty();
        }
    }
    
    private static class SharedTimerHolder {

        private static final SharedTimer INSTANCE = new SharedTimer();
    }
}
