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

package org.f3.ideaplugin.parsing;

import com.intellij.psi.tree.IElementType;
import org.f3.tools.antlr.v3Parser;
import org.f3.ideaplugin.F3Plugin;

/**
 * F3AstNodes
 */
public enum F3AstNodes {
    GENERIC_NODE(v3Parser.LAST_TOKEN + 1);//,
//    ERROR_NODE(v3Parser.LAST_TOKEN + 2),
//    MODULE(v3Parser.SCRIPT),
//    PACKAGE_DECL(v3Parser.PACKAGE);

    public final int tokenValue;
    public final F3ElementType elementType;

    private static F3ElementType[] tokenArray;

    static {
        int max = 0;
        for (F3AstNodes t : F3AstNodes.values())
            max = Math.max(max, t.tokenValue);
        tokenArray = new F3ElementType[max+1];
        for (F3AstNodes t : F3AstNodes.values())
            tokenArray[t.tokenValue] = t.elementType;
    }

    F3AstNodes(int tokenValue) {
        this.tokenValue = tokenValue;
        this.elementType = new F3ElementType(name(), F3Plugin.F3_LANGUAGE, tokenValue);
    }

    public IElementType asElementType() {
        return elementType;
    }

    public static IElementType getElement(int tokenType) {
        if (tokenType < 0)
            return null; // avoid AIOOBE
        else
            return tokenArray[tokenType];
    }
}
