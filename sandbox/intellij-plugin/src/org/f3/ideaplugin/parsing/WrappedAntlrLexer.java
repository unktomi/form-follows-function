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

import com.sun.tools.javac.util.Context;
import org.f3.tools.antlr.v3Lexer;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.RecognitionException;

/**
 * WrappedAntlrLexer
*
* @author Brian Goetz
*/
class WrappedAntlrLexer extends v3Lexer {
    public static final int SYNTHETIC_SEMI = -100;
    public final int syntheticSemi;
    public final boolean signalOnError;

    WrappedAntlrLexer(ANTLRStringStream stringStream, boolean useSyntheticSemi, boolean signalOnError) {
        super(new Context(), stringStream);
        syntheticSemi = useSyntheticSemi ? SYNTHETIC_SEMI : SEMI;
        this.signalOnError = signalOnError;
    }

    WrappedAntlrLexer(ANTLRStringStream stringStream, boolean useSyntheticSemi) {
        this(stringStream, useSyntheticSemi, true);
    }

    // Workaround IAE exception in creating diagnostic
    public void displayRecognitionError(String[] strings, RecognitionException recognitionException) {
        // Blechh!!  But if we don't do this, we loop forever.
        if (signalOnError)
            throw new RecognitionExceptionSignal(recognitionException);
    }

    /* Override this so we can distinguish between real and synthetic semicolon in lexing */
    protected int getSyntheticSemiType() {
        return syntheticSemi;
    }

    public int getState() {
        return getLexicalState();
    }
}
