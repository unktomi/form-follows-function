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

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.f3.ideaplugin.F3Plugin;
import org.f3.tools.antlr.v3Lexer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * F3Tokens
 *
 * @author Brian Goetz
 */
public enum F3Tokens {
    // Simple keywords
    ABSTRACT(v3Lexer.ABSTRACT),
    ASSERT(v3Lexer.ASSERT),
    AT(v3Lexer.AT),
    ATTRIBUTE(v3Lexer.ATTRIBUTE),
    BIND(v3Lexer.BIND),
    BOUND(v3Lexer.BOUND),
    BREAK(v3Lexer.BREAK),
    CLASS(v3Lexer.CLASS),
    CONTINUE(v3Lexer.CONTINUE),
    DELETE(v3Lexer.DELETE),
    FALSE(v3Lexer.FALSE),
    FOR(v3Lexer.FOR),
    FUNCTION(v3Lexer.FUNCTION),
    IF(v3Lexer.IF),
    IMPORT(v3Lexer.IMPORT),
    INDEXOF(v3Lexer.INDEXOF),
    INIT(v3Lexer.INIT),
    INSERT(v3Lexer.INSERT),
    NEW(v3Lexer.NEW),
    NOT(v3Lexer.NOT),
    NULL(v3Lexer.NULL),
    OVERRIDE(v3Lexer.OVERRIDE),
    PACKAGE(v3Lexer.PACKAGE),
    POSTINIT(v3Lexer.POSTINIT),
    PRIVATE(v3Lexer.PRIVATE),
    PROTECTED(v3Lexer.PROTECTED),
    PUBLIC(v3Lexer.PUBLIC),
    RETURN(v3Lexer.RETURN),
    REVERSE(v3Lexer.REVERSE),
    SUPER(v3Lexer.SUPER),
    SIZEOF(v3Lexer.SIZEOF),
    STATIC(v3Lexer.STATIC),
    THIS(v3Lexer.THIS),
    THROW(v3Lexer.THROW),
    TRY(v3Lexer.TRY),
    TRUE(v3Lexer.TRUE),
    TYPEOF(v3Lexer.TYPEOF),
    VAR(v3Lexer.VAR),
    WHILE(v3Lexer.WHILE),

    // Subsidiary keywords
    AFTER(v3Lexer.AFTER),
    AND(v3Lexer.AND),
    AS(v3Lexer.AS),
    BEFORE(v3Lexer.BEFORE),
    CATCH(v3Lexer.CATCH),
    ELSE(v3Lexer.ELSE),
    EXCLUSIVE(v3Lexer.EXCLUSIVE),
    EXTENDS(v3Lexer.EXTENDS),
    FINALLY(v3Lexer.FINALLY),
    FIRST(v3Lexer.FIRST),
    FROM(v3Lexer.FROM),
    IN(v3Lexer.IN),
    INSTANCEOF(v3Lexer.INSTANCEOF),
    INTO(v3Lexer.INTO),
    INVERSE(v3Lexer.INVERSE),
    LAST(v3Lexer.LAST),
    LAZY(v3Lexer.LAZY),
    ON(v3Lexer.ON),
    OR(v3Lexer.OR),
    REPLACE(v3Lexer.REPLACE),
    STEP(v3Lexer.STEP),
    THEN(v3Lexer.THEN),
    TRIGGER(v3Lexer.TRIGGER),
    WITH(v3Lexer.WITH),
    WHERE(v3Lexer.WHERE),

    // Punctuation
    POUND(v3Lexer.POUND),
    LPAREN(v3Lexer.LPAREN),
    LBRACKET(v3Lexer.LBRACKET),
    LBRACE(v3Lexer.LBRACE),
    PLUSPLUS(v3Lexer.PLUSPLUS),
    SUBSUB(v3Lexer.SUBSUB),
    PIPE(v3Lexer.PIPE),
    DOTDOT(v3Lexer.DOTDOT),
    RPAREN(v3Lexer.RPAREN),
    RBRACKET(v3Lexer.RBRACKET),
    RBRACE(v3Lexer.RBRACE),
    SEMI(v3Lexer.SEMI),
    COMMA(v3Lexer.COMMA),
    DOT(v3Lexer.DOT),
    EQEQ(v3Lexer.EQEQ),
    EQ(v3Lexer.EQ),
	NOTEQ(v3Lexer.NOTEQ),
	GT(v3Lexer.GT),
    LT(v3Lexer.LT),
    LTGT(v3Lexer.LTGT),
    LTEQ(v3Lexer.LTEQ),
    GTEQ(v3Lexer.GTEQ),
    PLUS(v3Lexer.PLUS),
    SUB(v3Lexer.SUB),
    STAR(v3Lexer.STAR),
    SLASH(v3Lexer.SLASH),
    PERCENT(v3Lexer.PERCENT),
    PLUSEQ(v3Lexer.PLUSEQ),
    SUBEQ(v3Lexer.SUBEQ),
    STAREQ(v3Lexer.STAREQ),
    SLASHEQ(v3Lexer.SLASHEQ),
    PERCENTEQ(v3Lexer.PERCENTEQ),
    COLON(v3Lexer.COLON),
    QUES(v3Lexer.QUES),
    TWEEN(v3Lexer.TWEEN),
    SUCHTHAT(v3Lexer.SUCHTHAT),

    // Literal types
    DECIMAL_LITERAL(v3Lexer.DECIMAL_LITERAL),
    FLOATING_POINT_LITERAL(v3Lexer.FLOATING_POINT_LITERAL),
	HEX_LITERAL(v3Lexer.HEX_LITERAL),
	OCTAL_LITERAL(v3Lexer.OCTAL_LITERAL),
	STRING_LITERAL(v3Lexer.STRING_LITERAL),
	FORMAT_STRING_LITERAL(v3Lexer.FORMAT_STRING_LITERAL),
    RBRACE_QUOTE_STRING_LITERAL(v3Lexer.RBRACE_QUOTE_STRING_LITERAL),
    RBRACE_LBRACE_STRING_LITERAL(v3Lexer.RBRACE_LBRACE_STRING_LITERAL),
    QUOTE_LBRACE_STRING_LITERAL(v3Lexer.QUOTE_LBRACE_STRING_LITERAL),
    TIME_LITERAL(v3Lexer.TIME_LITERAL),

    // Other
    WS(v3Lexer.WS),
    IDENTIFIER(v3Lexer.IDENTIFIER),
    LINE_COMMENT(v3Lexer.LINE_COMMENT),
    COMMENT(v3Lexer.COMMENT);

    public static final TokenSet KEYWORDS = createTokenSet(ABSTRACT, ASSERT, AT, ATTRIBUTE, BIND, BOUND, BREAK,
            CLASS, CONTINUE, DELETE, FALSE, FOR, FUNCTION, IF, IMPORT, INDEXOF, INIT, INSERT,
            NEW, NOT, NULL, OVERRIDE, PACKAGE, POSTINIT, PRIVATE, PROTECTED, PUBLIC, RETURN,
            REVERSE, SUPER, SIZEOF, STATIC, THIS, THROW, TRY, TRUE, TYPEOF, VAR, WHILE);

    public static TokenSet DEPENDENT_KEYWORDS = createTokenSet(AFTER, AND, AS, BEFORE, CATCH, ELSE, EXCLUSIVE,
            EXTENDS, FINALLY, FIRST, FROM, IN, INSTANCEOF, INTO, INVERSE, LAST, LAZY, ON, OR, REPLACE, STEP,
            THEN, TRIGGER, WITH, WHERE);

    public static TokenSet OPERATORS = createTokenSet(POUND, LPAREN, LBRACKET, LBRACE, PLUSPLUS, SUBSUB, PIPE,
            DOTDOT, RPAREN, RBRACKET, RBRACE, SEMI, COMMA, DOT, EQEQ, EQ, NOTEQ, GT, LT, LTGT, LTEQ, GTEQ, PLUS, SUB,
            STAR, SLASH, PERCENT, PLUSEQ, SUBEQ, STAREQ, SLASHEQ, PERCENTEQ, COLON, QUES, TWEEN, SUCHTHAT);

    public static final TokenSet STRING_LITERALS = createTokenSet(STRING_LITERAL, FORMAT_STRING_LITERAL, RBRACE_LBRACE_STRING_LITERAL, RBRACE_QUOTE_STRING_LITERAL, QUOTE_LBRACE_STRING_LITERAL);
    public static final TokenSet NUMERIC_LITERALS = createTokenSet(DECIMAL_LITERAL, HEX_LITERAL, OCTAL_LITERAL, FLOATING_POINT_LITERAL, TIME_LITERAL);

    public static final TokenSet COMMENTS = createTokenSet(COMMENT, LINE_COMMENT);
    public static final TokenSet WHITESPACE = createTokenSet(WS);

    public final int tokenValue;
    public final F3ElementType elementType;

    private static F3ElementType[] tokenArray;

    static {
        int max = 0;
        for (F3Tokens t : F3Tokens.values())
            max = Math.max(max, t.tokenValue);
        tokenArray = new F3ElementType[max+1];
        for (F3Tokens t : F3Tokens.values())
            tokenArray[t.tokenValue] = t.elementType;

        checkForMissingOrInvalidTokens ();
    }

    F3Tokens(int value) {
        tokenValue = value;
        elementType = new F3ElementType(name(), F3Plugin.F3_LANGUAGE, value);
    }

    public IElementType asElementType() {
        return elementType;
    }

    public static TokenSet createTokenSet(F3Tokens... tokens) {
        IElementType[] elements = new IElementType[tokens.length];
        for (int i = 0; i < tokens.length; i++)
            elements[i] = tokens[i].asElementType();
        return TokenSet.create(elements);
    }

    public static IElementType getElement(int tokenType) {
        if (tokenType < 0)
            return null; // avoid AIOOBE
        else if (tokenType == v3Lexer.WS)
            return TokenType.WHITE_SPACE;
        else
            return tokenArray[tokenType];
    }

    private static void checkForMissingOrInvalidTokens () {
        Field[] fields = v3Lexer.class.getDeclaredFields ();
        for (Field field : fields) {
            int mod = field.getModifiers ();
            if (! Modifier.isPublic (mod)  ||  ! Modifier.isStatic (mod)  ||  ! Modifier.isFinal (mod)  ||  ! Integer.TYPE.equals (field.getType ()))
                continue;
            int value;
            try {
                value = (Integer) field.get (null);
            } catch (IllegalAccessException e) {
                e.printStackTrace ();
                continue;
            }
            boolean error = false;
            String name = field.getName ();
            if (value < 0  ||  value >= tokenArray.length)
                error = true;
            else {
                F3ElementType type = tokenArray[value];
                if (type == null)
                    error = true;
                else
                    if (type.antlrToken != value)
                        error = true;
                F3Tokens token = null;
                try {
                    token = valueOf (name);
                } catch (IllegalArgumentException e) {
                }
                if (token == null  ||  type != token.elementType)
                    error = true;
            }
            if (error)
                System.out.println ("ERROR: F3Tokens class has to be updated: additional or incompatible token in v3Lexer class: Name/Value: " + name + "/" + value);
        }
    }

}
