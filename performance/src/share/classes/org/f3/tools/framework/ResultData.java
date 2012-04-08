/*
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
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

package org.f3.tools.framework;

/**
 *
 * @author ksrini
 */
public class ResultData {
    static final String DEFAULT_VALUE = "0.0";
    private String name = null;
    private String performance = DEFAULT_VALUE;
    private String heapsize = DEFAULT_VALUE;

    public ResultData(String name, String performance, String heapsize) {
        this.name = name;
        this.performance = performance;
        this.heapsize = heapsize;
    }

    public ResultData(String... flds) {
        this.name = flds[0];
        if (isFloatOk(flds[1]))
            this.performance = flds[1];
        if (isFloatOk(flds[2]))
            this.heapsize = flds[2];
    }
    
    public String getName() {
        return name;
    }

    public String getPerformance() {
        float value = Float.parseFloat(performance);
        return String.format("%10.2f", value);
    }
    
    public String getHeapsize() {
        float value = Float.parseFloat(heapsize);
        return String.format("%10.2f", value);
    }

    @Override
    public String toString() {
        return String.format(Utils.CSV_FORMAT_STRING, name, getPerformance(), getHeapsize());
    }

    private boolean isFloatOk(String s) {
        try {
            Float.parseFloat(s);
        } catch (NumberFormatException ignore) {
            return false;
        }
        return true;
    }
}
