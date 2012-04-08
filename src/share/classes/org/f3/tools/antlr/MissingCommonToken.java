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

package org.f3.tools.antlr;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;

/**
 * An implementation of the ANTLR 3.x CommonToken, with extra information
 * concerning error recovery.
 *
 * An object of this type is created by the parser error recovery mechanisms
 * when they can determine that a parsing error would be caused by the simple
 * problem of a single missing token, such as an IDENTIFIER or LPAREN etc. The
 * parser will then see the token in the stream as normal, and not throw exceptions
 * (though we do log the error.) The parer rules that care, such as those that
 * need to construct values in the AST, like TIME_LITERAL or STRING_LITERAL etc
 * can then choose to override the default value created by
 * #AbstractGeneratedParserV4.getMissingToken() according to any context information
 * they might have, but more importantly, they can see that the token they have
 * was auto inserted and therefore create different AST nodes to indicate the fact.
 *
 * @author jimi
 */
public class MissingCommonToken extends CommonToken {

    public MissingCommonToken(Token tok) {
        super(tok);
    }

    public MissingCommonToken(int tok, String textVal) {
        super(tok, textVal);
    }

    public MissingCommonToken(CharStream input, int type, int channel, int start, int stop) {
        super(input, type, channel, start, stop);
    }

    public MissingCommonToken(int type) {
        super(type);
    }

}
