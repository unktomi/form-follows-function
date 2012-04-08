/*
 * Copyright 1999-2009 Sun Microsystems, Inc.  All Rights Reserved.
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

import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;

import java.util.*;
import java.lang.reflect.*;
import java.io.FileInputStream;

public class MsgSymTest {

    @Test
    public void checkSymbolToMessageEN() {
        // get English resource-bundles
        LinkedList<ResourceBundle> resources = new LinkedList<ResourceBundle>();
        resources.add(ResourceBundle.getBundle("com.sun.tools.mjavac.resources.javac"));
        resources.add(ResourceBundle.getBundle("com.sun.tools.mjavac.resources.compiler"));
        resources.add(ResourceBundle.getBundle("org.f3.tools.resources.f3compiler"));
        
        // general test
        checkSymbolToMessage(resources);
    }
    
    public static void checkSymbolToMessage(List<ResourceBundle> resources) {
        // create map from bundles without prefixes
        Map<String, String> map = new HashMap<String, String>();
        String key = "";
        for (ResourceBundle bundle : resources) {
            for (Enumeration<String> it = bundle.getKeys(); it.hasMoreElements(); ) {
                key = it.nextElement();
                if (key.startsWith(MsgSym.MESSAGEPREFIX_COMPILER_ERR))
                    map.put(key.substring(MsgSym.MESSAGEPREFIX_COMPILER_ERR.length()), bundle.getString(key));
                else
                if (key.startsWith(MsgSym.MESSAGEPREFIX_COMPILER_WARN))
                    map.put(key.substring(MsgSym.MESSAGEPREFIX_COMPILER_WARN.length()), bundle.getString(key));
                if (key.startsWith(MsgSym.MESSAGEPREFIX_COMPILER_NOTE))
                    map.put(key.substring(MsgSym.MESSAGEPREFIX_COMPILER_NOTE.length()), bundle.getString(key));
                else
                if (key.startsWith(MsgSym.MESSAGEPREFIX_JAVAC))
                    map.put(key.substring(MsgSym.MESSAGEPREFIX_JAVAC.length()), bundle.getString(key));
                else
                if (key.startsWith(MsgSym.MESSAGEPREFIX_COMPILER_MISC + MsgSym.MESSAGEPREFIX_VERBOSE))
                    map.put(key.substring(MsgSym.MESSAGEPREFIX_COMPILER_MISC.length() + MsgSym.MESSAGEPREFIX_VERBOSE.length()), bundle.getString(key));
                else
                if (key.startsWith(MsgSym.MESSAGEPREFIX_COMPILER_MISC))
                    map.put(key.substring(MsgSym.MESSAGEPREFIX_COMPILER_MISC.length()), bundle.getString(key));
                else
                    map.put(key, bundle.getString(key));
            }
        }
        
        // loop through all message-symbols
        Field[] fields = MsgSym.class.getFields();
        for (Field cur : fields) {
            if (cur.getName().startsWith("MESSAGE_")
                    && Modifier.isPublic(cur.getModifiers())
                    && Modifier.isStatic(cur.getModifiers())
                    && Modifier.isFinal(cur.getModifiers())) {
                try {
                    if (!map.containsKey(cur.get(null))) {
                        Assert.fail("No error-message for symbol " + cur.getName() + " defined.");
                    }
                }
                catch (Exception ex) {
                    Assert.fail("Exception evaluating symbol " + cur.getName() + ".");
                }
            }
        }
    }
    
    @Test
    public void checkMessageToSymbolEN() {
        // read English message-list
        Properties messages = new Properties();
        try {
            messages.load(new FileInputStream("src/share/classes/org/f3/tools/resources/f3compiler.properties"));

            // general test
            checkMessageToSymbol(messages);
        } catch (IOException ex) {
            Assert.fail("Unable to read message-file");
        }
        
    }
    
    public static void checkMessageToSymbol(Properties messages) {
        // create a set of all keys from symbol-table
        Set keys = new HashSet();
        Field[] fields = MsgSym.class.getFields();
        for (Field cur : fields) {
            if (cur.getName().startsWith("MESSAGE_")
                    && Modifier.isPublic(cur.getModifiers())
                    && Modifier.isStatic(cur.getModifiers())
                    && Modifier.isFinal(cur.getModifiers())) {
                try {
                    keys.add(cur.get(null));
                }
                catch (Exception ex) {
                    Assert.fail("Exception evaluating symbol " + cur.getName() + ".");
                }
            }
        }
        
        // check if a symbol exists for every message 
        String cur = "";
        boolean found;
        for (Enumeration it = messages.propertyNames(); it.hasMoreElements(); ) {
            cur = (String)it.nextElement();
            if (cur.startsWith(MsgSym.MESSAGEPREFIX_COMPILER_ERR))
                found = keys.contains(cur.substring(MsgSym.MESSAGEPREFIX_COMPILER_ERR.length()));
            else
            if (cur.startsWith(MsgSym.MESSAGEPREFIX_COMPILER_WARN))
                found = keys.contains(cur.substring(MsgSym.MESSAGEPREFIX_COMPILER_WARN.length()));
            else
            if (cur.startsWith(MsgSym.MESSAGEPREFIX_COMPILER_NOTE))
                found = keys.contains(cur.substring(MsgSym.MESSAGEPREFIX_COMPILER_WARN.length()));
            else
            if (cur.startsWith(MsgSym.MESSAGEPREFIX_JAVAC))
                found = keys.contains(cur.substring(MsgSym.MESSAGEPREFIX_JAVAC.length()));
            else
            if (cur.startsWith(MsgSym.MESSAGEPREFIX_COMPILER_MISC + MsgSym.MESSAGEPREFIX_VERBOSE))
                found = keys.contains(cur.substring(MsgSym.MESSAGEPREFIX_COMPILER_MISC.length() + MsgSym.MESSAGEPREFIX_VERBOSE.length()));
            else
            if (cur.startsWith(MsgSym.MESSAGEPREFIX_COMPILER_MISC))
                found = keys.contains(cur.substring(MsgSym.MESSAGEPREFIX_COMPILER_MISC.length()));
            else
                found = keys.contains(cur);
            Assert.assertTrue("No message-symbol for key " + cur + " defined.", found);
        }
        
    }    

}
