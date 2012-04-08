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

package f3.lang;

import java.util.Vector;
import org.f3.runtime.Entry;
import org.f3.functions.Function0;
import org.f3.runtime.SystemProperties;
import org.f3.runtime.F3Exit;
import org.f3.runtime.F3Object;

import org.f3.runtime.sequence.Sequence;

/**
 * F3, analogous to java.lang.System, is a place to store static utility methods.  
 *
 * @author Brian Goetz
 * @author Saul Wold
 * 
 * @profile common
 */
public class F3 {

    /**
     * Compare two F3 Objects
     * 
     * @param a the first object to be compared
     * @param b the second object to compare
     * @return true if they are the same object
     */
    public static boolean isSameObject(Object a, Object b) {
        return Builtins.isSameObject(a, b);
    }

    /**
     * Print the Object 'val'.
     * 
     * @param val The Object to be printed
     */
    public static void print(Object val) {
        Builtins.print(val);
    }

    /**
     * Print the Object 'val' and a new-line.
     * 
     * @param val The Object to be printed
     */
    public static void println(Object val) {
        Builtins.println(val);
    }

    /**
     * Test if an instance variable has been initialized.
     *
     * @param instance instance to be tested
     * @param offset offset of variable to be tested
     * @return true if the variable has been initialized
     */
    @org.f3.runtime.annotation.F3Signature("(Ljava/lang/Object;)Z")
    public static boolean isInitialized(F3Object instance, int varOffset) {
        return Builtins.isInitialized(instance, varOffset);
    }

    /**
     * Gets the system property indicated by the specified key.
     * <p></p>
     * System Properties in F3 environment can be classified into 2 types:
     * <p>
     * 1. Runtime platform associated property. 
     *    Those properties have an equivalent in current java runtime 
     *    environment (SE/ME). The F3.getProperty() method retrieves
     *    those properties by mapping specified key with runtime platform key.<br>
     *    If there is a security manager, property access permission is checked. This may result in a SecurityException.  
     * </p><p>
     * 2. F3 specific property. 
     *    Those properties are specific to F3 environment therefore
     *    value of the properties is specified in the F3 tree.
     * </p>
     * <br>
     * This set of system properties always includes values for the following keys: <p>
     * 
     * <table summary="Shows property keys and associated values">
     * <tr><th>Key</th>
     *     <th>Description of Associated Value</th>
     * <tr><td><code>f3.version</code></td>
     *     <td><code>F3  release version - f3 specific property</code></td></tr>
     * <tr><td><code>f3.application.codebase</code></td>
     *     <td><code>Application codebase - f3 specific property</code></td></tr>  
     * <tr><td><code>f3.java.version</code></td>
     *     <td><code>Java Runtime Environment version</code></td></tr>
     * <tr><td><code>f3.java.vendor</code></td>
     *     <td><code>Java Runtime Environment vendor</code></td></tr
     * <tr><td><code>f3.java.vendor.url</code></td>
     *     <td><code>Java vendor URL</code></td></tr>
     * <tr><td><code>f3.java.io.tmpdir</code></td>
     *     <td><code>Default temp file path</code></td></tr>
     * <tr><td><code>f3.java.ext.dirs</code></td>
     *     <td><code>Path of extension directory or directories</code></td></tr>
     * <tr><td><code>f3.os.name</code></td>
     *     <td><code>Operating system name</code></td></tr>
     * <tr><td><code>f3.os.arch</code></td>
     *     <td><code>Operating system architecture</code></td></tr>
     * <tr><td><code>f3.os.version</code></td>
     *     <td><code>Operating system version</code></td></tr>
     * <tr><td><code>f3.file.separator</code></td>
     *     <td><code>File separator</code></td></tr>
     * <tr><td><code>f3.path.separator</code></td>
     *     <td><code>Path separator</code></td></tr>
     * <tr><td><code>f3.line.separator</code></td>
     *     <td><code>Line separator</code></td></tr>
     * <tr><td><code>f3.user.home</code></td>
     *     <td><code>User's home directory</code></td></tr>
     * <tr><td><code>f3.user.dir</code></td>
     *     <td><code>User's current working directory</code></td></tr>
     * <tr><td><code>f3.timezone</code></td>
     *     <td><code>User's timezone</code></td></tr>
     * <tr><td><code>f3.language</code></td>
     *     <td><code>User's language</code></td></tr>
     * <tr><td><code>f3.region</code></td>
     *     <td><code>User's region</code></td></tr>    
     * <tr><td><code>f3.variant</code></td>
     *     <td><code>User's variant</code></td></tr>    
     * <tr><td><code>f3.encoding</code></td>
     *     <td><code>User's encoding</code></td></tr>     
     * </table>
     * <p>
     *
     * @param key Environment Property to be inquired
     * @return The string value of the property
     * @throws SecurityException if a security manager exists and its checkPropertyAccess method doesn't allow access to the specified system property. 
     * @throws NullPointerException if key is null. 
     * @profile common
     */
    public static String getProperty (String key) {
        return SystemProperties.getProperty(key);
    }
 
    /*
     * This static will be unique to each applet, when we move
     * to the F3ME/Embedded this will need to be tied the AMS
     * TODO: for Mobile Guys
     */
    private static F3SystemActionData exitData = new F3SystemActionData();

    /**
     * Exits the Script and causes any Shutdown Actions to be called
     * This may cause the Runtime to exit as {@code System.exit()} 
     * depending on the underlying implementation.
     * </p><p> 
     * Any Shutdown Actions that were previously added using the 
     * {@code addShutdownAction()} function will be exectued at this time
     * in LIFO ordering.
     * </p><p>
     * A second call to {@code F3.exit()} once {@code F3.exit()} has 
     * started will result a {@code IllegalStateException} to be thrown,
     * this can occur if a {@code Timeline} calls {@code F3.exit()} while
     * F3.exit is started.
     * If a call to {@code F3.exit()} occurs in a Shutdown Action, that
     * action's function will simply exit without completing the rest of
     * its operation and the next Shutdown Action, if any, will run.
     * </p><p>
     * This function will not normally return to the calling Script.
     * </p>
     *
     * @throws IllegalStateException when called during the process of exiting.
     *
     * @profile common
     */
    public static void exit() {
        if (exitData.called) {
            throw new IllegalStateException("Can not call F3.exit() twice");
        } else {
            exitData.called = true;
        }
        /*
         * Run the exit actions
         */
        exitData.runActions();

        /*
         * Call to Entry.java in order to get the RuntimeProvider
         * to do additional cleanup as needed for that provider
         */
        Entry.exit();

        /*
         * Mark this as false for handling restarting from the
         * same VM or Browser Context
         */
        exitData.called = false;
        
        /*
         * Use of F3Exit here is needed because the EDT
         * will pass it along rather than catch and quit
         */
        throw new F3Exit();
    }

    /**
     * Adds an action to the queue to be executed at {@code F3.exit()} time
     * This action will be added to the queue as a push stack, meaning that
     * they will be excuted in FILO ordering. Duplicate actions are
     * not allowed and will cause the orignal Handle to be returned with
     * no reordering.
     * 
     * @param  action of type {@code function():Void} that will be executed
     * at {@code F3.exit()} time. Only one copy of an action can be in 
     * the queue, an attempt to add the same action a second time will
     * return the previous Handle without any reodering.
     * @return Handle used to remove the action if needed.
     *
     * @throws NullPointerException if the action if null
     * @profile common
     */
    public static int addShutdownAction(Function0<Void> action) {
        if (action == null) {
            throw new NullPointerException("Action function can not be null");
        } else {
            return exitData.addAction(action);
        }
    }

    /**
     * Removes the action from the queue specified by the actionType parameter. 
     *
     * @param  action of type {@code function():Void} that will be removed
     * from the Shutdown Action Stack
     * @return a Boolean value signifing sucess or failure of removing
     * the action
     *
     * @profile common
     */
    public static boolean removeShutdownAction(int handle) {
        return exitData.removeAction(handle);
    }

    /**
     * A {@code deferAction} represents an action that should be executed at a
     * later time of the system's choosing.
     * <p />
     * In systems based on event dispatch, such as Swing, execution of a
     * {@code deferAction} generally means putting it on the event queue
     * for later processing.
     *
     * @param  action of type {@code function():Void} that will be executed
     * later based on the implementation.
     * 
     * @throws NullPointerException if the action if null
     * @profile common
     */
    public static void deferAction(Function0<Void> action) {
        if (action == null) {
            throw new NullPointerException("Action function can not be null");
        } else {
            Entry.deferAction(action);
        }
    }

    /**
     * For F3 applications that are started on the command
     * line,running application. This will return Unnamed Arguments
     *
     * @return Sequence of commandline args as strings, this will return
     * null under the following conditions:
     * <ul>
     * <li>No Incoming arguments on Command line</li>
     * <li>Only Name, Value pairs on the Command line</li>
     * </ul>
     * @profile common
     */
    public static Sequence<? extends String> getArguments() {
        return Entry.getArguments();
    }

    /**
     * Returns the named incoming argument for the current F3
     * Script program; this is used for certain environments (in
     * particular, applets) where incoming arguments are represented
     * as name/value pairs. This usually returns a String, but some
     * environments may return other kinds of values. Accepts numbers
     * in the form of Strings (e.g. {@code getArgument("0")}) to
     * provide unification with {@link #getArguments getArguments}.
     * Returns null if the given named argument does not exist.
     * </p><p> 
     * getArgument("f3.applet") will return the underlying applet that is
     * used to run the F3 application inside the browser. This is 
     * an experimental facility, that may be changed in future versions.
     * </p><p> 
     * This can be used as follows:
     * </p><p> 
     * var applet = F3.getArgument("f3.applet") as java.applet.Applet;
     * </p><p> 
     * Once the applet is obtained, there are 4 suggested ways to use it
     * <ol><li>
     * to invoke AppletContext's showDocument() method(s)
     * </li><li>
     * to invoke AppletContext's showStatus() method
     * </li><li>
     * to retrieve the JSObject to interact with JavaScript in the page
     * </li><li>
     * to retrieve the DOM object using bootstrapping mechanism in the new plugin
     * </li></ol>
     * </p><p> 
     * getArgument("f3.applet") will return null if not running as an applet
     * </p><p> 
     * @return a string representing the value for named or numeric argument,
     * or null if given name does not exist.
     *
     * @profile common
     */
    public static Object getArgument(String name) {
        return Entry.getArgument(name);
    }

    /*
     * This inner help class is used to store the Action Data needed 
     * for exitActions, in the future there may me addition System 
     * Actions that will be required such as Low Resources or ...
     */
    private static class F3SystemActionData {
        boolean called;
        Vector actions, handles;

        private static F3SystemActionData singleton = null;

        private F3SystemActionData() {
            called = false;
            actions = new Vector();
            handles = new Vector();
        }

        int addAction(Object action) {
            int hash = action.hashCode();

            // Check for action already in Vector
            int index = handles.indexOf(hash);
            if (index != -1) {
                // Return the hash without reorder
                return hash;
            }
            actions.addElement(action);
            handles.addElement(hash);
            return hash;
        }

        boolean removeAction(int handle) {
            if (handles.isEmpty() || !handles.contains(handle)) {
                return false;
            } else {
                try {
                    int index = handles.indexOf(handle);
                    if (index != -1) {
                        actions.removeElementAt(index);
                        handles.removeElementAt(index);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    return false;
                }
                return true;
            }
        }

        void runActions() {
            if (actions == null) {
                return;
            }
            while (!actions.isEmpty()) {
                Function0<Void> action = null;
                // Execute LIFO Action
                action = (Function0<Void>) actions.lastElement();
                actions.remove(actions.lastIndexOf(action));
                if (action != null) {
                    try {
                        /*
                         * TODO: add timer to kill long running Action
                         */
                        action.invoke$(null, null, null);
                    } catch (Throwable ignore) {
                        // Ignore all Throwables
                    }
                }
            }
            handles.removeAllElements();
        }
    }
}
