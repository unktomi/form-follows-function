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

package org.f3.runtime.util;

import java.util.Collections;
import java.util.Map;
import java.util.Locale;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import org.f3.runtime.util.backport.ResourceBundle;

public class StringLocalization {

    private static final Map<ThreadGroup, Map<String, String>> map = 
        Collections.synchronizedMap(
            new WeakHashMap<ThreadGroup, Map<String, String>>());

    public static String getLocalizedString(String scriptName, String explicitKey,
                                            String literal, Object... embeddedExpr) {
        String key = scriptName.replaceAll("/", "\\.");
        int lastDot = key.lastIndexOf('.');
        if (lastDot != -1) {
            key = key.substring(0, lastDot) + "/" + key.substring(lastDot + 1);
        } else {
            key = "/" + key;
        }
        return getLocalizedString(
            getPropertiesName(key), explicitKey, literal, Locale.getDefault(), embeddedExpr);
    }

    public static String getLocalizedString(String propertiesName, String explicitKey, 
                        String literal, Locale locale, Object... embeddedExpr) {
        String localization = literal;
        ClassLoader cl = getCallerLoader();
        if (cl == null) {
            return literal;
        }

        ResourceBundle rb = ResourceBundle.getBundle(propertiesName,
                locale, cl, F3PropertyResourceBundle.F3PropertiesControl.INSTANCE);
        if (explicitKey != null) {
            localization = rb.getString(explicitKey);
            if (explicitKey.equals(localization) && 
                !rb.keySet().contains(explicitKey)) {
                localization = literal;
            }
        } else {
            localization = rb.getString(literal.replaceAll("\r\n|\r|\n", "\n"));
        }

        if (embeddedExpr.length != 0) {
            localization = F3Formatter.sprintf(locale, localization, embeddedExpr);
        }

        return localization;
    }

    public static void associate(String source, String properties) {
        getAssociation().put(source, properties);
    }

    public static void dissociate(String source) {
        Map<String, String> assoc = getAssociation();

        // remove itself first
        assoc.remove(source);

        // remove all associationis for source files in that package
        if (source.indexOf('/') == -1) {
            String toRemove = source + "/";
            for (String key : assoc.keySet()) {
                if (key.startsWith(toRemove)) {
                    assoc.remove(key);
                }
            }
        }
    }

    /**
     * Get the properties file name for the given key, which consists of
     * 'packageName(/scriptFileName)'. E.g., 'Example.f3' in 'foo.bar' package would have
     * a key as 'foo.bar/Example', while 'foo.bar' can represent the package itself.
     * A script file in the unnamed package can be denoted as '/Example'.
     */
    public static String getPropertiesName(String key) {
        String propertiesName = key.replaceAll("^/", "").replaceAll("/", ".");
        Map<String, String> assoc = getAssociation();
        Pattern chopoff = Pattern.compile("[\\./][^\\./]*\\z");
        
        while (true) {
            if (assoc.containsKey(key)) {
                propertiesName = assoc.get(key);
                break;
            } else {
                if ("".equals(key)) {
                    break;
                } else if (chopoff.matcher(key).find()) {
                    key = chopoff.matcher(key).replaceAll("");
                } else {
                    key = "";
                }
            }
        }

        return propertiesName;
    }

    private static Map<String, String> getAssociation() {
        ThreadGroup tg = Thread.currentThread().getThreadGroup();
        Map<String, String> assoc = map.get(tg);

        if (assoc == null) {
            assoc = new ConcurrentHashMap<String, String>();
            map.put(tg, assoc);
        }

        return assoc;
    }

    private static ClassContext classContext = null;
    private static final String PKGNAME = StringLocalization.class.getPackage().getName();
    static {
        try {
            classContext = (ClassContext)AccessController.doPrivileged(
                new PrivilegedExceptionAction() {
                    public Object run() {
                        return new ClassContext();
                    }
                });
        } catch (PrivilegedActionException pae) {
            // classContext should remain null.
        }
    }

    private static ClassLoader getCallerLoader() {
        if (classContext != null) {
            Class[] callers = classContext.getClassContext();
            for (Class c : callers) {
                if (!c.getName().startsWith(PKGNAME)) {
                    ClassLoader cl = c.getClassLoader();
                    if (cl == null) {
                        // bootstrap class loader.  use the system class loader instead
                        return ClassLoader.getSystemClassLoader();
                    } else {
                        return cl;
                    }
                }
            }
        }
        return null;
    }
}
