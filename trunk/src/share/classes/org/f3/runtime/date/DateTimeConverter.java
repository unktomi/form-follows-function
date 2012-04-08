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

public class DateTimeConverter {
    private static final String DELIMITERS = "--T::";

    private static int FIELD_YEAR = 0;
    private static int FIELD_MONTH = 1;
    private static int FIELD_DAY_OF_MONTH = 2;
    private static int FIELD_HOUR = 3;
    private static int FIELD_MINUTE = 4;
    private static int FIELD_SECOND = 5;

    public static DateTimeEngine parseXMLDateTime(String input) {
        int index = 0;
        int[] value = { 0 };
        int inputlen = input.length();
        int len = DELIMITERS.length();
        int[] fields = new int[len + 1];

        // parse the yyyy-MM-ddTHH:mm:ss portion
        for (int i = 0; i <= len; i++) {
            int x = parseInt(input, index, value);
            int digits = x - index;
            int val = value[0];
            if (i == FIELD_YEAR) {
                if (val == 0) {
                    // "0000" is not allowed as a year value.
                    syntaxError(index);
                }
                if (val < 0) {
                    digits--;
                    index++;
                }
                if (digits < 4 || (digits > 4 && input.charAt(index) == '0')) {
                    // year must have 4 or more digits. No leading 0s
                    // are allowed if year has more than 4 digits.
                    syntaxError(index);
                }
            } else if (digits != 2 || val < 0) {
                syntaxError(index);
            }
            index = x;
            if (i != len && index < inputlen
                && input.charAt(index++) != DELIMITERS.charAt(i)) {
               syntaxError(index - 1);
            }
            fields[i] = val;
        }

        // Handle optional fractional seconds [millisecond]
        int ms = 0;
        if (index < inputlen) {
            char c = input.charAt(index);
            if (c == '.') {
                int start = index++;
                // Accept any trailing "0"s here for SimpleDateFormat
                // which doesn't support fractional seconds, but
                // milliseconds with ".SSS" which may have trailing
                // "0"s.
                while (index < inputlen &&
                       (c = input.charAt(index)) >= '0' && c <= '9')
                    index++;
                try {
                    float fraction = Float.parseFloat(input.substring(start, index));
                    ms = (int)(fraction * 1000); // round-down
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(e.toString());
                }
            }
        }

        // Check the special time-of-day case "24:00:00"
        boolean handle24hour = false;
        if (fields[FIELD_HOUR] == 24) {
            if (fields[FIELD_MINUTE] != 0 || fields[FIELD_SECOND] != 0 || ms != 0) {
                throw new IllegalArgumentException("invalid hour of day value");
            }
            // Need to handle 24:00:00 after validation.
            handle24hour = true;
            fields[FIELD_HOUR] = 0;
        }

        DateTimeEngine engine = DateTimeEngine.getInstance();

        // parse the optional time zone part.
        if (index < inputlen) {
            char c = input.charAt(index++);
            if (c == 'Z') {
                engine.setZone(TimeZone.getTimeZone("UTC"));
            } else if (c == '-' || c == '+') {
                int start = index - 1;
                int x = parseInt(input, index, value);
                if (x - index != 2) {
                    syntaxError(index);
                }
                int val = value[0] * 60;
                index = x;
                if (input.charAt(index++) != ':') {
                    syntaxError(index - 1);
                }
                x = parseInt(input, index, value);
                if (x - index != 2 || value[0] > 59) {
                    syntaxError(index);
                }
                index = x;
                val += value[0];
                if (val > 14*60) {
                    syntaxError(index);
                }
                if (val == 0) {
                    engine.setZone(TimeZone.getTimeZone("UTC"));
                } else {
                    TimeZone tz = TimeZone.getTimeZone("GMT"
                                      + input.substring(start, start + 6));
                    // Got "GMT"?
                    if (tz.getRawOffset() == 0) {
                        // TODO: subclass java.util.TimeZone in case
                        // no custom time zone support is available in
                        // the Java runtime
                        throw new InternalError("No custom time zone support");
                    }
                    engine.setZone(tz);
                }
            }
        }
        // Check if we've consumed the whole string.
        if (index != inputlen) {
            syntaxError(index);
        }

        // Create a calender calculation engine
        engine.setDate(fields[FIELD_YEAR], fields[FIELD_MONTH],
                       fields[FIELD_DAY_OF_MONTH]);
        engine.setTimeOfDay(fields[FIELD_HOUR], fields[FIELD_MINUTE],
                            fields[FIELD_SECOND], ms);
        if (!engine.validate()) {
            throw new IllegalArgumentException("invalid date-time");
        }
        if (handle24hour) {
            engine.setHours(24);
        }
        engine.resetNormalized();
        return engine;
    }

    static final String[] DAY_ABBRS = {
        "Sun",
        "Mon",
        "Tue",
        "Wed",
        "Thu",
        "Fri",
        "Sat"
    };

    static final String[] MONTH_ABBRS = {
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "May",
        "Jun",
        "Jul",
        "Aug",
        "Sep",
        "Oct",
        "Nov",
        "Dec"
    };

    private static final int NAME_STD = 0;
    private static final int NAME_DST = 1;
    private static final String[][] RFC822ZONE_NAMES = {
        { "EST", "EDT" },
        { "CST", "CDT" },
        { "MST", "MDT" },
        { "PST", "PDT" },
        { "GMT", null },
        { "UT",  null },
    };

    private static final int OFFSET_STD = 0;
    private static final int OFFSET_DST = 1;
    private static final int[][] RFC822ZONE_OFFSETS = {
        { -5 * 60 * 60 * 1000, -4 * 60 * 60 * 1000 },
        { -6 * 60 * 60 * 1000, -5 * 60 * 60 * 1000 },
        { -7 * 60 * 60 * 1000, -6 * 60 * 60 * 1000 },
        { -8 * 60 * 60 * 1000, -7 * 60 * 60 * 1000 },
        { 0, 0 },
        { 0, 0 },
    };

    private static final int ZONE_ID = 0;
    private static final int ZONE_ALTSTD = 1;
    private static final int ZONE_ALTDST = 2;
    private static final String[][] RFC822ZONE_IDS = {
        { "America/New_York",    "GMT-05:00",       "GMT-04:00" },
        { "America/Chicago",     "GMT-06:00",       "GMT-05:00" },
        { "America/Denver",      "America/Phoenix", "GMT-06:00" },
        { "America/Los_Angeles", "GMT-08:00",       "GMT-07:00" },
        { "GMT",                 null,              null },
        { "GMT",                 null,              null },
    };

    public static String toRFC822String(DateTimeEngine engine) {
        engine.getInstant(); // normalize
        StringBuilder sb = new StringBuilder();
        sb.append(DAY_ABBRS[engine.getDayOfWeek() - 1]).append(", ");
        CalendarUtils.sprintf0d(sb, engine.getDayOfMonth(), 2).append(' ');
        sb.append(MONTH_ABBRS[engine.getMonth() - 1]).append(' ');
        CalendarUtils.sprintf0d(sb, engine.getYear(), 4).append(' ');
        CalendarUtils.sprintf0d(sb, engine.getHours(), 2).append(':');
        CalendarUtils.sprintf0d(sb, engine.getMinutes(), 2).append(':');
        CalendarUtils.sprintf0d(sb, engine.getSeconds(), 2);
        TimeZone tz = engine.getZone();
        if (tz != null) {
            sb.append(' ');
            try {
                // If both the standard abbreviation and the raw GMT
                // offset match an RFC822 zone, then use its RFC822
                // zone name. Otherwise, use the numeric format (e.g.,
                // +0900). If the TimeZone is "Europe/London", its
                // standard abbreviation matches "GMT", but its
                // daylight time needs to be converted to +0100. This
                // fallback is performed in appendRFC822ZoneName().
                String tzabbr = tz.getDisplayName(false, TimeZone.SHORT, Locale.US);
                int rawOffset = tz.getRawOffset();
                if (isRFC822ZoneName(tzabbr, rawOffset)) {
                    appendRFC822ZoneName(sb, engine.getZoneOffset(),
                                         engine.getDaylightSaving() > 0);
                } else {
                    appendRFC822ZoneNumeric(sb, engine.getZoneOffset());
                }
            } catch (LinkageError e) {
                appendRFC822ZoneNumeric(sb, engine.getZoneOffset());
            }
        }
        return sb.toString();
    }

    public static DateTimeEngine parseRFC822DateTime(String input) {
        try {
            DateTimeEngine engine = DateTimeEngine.getInstance();
            int index = 0;
            int dayOfWeek = 0;
            if (input.charAt(3) == ',') {
                for (int i = 0; i < DAY_ABBRS.length; i++) {
                    if (input.startsWith(DAY_ABBRS[i])) {
                        dayOfWeek = i + 1;
                        break;
                    }
                }
                if (dayOfWeek == 0) {
                    parseError("invalid day name");
                }
                checkDelimiter(input, 4, ' ');
                index = 5;
            }

            int[] value = { 0 };

            // parse day of month
            int x = parseInt(input, index, value);
            if (x - index > 2) {
                syntaxError(index);
            }
            engine.setDayOfMonth(value[0]);
            checkDelimiter(input, x++, ' ');
            index = x;

            // parse month abbreviation
            for (int i = 0; i < MONTH_ABBRS.length; i++) {
                if (input.startsWith(MONTH_ABBRS[i], index)) {
                    engine.setMonth(i + 1);
                    index += 3;
                    break;
                }
            }
            if (index == x) {
                parseError("invalid month name");
            }
            checkDelimiter(input, index++, ' ');

            // parse year
            x = parseInt(input, index, value);
            int year = value[0];
            if (year >= 0 && x - index == 2) {
                // handle 2-digit year
                DateTimeEngine e = DateTimeEngine.getInstance(System.currentTimeMillis(),
                                                              TimeZone.getDefault());
                int defaultCenturyStart = e.getYear() - 80;
                year += (defaultCenturyStart / 100) * 100;
                if (year < defaultCenturyStart) {
                    year += 100;
                }
            }
            if (year == 0) {
                parseError("invalid year value");
            }
            if (year < 0) {
                year++;
            }
            engine.setYear(year);
            index = x;
            checkDelimiter(input, index++, ' ');

            // parse time of day (HH:mm[:ss])
            int hours = 0;
          timeofday:
            for (int i = 0; i < 3; i++) {
                x = parseInt(input, index, value);
                if (x - index != 2) {
                    syntaxError(index);
                }
                int val = value[0];
                boolean foundColon = input.charAt(x) == ':';
                switch (i) {
                case 0:
                    if (!foundColon) {
                        syntaxError(x);
                    }
                    engine.setHours(val);
                    // save hours to determine if DST amount needs to
                    // be adjusted in case the given time zone is kind
                    // of invalid.
                    hours = val;
                    x++;
                    break;
                case 1:
                    engine.setMinutes(val);
                    if (!foundColon) {
                        // No seconds field
                        index = x;
                        break timeofday;
                    }
                    x++;
                    break;
                case 2:
                    engine.setSeconds(val);
                    break;
                }
                index = x;
            }
            checkDelimiter(input, index++, ' ');

            // parse time zone
            boolean[] isDST = { false };
            String tzid = parseRFC822Zone(input, index, isDST);
            if (tzid == null) {
                throw new IllegalArgumentException("invalid time zone");
            }
            TimeZone tz = TimeZone.getTimeZone(tzid);
            engine.setZone(tz);

            if (!engine.validate()) {
                parseError("invalid date-time");
            }

            boolean expectDST = isDST[0];
            engine.setDaylightTime(expectDST);

            long instant = engine.getInstant(); // normalize

            // In case the calculation result disagrees with the
            // specified time zone, it's due to either invalid local
            // time or corresponding non-DST time zone. (No support
            // for the historical differences in the U.S. time zones.)
            if ((engine.getDaylightSaving() > 0) != expectDST) {
                String altTzid = null;
                for (String[] ids : RFC822ZONE_IDS) {
                    if (tzid.equals(ids[ZONE_ID])) {
                        altTzid = ids[expectDST ? ZONE_ALTDST : ZONE_ALTSTD];
                    }
                }
                if (altTzid == null) {
                    parseError("invalid local time");
                }
                // Recalculate local time
                tz = TimeZone.getTimeZone(altTzid);
                if (hours == engine.getHours()) {
                    instant += engine.getDaylightSaving();
                }
                engine = DateTimeEngine.getInstance(instant, tz);
            }
            if (dayOfWeek != 0 && engine.getDayOfWeek() != dayOfWeek) {
                parseError("incorrect day of week");
            }
            engine.resetNormalized();
            return engine;
        } catch (IndexOutOfBoundsException ie) {
        }
        throw new IllegalArgumentException();
    }

    /**
     * Determines if the given time zone abbreviation is "GMT" or one
     * of the U.S. time zones defined by RFC822 zones.
     * <code>abbr</code> and <code>offset</code> must agree.
     *
     * @param abbr time zone abbreviation such as "PST"
     * @param offset time zone offset in milliseconds
     */
    private static boolean isRFC822ZoneName(String abbr, int offset) {
        for (int i = 0; i < RFC822ZONE_OFFSETS.length; i++) {
            if (offset == RFC822ZONE_OFFSETS[i][OFFSET_STD]
                && abbr.equals(RFC822ZONE_NAMES[i][NAME_STD])) {
                return true;
            }
        }
        return false;
    }

    private static void appendRFC822ZoneName(StringBuilder sb,
                                             int offset, boolean daylight) {
        int index = daylight ? OFFSET_DST : OFFSET_STD;
        for (int i = 0; i < RFC822ZONE_OFFSETS.length; i++) {
            if (offset == RFC822ZONE_OFFSETS[i][index]) {
                sb.append(RFC822ZONE_NAMES[i][index]);
                return;
            }
        }
        appendRFC822ZoneNumeric(sb, offset);
    }

    private static void appendRFC822ZoneNumeric(StringBuilder sb, int offset) {
        char sign = '+';
        if (offset < 0) {
            offset = -offset;
            sign = '-';
        }
        offset /= 60000;
        sb.append(sign);
        CalendarUtils.sprintf0d(sb, offset/60, 2);
        CalendarUtils.sprintf0d(sb, offset%60, 2);
    }

    private static String parseRFC822Zone(String input, int index, boolean[] isDST) {
        String tzid = null;
        char c = input.charAt(index);
        if (c  == '+' || c == '-') {
            int[] val = { 0 };
            int x = parseInt(input, ++index, val);
            int value = val[0];
            if (x - index != 4 || x != input.length() || (value % 100) >= 60) {
                throw new IllegalArgumentException();
            }
            if (value != 0) {
                StringBuilder sb = new StringBuilder();
                sb.append("GMT").append(c);
                CalendarUtils.sprintf0d(sb, value/100, 2).append(':');
                CalendarUtils.sprintf0d(sb, value%100, 2);
                tzid = sb.toString();
            } else {
                tzid = "GMT";
            }
        } else {
            String name = input.substring(index);
            if (name.length() == 1) {
                int offset = 0;
                char sign;
                if (c >= 'A' && c < 'J') {
                    offset = c - 'A' + 1;
                    sign = '-';
                } else if (c >= 'K' && c <= 'M') {
                    offset = c - 'A';
                    sign = '-';
                } else if (c >= 'N' && c <= 'Y') {
                    offset = c - 'N' + 1;
                    sign = '+';
                } else if (c == 'Z') {
                    sign = '+';
                } else {
                    throw new IllegalArgumentException();
                }
                StringBuilder sb = new StringBuilder();
                sb.append("GMT").append(sign);
                CalendarUtils.sprintf0d(sb, offset, 2).append(":00");
                tzid = sb.toString();
            } else {
                int len = RFC822ZONE_NAMES.length;
                for (int i = 0; i < RFC822ZONE_NAMES.length; i++) {
                    if (name.equals(RFC822ZONE_NAMES[i][NAME_STD])) {
                        tzid = RFC822ZONE_IDS[i][ZONE_ID];
                        break;
                    }
                    if (name.equals(RFC822ZONE_NAMES[i][NAME_DST])) {
                        tzid = RFC822ZONE_IDS[i][ZONE_ID];
                        isDST[0] = true;
                        break;
                    }
                }
            }
        }
        return tzid;
    }

    private static int parseInt(String input, int index, int[] value) {
        int length = input.length();
        int sign = 1;
        if (index < length && input.charAt(index) == '-') {
            sign = -1;
            index++;
        }
        int val = 0;
        char c;
        while (index < length &&
               (c = input.charAt(index)) >= '0' && c <= '9') {
            val = val * 10 + (c - '0');
            index++;
        }
        value[0] = val * sign;
        return index;
    }

    private static void checkDelimiter(String input, int index, char delimiter) {
        if (input.charAt(index) != delimiter) {
            syntaxError(index);
        }
    }

    private static void syntaxError(int index) {
        throw new IllegalArgumentException("syntax error at " + index);
    }

    private static void parseError(String msg) {
        throw new IllegalArgumentException(msg);
    }
}
