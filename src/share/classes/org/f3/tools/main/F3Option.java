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

package org.f3.tools.main;

import com.sun.tools.mjavac.util.Log;
import com.sun.tools.mjavac.util.Options;
import java.io.PrintWriter;

/**
 * TODO: describe com.sun.tools.mjavac.main.JavacOption
 *
 * <p><b>This is NOT part of any API supported by Sun Microsystems.
 * If you write code that depends on this, you do so at your own
 * risk.  This code and its internal interfaces are subject to change
 * or deletion without notice.</b></p>
 */
public interface F3Option {

    OptionKind getKind();

    /** Does this option take a (separate) operand? */
    boolean hasArg();

    /** Does argument string match option pattern?
     *  @param arg        The command line argument string.
     */
    boolean matches(String arg);

    /** Process the option (with arg). Return true if error detected.
     */
    boolean process(Options options, String option, String arg);

    /** Process the option (without arg). Return true if error detected.
     */
    boolean process(Options options, String option);
    
    OptionName getName();

    enum OptionKind {
        NORMAL,
        EXTENDED,
        HIDDEN,
    }
    
    /** This class represents an option recognized by the main program
     */
    static class Option implements F3Option {

	/** Option string.
	 */
	OptionName name;

	/** Documentation key for arguments.
	 */
	String argsNameKey;

	/** Documentation key for description.
	 */
	String descrKey;

	/** Suffix option (-foo=bar or -foo:bar)
	 */
	boolean hasSuffix;

	Option(OptionName name, String argsNameKey, String descrKey) {
	    this.name = name;
	    this.argsNameKey = argsNameKey;
	    this.descrKey = descrKey;
	    char lastChar = name.optionName.charAt(name.optionName.length()-1);
	    hasSuffix = lastChar == ':' || lastChar == '=';
	}
	Option(OptionName name, String descrKey) {
	    this(name, null, descrKey);
	}

    @Override
	public String toString() {
	    return name.optionName;
	}

	/** Does this option take a (separate) operand?
	 */
	public boolean hasArg() {
	    return argsNameKey != null && !hasSuffix;
	}

	/** Does argument string match option pattern?
	 *  @param arg        The command line argument string.
	 */
    public boolean matches(String arg) {
	    return hasSuffix ? arg.startsWith(name.optionName) : arg.equals(name.optionName);
	}

	/** Print a line of documentation describing this option, if standard.
	 */
	void help(PrintWriter out) {
	    String s = "  " + helpSynopsis();
	    out.print(s);
	    for (int j = s.length(); j < 29; j++) out.print(" ");
	    Log.printLines(out, Main.getLocalizedString(descrKey));
	}
	String helpSynopsis() {
	    return name +
		(argsNameKey == null ? "" :
		 ((hasSuffix ? "" : " ") +
		  Main.getLocalizedString(argsNameKey)));
	}

	/** Print a line of documentation describing this option, if non-standard.
	 */
	void xhelp(PrintWriter out) {}

	/** Process the option (with arg). Return true if error detected.
	 */
	public boolean process(Options options, String option, String arg) {
        if (options != null)
                options.put(option, arg);
	    return false;
	}

	/** Process the option (without arg). Return true if error detected.
	 */
	public boolean process(Options options, String option) {
	    if (hasSuffix)
		return process(options, name.optionName, option.substring(name.optionName.length()));
	    else
		return process(options, option, option);
	}
        
        public OptionKind getKind() { return OptionKind.NORMAL; }
        
        public OptionName getName() { return name; }
    };

    /** A nonstandard or extended (-X) option
     */
    static class XOption extends Option {
        XOption(OptionName name, String argsNameKey, String descrKey) {
            super(name, argsNameKey, descrKey);
        }
        XOption(OptionName name, String descrKey) {
            this(name, null, descrKey);
        }
        @Override
        void help(PrintWriter out) {}
        @Override
        void xhelp(PrintWriter out) { super.help(out); }
        @Override
            public OptionKind getKind() { return OptionKind.EXTENDED; }
    };

    /** A hidden (implementor) option
     */
    static class HiddenOption extends Option {
        HiddenOption(OptionName name) {
            super(name, null, null);
        }
        HiddenOption(OptionName name, String argsNameKey) {
            super(name, argsNameKey, null);
        }
        HiddenOption(OptionName name, String argsNameKey, String descrKey) {
            super(name, argsNameKey, descrKey);
        }
        @Override
        void help(PrintWriter out) {}
        @Override
        void xhelp(PrintWriter out) {}
        @Override
        public OptionKind getKind() { return OptionKind.HIDDEN; }
    };

    /** A f3c-specific option
     */
    static class F3COption extends Option {
 	F3COption(OptionName name, String argsNameKey, String descrKey) {
            super(name, argsNameKey, descrKey);
        }
        
        @Override
	void help(PrintWriter out) {
	    String s = "  " + helpSynopsis();
	    out.print(s);
	    for (int j = s.length(); j < 29; j++) out.print(" ");
	    Log.printLines(out, Main.getF3LocalizedString(descrKey));
	}
        
        @Override
	String helpSynopsis() {
	    return name +
		(argsNameKey == null ? "" :
		 ((hasSuffix ? "" : " ") +
		  Main.getF3LocalizedString(argsNameKey)));
	}       
    }
}
