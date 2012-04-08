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

package org.f3.runtime.date;

import java.util.TimeZone;

public abstract class DateTimeEngine {

    public static DateTimeEngine getInstance() {
        return new DateTimeEngineImpl();
    }

    public static DateTimeEngine getInstance(long instant, TimeZone tz) {
        return new DateTimeEngineImpl(instant, tz);
    }

    public abstract long getInstant();

    public abstract int getYear();

    public abstract void setYear(int year);

    public abstract int getMonth();

    public abstract void setMonth(int month);

    public abstract int getDayOfMonth();

    public abstract void setDayOfMonth(int dayOfMonth);

    public abstract int getDayOfWeek();

    public abstract int getHours();

    public abstract void setHours(int hours);

    public abstract int getMinutes();

    public abstract void setMinutes(int minutes);

    public abstract int getSeconds();

    public abstract void setSeconds(int seconds);

    public abstract int getMillis();

    public abstract void setMillis(int millis);

    public abstract void setDate(int year, int month, int dayOfMonth);

    public abstract void setTimeOfDay(int hours, int minutes, int seconds, int millis);

    public abstract int getZoneOffset();

    public abstract int getDaylightSaving();

    public void setDaylightSaving(int saving) {
    }

    public void setDaylightTime(boolean flag) {
    }

    public abstract TimeZone getZone();

    public abstract void setZone(TimeZone tz);

    public abstract boolean validate();

    public abstract boolean isNormalized();

    public abstract void resetNormalized();
}
