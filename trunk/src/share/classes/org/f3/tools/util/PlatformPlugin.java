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

package org.f3.tools.util;

import com.sun.tools.mjavac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.Options;
import com.sun.tools.mjavac.util.ListBuffer;
import com.sun.tools.mjavac.util.Log;

/**
 * F3 compiler platform plug-in.
 */
public abstract class PlatformPlugin
{
    /** Service name of platform plug-in. */
    public final static String SERVICE = 
        "org.f3.tools.util.PlatformPlugin";

    /** Error messages of platform plug-in. */
    public final static String MESSAGE = 
        "org.f3.tools.resources.platformplugin";

    /**
     * The context key for the platform plugin. 
     */
    public static final Context.Key<PlatformPlugin> pluginKey =
        new Context.Key<PlatformPlugin>();

    /**
     * Get the PlatformPlugin instance for this context. 
     *
     * @param context The compiler context.
     * @return An instance of the PlatformPlugin or <code>null</code> if plugin 
     *   had not been loaded.
     */
    public static PlatformPlugin instance(Context context) {
        PlatformPlugin instance = context.get(pluginKey);
        return instance;
    }

    /**
     * Returns <code>true</code> if platform string identifies supported platform.
     *
     * @param platform Target platform identifier string.
     * @return <code>true</code> if plugin supports the platform.
     */
    public abstract boolean isSupported(String platform);

    /**
     * Initializes the plugin.
     *
     * @param options The compiler options.
     * @param log The compiler log object.
     */
    public abstract void initialize(Options options, Log log); 

    /**
     * Performs platform specific abstract syntax tree processing.
     *
     * @param trees The abstract syntax tree list.
     */
    public abstract void process(ListBuffer<JCCompilationUnit> trees);
}
