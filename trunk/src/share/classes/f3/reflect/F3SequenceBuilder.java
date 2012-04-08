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

package f3.reflect;

/** Helper class for reflectively building sequences.
 *
 * <blockquote><pre>
 * F3Context rcontext = ...;
 * RefType elementType = rcontext.getAnyType();  // Or whatever
 * F3SequenceBuilder builder = rcontext.makeSequenceBuilder(elementType);
 * builder.append(...);
 * builder.append(...);
 * F3Value seq = builder.getSequence();
 * </pre></blockquote>
 *
 * @author Per Bothner
 * @profile desktop
 */

public class F3SequenceBuilder {
    F3Value[] values;
    int nvalues;
    F3Value sequence;
    F3Type elementType;
    F3Context context;

    protected F3SequenceBuilder(F3Context context, F3Type elementType) {
        values = new F3Value[16];
        this.elementType = elementType;
        this.context = context;
    }

    public void append(F3Value value) {
        if (sequence != null)
            throw new IllegalStateException("appending to SequenceBuilder after getSequence");
        int nitems = value.getItemCount();
        if (nvalues + nitems > values.length) {
            int newSize = values.length;
            while (nvalues + nitems > newSize)
                newSize = 2 * newSize;
            F3Value[] newValues = new F3Value[newSize];
            System.arraycopy(values, 0, newValues, 0, nvalues);
            values = newValues;
        }
        for (int i = 0;  i < nitems;  i++) {
            F3Value item = value.getItem(i);
            item = elementType.coerceOrNull(item);
            if (item == null)
                throw new ClassCastException("cannot coerce to "+elementType);
            values[nvalues++] = item;
        }
    }

    /** Get the final sequence result. */
    public F3Value getSequence() {
        if (sequence == null) {
            sequence = context.makeSequenceValue(values, nvalues, elementType);
            values = null;
        }
        return sequence;
    }
}
