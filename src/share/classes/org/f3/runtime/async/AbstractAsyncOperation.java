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

package org.f3.runtime.async;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.f3.runtime.Entry;

/**
 * AbstractAsyncOperation.   Base class for result-bearing, asynchronous operations. Some operations are asynchronous
 * because they would potentially block the EDT for unacceptably long. Since F3 lacks a clear concurrency model,
 * allowing users to execute arbitrary F3 code in background threads would invariably cause problems.  Therefore,
 * we provide a number of Java classes for async operations, which will execute in a background thread, such as
 * "fetch a resource over the web".  Async operations should not access any F3 state except the immutable parameters
 * passed in, and should not have side effects other than those managed by thread-safe Java classes.
 *
 * Async operations are one-time use; subclasses should not attempt to reuse them.  
 *
 * @author Brian Goetz
 */
public abstract class AbstractAsyncOperation<V> implements Callable<V> {

    private final FutureTask<V> future;
    protected final AsyncOperationListener listener;

    private int progressGranularity = 100;
    private int progressMax, lastProgress, progressIncrement, nextProgress, bytesRead;

    protected AbstractAsyncOperation(final AsyncOperationListener<V> listener) {
        this.listener = listener;

        Callable<V> callable = new Callable<V>() {
            public V call() throws Exception {
                return AbstractAsyncOperation.this.call();
            }
        };

        final Runnable completionRunnable = new Runnable() {
            public void run() {
                if (future.isCancelled()) {
                    listener.onCancel();
                }
                else
                    try {
                        listener.onCompletion(future.get());
                    }
                    catch (InterruptedException e) {
                        listener.onCancel();
                    }
                    catch (ExecutionException e) {
                        listener.onException(e);
                    }
            }
        };

        future = new FutureTask<V>(callable) {
            @Override
            protected void done() {
                try {
                    Entry.deferAction(completionRunnable);
                }
                finally {
                    super.done();
                }
            }
        };
    }

    public boolean isCancelled() {
        return future.isCancelled();
    }

    public boolean isDone() {
        return future.isDone();
    }

    public void cancel() {
        future.cancel(true);
    }

    public void start() {
        BackgroundExecutor.getExecutor().execute(future);
    }

    protected void notifyProgress() {
        final int last = lastProgress;
        final int max = progressMax;
        Entry.deferAction(new Runnable() {
            public void run() {
                listener.onProgress(last, max);
            }
        });
    }

    protected void addProgress(int amount) {
        bytesRead += amount;
        if (bytesRead > nextProgress) {
            lastProgress = bytesRead;
            notifyProgress();
            nextProgress = ((lastProgress / progressIncrement) + 1) * progressIncrement;
        }
    }

    protected int getProgressMax() {
        return progressMax;
    }

    protected void setProgressMax(int progressMax) {
        if (progressMax == 0) {
            progressIncrement = progressGranularity;
        }
        else if (progressMax == -1) {
            progressIncrement = progressGranularity;
        }
        else {
            this.progressMax = progressMax;
            progressIncrement = progressMax / progressGranularity;
            if(progressIncrement < 1) {
                progressIncrement = 1;
            }
        }
        nextProgress = ((lastProgress / progressIncrement) + 1) * progressIncrement;
        notifyProgress();
    }

    protected int getProgressGranularity() {
        return progressGranularity;
    }

    protected void setProgressGranularity(int progressGranularity) {
        this.progressGranularity = progressGranularity;
        progressIncrement = progressMax / progressGranularity;
        nextProgress = ((lastProgress / progressIncrement) + 1) * progressIncrement;
        notifyProgress();
    }

}
