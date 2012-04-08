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

package org.f3.runtime;

import java.net.URL;
import java.net.MalformedURLException;

/**
 * PseudoVariables
 *
 * @author Brian Goetz
 */
public class PseudoVariables {
    /**
     * Returns the __FILE__ pseudo-variable for a module.
     *
     * @param moduleClass the fully-qualified name of the module class
     * @return the resource URL to the module's class as a String
     */
    public static String get__FILE__(Class<?> moduleClass) {
        try {
            String resource = moduleClass.getName().replace(".", "/") + ".class";
            return moduleClass.getClassLoader().getResource(resource).toString();
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Returns the __DIR__ pseudo-variable for a module.
     * @param __FILE__ the module's __FILE__ pseudo-variable as a String
     * @return the module's __DIR__ URL as a String
     */
    public static String get__DIR__(String __FILE__) {
        try {
            return __FILE__ == null ? null : new URL(new URL(__FILE__), ".").toString();
        } catch (MalformedURLException ex) {
            return null;
        }
    }

   /**
     * Returns the __PROFILE__ pseudo-variable for a module.
     * @return the module's __PROFILE__ pseudo-variable as a String
     */
    public static String get__PROFILE__() {
	String ret;	
	if (SystemProperties.getProperty("f3.runtime.isApplet") != null)
	    ret = "browser";
	else if (SystemProperties.getProperty("f3.me.profiles") != null)
	    ret = "mobile";
	else 
	    ret = "desktop";
	return ret;
    }

}
