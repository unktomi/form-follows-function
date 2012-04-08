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

import static java.util.Calendar.*;
import java.util.Locale;
import java.util.TimeZone;

/*
 * The DateTimeEngine implementation for desktop.
 */
class DateTimeEngineImpl extends DateTimeEngine {
    static final Gregorian gcal = Gregorian.getInstance();

    private BaseCalendar.Date gdate;

    DateTimeEngineImpl() {
        gdate = gcal.newCalendarDate(null);
    }

    DateTimeEngineImpl(long instant, TimeZone tz) {
        gdate = gcal.getCalendarDate(instant, tz);
    }

    public long getInstant() {
        return gcal.getTime(gdate);
    }

    public int getYear() {
        int year = gdate.getYear();
        // Adjust year numbering
        if (year <= 0) {
            year--;
        }
        return year;
    }

    public void setYear(int year) {
        // Adjust year numbering
        if (year < 0) {
            year++;
        }
        gdate.setYear(year);
    }

    public int getMonth() {
        return gdate.getMonth();
    }

    public void setMonth(int month) {
        gdate.setMonth(month);
    }

    public int getDayOfMonth() {
        return gdate.getDayOfMonth();
    }

    public void setDayOfMonth(int dayOfMonth) {
        gdate.setDayOfMonth(dayOfMonth);
    }

    public int getDayOfWeek() {
        return gdate.getDayOfWeek();
    }

    public int getHours() {
        return gdate.getHours();
    }

    public void setHours(int hours) {
        gdate.setHours(hours);
    }

    public int getMinutes() {
        return gdate.getMinutes();
    }

    public void setMinutes(int minutes) {
        gdate.setMinutes(minutes);
    }

    public int getSeconds() {
        return gdate.getSeconds();
    }

    public void setSeconds(int seconds) {
        gdate.setSeconds(seconds);
    }

    public int getMillis() {
        return gdate.getMillis();
    }

    public void setMillis(int millis) {
        gdate.setMillis(millis);
    }

    public void setDate(int year, int month, int dayOfMonth) {
        // Adjust year numbering
        if (year < 0) {
            year++;
        }
        gdate.setDate(year, month, dayOfMonth);
    }

    public void setTimeOfDay(int hours, int minutes, int seconds, int millis) {
        gdate.setTimeOfDay(hours, minutes, seconds, millis);
    }

    public int getZoneOffset() {
        return gdate.getZoneOffset();
    }

    public int getDaylightSaving() {
        return gdate.getDaylightSaving();
    }

    public void setDaylightSaving(int saving) {
        gdate.setDaylightSaving(saving);
    }

    public void setDaylightTime(boolean flag) {
        gdate.setDaylightTime(flag);
    }

    public TimeZone getZone() {
        return gdate.getZone();
    }

    public void setZone(TimeZone tz) {
        gdate.setZone(tz);
    }

    public boolean validate() {
        return gcal.validate(gdate);
    }

    public boolean isNormalized() {
        return gdate.isNormalized();
    }

    public void resetNormalized() {
        gdate.setNormalized(false);
    }

    public String toString() {
        return gdate.toString();
    }
}
