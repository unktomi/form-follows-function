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

import java.util.Hashtable;
import java.io.InputStream;

public class  SystemProperties {
   /**
    * F3 System Properties table.
    * First column represents f3 property name with "f3" prefix stripped off.
    * Second column represents underlying runtime platform equivalent. 
    * "f3_specific" value in the runtime platform equivalent field indicates the property is F3 specific.
    * Empty string in   the runtime platform equivalent field indicates thete is no equivalent property for given platform.
    */
    private static String[] sysprop_table = {
        /*"f3.*/"application.codebase", "f3_specific",
    };


    /**
     * F3 Specific System Properties table.
     * First column represents f3 environment specific property name with "f3" prefix stripped off.
     * Second column represents value of the property 
    */
    private static String[] f3prop_table = {
        /*"f3.*/"application.codebase", "",
    };

    private static Hashtable sysprop_list = new Hashtable();  
    private static Hashtable f3prop_list = new Hashtable();

    private static final String versionResourceName =
            "/org/f3/runtime/resources/version.properties";
    

    static {
        addProperties (sysprop_table, false);
        addProperties (f3prop_table, true);
        setVersions();
    }

    /*
     * Populate our well known version strings
     */
    private static void setVersions() {
        int size;
        InputStream is =
                SystemProperties.class.getResourceAsStream(versionResourceName);
        try  {
            size = is.available();
        
            byte[] b = new byte[size];
            int n = is.read(b);            
            String inStr = new String(b, "utf-8");
            SystemProperties.setF3Property("f3.version",
                    getValue(inStr, "release="));

            SystemProperties.setF3Property("f3.runtime.version",
                    getValue(inStr, "full="));

        } catch (Exception ignore) {
        }
    }
    /*
     * Returns a value given a name
     */
    private static String getValue(String toSearch, String name) {
        String s = toSearch;
        int index;
        while ((index = s.indexOf(name)) != -1) {
            s = s.substring(index);
            if ((index = s.indexOf(0x0A))!= -1) {
                return (s.substring(name.length(), index)).trim();
            }
            return (s.substring(name.length(), s.length())).trim();
        }
        return "unknown";
    }
    /**
     * Registers a statically allocated System Properties table 
     * Once registered properties listed in the table are availabe for inquiry through F3.getProperty().
     * Table is defined as a String array with F3 property name followed by property value or property mapping identifier
     * depending on whether the table contains F3 specific properties or not.
     * Note that F3 property names have "f3" stripped out to optimize table lookup.
     * The following identifiers are available:
     * </p>
     * 1. Underlying runtime platform property name. When listed, F3.getProperty() will invoke System.getProperty()
     *    method to retrieve property value.
     *    example:
     *    {"version", "java.version"}
     * </p>   
     * 2. "f3_specific". When listed indicates there is no association between the property and underlying runtime
     *    platform. Rather the property is F3 specific. In that case another table needs to be provided with values
     *    for all F3 specific properties. F3 specific properties table is a string array containing property name
     *    and corresponding property value.
     *    example:
     *    {"hw.radio", "none"} 
     * </p>     
     * 3. Empty string. When listed, the meaning there is no association between the property and underlying runtime 
     *    platform nor the property is F3 specific. F3.getProperty() invoked on that property returns null.
     *    example:
     *    {"supports.mixing", "none"} 
     * @param table System Properties table
     * @param f3_specific Indicates the table contains F3 specific properties
     */      
    public static void addProperties (String[] table, boolean f3_specific) {
        if (table == null)
            return;

        Hashtable props;
                            
        if (f3_specific) {
            props = f3prop_list;
        } else {
            props = sysprop_list;
        }
                                  
        for (int i=0; i<table.length; i+=2) {
            props.put(table[i], table[i+1]);
        }
    } 

    public static String getProperty (String key) {
        Hashtable props = sysprop_list;
        final String prefix = "f3.";

        if (key == null)
                return null;

        if (key.startsWith(prefix.toString())) {
            key = key.substring(prefix.length());
        } else {
            return null;
        }
        final String found = (String)props.get(key);
        if ((found == null) || (found.equals(""))) {
        // No Java Runtime Environment property equivalent is found
            return null;
        }                        

                
        // Now check if the property is F3 specific and has no association with Runtime Environment
        if (found.equals("f3_specific")) {
            props = f3prop_list;
            return (String)props.get(key);
        } else {
            return System.getProperty(found);
        }
    }

   /*
    * Removes the property from F3 System Properties list 
    * @param key F3 System Property name
    */
    public static void clearProperty (String key) {
        if (key == null)
                return;

        Hashtable props = sysprop_list;
        final String prefix = "f3.";
        
        // Remove "f3." prefix from the key
        if (key.startsWith(prefix.toString())) {
            key = key.substring(prefix.length());
        } else {
            return;
        }

        String value = (String)props.get(key);
        if (value == null)
            return;

        props.remove(key);

        // Remove the prop from the F3 specific properties table if applicable
        if (value.equals("f3_specific")) {
           props = f3prop_list;                
            props.remove(key);
        }
    }

    /**
     * Adds a new F3 specific property or modifyies existing property value.
     * Note that there is no method in this class to set underlying platform 
     * property as MIDP doesn't support System.setProperty() method.
     * @param key F3 Property name
     * @param value Property value
     * @throws NullPointerException if key or value is null
     */
    public static void setF3Property (String key, final String value) {
        
        Hashtable props = sysprop_list;
        final String prefix = "f3."; 
        
        // Remove "f3." prefix from the key
        if (key.startsWith(prefix)) {
            key = key.substring(prefix.length());
       
           String k = (String)props.get(key);
	   // Add new property to the list
           if (k == null) {
               props.put(key, "f3_specific");
               props = f3prop_list;
               props.put(key, value);
	   } else if (k.equals("f3_specific")) {
               // Change existing property value
               props = f3prop_list;
               props.put(key, value);
               if (codebase.equals(prefix+key))
		   codebase_value = value;
	   }
	} 
    }

    public static String getCodebase() {
	return codebase_value;
    }

    public static void setCodebase(String value) {
	 if (value == null)
		value = "";
 	 codebase_value = value;
	 setF3Property("f3.application.codebase", value);
    }

    private static String codebase_value;

    public static final String codebase = "f3.application.codebase";
}

