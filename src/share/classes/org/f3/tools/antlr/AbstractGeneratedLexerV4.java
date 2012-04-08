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

import com.sun.tools.mjavac.util.Convert;
import com.sun.tools.mjavac.util.Log;
import org.f3.tools.util.MsgSym;
import org.antlr.runtime.*;

/**
 * Base class for ANTLR generated parsers
 *
 * @author Robert Field
 * @author Zhiqun Chen
 */
public abstract class AbstractGeneratedLexerV4 extends org.antlr.runtime.Lexer {

    /**
     * The log to be used for error diagnostics.
     */
    protected Log log;

    /**
     * Initial value of the brace quote tracker provides a door stop for leaving
     * quotes altogether (not yet processing any {} expressions).
     */
    private final BraceQuoteTracker NULL_BQT = new BraceQuoteTracker(null, '\'', false);
    
    /**
     * Tracks the level of nested {} that the lexer is currently processing
     * within. 
     */
    private BraceQuoteTracker quoteStack = NULL_BQT;

    // quote context --
    static final int CUR_QUOTE_CTX	= 0;	// 0 = use current quote context
    static final int SNG_QUOTE_CTX	= 1;	// 1 = single quote quote context
    static final int DBL_QUOTE_CTX	= 2;	// 2 = double quote quote context
    
    // Recorded start of string with embedded expression
    //
    int	eStringStart = 0;
    
    /**
     * Construct a new F3 lexer with no pre-known input stream
     */
    protected AbstractGeneratedLexerV4() {
    }

    /**
     * Construct a new F3 lexer installing the character stream at
     * the same time.
     * 
     * @param input The character stream that the lexer will scan, which should already
     *              be opened and initialized.
     */
    protected AbstractGeneratedLexerV4(CharStream input) {
        super(input);
    }

    /**
     * Construct a new F3 lexer installing the character stream and
     * the shared state (used if there is more than one lexer, which is currently
     * not used by the F3 compiler) at the same time.
     * 
     * @param input The character stream that the lexer will scan, which should already
     *              be opened and initialized.
     * @param state The lexer state object that was created by a previously created lexer
     * 
     */
    protected AbstractGeneratedLexerV4(CharStream input, RecognizerSharedState state) {
        super(input, state);
    }

    /**
     * Used in lexer rule actions to process the characters scanned to match a literal string.
     * 
     * Converts the literal string by removing bounding delimiters such as "xxx" 'xxx' "xxx{
     * and so on, to yield xxx. Then sets the converted text to be the text associated
     * with the lexer token that is currently being processed (and from whence this
     * method is called.
     */
    void processString() {
        setText(StringLiteralProcessor.convert(log, getCharIndex(), getText()));
    }

    /**
     * Used in lexer rule actions to create a literal string conforming to
     * the format string of F3 compound string: %pattern -> internal representation.
     */
    void processFormatString() {
        
        // Add quote characters and adjust the index to invoke StringLiteralProcessor.convert().
        //
        StringBuilder sb = new StringBuilder();
        sb.append('"').append(getText()).append('"');
        setText(StringLiteralProcessor.convert(log, getCharIndex() + 1, sb.toString()));
    }

    /**
     * Called from lexer rule actions to convert the external form of a string literal
     * translation key into the internal form.
     */
    void processTranslationKey() {
        String text = getText().substring(2); // remove '##'
        if (text.length() > 0) {
            text = StringLiteralProcessor.convert(log, getCharIndex(), text);
        }
        setText(text);
    }


    /**
     * Called by lexer rule actions when the lexer detects a '{' within a literal string
     * and has worked out whether a format '%xxx' will follow.
     * @param quote The type of literal string quite " or '
     * @param nextIsPercent Whether there is a following %format string
     */
    protected void enterBrace(int quote, boolean nextIsPercent) {
        quoteStack.enterBrace(quote, nextIsPercent);
    }

    /**
     * Called by lexer rule actions when the lexer detects a ' or " that
     * closes a literal string (which means '{' no longer indicate
     * string embedded expressions.
     */
    protected void leaveQuote() {
        quoteStack.leaveQuote();
    }

    /**
     * Used in the lexer as a gated semantic predicate to indicate whether
     * a right brace '}' is currently expcted to indicate closure of
     * an embedded string literal expression or not.
     * @param quote The type of quote ' or " that we are looking to see if the } is embedded within
     * @return true indicates that the right brace should be seen as ending an embedded
     *              expression within the quoted literal string type indicated by the quote
     *              parameter. false indicates that the brace is just a brace, for say block
     *              closure.
     */
    protected boolean rightBraceLikeQuote(int quote) {
        return quoteStack.rightBraceLikeQuote(quote);
    }

    /**
     * Called by the lexer rules to indicate that we have found a '}' within
     * a literal string and are therefore exiting one level of 
     * nested expression depth.
     */
    protected void leaveBrace() {
        quoteStack.leaveBrace();
    }

    /**
     * Used as a gated semantic predicate by the lexer to decide if the
     * '%' it is about to scan is the introducer to the format string 
     * for an embdedded string expression or not.
     * @return true '%' is starting a string format specficaier
     *         false '%' is just a '%'
     */
    protected boolean percentIsFormat() {
        return quoteStack.percentIsFormat();
    }

    /**
     * Called by the lexer rules after a '%' has been recognized
     * as a format string specifier and therfore any more '%' are not
     * indicating a format string.
     */
    protected void resetPercentIsFormat() {
        quoteStack.resetPercentIsFormat();
    }

    /**
     * Returns and indicator of what level of nested expressions
     * the lexer is currently within.
     * @return 0 - start ste, no expressions are active. >1 indicates
     *             the level of nesting that the lexer is currently processing.
     */
    protected int getLexicalState() {
        return quoteStack.getLexicalState();
    }


    /**
     * Overrides the standard ANTLR 3.1 lexer error message generator
     * to provide a message that will make more sense to F3 programmers.
     * @param e The exception that the lexer raised because it could not decode
     *          what to do next.
     * @param tokenNames The ANTLR supplied list of token names as used in the lexer.
     * @return The string that shuold be used as the error message by the F3 compiler
     */
    @Override
    public String getErrorMessage(RecognitionException e, String[] tokenNames) {

        StringBuffer mb = new StringBuffer();
        
        // No viable alt means that somehow the lexer rule or the
        // lexer itself found a character that cannot match any
        // decisions points. In theory, as of the v4 lexer, this cannot
        // happen unless something went wrong in the gramamr analysis.
        // However, because there are predicates used for embedded string
        // expressions, and this can play with the analysis, we cater for it
        // anyway.
        //
        if (e instanceof NoViableAltException) {
            
            if (e.c == Token.EOF) {
                
                // Changes in the v4 lexer mean that it shoudl be virtually impossible
                // to trigger this error. However it is perhaps possible if the lexer
                // predicts a token, tries to match it and discovers EOF because this
                // is a file produced on Windows and has no terminating \n. Hence
                // we look for this EOF sceanrio and report it nicely.
                //
                mb.append("Sorry, I scanned to the end of your script from around line " + e.line + " but could not see how to process it. ");
                mb.append("This can happen if you forget a closing delimiter such as ''' '\"' or '{'");
            
            } else {
                
                // We managed to predict some lexer token that once we started
                // down the path, turned out not to be what we thought it was.
                // With the v4 lexer, this shoudl not be happening, but this message
                // is used as belt and braces protection.
                //
                mb.append("Sorry, but the character " + getCharErrorDisplay(e.c));
                mb.append("is not allowed in a F3. Well at least, not here.");
            }
        
        } else {
            
            // Any other kind of exception is something we cannot really deal with 
            // here. So we gather ANTLR's assessment of the error state and
            // use that. 
            //
            mb.append(super.getErrorMessage(e, tokenNames));
        }
        
        return mb.toString();
    }


    /**
     * Override for the ANTLR 3.x message display routine so that we can log
     * errors within the F3 compiler infrastructure.
     * 
     * @param tokenNames ANTLR provided array of the lexer token names
     * @param e The excpetion that was raised by the lexer, for further action.
     */
    @Override
    public void displayRecognitionError(String[] tokenNames, RecognitionException e) {

        // Find out how we wish to describe this expcetion to the script author/user
        //
        String msg = getErrorMessage(e, tokenNames);
        
        // Record the error for later output or capture by development tools
        //
        log.error(getCharIndex(), MsgSym.MESSAGE_F3_GENERALERROR, msg);
    }
    
    protected boolean checkIntLiteralRange(String text, int pos, int radix, boolean negative) {
        // Because Long.MIN_VALUE < -Long.MAX_VALUE we need to use the actual negative when present
        //
        String checkText = negative? "-" + text : text;

        // Correct start position for error display
        //
        pos = pos - checkText.length();

        try {

            Convert.string2long(checkText, radix);

        } catch (Exception e) {
       
            // Number form was too outrageous even for the converter
            //
            log.error(pos, MsgSym.MESSAGE_F3_LITERAL_OUT_OF_RANGE, "Long", checkText);

            return false;
        }

        return true;
    }

    protected boolean checkColorString(String text, int pos) {
        // valid strings: #rgb, #rrggbb, #rgb|a, or #rrggbb|aa
        int total = text.length();
        int length = 0;
        int dividerLoc = -1;
        boolean valid = true;
        for (int i = 1; i < total; i++) {
            if (text.charAt(i) == '|') {
                if (dividerLoc != -1) valid = false;
                dividerLoc = i;
            } else {
                length++;
            }
        }

        valid &= (length == 3 || length == 6) && dividerLoc == -1
              || length == 4 && dividerLoc == 4
              || length == 8 && dividerLoc == 7;

        if (!valid) {
            log.error(pos, MsgSym.MESSAGE_F3_COLOR_WRONG_FORMAT, text);
        }
        return valid;
    }


    /**
     * Tracker for the quotes and braces used to define embedded expressions within literal strings
     * such as "He{"l{"l"}o"} world".
     */
    protected class BraceQuoteTracker {
        
        /**
         * How many levels deep is this instance, within nests such as {{{{{{}}}}}}
         */
        private int braceDepth;
        
        /**
         * Which quote is this instance tracking: ' or "
         */
        private char quote;
        
        /**
         * Indicates whether, at this tracking level and in the current
         * lexing state, a following '%' should be seen as introducing a
         * string formatting specification or just as a normal '%' character.
         */
        private boolean percentIsFormat;
        
        /**
         * Tracks the tracker instance prior to this instance of the tracker.
         */
        private BraceQuoteTracker next;

        /**
         * Constructs a new instance of the tracker, and stores a reference
         * to the provided current instance on the tracker stack.
         * 
         * @param prev
         * @param quote
         * @param percentIsFormat
         */
        private BraceQuoteTracker(BraceQuoteTracker prev, char quote, boolean percentIsFormat) {
            this.quote             = quote;
            this.percentIsFormat   = percentIsFormat;
            this.braceDepth        = 1;
            this.next              = prev;
        }

        /**
         * Causes a new instance of the tracker class to be created then placed at the
         * top of the tracking stack, with a reference to the current tracking instance
         * @param quote Type of quoteed string " ' that we are tracking within
         * @param percentIsFormat Whether we should expect a format specification or not
         */
        void enterBrace(int quote, boolean percentIsFormat) {
            if (quote == 0) {  // exisiting string expression or non string expression
                if (quoteStack != NULL_BQT) {
                    ++quoteStack.braceDepth;
                    quoteStack.percentIsFormat = percentIsFormat;
                }
            }
            else {
                quoteStack = new BraceQuoteTracker(quoteStack, (char) quote, percentIsFormat); // push
            }
        }

        /**
         * Called to indicate that we are leaving teh current nested brace level
         * and find out what type of quoted string we are popping back in to.
         *
         * @return The type of quite " or ' that we are re-entering.
         */
        char leaveBrace() {
            if (quoteStack != NULL_BQT && --quoteStack.braceDepth == 0) {
                return quoteStack.quote;
            }
            return 0;
        }

        /**
         * Retuns true if the right brace '}' is currently seen as ending an embedded expression.
         * @param quote Teh type of quoted literal string that the lexer is currently traversing.
         * @return true - use } to end an expression. false - we were not looking to end an expression.
         */
        boolean rightBraceLikeQuote(int quote) {
            return quoteStack != NULL_BQT && quoteStack.braceDepth == 1 && (quote == 0 || quoteStack.quote == (char) quote);
        }

        /**
         * Called to indicate that the lexer has matched the closing quote of a literal
         * string.
         */
        void leaveQuote() {
            assert (quoteStack != NULL_BQT && quoteStack.braceDepth == 0);
            quoteStack = quoteStack.next; // pop
        }

        /**
         * Called to indicate if the lexer shoudl see '%' as teh start of a
         * format specification, or not.
         * 
         * @return true - the upcoming '%' is a format. false, the upcoming '%' is not a format.
         */
        boolean percentIsFormat() {
            return quoteStack != NULL_BQT && quoteStack.percentIsFormat;
        }

        /**
         * Called by the lexer to indicate that it knows that any upcoming '%' cannot
         * possibly be the introducer for a format specification.
         */
        void resetPercentIsFormat() {
            quoteStack.percentIsFormat = false;
        }

        /**
         * Calleld to find out if the lexer is currently scanning with brace quotes or not.
         * @return true if the lexer is traversing an embedded brace delimited expression and;
         *         false if it is not.
         */
        boolean inBraceQuote() {
            return quoteStack != NULL_BQT;
        }

        /** 
         * Encode the lexical state into an integer, to permit incremental lexing in IDEs that support it 
         * @return Level of emdedded 
         */
        int getLexicalState() {
            // This is a hack -- state is not invertible yet
            return (quoteStack == NULL_BQT) ? 0 : quoteStack.braceDepth;

        }
    }
}
