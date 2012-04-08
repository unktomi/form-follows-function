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

package org.f3.api;

import org.f3.api.tree.UnitTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskEvent.Kind;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

/**
 * Provides details about work that has been done by the F3 Compiler.
 */
public final class F3TaskEvent
{
    private TaskEvent javacEvent;
    private UnitTree unit;
    private TypeElement clazz;

    public F3TaskEvent(Kind kind, JavaFileObject sourceFile) {
        this.javacEvent = new TaskEvent(kind, sourceFile);
        this.unit = null;
    }

    public F3TaskEvent(Kind kind, CompilationUnitTree javaUnit, TypeElement clazz) {
        this.javacEvent = new TaskEvent(kind, javaUnit, clazz);
        this.unit = null;
    }

    public F3TaskEvent(Kind kind, UnitTree unit) {
        this(kind, unit, null);
    }

    public F3TaskEvent(Kind kind, UnitTree unit, TypeElement clazz) {
        this.javacEvent = new TaskEvent(kind);
        this.unit = unit;
        this.clazz = clazz;
    }

    public Kind getKind() {
        return javacEvent.getKind();
    }

    public JavaFileObject getSourceFile() {
        return unit != null ? unit.getSourceFile() : javacEvent.getSourceFile();
    }

    public UnitTree getUnit() {
        return unit;
    }

    public CompilationUnitTree getCompilationUnit() {
        return javacEvent.getCompilationUnit();
    }

    public TypeElement getTypeElement() {
        return clazz != null ? clazz : javacEvent.getTypeElement();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("F3TaskEvent[");
        sb.append(getKind());
        sb.append(',');
        sb.append(getSourceFile());
        TypeElement type = getTypeElement();
        if (type != null) {
            sb.append(',');
            sb.append(type);
        }
        sb.append(']');
        return sb.toString();
    }
}
