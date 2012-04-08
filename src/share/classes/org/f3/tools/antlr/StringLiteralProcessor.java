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

import com.sun.tools.mjavac.util.Log;
import org.f3.tools.util.MsgSym;

/**
 * Convert escapes in string literals
 * 
 * @author Robert Field
 * 
 * Stolen liberally from the javac Scanner
 */

public class StringLiteralProcessor {

    private final int basepos;
    
    /** A character buffer for literals.
     */
    private char[] sbuf;
    private int sp;

    /** The input buffer, index of next chacter to be read,
     *  index of one past last character in buffer.
     */
    private final char[] buf;
    private int bp;
    private final int buflen;
    
    /** The current character.
     */
    private char ch;

    /** The buffer index of the last converted unicode character
     */
    private int unicodeConversionBp = -1;

    /** The log to be used for error reporting.
     */
    private final Log log;

    public static String convert(Log log, int pos, String str) {
        StringLiteralProcessor slp = new StringLiteralProcessor(log, pos, str);
        return slp.convert();
    }
    
    private StringLiteralProcessor(Log log, int pos, String str) {
        this.log = log;
        this.basepos = pos;
    	this.buf = str.toCharArray();
    	this.buflen = buf.length - 1;  // skip trailing quote
    	this.bp = 0; // scanChar pre-increments so we skip the leading quote
    	this.sbuf = new char[buflen];
    	this.sp = 0;
    }
    
    private String convert() {
        scanChar();
        while (bp < buflen) {
            scanLitChar();
        }
        return new String(sbuf, 0, sp);
    }

    /** Report an error at the given position using the provided arguments.
     */
    private void lexError(String key, Object... args) {
        log.error(basepos+bp-1-buflen, key, args);
    }

    /** Convert an ASCII digit from its base (8, 10, or 16)
     *  to its value.
     */
    private int digit(int base) {
        char c = ch;
        int result = Character.digit(c, base);
        if (result >= 0 && c > 0x7f) {
            lexError(MsgSym.MESSAGE_ILLEGAL_NONASCII_DIGIT);
            ch = "0123456789abcdef".charAt(result);
        }
        return result;
    }


    /** Append a character to sbuf.
     */
    private void putChar(char ch) {
        if (sp == sbuf.length) {
            char[] newsbuf = new char[sbuf.length * 2];
            System.arraycopy(sbuf, 0, newsbuf, 0, sbuf.length);
            sbuf = newsbuf;
        }
        sbuf[sp++] = ch;
    }

    /** Convert unicode escape; bp points to initial '\' character
     *  (Spec 3.3).
     */
    private void convertUnicode() {
        if (ch == '\\' && unicodeConversionBp != bp) {
            bp++; ch = buf[bp];
            if (ch == 'u') {
                do {
                    bp++; ch = buf[bp];
                } while (ch == 'u');
                int limit = bp + 3;
                if (limit < buflen) {
                    int d = digit(16);
                    int code = d;
                    while (bp < limit && d >= 0) {
                        bp++; ch = buf[bp];
                        d = digit(16);
                        code = (code << 4) + d;
                    }
                    if (d >= 0) {
                        ch = (char)code;
                        unicodeConversionBp = bp;
                        return;
                    }
                }
                lexError(MsgSym.MESSAGE_ILLEGAL_UNICODE_ESC);
            } else {
                bp--;
                ch = '\\';
            }
        }
    }

    /** Read next character.
     */
    private void scanChar() {
        ch = buf[++bp];
        if (ch == '\\') {
            convertUnicode();
        }
    }

    /** Read next character in character or string literal and copy into sbuf.
     */
    private void scanLitChar() {
        if (ch == '\\') {
            if (buf[bp+1] == '\\' && unicodeConversionBp != bp) {
                bp++;
                putChar('\\');
                scanChar();
            } else {
                scanChar();
                switch (ch) {
                case '0': case '1': case '2': case '3':
                case '4': case '5': case '6': case '7':
                    char leadch = ch;
                    int oct = digit(8);
                    scanChar();
                    if ('0' <= ch && ch <= '7') {
                        oct = oct * 8 + digit(8);
                        scanChar();
                        if (leadch <= '3' && '0' <= ch && ch <= '7') {
                            oct = oct * 8 + digit(8);
                            scanChar();
                        }
                    }
                    putChar((char)oct);
                    break;
                case 'b':
                    putChar('\b'); scanChar(); break;
                case 't':
                    putChar('\t'); scanChar(); break;
                case 'n':
                    putChar('\n'); scanChar(); break;
                case 'f':
                    putChar('\f'); scanChar(); break;
                case 'r':
                    putChar('\r'); scanChar(); break;
                case '\'':
                    putChar('\''); scanChar(); break;
                case '\"':
                    putChar('\"'); scanChar(); break;
                case '{':
                    putChar('{'); scanChar(); break;
                case '}':
                    putChar('}'); scanChar(); break;
                case '\\':
                    putChar('\\'); scanChar(); break;
                default:
                    lexError(MsgSym.MESSAGE_ILLEGAL_ESC_CHAR);
                }
            }
        } else if (bp != buflen) {
            putChar(ch); scanChar();
        }
    }
}
