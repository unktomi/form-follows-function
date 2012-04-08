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
package com.sun.tools.renamer;
import java.util.regex.Pattern;

/**
 *
 * @author ksrini
 */

public class PatternContainer {
    Pattern spattern;
    String  source;
    String  target;

    PatternContainer(String source, String target) {
        this.source = source;
        this.target = target;
        spattern = Pattern.compile(source);
    }

    Pattern getPattern() {
        return this.spattern;
    }

    String getSource() {
        return this.source;
    }

    String getTarget() {
        return this.target;
    }

    public boolean equals(Object o) {
        if (o instanceof PatternContainer) {
            PatternContainer that = (PatternContainer)o;
            if (this.source.equals(that.source) && this.target.equals(that.target)) {
                return true;
            }
        }
        return false;
    }
}
