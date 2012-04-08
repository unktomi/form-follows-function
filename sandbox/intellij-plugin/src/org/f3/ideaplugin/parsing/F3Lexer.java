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

import com.intellij.lexer.LexerBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.text.CharArrayCharSequence;
import com.intellij.util.text.CharArrayUtil;
import org.f3.tools.antlr.v3Lexer;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
import org.jetbrains.annotations.Nullable;

/**
 * F3Lexer
 *
 * @author Brian Goetz
 */
public class F3Lexer extends LexerBase {

    private final boolean ignoreSyntheticSemi;
    private int bufferStart, bufferEnd;
    private Token nextToken;
    private int nextState;
    private int curStart, curEnd;
    private CharSequence buffer;
    private WrappedAntlrLexer lexer;

    public F3Lexer(boolean ignoreSyntheticSemi) {
        this.ignoreSyntheticSemi = ignoreSyntheticSemi;
    }

    public F3Lexer() {
        this(true);
    }

    public void start(char[] buffer, int startOffset, int endOffset, int initialState) {
        start(new CharArrayCharSequence(buffer), startOffset, endOffset, initialState);
    }

    public void start(CharSequence buffer, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        bufferStart = startOffset;
        bufferEnd = endOffset;
        lexer = new WrappedAntlrLexer(new ANTLRStringStream(buffer.toString().substring(startOffset, endOffset)), true);
        advance();
    }

    public void reset() {
        lexer.reset();
        advance();
    }
    
    public int getState() {
        return nextState;
    }

    public int getTokenStart() {
        return bufferStart + curStart;
    }

    public int getTokenEnd() {
        return bufferStart + curEnd;
    }

    @Nullable
    public IElementType getTokenType() {
        int tokenType = nextToken.getType();
        if (tokenType == v3Lexer.EOF)
            return null;
        IElementType result = F3Tokens.getElement(tokenType);
        if (result == null) {
            System.out.printf("unknown token type %d%n", tokenType);
            return TokenType.BAD_CHARACTER;
        }
        return result;
    }

    public void advance() {
        curStart = lexer.getCharIndex();
        try {
            do {
                nextState = lexer.getState();
                nextToken = lexer.nextToken();
            }
            while (ignoreSyntheticSemi && nextToken.getType() == WrappedAntlrLexer.SYNTHETIC_SEMI);
        } catch (RecognitionExceptionSignal s) {
            lexer.recover(s.exception);
            nextToken = Token.INVALID_TOKEN;
        }
        curEnd = lexer.getCharIndex();
//        System.out.printf("Processed %d:%s @ %d:%d/%d => %d%n", nextToken.getType(), F3Tokens.getElement(nextToken.getType()), getTokenStart(), getTokenEnd(), bufferEnd, nextState);
        if (curEnd == curStart && nextToken.getType() != v3Lexer.EOF)
            System.out.printf("Failed to advance position: %d:%d/%d(%s:%s)%n", curStart, curEnd, bufferEnd, F3Tokens.getElement(nextToken.getType()), nextToken.getText());
    }

    @Deprecated
    public char[] getBuffer() {
        return CharArrayUtil.fromSequence(buffer);
    }

    public int getBufferEnd() {
        return bufferEnd;
    }

}

