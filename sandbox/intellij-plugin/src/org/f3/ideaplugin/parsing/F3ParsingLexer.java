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
import com.intellij.psi.tree.IElementType;
import org.f3.tools.antlr.v3Lexer;
import org.jetbrains.annotations.Nullable;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;

/**
 * F3ParsingLexer
 *
 * @author Brian Goetz
 */
public class F3ParsingLexer extends LexerBase {
    private int bufferEnd;
    private char[] buffer;
    private int curIndex, size;
    private int[] tokenStart;
    private IElementType[] tokenType;

    @Deprecated
    public void start(char[] buffer, int startOffset, int endOffset, int initialState) {
        System.out.printf("%s/%s: starting lexing %d:%d%n", Thread.currentThread(), this, startOffset, endOffset);
        assert(startOffset == 0);
        this.buffer = buffer;
        this.bufferEnd = endOffset;
        WrappedAntlrLexer lexer = new WrappedAntlrLexer(new ANTLRStringStream(buffer, endOffset), false);

        // Inelegant -- prepare buffers that are way too big and resize at end.  Better to resize dynamically -- tbd.
        // Need to make room for synthetic semicolon tokens
        tokenStart = new int[endOffset*2];
        tokenType = new IElementType[endOffset*2];
        curIndex = 0;
        size = 0;

        if (endOffset == 0)
            return;
        
        while (true) {
            tokenStart[curIndex] = lexer.getCharIndex();
            Token t;
            try {
                t = lexer.nextToken();
            } catch (RecognitionExceptionSignal s) {
                lexer.recover(s.exception);
                t = Token.INVALID_TOKEN;
            }
            if (t.getType() == v3Lexer.EOF) {
                tokenType[curIndex++] = null;
                size = curIndex;
                int[] tempInts = new int[size];
                System.arraycopy(tokenStart, 0, tempInts, 0, size);
                tokenStart = tempInts;
                IElementType[] tempTokens = new IElementType[size];
                System.arraycopy(tokenType, 0, tempTokens, 0, size);
                tokenType = tempTokens;
                break;
            }
            else {
                IElementType element = F3Tokens.getElement(t.getType());
                if (element == null) {
                    System.out.println("Unknown token type " + t.getType());
                    element = F3Tokens.WS.elementType;
                }
                tokenType[curIndex++] = element;
            }
        }

        System.out.printf("%s/%s: done lexing %d%n", Thread.currentThread(), this, getSize());
        curIndex = 0;
    }

    public int getState() {
        return 0;
    }

    public int getIndex() {
        return curIndex;
    }

    public int getSize() {
        return size;
    }

    @Nullable
    public IElementType getTokenType() {
        return (curIndex < size) ? tokenType[curIndex] : null;
    }

    public int getTokenStart() {
        return (curIndex < size) ? tokenStart[curIndex] : bufferEnd;
    }

    public int getTokenEnd() {
        return curIndex >= size ? bufferEnd : tokenStart[curIndex+1];
    }

    public void advance() {
        ++curIndex;
    }

    @Deprecated
    public char[] getBuffer() {
        return buffer;
    }

    public int getBufferEnd() {
        return bufferEnd;
    }
}
