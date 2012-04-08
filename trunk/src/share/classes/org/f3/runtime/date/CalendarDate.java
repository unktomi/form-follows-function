/*
 * Copyright 2000-2009 Sun Microsystems, Inc.  All Rights Reserved.
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

import java.lang.Cloneable;
import java.util.TimeZone;

/**
 * The <code>CalendarDate</code> class represents a specific instant
 * in time by calendar date and time fields that are multiple cycles
 * in different time unites. The semantics of each calendar field is
 * given by a concrete calendar system rather than this
 * <code>CalendarDate</code> class that holds calendar field values
 * without interpreting them. Therefore, this class can be used to
 * represent an amount of time, such as 2 years and 3 months.
 *
 * <p>A <code>CalendarDate</code> instance can be created by calling
 * the <code>newCalendarDate</code> or <code>getCalendarDate</code>
 * methods in <code>CalendarSystem</code>. A
 * <code>CalendarSystem</code> instance is obtained by calling one of
 * the factory methods in <code>CalendarSystem</code>. Manipulations
 * of calendar dates must be handled by the calendar system by which
 * <code>CalendarDate</code> instances have been created.
 *
 * <p>Some calendar fields can be modified through method calls. Any
 * modification of a calendar field brings the state of a
 * <code>CalendarDate</code> to <I>not normalized</I>. The
 * normalization must be performed to make all the calendar fields
 * consistent with a calendar system.
 *
 * <p>The <code>protected</code> methods are intended to be used for
 * implementing a concrete calendar system, not for general use as an
 * API.
 *
 * @see CalendarSystem
 * @author Masayoshi Okutsu
 */

/*
 * Derived from JDK7 sun.util.calendar.
 */
abstract class CalendarDate implements Cloneable {
    public static final int FIELD_UNDEFINED = Integer.MIN_VALUE;
    public static final long TIME_UNDEFINED = Long.MIN_VALUE;

    private int year;
    private int month;
    private int dayOfMonth;
    private int dayOfWeek = FIELD_UNDEFINED;
    private boolean leapYear;

    private int hours;
    private int minutes;
    private int seconds;
    private int millis;         // fractional part of the second
    private long fraction;      // time of day value in millisecond

    private boolean normalized;

    private TimeZone zoneinfo;
    private int zoneOffset;
    private int daylightSaving;
    private boolean forceDaylightTime;

    protected CalendarDate() {
        this(TimeZone.getDefault());
    }

    protected CalendarDate(TimeZone zone) {
        zoneinfo = zone;
    }

    public int getYear() {
        return year;
    }

    public CalendarDate setYear(int year) {
        if (this.year != year) {
            this.year = year;
            normalized = false;
        }
        return this;
    }

    public CalendarDate addYear(int n) {
        if (n != 0) {
            year += n;
            normalized = false;
        }
        return this;
    }

    /**
     * Returns whether the year represented by this
     * <code>CalendarDate</code> is a leap year. If leap years are
     * not applicable to the calendar system, this method always
     * returns <code>false</code>.
     *
     * <p>If this <code>CalendarDate</code> hasn't been normalized,
     * <code>false</code> is returned. The normalization must be
     * performed to retrieve the correct leap year information.
     *
     * @return <code>true</code> if this <code>CalendarDate</code> is
     * normalized and the year of this <code>CalendarDate</code> is a
     * leap year, or <code>false</code> otherwise.
     * @see BaseCalendar#isGregorianLeapYear
     */
    public boolean isLeapYear() {
        return leapYear;
    }

    void setLeapYear(boolean leapYear) {
        this.leapYear = leapYear;
    }

    public int getMonth() {
        return month;
    }

    public CalendarDate setMonth(int month) {
        if (this.month != month) {
            this.month = month;
            normalized = false;
        }
        return this;
    }

    public CalendarDate addMonth(int n) {
        if (n != 0) {
            month += n;
            normalized = false;
        }
        return this;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public CalendarDate setDayOfMonth(int date) {
        if (dayOfMonth != date) {
            dayOfMonth = date;
            normalized = false;
        }
        return this;
    }

    public CalendarDate addDayOfMonth(int n) {
        if (n != 0) {
            dayOfMonth += n;
            normalized = false;
        }
        return this;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public int getHours() {
        return hours;
    }

    public CalendarDate setHours(int hours) {
        if (this.hours != hours) {
            this.hours = hours;
            normalized = false;
        }
        return this;
    }

    public CalendarDate addHours(int n) {
        if (n != 0) {
            hours += n;
            normalized = false;
        }
        return this;
    }

    public int getMinutes() {
        return minutes;
    }

    public CalendarDate setMinutes(int minutes) {
        if (this.minutes != minutes) {
            this.minutes = minutes;
            normalized = false;
        }
        return this;
    }

    public CalendarDate addMinutes(int n) {
        if (n != 0) {
            minutes += n;
            normalized = false;
        }
        return this;
    }

    public int getSeconds() {
        return seconds;
    }

    public CalendarDate setSeconds(int seconds) {
        if (this.seconds != seconds) {
            this.seconds = seconds;
            normalized = false;
        }
        return this;
    }

    public CalendarDate addSeconds(int n) {
        if (n != 0) {
            seconds += n;
            normalized = false;
        }
        return this;
    }

    public int getMillis() {
        return millis;
    }

    public CalendarDate setMillis(int millis) {
        if (this.millis != millis) {
            this.millis = millis;
            normalized = false;
        }
        return this;
    }

    public CalendarDate addMillis(int n) {
        if (n != 0) {
            millis += n;
            normalized = false;
        }
        return this;
    }

    public long getTimeOfDay() {
        if (!isNormalized()) {
            return fraction = TIME_UNDEFINED;
        }
        return fraction;
    }

    public CalendarDate setDate(int year, int month, int dayOfMonth) {
        setYear(year);
        setMonth(month);
        setDayOfMonth(dayOfMonth);
        return this;
    }

    public CalendarDate addDate(int year, int month, int dayOfMonth) {
        addYear(year);
        addMonth(month);
        addDayOfMonth(dayOfMonth);
        return this;
    }

    public CalendarDate setTimeOfDay(int hours, int minutes, int seconds, int millis) {
        setHours(hours);
        setMinutes(minutes);
        setSeconds(seconds);
        setMillis(millis);
        return this;
    }

    public CalendarDate addTimeOfDay(int hours, int minutes, int seconds, int millis) {
        addHours(hours);
        addMinutes(minutes);
        addSeconds(seconds);
        addMillis(millis);
        return this;
    }

    protected void setTimeOfDay(long fraction) {
        this.fraction = fraction;
    }

    public boolean isNormalized() {
        return normalized;
    }


    public boolean forceDaylightTime() {
        return forceDaylightTime;
    }

    public void setDaylightTime(boolean daylightTime) {
        forceDaylightTime = daylightTime;
    }

    public boolean isDaylightTime() {
        return daylightSaving != 0;
    }

    public TimeZone getZone() {
        return zoneinfo;
    }

    public CalendarDate setZone(TimeZone zoneinfo) {
        this.zoneinfo = zoneinfo;
        return this;
    }

    /**
     * Returns whether the specified date is the same date of this
     * <code>CalendarDate</code>. The time of the day fields are
     * ignored for the comparison.
     */
    public boolean isSameDate(CalendarDate date) {
        return getDayOfWeek() == date.getDayOfWeek()
            && getMonth() == date.getMonth()
            && getYear() == date.getYear();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof CalendarDate)) {
            return false;
        }
        CalendarDate that = (CalendarDate) obj;
        if (isNormalized() != that.isNormalized()) {
            return false;
        }
        boolean hasZone = zoneinfo != null;
        boolean thatHasZone = that.zoneinfo != null;
        if (hasZone != thatHasZone) {
            return false;
        }
        if (hasZone && !zoneinfo.equals(that.zoneinfo)) {
            return false;
        }
        return (year == that.year
                && month == that.month
                && dayOfMonth == that.dayOfMonth
                && hours == that.hours
                && minutes == that.minutes
                && seconds == that.seconds
                && millis == that.millis
                && zoneOffset == that.zoneOffset);
    }

    public int hashCode() {
        // a pseudo (local standard) time stamp value in milliseconds
        // from the Epoch, assuming Gregorian calendar fields.
        long hash = ((((((long)year - 1970) * 12) + (month - 1)) * 30) + dayOfMonth) * 24;
        hash = ((((((hash + hours) * 60) + minutes) * 60) + seconds) * 1000) + millis;
        hash -= zoneOffset;
        int normalized = isNormalized() ? 1 : 0;
        int zone = zoneinfo != null ? zoneinfo.hashCode() : 0;
        return (int) hash * (int)(hash >> 32) ^ normalized ^ zone;
    }

    /**
     * Returns a copy of this <code>CalendarDate</code>. The
     * <code>TimeZone</code> object, if any, is not cloned.
     *
     * @return a copy of this <code>CalendarDate</code>
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen
            throw new InternalError();
        }
    }

    /**
     * Converts calendar date values to a <code>String</code> in the
     * following format.
     * <pre>
     *     yyyy-MM-dd'T'HH:mm:ss.SSSz
     * </pre>
     *
     * @see java.text.SimpleDateFormat
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (year > 0) {
            CalendarUtils.sprintf0d(sb, year, 4).append('-');
        } else {
            CalendarUtils.sprintf0d(sb, year - 1, 5).append('-');
        }
        CalendarUtils.sprintf0d(sb, month, 2).append('-');
        CalendarUtils.sprintf0d(sb, dayOfMonth, 2).append('T');
        CalendarUtils.sprintf0d(sb, hours, 2).append(':');
        CalendarUtils.sprintf0d(sb, minutes, 2).append(':');
        CalendarUtils.sprintf0d(sb, seconds, 2);
        // Convert millis, if not 0, to fractional seconds.
        // eg, 6 -> ".006", 120 -> ".12", 300 -> ".3"
        if (millis != 0) {
            sb.append('.');
            if ((millis % 10) == 0) {
                if ((millis % 100) == 0) {
                    sb.append(millis/100);
                } else {
                    CalendarUtils.sprintf0d(sb, millis/10, 2);
                }
            } else {
                CalendarUtils.sprintf0d(sb, millis, 3);
            }
        }
        if (zoneinfo != null) {
            if (zoneOffset == 0) {
                sb.append('Z');
            } else if (zoneOffset != FIELD_UNDEFINED) {
                int offset;
                char sign;
                if (zoneOffset > 0) {
                    offset = zoneOffset;
                    sign = '+';
                } else {
                    offset = -zoneOffset;
                    sign = '-';
                }
                offset /= 60000;
                sb.append(sign);
                CalendarUtils.sprintf0d(sb, offset / 60, 2).append(':');
                CalendarUtils.sprintf0d(sb, offset % 60, 2);
            }
        }
        return sb.toString();
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setNormalized(boolean normalized) {
        this.normalized = normalized;
    }

    public int getZoneOffset() {
        return zoneOffset;
    }

    public void setZoneOffset(int offset) {
        zoneOffset = offset;
    }

    public int getDaylightSaving() {
        return daylightSaving;
    }

    public void setDaylightSaving(int daylightSaving) {
        this.daylightSaving = daylightSaving;
    }
}
