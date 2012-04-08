/*
 * Copyright 2010 Sun Microsystems, Inc.  All Rights Reserved.
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

package org.f3.jdi.connect;

import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.Transport;
import java.util.Map;

/**
 *
 * @author sundar
 */
public class F3Connector implements Connector {
    private final Connector underlying;

    public F3Connector(Connector underlying) {
        this.underlying = underlying;
    }

    public Map<String, Argument> defaultArguments() {
        return underlying().defaultArguments();
    }

    public String description() {
        return underlying().description();
    }

    public String name() {
        return getClass().getName();
    }

    public Transport transport() {
        return new F3Transport(underlying().transport());
    }

    protected Connector underlying() {
        return underlying;
    }
}
