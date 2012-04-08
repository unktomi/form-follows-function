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

/**
 * This package provides F3 Runtime APIs
 * <p>
 * <h2>Builtins</h2>
 * This class is automatically imported to all F3 Files
 * </p>
 * <h2>F3</h2>
 * The F3 class contains number of static entry points
 * for a couple of different API sets provided by F3.
 * <ul>
 * <li> Application Model
 * <p>F3 APIs are categorized into following profiles.   </p>
 * <ul>
 * <li>Common Profile
 * <p>This API is common across all platforms</p>
 * <li>Desktop Profile
 * <p>This API is available only in the desktop environment
 * (browser & standalone)</p>
 * <li>Mobile Profile
 * <p>This API is available only in the mobile environment.</p>
 * </ul>
 * <p>If an application needs to be portable across all screens,
 * it has to limit itself to common profile APIs. The F3
 * Runtime has the ability to run F3 Files
 * that use the common or desktop API sets in any supported Browser.
 * </p>
 * <li> Argument or Parameter Handling
 * <p>F3 Files can get Arguments or Parameters in 2 different 
 * forms
 * <ul><li>Named
 * <p>Named Parameters come in the form of a Name, Value pair typically
 * from HTML, JAD or JNLP files. They can also be passed as a commandline
 * argument in the form of "name=value".</p>
 * <p>Use the {@link f3.lang.F3#getArgument(java.lang.String) getArgument()}
 * api for Named parameters</p>
 * <li>Unnamed
 * <p>Unnamed Arguments are always passed on the commandline.
 * <p>Use the {@link f3.lang.F3#getArguments() F3.getArguments()}
 * api for Named parameters</p>
 * </ul>
 * These <b>cannot</b> be combined, if they are it will behave as space
 * seperated arguments.</p>
 * <li> SystemProperty information
 * </ul>
 */

package f3.lang;
