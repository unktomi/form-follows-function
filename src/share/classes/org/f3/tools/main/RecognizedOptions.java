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

import com.sun.tools.mjavac.code.Source;
import com.sun.tools.mjavac.code.Type;
import com.sun.tools.mjavac.jvm.Target;
import org.f3.tools.main.F3Option.HiddenOption;
import org.f3.tools.main.F3Option.Option;
import org.f3.tools.main.F3Option.XOption;
import org.f3.tools.util.MsgSym;
import com.sun.tools.mjavac.util.List;
import com.sun.tools.mjavac.util.ListBuffer;
import com.sun.tools.mjavac.util.Log;
import com.sun.tools.mjavac.util.Options;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.EnumSet;
import java.util.Set;
import java.util.StringTokenizer;
import javax.lang.model.SourceVersion;
import static org.f3.tools.main.OptionName.*;

/**
 * TODO: describe com.sun.tools.mjavac.main.RecognizedOptions
 *
 * <p><b>This is NOT part of any API supported by Sun Microsystems.
 * If you write code that depends on this, you do so at your own
 * risk.  This code and its internal interfaces are subject to change
 * or deletion without notice.</b></p>
 */
public class RecognizedOptions {
    
    private RecognizedOptions() {}

    public interface OptionHelper {

        void setOut(PrintWriter out);

        void error(String key, Object... args);

        void printVersion();

        void printFullVersion();

        void printHelp();

        void printXhelp();

        void addFile(File f);

        void addClassName(String s);

    }

    public static class GrumpyHelper implements OptionHelper {
        
        public void setOut(PrintWriter out) {
            throw new IllegalArgumentException();
        }
        
        public void error(String key, Object... args) {
            throw new IllegalArgumentException(Main.getLocalizedString(key, args));
        }
        
        public void printVersion() {
            throw new IllegalArgumentException();
        }
        
        public void printFullVersion() {
            throw new IllegalArgumentException();
        }
        
        public void printHelp() {
            throw new IllegalArgumentException();
        }
        
        public void printXhelp() {
            throw new IllegalArgumentException();
        }
        
        public void addFile(File f) {
            throw new IllegalArgumentException(f.getPath());
        }
        
        public void addClassName(String s) {
            throw new IllegalArgumentException(s);
        }
        
    }

    static Set<OptionName> f3cOptions = EnumSet.of(
        G,
        G_NONE,
        G_CUSTOM,
        XLINT,
        XLINT_CUSTOM,
        NOWARN,
        VERBOSE,
        DEPRECATION,
        CLASSPATH,
        CP,
        SOURCEPATH,
        BOOTCLASSPATH,
        XBOOTCLASSPATH_PREPEND,
        XBOOTCLASSPATH_APPEND,
        XBOOTCLASSPATH,
        EXTDIRS,
        DJAVA_EXT_DIRS,
        ENDORSEDDIRS,
        DJAVA_ENDORSED_DIRS,
        //PROC_CUSTOM,
        //PROCESSOR,
        //PROCESSORPATH,
        D,
        // S,
        IMPLICIT,
        ENCODING,
        SOURCE,
        TARGET,
        PLATFORM,
        VERSION,
        FULLVERSION,
        HELP,
        // A,
        X,
        J,
        MOREINFO,
        WERROR,
        // COMPLEXINFERENCE,
        PROMPT,
        DOE,
        PRINTSOURCE,
        WARNUNCHECKED,
        XMAXERRS,
        XMAXWARNS,
        XSTDOUT,
        XPRINT,
        //XPRINTROUNDS,
        //XPRINTPROCESSORINFO,
        XPREFER,
        O,
        XJCOV,
        XD,
        DUMPJAVA,
        DUMPF3,
        SOURCEFILE);

    static Set<OptionName> javacFileManagerOptions = EnumSet.of(
        CLASSPATH,
        CP,
        SOURCEPATH,
        BOOTCLASSPATH,
        XBOOTCLASSPATH_PREPEND,
        XBOOTCLASSPATH_APPEND,
        XBOOTCLASSPATH,
        EXTDIRS,
        DJAVA_EXT_DIRS,
        ENDORSEDDIRS,
        DJAVA_ENDORSED_DIRS,
        //PROCESSORPATH,
        D,
        // S,
        ENCODING,
	SOURCE);

    static Set<OptionName> javacToolOptions = EnumSet.of(
        G,
        G_NONE,
        G_CUSTOM,
        XLINT,
        XLINT_CUSTOM,
        NOWARN,
        VERBOSE,
        DEPRECATION,
        //PROC_CUSTOM,
        //PROCESSOR,
        IMPLICIT,
        SOURCE,
        TARGET,
        // VERSION,
        // FULLVERSION,
        // HELP,
        // A,
        // X,
        // J,
        MOREINFO,
        WERROR,
        // COMPLEXINFERENCE,
        PROMPT,
        DOE,
        PRINTSOURCE,
        WARNUNCHECKED,
        XMAXERRS,
        XMAXWARNS,
        // XSTDOUT,
        XPRINT,
        //XPRINTROUNDS,
        //XPRINTPROCESSORINFO,
        XPREFER,
        O,
        XJCOV,
        XD);

    static Option[] getJavaCompilerOptions(OptionHelper helper) {
        return getOptions(helper, f3cOptions);
    }

    public static Option[] getJavacFileManagerOptions(OptionHelper helper) {
        return getOptions(helper, javacFileManagerOptions);
    }

    public static Option[] getJavacToolOptions(OptionHelper helper) {
        return getOptions(helper, javacToolOptions);
    }

    static Option[] getOptions(OptionHelper helper, Set<OptionName> desired) {
        ListBuffer<Option> options = new ListBuffer<Option>();
        for (Option option : getAll(helper))
            if (desired.contains(option.getName()))
                options.append(option);
        return options.toArray(new Option[options.length()]);
    }
    
    /**
     * @param out the writer to use for diagnostic output
     */
    public static Option[] getAll(final OptionHelper helper) {
        return new Option[]{
	new Option(G,                       MsgSym.MESSAGE_OPT_G),
	new Option(G_NONE,					MsgSym.MESSAGE_OPT_G_NONE) {
        @Override
	    public boolean process(Options options, String option) {
		options.put("-g:", "none");
		return false;
	    }
	},

	new Option(G_CUSTOM,                MsgSym.MESSAGE_OPT_G_LINES_VARS_SOURCE) {
        @Override
	    public boolean matches(String s) {
		return s.startsWith("-g:");
	    }
        @Override
	    public boolean process(Options options, String option) {
		String suboptions = option.substring(3);
		options.put("-g:", suboptions);
		// enter all the -g suboptions as "-g:suboption"
		for (StringTokenizer t = new StringTokenizer(suboptions, ","); t.hasMoreTokens(); ) {
		    String tok = t.nextToken();
		    String opt = "-g:" + tok;
		    options.put(opt, opt);
		}
		return false;
	    }
	},

	new XOption(XLINT,                  MsgSym.MESSAGE_OPT_XLINT),
	new XOption(XLINT_CUSTOM,           MsgSym.MESSAGE_OPT_XLINT_SUBOPTLIST) {
        @Override
	    public boolean matches(String s) {
		return s.startsWith("-Xlint:");
	    }
        @Override
	    public boolean process(Options options, String option) {
		String suboptions = option.substring(7);
		options.put("-Xlint:", suboptions);
		// enter all the -Xlint suboptions as "-Xlint:suboption"
		for (StringTokenizer t = new StringTokenizer(suboptions, ","); t.hasMoreTokens(); ) {
		    String tok = t.nextToken();
		    String opt = "-Xlint:" + tok;
		    options.put(opt, opt);
		}
		return false;
	    }
	},

	// -nowarn is retained for command-line backward compatibility
	new Option(NOWARN,					MsgSym.MESSAGE_OPT_NOWARN) {
        @Override
		public boolean process(Options options, String option) {
		    options.put("-Xlint:none", option);
		    return false;
		}
	    },

	new Option(VERBOSE,					MsgSym.MESSAGE_OPT_VERBOSE),

	// -deprecation is retained for command-line backward compatibility
	new Option(DEPRECATION,             MsgSym.MESSAGE_OPT_DEPRECATION) {
        @Override
		public boolean process(Options options, String option) {
		    options.put("-Xlint:deprecation", option);
		    return false;
		}
	    },

	new F3Option.F3COption(CLASSPATH,               MsgSym.MESSAGE_OPT_ARG_PATH,	MsgSym.MESSAGE_OPT_CLASSPATH),
	new F3Option.F3COption(CP,                      MsgSym.MESSAGE_OPT_ARG_PATH,	MsgSym.MESSAGE_OPT_CLASSPATH) {
        @Override
	    public boolean process(Options options, String option, String arg) {
		return super.process(options, "-classpath", arg);
	    }
	},
	new Option(SOURCEPATH,              MsgSym.MESSAGE_OPT_ARG_PATH,	MsgSym.MESSAGE_OPT_SOURCEPATH),
	new Option(BOOTCLASSPATH,           MsgSym.MESSAGE_OPT_ARG_PATH,	MsgSym.MESSAGE_OPT_BOOTCLASSPATH) {
        @Override
	    public boolean process(Options options, String option, String arg) {
	 	options.remove("-Xbootclasspath/p:");
	 	options.remove("-Xbootclasspath/a:");
		return super.process(options, option, arg);
	    }
	},
	new XOption(XBOOTCLASSPATH_PREPEND,MsgSym.MESSAGE_OPT_ARG_PATH, MsgSym.MESSAGE_OPT_XBOOTCLASSPATH_P),
	new XOption(XBOOTCLASSPATH_APPEND, MsgSym.MESSAGE_OPT_ARG_PATH, MsgSym.MESSAGE_OPT_XBOOTCLASSPATH_A),
	new XOption(XBOOTCLASSPATH,        MsgSym.MESSAGE_OPT_ARG_PATH, MsgSym.MESSAGE_OPT_BOOTCLASSPATH) {
        @Override
	    public boolean process(Options options, String option, String arg) {
	 	options.remove("-Xbootclasspath/p:");
	 	options.remove("-Xbootclasspath/a:");
	 	return super.process(options, "-bootclasspath", arg);
	    }
	},
	new Option(EXTDIRS,                 MsgSym.MESSAGE_OPT_ARG_DIRS,	MsgSym.MESSAGE_OPT_EXTDIRS),
	new XOption(DJAVA_EXT_DIRS,         MsgSym.MESSAGE_OPT_ARG_DIRS,	MsgSym.MESSAGE_OPT_EXTDIRS) {
        @Override
	    public boolean process(Options options, String option, String arg) {
	 	return super.process(options, "-extdirs", arg);
	    }
	},
	new Option(ENDORSEDDIRS,            MsgSym.MESSAGE_OPT_ARG_DIRS,	MsgSym.MESSAGE_OPT_ENDORSEDDIRS),
	new XOption(DJAVA_ENDORSED_DIRS,    MsgSym.MESSAGE_OPT_ARG_DIRS,	MsgSym.MESSAGE_OPT_ENDORSEDDIRS) {
        @Override
	    public boolean process(Options options, String option, String arg) {
	 	return super.process(options, "-endorseddirs", arg);
	    }
	},
	new HiddenOption(PROC_CUSTOM,             MsgSym.MESSAGE_OPT_PROC_NONE_ONLY) {
        @Override
	    public boolean matches(String s) {
		return s.equals("-proc:none") || s.equals("-proc:only");
	    }

        @Override
	    public boolean process(Options options, String option) {
		if (option.equals("-proc:none")) {
		    options.remove("-proc:only");
		} else {
		    options.remove("-proc:none");
		}
		options.put(option, option);
		return false;
	    }
        },
	new HiddenOption(PROCESSOR,           MsgSym.MESSAGE_OPT_ARG_CLASS_LIST,	MsgSym.MESSAGE_OPT_PROCESSOR),
	new HiddenOption(PROCESSORPATH,       MsgSym.MESSAGE_OPT_ARG_PATH,		MsgSym.MESSAGE_OPT_PROCESSORPATH),
	new Option(D,                   MsgSym.MESSAGE_OPT_ARG_DIRECTORY,	MsgSym.MESSAGE_OPT_D),
	new Option(S,                   MsgSym.MESSAGE_OPT_ARG_DIRECTORY,	MsgSym.MESSAGE_OPT_SOURCE_DEST),
        new Option(IMPLICIT,                                    "opt.implicit") {
            @Override
            public boolean matches(String s) {
                return s.equals("-implicit:none") || s.equals("-implicit:class");
            }
            @Override
            public boolean process(Options options, String option, String operand) {
                int sep = option.indexOf(":");
                options.put(option.substring(0, sep), option.substring(sep+1));
                options.put(option,option);
                return false;
            }
        },
	new Option(ENCODING,            MsgSym.MESSAGE_OPT_ARG_ENCODING,	MsgSym.MESSAGE_OPT_ENCODING),
	//new Option(SOURCE,              MsgSym.MESSAGE_OPT_ARG_RELEASE,     MsgSym.MESSAGE_OPT_SOURCE) {
	new HiddenOption(SOURCE,              MsgSym.MESSAGE_OPT_ARG_RELEASE,     MsgSym.MESSAGE_OPT_SOURCE) {
        @Override
	    public boolean process(Options options, String option, String operand) {
		Source source = Source.lookup(operand);
		if (source == null) {
		    helper.error(MsgSym.MESSAGE_ERR_INVALID_SOURCE, operand);
		    return true;
		}
		return super.process(options, option, operand);
	    }
	},
	new Option(TARGET,              MsgSym.MESSAGE_OPT_ARG_RELEASE,	MsgSym.MESSAGE_OPT_TARGET) {
        @Override
	    public boolean process(Options options, String option, String operand) {
		Target target = Target.lookup(operand);
		if (target == null) {
		    helper.error(MsgSym.MESSAGE_ERR_INVALID_TARGET, operand);
		    return true;
		}
		return super.process(options, option, operand);
	    }
	},
	new F3Option.F3COption(PLATFORM,	MsgSym.MESSAGE_F3_OPT_ARG_NAME,	MsgSym.MESSAGE_F3_OPT_PLATFORM) {
	},
	new Option(VERSION,					MsgSym.MESSAGE_OPT_VERSION) {
        @Override
	    public boolean process(Options options, String option) {
                helper.printVersion();
		return super.process(options, option);
	    }
	},
	new HiddenOption(FULLVERSION) {
        @Override
	    public boolean process(Options options, String option) {
                helper.printFullVersion();
		return super.process(options, option);
	    }
	},
	new Option(HELP,					MsgSym.MESSAGE_OPT_HELP) {
        @Override
	    public boolean process(Options options, String option) {
            helper.printHelp();
            return super.process(options, option);
	    }
	},
	new Option(X,   					MsgSym.MESSAGE_OPT_X) {
        @Override
	    public boolean process(Options options, String option) {
            helper.printXhelp();
            return super.process(options, option);
	    }
	},

	// This option exists only for the purpose of documenting itself.
	// It's actually implemented by the launcher.
	new Option(J,                       MsgSym.MESSAGE_OPT_ARG_FLAG,    MsgSym.MESSAGE_OPT_J) {
        @Override
	    String helpSynopsis() {
            hasSuffix = true;
            return super.helpSynopsis();
	    }
        @Override
	    public boolean process(Options options, String option) {
            throw new AssertionError
                ("the -J flag should be caught by the launcher.");
	    }
	},

	// stop after parsing and attributing.
	// new HiddenOption("-attrparseonly"),

	// new Option("-moreinfo",					"opt.moreinfo") {
	new HiddenOption(MOREINFO) {
        @Override
	    public boolean process(Options options, String option) {
		Type.moreInfo = true;
		return super.process(options, option);
	    }
	},

	// treat warnings as errors
	new HiddenOption(WERROR),

	// use complex inference from context in the position of a method call argument
	new HiddenOption(COMPLEXINFERENCE),

	// generare source stubs
	// new HiddenOption("-stubs"),

	// relax some constraints to allow compiling from stubs
	// new HiddenOption("-relax"),

	// output source after translating away inner classes
	// new Option("-printflat",				"opt.printflat"),
	// new HiddenOption("-printflat"),

	// display scope search details
	// new Option("-printsearch",				"opt.printsearch"),
	// new HiddenOption("-printsearch"),

	// prompt after each error
	// new Option("-prompt",					"opt.prompt"),
	new HiddenOption(PROMPT),

	// dump stack on error
	new HiddenOption(DOE),

	// output source after type erasure
	// new Option("-s",					"opt.s"),
	new HiddenOption(PRINTSOURCE),

	// output shrouded class files
	// new Option("-scramble",				"opt.scramble"),
	// new Option("-scrambleall",				"opt.scrambleall"),

	// display warnings for generic unchecked operations
	new HiddenOption(WARNUNCHECKED) {
        @Override
	    public boolean process(Options options, String option) {
		options.put("-Xlint:unchecked", option);
		return false;
	    }
	},

	new XOption(XMAXERRS,   	MsgSym.MESSAGE_OPT_ARG_NUMBER,	MsgSym.MESSAGE_OPT_MAXERRS),
	new XOption(XMAXWARNS,  	MsgSym.MESSAGE_OPT_ARG_NUMBER,	MsgSym.MESSAGE_OPT_MAXWARNS),
	new XOption(XSTDOUT,		MsgSym.MESSAGE_OPT_ARG_FILE,    MsgSym.MESSAGE_OPT_XSTDOUT) {
        @Override
	    public boolean process(Options options, String option, String arg) {
		try {
		    helper.setOut(new PrintWriter(new FileWriter(arg), true));
		} catch (java.io.IOException e) {
		    helper.error(MsgSym.MESSAGE_ERR_ERROR_WRITING_FILE, arg, e);
		    return true;
		}
		return super.process(options, option, arg);
	    }
	},

	new XOption(XPRINT,   					MsgSym.MESSAGE_OPT_PRINT),

	new HiddenOption(XPRINTROUNDS,   			MsgSym.MESSAGE_OPT_PRINT_ROUNDS),

	new HiddenOption(XPRINTPROCESSORINFO,		MsgSym.MESSAGE_OPT_PRINT_PROCESSOR_INFO),
        
    new XOption(XPREFER,                MsgSym.MESSAGE_OPT_PREFER) {
        @Override
        public boolean matches(String s) {
            return s.equals("-Xprefer:source") || s.equals("-Xprefer:newer");
        }
        @Override
        public boolean process(Options options, String option, String operand) {
            int sep = option.indexOf(":");
            options.put(option.substring(0, sep), option.substring(sep+1));
            options.put(option,option);
            return false;
        }
    },

	/* -O is a no-op, accepted for backward compatibility. */
	new HiddenOption(O),

	/* -Xjcov produces tables to support the code coverage tool jcov. */
	new HiddenOption(XJCOV),

	/* This is a back door to the compiler's option table.
	 * -XDx=y sets the option x to the value y.
	 * -XDx sets the option x to the value x.
	 */
	new HiddenOption(XD) {
	    String s;
        @Override
	    public boolean matches(String s) {
		this.s = s;
		return s.startsWith(name.optionName);
	    }
        @Override
	    public boolean process(Options options, String option) {
		s = s.substring(name.optionName.length());
		int eq = s.indexOf('=');
		String key = (eq < 0) ? s : s.substring(0, eq);
		String value = (eq < 0) ? s : s.substring(eq+1);
		options.put(key, value);
		return false;
	    }
	},

        // F3c-specific options
        new HiddenOption(DUMPJAVA),
        new HiddenOption(DUMPF3),
        
        /*
	 * TODO: With apt, the matches method accepts anything if
	 * -XclassAsDecls is used; code elsewhere does the lookup to
	 * see if the class name is both legal and found.
	 *
	 * In apt, the process method adds the candiate class file
	 * name to a separate list.
	 */
	new HiddenOption(SOURCEFILE) {
	    String s;
        @Override
	    public boolean matches(String s) {
		this.s = s;
		return s.endsWith(".f3");  // F3 source file
	    }
        @Override
	    public boolean process(Options options, String option) {
		if (s.endsWith(".f3") ) {
                    File f = new File(s);
                    if (!f.exists()) {
                        helper.error(MsgSym.MESSAGE_ERR_FILE_NOT_FOUND, f);
                        return true;
                    }
                    if (!f.isFile()) {
                        helper.error(MsgSym.MESSAGE_ERR_FILE_NOT_FILE, f);
                        return true;
                    }
                    helper.addFile(f);
                }
		else
                    helper.addClassName(s);
		return false;
	    }
	},
    };        
    }
    
}
