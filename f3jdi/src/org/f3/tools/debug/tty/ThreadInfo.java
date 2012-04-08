/*
 * Copyright 1998-2008 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
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

package org.f3.tools.debug.tty;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.StackFrame;
import java.util.List;

class ThreadInfo {
    private final ThreadReference thread;
    private int currentFrameIndex = 0;

    ThreadInfo(ThreadReference thread) {
        this.thread = thread;
    }

    /**
     * Get the thread from this ThreadInfo object.
     *
     * @return the Thread wrapped by this ThreadInfo.
     */
    ThreadReference getThread() {
        return thread;
    }

    /**
     * Get the thread stack frames.
     *
     * @return a <code>List</code> of the stack frames.
     */
    List<StackFrame> getStack() throws IncompatibleThreadStateException {
        return thread.frames();
    }

    /**
     * Get the current stackframe.
     *
     * @return the current stackframe.
     */
    StackFrame getCurrentFrame() throws IncompatibleThreadStateException {
        if (thread.frameCount() == 0) {
            return null;
        }
        return thread.frame(currentFrameIndex);
    }

    /**
     * Invalidate the current stackframe index.
     */
    void invalidate() {
        currentFrameIndex = 0;
    }

    /* Throw IncompatibleThreadStateException if not suspended */
    private void assureSuspended() throws IncompatibleThreadStateException {
        if (!thread.isSuspended()) {
            throw new IncompatibleThreadStateException();
        }
    }

    /**
     * Get the current stackframe index.
     *
     * @return the number of the current stackframe.  Frame zero is the
     * closest to the current program counter
     */
    int getCurrentFrameIndex() {
        return currentFrameIndex;
    }

    /**
     * Set the current stackframe to a specific frame.
     *
     * @param nFrame    the number of the desired stackframe.  Frame zero is the
     * closest to the current program counter
     * @exception IllegalAccessError when the thread isn't
     * suspended or waiting at a breakpoint
     * @exception ArrayIndexOutOfBoundsException when the
     * requested frame is beyond the stack boundary
     */
    void setCurrentFrameIndex(int nFrame) throws IncompatibleThreadStateException {
        assureSuspended();
        if ((nFrame < 0) || (nFrame >= thread.frameCount())) {
            throw new ArrayIndexOutOfBoundsException();
        }
        currentFrameIndex = nFrame;
    }

    /**
     * Change the current stackframe to be one or more frames higher
     * (as in, away from the current program counter).
     *
     * @param nFrames   the number of stackframes
     * @exception IllegalAccessError when the thread isn't
     * suspended or waiting at a breakpoint
     * @exception ArrayIndexOutOfBoundsException when the
     * requested frame is beyond the stack boundary
     */
    void up(int nFrames) throws IncompatibleThreadStateException {
        setCurrentFrameIndex(currentFrameIndex + nFrames);
    }

    /**
     * Change the current stackframe to be one or more frames lower
     * (as in, toward the current program counter).     *
     * @param nFrames   the number of stackframes
     * @exception IllegalAccessError when the thread isn't
     * suspended or waiting at a breakpoint
     * @exception ArrayIndexOutOfBoundsException when the
     * requested frame is beyond the stack boundary
     */
    void down(int nFrames) throws IncompatibleThreadStateException {
        setCurrentFrameIndex(currentFrameIndex - nFrames);
    }

}
