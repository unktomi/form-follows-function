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

import java.util.HashMap;

import com.sun.tools.mjavac.code.Source;

import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.List;
import com.sun.tools.mjavac.util.Log;
import com.sun.tools.mjavac.util.Name;
import com.sun.tools.mjavac.tree.JCTree;

import com.sun.tools.mjavac.util.Options;
import org.f3.tools.tree.F3InterpolateValue;
import org.f3.tools.tree.F3Tree;
import org.f3.tools.tree.F3Block;
import org.f3.tools.tree.F3Erroneous;
import org.f3.tools.tree.F3Type;
import org.f3.tools.tree.F3TreeInfo;
import org.f3.tools.tree.F3TreeMaker;

import org.f3.tools.util.MsgSym;
import javax.tools.DiagnosticListener;
import org.antlr.runtime.*;

/**
 * Base class for ANTLR generated parsers.
 * This version incorporates error reporting and recovery changes
 * enabled by using ANTLR 3.1.
 * 
 * @author Robert Field
 * @author Jim Idle
 */
public abstract class AbstractGeneratedParserV4 extends Parser {
    
        /**
     * Create a new parser instance, pre-supplying the input token stream.
     * @param input The stream of tokens that will be pulled from the lexer
     */
    protected AbstractGeneratedParserV4(TokenStream input) {
        super(input);
    }
    
    /**
     * Create a new parser instance, pre-supplying the input token stream
     * and the shared state.
     * This is only used when a grammar is imported into another grammar.
     * 
     * @param input The stream of tokesn that will be pulled from the lexer
     * @param state The shared state object created by an interconnectd grammar
     */
    protected AbstractGeneratedParserV4(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }
    
    /** The factory to be used for abstract syntax tree construction.
     */
    protected F3TreeMaker  F;
    
    /** The log to be used for error diagnostics.
     */
    protected Log              log;
    
    /** 
     * The Source language setting. 
     */
    protected Source           source;
    
    /** 
     * The token id for white space 
     */
    protected int whiteSpaceToken = v4Parser.WS;
    
    /** 
     * Should the parser generate an end positions map? 
     */
    protected boolean genEndPos;

    /**
     * Should the parser preserve trees as much as possible (for IDE)?
     */
    protected boolean preserveTrees;
    
    /** 
     * The end positions map. 
     * End positions are built by the parser such that each entry in the map
     * is keyed by a F3 tree node built by the parser and the value is
     * the token number in the token stream that correponds to the end position
     * of the node.
     */
    HashMap<JCTree,Integer> endPositions;

    /** 
     * The doc comments map.
     * The documentation comments are comments starting
     * with '/**'. Built by the parser, this map is keyed by the AST
     * node that a comment belongs to and the value is the full text
     * of the comment, including the enclosing '/**' and comment end sequence 
     */
    HashMap<JCTree,String> docComments;

    /**
     * 
     */
    private F3TreeInfo treeInfo;
    
    /** 
     * The name table.
     * Keeps track of all the identifiers discovered by the parser in any particular
     * context.
     */
    protected Name.Table names;
    /**
     * Local F3 tree node used to build an error node in to the AST
     * when a syntax or semantic error is detected while parsing the
     * script. Error nodes are used by downstream tools such as IDEs
     * so that they can navigate source code even while it is not,
     * strictly speaking, valid code.
     */
    protected F3Erroneous errorNode = null;  

    /**
     * Defines the human readable names of all the tokens that the lexer
     * can produce for use by error messages and utilities that interact with
     * the user/author.
     */
    protected java.util.Map<String, String> tokenMap = new java.util.HashMap<String, String>(); 
    {
        tokenMap.put("ABSTRACT", "abstract");
        tokenMap.put("ASSERT", "assert");
        tokenMap.put("BIND", "bind");
        tokenMap.put("BOUND", "bound");
        tokenMap.put("BREAK", "break");
        tokenMap.put("CLASS", "class");
        tokenMap.put("CONTINUE", "continue");
        tokenMap.put("DELETE", "delete");
        tokenMap.put("FALSE", "false");
        tokenMap.put("FOR", "for");
        tokenMap.put("FUNCTION", "function");
        tokenMap.put("IF", "if");
        tokenMap.put("IMPORT", "import");
        tokenMap.put("INIT", "init");
        tokenMap.put("INSERT", "insert");
        tokenMap.put("LET", "let");
        tokenMap.put("NEW", "new");
        tokenMap.put("NOT", "not");
        tokenMap.put("NULL", "null");
        tokenMap.put("OVERRIDE", "override");
        tokenMap.put("PACKAGE", "package");
        tokenMap.put("POSTINIT", "postinit");
        tokenMap.put("PRIVATE", "private");
        tokenMap.put("PROTECTED", "protected");
        tokenMap.put("PUBLIC", "public");
        tokenMap.put("READONLY", "readonly");
        tokenMap.put("RETURN", "return");
        tokenMap.put("SUPER", "super");
        tokenMap.put("SIZEOF", "sizeof");
        tokenMap.put("STATIC", "static");
        tokenMap.put("THIS", "this");
        tokenMap.put("THROW", "throw");
        tokenMap.put("TRY", "try");
        tokenMap.put("TRUE", "true");
        tokenMap.put("VAR", "var");
        tokenMap.put("WHILE", "while");
        tokenMap.put("POUND", "#");
        tokenMap.put("LPAREN", "(");
        tokenMap.put("LBRACKET", "[");
        tokenMap.put("PLUSPLUS", "++");
        tokenMap.put("SUBSUB", "--");
        tokenMap.put("PIPE", "|");
        tokenMap.put("AFTER", "after");
        tokenMap.put("AND", "and");
        tokenMap.put("AS", "as");
        tokenMap.put("BEFORE", "before");
        tokenMap.put("CATCH", "catch");
        tokenMap.put("ELSE", "else");
        tokenMap.put("EXCLUSIVE", "exclusive");
        tokenMap.put("EXTENDS", "extends");
        tokenMap.put("FINALLY", "finally");
        tokenMap.put("FIRST", "first");
        tokenMap.put("FROM", "from");
        tokenMap.put("IN", "in");
        tokenMap.put("INDEXOF", "indexof");
        tokenMap.put("INSTANCEOF", "instanceof");
        tokenMap.put("INTO", "into");
        tokenMap.put("INVERSE", "inverse");
        tokenMap.put("LAST", "last");
        tokenMap.put("LAZY", "lazy");
        tokenMap.put("ON", "on");
        tokenMap.put("OR", "or");
        tokenMap.put("REPLACE", "replace");
        tokenMap.put("REVERSE", "reverse");
        tokenMap.put("STEP", "step");
        tokenMap.put("THEN", "then");
        tokenMap.put("TYPEOF", "typeof");
        tokenMap.put("WITH", "with");
        tokenMap.put("WHERE", "where");
        tokenMap.put("DOTDOT", "..");
        tokenMap.put("RPAREN", ")");
        tokenMap.put("RBRACKET", "]");
        tokenMap.put("SEMI", ";");
        tokenMap.put("COMMA", ",");
        tokenMap.put("DOT", ".");
        tokenMap.put("EQEQ", "==");
        tokenMap.put("EQ", "=");
        tokenMap.put("GT", ">");
        tokenMap.put("LT", "<");
        tokenMap.put("LTGT", "<>");
        tokenMap.put("NOTEQ", "!=");
        tokenMap.put("LTEQ", "<=");
        tokenMap.put("GTEQ", ">=");
        tokenMap.put("PLUS", "+");
        tokenMap.put("SUB", "-");
        tokenMap.put("STAR", "*");
        tokenMap.put("SLASH", "/");
        tokenMap.put("PERCENT", "%");
        tokenMap.put("PLUSEQ", "+=");
        tokenMap.put("SUBEQ", "-=");
        tokenMap.put("STAREQ", "*=");
        tokenMap.put("SLASHEQ", "/=");
        tokenMap.put("PERCENTEQ", "%=");
        tokenMap.put("COLON", ":");
        tokenMap.put("QUES", "?");
        tokenMap.put("DoubleQuoteBody", "double quote string literal");
        tokenMap.put("SingleQuoteBody", "single quote string literal");
        tokenMap.put("STRING_LITERAL", "string literal");
        tokenMap.put("NextIsPercent", "%");
        tokenMap.put("QUOTE_LBRACE_STRING_LITERAL", "\" { string literal");
        tokenMap.put("LBRACE", "{");
        tokenMap.put("RBRACE_QUOTE_STRING_LITERAL", "} \" string literal");
        tokenMap.put("RBRACE_LBRACE_STRING_LITERAL", "} { string literal");
        tokenMap.put("RBRACE", "}");
        tokenMap.put("FORMAT_STRING_LITERAL", "format string literal");
        tokenMap.put("TranslationKeyBody", "translation key body");
        tokenMap.put("TRANSLATION_KEY", "translation key");
        tokenMap.put("DECIMAL_LITERAL", "decimal literal");
        tokenMap.put("Digits", "digits");
        tokenMap.put("Exponent", "exponent");
        tokenMap.put("TIME_LITERAL", "time literal");
        tokenMap.put("OCTAL_LITERAL", "octal literal");
        tokenMap.put("HexDigit", "hex digit");
        tokenMap.put("HEX_LITERAL", "hex literal");
        tokenMap.put("RangeDots", "..");
        tokenMap.put("FLOATING_POINT_LITERAL", "floating point literal");
        tokenMap.put("Letter", "letter");
        tokenMap.put("JavaIDDigit", "java ID digit");
        tokenMap.put("IDENTIFIER", "identifier");
        tokenMap.put("WS", "white space");
        tokenMap.put("COMMENT", "comment");
        tokenMap.put("LINE_COMMENT", "line comment");
        tokenMap.put("LAST_TOKEN", "last token");
    } 
    
    /**
     * An array of the human readable names of all the tokens the 
     * lexer can provide to the parser.
     * 
     * This field should be accessed using the getF3TokenNames method
     * @see #getF3TokenNames
     */
    protected String[] f3TokenNames = null;
    
    /**
     * Provides a human readable name for each of the parser grammar rules
     * for use by error messages or any tool that interacts with the user/author
     */
    protected String[][] ruleMap = { 
            {"script",                      "the script contents"},
            {"scriptItems",                 "the script contents"},
            {"scriptItem",                  "the script contents"},
            {"modifers",                    "the modifiers for a declaration ('function', 'var', 'class', etc)"},
            {"modiferFlag",                 "an access modifier"},
            {"packageDecl",                 "a 'package' declaration"},
            {"importDecl",                  "an 'import' declaration"},
            {"importId",                    "an 'import' declaration"},
            {"classDefinition",             "a 'class' declaration"},
            {"supers",                      "the 'extends' part of a 'class' declaration"},
            {"classMembers",                "the members of a 'class' declaration"},
            {"classMember",                 "a 'class' declaration member"},
            {"functionDefinition",          "a function declaration"},
            {"overrideDeclaration",         "an overridden variable"},
            {"initDefinition",              "an 'init' block"},
            {"postInitDefinition",          "a 'postinit' block"},
            {"variableDeclaration",         "a variable declaration"},
            {"formalParameters",            "the parameters of a function declaration"},
            {"formalParameter",             "a parameter"},
            {"block",                       "a block"},
            {"statement",                   "a statement"},
            {"onReplaceClause",             "an 'on replace' clause"},
            {"paramNameOpt",                "an optional parameter name"},
            {"paramName",                   "a parameter name"},
            {"variableLabel",               "a variable declaration"},
            {"throwStatement",              "a 'throw' statement"},
            {"whileStatement",              "a 'while' statement"},
            {"insertStatement",             "an 'insert' statement"},
            {"indexedSequenceForInsert",    "an indexed sequence in an insert statement"},
            {"deleteStatement",             "a 'delete' statement"},
            {"returnStatement",             "a 'return' statement"},
            {"tryStatement",                "a 'try' statement"},
            {"finallyClause",               "a 'finally' clause"},
            {"catchClause",                 "a 'catch' clause"},
            {"boundExpression",             "an expression"},
            {"expression",                  "an expression"},
            {"forExpression",               "a 'for' statement or expression"},
            {"inClause",                    "the 'in' clause of a 'for' expression"},
            {"ifExpression",                "an if statement or expression"},
            {"elseClause",                  "the 'else' clause of an 'if' expression"},
            {"assignmentExpression",        "an assignment"},
            {"assignmentOpExpression",      "an operator assignment expression"},
            {"assignOp",                    "an assignment operator"},
            {"andExpression",               "an expression"},
            {"orExpression",                "an expression"},
            {"typeExpression",              "an expression"},
            {"relationalExpression",        "an expression"},
            {"relOps",                      "a relational operator"},
            {"additiveExpression",          "an expression"},
            {"arithOps",                    "an arithmetic operator"},
            {"multiplicativeExpression",    "an expression"},
            {"multOps",                     "an arithmetic operator"},
            {"unaryExpression",             "an expression"},
            {"unaryOps",                    "a unary operator"},
            {"suffixedExpression",          "an expression"},
            {"postfixExpression",           "an expression"},
            {"primaryExpression",           "an expression"},
            {"keyFrameLiteralPart",         "a frame value expression"},
            {"functionExpression",          "an anonymous function definition"},
            {"newExpression",               "a 'new' expression"},
            {"objectLiteral",               "an object literal definition"},
            {"objectLiteralPart",           "a member of an object literal"},
            {"objectLiteralInit",           "an object literal initializer"},
            {"stringExpression",            "a string expression"},
            {"strCompoundElement",          "a compound string element"},
            {"stringLiteral",               "a string literal"},
            {"qlsl",                        "a compound string element"},
            {"stringExpressionInner",       "an embedded string expression"},
            {"stringFormat",                "a string formatting specification"},
            {"bracketExpression",           "a sequence creation expression"},
            {"expressionList",              "a list of expressions"},
            {"expressionListOpt",           "an optional list of expressions"},
            {"type",                        "a type specification"},
            {"typeArgList",                 "a type specification"},
            {"typeArg",                     "a type specification"},
            {"typeReference",               "a type specification"},
            {"cardinality",                 "a type specification"},
            {"typeName",                    "a type specification"},
            {"genericArgument",             "a type specification"},
            {"literal",                     "a literal constant"},
            {"qualname",                    "a qualified identifier"},
            {"identifier",                  "an identifier"},
            {"identifierAll",               "an identifier"},
            {"name",                        "an identifier"},
            {"nameAll",                     "an identifier"},
    };
    


    /** 
     * Initializes a new instance of GeneratedParser 
     */
    protected void initialize(Context context) {
       
        this.F          = (F3TreeMaker)F3TreeMaker.instance(context);
        this.log        = Log.instance(context);
        this.names      = Name.Table.instance(context);
        this.source     = Source.instance(context);
        Options options = Options.instance(context);
        this.genEndPos  =    options.get("-Xjcov") != null 
                          || context.get(DiagnosticListener.class) != null 
                          || Boolean.getBoolean("F3ModuleBuilder.debugBadPositions");

        this.preserveTrees = options.get("preserveTrees") != null;
        this.treeInfo = (F3TreeInfo) F3TreeInfo.instance(context);
        
    }
    
    /**
     * Using the supplied grammar rule name, search the rule map
     * and return a user friendly description of the what the
     * rule indicates we must have been parsing at the time of
     * error.
     * 
     * @param ruleName The grammar rule name as supplied by ANTLR error routines
     * @return Friendly form of the rule name for use in messages
     */
    protected String stackPositionDescription(String ruleName) {
        
        // optimize for the non-error case: do sequential search
        //
        for (String[] pair : ruleMap) {
            if (pair[0].equals(ruleName)) {
                
                // We found a rule name that matched where we are on the stack
                // so we can use the description associated with it.
                //
                return pair[1];
            }
        }
        
        // If here then we did not suppyl a specific description
        // for this rule, so we attempt to formulate it into something
        // readable by humans. We wplit the rule name on camel case
        // and predict if this is 'an' or 'a'
        //
        StringBuffer sb = new StringBuffer(ruleName.length()+1);
        switch (ruleName.charAt(0)) {
            case 'a': case 'e': case 'i': case 'o': case 'u': 
                 sb.append("an ");
                break;
            default:
                sb.append("a ");
                break;
        }
        for (char ch : ruleName.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                sb.append(' ');
                sb.append(Character.toLowerCase(ch));
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
    
    /**
     * A translation matrix for converting a particular token classification
     * into a human readable description.
     */
    protected enum TokenClassification {
        KEYWORD {
            String forHumans() {
                return "a keyword";
            }
        },
        DEPRECATED_KEYWORD {
            String forHumans() {
                return "a no longer supported keyword";
            }
        },
        OPERATOR {
            String forHumans() {
                return "an operator";
            }
        }, 
        IDENTIFIER {
            String forHumans() {
                return "an identifier";
            }
        },  
        PUNCTUATION {
              String forHumans() {
                return "a punctuation character";
            }
        },
        UNKNOWN {
            String forHumans() {
                return "a token";
            }
        };
        abstract String forHumans();
    };

    /**
     * 
     */
    protected TokenClassification[] tokenClassMap = new TokenClassification[v4Parser.LAST_TOKEN + 1];
    
    /**
     * Initializer is used to initalize our token class map, which tells
     * error messages and so on how to describe the token to human beings.
     */
    {
        
        // First, set all the token types to UNKNOWN. LAST_TOKEN is an artifical
        // token generated by the parser, so that it is assigned a token number
        // higher than all the lexer defined tokens and we can use it as size
        //
        for (int index = 0; index <= v4Parser.LAST_TOKEN; index += 1) {
            tokenClassMap[index] = TokenClassification.UNKNOWN;
        }
        // Now set the type ourselves, leaving anythign we don't know about yet
        // to show up as UNKNOWN.
        // If a token is removed from the grammar, the corresponding initialization 
        // will fail to compile (which is the earliest we could detect the problem).
        //
        // Keywords:
        //
        tokenClassMap[v4Parser.ABSTRACT]            = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.ASSERT]              = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.BIND]                = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.BOUND]               = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.BREAK]               = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.CLASS]               = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.CONTINUE]            = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.DELETE]              = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.FALSE]               = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.FOR]                 = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.FUNCTION]            = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.IF]                  = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.IMPORT]              = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.INIT]                = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.INSERT]              = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.DEF]                 = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.CONST]                 = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.NEW]                 = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.NOT]                 = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.NULL]                = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.OVERRIDE]            = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.PACKAGE]             = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.POSTINIT]            = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.PRIVATE]             = TokenClassification.DEPRECATED_KEYWORD;
        tokenClassMap[v4Parser.PROTECTED]           = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.PUBLIC]              = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.RETURN]              = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.SUPER]               = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.SIZEOF]              = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.STATIC]              = TokenClassification.DEPRECATED_KEYWORD;
        tokenClassMap[v4Parser.THIS]                = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.THROW]               = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.TRY]                 = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.TRUE]                = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.VAR]                 = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.WHILE]               = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.AFTER]               = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.AND]                 = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.AS]                  = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.BEFORE]              = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.CATCH]               = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.ELSE]                = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.EXCLUSIVE]           = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.EXTENDS]             = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.FINALLY]             = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.FIRST]               = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.FROM]                = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.IN]                  = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.INDEXOF]             = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.INSTANCEOF]          = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.INTO]                = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.INVERSE]             = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.LAST]                = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.LAZY]                = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.ON]                  = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.OR]                  = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.REPLACE]             = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.REVERSE]             = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.STEP]                = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.THEN]                = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.TYPEOF]              = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.WITH]                = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.WHERE]               = TokenClassification.KEYWORD;
        tokenClassMap[v4Parser.TWEEN]               = TokenClassification.KEYWORD;
        
        // Operators:
        //
        tokenClassMap[v4Parser.PLUSPLUS]            = TokenClassification.OPERATOR;
        tokenClassMap[v4Parser.SUBSUB]              = TokenClassification.OPERATOR;
        tokenClassMap[v4Parser.PIPE]                = TokenClassification.OPERATOR;
        tokenClassMap[v4Parser.DOTDOT]              = TokenClassification.OPERATOR;
        tokenClassMap[v4Parser.DOT]                 = TokenClassification.OPERATOR;
        tokenClassMap[v4Parser.EQEQ]                = TokenClassification.OPERATOR;
        tokenClassMap[v4Parser.EQ]                  = TokenClassification.OPERATOR;
        tokenClassMap[v4Parser.GT]                  = TokenClassification.OPERATOR;
        tokenClassMap[v4Parser.LT]                  = TokenClassification.OPERATOR;
        tokenClassMap[v4Parser.LTGT]                = TokenClassification.OPERATOR;
        tokenClassMap[v4Parser.NOTEQ]               = TokenClassification.OPERATOR;
        tokenClassMap[v4Parser.LTEQ]                = TokenClassification.OPERATOR;
        tokenClassMap[v4Parser.GTEQ]                = TokenClassification.OPERATOR;
        tokenClassMap[v4Parser.PLUS]                = TokenClassification.OPERATOR;
        tokenClassMap[v4Parser.SUB]                 = TokenClassification.OPERATOR;
        tokenClassMap[v4Parser.STAR]                = TokenClassification.OPERATOR;
        tokenClassMap[v4Parser.SLASH]               = TokenClassification.OPERATOR;
        tokenClassMap[v4Parser.PERCENT]             = TokenClassification.OPERATOR;
        tokenClassMap[v4Parser.PLUSEQ]              = TokenClassification.OPERATOR;
        tokenClassMap[v4Parser.SUBEQ]               = TokenClassification.OPERATOR;
        tokenClassMap[v4Parser.STAREQ]              = TokenClassification.OPERATOR;
        tokenClassMap[v4Parser.SLASHEQ]             = TokenClassification.OPERATOR;
        tokenClassMap[v4Parser.PERCENTEQ]           = TokenClassification.OPERATOR;
        tokenClassMap[v4Parser.QUES]                = TokenClassification.OPERATOR;
        tokenClassMap[v4Parser.SUCHTHAT]            = TokenClassification.OPERATOR;
        
        // Punctuation/syntactic sugar:
        //
        tokenClassMap[v4Parser.COLON]               = TokenClassification.PUNCTUATION;
        tokenClassMap[v4Parser.RPAREN]              = TokenClassification.PUNCTUATION;
        tokenClassMap[v4Parser.RBRACKET]            = TokenClassification.PUNCTUATION;
        tokenClassMap[v4Parser.SEMI]                = TokenClassification.PUNCTUATION;
        tokenClassMap[v4Parser.COMMA]               = TokenClassification.PUNCTUATION;
        tokenClassMap[v4Parser.POUND]               = TokenClassification.PUNCTUATION;
        tokenClassMap[v4Parser.LPAREN]              = TokenClassification.PUNCTUATION;
        tokenClassMap[v4Parser.LBRACKET]            = TokenClassification.PUNCTUATION;
        
        
        // Others:
        //
        tokenClassMap[v4Parser.IDENTIFIER]          = TokenClassification.IDENTIFIER;
    }
    
    /**
     * Returns the classification (OPERATOR, PUNCTUATION, etc) of the
     * supplied token. 
     * @param t The token to classify
     * @return The token classification 
     */
    private TokenClassification classifyToken(Token t) {
               
        // Ask ANTLR what the type is
        //
        int tokenType = t.getType();
        
        // And work out what we have
        //
        return classifyToken(tokenType);
    }

    /**
     * Returns the classification (OPERATOR, PUNCTUATION, etc) of the
     * supplied token type
     * @param t The token to classify
     * @return The token classification 
     */
    private TokenClassification classifyToken(int tokenType) {
        
        // Assume that we don't know what this token is
        //
        TokenClassification result = TokenClassification.UNKNOWN;
              
        // And if it is wihtin the range that we know about, then
        // return the classification that we hard coded.
        //
        if ((tokenType >= 0) && tokenType < tokenClassMap.length) {
            result = tokenClassMap[tokenType];
        }
        return result;
    }
    
    /**
     * Returns the parser name, which is really only useful fdor debugging scenarios.
     * @return The name of the parser class
     */
    protected String getParserName() {
        return this.getClass().getName();
    }

    /**
     * Using the given exception generated by the parser, produce an error
     * message string that is geared towards the F3 script author/user.
     * @param e The exception generated by the parser. 
     * @param tokenNames The names of the tokens as generated by ANTLR (unused by this method).
     * @return The human readable error message string.
     */
    @Override
    public String getErrorMessage(RecognitionException e, String[] tokenNames) {
        
        // The rule invocation stack tells us where we are in terms of
        // LL parse and the path throguh the rules that got us to this point.
        // 
        java.util.List stack = getRuleInvocationStack(e, getParserName());
        
        // The top of the stack is the rule that actaully generated the 
        // exception.
        //
        String stackTop = stack.get(stack.size()-1).toString();
        
        // Now we know where we are, we can pick out the human oriented
        // description of what we were tryig to parse.
        //
        String posDescription = stackPositionDescription(stackTop);
        
        // Where we will build the error message string
        //
        StringBuffer mb = new StringBuffer();

        // Rather than just send a diagnostic message containing just a
        // start position, we really want to create an error spanning
        // Erroneous node. The recipient, such as the IDE, can then nicely underline
        // the token(s) that are in error. So we calculate an endPos and start pos to create
        // an erroneous node at the end of this method, defaulting it to the current
        // position.
        //
        int ep = pos()+1;
        int sp = pos();

        // The exact error message we will construct depends on the
        // exception type that was generated. We will be given one of 
        // the following exceptions:
        //
        // UnwantedTokenException   - There was an extra token in the stream that
        //                            we can see was extra because the next token after it
        //                            is the one that would have matched correctly.
        // 
        // MissingTokenException    - There was a missing token in the stream that we see 
        //                            was missing because the token we actually saw was one
        //                            that is a member of the followset had the token been
        //                            present.
        //
        // MismatchedTokenException - The token we received was not one we were expecting, but
        //                            we could neither identify a missing token that would have made it
        //                            something we can deal with, nor that it was just an
        //                            accidental extra token that we can throw away. Something like
        //                            A B C D and we got to B but the token we got was neither 
        //                            C, D nor anything following.
        //
        // NoViableAltException     - The token we saw isn't predicted by any alternative
        //                            path available at this point in the current rule.
        //                            something like:  ... (B|C|D|E) but we got Z which does
        //                            not follow from anywhere.
        //
        // EarlyExitException       - The parser wants one or more of some construct but there
        //                            were none at all in the input stream. Something like
        //                            X SEMI+
        //
        // MismatchedSetException   - The parser would have accepted any one of two or more
        //                            tokens, but the actual token was not in that set and
        //                            was not a token that we could determine was spurious or
        //                            from which we could determine that we just had a token missing.
        //
        // Other exceptions, and some of the above, are dealt with as generic RecognitionExceptions
        //
        
        // Leadin is always the same apology
        //
        mb.append("Sorry, I was trying to understand ");
        mb.append(posDescription);

        if (e instanceof UnwantedTokenException) {
         
            // We had an extraneous token in the stream, so we have discarded it
            // for error recovery but still need to report it.
            //
            UnwantedTokenException  ute = (UnwantedTokenException) e;
            CommonToken             uwt = (CommonToken)ute.getUnexpectedToken();
            // Inveigh about the extra token
            //
            mb.append(" but I got confused when I found an extra ");
            mb.append(getTokenErrorDisplay(uwt));

            TokenClassification tokenClass = classifyToken(e.token);

            // Don't ramble by repeating things like "...extra identifier, which is an identifier that should not be there"
            //
            if (       tokenClass != TokenClassification.UNKNOWN
                    && tokenClass != TokenClassification.OPERATOR
                    && !(posDescription.equalsIgnoreCase(tokenClass.forHumans()))
               ) {
                mb.append(" which is ");
                mb.append(tokenClass.forHumans());
            }
            
            mb.append(" that should not be there");

            // Work out what our start and end point should be for the error. When we have an extar
            // token in this language, it is quite often because the source code is coming from
            // the net beans (or other) IDE and the user is typing some new definition, viz:
            //
            // var
            // var answer : Integer = 42;
            //
            // In such a case, we would throw the error at the second instance of var, but
            // it is more useful for the IDE if we throw the error at the first instance
            // (for various reasons). Hence we do a check here to see if the prior token is the
            // same type as the current token. If it is, then we report the error with
            // reference to the prior token. Note that we have already consumed the token
            // when we get here bceause this is an error that is not sent back to the parser
            // it is just auto-recovered, so we need to use LA(-2) here.
            //
            if  (uwt.getType() == input.LA(-2)) {

                // Replace the token with the previous token
                //
                uwt = (CommonToken)(input.LT(-2));
                ute.token = uwt;
            }

            sp = uwt.getStartIndex();
            ep = uwt.getStopIndex()+1;

            
        } else if (e instanceof MissingTokenException) {
            
            // We were able to work out that there was just a single token missing
            // and need to report this like that.
            //
            MissingTokenException mte = (MissingTokenException) e;
            
            // Say what we think is missing
            //
            mb.append(" but I got confused because ");

            TokenClassification tokenClass = classifyToken(mte.expecting);
            if  (posDescription.equalsIgnoreCase(tokenClass.forHumans())) {

                mb.append("you seem to have omitted this");
            }
            else if  (mte.expecting == Token.EOF)
            {
                mb.append("I was looking for the end of the script here");
                
            }
            else {
                
                mb.append("you seem to have missed out '");
                mb.append(tokenNames[mte.expecting]);
                mb.append("'");
                
                if (       tokenClass != TokenClassification.UNKNOWN
                        && tokenClass != TokenClassification.OPERATOR
                        && !posDescription.equalsIgnoreCase(tokenClass.forHumans())
                   ) {
                    mb.append(" which is ");
                    mb.append(tokenClass.forHumans());
                }
            
                mb.append(" that should be there");
            }
            
            // The token is missing, so we want to use the char position directly
            // after the previous token and just make it a single character long.
            // This will be the insert point for the missing token, whatever is
            // actually at that position
            //
            sp = semiPos();
            ep = sp+1;

        } else if (e instanceof MismatchedTokenException) {
            
            
            MismatchedTokenException mte = (MismatchedTokenException) e;
            TokenClassification tokenClass = classifyToken(e.token);
            
            mb.append(" but I got confused when I ");

            if  (mte.token.getType() == Token.EOF)
            {
                mb.append("hit the end of the script.");

                // The start and end points come directly from the end of the prior token
                //
                sp = semiPos();
                ep = sp+1;

            } else {

                mb.append("saw ");
                mb.append(getTokenErrorDisplay(e.token));
            

                if (       tokenClass != TokenClassification.UNKNOWN
                        && tokenClass != TokenClassification.OPERATOR
                        && !posDescription.equalsIgnoreCase(tokenClass.forHumans())
                   ) {
                    mb.append(" which is ");
                    mb.append(tokenClass.forHumans());
                }

                // The start and end points come directly from the mismatched token.
                //
                sp = ((CommonToken)mte.token).getStartIndex();
                ep = ((CommonToken)mte.token).getStopIndex()+1;
            }
            
            if (tokenClass == TokenClassification.KEYWORD && mte.expecting == v4Parser.IDENTIFIER) {
                
                mb.append(".\n Perhaps you tried to use a keyword as the name of a variable (use <<keyword>> if you need to do this)");
                
            } else if (mte.expecting != Token.EOF) {

                mb.append(".\n Perhaps you are missing a ");
                mb.append("'" + tokenNames[mte.expecting]+"'");
            }
            else
            {
                mb.append(".\n I was looking for the end of the script here");
            }


            
        } else if (e instanceof NoViableAltException) {
            
            NoViableAltException nvae = (NoViableAltException) e;
            TokenClassification tokenClass = classifyToken(e.token);

            mb.append(" but I got confused when I ");

            if  (nvae.token.getType() == Token.EOF)
            {
                mb.append("hit the end of the script.");

                // The start and end points come directly from the end of the prior token
                //
                sp = semiPos();
                ep = sp+1;

            } else {

                mb.append("saw ");
                mb.append(getTokenErrorDisplay(e.token));


                if (       tokenClass != TokenClassification.UNKNOWN
                        && tokenClass != TokenClassification.OPERATOR
                        && !posDescription.equalsIgnoreCase(tokenClass.forHumans())
                   ) {
                    mb.append(" which is ");
                    mb.append(tokenClass.forHumans());
                }

                if (tokenClass == TokenClassification.KEYWORD && (stackTop.equals("name") || stackTop.equals("identifier"))) {

                    mb.append(".\n Perhaps you tried to use a keyword as the name of a variable (use <<keyword>> if you need to do this)");

                }
                // The start and end points come directly from the mismatched token.
                //
                sp = ((CommonToken)nvae.token).getStartIndex();
                ep = ((CommonToken)nvae.token).getStopIndex()+1;
            }


            
        } else if (e instanceof MismatchedSetException) {

            MismatchedSetException mse = (MismatchedSetException)e;
            
            mb.append(" but I got confused when I saw ");
            mb.append(getTokenErrorDisplay(e.token));
            TokenClassification tokenClass = classifyToken(e.token);
            if (       tokenClass != TokenClassification.UNKNOWN
                    && tokenClass != TokenClassification.OPERATOR
                    && !posDescription.equalsIgnoreCase(tokenClass.forHumans())
               ) {
                mb.append(" which is ");
                mb.append(tokenClass.forHumans());
            }
            mb.append(".\n I was looking for one of: "+ mse.expecting);

             // The start and end points come directly from the mismatched token.
             //
             sp = ((CommonToken)e.token).getStartIndex();
             ep = ((CommonToken)e.token).getStopIndex()+1;

        } else {

             // The start and end points come directly from the mismatched token.
             //
             sp = ((CommonToken)e.token).getStartIndex();
             ep = ((CommonToken)e.token).getStopIndex()+1;

            mb.append( super.getErrorMessage(e, tokenNames) );
        }

        // Having constructed the error string, and decided on our start
        // and end points, then we need to create an erroneous node, which we will
        // eventually supply within the AST, but will also use for logging
        // the error message, so that the diagnostic positions are useful to
        // anyone listening to the diagnostics.
        //
        errorNode = F.at(sp).Erroneous();
        endPos(errorNode, ep);

        // Give back the string
        //
        return  mb.toString();
    }

/**
    public String getTokenErrorDisplay(Token t) {
        return t.toString();
    }
**/
    
    /** 
     * Creates the error/warning message that we need to show users/IDEs when
     * ANTLR has found a parsing error, has recovered from it and is now
     * telling us that a parsing exception occurred.
     * 
     * We call our own override of getErrorMessage, and this will build the
     * a string that is geared towards the F3 author. Then we work out
     * where we are in the character stream and record the error using the
     * F3 infrastructure.
     */
    @Override
    public void displayRecognitionError(String[] tokenNames, RecognitionException e) {

        // Now we build the appropriate error message
        //
        String msg = getErrorMessage(e, getF3TokenNames(tokenNames));
        
        // And record the information using the F3 error sink and the
        // DiagnosticPostion interface of the errorNode, which is created
        // by the getErrorMessage call.
        //
        log.error(errorNode, MsgSym.MESSAGE_F3_GENERALERROR, msg);
    }

    /**
     * Creates the error/warning message that we need to show users/IDEs when
     * ANTLR has found a parsing error, has recovered from it and is now
     * telling us that a parsing exception occurred.
     *
     * We call our own override of getErrorMessage, and this will build the
     * a string that is geared towards the F3 author. Then we work out
     * where we are in the character stream and record the error using the
     * F3 infrastructure.
     */

    public void displayRecognitionError(String[] tokenNames, RecognitionException e, F3Tree node) {

        // Now we build the appropriate error message
        //
        String msg = getErrorMessage(e, getF3TokenNames(tokenNames));

        // And record the information using the F3 error sink and the
        // DiagnosticPostion interface of the supplied node.
        //
        log.error(node, MsgSym.MESSAGE_F3_GENERALERROR, msg);
    }

    /**
     * Provides a reference to the array of human readable descriptions
     * of each token that the lexer can generate.
     * @param tokenNames The names of the tokens as ANTLR sees them
     * @return An array of human readable descriptions indexewd by the ANTLR generated token type (integer)
     */
    protected String[] getF3TokenNames(String[] tokenNames) {
        
        // If we have already generated this array, then we jsut return the
        // reference to it.
        //
        if (f3TokenNames != null) {
            return f3TokenNames;
        } else {
          
            // This is the first request for the array, so we build it
            // on the fly.
            //
            f3TokenNames = new String[tokenNames.length];
            int count = 0;
            for (String tokenName:tokenNames) {
                String f3TokenName = tokenMap.get(tokenName); 
                if (f3TokenName == null) {
                    f3TokenNames[count] = tokenName;
                } else {
                    f3TokenNames[count] = f3TokenName;
                }
                count++;
            }
            
            return f3TokenNames;
        }
    
    }
    /**
     * Calculates the current character position in the input stream.
     * This method skips whitespace tokens by virtue of using LT(1)
     * which automatically skips off channel tokens. Use when there is
     * no token yet exmined in a rule.
     * 
     * @return The character position of the next non-whitespace token in the input stream
     * 
     */
    protected int pos() {
        
        return pos(input.LT(1));
    }
    
    /**
     * Calculates the character position of the first character of the text
     * in the input stream that the supplied token represents.
     * @param tok The token to locate in the input stream
     * @return The character position of the next non-whitespace token in the input stream
     */
    protected int pos(Token tok) {
        
        return ((CommonToken)tok).getStartIndex();
    }
    
    /**
     * Calculates the position in the character stream where a missing
     * semi-colon looks like it ought to have been.
     * 
     * The method is called from the rule that detects that there should have been
     * a semi colon to terminate a statement or expression, hence the input
     * stream will be positioned too far ahead of the position we are looking to report.
     * To find where we should report we need to search backwards in the input stream for
     * the first non-hidden token before the current one, then position after the end of
     * the text that that token represents.
     */
    protected int semiPos() {
        
        CommonToken  tok;
        
        // Traverse backwards until we find a token that is on the default
        // channel.
        //
        tok = (CommonToken)(input.LT(-1));
        
        // If the source consists of just one token, say 'function' then
        // we can actually end up positioned at first token, we get null back if this
        // happens and use the current token instead.
        //
        if (tok == null) {
          
            tok = (CommonToken)(input.LT(1));
        }
        
        // Just in case somethign goes wrong getting ANY token, check for null
        //
        if  (tok == null) {
            return 0;
        }
        // Now, all we need to do is position after the last character of the
        // text that this token represents.
        //
        return tok.getStopIndex() + 1;
    }
    
    /**
     * Associate a documentation comment with a particular AST.
     * 
     * The parser keeps a map off all the AST fragements which it has
     * identified has having a documentation comment. This is the
     * method that creates and builds that list as the parser rules
     * find out associations.
     * 
     * @param tree  The tree or tree fragment with which the documentation comment should be associated.
     * @param comment The comment that has been identified as the documentation comment for this tree.
     */
    void setDocComment(JCTree tree, CommonToken comment) {
        if (comment != null) {

            if (docComments == null) {
                docComments = new HashMap<JCTree,String>();
            }
            docComments.put(tree, comment.getText());
        }
    }
    
    
    /***
     * Given a specific starting token, locate the first non-whitespace token
     * that preceeds it, returning it if it is a comment.
     *
     * A number of syntactical constructs can be preceded by a documentatin COMMENT which 
     * is assocaitaed with the construct and should be placed in the AST. Such comments
     * must begin with the introduceer '/**'.
     * This method scans backwards from the supplied token until it finds a token that is 
     * not considered to be WHITESPACE. If the token is a qualifying COMMENT then it is
     * deemed to belong to the construct that asked to locate the comment and is
     * returned to the caller.
     *
     * @param start The token from whence to search backwards in the token stream.
     * @return null if there is no associated comment, the token that contains the
     *         comment, if there is.
     */
    protected CommonToken getDocComment(Token start) {

        // Locate the position of the token before this one in the input stream
        //
        int index = start.getTokenIndex() - 1;

        // Loop backwards through the token stream until
        // we find a token that is not considered to be whitespace
        // or we reach the start of the token stream.
        //
        while (index >= 0) {
            
            Token tok = input.get(index);
            int type;

            // Because modifiers are dealt with uniformly now, we must ignore
            // them when running backwards looking for comments.
            //
            type = tok.getType();
            if (    type == v4Parser.WS         || type == v4Parser.ABSTRACT    || type == v4Parser.BOUND 
                 || type == v4Parser.DEFAULT    || type == v4Parser.OVERRIDE    || type == v4Parser.PACKAGE
                 || type == v4Parser.PROTECTED  || type == v4Parser.PUBLIC      || type == v4Parser.PUBLIC_READ
                 || type == v4Parser.PUBLIC_INIT || type == v4Parser.MIXIN
                 
                 //TODO: deprecated -- remove this at some point
                 //
                 || type == v4Parser.STATIC
                 || type == v4Parser.LINE_COMMENT
                 || type == v4Parser.COMMENT) {
                
                --index;
                
            } else {
                
                break;
            }
        }

        // Assuming that we have found a valid token (not reached the
        // the token stream start, check to see if that token is a DOC_COMMENT
        // and return null if it is not.
        //
        if (index < 0 || input.get(index).getType() != v4Parser.DOC_COMMENT) {

            return null;
        }

        // We have documentation comment, rather than just a normal comment.
        //
        return (CommonToken) (input.get(index));
    }

    /**
     * Given a list of interpolation values, create an entry for the supplied AST node
     * in the end position map using the end position of the last AST node in the interpolation
     * value list.
     * 
     * @param tree The AST node that we wish to create an endpos for
     * @param list A list of interpolation value AST nodes.
     */
    void endPos(JCTree tree, com.sun.tools.mjavac.util.List<F3InterpolateValue> list) {
        if (genEndPos) {
            int endLast = endPositions.get(list.last());
            endPositions.put(tree, endLast);
        }
    }

    /** Using the current token stream position as the start point
     *  search back through the input token stream and set the end point
     *  of the supplied tree object to the first non-whitespace token
     *  we find.
     * 
     * Note that this version of endPos() is called when all elements of a
     * construct have been parsed. Hence we traverse back from one token
     * before the current index.
     */
    void endPos(JCTree tree) {

        CommonToken tok;

        // Unless we are at the very start, then the token that
        // ended whatever AST fragment we are constructing was the
        // one before the one at the current index and so we need
        // to start at that token.
        //
        tok = (CommonToken)(input.LT(-1));

        if  (tok == null)
        {
            // This can happen if the first thing is a script member
            // declaration and it has no modifiers, modifiers is then
            // starting at 0 and ending at 0
            //
            tok = (CommonToken)(input.LT(1));
            endPos(tree, tok.getStartIndex());

        } else {

            // We have found a token that is non-whitespace and is not BOF
            //
            endPos(tree, tok.getStopIndex() + 1);
        }
     }
    
    /**
     * Create the end position map entry for the given JCTree at the supplied
     * character inde, which is the offset into the script source.
     * 
     * @param tree The tree for which we are mapping the endpoint
     * @param end The character position in the input stream that matches the end of the tree
     */
    void endPos(JCTree tree, int end) {

        // Check that we are not trying to create an endPos that is before the
        // start of the tree. This can happen if we were in error recovery mode from
        // a missing element, and ended up taking the end position of the token
        // in the stream prior to the place where the missing element shoudl be. In that
        // case we are creating an erroneous node and it will be empty of error nodes,
        // so gets an end positon the same as its start position.
        //
        if (tree != null) {
            int start = tree.getStartPosition();
            if (end <= start)
                end = start + 1;
            if (tree instanceof F3Block)
                ((F3Block) tree).endpos = end;
            if (genEndPos) {
                endPositions.put(tree, end);
            }
        }
    }

    /**
     * 
     * @return
     */
    protected List noF3Trees() {
        return List.<F3Tree>nil();
    }
    
    /**
     * Examines the token stream to see if we require a SEMI
     * token to terminate the previous statement, or we do not.
     * 
     * The rules for deciding wheter a SEMI is required here or
     * not are reasonably straight forward:
     * 
     * 1) If the next token is a '}' then we do not required a SEMI
     *    as the last statement of a block does not need to terminate
     *    with a SEMI;
     * 
     * 2) If the next token is EOF then we do not require a SEMI as
     *    the last statement of the script does not require termination;
     * 
     * 3) If the previous token was a '}' then we do not require a SEMI
     *    as brace blocks do not require termination ever.
     * 
     * 4) If the previous token was itself a SEMI then we assume that
     *    the prior statement was terminated correctly.
     * 
     * 5) If the next token is ELSE, then the prior single statement
     *    of a then clause does not require a SEMI.
     *    For instance if (x) a else b;
     * 
     * Note that we always consume a SEMI colon here if there is one
     * as there is never any harm in having too many SEMIs.
     * 
     * @param contextMessage Message context to use when reporting that a required SEMI is missing
     */
    protected void checkForSemi()
    {
         
        Token nextTok       = input.LT(1);
        int   nextTokType  = nextTok.getType();
        
        //System.out.println("Check " + contextMessage);
        //System.out.println(" next token is  '" + nextTok.getText() + "'");
        //System.out.println(" previous token is  '" + input.LT(-1).getText() + "'");
        
        // If there is a SEMI colon next anyway, then we just eat it
        //
        if  (nextTokType == v4Parser.SEMI) {
            // Just consume it and return then
            //
            input.consume();
            return;
        }
        
        // Ignore if next token is something that relaxes the rules
        //
        if  (      nextTokType == v4Parser.RBRACE 
                || nextTokType == Token.EOF
                || nextTokType == v4Parser.ELSE
                || nextTokType == v4Parser.RBRACE_LBRACE_STRING_LITERAL
                || nextTokType == v4Parser.RBRACE_QUOTE_STRING_LITERAL
            ) {
            
            // The SEMI was optional anyway so just return
            //
            return;
        }
        
        // Now we need to know the previous on channel token
        //
        Token prevToken = input.LT(-1);
        
        if  (      prevToken == null
                || prevToken.getType() == v4Parser.RBRACE
                || prevToken.getType() == v4Parser.SEMI
            )
        {
            // We don't require a SEMI after a '}' or after a prior SEMI or if
            // this error occurred on the first token (in whcih case prevToken is null)
            //
            return;
        }

        // OK, having got here, we must require a SEMI and it is missing
        // so issue the error.
        //
        log.error(semiPos(), MsgSym.MESSAGE_F3_SEMI_REQUIRED);
                 
    }
    
    /**
     * If the parser is able to recover from the fact that a single token
     * is missing from the input stream, then it will call this method
     * to manufacture a token for use by actions in the grammar.
     *
     * In general the tokens we will need to manufacture here will be things
     * like identifiers, missing parens and braces and other fairly simple constructs
     * as these can be recognized from the union of follow sets that can be
     * constructed at any one point.
     * 
     * @param input The token stream where we are normally drawing tokens from
     * @param e The exception that was raised by the parser
     * @param expectedTokenType The type of the token that the parser was expecting to see next
     * @param follow The followset of tokens that can follow on from here
     * @return A newly manufactured token of the required type
     */
    @Override
    protected Object getMissingSymbol(IntStream input,
                                      RecognitionException e, 
                                      int expectedTokenType,
                                      BitSet follow) {

        // Used to manufacture the token that we will insert into
        // the input stream
        //
        MissingCommonToken t;
                
        // The token string contents, so that we can make up some sensible
        // error value.
        //
        String tokenText;

        // Pick up the prior token (the one we will return after this
        // manufactured one), and use it to generate position information
        // for our fake token.
        //
        CommonToken current = (CommonToken)((TokenStream)input).LT(-1);
        
        // If there was no next token, then we use the next
        // token as a reference point.
        //
        if ( current.getType() == Token.EOF ) {
		current = (CommonToken)((TokenStream)input).LT(1);
	}
                        
        // Work out what type of token we were expecting so we can 
        // use it in the token next if we need to.
        //
        TokenClassification tokenClass = classifyToken(expectedTokenType);
        
        // When we are manufacturing a token for error recovery, we must intercept
        // a number of token types and create something that can be sensibly used
        // by the F3 AST to indicate that it was in error. Otherwise the AST
        // will appear to be perfectly correct.
        //
        switch (expectedTokenType)
        {
            
            case Token.EOF:
                
                // If we were expection end of file at this point, then
                // there is a little extra work to do.
                //

                tokenText  = "<missing EOF>";
                break;            

            
            case v4Parser.TIME_LITERAL:
            
                // A time literal needs special handling so if anything wants to
                // try and use the value it should contain, then it needs to be
                // some valid default value. Here we use 1 second as a default
                //
                tokenText = "1s";
                break;

            case v4Parser.IDENTIFIER:

                // A missing indentifer, which we adorn with text to make sure
                // it is obvious that it is inserted by this routine, in case
                // checking the class instance is impractical somewhere down stream.
                //
                tokenText = "<missing IDENTIFIER>";
                break;

            // For anything else, we use the default methodology
            //
            default:

                // We create text that is some indication of what was missing
                //
                tokenText = "<missing " + tokenClass.forHumans() + ">";
                break;
        }
        
        // We have created the raw information we need, so now we can 
        // manufacture the token and just return it for inclusion in
        // the input stream.
        //
        t                 = new MissingCommonToken(expectedTokenType, tokenText);
                
        // Use the current/prior token to make up a position for the
        // manufactured one, one character after the end of the previous one.
        //
        t.setLine                   (current.getLine());   
        t.setCharPositionInLine     (current.getStopIndex() + 1);
        t.setChannel                (DEFAULT_TOKEN_CHANNEL);
        
        // Our manufactured token is complete so let's return it
        //
        return t;
    }

    // ----------------------------------------------------------------------
    // Error recovery methods.
    //
    // In the general and most simple cases, ANTLR recognizes will do simple error
    // recovery pretty well, as it will detect things like a single missing token
    // or a single extraneous token. Howeer, its default in other cases is to
    // delete a single token and throw a RecognitionException. It will try to
    // resync the token stream, but if the source is 'very' erroneous, then all
    // it can really do is consume a token and see if that helps.
    //
    // In any particular rule, we have more or less an idea of context and in
    // these cases we take some specific actions for recovery that will resync
    // the input stream to somewhere that is more likely to allow us to carry
    // on parsing.
    //

    /**
     * Called to resync the input stream when we received an exception trying
     * to start the parse of a class member. This happens when the upcoming
     * stream is very erroneous, such as when someone has tried to place
     * soenthing in a class definition that has no business being there, or
     * has left out a critical keyword such as FUNCTION or VAR and we can
     * therefore just not predict what the code is trying to declare.
     *
     * As the class member will be completely out of context, the best thing
     * we can do is resync to the start of another, viable class member definition.
     *
     * @param ruleStart The position in the input stream of the first token that
     *                  spans the elements in error.
     * @param re The exception that the parser threw to get us heer, in case we
     *           can use that information.
     * @return A F3 error node for the AST that spans the start and end of
     *         all the tokesn that we had to discard in order to resync somewhere
     *         sensible.
     */
    protected F3Erroneous resyncClassMember(int ruleStart, RecognitionException re)
    {
        // First lets find out what the follow set is from this particular context
        //
        BitSet follow = computeContextSensitiveRuleFOLLOW();

        // Brace depth for terminating consumption
        //
        int braceDepth = 0;
        
        for (;;) {

            int ttype = input.LA(1);
            boolean consumeNext = false;

            switch(ttype)
            {
                case    Token.EOF:          // Reached end of file, we must stop
                case    v4Parser.INIT:      // Reached an init definition
                case    v4Parser.POSTINIT:  // Reached a post init definition
                case    v4Parser.OVERRIDE:  // Override defintion
                
                    // Any of the modifiers, we must assume to be a new class member
                    // even if they are not allowed here, as they may be just erroneously
                    // specified.
                    //
                case    v4Parser.ABSTRACT:
                case    v4Parser.BOUND:
                case    v4Parser.DEFAULT:
                case    v4Parser.PACKAGE:
                case    v4Parser.PROTECTED:
                case    v4Parser.PUBLIC:
                case    v4Parser.PUBLIC_READ:
                case    v4Parser.PUBLIC_INIT:
                    
                    // Variable declarations and member functions mean we are done consuming
                    //
                case    v4Parser.VAR:
                case    v4Parser.DEF:
                case    v4Parser.CONST:
                case    v4Parser.FUNCTION:

                    // We found a token that looks like it is the start of a new
                    // class member definition, or otherwise somewhere we should
                    // end consumption; so we can break this loop.
                    //
                    consumeNext = false;
                    break;

                case    v4Parser.RBRACE:
                    
                    // A right brace forces us to consider the brace depth.
                    // We assume that there was an opening brace for the class
                    // definition, and that any opening braces being to the
                    // erroneous class member, hence we force loop exit if,
                    // after decrementing the brace level, we get to zero
                    //
                    braceDepth--;
                    if  (braceDepth == 0) {
                        consumeNext = false;
                    }
                    break;
                    
                case    v4Parser.LBRACE:
                    
                    // An opening brace must belong to some constrcut within the erroneous
                    // class member, and so we count it, and consume it.
                    //
                    braceDepth++;
                    consumeNext = true;
                    break;
                    
                default:

                    // The next token was not anythig nwe wanted to sync to, so
                    // just consume it and move on.
                    //
                    consumeNext = true;
                    break;
            }
            
            // Now, are we consuming still, or are we done?
            //
            if  (consumeNext) {
                input.consume();
            } else {
                break;
            }
        }

        // We have resynced to somewhere with a possibilty to recover from
        // So we need to create an erroneous node that covers everything
        // we skipped.
        //
        F3Erroneous errNode = F.at(ruleStart).Erroneous();
        endPos(errNode);

        // The caller will send the AST node to whereever it needs to be
        // in the build structure.
        //
        return errNode;
    }

    /**
     * Called to resync the input stream after we failed to make any sense of
     * what should have been a type such as : String and so on.
     * 
     * Performs customized resynchronization of the input stream and returns
     * either a missing type node or an erroneous node, depending on whether it
     * can make any sense of the error, or just has to resync to the followSet.
     * 
     * @param ruleStart The position in the input stream of the first token that
     *                  spans the elements in error.
     * @param re The exception that the parser threw to get us heer, in case we
     *           can use that information.
     * @return Either a F3 error node for the AST that spans the start and end of
     *         all the tokens that we had to discard in order to resync somewhere
     *         sensible, or a F3MissingType
     */
    protected F3Type resyncType(int ruleStart, RecognitionException re)
    {
        F3Type errNode;
        
    	// If we got an NVA here then basically there was no typeName or typeArgList
	// and so on. We create a missing type is the rule has consumed no tokens
	// as we know that the rule was supposed to match a type and there was nothing
	// there (and we could not manufacture anything it seems.) If we have consumed some
	// tokens, then we create an erroneous node, resync and move on.
	//
	
	if	(re instanceof NoViableAltException)
	{
		// Now create an AST node that represents a missing type, The required entry
		// is of type Name so we use an identifier name that cannot exist in
		// F3, so that IDEs can detect it.
		//
		errNode = F.at(ruleStart).ErroneousType();
		
	} else {
	
		// Perform standard ANTLR recovery.
		//
		recover(input, re);
                errNode = F.at(ruleStart).ErroneousType();
	}

        // Calculate the AST span we have covered
        //
	endPos(errNode);

        return errNode;
    }
    /**
     * Use the current stacked followset to work out the valid tokens that
     * can follow on from the current point in the parse, then recover by
     * eating tokens that are not a member of the follow set we compute.
     *
     * This method is used whenever we wish to force a sync, even though
     * the parser has not yet checked LA(1) for alt selection. This is useful
     * in situations where only a subset of tokens can begin a new construct
     * (such as the start of a new statement in a block) and we want to
     * proactively detect garbage so that the current rule does not exit on
     * on an exception.
     *
     * We could override recover() to make this the default behavior but that
     * is too much like using a sledge hammer to crack a nut. We want finer
     * grained control of the recovery and error mechanisms.
     */
    protected void syncToGoodToken()
    {
        // Compute the followset that is in context whereever we are in the
        // rule chain/stack
        //
         BitSet follow = state.following[state._fsp]; //computeContextSensitiveRuleFOLLOW();

         syncToGoodToken(follow);
    }

    /**
     * Temporary to get around bug in ANTLR 3.1 followSet generation
     */
    protected void syncToGoodClassToken()
    {
        BitSet follow = new BitSet();

        // Normal follow set
        //
        follow.add(v4Parser.INIT);
        follow.add(v4Parser.ABSTRACT);
        follow.add(v4Parser.BOUND);
        follow.add(v4Parser.DEF);
        follow.add(v4Parser.CONST);
        follow.add(v4Parser.DEFAULT);
        follow.add(v4Parser.FUNCTION);
        follow.add(v4Parser.OVERRIDE);
        follow.add(v4Parser.PACKAGE);
        follow.add(v4Parser.POSTINIT);
        follow.add(v4Parser.PRIVATE);
        follow.add(v4Parser.PROTECTED);
        follow.add(v4Parser.PUBLIC);
        follow.add(v4Parser.PUBLIC_INIT);
        follow.add(v4Parser.PUBLIC_READ);
        follow.add(v4Parser.PRIVATE);
        follow.add(v4Parser.RBRACE);
        follow.add(v4Parser.SEMI);
        follow.add(v4Parser.STATIC);
        follow.add(v4Parser.VAR);

        // Additional elements that we want to halt on if syncing.
        //


        
        syncToGoodToken(follow);
    }
    protected void syncToGoodToken(BitSet follow)
    {
        int mark = -1;

        try {

            input.mark();

            // Consume all tokens in the stream until we find a member of the follow
            // set, which means the next production should be guarenteed to be happy.
            //
            while (! follow.member(input.LA(1)) ) {

                if  (input.LA(1) == Token.EOF) {

                    // Looks like we didn't find anything a tall that can help us here
                    // so we need to rewind to where we wer and let normal error handling
                    // bail out.
                    //
                    input.rewind();
                    return;
                }
                input.consume();
            }
        } catch (Exception e) {

          // Just ignore any errors here, we will just let the recognizer
          // try to resync as normal - something must be very screwed.
          //
        }
        finally {

            // Always release the mark we took
            //
            if  (mark != -1) {
                input.release(mark);
            }
        }

    }



 
}
