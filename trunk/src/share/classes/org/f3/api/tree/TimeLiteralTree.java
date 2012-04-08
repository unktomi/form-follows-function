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

package org.f3.api.tree;

/**
 * A tree node for a F3 time literal.
 * @author tball
 */
public interface TimeLiteralTree extends Tree {

    public enum Duration {
        
        MILLIS("ms",            1),
        SECONDS("s",         1000),
        MINUTES("m",    60 * 1000),
        HOURS("h", 60 * 60 * 1000);
        
        Duration(String suffix, int multiplier) {
            this.suffix = suffix;
            this.multiplier = multiplier;
        }
        
        public String getSuffix() {
            return suffix;
        }
        
        public int getMultiplier() {
            return multiplier;
        }
        
        private String suffix;
        private int multiplier;
    }
    
    /**
     * @return the numeric value of this tree.
     */
    public LiteralTree getValue();
    
    /**
     * @return the duration specified to declare this time literal.
     */
    Duration getDuration();
}
