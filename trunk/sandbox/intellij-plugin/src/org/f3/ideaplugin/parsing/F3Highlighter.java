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

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * F3Highlighter
 *
 * @author Brian Goetz
 */
public class F3Highlighter extends SyntaxHighlighterBase {
    private static final Map<IElementType, TextAttributesKey> keys;

    private static final TextAttributesKey F3_KEYWORD
            = TextAttributesKey.createTextAttributesKey("F3.KEYWORD", HighlighterColors.JAVA_KEYWORD.getDefaultAttributes());

    private static final TextAttributesKey F3_STRING
            = TextAttributesKey.createTextAttributesKey("F3.STRING", HighlighterColors.JAVA_STRING.getDefaultAttributes());

    private static final TextAttributesKey F3_NUMERIC
            = TextAttributesKey.createTextAttributesKey("F3.NUMERIC", HighlighterColors.JAVA_NUMBER.getDefaultAttributes());

    private static final TextAttributesKey F3_BAD_CHARACTER
            = TextAttributesKey.createTextAttributesKey("F3.BADCHARACTER", HighlighterColors.BAD_CHARACTER.getDefaultAttributes());

    private static final TextAttributesKey F3_LINE_COMMENT
            = TextAttributesKey.createTextAttributesKey("F3.LINE_COMMENT", HighlighterColors.JAVA_LINE_COMMENT.getDefaultAttributes());

    private static final TextAttributesKey F3_BLOCK_COMMENT
            = TextAttributesKey.createTextAttributesKey("F3.BLOCK_COMMENT", HighlighterColors.JAVA_BLOCK_COMMENT.getDefaultAttributes());

    static {
        keys = new HashMap<IElementType, TextAttributesKey>();

        fillMap(keys, F3Tokens.KEYWORDS, F3_KEYWORD);
        fillMap(keys, F3Tokens.STRING_LITERALS, F3_STRING);
        fillMap(keys, F3Tokens.NUMERIC_LITERALS, F3_NUMERIC);
        keys.put(TokenType.BAD_CHARACTER, F3_BAD_CHARACTER);
        keys.put(F3Tokens.COMMENT.elementType, F3_BLOCK_COMMENT);
        keys.put(F3Tokens.LINE_COMMENT.elementType, F3_LINE_COMMENT);
    }

    @NotNull
    public Lexer getHighlightingLexer() {
        return new F3Lexer();
    }

    @NotNull
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        return pack(keys.get(tokenType));
    }
}
